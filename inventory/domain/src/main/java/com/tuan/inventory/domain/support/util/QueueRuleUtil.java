package com.tuan.inventory.domain.support.util;

import com.tuan.core.common.lang.utils.TimeUtil;

/**
 * 队列消费 开始时间 控制
 */
public class QueueRuleUtil {
	
	public static int getConsumeMinTime(int consumeCount) {
		return getConsumeMinTime(consumeCount, 0);
	}

	public static long getNextConsumeSecond(int consumeCount) {
		return getConsumeMinTime(consumeCount, 0);
	}
	
	public static int getConsumeMinTime(int cousumeCount, int addInteval) {
		int secends = getNextConsumeSecond(cousumeCount,addInteval);
		return TimeUtil.getNowTimestamp10Int()+secends;
	}
	
	public static int getNextConsumeSecond(int cousumeCount, int addInteval) {
		if (cousumeCount == 1) {
			return  addInteval + 10;
		} else if (cousumeCount == 2) {
			return  addInteval + 60;
		} else if (cousumeCount == 3) {
			return  addInteval + 60 * 5;
		} else if (cousumeCount == 4) {
			return  addInteval + 60 * 15;
		} else if (cousumeCount == 5) {
			return addInteval + 60 * 60;
		} else if (cousumeCount == 6){
			return addInteval + 60 * 60 *2;
		} else if(cousumeCount == 7){
			return addInteval + 60 * 60 *5;
		} else {
			return addInteval + 60 * 60 * 10;
		}
	}

}
