package com.tuan.inventory.model.util;

import java.util.Calendar;

public class DateUtils {

	/***
	 * 获取指定时间前的时间，单位:分
	 * @param minute
	 * @return
	 */
	public static long getBeforXTimestamp10Long(int minute) {
		long curTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(curTime);
		cal.add(10, -8);
		cal.add(Calendar.MINUTE, (-minute));// x分钟之前的时间
		String str = String.valueOf(cal.getTime().getTime()).substring(0, 10);
		new Long(0L);
		return Long.parseLong(str);
	}
}
