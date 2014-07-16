package com.tuan.inventory.client.support.utils;

/**
 * 执行异常：运行时异常
 * @author henry.yu
 * @date 2014/3/17
 */
public class CacheRunTimeException extends Exception {
	private static final long serialVersionUID = 4353196323557490148L;

	/**
	 * 运行时异常
	 * @param msg
	 */
	public CacheRunTimeException(String msg){  
        super(msg);  
    }  
	/**
	 * 运行时异常
	 * @param msg
	 * @param e
	 */
	public CacheRunTimeException(String msg,Exception e){  
        super(msg,e);  
    }  
}
