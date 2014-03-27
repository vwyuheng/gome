package com.tuan.inventory.domain.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.exception.RedisRunException;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory.JWork;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.model.GoodsSelectionRelationModel;
import com.tuan.inventory.model.util.QueueConstant;

/**
 * 用于库存相关的读取服务接口
 * 
 * @author henry.yu
 * @date 2014/3/13
 */
public class InventoryProviderReadServiceImpl implements
		InventoryProviderReadService {
	private final static LocalLogger log = LocalLogger.getLog("InventoryProviderReadService.LOG");
	@Resource
	ReadJedisFactory readJedisFactory;
	@Resource
	NullCacheInitService nullCacheInitService;
	@Override
	public GoodsSelectionRelationDO getSelectionRelationBySrId(
			final int SelectionRelationId) throws Exception {
		
		return readJedisFactory.withJedisDo(new JWork<GoodsSelectionRelationDO>() {
			@Override
			public GoodsSelectionRelationDO work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getSelectionRelationBySrId(SelectionRelationId)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", SelectionRelationId)
						.addMetaData("startTime", startTime).toJson());
			    GoodsSelectionRelationDO result = null;
				// TODO 测试用
				// j.del(String.valueOf(InventoryEnum.HASHCACHE+ ":"+
				// SelectionRelationId));
				if (j == null)
					return result;
				try {
					Map<String, String> objMap = j
							.hgetAll(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(SelectionRelationId));
					if (!CollectionUtils.isEmpty(objMap)) { // if1
						result = (GoodsSelectionRelationDO) ObjectUtil
								.convertMap(GoodsSelectionRelationDO.class,
										objMap);
					} else {
						result = nullCacheInitService.initSelectionRelation(j,
								SelectionRelationId);

					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(SelectionRelationId))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getSelectionRelationBySrId run exception!",e);
				}
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(SelectionRelationId))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
		});

	}
	@Override
	public GoodsSuppliersInventoryDO getSuppliersInventoryBySiId(
			final int SuppliersInventoryId) throws Exception {
		return readJedisFactory.withJedisDo(new JWork<GoodsSuppliersInventoryDO>() {
			@Override
			public GoodsSuppliersInventoryDO work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getSuppliersInventoryBySiId(SuppliersInventoryId)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(SuppliersInventoryId))
						.addMetaData("startTime", startTime).toJson());
				GoodsSuppliersInventoryDO result = null;
				if (j == null)
					return result;
				try {
					// TODO 测试用
					// j.del(String.valueOf(InventoryEnum.HASHCACHE+ ":"+
					// SuppliersInventoryId));
					Map<String, String> objMap = j
							.hgetAll(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(SuppliersInventoryId));
					if (!CollectionUtils.isEmpty(objMap)) {
						result = (GoodsSuppliersInventoryDO) ObjectUtil
								.convertMap(GoodsSuppliersInventoryDO.class,
										objMap);

					} else {
						result = nullCacheInitService.initSuppliersInventory(j,
								SuppliersInventoryId);
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(SuppliersInventoryId))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getSuppliersInventoryBySiId run exception!",e);
				}
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(SuppliersInventoryId))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
			
		});

	}

	@Override
	public List<GoodsSelectionRelationModel> getSelectionRelationBySrIds(
			final List<Long> selectionRelationIdList, final long goodsId)
			throws Exception {
		return readJedisFactory
				.withJedisDo(new JWork<List<GoodsSelectionRelationModel>>() {
					@Override
					public List<GoodsSelectionRelationModel> work(Jedis j)
							throws Exception {
						LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getSelectionRelationBySrIds(selectionRelationIdList,goodsId)");
						long startTime = System.currentTimeMillis();
						log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
								.addMetaData("selectionRelationIdList", selectionRelationIdList)
								.addMetaData("startTime", startTime).toJson());
						List<GoodsSelectionRelationModel> result = null;
						if (j == null)
							return result;
						try {
							if (!CollectionUtils
									.isEmpty(selectionRelationIdList)) { // if1
								result = new ArrayList<GoodsSelectionRelationModel>();
								for (Long lIds : selectionRelationIdList) {
									RedisGoodsSelectionRelationDO resultTmp = null;
									Map<String, String> objMap = j
											.hgetAll(InventoryEnum.HASHCACHE
													+ ":"
													+ String.valueOf(lIds));
									if (!CollectionUtils.isEmpty(objMap)) {
										resultTmp = (RedisGoodsSelectionRelationDO) ObjectUtil
												.convertMap(
														RedisGoodsSelectionRelationDO.class,
														objMap);
										if (resultTmp != null) {
											result.add(asembly(resultTmp));
										}

									} else {
										resultTmp = nullCacheInitService
												.initSRelation(j, lIds);
										if (resultTmp != null) {
											result.add(asembly(resultTmp));
										}
									}
								}
							}// if1
						} catch (Exception e) {
							log.error(lm.addMetaData("goodsId", String.valueOf(goodsId))
									.addMetaData("selectionRelationIdList", selectionRelationIdList)
									.addMetaData("endTime", System.currentTimeMillis())
									.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
							throw new RedisRunException("InventoryProviderReadService:getSelectionRelationBySrIds run exception!",e);
						}
						log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
								.addMetaData("selectionRelationIdList", selectionRelationIdList)
								.addMetaData("endTime", System.currentTimeMillis())
								.addMetaData("useTime", LogUtil.getRunTime(startTime))
								.addMetaData("result", result).toJson());
						return result;
					}
					
				});

	}

	@Override
	public RedisInventoryDO getNotSeleInventory(final long goodsId)
			throws Exception {
		return readJedisFactory.withJedisDo(new JWork<RedisInventoryDO>() {
			@Override
			public RedisInventoryDO work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getNotSeleInventory(goodsId)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(goodsId))
						.addMetaData("startTime", startTime).toJson());
				RedisInventoryDO result = null;
				if (j == null)
					return result;
				try {
					Map<String, String> objMap = j
							.hgetAll(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(goodsId));
					if (!CollectionUtils.isEmpty(objMap)) {
						result = (RedisInventoryDO) ObjectUtil.convertMap(
								RedisInventoryDO.class, objMap);

					} else {
						result = nullCacheInitService
								.initGoodsInventoryNotSelectionOrSuppliers(j,
										goodsId);
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(goodsId)).addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getNotSeleInventory run exception!",e);
				}
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(goodsId))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
			
		});
		
		
	}

	/**
	 * 装配选型model对象信息
	 * 
	 * @param resultTmp
	 * @return
	 */
	private GoodsSelectionRelationModel asembly(
			RedisGoodsSelectionRelationDO resultTmp) {
		GoodsSelectionRelationModel gsrModel = new GoodsSelectionRelationModel();
		gsrModel.setId(resultTmp.getId());
		gsrModel.setGoodId(resultTmp.getGoodsId());
		gsrModel.setLeftNumber(resultTmp.getLeftNumber());
		gsrModel.setTotalNumber(resultTmp.getTotalNumber());
		gsrModel.setLimitStorage(resultTmp.getLimitStorage());
		gsrModel.setGoodTypeId(resultTmp.getGoodTypeId());

		return gsrModel;
	}
	@Override
	public List<RedisInventoryQueueDO> getInventoryQueueByScoreStatus(
			final Double status) throws Exception {
		
		return readJedisFactory.withJedisDo(new JWork<List<RedisInventoryQueueDO>>() {
			@Override
			public List<RedisInventoryQueueDO> work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getNotSeleInventory(goodsId)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", QueueConstant.QUEUE_SEND_MESSAGE)
						.addMetaData("status", String.valueOf(status))
						.addMetaData("startTime", startTime).toJson());
				List<RedisInventoryQueueDO> result = null;
				if (j == null)
					return result;
				try {
					Set<String> members = j.zrangeByScore(QueueConstant.QUEUE_SEND_MESSAGE, status, status);
					result = ObjectUtil.convertSet(members);
					
				} catch (Exception e) {
					log.error(lm.addMetaData("key", QueueConstant.QUEUE_SEND_MESSAGE)
							.addMetaData("status", String.valueOf(status))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getInventoryQueueByScoreStatus run exception!",e);
				}
				log.info(lm.addMetaData("key", QueueConstant.QUEUE_SEND_MESSAGE)
						.addMetaData("status", String.valueOf(status))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
			
		});
		
	}
	@Override
	public List<RedisInventoryLogDO> getInventoryLogsQueue(final String key,
			final int timeout) throws Exception {
		return readJedisFactory.withJedisDo(new JWork<List<RedisInventoryLogDO>>() {
			@Override
			public List<RedisInventoryLogDO> work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getInventoryLogsQueue(key,timeout)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						.addMetaData("timeout", String.valueOf(timeout))
						.addMetaData("startTime", startTime).toJson());
				List<RedisInventoryLogDO> result = null;
				if (j == null)
					return result;
				try {
					List<String> elements = j.brpop(timeout, QueueConstant.QUEUE_LOGS_MESSAGE);
					result = ObjectUtil.convertList(elements);
					
				} catch (Exception e) {
					log.error(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
							.addMetaData("timeout", String.valueOf(timeout))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getInventoryLogsQueue run exception!",e);
				}
				log.info(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						.addMetaData("timeout", String.valueOf(timeout))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
			
		});
		
	}
	
	@Override
	public List<RedisInventoryLogDO> getInventoryLogsQueue() throws Exception {
		return readJedisFactory.withJedisDo(new JWork<List<RedisInventoryLogDO>>() {
			@Override
			public List<RedisInventoryLogDO> work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getInventoryLogsQueue");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						//.addMetaData("timeout", String.valueOf(timeout))
						.addMetaData("startTime", startTime).toJson());
				List<RedisInventoryLogDO> result = null;
				if (j == null)
					return result;
				try {
					String elements = j.rpop(QueueConstant.QUEUE_LOGS_MESSAGE);
					if(StringUtils.isNotEmpty(elements)){
						result =  new ArrayList<RedisInventoryLogDO>();
						RedisInventoryLogDO tmpResult = JsonUtils.convertStringToObject(elements, RedisInventoryLogDO.class);
						result.add(tmpResult);
					}
					
				} catch (Exception e) {
					log.error(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
							//.addMetaData("timeout", String.valueOf(timeout))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getInventoryLogsQueue run exception!",e);
				}
				log.info(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						//.addMetaData("timeout", String.valueOf(timeout))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
			
		});
	}
	@Override
	public List<RedisInventoryLogDO> getInventoryLogsQueueByIndex()
			throws Exception {
		return readJedisFactory.withJedisDo(new JWork<List<RedisInventoryLogDO>>() {
			@Override
			public List<RedisInventoryLogDO> work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getInventoryLogsQueue");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						//.addMetaData("timeout", String.valueOf(timeout))
						.addMetaData("startTime", startTime).toJson());
				List<RedisInventoryLogDO> result = null;
				if (j == null)
					return result;
				try {
					//总是取最后一个元素
					String elements = j.lindex(QueueConstant.QUEUE_LOGS_MESSAGE, (-1));
					if(StringUtils.isNotEmpty(elements)){
						result =  new ArrayList<RedisInventoryLogDO>();
						RedisInventoryLogDO tmpResult = JsonUtils.convertStringToObject(elements, RedisInventoryLogDO.class);
						result.add(tmpResult);
					}
					
				} catch (Exception e) {
					log.error(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
							//.addMetaData("timeout", String.valueOf(timeout))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getInventoryLogsQueue run exception!",e);
				}
				log.info(lm.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						//.addMetaData("timeout", String.valueOf(timeout))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
			
		});
	}
}
