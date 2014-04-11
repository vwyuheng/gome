package com.tuan.inventory.domain.support.util;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.tuan.inventory.domain.support.jedistools.RedisCacheUtil;

/**
 * 基于redis的序列生成器
 * 
 * @author henry.yu
 * @date 2014/3/11
 */
public class SequenceUtil {
	private static Logger logger = Logger.getLogger(SequenceUtil.class);
	@Resource
	RedisCacheUtil redisCacheUtil;

	public Long getSequence(final SEQNAME seqName) {

		Long result = null;
		if (seqName == null) {
			return result;
		}
		String key = seqName.toString();
		long startTime = System.currentTimeMillis();
		result = redisCacheUtil.incr(key);
		long endTime = System.currentTimeMillis();
		logger.error("key: " + key + "   value: " + result
				+ " Processed Time: " + (endTime - startTime) + " ms");
		return result;

	}

}
