package com.tuan.inventory.domain.repository.impl;

import javax.annotation.Resource;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.support.exception.RedisRunException;
import com.tuan.inventory.domain.support.jedistools.WriteJedisFactory;
import com.tuan.inventory.domain.support.jedistools.WriteJedisFactory.JWork;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.domain.support.util.QueueConstant;
import com.tuan.inventory.model.enu.ResultStatusEnum;

public class InventoryQueueServiceImpl implements InventoryQueueService {
	private final static LocalLogger log = LocalLogger.getLog("InventoryQueueService.LOG");
	@Resource
	WriteJedisFactory writeJedisFactory;
	/**
	 * 将库存日志队列信息压入到redis list
	 */
	@Override
	public void pushLogQueues(final RedisInventoryLogDO logDO) throws Exception {
				writeJedisFactory.withJedisDo(new JWork<Boolean>() {
					@Override
					public boolean work(Jedis j) throws Exception {
						boolean result = false;
						if (j == null){
							return result;
						}
						return true;
					}

					@Override
					public void workAfter(Jedis p) throws Exception {
						LogModel lm = LogModel.newLogModel("InventoryQueueServiceImpl.pushLogQueues");
						long startTime = System.currentTimeMillis();
						log.info(lm.addMetaData("key",QueueConstant.QUEUE_LOGS_MESSAGE)
								.addMetaData("element",logDO)
								.addMetaData("startTime", startTime).toJson());
						try {
							
							// 将库存日志队列信息压入到redis list
							p.lpush(QueueConstant.QUEUE_LOGS_MESSAGE,
									JSONObject.fromObject(logDO).toString());
						} catch (Exception e) {
							log.error(lm.addMetaData("key",QueueConstant.QUEUE_LOGS_MESSAGE)
									.addMetaData("element",logDO)
									.addMetaData("endTime", System.currentTimeMillis())
									.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
							throw new RedisRunException("InventoryQueueServiceImpl.pushLogQueues run exception!",e);
						}
						log.info(lm.addMetaData("key",QueueConstant.QUEUE_LOGS_MESSAGE)
								.addMetaData("element",logDO)
								.addMetaData("endTime", System.currentTimeMillis())
								.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
						
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
				if (j == null){
					return result;
				}
				return true;
			}

			@Override
			public void workAfter(Jedis p) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryQueueServiceImpl.pushQueueSendMsg");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key",QueueConstant.QUEUE_SEND_MESSAGE)
						.addMetaData("element",queueDO)
						.addMetaData("startTime", startTime).toJson());
				try {
					// 将库存更新队列信息压入到redis zset集合 便于统计
					// job程序中会每次将score为1的元素取出，做库存消息更新的处理，处理完根据key score member(因id都是唯一的，因此每个member都是不一样的)立即清空源集合中的相关元素，并重复操作
					// 如下 可以指定score值取值 ZADD salary 2500 jack
					// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
					// ，2取到相应member后，按照member及其删除 [ZREM key member]
					// 删除指定score的元素 ZREMRANGEBYSCORE salary 2500 2500
					String jsonMember = JSONObject.fromObject(queueDO)
							.toString();
					// 缓存队列的key、member信息 1小时失效
					p.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
							+ String.valueOf(queueDO.getId()), 3600,
							jsonMember);
					// zset key score value 其中score作为status用
					p.zadd(QueueConstant.QUEUE_SEND_MESSAGE, Double
							.valueOf(ResultStatusEnum.LOCKED.getCode()),
							jsonMember);
				} catch (Exception e) {
					log.error(lm.addMetaData("key",QueueConstant.QUEUE_SEND_MESSAGE)
							.addMetaData("element",queueDO)
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryQueueServiceImpl.pushQueueSendMsg run exception!",e);
				}
				log.info(lm.addMetaData("key",QueueConstant.QUEUE_SEND_MESSAGE)
						.addMetaData("element",queueDO)
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
				
			}
		});

	}

}
