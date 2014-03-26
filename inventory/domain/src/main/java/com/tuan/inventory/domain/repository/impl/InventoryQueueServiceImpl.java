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
	 * �������־������Ϣѹ�뵽redis list
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
							
							// �������־������Ϣѹ�뵽redis list
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
	 * �������¶�����Ϣѹ�뵽redis zset���� ����ͳ��
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
					// �������¶�����Ϣѹ�뵽redis zset���� ����ͳ��
					// job�����л�ÿ�ν�scoreΪ1��Ԫ��ȡ�����������Ϣ���µĴ������������key score member(��id����Ψһ�ģ����ÿ��member���ǲ�һ����)�������Դ�����е����Ԫ�أ����ظ�����
					// ���� ����ָ��scoreֵȡֵ ZADD salary 2500 jack
					// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
					// ��2ȡ����Ӧmember�󣬰���member����ɾ�� [ZREM key member]
					// ɾ��ָ��score��Ԫ�� ZREMRANGEBYSCORE salary 2500 2500
					String jsonMember = JSONObject.fromObject(queueDO)
							.toString();
					// ������е�key��member��Ϣ 1СʱʧЧ
					p.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
							+ String.valueOf(queueDO.getId()), 3600,
							jsonMember);
					// zset key score value ����score��Ϊstatus��
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
