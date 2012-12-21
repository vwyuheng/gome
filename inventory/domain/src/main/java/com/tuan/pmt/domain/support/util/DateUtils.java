package com.tuan.pmt.domain.support.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    
    private  static final  SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	/**
	 * 将当前时间，转成Timestamp对象 
	 * @return	当前时间的Timestamp对象 
	 */
	public static Timestamp getNowTimestamp(){
		return new Timestamp((new Date()).getTime());
	}
	
	 /**
     * 判断nowTime是否小于endTime在区间内返回0；大于endTime返回1
     * @return
     */
    public static int checkTimeScope(Timestamp nowTime, Timestamp endTime){
        if(nowTime.after(endTime)){
            return 1;
        }
        return 0;
    }
	
	/**
	 * 判断nowTime是否在startTime和endTime之间。小于startTime返回-1；在区间内返回0；大于endTime返回1
	 * @return
	 */
	public static int checkTimeScope(Timestamp nowTime,Timestamp startTime, Timestamp endTime){
		if(nowTime.before(startTime)){
			return -1;
		}
		if(nowTime.after(endTime)){
			return 1;
		}
		return 0;
	}
	   /**
     * 将Timestamp 类型转化为 Date类型
     * @param time
     * @param fromat
     * @return
     */
    public static Date timestampToDate(Timestamp time) {
        if (null == time) {
            return null;
        }
        try {
            return new Date(time.getTime());
        } catch (Exception ex) {
            return null;
        }
    }
    
  
	
	/**
	 * 将Timestamp 类型转化为 制定格式的字符串
	 * @param time
	 * @param fromat
	 * @return
	 */
    public static String timestampToString(Timestamp time, DateFormat fromat) {
        if (null == fromat) {
            fromat = sformat;
        }
        try {
            return fromat.format(timestampToDate(time));
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * 将字符串转化为为Timestamp
     * @param time
     * @param fromat
     * @return
     */
    public static Timestamp StringToTimestamp(String time,DateFormat fromat){
        if (null == fromat) {
            fromat = sformat;
        }
        try {  
            return new Timestamp(fromat.parse(time).getTime());   
            } catch (ParseException e) { 
                
            }
            return null;
    }
	
	public static void main(String[] args){
//		Timestamp now = getNowTimestamp();
//		Timestamp start = getNowTimestamp();
//		Timestamp end = getNowTimestamp();
//		System.out.println(checkTimeScope(now,start,end));
		
	}
	
}
