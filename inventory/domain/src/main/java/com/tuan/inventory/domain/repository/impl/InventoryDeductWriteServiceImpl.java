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
 * 库存扣减相关写服务 新增库存、库存扣减[更新]、库存更新后的回调确认、新增注水、修改注水值、删除库存
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
							+ String.valueOf(goodsId))) { // 已存在返回false
						return result;
					}
					// 1.保存商品库存主体信息
					String redisAck = j.hmset(InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(goodsId),
							ObjectUtil.convertBean(riDO));
					// 商品是否添加配型 0：不添加；1：添加
					if (riDO.getIsAddGoodsSelection() == 1) {
						if (!CollectionUtils.isEmpty(rgsrList)) {
							// jedis.sadd(String.valueOf(goodsId),
							// StringUtil.getSelectionRelationString(rgsrList));
							for (RedisGoodsSelectionRelationDO rgsrDO : rgsrList) {
								// 2.商品id与选型关系
								j.sadd(InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId),
										String.valueOf(rgsrDO.getId()));
								// 3.保存选型库存主体信息
								redisAck = j.hmset(InventoryEnum.HASHCACHE
										+ ":" + String.valueOf(rgsrDO.getId()),
										ObjectUtil.convertBean(rgsrDO));
							}

						}
					}
					// 商品销售是否需要指定分店 0：不指定；1：指定
					if (riDO.getIsDirectConsumption() == 1) {
						if (!CollectionUtils.isEmpty(rgsiList)) {
							// jedis.sadd(String.valueOf(goodsId),
							// StringUtil.getSuppliersInventoryString(rgsiList));
							for (RedisGoodsSuppliersInventoryDO rgsiDO : rgsiList) {
								// 2.商品与商家分店关系
								j.sadd(InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId),
										String.valueOf(rgsiDO.getId()));
								// 3.保存商品分店库存主体信息
								redisAck = j.hmset(InventoryEnum.HASHCACHE
										+ ":" + String.valueOf(rgsiDO.getId()),
										ObjectUtil.convertBean(rgsiDO));
							}
						}
					}
					// 执行事务
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
			// 构建库存操作日志对象
			RedisInventoryLogDO logDO = asemblyLogDO(
					sequenceUtil.getSequence(SEQNAME.seq_log),
					goodsId, 0L, "", "",
					 "",
					ResultStatusEnum.INVENTORYINIT.getDescription(),
					userId, system, clientIp, "",
					asemblyInitJsonData(riDO,rgsrList,rgsiList, pindaoId));
			//压入日志队列
			inventoryQueueService.pushLogQueues(logDO);
			//发送库存新增消息[立即发送]，不在走队列发更新消息了
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
					// 新增返回1 :hsetnx 若域 field 已经存在，该操作无效。
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
				// TODO 插入操作日志【插入了一条新字段数据】
				JSONObject json = new JSONObject();
				json.put("field", field);
				json.put("value", value);
				// 构建库存操作日志对象
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						goodsId, 0L, "", "",
						 "",
						ResultStatusEnum.MANUALADAPT.getDescription(),
						userId, system, clientIp, "",
						json.toString());
				//压入日志队列
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
				// TODO 记录覆盖更新单条库存信息的日志
				// TODO 发送相关更新的消息
				
				JSONObject json = new JSONObject();
				json.put("field", field);
				json.put("value", value);
				// 构建库存操作日志对象
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						goodsId, 0L, "", "",
						 "",
						ResultStatusEnum.MANUALADAPT.getDescription(),
						userId, system, clientIp, "",
						json.toString());
				//压入日志队列
				inventoryQueueService.pushLogQueues(logDO);
				//发送库存新增消息[立即发送]，不在走队列发更新消息了
				notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(asemblyNotifyMessage(userId, 0L, goodsId, 0, 0, json.toString())));
			}
			
			
		});

	}

	@Override
	public boolean deleteInventory(final long goodsId,
			final List<OrderGoodsSelectionModel> goodsSelectionList,final Long userId, final String system, final String clientIp)
			throws Exception {
		//构建业务返回对象
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
				String selectType = null; // 声明选型商品的属性类别
				String suppliersType = null; // 声明分店商品的属性类别
				if (j == null)
					return result;
				// Transaction ts = null;
				long redisAck = 0;
				try {
					// 级联删除操作
					// jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
					if (j.exists(InventoryEnum.SETCACHE + ":"
							+ String.valueOf(goodsId))) {
						// 开启事务
						// ts = j.multi();
						if (!CollectionUtils.isEmpty(goodsSelectionList)) {// if2
							// 选型商品类别赋值
							selectType = StringUtil
									.getIdsString(goodsSelectionList);
							// 分店商品类别赋值
							suppliersType = StringUtil
									.getIdsString(goodsSelectionList);
							for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {// if3
								if (orderGoodsSelectionModel
										.getSelectionRelationId() != null
										&& orderGoodsSelectionModel
												.getSelectionRelationId() > 0) { // 删除商品选型库存
									// 根据选型id删除选型库存信息
									j.del(InventoryEnum.HASHCACHE
											+ ":"
											+ orderGoodsSelectionModel
													.getSelectionRelationId());
								}
								if (orderGoodsSelectionModel.getSuppliersId() > 0) { // 删除商品分店的库存
									// 根据选型id删除选型分店库存信息
									j.del(InventoryEnum.HASHCACHE
											+ ":"
											+ orderGoodsSelectionModel
													.getSuppliersId());
								}
							}// if3
						}// if2
							// 3.最后删除商品库存主体信息和商品id与选型id或商品分店id之间的对应关系
						redisAck = j.del(
								InventoryEnum.HASHCACHE + ":"
										+ String.valueOf(goodsId),
								InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId));
						// 执行事务
						// ts.exec();
					}// else {//if1
						// 保证下一个事务的执行不受影响
						// jedis.unwatch();
					// }
					if (redisAck > 0) {
						// TODO 发送库存更新消息

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
				// TODO 记录库存删除日志
				// TODO 发送库存更新消息
				UpdateInventoryBeanResult beanResult = callResult.getBusinessResult();
				JSONObject json = new JSONObject();
				json.put("goodsSelectionList", goodsSelectionList);
				// 构建库存操作日志对象
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						goodsId, 0L, beanResult.getSelectType(), beanResult.getSuppliersType(),
						 "",
						ResultStatusEnum.MANUALADAPT.getDescription(),
						userId, system, clientIp, "",
						json.toString());
				//压入日志队列
				inventoryQueueService.pushLogQueues(logDO);
				//发送库存新增消息[立即发送]，不在走队列发更新消息了
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
		    //构建接口返回类型
		    final Map<String, String> mapResult = new HashMap<String, String>();
		    //构建业务返回对象
		    final CallResult<UpdateInventoryBeanResult> callResult = new CallResult<UpdateInventoryBeanResult>();
		    //业务执行
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
				
				String selectType = null; // 声明选型商品的属性类别
				String suppliersType = null; // 声明分店商品的属性类别
				// 声明库存变化量json对象
				JSONObject jsonData = new JSONObject();
				//声明一个保存商品选型库存变化的list
				List<RedisSelectionNumDO> sLists= new ArrayList<RedisSelectionNumDO>();
				//声明一个保存商品分店库存变化的list
				List<RedisSuppliersNumDO> suppLists= new ArrayList<RedisSuppliersNumDO>();
				boolean result = false;
				if (j == null) {
					//mapResult = new HashMap<String, String>();
					mapResult.put("result", String.valueOf(result));
					//return mapResult;
					return result;
				}
				// 开启事务
				// Transaction ts = j.multi();
				long resultAck = 0;
				try {
					if (goodsId > 0 && limitStorage == 1) { // limitStorage>0:库存无限制；1：限制库存
						if (!j.hexists(
								InventoryEnum.HASHCACHE + ":"
										+ String.valueOf(goodsId), /* "leftNumber" */
								HashFieldEnum.leftNumber.toString())) { // key field
																		// 域不存在则初始化
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
						//jsonData.put("商品总库存变化量", num);
						jsonData.put(InventoryVarQttEnum.num, num);
						resultAck = j.hincrBy(InventoryEnum.HASHCACHE + ":"
								+ String.valueOf(goodsId),
								HashFieldEnum.leftNumber.toString(), (-num));
						if (resultAck < 0) { // 说明本次交易的库存不足
							// 还原扣减的库存
							resultAck = j.hincrBy(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(goodsId),
									HashFieldEnum.leftNumber.toString(), (num));
							//mapResult = new HashMap<String, String>();
							mapResult.put("result", String.valueOf(result));
							//return mapResult;
							return result;
						} else {
							//jsonData.put("商品总库存剩余量", resultAck);
							jsonData.put(InventoryVarQttEnum.leftNum, resultAck);
						}

					}
					if (!CollectionUtils.isEmpty(goodsSelectionList)) { // if1
						// 选型商品类别赋值
						selectType = StringUtil
								.getIdsString(goodsSelectionList);
						// 分店商品类别赋值
						suppliersType = StringUtil
								.getIdsString(goodsSelectionList);
						
						for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) { // for
							if (orderGoodsSelectionModel
									.getSelectionRelationId() != null
									&& orderGoodsSelectionModel
											.getSelectionRelationId() > 0) { // if选型
																				// //更新商品选型库存
								if (!j.exists(InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId))) {
									result = nullCacheInitService
											.initRedisInventoryCache(j,
													goodsId, limitStorage,
													goodsSelectionList);
									if (!result) {
										// 还原被扣减商品的总库存
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
								// 1.首先参数检查获商品是否添加配型关系
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
										// 还原被扣减商品的总库存
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
								// 根据选型id更新选型库存主体信息
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
								if (resultAck < 0) { // 说明本次交易的库存不足
									// 首先还原商品选型被扣减的库存
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
									// 再还原商品主体信息中被扣减的库存
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
									//选型商品库存剩余量
									//jsonData.put(InventoryVarQttEnum.selectionLeftNum, (resultAck));
									//选型商品库存变化量
									RedisSelectionNumDO sDO = new RedisSelectionNumDO();
									//每次扣减的选型库存量
									sDO.setSelectionNum(orderGoodsSelectionModel.getCount()
													.intValue());
									sDO.setSelectionRelationId(orderGoodsSelectionModel
																	.getSelectionRelationId());
									//每次扣减后剩余的选型库存量
									sDO.setSelectionLeftNum(resultAck);
									
									sLists.add(sDO);
								}
								

							} // if选型
							if (orderGoodsSelectionModel.getSuppliersId() > 0) { // if分店
								// 检查商品与分店的关系集
								if (!j.exists(InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId))) {
									// 初始化商品分店的库存
									result = nullCacheInitService
											.initRedisInventoryCache(j,
													goodsId, limitStorage,
													goodsSelectionList);
									if (!result) {
										// 还原被扣减的商品总库存
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
								// 2.检查商品销售是否指定分店
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
										// 还原被扣减的商品总库存
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
								//分店商品库存变化量
								//jsonData.put(InventoryVarQttEnum.supplierNum,
										//(orderGoodsSelectionModel.getCount()
											//	.intValue()));
								// 根据选型id删除选型库存主体信息
								resultAck = j.hincrBy(
										InventoryEnum.HASHCACHE
												+ ":"
												+ orderGoodsSelectionModel
														.getSuppliersId(),
										HashFieldEnum.leftNumber.toString(),
										(-(orderGoodsSelectionModel.getCount()
												.intValue())));
								if (resultAck < 0) { // 说明本次交易的库存不足
									// 首先还原商品分店中被扣减的库存
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
									// 再还原商品主体信息中被扣减的库存
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
									//分店商品库存剩余量
									//jsonData.put(InventoryVarQttEnum.supplierLeftNum, (resultAck));
									RedisSuppliersNumDO suppDO = new RedisSuppliersNumDO();
									//每次扣减的分店库存量
									suppDO.setSupplierNum(orderGoodsSelectionModel.getCount()
													.intValue());
									suppDO.setSuppliersId(orderGoodsSelectionModel
																	.getSuppliersId());
									//每次扣减后剩余的分店库存量
									suppDO.setSupplierLeftNum(resultAck);
									
									suppLists.add(suppDO);
								}

							}// if分店
						}// for
						//商品选型库存变化情况汇总
						jsonData.put("sLists",sLists);
						//商品分店库存变化情况汇总
						jsonData.put("suppLists", suppLists);
					} // if1
						// 执行事务
						// ts.exec();
					if (resultAck >= 0) {// 库存充足并扣减成功
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
			//业务处理成功后，进行日志和消息更新的异步队列创建
			@Override
			public void workAfter(Jedis p) throws Exception {
				UpdateInventoryBeanResult beanResult = callResult.getBusinessResult();
				// 构建库存更新的队列信息 每个队列成员(member)都自己的唯一的序列id值
				RedisInventoryQueueDO queueDO = asemblyQueueDO(
						sequenceUtil.getSequence(
								SEQNAME.seq_queue_send), goodsId,
						orderId, limitStorage,/*ResultStatusEnum.LOCKED.getCode(),*/beanResult.getSelectType(), beanResult.getSuppliersType(),
									 beanResult.getJsonData());
				// 构建库存操作日志对象
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						goodsId, orderId, beanResult.getSelectType(), beanResult.getSuppliersType(),
						 beanResult.getJsonData(),
						ResultStatusEnum.DEDUCTION.getDescription(),
						userId, system, clientIp, "",
						asemblyJsonData(queueDO, pindaoId));
				//将队列的id返回给调用方
				mapResult.put(QueueConstant.QUEUE_KEY_ID,
						String.valueOf(queueDO.getId()));
				//将库存更新消息压入redis zset模拟队列
				inventoryQueueService.pushQueueSendMsg(queueDO);
				// 将库存日志队列信息压入到redis list
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
			//更新队列状态,并做相应处理
			@Override
			public void workAfter(Jedis j) throws Exception {

				LogModel lm = LogModel.newLogModel("InventoryDeductWriteServiceImpl.inventoryCallbackAck(ack,key)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("ack",ack)
						.addMetaData("key",QueueConstant.QUEUE_KEY_MEMBER
								+ ":" + key)
						.addMetaData("startTime", startTime).toJson());
				
				try {
					// 打开对相关key的监控
					//j.watch(QueueConstant.QUEUE_KEY_MEMBER + ":" + key,
							//QueueConstant.QUEUE_SEND_MESSAGE);
					// 事务声明、开启事务
					Transaction ts = j.multi();
					
					if (StringUtils.isNotBlank(ack)
							&& ack.equalsIgnoreCase(ResultStatusEnum.ACTIVE
									.getCode())) {
						
						// 根据key取出缓存的对象，仅系统运行正常时有用，因为其有有效期默认是60分钟
						String member = j.get(QueueConstant.QUEUE_KEY_MEMBER
								+ ":" + key);
						// 将消息发送队列状态更新为:1>正常：有效可处理（active）
						Double scoreAck = j
								.zincrby(
										QueueConstant.QUEUE_SEND_MESSAGE/* +":"+key */,
										-(2), member);
						// 执行事务
						ts.exec();
						if (scoreAck == 1) { // scoreAck返回的是对于的结果值
							//result = true;
						}
					} else if (StringUtils.isNotBlank(ack)
							&& ack.equalsIgnoreCase(ResultStatusEnum.EXCEPTION
									.getCode())) { //EXCEPTION    ("5",   "异常队列")
						
						// 根据key取出缓存的对象，仅系统运行正常时[能正常调用该接口时]有用，因为其有有效期默认是60分钟
						String member = j.get(QueueConstant.QUEUE_KEY_MEMBER
								+ ":" + key);
						//JSONObject.toBean(JSONObject.fromObject(member));
						RedisInventoryNumDO inventoryNumDO = null;
						RedisInventoryQueueDO queueDO = (RedisInventoryQueueDO) LogUtil.jsonToObject(member,RedisInventoryQueueDO.class);
						if(queueDO!=null) {
							inventoryNumDO = (RedisInventoryNumDO) LogUtil.jsonToObject(queueDO.getVariableQuantityJsonData(), RedisInventoryNumDO.class);
							if(inventoryNumDO!=null) {
								//还原库存
								//result = 
										nullCacheInitService.rollbackInventoryCache(j, queueDO.getGoodsId(),queueDO.getLimitStorage(), inventoryNumDO);
							}
						}
						// 删除该条消息(member):库存更新的队列信息
						Long scoreAck = j
								.zrem(QueueConstant.QUEUE_SEND_MESSAGE,member);
						// 执行事务
						ts.exec();
						if (scoreAck == 1) { //  删除N个member就返回数值N
							//result = true;
						}
						
					}else {
						// 保证下一个事务的执行不受影响
						//j.unwatch();
						
					}
					// 销毁事务
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
					 * hincrby返回的是field更新后的值 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
					 * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
					 */
					Long resultAck = j.hincrBy(InventoryEnum.HASHCACHE
							+ ":"+key, field, (num));
					if (resultAck >= 0) { // 因为库存、注水等值不能为负数
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
				//TODO 记录相应调整日志
				// TODO 也要发送相应调整通知
				
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
					// hincrby返回的是field更新后的值
					Long resultAck = j.hincrBy(InventoryEnum.HASHCACHE
							+ ":"+key,
							HashFieldEnum.waterfloodVal.toString(), (num));
					if (resultAck >= 0) { // 因为注水值不能为负数
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
				//TODO 记录相应注水值调整日志
				// TODO 发送注水值调整通知
				JSONObject json = new JSONObject();
				json.put("key", key);
				json.put("HashFieldEnum.waterfloodVal.toString()", HashFieldEnum.waterfloodVal.toString());
				json.put("num", num);
				// 构建库存操作日志对象
				RedisInventoryLogDO logDO = asemblyLogDO(
						sequenceUtil.getSequence(SEQNAME.seq_log),
						Long.valueOf(key), 0L, "", "",
						 "",
						ResultStatusEnum.MANUALADAPT.getDescription(),
						userId, system, clientIp, "",
						json.toString());
				//压入日志队列
				inventoryQueueService.pushLogQueues(logDO);
				//发送库存新增消息[立即发送]，不在走队列发更新消息了
				WaterfloodValAdjustment wf = new WaterfloodValAdjustment();
				wf.setGoodsId(Long.valueOf(key));
				wf.setWaterfloodVal(num);
				notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(wf));
			}
			
		});

	}

	/**
	 * 负责装配日志对象
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
	 * 负责装配队列对象
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
		// 构建一个连接池bean对象
		RedisInventoryQueueDO queueDO = new RedisInventoryQueueDO();
		queueDO.setId(id);
		queueDO.setGoodsId(goodsId);
		queueDO.setOrderId(orderId);
		queueDO.setLimitStorage(limitStorage);
		// 队列初始状态
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
	 * 装配日志记录里的json数据
	 * 
	 * @param queueDO
	 * @param pindaoId
	 * @param resultAck
	 * @return
	 */
	private String asemblyJsonData(RedisInventoryQueueDO queueDO, int pindaoId) {
		JSONObject json = JSONObject.fromObject(queueDO);
		json.put("频道", pindaoId);
		// json.put("库存变化",jsonData);
		return json.toString();
	}
	/**
	 * 装配新增库存信息日志信息
	 * @param riDO
	 * @param rgsrList
	 * @param rgsiList
	 * @param pindaoId
	 * @return
	 */
	private String asemblyInitJsonData(RedisInventoryDO riDO,List<RedisGoodsSelectionRelationDO> rgsrList,
			 List<RedisGoodsSuppliersInventoryDO> rgsiList ,int pindaoId) {
		JSONObject json = JSONObject.fromObject(riDO);
		json.put("频道", pindaoId);
		if(!CollectionUtils.isEmpty(rgsrList)){
			json.put("选型商品", rgsrList);
		}
		if(!CollectionUtils.isEmpty(rgsiList)){
			json.put("分店商品", rgsiList);
		}
		json.put("频道", pindaoId);
		// json.put("库存变化",jsonData);
		return json.toString();
	}
	
	/**
	 * 装配notifyserver消息对象
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
		//构建消息体
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
