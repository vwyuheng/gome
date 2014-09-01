package com.tuan.inventory.domain.support.jedistools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.alibaba.fastjson.JSON;
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
	private final static LocalLogger log = LocalLogger.getLog("CACHE.ERROR");

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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.hgetAll");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hgetAll("+key+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.hmset");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("hash", hash)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hmset("+key+","+hash+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.hincrBy");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("field", field)
						    .addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hincrBy("+key+","+field+","+value+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.sadd");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("member", member)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.sadd("+key+","+member+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.zrangeByScore");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("min", min)
						    .addMetaData("max", max)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.zrangeByScore("+key+","+min+","+max+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.index");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("index", index)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.index("+key+","+index+") error!",e);
				}
				
			}
		});

	}
	/**
	 * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 end 指定
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> lrange(final String key, final long start,
		    final long end) {
		return jedisFactory.withJedisDo(new JWork<List<String>>() {
			@Override
			public List<String> work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.lrange(key,start,end);
					
				} catch (Exception e) {
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.lrange");
					log.error(lm.addMetaData("key", key)
							.addMetaData("start", start)
							.addMetaData("end", end)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.lrange("+key+","+start+","+end+") error!",e);
				}
				
			}
		});
		
	}
	/**
	 * 返回列表 key 的长度。
              如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
              如果 key 不是列表类型，返回一个错误。
	 * @param key
	 * @return
	 */
	public Long llen(final String key) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.llen(key);
					
				} catch (Exception e) {
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.llen");
					log.error(lm.addMetaData("key", key)
							
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.llen("+key+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.smembers");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.smembers("+key+") error!",e);
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
					return false;
				try {
					return j.exists(key);
					
				} catch (Exception e) {
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.exists");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.exists("+key+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.lpush");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("strings", strings)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.lpush("+key+","+strings+") error!",e);
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
	public Long rpush(final String key, final String... strings) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.rpush(key,strings);
					
				} catch (Exception e) {
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.rpush");
					log.error(lm.addMetaData("key", key)
							.addMetaData("strings", strings)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.rpush("+key+","+strings+") error!",e);
				}
				
			}
		});
		
	}
	/**
	 * 列表的头元素当 key 不存在时，返回 nil 
	 * @param key
	 * @return
	 */
	public String lpop(final String key) {
		return jedisFactory.withJedisDo(new JWork<String>() {
			@Override
			public String work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.lpop(key);
					
				} catch (Exception e) {
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.lpop");
					log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.lpop("+key+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.setex");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("seconds", seconds)
						    .addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("RedisCacheUtil.setex("+key+","+seconds+","+value+") error!",e);
				}
				
			}
		});

	}
	/**
	 * 监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断。
	 * @param key
	 * @return
	 */
	public boolean watch(final String key,final String tagval) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return false;
				boolean result = false;
				try {
					 j.watch(key);
					 String tag = j.get(key);
					 if(tag!=null&&tag.equals(tagval)) {  //tag未被修改过，可以执行redis的数据更新操作
						 result = true;
						 //同时删除该key
						 j.del(key);
					 }else {  //tag 被修改过
						 result = false;
						
					 }
					// 保证下一个事务的执行不受影响
					j.unwatch();
					
				} catch (Exception e) {
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.watch");
					log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.watch("+key+") error!",e);
				}
				return result;
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.zadd");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("score", score)
						    .addMetaData("member", member)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("RedisCacheUtil.zadd("+key+","+score+","+member+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.lrem");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("count", count)
						    .addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("RedisCacheUtil.lrem("+key+","+count+","+value+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.get");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("RedisCacheUtil.get("+key+") error!",e);
				}
				
			}
		});

	}
	/**
	 * 将 key 的值设为 value ，当且仅当 key 不存在
	 * 若给定的 key 已经存在，则 SETNX 不做任何动作。
	 * SETNX 是『SET if Not eXists』(如果不存在，则 SET)的简写
	 * @param key
	 * @param value
	 * @return
	 * 设置成功，返回 1 。
		设置失败，返回 0 
	 */
	public String getSet(final String key, final String value) {
		return jedisFactory.withJedisDo(new JWork<String>() {
			@Override
			public String work(Jedis j) throws Exception {
				if (j == null)
					return null;
				try {
					return j.getSet(key,value);
					
				} catch (Exception e) {
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.value");
					log.error(lm.addMetaData("key", key)
							.addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.get("+key+","+value+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.zincrby");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("score", score)
						    .addMetaData("member", member)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("RedisCacheUtil.zincrby("+key+","+score+","+member+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.incr");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.incr("+key+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.hexists");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("field", field)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hexists("+key+","+field+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.del");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.del("+key+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.hdel");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("fields", fields)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hdel("+key+","+fields+") error!",e);
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
				//Transaction ts = null;
				Pipeline  p = null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();	
					p.zincrby(zincrbykey,(upStatusNum),zincrbymember);
					p.del(delkey);
					// 执行事务
					//ts.exec();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
					throw new CacheRunTimeException("Pipeline error: no response...");
					}
				} catch (Exception e) {
					result = false;
					// 销毁事务
					//if (ts != null)
						//ts.discard();
					if (p != null)
					    p.discard();
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.zincrbyAnddel");
				log.error(lm
						    .addMetaData("zincrbykey", zincrbykey)
						    .addMetaData("upStatusNum", upStatusNum)
						    .addMetaData("zincrbymember", zincrbymember)
						    .addMetaData("delkey", delkey)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("RedisCacheUtil.zincrbyAnddel("+delkey+","+zincrbykey+","+upStatusNum+","+zincrbymember+") error!",e);
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
				//Transaction ts = null;
				Pipeline  p = null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();	
					String jsonMember = JSON.toJSONString(queueDO);
					//缓存7天
					p.setex(setexkey,3600*24*7, jsonMember);
					
					p.zadd(zaddkey,Double.valueOf(ResultStatusEnum.LOCKED.getCode()),
							//Double.valueOf(ResultStatusEnum.CONFIRM.getCode()),  //测试用
							jsonMember);
					
					// 执行事务
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
				} catch (Exception e) {
					result = false;
					// 销毁事务
					//if (ts != null)
						//ts.discard();
					if (p != null)
						p.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.setexAndzadd");
					log.error(lm.addMetaData("setexkey", setexkey)
							.addMetaData("zaddkey", zaddkey)
							.addMetaData("queueDO", queueDO)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.setexAndzadd("+setexkey+","+zaddkey+","+queueDO+") error!",e);
				}
				
				return result;
			}
		});
		
	}
	/**
	 * 多个命令顺序调用时需开启事务
	 * @param saddkey
	 * @param hmsetkey
	 * @param id  分店\选型\物流商品id
	 * @param hash
	 * @return
	 */
	public boolean saddAndhmset(final String saddkey,final String hmsetkey,final String id,final Map<String,String> hash) {
		return jedisFactory.withJedisDo(new JWork<Boolean>() {
			@Override
			public Boolean work(Jedis j) throws Exception {
				if (j == null)
					return false;
				boolean result = true;
				//Transaction ts = null;
				Pipeline  p = null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();		
					p.sadd(saddkey,id);
					p.hmset(hmsetkey,hash);
					// 执行事务
					//ts.exec();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
					
				} catch (Exception e) {
					result = false;
					// 销毁管道
					if (p != null)
						p.discard();
					// 销毁事务
					//if (ts != null)
						//ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.saddAndhmset");
					log.error(lm.addMetaData("saddkey", saddkey)
							.addMetaData("hmsetkey", hmsetkey)
							.addMetaData("id", id)
							.addMetaData("hash", hash)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.saddAndhmset("+saddkey+","+hmsetkey+","+id+","+hash+") error!",e);
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
	public List<Long> hincrByAndhincrBy(final String key, final String field1,final String field2, final long value) {
		return jedisFactory.withJedisDo(new JWork<List<Long>>() {
			@Override
			public List<Long> work(Jedis j) throws Exception {
				if (j == null)
					return null;
				List<Long> result = new ArrayList<Long>();
				//Transaction ts = null;
				Pipeline  p = null;
				try {
					//开启事务
					//ts = j.multi(); 
					 p = j.pipelined();		
					//总库存
					p.hincrBy(key, field1, value);
					//剩余库存
					p.hincrBy(key, field2, value);
					// 执行事务
					//ts.exec();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
					for(Object resp : resultlist){  
						long rest = (Long) resp;
						result.add(rest);
			        }  
				} catch (Exception e) {
					result = null;
					// 销毁管道
					if (p != null)
						p.discard();
					// 销毁事务
					//if (ts != null)
						//ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.hincrByAndhincrBy");
					log.error(lm.addMetaData("key", key)
							.addMetaData("field1", field1)
							.addMetaData("field2", field2)
							.addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hincrByAndhincrBy("+key+","+field1+","+field2+","+value+") error!",e);
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
	public List<Long> hincrByAndhincrBy(final String key,final String key2, final String field1,final String field2,final String field3, final String field4, final long value1,final long value2) {
		return jedisFactory.withJedisDo(new JWork<List<Long>>() {
			@Override
			public List<Long> work(Jedis j) throws Exception {
				if (j == null)
				    return null;
				List<Long> result = new ArrayList<Long>();
				//Transaction ts = null;
				Pipeline  p = null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();					
					//总库存
					p.hincrBy(key, field1, value1);
					//剩余库存
					p.hincrBy(key, field2, value1);
					//调整是否限制库存标识
					p.hincrBy(key, field3, value2);
					
					//库存基表库存总量
					p.hincrBy(key2, field4, value1);
					//return j.hincrBy(key,field,value);
					// 执行事务
					//ts.exec();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
					for(Object resp : resultlist){  
						long rest = (Long) resp;
						result.add(rest);
			        }  
					
				} catch (Exception e) {
					result = null;
					// 销毁管道
					if (p != null)
						p.discard();
					// 销毁事务
					//if (ts != null)
					//	ts.discard();
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.hincrByAndhincrBy");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("field1", field1)
						    .addMetaData("field2", field2)
						    .addMetaData("value1", value1)
						     .addMetaData("value2", value2)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hincrByAndhincrBy("+key+","+field1+","+field2+","+value1+","+value2+") error!",e);
				}
				return result;
			}
		});

	}
	
	/**
	 * 物流调整库存
	 * @param key
	 * @param field1
	 * @param field2
	 * @param value1  总
	 * @param value2 剩余
	 * @return
	 */
	public List<Long> hincrByAndhincrBy4wms(final String key, final String field1,final String field2, final long value1,final long value2) {
		return jedisFactory.withJedisDo(new JWork<List<Long>>() {
			@Override
			public List<Long> work(Jedis j) throws Exception {
				if (j == null)
					return null;
				List<Long> result = new ArrayList<Long>();
				//Transaction ts = null;
				Pipeline  p = null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();	
					//总库存
					p.hincrBy(key, field1, value1);
					//剩余库存
					p.hincrBy(key, field2, value2);
					//return j.hincrBy(key,field,value);
					// 执行事务
					//ts.exec();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
					for(Object resp : resultlist){  
						long rest = (Long) resp;
						result.add(rest);
			        }  
				} catch (Exception e) {
					result = null;
					if (p != null)
						p.discard();
					// 销毁事务
					//if (ts != null)
						//ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.hincrByAndhincrBy4wms");
					log.error(lm.addMetaData("key", key)
							.addMetaData("field1", field1)
							.addMetaData("field2", field2)
							.addMetaData("value1", value1)
							.addMetaData("value2", value2)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hincrByAndhincrBy4wms("+key+","+field1+","+field2+","+value1+value2+") error!",e);
				}
				return result;
			}
		});
		
	}
	/**
	 * 两个hincrBy顺序执行4注水
	 * @param key1
	 * @param key2
	 * @param field
	 * @param value
	 * @return
	 */
	public List<Long> hincrByAndhincrBy4wf(final String key1, final String key2,final String field, final long value) {
		return jedisFactory.withJedisDo(new JWork<List<Long>>() {
			@Override
			public List<Long> work(Jedis j) throws Exception {
				if (j == null)
					return null;
				//boolean result = true;
				List<Long> result = new ArrayList<Long>();
				//Transaction ts = null;
				Pipeline  p =  null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();
					//总库存
					p.hincrBy(key1, field, value);
					//剩余库存
					p.hincrBy(key2, field, value);
					//return j.hincrBy(key,field,value);
					// 执行事务
					//ts.exec();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
					for(Object resp : resultlist){  
						long rest = (Long) resp;
						result.add(rest);
			        }  
				} catch (Exception e) {
					result = null;
					if (p != null)
						p.discard();
					// 销毁事务
					//if (ts != null)
						//ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.hincrByAndhincrBy4wf");
					log.error(lm.addMetaData("key1", key1)
							.addMetaData("key2", key2)
							.addMetaData("field", field)
							.addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hincrByAndhincrBy4wf("+key1+","+key2+","+field+","+value+") error!",e);
				}
				return result;
			}
		});
		
	}
	/**
	 * 四个hincrBy顺序执行 4分店库存
	 * @param goodskey
	 * @param suppkey
	 * @param field1
	 * @param field2
	 * @param value
	 * @return
	 */
	public List<Long> hincrByAndhincrBy4supp(final String goodskey,final String suppkey, final String field1,final String field2, final long value) {
		return jedisFactory.withJedisDo(new JWork<List<Long>>() {
			@Override
			public List<Long> work(Jedis j) throws Exception {
				if (j == null)
					return null;
				List<Long> result = new ArrayList<Long>();
				//Transaction ts = null;
				Pipeline  p =  null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();
					//商品总库存
					p.hincrBy(goodskey, field1, value);
					//商品剩余库存
					p.hincrBy(goodskey, field2, value);
					//分店总库存
					p.hincrBy(suppkey, field1, value);
					//分店剩余库存
					p.hincrBy(suppkey, field2, value);
					//return j.hincrBy(key,field,value);
					// 执行事务
					//ts.exec();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
					for(Object resp : resultlist){  
						long rest = (Long) resp;
						result.add(rest);
			        }  
				} catch (Exception e) {
					result = null;
					if (p != null)
						p.discard();
					// 销毁事务
					//if (ts != null)
						//ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.hincrByAndhincrBy4supp");
					log.error(lm.addMetaData("goodskey", goodskey)
							.addMetaData("suppkey", suppkey)
							.addMetaData("field1", field1)
							.addMetaData("field2", field2)
							.addMetaData("value", value)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hincrByAndhincrBy4supp("+goodskey+","+suppkey+","+field1+","+field2+","+value+") error!",e);
				}
				return result;
			}
		});
		
	}
	/**
	 * 四个hincrBy顺序执行 4选型库存
	 * @param goodskey
	 * @param selkey
	 * @param field1
	 * @param field2
	 * @param value
	 * @return
	 */
	public List<Long> hincrByAndhincrBy4sel(final String goodskey,final String selkey, final String field1,final String field2,/*final String field3, */final long value1/*,final long value2*/) {
		return jedisFactory.withJedisDo(new JWork<List<Long>>() {
			@Override
			public List<Long> work(Jedis j) throws Exception {
				if (j == null)
					return null;
				List<Long> result = new ArrayList<Long>();
				//Transaction ts = null;
				Pipeline  p =  null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();
					//商品总库存
					p.hincrBy(goodskey, field1, value1);
					//商品剩余库存
					p.hincrBy(goodskey, field2, value1);
					//选型总库存
					p.hincrBy(selkey, field1, value1);
					//选型剩余库存
					p.hincrBy(selkey, field2, value1);
					
					//p.hincrBy(goodskey, field3, value2);
					//p.hincrBy(selkey, field3, value2);
					//return j.hincrBy(key,field,value);
					// 执行事务
					//ts.exec();
					//p.sync();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
					for(Object resp : resultlist){  
						long rest = (Long) resp;
						result.add(rest);
			        }  
				} catch (Exception e) {
					result = null;
					// 销毁管道
					if (p != null)
						p.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.hincrByAndhincrBy4sel");
					log.error(lm.addMetaData("goodskey", goodskey)
							.addMetaData("selkey", selkey)
							.addMetaData("field1", field1)
							.addMetaData("field2", field2)
							.addMetaData("value1", value1)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hincrByAndhincrBy4sel("+goodskey+","+selkey+","+field1+","+field2+","+value1+") error!",e);
				}
				return result;
			}
		});
		
	}
	/**
	 * 移除集合 key 中的一个或多个 member 元素，
	 * 不存在的 member 元素会被忽略。
	 * 当 key 不是集合类型，返回一个错误。
	 * @param key
	 * @param member
	 * @return
	 */
	public Long srem(final String key, final String... member) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
		
				if (j == null)
					return null;
				try {
					return j.srem(key,member);
					
				} catch (Exception e) {
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.srem");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("member", member)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.srem("+key+","+member+") error!",e);
				}
				
			}
		});

	}
	
	
	public List<Long> hincrBy2Key(final String inventoryKey, final String baseKey,
			final String itField1,final String itField2,final String bField1,
			final long itValue1,final long itValue2,
			final long bValue1) {
		return jedisFactory.withJedisDo(new JWork<List<Long>>() {
			@Override
			public List<Long> work(Jedis j) throws Exception {
				if (j == null)
					return null;
				List<Long> result = new ArrayList<Long>();
				//Transaction ts = null;
				Pipeline  p = null;
				try {
					//开启事务
					//ts = j.multi(); 
					p = j.pipelined();		
		
					//剩余库存更新
					p.hincrBy(inventoryKey, itField1, itValue1);
					//销量更新
					p.hincrBy(inventoryKey, itField2, itValue2);
					//库存基本表销量更新
					p.hincrBy(baseKey, bField1, bValue1);
					//库存基本表总量更新
					// 执行事务
					//ts.exec();
					List<Object> resultlist = p.syncAndReturnAll();
					if(resultlist == null || resultlist.isEmpty()){  
						throw new CacheRunTimeException("Pipeline error: no response...");
					}
					for(Object resp : resultlist){  
						long rest = (Long) resp;
						result.add(rest);
			        }  
				} catch (Exception e) {
					result = null;
					// 销毁管道
					if (p != null)
						p.discard();
					// 销毁事务
					//if (ts != null)
						//ts.discard();
					//异常发生时记录日志
					LogModel lm = LogModel.newLogModel("RedisCacheUtil.hincrByAndhincrBy");
					log.error(lm.addMetaData("inventoryKey", inventoryKey)
							.addMetaData("itField1", itField1)
							.addMetaData("itField2", itField2)
							.addMetaData("itValue1", itValue1)
							.addMetaData("itValue2", itValue2)
							.addMetaData("baseKey", baseKey)
					        .addMetaData("bField1", bField1)
							.addMetaData("bValue1", bValue1)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.hincrByAndhincrBy("+inventoryKey+","+itField1+","+itValue1+","+itField2+","+itValue2+","+baseKey+","+bField1+","+bValue1+") error!",e);
				}
				return result;
			}
		});
		
	}
	/**
	 * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	 public Long zremrangeByScore(final String key, final double start,
			    final double end) {
		return jedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) throws Exception {
			
				if (j == null)
					return null;
				try {
					return j.zremrangeByScore(key,start,end);
					
				} catch (Exception e) {
				//异常发生时记录日志
				LogModel lm = LogModel.newLogModel("RedisCacheUtil.zremrangeByScore");
				log.error(lm.addMetaData("key", key)
						    .addMetaData("start", start)
						    .addMetaData("end", end)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisCacheUtil.zremrangeByScore("+key+","+start+","+end+") error!",e);
				}
				
			}
		});

	}
	
}
