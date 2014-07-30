/**
 * 
 */
package com.tuan.inventory.domain.support.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author henry.yu
 * @Date   2014-7-30
 * @Time  
 *
 */
public class AtomicCount {
	private String tableName;
	private long timeStamp;
	private AtomicInteger count;
	
	private static AtomicCount instance = null;
	
	private AtomicCount(String tableName){
		this.tableName = tableName;
		timeStamp = System.currentTimeMillis();
		count = new AtomicInteger(0);
	}
	
	public static AtomicCount getInstance(String tableName){
		if(instance == null){
			synchronized(AtomicCount.class){
				if(instance == null){
					synchronized(AtomicCount.class){
						instance = new AtomicCount(tableName);
					}
				}
			}
		}
		return instance;
	}
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * @return the count
	 */
	public AtomicInteger getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(AtomicInteger count) {
		this.count = count;
	}
}
