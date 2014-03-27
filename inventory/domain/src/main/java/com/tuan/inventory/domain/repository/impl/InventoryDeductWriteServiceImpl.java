package com.tuan.inventory.domain.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryNumDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.RedisSelectionNumDO;
import com.tuan.inventory.dao.data.redis.RedisSuppliersNumDO;
import com.tuan.inventory.domain.repository.InventoryDeductWriteService;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.repository.NotifyServerSendMessage;
import com.tuan.inventory.domain.support.bean.UpdateInventoryBeanResult;
import com.tuan.inventory.domain.support.bean.job.NotifyMessage;
import com.tuan.inventory.domain.support.bean.message.WaterfloodValAdjustment;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.enu.InventoryVarQttEnum;
import com.tuan.inventory.domain.support.exception.RedisRunException;
import com.tuan.inventory.domain.support.jedistools.WriteJedisFactory;
import com.tuan.inventory.domain.support.jedistools.WriteJedisFactory.JWork;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.OrderGoodsSelectionModel;
import com.tuan.inventory.model.enu.QueueConstant;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.result.CallResult;

/**
 * ���ۼ����д���� ������桢���ۼ�[����]�������º�Ļص�ȷ�ϡ�����עˮ���޸�עˮֵ��ɾ�����
 * 
 * @author henry.yu
 * @date 2014/03/10
 */
