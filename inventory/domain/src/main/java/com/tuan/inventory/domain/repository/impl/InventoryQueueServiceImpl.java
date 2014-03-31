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
	 * �������־������Ϣѹ�뵽redis list
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

					// �������־������Ϣѹ�뵽redis list
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
	 * �������¶�����Ϣѹ�뵽redis zset���� ����ͳ��
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
					// �������¶�����Ϣѹ�뵽redis zset���� ����ͳ��
					// job�����л�ÿ�ν�scoreΪ1��Ԫ��ȡ�����������Ϣ���µĴ������������key score
					// member(��id����Ψһ�ģ����ÿ��member���ǲ�һ����)�������Դ�����е����Ԫ�أ����ظ�����
					// ���� ����ָ��scoreֵȡֵ ZADD salary 2500 jack
					// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
					// ��2ȡ����Ӧmember�󣬰���member����ɾ�� [ZREM key member]
					// ɾ��ָ��score��Ԫ�� ZREMRANGEBYSCORE salary 2500 2500
					String jsonMember = JSONObject.fromObject(queueDO)
							.toString();
					// ������е�key��member��Ϣ 1СʱʧЧ
					p.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
							+ String.valueOf(queueDO.getId()), 3600, jsonMember);
					// zset key score value ����score��Ϊstatus��
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
	 * ���ڽ��־û���mysql�е���־���д�redis list��ɾ��
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
					// �������־������Ϣ�Ƴ�:�����Ƴ����һ����list��ĩ����ǰ�� value ��ͬ�Ķ���
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
				// ������������������
				Transaction ts = p.multi();
				try {
					// ����keyȡ������Ķ��󣬽�ϵͳ��������ʱ���ã���Ϊ������Ч��Ĭ����60����
					String member = p.get(QueueConstant.QUEUE_KEY_MEMBER + ":"
							+ key);
					// ����Ϣ���Ͷ���״̬����Ϊ:ResultStatusEnum ��Ӧ�Ķ���״ֵ̬
					// Double scoreAck =
					p.zincrby(QueueConstant.QUEUE_SEND_MESSAGE, (upStatusNum),
							member);
					// ִ������
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
				
				// ������������������
				Transaction ts = p.multi();
				try {
					// ����keyȡ������Ķ��󣬽�ϵͳ��������ʱ[���������øýӿ�ʱ]���ã���Ϊ������Ч��Ĭ����60����
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
							//��ԭ���
							 nullCacheInitService.rollbackInventoryCache(p, queueDO.getGoodsId(),queueDO.getLimitStorage(), inventoryNumDO);
						}
					}
					// ɾ��������Ϣ(member):�����µĶ�����Ϣ
					//Long scoreAck = j
							//.zrem(QueueConstant.QUEUE_SEND_MESSAGE,member);
					// ����Ϣ���Ͷ���״̬����Ϊ:ResultStatusEnum :��Ӧ��ҵ��״̬
					//Double scoreAck = //  ���ص��ǵ�ǰ�޸ĺ�Ľ��
					/*p.zincrby(QueueConstant.QUEUE_SEND_MESSAGE,
									(upStatusNum), member);*/
					// ִ������
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
