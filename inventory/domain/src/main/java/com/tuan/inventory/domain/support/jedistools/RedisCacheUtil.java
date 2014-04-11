package com.tuan.inventory.domain.support.jedistools;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.domain.support.exception.CacheRunTimeException;
import com.tuan.inventory.domain.support.jedistools.JedisFactory.JWork;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;

public class RedisCacheUtil {
	private final static LocalLogger log = LocalLogger.getLog("RedisCacheUtil.LOG");
	@Resource
	JedisFactory jedisFactory;
	/**
	 * ���ع�ϣ�� key �У����е����ֵ
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetAll(
			final String key) {
		return jedisFactory.withJedisDo(new JWork<Map<String, String>>() {
			@Override
			public Map<String, String> work(Jedis j) throws Exception {
				// TODO ������
				// j.del(key);
				if (j == null)
					return null;
				try {
					return j.hgetAll(key);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.getData");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hgetAll("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * (��-ֵ)�����õ���ϣ�� key ��
	 * ������Ḳ�ǹ�ϣ�����Ѵ��ڵ���
	 * @param key
	 * @param hash
	 * @return
	 */
	public String hmset(
			final String key,final Map<String, String> hash) {
		return jedisFactory.withJedisDo(new JWork<String>() {
			@Override
			public String work(Jedis j) throws Exception {
				// TODO ������
				// j.del(key);
				if (j == null)
					return null;
				try {
					return j.hmset(key,hash);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.hmset");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("hash", hash)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hmset("+key+","+hash+") error!",e);
				}
				
			}
		});

	}
	/**
	 * Ϊ��ϣ�� key �е��� field ��ֵ�������� value ��
	 * ����Ҳ����Ϊ�������൱�ڶԸ�������м���������
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public Long hincrBy(final String key, final String field, final long value) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				// TODO ������
				// j.del(key);
				if (j == null)
					return null;
				try {
					return j.hincrBy(key,field,value);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.hincrBy");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("field", field)
						    .addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hincrBy("+key+","+field+","+value+") error!",e);
				}
				
			}
		});

	}
	/***
	 * ��һ�� member Ԫ�ؼ��뵽���� key ���У�
	 * �Ѿ������ڼ��ϵ� member Ԫ�ؽ�������
	 * @param key
	 * @param member
	 * @return
	 */
	public Long sadd(final String key, final String member) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				// TODO ������
				// j.del(key);
				if (j == null)
					return null;
				try {
					return j.sadd(key,member);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.sadd");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("member", member)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.sadd("+key+","+member+") error!",e);
				}
				
			}
		});

	}
	/***
	 * �������� key �У����� score ֵ���� min �� max ֮��(�������� min �� max )�ĳ�Ա
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> zrangeByScore(final String key, final double min,
		    final double max) {
		return jedisFactory.withJedisDo(new JWork<Set<String>>() {
			@Override
			public Set<String> work(Jedis j) throws Exception {
				// TODO ������
				// j.del(key);
				if (j == null)
					return null;
				try {
					return j.zrangeByScore(key,min,max);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.zrangeByScore");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("min", min)
						    .addMetaData("max", max)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.zrangeByScore("+key+","+min+","+max+") error!",e);
				}
				
			}
		});

	}
	/***
	 * �����б� key �У��±�Ϊ index ��Ԫ��
	 * @param key
	 * @param index
	 * @return
	 */
	public String lindex(final String key, final long index) {
		return jedisFactory.withJedisDo(new JWork<String>() {
			@Override
			public String work(Jedis j) throws Exception {
				// TODO ������
				// j.del(key);
				if (j == null)
					return null;
				try {
					return j.lindex(key,index);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.index");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("index", index)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.index("+key+","+index+") error!",e);
				}
				
			}
		});

	}
	/***
	 * ����set���� key �е����г�Ա
	 * �����ڵ� key ����Ϊ�ռ���
	 * @param key
	 * @return
	 */
	public Set<String> smembers(final String key) {
		return jedisFactory.withJedisDo(new JWork<Set<String>>() {
			@Override
			public Set<String> work(Jedis j) throws Exception {
				// TODO ������
				// j.del(key);
				if (j == null)
					return null;
				try {
					return j.smembers(key);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.smembers");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.smembers("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * ������ key �Ƿ����
	 * @param key
	 * @return
	 */
	public Boolean exists(final String key) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.exists(key);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.exists");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.exists("+key+") error!",e);
				}
				
			}
		});

	}
	/***
	 * ��һ������ֵ value ���뵽�б� key �ı�ͷ
	 * @param key
	 * @param strings
	 * @return
	 */
	public Long lpush(final String key, final String... strings) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.lpush(key,strings);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.lpush");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("strings", strings)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.lpush("+key+","+strings+") error!",e);
				}
				
			}
		});

	}
	/**
	 * ��ֵ value ������ key ��
	 * ���� key ������ʱ����Ϊ seconds (����Ϊ��λ)
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public String setex(final String key, final int seconds, final String value) {
		return jedisFactory.withJedisDo(new JWork<String>() {
			@Override
			public String work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.setex(key,seconds,value);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.setex");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("seconds", seconds)
						    .addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("jedis.setex("+key+","+seconds+","+value+") error!",e);
				}
				
			}
		});

	}
	/**
	 * ��һ�� member Ԫ�ؼ��� score ֵ���뵽���� key ����
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public Long zadd(final String key, final double score, final String member) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.zadd(key,score,member);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.zadd");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("score", score)
						    .addMetaData("member", member)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("jedis.zadd("+key+","+score+","+member+") error!",e);
				}
				
			}
		});

	}
	/**
	 * ���ݲ��� count ��ֵ���Ƴ��б�������� value ��ȵ�Ԫ��
	 * count Ϊ-1ʱ����ʾ���Ƴ��ӱ�β����ͷ����һ�� valueԪ��
	 * @param key
	 * @param count
	 * @param value
	 * @return
	 */
	public Long lrem(final String key, final long count, final String value) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.lrem(key,count,value);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.lrem");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("count", count)
						    .addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("jedis.lrem("+key+","+count+","+value+") error!",e);
				}
				
			}
		});

	}
	/**
	 * ���� key ���������ַ���ֵ��
	 * @param key
	 * @return
	 */
	public String get(final String key) {
		return jedisFactory.withJedisDo(new JWork<String>() {
			@Override
			public String work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.get(key);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.get");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("jedis.get("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * Ϊ���� key �ĳ�Ա member �� score ֵ�������� score
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public Double zincrby(final String key, final double score,
		    final String member) {
		return jedisFactory.withJedisDo(new JWork<Double>() {
			@Override
			public Double work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.zincrby(key,score,member);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.zincrby");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("score", score)
						    .addMetaData("member", member)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("jedis.zincrby("+key+","+score+","+member+") error!",e);
				}
				
			}
		});

	}
	/**
	 * �� key �д��������ֵ��һ
	 * @param key
	 * @return
	 */
	public Long incr(final String key) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.incr(key);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.incr");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.incr("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * �鿴��ϣ�� key �У������� field �Ƿ����
	 * @param key
	 * @param field
	 * @return
	 */
	public Boolean hexists(final String key, final String field) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.hexists(key,field);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.hexists");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("field", field)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hexists("+key+","+field+") error!",e);
				}
				
			}
		});

	}
	/**
	 * ɾ��������һ������ key 
	 * @param key
	 * @return
	 */
	public Long del(final String key) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.del(key);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.del");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.del("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * ɾ����ϣ�� key �е�һ������ָ���򣬲����ڵ��򽫱����ԡ�
	 * @param key
	 * @return
	 */
	public Long hdel(final String key,final String fields[]) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.hdel(key,fields);
					
				} catch (Exception e) {
				//�쳣����ʱ��¼��־
				LogModel lm = LogModel.newLogModel("RedisLockCache.hdel");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("fields", fields)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hdel("+key+","+fields+") error!",e);
				}
				
			}
		});

	}
}
