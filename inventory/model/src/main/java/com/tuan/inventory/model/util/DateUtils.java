package com.tuan.inventory.model.util;

import java.util.Calendar;

public class DateUtils {

	/***
	 * ��ȡָ��ʱ��ǰ��ʱ�䣬��λ:��
	 * @param minute
	 * @return
	 */
	public static long getBeforXTimestamp10Long(int minute) {
		long curTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(curTime);
		cal.add(10, -8);
		cal.add(Calendar.MINUTE, (-minute));// x����֮ǰ��ʱ��
		String str = String.valueOf(cal.getTime().getTime()).substring(0, 10);
		new Long(0L);
		return Long.parseLong(str);
	}
}
