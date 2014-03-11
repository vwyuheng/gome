package com.tuan.inventory.domain.support.util;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
/**
 * 基于redis的序列生成器
 * @author henry.yu
 * @date 2014/3/11
 */
public class SequenceUtil {
	private static Logger logger=Logger.getLogger(SequenceUtil.class);
	
	public static Long getSequence(SEQNAME seqName, Jedis jedis) {
		Long result = null;
		if (seqName == null) {
			return result;
		}
		String key = seqName.toString();
		long startTime = System.currentTimeMillis();
		result = jedis.incr(key);
		long endTime = System.currentTimeMillis();
		logger.error("key: " + key + "   value: " + result
				+ " Processed Time: " + (endTime - startTime) + " ms");

		return result;
	}
	
}
