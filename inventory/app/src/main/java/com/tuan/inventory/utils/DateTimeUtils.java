package com.tuan.inventory.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
	
	/**
	 * 获取当前时间（北京时间）
	 * @return
	 */
	public static long getSystemTime(){
		return System.currentTimeMillis() / 1000;
	}

	public static long getRunTime(long startTime){
		return System.currentTimeMillis() - startTime ;
	}
	
	/**
	 * 格式化当前时间为“yyyyMMddHHmmss”
	 * @return
	 */
	public static String dateFormat(){
		String format = "yyyyMMddHHmmss";
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static int makeRefundTime(String msgTime) throws ParseException{
		Date date = null;  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		try {
			date = sdf.parse(msgTime);
		} catch (ParseException e) {
			throw e;
		}
		return (int)(date.getTime() / 1000);
	}
	/**
	 * 格式化时间绰
	 * @param dateTimeStr	日期串“yyyyMMddHHmmss”
	 * @return
	 * @throws ParseException
	 */
	public static int dateFormat(String dateTimeStr) throws ParseException{
		Date date = null;  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		try {
			date = sdf.parse(dateTimeStr);
		} catch (ParseException e) {
			throw e;
		}
		return (int)(date.getTime() / 1000);
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(DateTimeUtils.dateFormat());
		System.out.println(DateTimeUtils.makeRefundTime("20131215181216"));
		System.out.println(DateTimeUtils.dateFormat("20131215162325"));
	}

}
