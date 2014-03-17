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
 * 库存扣减相关写服务 新增库存、库存扣减[更新]、库存更新后的回调确认、新增注水、修改注水值、删除库存
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
					if (!redisAck.equalsIgnoreCase("ok")) {
						//log.error("InventoryRelationMainTainService:createInventory invoke error [goodsId="
								//+ goodsId + "]");
					} else {
						// TODO 发送库存新增消息

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
					// 级联删除操作
					// jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
					if (j.exists(InventoryEnum.SETCACHE + ":"
							+ String.valueOf(goodsId))) {
						// 开启事务
						// ts = j.multi();
						if (!CollectionUtils.isEmpty(goodsSelectionList)) {// if2
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
						// TODO 发送库存新增消息

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
				String selectType = null; // 声明选型商品的属性类别
				String suppliersType = null; // 声明分店商品的属性类别
				Map<String, String> mapResult = null;
				// 声明库存变化量json对象
				JSONObject jsonData = new JSONObject();
				boolean result = false;
				if (j == null) {
					mapResult = new HashMap<String, String>();
					mapResult.put("result", String.valueOf(result));
					return mapResult;
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
								mapResult = new HashMap<String, String>();
								mapResult.put("result", String.valueOf(result));
								return mapResult;
							}
						}
						jsonData.put("商品总库存变化量", num);
						resultAck = j.hincrBy(InventoryEnum.HASHCACHE + ":"
								+ String.valueOf(goodsId),
								HashFieldEnum.leftNumber.toString(), (-num));
						if (resultAck < 0) { // 说明本次交易的库存不足
							// 还原扣减的库存
							resultAck = j.hincrBy(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(goodsId),
									HashFieldEnum.leftNumber.toString(), (num));
							mapResult = new HashMap<String, String>();
							mapResult.put("result", String.valueOf(result));
							return mapResult;
						} else {
							jsonData.put("商品总库存剩余量", resultAck);
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
										// 还原被扣减的库存
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
										mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										return mapResult;
									}
								}
								jsonData.put("商品选型库存变化量",
										(orderGoodsSelectionModel.getCount()
												.intValue()));
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
									mapResult = new HashMap<String, String>();
									mapResult.put("result",
											String.valueOf(result));
									return mapResult;
								} else {
									jsonData.put("商品选型库存剩余量", (resultAck));
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
										mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										return mapResult;
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
										mapResult = new HashMap<String, String>();
										mapResult.put("result",
												String.valueOf(result));
										return mapResult;
									}
								}
								jsonData.put("商品分店库存变化量",
										(orderGoodsSelectionModel.getCount()
												.intValue()));
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
									mapResult = new HashMap<String, String>();
									mapResult.put("result",
											String.valueOf(result));
									return mapResult;
								} else {
									jsonData.put("商品分店库存剩余量", (resultAck));
								}

							}// if分店
						}// for
					} // if1
						// 执行事务
						// ts.exec();
					if (resultAck >= 0) {// 库存充足并扣减成功
						result = true;
						mapResult = new HashMap<String, String>();
						mapResult.put("result", String.valueOf(result));
						// 构建库存更新的队列信息 每个队列成员(member)都自己的唯一的序列id值
						RedisInventoryQueueDO queueDO = asemblyQueueDO(
								SequenceUtil.getSequence(
										SEQNAME.seq_queue_send, j), goodsId,
								orderId, /*
											 * ResultStatusEnum.LOCKED.getCode(),
											 */selectType, suppliersType,
								jsonData.toString());
						// 构建库存操作日志对象
						RedisInventoryLogDO logDO = asemblyLogDO(
								SequenceUtil.getSequence(SEQNAME.seq_log, j),
								goodsId, orderId, selectType, suppliersType,
								jsonData.toString(),
								ResultStatusEnum.DEDUCTION.getDescription(),
								userId, system, clientIp, "",
								asemblyJsonData(queueDO, pindaoId));
						// 将库存更新队列信息压入到redis set集合 便于统计
						// job程序中会每次将某区间的元素移动到另一个集合set中去，在这个目的set集合中做库存消息更新的处理，处理完立即情况该集合，并重复操作
						// 如下 可以指定score值取值 ZADD salary 2500 jack
						// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
						// ，2取到相应member后，按照member及其删除 [ZREM key member]
						// 删除指定score的元素 ZREMRANGEBYSCORE salary 2500 2500
						String jsonMember = JSONObject.fromObject(queueDO)
								.toString();
						mapResult.put(QueueConstant.QUEUE_KEY_ID,
								String.valueOf(queueDO.getId()));
						// 缓存队列的key、member信息 1小时失效
						j.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
								+ String.valueOf(queueDO.getId()), 3600,
								jsonMember);
						// zset key score value 其中score作为status用
						j.zadd(QueueConstant.QUEUE_SEND_MESSAGE, Double
								.valueOf(ResultStatusEnum.LOCKED.getCode()),
								jsonMember);
						// 将库存日志队列信息压入到redis list
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
					// 打开对相关key的监控
					j.watch(QueueConstant.QUEUE_KEY_MEMBER + ":" + key,
							QueueConstant.QUEUE_SEND_MESSAGE + ":" + key);
					// 事务声明
					Transaction ts = null;
					if (StringUtils.isNotBlank(ack)
							&& ack.equalsIgnoreCase(ResultStatusEnum.ACTIVE
									.getCode())) {
						// 开启事务
						ts = j.multi();
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
							result = true;
						}
					} else {
						// 保证下一个事务的执行不受影响
						j.unwatch();
					}
					// 销毁事务
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
					 * hincrby返回的是field更新后的值 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
					 * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
					 */
					Long resultAck = j.hincrBy(key, field, (num));
					if (resultAck >= 0) { // 因为库存、注水等值不能为负数
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
					// hincrby返回的是field更新后的值
					Long resultAck = j.hincrBy(key,
							HashFieldEnum.waterfloodVal.toString(), (num));
					if (resultAck >= 0) { // 因为注水值不能为负数
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
			Long orderId, String selectType, String suppliersType, String num) {
		// 构建一个连接池bean对象
		RedisInventoryQueueDO queueDO = new RedisInventoryQueueDO();
		queueDO.setId(id);
		queueDO.setGoodsId(goodsId);
		queueDO.setOrderId(orderId);
		// 队列初始状态
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

}
