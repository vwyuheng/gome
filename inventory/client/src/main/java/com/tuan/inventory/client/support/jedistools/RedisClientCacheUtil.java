package com.tuan.inventory.client.support.jedistools;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.client.support.jedistools.JedisFactory.JWork;
import com.tuan.inventory.client.support.utils.CacheRunTimeException;
import com.tuan.inventory.client.support.utils.LogModel;
/***
 * redis client dao层工具类
 * @author henry.yu
 * @date 2014.3
 */
public class RedisClientCacheUtil {
	private static Log log = LogFactory.getLog("INVENTORY.CLIENT.LOG");
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
				LogModel lm = LogModel.newLogModel("RedisClientCacheUtil.hgetAll");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
					throw new CacheRunTimeException("RedisClientCacheUtil.hgetAll("+key+") error!",e);
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
				LogModel lm = LogModel.newLogModel("RedisClientCacheUtil.get");
				log.error(lm.addMetaData("key", key)
							.addMetaData("time", System.currentTimeMillis()).toJson(),e);
				 throw new CacheRunTimeException("RedisClientCacheUtil.get("+key+") error!",e);
				}
				
			}
		});

	}
}
