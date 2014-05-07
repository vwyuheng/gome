package com.tuan.inventory.domain.support.jedistools;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.domain.support.exception.CacheRunTimeException;
import com.tuan.inventory.domain.support.jedistools.JedisFactory.JWork;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
/***
 * redis dao层工具类
 * @author henry.yu
 * @date 2014.3
 */
public class RedisCacheUtil {
	private final static LocalLogger log = LocalLogger.getLog("RedisCacheUtil.LOG");
	@Resource
	JedisFactory jedisFactory;
	/**
	 * 返回哈希表 key 中，所有的域和值
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetAll(
			final String key) {
		return jedisFactory.withJedisDo(new JWork<Map<String, String>>() {
			@Override
			public Map<String, String> work(Jedis j) throws Exception {
		
				if (j == null)
					return null;
				try {
					return j.hgetAll(key);
					
				} catch (Exception e) {
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.getData");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hgetAll("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * (域-值)对设置到哈希表 key 中
	 * 此命令会覆盖哈希表中已存在的域
	 * @param key
	 * @param hash
	 * @return
	 */
	public String hmset(
			final String key,final Map<String, String> hash) {
		return jedisFactory.withJedisDo(new JWork<String>() {
			@Override
			public String work(Jedis j) throws Exception {
	
				if (j == null)
					return null;
				try {
					return j.hmset(key,hash);
					
				} catch (Exception e) {
				//异常发生时记录日志
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
	 * 为哈希表 key 中的域 field 的值加上增量 value 。
	 * 增量也可以为负数，相当于对给定域进行减法操作。
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public Long hincrBy(final String key, final String field, final long value) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
		
				if (j == null)
					return null;
				try {
					return j.hincrBy(key,field,value);
					
				} catch (Exception e) {
				//异常发生时记录日志
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
	 * 将一个 member 元素加入到集合 key 当中，
	 * 已经存在于集合的 member 元素将被忽略
	 * @param key
	 * @param member
	 * @return
	 */
	public Long sadd(final String key, final String member) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
		
				if (j == null)
					return null;
				try {
					return j.sadd(key,member);
					
				} catch (Exception e) {
				//异常发生时记录日志
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
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
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
			
				if (j == null)
					return null;
				try {
					return j.zrangeByScore(key,min,max);
					
				} catch (Exception e) {
				//异常发生时记录日志
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
	 * 返回列表 key 中，下标为 index 的元素
	 * @param key
	 * @param index
	 * @return
	 */
	public String lindex(final String key, final long index) {
		return jedisFactory.withJedisDo(new JWork<String>() {
			@Override
			public String work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.lindex(key,index);
					
				} catch (Exception e) {
				//异常发生时记录日志
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
	 * 返回set集合 key 中的所有成员
	 * 不存在的 key 被视为空集合
	 * @param key
	 * @return
	 */
	public Set<String> smembers(final String key) {
		return jedisFactory.withJedisDo(new JWork<Set<String>>() {
			@Override
			public Set<String> work(Jedis j) throws Exception {

				if (j == null)
					return null;
				try {
					return j.smembers(key);
					
				} catch (Exception e) {
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.smembers");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.smembers("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * 检查给定 key 是否存在
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
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.exists");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.exists("+key+") error!",e);
				}
				
			}
		});

	}
	/***
	 * 将一个或多个值 value 插入到列表 key 的表头
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
				//异常发生时记录日志
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
	 * 将值 value 关联到 key ，
	 * 并将 key 的生存时间设为 seconds (以秒为单位)
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
				//异常发生时记录日志
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
	 * 将一个 member 元素及其 score 值加入到有序集 key 当中
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
				//异常发生时记录日志
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
	 * 根据参数 count 的值，移除列表中与参数 value 相等的元素
	 * count 为-1时，表示：移除从表尾到表头，第一个 value元素
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
				//异常发生时记录日志
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
	 * 返回 key 所关联的字符串值。
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
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.get");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("jedis.get("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * 为有序集 key 的成员 member 的 score 值加上增量 score
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
				//异常发生时记录日志
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
	 * 将 key 中储存的数字值增一
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
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.incr");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.incr("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * 查看哈希表 key 中，给定域 field 是否存在
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
				//异常发生时记录日志
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
	 * 删除给定的一个或多个 key 
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
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.del");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.del("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
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
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.hdel");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("fields", fields)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hdel("+key+","+fields+") error!",e);
				}
				
			}
		});

	}
	
	/***
	 * 多个命令顺序调用时需开启事务
	 * @param zincrbykey
	 * @param zincrbymember
	 * @param upStatusNum
	 * @param delkey
	 * @return
	 */
	public boolean zincrbyAnddel(final String zincrbykey,final String zincrbymember,final int upStatusNum,final String delkey) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return false;
				boolean result = true;
				Transaction ts = null;
				try {
					//开启事务
					ts = j.multi(); 
					ts.zincrby(zincrbykey,(upStatusNum),zincrbymember);
					ts.del(delkey);
					// 执行事务
					ts.exec();
				} catch (Exception e) {
					result = false;
					// 销毁事务
					if (ts != null)
						ts.discard();
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.zincrbyAnddel");
				log.error(lm
						    .addMetaData("zincrbykey", zincrbykey)
						    .addMetaData("upStatusNum", upStatusNum)
						    .addMetaData("zincrbymember", zincrbymember)
						    .addMetaData("delkey", delkey)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("jedis.zincrbyAnddel("+delkey+","+zincrbykey+","+upStatusNum+","+zincrbymember+") error!",e);
				}
				
				return result;
			}
		});

	}
	/**
	 * 多个命令顺序调用时需开启事务
	 * @param setexkey
	 * @param zaddkey
	 * @param queueDO
	 * @return
	 */
	public boolean setexAndzadd(final String setexkey,final String zaddkey,final GoodsInventoryQueueDO queueDO) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return false;
				boolean result = true;
				Transaction ts = null;
				try {
					//开启事务
					ts = j.multi(); 
					
					String jsonMember = JSONObject.fromObject(queueDO).toString();
					ts.setex(setexkey,3600*24*365, jsonMember);
					
					ts.zadd(zaddkey,Double.valueOf(ResultStatusEnum.LOCKED.getCode()),
							//Double.valueOf(ResultStatusEnum.CONFIRM.getCode()),  //测试用
							jsonMember);
					
					// 执行事务
					ts.exec();
				} catch (Exception e) {
					result = false;
					// 销毁事务
					if (ts != null)
						ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisLockCache.setexAndzadd");
					log.error(lm.addMetaData("setexkey", setexkey)
							.addMetaData("zaddkey", zaddkey)
							.addMetaData("queueDO", queueDO)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.setexAndzadd("+setexkey+","+zaddkey+","+queueDO+") error!",e);
				}
				
				return result;
			}
		});
		
	}
	/**
	 * 多个命令顺序调用时需开启事务
	 * @param saddkey
	 * @param hmsetkey
	 * @param suppliersId  分店id
	 * @param hash
	 * @return
	 */
	public boolean saddAndhmset(final String saddkey,final String hmsetkey,final String suppliersId,final Map<String,String> hash) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return false;
				boolean result = true;
				Transaction ts = null;
				try {
					//开启事务
					ts = j.multi(); 
					
					ts.sadd(saddkey,suppliersId);
					ts.hmset(hmsetkey,hash);
					// 执行事务
					ts.exec();
				} catch (Exception e) {
					result = false;
					// 销毁事务
					if (ts != null)
						ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisLockCache.saddAndhmset");
					log.error(lm.addMetaData("saddkey", saddkey)
							.addMetaData("hmsetkey", hmsetkey)
							.addMetaData("suppliersId", suppliersId)
							.addMetaData("hash", hash)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.saddAndhmset("+saddkey+","+hmsetkey+","+suppliersId+","+hash+") error!",e);
				}
				
				return result;
			}
		});
		
	}
	
	/**
	 * 两个hincrBy顺序执行
	 * @param key
	 * @param field1 总库存field
	 * @param field2 剩余库存field
	 * @param value
	 * @return
	 */
	public boolean hincrByAndhincrBy(final String key, final String field1,final String field2, final long value) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
				    return false;
				boolean result = true;
				Transaction ts = null;
				try {
					//开启事务
					ts = j.multi(); 
					//总库存
					ts.hincrBy(key, field1, value);
					//剩余库存
					ts.hincrBy(key, field2, value);
					//return j.hincrBy(key,field,value);
					// 执行事务
					ts.exec();
				} catch (Exception e) {
					result = false;
					// 销毁事务
					if (ts != null)
						ts.discard();
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisLockCache.hincrByAndhincrBy");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("field1", field1)
						    .addMetaData("field2", field2)
						    .addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hincrByAndhincrBy("+key+","+field1+","+field2+","+value+") error!",e);
				}
				return result;
			}
		});

	}
	public boolean hincrByAndhincrBy4wf(final String key1, final String key2,final String field, final long value) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return false;
				boolean result = true;
				Transaction ts = null;
				try {
					//开启事务
					ts = j.multi(); 
					//总库存
					ts.hincrBy(key1, field, value);
					//剩余库存
					ts.hincrBy(key2, field, value);
					//return j.hincrBy(key,field,value);
					// 执行事务
					ts.exec();
				} catch (Exception e) {
					result = false;
					// 销毁事务
					if (ts != null)
						ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisLockCache.hincrByAndhincrBy4wf");
					log.error(lm.addMetaData("key1", key1)
							.addMetaData("key2", key2)
							.addMetaData("field", field)
							.addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hincrByAndhincrBy4wf("+key1+","+key2+","+field+","+value+") error!",e);
				}
				return result;
			}
		});
		
	}
	public boolean hincrByAndhincrBy4supp(final String goodskey,final String suppkey, final String field1,final String field2, final long value) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return false;
				boolean result = true;
				Transaction ts = null;
				try {
					//开启事务
					ts = j.multi(); 
					//商品总库存
					ts.hincrBy(goodskey, field1, value);
					//商品剩余库存
					ts.hincrBy(goodskey, field2, value);
					//分店总库存
					ts.hincrBy(suppkey, field1, value);
					//分店剩余库存
					ts.hincrBy(suppkey, field2, value);
					//return j.hincrBy(key,field,value);
					// 执行事务
					ts.exec();
				} catch (Exception e) {
					result = false;
					// 销毁事务
					if (ts != null)
						ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisLockCache.hincrByAndhincrBy4supp");
					log.error(lm.addMetaData("goodskey", goodskey)
							.addMetaData("suppkey", suppkey)
							.addMetaData("field1", field1)
							.addMetaData("field2", field2)
							.addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hincrByAndhincrBy4supp("+goodskey+","+suppkey+","+field1+","+field2+","+value+") error!",e);
				}
				return result;
			}
		});
		
	}
	public boolean hincrByAndhincrBy4sel(final String goodskey,final String selkey, final String field1,final String field2, final long value) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return false;
				boolean result = true;
				Transaction ts = null;
				try {
					//开启事务
					ts = j.multi(); 
					//商品总库存
					ts.hincrBy(goodskey, field1, value);
					//商品剩余库存
					ts.hincrBy(goodskey, field2, value);
					//选型总库存
					ts.hincrBy(selkey, field1, value);
					//选型剩余库存
					ts.hincrBy(selkey, field2, value);
					//return j.hincrBy(key,field,value);
					// 执行事务
					ts.exec();
				} catch (Exception e) {
					result = false;
					// 销毁事务
					if (ts != null)
						ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisLockCache.hincrByAndhincrBy4sel");
					log.error(lm.addMetaData("goodskey", goodskey)
							.addMetaData("selkey", selkey)
							.addMetaData("field1", field1)
							.addMetaData("field2", field2)
							.addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("jedis.hincrByAndhincrBy4sel("+goodskey+","+selkey+","+field1+","+field2+","+value+") error!",e);
				}
				return result;
			}
		});
		
	}
}
