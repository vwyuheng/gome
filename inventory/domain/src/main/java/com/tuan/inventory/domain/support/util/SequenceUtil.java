package com.tuan.inventory.domain.support.util;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory.JWork;
/**
 * 基于redis的序列生成器
 * @author henry.yu
 * @date 2014/3/11
 */
public class SequenceUtil {
	private static Logger logger = Logger.getLogger(SequenceUtil.class);
	@Resource
	ReadJedisFactory readJedisFactory;

	public Long getSequence(final SEQNAME seqName) {

		return readJedisFactory.withJedisDo(new JWork<Long>() {
			@Override
			public Long work(Jedis j) {
				Long result = null;
				if (seqName == null) {
					return result;
				}
				String key = seqName.toString();
				long startTime = System.currentTimeMillis();
				result = j.incr(key);
				long endTime = System.currentTimeMillis();
				logger.error("key: " + key + "   value: " + result
						+ " Processed Time: " + (endTime - startTime) + " ms");
				return result;
			}

		});

	}

}
