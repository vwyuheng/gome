package com.tuan.inventory.domain.repository.impl;

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
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.repository.InventoryDeductWriteService;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.exception.RedisRunException;
import com.tuan.inventory.domain.support.jedistools.JedisFactory;
import com.tuan.inventory.domain.support.jedistools.JedisFactory.JWork;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.domain.support.util.QueueConstant;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.OrderGoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;

/**
 * ���ۼ����д���� ������桢���ۼ�[����]�������º�Ļص�ȷ�ϡ�����עˮ���޸�עˮֵ��ɾ�����
 * 
 * @author henry.yu
 * @date 2014/03/10
 */
public class InventoryDeductWriteServiceImpl implements
		InventoryDeductWriteService {
	@Resource
	NullCacheInitService nullCacheInitService;
	@Resource
	JedisFactory jedisFactory;
	
	private final static LocalLogger log = LocalLogger.getLog("InventoryDeductWriteService.LOG");
	@Override
	public Boolean createInventory(final long goodsId,
			final RedisInventoryDO riDO,
			final List<RedisGoodsSelectionRelationDO> rgsrList,
			final List<RedisGoodsSuppliersInventoryDO> rgsiList)
			throws Exception {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.createInventory(goodsId,RedisInventoryDO,"
						+ "List<RedisGoodsSelectionRelationDO>,List<RedisGoodsSuppliersInventoryDO>)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
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
					if (!redisAck.equalsIgnoreCase("ok")) {
						//log.error("InventoryRelationMainTainService:createInventory invoke error [goodsId="
								//+ goodsId + "]");
					} else {
						// TODO ���Ϳ��������Ϣ

						result = true;
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("goodsId", String.valueOf(goodsId))
							.addMetaData("RedisInventoryDO", riDO)
							.addMetaData("List<RedisGoodsSelectionRelationDO>", rgsrList)
							.addMetaData("List<RedisGoodsSuppliersInventoryDO>", rgsiList)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:createInventory run exception!",e);
				}
				log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
						.addMetaData("RedisInventoryDO", riDO)
						.addMetaData("List<RedisGoodsSelectionRelationDO>", rgsrList)
						.addMetaData("List<RedisGoodsSuppliersInventoryDO>", rgsiList)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
		});

	}

	@Override
	public boolean insertSingleInventoryInfoNotExist(final long goodsId,
			final String field, final String value) throws Exception {

		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
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
		});

	}

	@Override
	public boolean updateOverrideSingleInventoryInfo(final long goodsId,
			final String field, final String value) throws Exception {

		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
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
		});

	}

	@Override
	public boolean deleteInventory(final long goodsId,
			final List<OrderGoodsSelectionModel> goodsSelectionList)
			throws Exception {

		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.deleteInventory(goodsId,List<OrderGoodsSelectionModel>)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("goodsId",String.valueOf(goodsId))
						.addMetaData("List<OrderGoodsSelectionModel>",goodsSelectionList)
						.addMetaData("startTime", startTime).toJson());
				boolean result = false;
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
						// TODO ���Ϳ��������Ϣ

						result = true;
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
		});

	}

	@Override
	public Map<String, String> updateInventory(final long orderId,
			final long goodsId, final int pindaoId, final int num,
			final int limitStorage,
			final List<OrderGoodsSelectionModel> goodsSelectionList,
			final Long userId, final String system, final String clientIp)
			throws Exception {
		
		    return jedisFactory.withJedisDo(new JWork<Map<String, String>>() {
			@Override
			public Map<String, String> work(Jedis j) throws Exception {
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
				Map<String, String> mapResult = null;
				// �������仯��json����
				JSONObject jsonData = new JSONObject();
				boolean result = false;
				if (j == null) {
					mapResult = new HashMap<String, String>();
					mapResult.put("result", String.valueOf(result));
					return mapResult;
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
								mapResult = new HashMap<String, String>();
								mapResult.put("result", String.valueOf(result));
								return mapResult;
							}
						}
						jsonData.put("��Ʒ�ܿ��仯��", num);
						resultAck = j.hincrBy(InventoryEnum.HASHCACHE + ":"
								+ String.valueOf(goodsId),
								HashFieldEnum.leftNumber.toString(), (-num));
						if (resultAck < 0) { // ˵�����ν��׵Ŀ�治��
							// ��ԭ�ۼ��Ŀ��
							resultAck = j.hincrBy(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(goodsId),
									HashFieldEnum.leftNumber.toString(), (num));
							mapResult = new HashMap<String, String>();
							mapResult.put("result", String.valueOf(result));
							return mapResult;
						} else {
							jsonData.put("��Ʒ�ܿ��ʣ����", resultAck);
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
										// ��ԭ���ۼ��Ŀ��
										resultAck = j
												.hincrBy(
														InventoryEnum.HASHCACHE
																+ ":"
																+ String.valueOf(goodsId),
														HashFieldEnum.leftNumber
																.toString(),
														(num));
										mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										return mapResult;
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
										mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										return mapResult;
									}
								}
								jsonData.put("��Ʒѡ�Ϳ��仯��",
										(orderGoodsSelectionModel.getCount()
												.intValue()));
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
									mapResult = new HashMap<String, String>();
									mapResult.put("result",
											String.valueOf(result));
									return mapResult;
								} else {
									jsonData.put("��Ʒѡ�Ϳ��ʣ����", (resultAck));
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
										mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										return mapResult;
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
										mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										return mapResult;
									}
								}
								jsonData.put("��Ʒ�ֵ���仯��",
										(orderGoodsSelectionModel.getCount()
												.intValue()));
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
									mapResult = new HashMap<String, String>();
									mapResult.put("result",
											String.valueOf(result));
									return mapResult;
								} else {
									jsonData.put("��Ʒ�ֵ���ʣ����", (resultAck));
								}

							}// if�ֵ�
						}// for
					} // if1
						// ִ������
						// ts.exec();
					if (resultAck >= 0) {// �����㲢�ۼ��ɹ�
						result = true;
						mapResult = new HashMap<String, String>();
						mapResult.put("result", String.valueOf(result));
						// ���������µĶ�����Ϣ ÿ�����г�Ա(member)���Լ���Ψһ������idֵ
						RedisInventoryQueueDO queueDO = asemblyQueueDO(
								SequenceUtil.getSequence(
										SEQNAME.seq_queue_send, j), goodsId,
								orderId, /*
											 * ResultStatusEnum.LOCKED.getCode(),
											 */selectType, suppliersType,
								jsonData.toString());
						// ������������־����
						RedisInventoryLogDO logDO = asemblyLogDO(
								SequenceUtil.getSequence(SEQNAME.seq_log, j),
								goodsId, orderId, selectType, suppliersType,
								jsonData.toString(),
								ResultStatusEnum.DEDUCTION.getDescription(),
								userId, system, clientIp, "",
								asemblyJsonData(queueDO, pindaoId));
						// �������¶�����Ϣѹ�뵽redis set���� ����ͳ��
						// job�����л�ÿ�ν�ĳ�����Ԫ���ƶ�����һ������set��ȥ�������Ŀ��set�������������Ϣ���µĴ�����������������ü��ϣ����ظ�����
						// ���� ����ָ��scoreֵȡֵ ZADD salary 2500 jack
						// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
						// ��2ȡ����Ӧmember�󣬰���member����ɾ�� [ZREM key member]
						// ɾ��ָ��score��Ԫ�� ZREMRANGEBYSCORE salary 2500 2500
						String jsonMember = JSONObject.fromObject(queueDO)
								.toString();
						mapResult.put(QueueConstant.QUEUE_KEY_ID,
								String.valueOf(queueDO.getId()));
						// ������е�key��member��Ϣ 1СʱʧЧ
						j.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
								+ String.valueOf(queueDO.getId()), 3600,
								jsonMember);
						// zset key score value ����score��Ϊstatus��
						j.zadd(QueueConstant.QUEUE_SEND_MESSAGE, Double
								.valueOf(ResultStatusEnum.LOCKED.getCode()),
								jsonMember);
						// �������־������Ϣѹ�뵽redis list
						j.lpush(QueueConstant.QUEUE_LOGS_MESSAGE, JSONObject
								.fromObject(logDO).toString());

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
				return mapResult;
			}
		});

	}

	@Override
	public boolean inventoryCallbackAck(final String ack, final String key)
			throws Exception {

		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.inventoryCallbackAck(ack,key)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("ack",ack)
						.addMetaData("key",key)
						.addMetaData("startTime", startTime).toJson());
				boolean result = false;
				if (j == null)
					return result;
				try {
					// �򿪶����key�ļ��
					j.watch(QueueConstant.QUEUE_KEY_MEMBER + ":" + key,
							QueueConstant.QUEUE_SEND_MESSAGE + ":" + key);
					// ��������
					Transaction ts = null;
					if (StringUtils.isNotBlank(ack)
							&& ack.equalsIgnoreCase(ResultStatusEnum.ACTIVE
									.getCode())) {
						// ��������
						ts = j.multi();
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
							result = true;
						}
					} else {
						// ��֤��һ�������ִ�в���Ӱ��
						j.unwatch();
					}
					// ��������
					if (ts != null)
						ts.discard();
				} catch (Exception e) {
					log.error(lm.addMetaData("ack",ack)
							.addMetaData("key",key)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:inventoryCallbackAck run exception!",e);
				}
				log.info(lm.addMetaData("ack",ack)
						.addMetaData("key",key)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
		});

	}

	@Override
	public boolean inventoryAdjustment(final String key, final String field,
			final int num) throws Exception {

		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.inventoryAdjustment(key,field,num)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key",key)
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
					Long resultAck = j.hincrBy(key, field, (num));
					if (resultAck >= 0) { // ��Ϊ��桢עˮ��ֵ����Ϊ����
						result = true;
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key",key)
							.addMetaData("field",field)
							.addMetaData("num",String.valueOf(num))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:inventoryCallbackAck run exception!",e);
				}
				log.info(lm.addMetaData("key",key)
						.addMetaData("field",field)
						.addMetaData("num",String.valueOf(num))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
		});

	}

	@Override
	public boolean waterfloodValAdjustment(final String key, final int num)
			throws Exception {

		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.waterfloodValAdjustment(key,num)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key",key)
						.addMetaData("num",String.valueOf(num))
						.addMetaData("startTime", startTime).toJson());
				boolean result = false;
				if (j == null)
					return result;
				try {
					// hincrby���ص���field���º��ֵ
					Long resultAck = j.hincrBy(key,
							HashFieldEnum.waterfloodVal.toString(), (num));
					if (resultAck >= 0) { // ��Ϊעˮֵ����Ϊ����
						result = true;
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key",key)
							.addMetaData("num",String.valueOf(num))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryDeductWriteServiceImpl:waterfloodValAdjustment run exception!",e);
				}
				log.info(lm.addMetaData("key",key)
						.addMetaData("num",String.valueOf(num))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
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
		if (selectType != null) {
			logDO.setType(QueueConstant.SELECTION);
			logDO.setItem(selectType);
		} else if (suppliersType != null) {
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
			Long orderId, String selectType, String suppliersType, String num) {
		// ����һ�����ӳ�bean����
		RedisInventoryQueueDO queueDO = new RedisInventoryQueueDO();
		queueDO.setId(id);
		queueDO.setGoodsId(goodsId);
		queueDO.setOrderId(orderId);
		// ���г�ʼ״̬
		// queueDO.setStatus(status);
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
		// queueDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
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

}
