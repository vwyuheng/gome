package com.tuan.pmt.domain.support.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    
    private  static final  SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	/**
	 * ����ǰʱ�䣬ת��Timestamp���� 
	 * @return	��ǰʱ���Timestamp���� 
	 */
	public static Timestamp getNowTimestamp(){
		return new Timestamp((new Date()).getTime());
	}
	
	 /**
     * �ж�nowTime�Ƿ�С��endTime�������ڷ���0������endTime����1
     * @return
     */
    public static int checkTimeScope(Timestamp nowTime, Timestamp endTime){
        if(nowTime.after(endTime)){
            return 1;
        }
        return 0;
    }
	
	/**
	 * �ж�nowTime�Ƿ���startTime��endTime֮�䡣С��startTime����-1���������ڷ���0������endTime����1
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
     * ��Timestamp ����ת��Ϊ Date����
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
	 * ��Timestamp ����ת��Ϊ �ƶ���ʽ���ַ���
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
     * ���ַ���ת��ΪΪTimestamp
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
