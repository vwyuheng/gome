package com.tuan.inventory.domain.repository.impl;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryNumDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.support.exception.RedisRunException;
import com.tuan.inventory.domain.support.jedistools.WriteJedisFactory;
import com.tuan.inventory.domain.support.jedistools.WriteJedisFactory.JWork;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.util.QueueConstant;

public class InventoryQueueServiceImpl implements InventoryQueueService {
	private final static LocalLogger log = LocalLogger
			.getLog("InventoryQueueService.LOG");
	@Resource
	WriteJedisFactory writeJedisFactory;
	@Resource
	NullCacheInitService nullCacheInitService;
	/**
	 * 将库存日志队列信息压入到redis list
	 */
	@Override
	public void pushLogQueues(final RedisInventoryLogDO logDO) throws Exception {
		writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				boolean result = false;
				if (j == null) {
					return result;
				}
				return true;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				LogModel lm = LogModel
						.newLogModel("InventoryQueueServiceImpl.pushLogQueues");
				long startTime = System.currentTimeMillis();
				log.info(lm
						.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						.addMetaData("element", logDO)
						.addMetaData("startTime", startTime).toJson());
				try {

					// 将库存日志队列信息压入到redis list
					p.lpush(QueueConstant.QUEUE_LOGS_MESSAGE, JSONObject
							.fromObject(logDO).toString());
				} catch (Exception e) {
					log.error(
							lm.addMetaData("key",
									QueueConstant.QUEUE_LOGS_MESSAGE)
									.addMetaData("element", logDO)
									.addMetaData("endTime",
											System.currentTimeMillis())
									.addMetaData("useTime",
											LogUtil.getRunTime(startTime))
									.toJson(), e);
					throw new RedisRunException(
							"InventoryQueueServiceImpl.pushLogQueues run exception!",
							e);
				}
				log.info(lm
						.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						.addMetaData("element", logDO)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.toJson());

			}
		});
	}

	/**
	 * 将库存更新队列信息压入到redis zset集合 便于统计
	 */
	@Override
	public void pushQueueSendMsg(final RedisInventoryQueueDO queueDO)
			throws Exception {
		writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				boolean result = false;
				if (j == null) {
					return result;
				}
				return true;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				LogModel lm = LogModel
						.newLogModel("InventoryQueueServiceImpl.pushQueueSendMsg");
				long startTime = System.currentTimeMillis();
				log.info(lm
						.addMetaData("key", QueueConstant.QUEUE_SEND_MESSAGE)
						.addMetaData("element", queueDO)
						.addMetaData("startTime", startTime).toJson());
				try {
					// 将库存更新队列信息压入到redis zset集合 便于统计
					// job程序中会每次将score为1的元素取出，做库存消息更新的处理，处理完根据key score
					// member(因id都是唯一的，因此每个member都是不一样的)立即清空源集合中的相关元素，并重复操作
					// 如下 可以指定score值取值 ZADD salary 2500 jack
					// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
					// ，2取到相应member后，按照member及其删除 [ZREM key member]
					// 删除指定score的元素 ZREMRANGEBYSCORE salary 2500 2500
					String jsonMember = JSONObject.fromObject(queueDO)
							.toString();
					// 缓存队列的key、member信息 1小时失效
					p.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
							+ String.valueOf(queueDO.getId()), 3600, jsonMember);
					// zset key score value 其中score作为status用
					p.zadd(QueueConstant.QUEUE_SEND_MESSAGE,
							Double.valueOf(ResultStatusEnum.LOCKED.getCode()),
							jsonMember);
				} catch (Exception e) {
					log.error(
							lm.addMetaData("key",
									QueueConstant.QUEUE_SEND_MESSAGE)
									.addMetaData("element", queueDO)
									.addMetaData("endTime",
											System.currentTimeMillis())
									.addMetaData("useTime",
											LogUtil.getRunTime(startTime))
									.toJson(), e);
					throw new RedisRunException(
							"InventoryQueueServiceImpl.pushQueueSendMsg run exception!",
							e);
				}
				log.info(lm
						.addMetaData("key", QueueConstant.QUEUE_SEND_MESSAGE)
						.addMetaData("element", queueDO)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.toJson());

			}
		});

	}

	/**
	 * 用于将持久化到mysql中的日志队列从redis list中删除
	 */
	@Override
	public void lremLogQueue(final RedisInventoryLogDO logDO) throws Exception {
		writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				boolean result = false;
				if (j == null) {
					return result;
				}
				return true;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				LogModel lm = LogModel
						.newLogModel("InventoryQueueServiceImpl.lremLogQueue");
				long startTime = System.currentTimeMillis();
				log.info(lm
						.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						.addMetaData("element", logDO)
						.addMetaData("startTime", startTime).toJson());
				try {
					// 将库存日志队列信息移除:总是移除最后一条从list最末端往前找 value 相同的对象
					p.lrem(QueueConstant.QUEUE_LOGS_MESSAGE, (-1), JSONObject
							.fromObject(logDO).toString());
				} catch (Exception e) {
					log.error(
							lm.addMetaData("key",
									QueueConstant.QUEUE_LOGS_MESSAGE)
									.addMetaData("element", logDO)
									.addMetaData("endTime",
											System.currentTimeMillis())
									.addMetaData("useTime",
											LogUtil.getRunTime(startTime))
									.toJson(), e);
					throw new RedisRunException(
							"InventoryQueueServiceImpl.lremLogQueue run exception!",
							e);
				}
				log.info(lm
						.addMetaData("key", QueueConstant.QUEUE_LOGS_MESSAGE)
						.addMetaData("element", logDO)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.toJson());

			}
		});
	}

	@Override
	public void markQueueStatus(final String key, final int upStatusNum)
			throws Exception {
		writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				boolean result = false;
				if (j == null) {
					return result;
				}
				return true;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				LogModel lm = LogModel
						.newLogModel("InventoryQueueServiceImpl.markQueueStatus");
				long startTime = System.currentTimeMillis();
				log.info(lm
						.addMetaData("key",
								QueueConstant.QUEUE_KEY_MEMBER + ":" + key)
						.addMetaData("upStatusNum", upStatusNum)
						.addMetaData("startTime", startTime).toJson());
				// 事务声明、开启事务
				Transaction ts = p.multi();
				try {
					// 根据key取出缓存的对象，仅系统运行正常时有用，因为其有有效期默认是60分钟
					String member = p.get(QueueConstant.QUEUE_KEY_MEMBER + ":"
							+ key);
					// 将消息发送队列状态更新为:ResultStatusEnum 对应的队列状态值
					// Double scoreAck =
					p.zincrby(QueueConstant.QUEUE_SEND_MESSAGE, (upStatusNum),
							member);
					// 执行事务
					ts.exec();
				} catch (Exception e) {
					log.error(
							lm.addMetaData("key",
									QueueConstant.QUEUE_KEY_MEMBER + ":" + key)
									.addMetaData("upStatusNum", upStatusNum)
									.addMetaData("endTime",
											System.currentTimeMillis())
									.addMetaData("useTime",
											LogUtil.getRunTime(startTime))
									.toJson(), e);
					throw new RedisRunException(
							"InventoryQueueServiceImpl.markQueueStatus run exception!",
							e);
				}
				log.info(lm
						.addMetaData("key",
								QueueConstant.QUEUE_KEY_MEMBER + ":" + key)
						.addMetaData("upStatusNum", upStatusNum)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.toJson());

			}
		});
	}

	@Override
	public void rollbackInventoryCache(final String key, final int upStatusNum)
			throws Exception {
		writeJedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public boolean work(Jedis j) throws Exception {
				boolean result = false;
				if (j == null) {
					return result;
				}
				return true;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryQueueServiceImpl.rollbackInventoryCache");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key",key)
						.addMetaData("startTime", startTime).toJson());
				
				// 事务声明、开启事务
				Transaction ts = p.multi();
				try {
					// 根据key取出缓存的对象，仅系统运行正常时[能正常调用该接口时]有用，因为其有有效期默认是60分钟
					String member = p.get(QueueConstant.QUEUE_KEY_MEMBER
							+ ":" + key);
					//JSONObject.toBean(JSONObject.fromObject(member));
					RedisInventoryNumDO inventoryNumDO = null;
					RedisInventoryQueueDO queueDO = null;
					if(StringUtils.isNotEmpty(member)) {
						queueDO = (RedisInventoryQueueDO) LogUtil.jsonToObject(member,RedisInventoryQueueDO.class);
					}
					if(queueDO!=null) {
						inventoryNumDO = (RedisInventoryNumDO) LogUtil.jsonToObject(queueDO.getVariableQuantityJsonData(), RedisInventoryNumDO.class);
						if(inventoryNumDO!=null) {
							//还原库存
							 nullCacheInitService.rollbackInventoryCache(p, queueDO.getGoodsId(),queueDO.getLimitStorage(), inventoryNumDO);
						}
					}
					// 删除该条消息(member):库存更新的队列信息
					//Long scoreAck = j
							//.zrem(QueueConstant.QUEUE_SEND_MESSAGE,member);
					// 将消息发送队列状态更新为:ResultStatusEnum :对应的业务状态
					//Double scoreAck = //  返回的是当前修改后的结果
					/*p.zincrby(QueueConstant.QUEUE_SEND_MESSAGE,
									(upStatusNum), member);*/
					// 执行事务
					ts.exec();
				
				} catch (Exception e) {
					log.error(lm.addMetaData("key",key)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryQueueServiceImpl.rollbackInventoryCache run exception!",e);
				}
				log.info(lm.addMetaData("key",key)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
				
			}
		});
	}
}