public class InventoryDeductWriteServiceImpl  implements
		InventoryDeductWriteService {
	@Resource
	NullCacheInitService nullCacheInitService;
	@Resource
	WriteJedisFactory writeJedisFactory;
	@Resource
	NotifyServerSendMessage notifyServerSendMessage;
	@Resource 
	SequenceUtil sequenceUtil;
	@Resource
	InventoryQueueService inventoryQueueService;
	
	private final static LocalLogger log = LocalLogger.getLog("InventoryDeductWriteService.LOG");
	@Override
	public Boolean createInventory(final long goodsId,final int pindaoId,final int limitStorage,final Long userId, final String system, final String clientIp,
			final RedisInventoryDO riDO,
			final List<RedisGoodsSelectionRelationDO> rgsrList,
			final List<RedisGoodsSuppliersInventoryDO> rgsiList)
			throws Exception {
		//boolean resultReturn = jedisFactory.withJedisDo(new JWork<Boolean>() {
		return writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.createInventory(goodsId,RedisInventoryDO,"
						+ "List<RedisGoodsSelectionRelationDO>,List<RedisGoodsSuppliersInventoryDO>)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
						.addMetaData("pindaoId", String.valueOf(pindaoId))
						.addMetaData("limitStorage", String.valueOf(limitStorage))
						.addMetaData("userId", String.valueOf(userId))
						.addMetaData("system", system)
						.addMetaData("clientIp", clientIp)
						.addMetaData("RedisInventoryDO", riDO)
						.addMetaData("List<RedisGoodsSelectionRelationDO>", rgsrList)
						.addMetaData("List<RedisGoodsSuppliersInventoryDO>", rgsiList)
						.addMetaData("startTime", startTime).toJson());
			boolean result = false;
				if (j == null)
					return result;
				try {
					if (j.exists(InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(goodsId))) { // �Ѵ��ڷ���false
						return result;
					}
					// 1.������Ʒ���������Ϣ
					String redisAck = j.hmset(InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(goodsId),
							ObjectUtil.convertBean(riDO));
					// ��Ʒ�Ƿ�������� 0������ӣ�1�����
					if (riDO.getIsAddGoodsSelection() == 1) {
						if (!CollectionUtils.isEmpty(rgsrList)) {
							// jedis.sadd(String.valueOf(goodsId),
							// StringUtil.getSelectionRelationString(rgsrList));
							for (RedisGoodsSelectionRelationDO rgsrDO : rgsrList) {
								// 2.��Ʒid��ѡ�͹�ϵ
								j.sadd(InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId),
										String.valueOf(rgsrDO.getId()));
								// 3.����ѡ�Ϳ��������Ϣ
								redisAck = j.hmset(InventoryEnum.HASHCACHE
										+ ":" + String.valueOf(rgsrDO.getId()),
										ObjectUtil.convertBean(rgsrDO));
							}

						}
					}
					// ��Ʒ�����Ƿ���Ҫָ���ֵ� 0����ָ����1��ָ��
					if (riDO.getIsDirectConsumption() == 1) {
						if (!CollectionUtils.isEmpty(rgsiList)) {
							// jedis.sadd(String.valueOf(goodsId),
							// StringUtil.getSuppliersInventoryString(rgsiList));
							for (RedisGoodsSuppliersInventoryDO rgsiDO : rgsiList) {
								// 2.��Ʒ���̼ҷֵ��ϵ
								j.sadd(InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId),
										String.valueOf(rgsiDO.getId()));
								// 3.������Ʒ�ֵ���������Ϣ
								redisAck = j.hmset(InventoryEnum.HASHCACHE
										+ ":" + String.valueOf(rgsiDO.getId()),
										ObjectUtil.convertBean(rgsiDO));
							}
						}
					}
					// ִ������
					// List<Object> redisAckList =
					// ts.exec();
					if (redisAck.equalsIgnoreCase("ok")) {
						result = true;
					}
					
				} catch (Exception e) {
					log.error(lm.addMetaData("goodsId", String.valueOf(goodsId))
							.addMetaData("pindaoId", String.valueOf(pindaoId))
							.addMetaData("limitStorage", String.valueOf(limitStorage))
							.addMetaData("userId", String.valueOf(userId))
							.addMetaData("system", system)
							.addMetaData("clientIp", clientIp)
							.addMetaData("RedisInventoryDO", riDO)
							.addMetaData("List<RedisGoodsSelectionRelationDO>", rgsrList)
							.addMetaData("List<RedisGoodsSuppliersInventoryDO>", rgsiList)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:createInventory run exception!",e);
				}
				log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
						.addMetaData("pindaoId", String.valueOf(pindaoId))
						.addMetaData("limitStorage", String.valueOf(limitStorage))
						.addMetaData("userId", String.valueOf(userId))
						.addMetaData("system", system)
						.addMetaData("clientIp", clientIp)
						.addMetaData("RedisInventoryDO", riDO)
						.addMetaData("List<RedisGoodsSelectionRelationDO>", rgsrList)
						.addMetaData("List<RedisGoodsSuppliersInventoryDO>", rgsiList)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}

		@Override
		public void workAfter(Jedis p) throws Exception {
			// ������������־����
			RedisInventoryLogDO logDO = asemblyLogDO(
					sequenceUtil.getSequence(SEQNAME.seq_log),
					goodsId, 0L, "", "",
					 "",
					ResultStatusEnum.INVENTORYINIT.getDescription(),
					userId, system, clientIp, "",
					asemblyInitJsonData(riDO,rgsrList,rgsiList, pindaoId));
			//ѹ����־����
			inventoryQueueService.pushLogQueues(logDO);
			//���Ϳ��������Ϣ[��������]�������߶��з�������Ϣ��
			notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(asemblyNotifyMessage(userId, 0L, goodsId, limitStorage, riDO.getWaterfloodVal(), asemblyInitJsonData(riDO,rgsrList,rgsiList, pindaoId))));
		}

	  });
	
	}

	@Override
	public boolean insertSingleInventoryInfoNotExist(final long goodsId,
			final String field, final String value,final Long userId, final String system, final String clientIp) throws Exception {

		return writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.insertSingleInventoryInfoNotExist(goodsId,field,value)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
						.addMetaData("field", field)
						.addMetaData("value", value)
						.addMetaData("startTime", startTime).toJson());
				boolean result = false;
				if (j == null)
					return result;
				try {
					// ��������1 :hsetnx ���� field �Ѿ����ڣ��ò�����Ч��
					Long redisAck = j.hsetnx(InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(goodsId), field, value);
					if (redisAck == 1) {
						result = true;
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("goodsId", String.valueOf(goodsId))
							.addMetaData("field", field)
							.addMetaData("value", value)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:insertSingleInventoryInfoNotExist run exception!",e);
				}
				log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
						.addMetaData("field", field)
						.addMetaData("value", value)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				// TODO ���������־��������һ�����ֶ����ݡ�
				JSONObject json = new JSONObject();
				json.put("field", field);
				json.put("value", value);
				// ������������־����
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						goodsId, 0L, "", "",
						 "",
						ResultStatusEnum.MANUALADAPT.getDescription(),
						userId, system, clientIp, "",
						json.toString());
				//ѹ����־����
				inventoryQueueService.pushLogQueues(logDO);
			}
			
		});

	}

	@Override
	public boolean updateOverrideSingleInventoryInfo(final long goodsId,
			final String field, final String value,final Long userId, final String system, final String clientIp) throws Exception {

		return writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.updateOverrideSingleInventoryInfo(goodsId,field,value)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
						.addMetaData("field", field)
						.addMetaData("value", value)
						.addMetaData("startTime", startTime).toJson());
				boolean result = false;
				if (j == null)
					return result;

				try {
					Long redisAck = j.hset(InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(goodsId), field, value);
					if (redisAck == 1)
						result = true;
				} catch (Exception e) {
					log.error(lm.addMetaData("goodsId", String.valueOf(goodsId))
							.addMetaData("field", field)
							.addMetaData("value", value)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:updateOverrideSingleInventoryInfo run exception!",e);
				}
				log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
						.addMetaData("field", field)
						.addMetaData("value", value)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				// TODO ��¼���Ǹ��µ��������Ϣ����־
				// TODO ������ظ��µ���Ϣ
				
				JSONObject json = new JSONObject();
				json.put("field", field);
				json.put("value", value);
				// ������������־����
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						goodsId, 0L, "", "",
						 "",
						ResultStatusEnum.MANUALADAPT.getDescription(),
						userId, system, clientIp, "",
						json.toString());
				//ѹ����־����
				inventoryQueueService.pushLogQueues(logDO);
				//���Ϳ��������Ϣ[��������]�������߶��з�������Ϣ��
				notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(asemblyNotifyMessage(userId, 0L, goodsId, 0, 0, json.toString())));
			}
			
			
		});

	}

	@Override
	public boolean deleteInventory(final long goodsId,
			final List<OrderGoodsSelectionModel> goodsSelectionList,final Long userId, final String system, final String clientIp)
			throws Exception {
		//����ҵ�񷵻ض���
	    final CallResult<UpdateInventoryBeanResult> callResult = new CallResult<UpdateInventoryBeanResult>();
		return writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.deleteInventory(goodsId,List<OrderGoodsSelectionModel>)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("goodsId",String.valueOf(goodsId))
						.addMetaData("List<OrderGoodsSelectionModel>",goodsSelectionList)
						.addMetaData("startTime", startTime).toJson());
				boolean result = false;
				String selectType = null; // ����ѡ����Ʒ���������
				String suppliersType = null; // �����ֵ���Ʒ���������
				if (j == null)
					return result;
				// Transaction ts = null;
				long redisAck = 0;
				try {
					// ����ɾ������
					// jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
					if (j.exists(InventoryEnum.SETCACHE + ":"
							+ String.valueOf(goodsId))) {
						// ��������
						// ts = j.multi();
						if (!CollectionUtils.isEmpty(goodsSelectionList)) {// if2
							// ѡ����Ʒ���ֵ
							selectType = StringUtil
									.getIdsString(goodsSelectionList);
							// �ֵ���Ʒ���ֵ
							suppliersType = StringUtil
									.getIdsString(goodsSelectionList);
							for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {// if3
								if (orderGoodsSelectionModel
										.getSelectionRelationId() != null
										&& orderGoodsSelectionModel
												.getSelectionRelationId() > 0) { // ɾ����Ʒѡ�Ϳ��
									// ����ѡ��idɾ��ѡ�Ϳ����Ϣ
									j.del(InventoryEnum.HASHCACHE
											+ ":"
											+ orderGoodsSelectionModel
													.getSelectionRelationId());
								}
								if (orderGoodsSelectionModel.getSuppliersId() > 0) { // ɾ����Ʒ�ֵ�Ŀ��
									// ����ѡ��idɾ��ѡ�ͷֵ�����Ϣ
									j.del(InventoryEnum.HASHCACHE
											+ ":"
											+ orderGoodsSelectionModel
													.getSuppliersId());
								}
							}// if3
						}// if2
							// 3.���ɾ����Ʒ���������Ϣ����Ʒid��ѡ��id����Ʒ�ֵ�id֮��Ķ�Ӧ��ϵ
						redisAck = j.del(
								InventoryEnum.HASHCACHE + ":"
										+ String.valueOf(goodsId),
								InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId));
						// ִ������
						// ts.exec();
					}// else {//if1
						// ��֤��һ�������ִ�в���Ӱ��
						// jedis.unwatch();
					// }
					if (redisAck > 0) {
						// TODO ���Ϳ�������Ϣ

						result = true;
						
						UpdateInventoryBeanResult beanResult = new UpdateInventoryBeanResult();
						beanResult.setCallResult(result);
						//beanResult.setJsonData(jsonData.toString());
						beanResult.setSelectType(selectType);
						beanResult.setSuppliersType(suppliersType);
						callResult.setBusinessObject(beanResult);
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("goodsId",String.valueOf(goodsId))
							.addMetaData("List<OrderGoodsSelectionModel>",goodsSelectionList)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:deleteInventory run exception!",e);
				}
				log.info(lm.addMetaData("goodsId",String.valueOf(goodsId))
						.addMetaData("List<OrderGoodsSelectionModel>",goodsSelectionList)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				// TODO ��¼���ɾ����־
				// TODO ���Ϳ�������Ϣ
				UpdateInventoryBeanResult beanResult = callResult.getBusinessResult();
				JSONObject json = new JSONObject();
				json.put("goodsSelectionList", goodsSelectionList);
				// ������������־����
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						goodsId, 0L, beanResult.getSelectType(), beanResult.getSuppliersType(),
						 "",
						ResultStatusEnum.MANUALADAPT.getDescription(),
						userId, system, clientIp, "",
						json.toString());
				//ѹ����־����
				inventoryQueueService.pushLogQueues(logDO);
				//���Ϳ��������Ϣ[��������]�������߶��з�������Ϣ��
				notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(asemblyNotifyMessage(userId, 0L, goodsId, 0, 0, json.toString())));
			}
			
		});

	}

	@Override
	public Map<String, String> updateInventory(final long orderId,
			final long goodsId, final int pindaoId, final int num,
			final int limitStorage,
			final List<OrderGoodsSelectionModel> goodsSelectionList,
			final Long userId, final String system, final String clientIp)
			throws Exception {
		    //�����ӿڷ�������
		    final Map<String, String> mapResult = new HashMap<String, String>();
		    //����ҵ�񷵻ض���
		    final CallResult<UpdateInventoryBeanResult> callResult = new CallResult<UpdateInventoryBeanResult>();
		    //ҵ��ִ��
			writeJedisFactory.withJedisDo(new JWork<Boolean>() {
		   // return writeJedisFactory.withJedisDo(new JWork<Map<String, String>>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.updateInventory(orderId,goodsId,"
						+ "pindaoId,num,limitStorage,List<OrderGoodsSelectionModel>,userId,system,clientIp)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("orderId",String.valueOf(orderId))
						.addMetaData("goodsId",String.valueOf(goodsId))
						.addMetaData("pindaoId",String.valueOf(pindaoId))
						.addMetaData("num",String.valueOf(num))
						.addMetaData("startTime", startTime).toJson());
				
				String selectType = null; // ����ѡ����Ʒ���������
				String suppliersType = null; // �����ֵ���Ʒ���������
				// �������仯��json����
				JSONObject jsonData = new JSONObject();
				//����һ��������Ʒѡ�Ϳ��仯��list
				List<RedisSelectionNumDO> sLists= new ArrayList<RedisSelectionNumDO>();
				//����һ��������Ʒ�ֵ���仯��list
				List<RedisSuppliersNumDO> suppLists= new ArrayList<RedisSuppliersNumDO>();
				boolean result = false;
				if (j == null) {
					//mapResult = new HashMap<String, String>();
					mapResult.put("result", String.valueOf(result));
					//return mapResult;
					return result;
				}
				// ��������
				// Transaction ts = j.multi();
				long resultAck = 0;
				try {
					if (goodsId > 0 && limitStorage == 1) { // limitStorage>0:��������ƣ�1�����ƿ��
						if (!j.hexists(
								InventoryEnum.HASHCACHE + ":"
										+ String.valueOf(goodsId), /* "leftNumber" */
								HashFieldEnum.leftNumber.toString())) { // key field
																		// �򲻴������ʼ��
							result = nullCacheInitService
									.initRedisInventoryCache(j, goodsId,
											limitStorage, goodsSelectionList);
							if (!result) {
								//mapResult = new HashMap<String, String>();
								mapResult.put("result", String.valueOf(result));
								//return mapResult;
								return result;
							}
						}
						//jsonData.put("��Ʒ�ܿ��仯��", num);
						jsonData.put(InventoryVarQttEnum.num, num);
						resultAck = j.hincrBy(InventoryEnum.HASHCACHE + ":"
								+ String.valueOf(goodsId),
								HashFieldEnum.leftNumber.toString(), (-num));
						if (resultAck < 0) { // ˵�����ν��׵Ŀ�治��
							// ��ԭ�ۼ��Ŀ��
							resultAck = j.hincrBy(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(goodsId),
									HashFieldEnum.leftNumber.toString(), (num));
							//mapResult = new HashMap<String, String>();
							mapResult.put("result", String.valueOf(result));
							//return mapResult;
							return result;
						} else {
							//jsonData.put("��Ʒ�ܿ��ʣ����", resultAck);
							jsonData.put(InventoryVarQttEnum.leftNum, resultAck);
						}

					}
					if (!CollectionUtils.isEmpty(goodsSelectionList)) { // if1
						// ѡ����Ʒ���ֵ
						selectType = StringUtil
								.getIdsString(goodsSelectionList);
						// �ֵ���Ʒ���ֵ
						suppliersType = StringUtil
								.getIdsString(goodsSelectionList);
						
						for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) { // for
							if (orderGoodsSelectionModel
									.getSelectionRelationId() != null
									&& orderGoodsSelectionModel
											.getSelectionRelationId() > 0) { // ifѡ��
																				// //������Ʒѡ�Ϳ��
								if (!j.exists(InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId))) {
									result = nullCacheInitService
											.initRedisInventoryCache(j,
													goodsId, limitStorage,
													goodsSelectionList);
									if (!result) {
										// ��ԭ���ۼ���Ʒ���ܿ��
										resultAck = j
												.hincrBy(
														InventoryEnum.HASHCACHE
																+ ":"
																+ String.valueOf(goodsId),
														HashFieldEnum.leftNumber
																.toString(),
														(num));
										//mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										//return mapResult;
										return result;
									}
								}
								// 1.���Ȳ���������Ʒ�Ƿ�������͹�ϵ
								if (!j.hexists(
										InventoryEnum.HASHCACHE
												+ ":"
												+ orderGoodsSelectionModel
														.getSelectionRelationId(),
										HashFieldEnum.leftNumber.toString())) {
									result = nullCacheInitService
											.initRedisInventoryCache(j,
													goodsId, limitStorage,
													goodsSelectionList);
									if (!result) {
										// ��ԭ���ۼ���Ʒ���ܿ��
										resultAck = j
												.hincrBy(
														InventoryEnum.HASHCACHE
																+ ":"
																+ String.valueOf(goodsId),
														HashFieldEnum.leftNumber
																.toString(),
														(num));
										//mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										//return mapResult;
										return result;
									}
								}
								//jsonData.put(InventoryVarQttEnum.selectionNum,
									//	(orderGoodsSelectionModel.getCount()
										//		.intValue()));
								// ����ѡ��id����ѡ�Ϳ��������Ϣ
								resultAck = j
										.hincrBy(
												InventoryEnum.HASHCACHE
														+ ":"
														+ orderGoodsSelectionModel
																.getSelectionRelationId(),
												HashFieldEnum.leftNumber
														.toString(),
												(-(orderGoodsSelectionModel
														.getCount().intValue())));
								if (resultAck < 0) { // ˵�����ν��׵Ŀ�治��
									// ���Ȼ�ԭ��Ʒѡ�ͱ��ۼ��Ŀ��
									resultAck = j
											.hincrBy(
													InventoryEnum.HASHCACHE
															+ ":"
															+ orderGoodsSelectionModel
																	.getSelectionRelationId(),
													HashFieldEnum.leftNumber
															.toString(),
													(orderGoodsSelectionModel
															.getCount()
															.intValue()));
									// �ٻ�ԭ��Ʒ������Ϣ�б��ۼ��Ŀ��
									resultAck = j
											.hincrBy(
													InventoryEnum.HASHCACHE
															+ ":"
															+ String.valueOf(goodsId),
													HashFieldEnum.leftNumber
															.toString(), (num));
									//mapResult = new HashMap<String, String>();
									mapResult.put("result",
											String.valueOf(result));
									//return mapResult;
									return result;
								} else {
									//ѡ����Ʒ���ʣ����
									//jsonData.put(InventoryVarQttEnum.selectionLeftNum, (resultAck));
									//ѡ����Ʒ���仯��
									RedisSelectionNumDO sDO = new RedisSelectionNumDO();
									//ÿ�οۼ���ѡ�Ϳ����
									sDO.setSelectionNum(orderGoodsSelectionModel.getCount()
													.intValue());
									sDO.setSelectionRelationId(orderGoodsSelectionModel
																	.getSelectionRelationId());
									//ÿ�οۼ���ʣ���ѡ�Ϳ����
									sDO.setSelectionLeftNum(resultAck);
									
									sLists.add(sDO);
								}
								

							} // ifѡ��
							if (orderGoodsSelectionModel.getSuppliersId() > 0) { // if�ֵ�
								// �����Ʒ��ֵ�Ĺ�ϵ��
								if (!j.exists(InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId))) {
									// ��ʼ����Ʒ�ֵ�Ŀ��
									result = nullCacheInitService
											.initRedisInventoryCache(j,
													goodsId, limitStorage,
													goodsSelectionList);
									if (!result) {
										// ��ԭ���ۼ�����Ʒ�ܿ��
										resultAck = j
												.hincrBy(
														InventoryEnum.HASHCACHE
																+ ":"
																+ String.valueOf(goodsId),
														HashFieldEnum.leftNumber
																.toString(),
														(num));
										//mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										//return mapResult;
										return result;
									}
								}
								// 2.�����Ʒ�����Ƿ�ָ���ֵ�
								if (!j.hexists(
										InventoryEnum.HASHCACHE
												+ ":"
												+ orderGoodsSelectionModel
														.getSuppliersId(),
										HashFieldEnum.leftNumber.toString())) {
									result = nullCacheInitService
											.initRedisInventoryCache(j,
													goodsId, limitStorage,
													goodsSelectionList);
									if (!result) {
										// ��ԭ���ۼ�����Ʒ�ܿ��
										resultAck = j
												.hincrBy(
														InventoryEnum.HASHCACHE
																+ ":"
																+ String.valueOf(goodsId),
														HashFieldEnum.leftNumber
																.toString(),
														(num));
										//mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										//return mapResult;
										return result;
									}
								}
								//�ֵ���Ʒ���仯��
								//jsonData.put(InventoryVarQttEnum.supplierNum,
										//(orderGoodsSelectionModel.getCount()
											//	.intValue()));
								// ����ѡ��idɾ��ѡ�Ϳ��������Ϣ
								resultAck = j.hincrBy(
										InventoryEnum.HASHCACHE
												+ ":"
												+ orderGoodsSelectionModel
														.getSuppliersId(),
										HashFieldEnum.leftNumber.toString(),
										(-(orderGoodsSelectionModel.getCount()
												.intValue())));
								if (resultAck < 0) { // ˵�����ν��׵Ŀ�治��
									// ���Ȼ�ԭ��Ʒ�ֵ��б��ۼ��Ŀ��
									resultAck = j
											.hincrBy(
													InventoryEnum.HASHCACHE
															+ ":"
															+ orderGoodsSelectionModel
																	.getSuppliersId(),
													HashFieldEnum.leftNumber
															.toString(),
													(orderGoodsSelectionModel
															.getCount()
															.intValue()));
									// �ٻ�ԭ��Ʒ������Ϣ�б��ۼ��Ŀ��
									resultAck = j
											.hincrBy(
													InventoryEnum.HASHCACHE
															+ ":"
															+ String.valueOf(goodsId),
													HashFieldEnum.leftNumber
															.toString(), (num));
									//mapResult = new HashMap<String, String>();
									mapResult.put("result",
											String.valueOf(result));
									//return mapResult;
									return result;
								} else {
									//�ֵ���Ʒ���ʣ����
									//jsonData.put(InventoryVarQttEnum.supplierLeftNum, (resultAck));
									RedisSuppliersNumDO suppDO = new RedisSuppliersNumDO();
									//ÿ�οۼ��ķֵ�����
									suppDO.setSupplierNum(orderGoodsSelectionModel.getCount()
													.intValue());
									suppDO.setSuppliersId(orderGoodsSelectionModel
																	.getSuppliersId());
									//ÿ�οۼ���ʣ��ķֵ�����
									suppDO.setSupplierLeftNum(resultAck);
									
									suppLists.add(suppDO);
								}

							}// if�ֵ�
						}// for
						//��Ʒѡ�Ϳ��仯�������
						jsonData.put("sLists",sLists);
						//��Ʒ�ֵ���仯�������
						jsonData.put("suppLists", suppLists);
					} // if1
						// ִ������
						// ts.exec();
					if (resultAck >= 0) {// �����㲢�ۼ��ɹ�
						result = true;
						//mapResult = new HashMap<String, String>();
						mapResult.put("result", String.valueOf(result));
						UpdateInventoryBeanResult beanResult = new UpdateInventoryBeanResult();
						beanResult.setCallResult(result);
						beanResult.setJsonData(jsonData.toString());
						beanResult.setSelectType(selectType);
						beanResult.setSuppliersType(suppliersType);
						callResult.setBusinessObject(beanResult);
						//return result;
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("orderId",String.valueOf(orderId))
							.addMetaData("goodsId",String.valueOf(goodsId))
							.addMetaData("pindaoId",String.valueOf(pindaoId))
							.addMetaData("num",String.valueOf(num))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:updateInventory run exception!",e);
				}
				log.info(lm.addMetaData("orderId",String.valueOf(orderId))
						.addMetaData("goodsId",String.valueOf(goodsId))
						.addMetaData("pindaoId",String.valueOf(pindaoId))
						.addMetaData("num",String.valueOf(num))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				//return mapResult;
				return result;
				
			}
			//ҵ����ɹ��󣬽�����־����Ϣ���µ��첽���д���
			@Override
			public void workAfter(Jedis p) throws Exception {
				UpdateInventoryBeanResult beanResult = callResult.getBusinessResult();
				// ���������µĶ�����Ϣ ÿ�����г�Ա(member)���Լ���Ψһ������idֵ
				RedisInventoryQueueDO queueDO = asemblyQueueDO(
						sequenceUtil.getSequence(
								SEQNAME.seq_queue_send), goodsId,
						orderId, limitStorage,/*ResultStatusEnum.LOCKED.getCode(),*/beanResult.getSelectType(), beanResult.getSuppliersType(),
									 beanResult.getJsonData());
				// ������������־����
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						goodsId, orderId, beanResult.getSelectType(), beanResult.getSuppliersType(),
						 beanResult.getJsonData(),
						ResultStatusEnum.DEDUCTION.getDescription(),
						userId, system, clientIp, "",
						asemblyJsonData(queueDO, pindaoId));
				//�����е�id���ظ����÷�
				mapResult.put(QueueConstant.QUEUE_KEY_ID,
						String.valueOf(queueDO.getId()));
				//����������Ϣѹ��redis zsetģ�����
				inventoryQueueService.pushQueueSendMsg(queueDO);
				// �������־������Ϣѹ�뵽redis list
				inventoryQueueService.pushLogQueues(logDO);
			}
			
		});
		return mapResult;
	}

	@Override
	public void inventoryCallbackAck(final String ack, final String key)
			throws Exception {
		//return writeJedisFactory.withJedisDo(new JWork<Boolean>() {
		writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				boolean result = false;
				if (j == null){
					return result;
				}
				return true;
			}
			//���¶���״̬,������Ӧ����
			@Override
			public void workAfter(Jedis j) throws Exception {

				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.inventoryCallbackAck(ack,key)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("ack",ack)
						.addMetaData("key",QueueConstant.QUEUE_KEY_MEMBER
								+ ":" + key)
						.addMetaData("startTime", startTime).toJson());
				
				try {
					// �򿪶����key�ļ��
					//j.watch(QueueConstant.QUEUE_KEY_MEMBER + ":" + key,
							//QueueConstant.QUEUE_SEND_MESSAGE);
					// ������������������
					Transaction ts = j.multi();
					
					if (StringUtils.isNotBlank(ack)
							&& ack.equalsIgnoreCase(ResultStatusEnum.ACTIVE
									.getCode())) {
						
						// ����keyȡ������Ķ��󣬽�ϵͳ��������ʱ���ã���Ϊ������Ч��Ĭ����60����
						String member = j.get(QueueConstant.QUEUE_KEY_MEMBER
								+ ":" + key);
						// ����Ϣ���Ͷ���״̬����Ϊ:1>��������Ч�ɴ���active��
						Double scoreAck = j
								.zincrby(
										QueueConstant.QUEUE_SEND_MESSAGE/* +":"+key */,
										-(2), member);
						// ִ������
						ts.exec();
						if (scoreAck == 1) { // scoreAck���ص��Ƕ��ڵĽ��ֵ
							//result = true;
						}
					} else if (StringUtils.isNotBlank(ack)
							&& ack.equalsIgnoreCase(ResultStatusEnum.EXCEPTION
									.getCode())) { //EXCEPTION    ("5",   "�쳣����")
						
						// ����keyȡ������Ķ��󣬽�ϵͳ��������ʱ[���������øýӿ�ʱ]���ã���Ϊ������Ч��Ĭ����60����
						String member = j.get(QueueConstant.QUEUE_KEY_MEMBER
								+ ":" + key);
						//JSONObject.toBean(JSONObject.fromObject(member));
						RedisInventoryNumDO inventoryNumDO = null;
						RedisInventoryQueueDO queueDO = (RedisInventoryQueueDO) LogUtil.jsonToObject(member,RedisInventoryQueueDO.class);
						if(queueDO!=null) {
							inventoryNumDO = (RedisInventoryNumDO) LogUtil.jsonToObject(queueDO.getVariableQuantityJsonData(), RedisInventoryNumDO.class);
							if(inventoryNumDO!=null) {
								//��ԭ���
								//result = 
										nullCacheInitService.rollbackInventoryCache(j, queueDO.getGoodsId(),queueDO.getLimitStorage(), inventoryNumDO);
							}
						}
						// ɾ��������Ϣ(member):�����µĶ�����Ϣ
						Long scoreAck = j
								.zrem(QueueConstant.QUEUE_SEND_MESSAGE,member);
						// ִ������
						ts.exec();
						if (scoreAck == 1) { //  ɾ��N��member�ͷ�����ֵN
							//result = true;
						}
						
					}else {
						// ��֤��һ�������ִ�в���Ӱ��
						//j.unwatch();
						
					}
					// ��������
					if (ts != null)
						ts.discard();
				} catch (Exception e) {
					log.error(lm.addMetaData("ack",ack)
							.addMetaData("key",QueueConstant.QUEUE_KEY_MEMBER
									+ ":" + key)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:inventoryCallbackAck run exception!",e);
				}
				log.info(lm.addMetaData("ack",ack)
						.addMetaData("key",QueueConstant.QUEUE_KEY_MEMBER
								+ ":" + key)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
						//.addMetaData("result", result).toJson());
				//return result;
				
			}
			
			
		});

	}

	@Override
	public boolean inventoryAdjustment(final String key, final String field,
			final int num) throws Exception {

		return writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.inventoryAdjustment(key,field,num)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key",InventoryEnum.HASHCACHE
						+ ":"+key)
						.addMetaData("field",field)
						.addMetaData("num",String.valueOf(num))
						.addMetaData("startTime", startTime).toJson());
				boolean result = false;
				if (j == null)
					return result;
				try {
					/***
					 * hincrby���ص���field���º��ֵ ��� key �����ڣ�һ���µĹ�ϣ��������ִ�� HINCRBY ���
					 * ����� field �����ڣ���ô��ִ������ǰ�����ֵ����ʼ��Ϊ 0 ��
					 */
					Long resultAck = j.hincrBy(InventoryEnum.HASHCACHE
							+ ":"+key, field, (num));
					if (resultAck >= 0) { // ��Ϊ��桢עˮ��ֵ����Ϊ����
						result = true;
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key",InventoryEnum.HASHCACHE
							+ ":"+key)
							.addMetaData("field",field)
							.addMetaData("num",String.valueOf(num))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:inventoryCallbackAck run exception!",e);
				}
				log.info(lm.addMetaData("key",InventoryEnum.HASHCACHE
						+ ":"+key)
						.addMetaData("field",field)
						.addMetaData("num",String.valueOf(num))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				//TODO ��¼��Ӧ������־
				// TODO ҲҪ������Ӧ����֪ͨ
				
			}
			
		});

	}

	@Override
	public boolean waterfloodValAdjustment(final String key, final int num,final Long userId, final String system, final String clientIp)
			throws Exception {

		return writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.waterfloodValAdjustment(key,num)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key",InventoryEnum.HASHCACHE
						+ ":"+key)
						.addMetaData("num",String.valueOf(num))
						.addMetaData("startTime", startTime).toJson());
				boolean result = false;
				if (j == null)
					return result;
				try {
					// hincrby���ص���field���º��ֵ
					Long resultAck = j.hincrBy(InventoryEnum.HASHCACHE
							+ ":"+key,
							HashFieldEnum.waterfloodVal.toString(), (num));
					if (resultAck >= 0) { // ��Ϊעˮֵ����Ϊ����
						result = true;
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key",InventoryEnum.HASHCACHE
							+ ":"+key)
							.addMetaData("num",String.valueOf(num))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:waterfloodValAdjustment run exception!",e);
				}
				log.info(lm.addMetaData("key",InventoryEnum.HASHCACHE
						+ ":"+key)
						.addMetaData("num",String.valueOf(num))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				//TODO ��¼��Ӧעˮֵ������־
				// TODO ����עˮֵ����֪ͨ
				JSONObject json = new JSONObject();
				json.put("key", key);
				json.put("HashFieldEnum.waterfloodVal.toString()", HashFieldEnum.waterfloodVal.toString());
				json.put("num", num);
				// ������������־����
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						Long.valueOf(key), 0L, "", "",
						 "",
						ResultStatusEnum.MANUALADAPT.getDescription(),
						userId, system, clientIp, "",
						json.toString());
				//ѹ����־����
				inventoryQueueService.pushLogQueues(logDO);
				//���Ϳ��������Ϣ[��������]�������߶��з�������Ϣ��
				WaterfloodValAdjustment wf = new WaterfloodValAdjustment();
				wf.setGoodsId(Long.valueOf(key));
				wf.setWaterfloodVal(num);
				notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(wf));
			}
			
		});

	}

	/**
	 * ����װ����־����
	 * 
	 * @param id
	 * @param goodsId
	 * @param orderId
	 * @param status
	 * @param selectType
	 * @param suppliersType
	 * @param num
	 * @param operateType
	 * @param userId
	 * @param system
	 * @param clientIp
	 * @param remark
	 * @return
	 */
	private RedisInventoryLogDO asemblyLogDO(Long id, Long goodsId,
			Long orderId, String selectType, String suppliersType, String num,
			String operateType, Long userId, String system, String clientIp,
			String remark, String jsonContent) {
		RedisInventoryLogDO logDO = new RedisInventoryLogDO();
		logDO.setId(id);
		logDO.setGoodsId(goodsId);
		logDO.setOrderId(orderId);
		if (StringUtils.isNotEmpty(selectType) ) {
			logDO.setType(QueueConstant.SELECTION);
			logDO.setItem(selectType);
		} else if (StringUtils.isNotEmpty(suppliersType)) {
			logDO.setType(QueueConstant.SUBBRANCH);
			logDO.setItem(suppliersType);
		} else {
			logDO.setType(QueueConstant.GOODS);
		}
		logDO.setVariableQuantity(num);
		logDO.setUserId(userId);
		logDO.setOperateType(operateType);
		logDO.setSystem(system);
		logDO.setClientIp(clientIp);
		logDO.setContent(jsonContent);
		logDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		logDO.setRemark(remark);

		return logDO;
	}

	/**
	 * ����װ����ж���
	 * 
	 * @param id
	 * @param goodsId
	 * @param orderId
	 * @param status
	 * @param selectType
	 * @param suppliersType
	 * @param num
	 * @return
	 */
	private RedisInventoryQueueDO asemblyQueueDO(Long id, Long goodsId,
			Long orderId, int limitStorage,/*String status,*/String selectType, String suppliersType, String num) {
		// ����һ�����ӳ�bean����
		RedisInventoryQueueDO queueDO = new RedisInventoryQueueDO();
		queueDO.setId(id);
		queueDO.setGoodsId(goodsId);
		queueDO.setOrderId(orderId);
		queueDO.setLimitStorage(limitStorage);
		// ���г�ʼ״̬
		//queueDO.setStatus(status);
		if (StringUtils.isNotEmpty(selectType)) {
			queueDO.setType(QueueConstant.SELECTION);
			queueDO.setItem(selectType);
		} else if (StringUtils.isNotEmpty(suppliersType)) {
			queueDO.setType(QueueConstant.SUBBRANCH);
			queueDO.setItem(suppliersType);
		} else {
			queueDO.setType(QueueConstant.GOODS);
		}
		queueDO.setVariableQuantityJsonData(num);
	    queueDO.setCreateTime(TimeUtil.getNowTimestamp10Long());
		// queueDO.setUpdateTime(TimeUtil.getNowTimestamp10Int());

		return queueDO;
	}

	/**
	 * װ����־��¼���json����
	 * 
	 * @param queueDO
	 * @param pindaoId
	 * @param resultAck
	 * @return
	 */
	private String asemblyJsonData(RedisInventoryQueueDO queueDO, int pindaoId) {
		JSONObject json = JSONObject.fromObject(queueDO);
		json.put("Ƶ��", pindaoId);
		// json.put("���仯",jsonData);
		return json.toString();
	}
	/**
	 * װ�����������Ϣ��־��Ϣ
	 * @param riDO
	 * @param rgsrList
	 * @param rgsiList
	 * @param pindaoId
	 * @return
	 */
	private String asemblyInitJsonData(RedisInventoryDO riDO,List<RedisGoodsSelectionRelationDO> rgsrList,
			 List<RedisGoodsSuppliersInventoryDO> rgsiList ,int pindaoId) {
		JSONObject json = JSONObject.fromObject(riDO);
		json.put("Ƶ��", pindaoId);
		if(!CollectionUtils.isEmpty(rgsrList)){
			json.put("ѡ����Ʒ", rgsrList);
		}
		if(!CollectionUtils.isEmpty(rgsiList)){
			json.put("�ֵ���Ʒ", rgsiList);
		}
		json.put("Ƶ��", pindaoId);
		// json.put("���仯",jsonData);
		return json.toString();
	}
	
	/**
	 * װ��notifyserver��Ϣ����
	 * @param userId
	 * @param orderId
	 * @param goodsId
	 * @param limitStorage
	 * @param waterfloodVal
	 * @param variableQuantityJsonData
	 * @return
	 */
	private NotifyMessage asemblyNotifyMessage(Long userId, long orderId,
			Long goodsId, int limitStorage,int waterfloodVal,String variableQuantityJsonData) {
		//������Ϣ��
				NotifyMessage message = new NotifyMessage();
				message.setUserId(userId);
				message.setOrderId(orderId);
				message.setGoodsId(goodsId);
				message.setLimitStorage(limitStorage);
				message.setWaterfloodVal(waterfloodVal);
				message.setVariableQuantityJsonData(variableQuantityJsonData);
				
				return message;
	}
}
