package com.tuan.inventory.client.support.utils;

import java.util.Random;
/**
 * 随机数生成器：随机生成一个seed范围内的一个正整数
 * @author henry.yu
 * @date 2014/7/15
 */
public class Utils {

	public static int random(int seed) {
		return Math.abs(new Random().nextInt())%(seed);
       
	}
}
