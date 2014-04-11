package com.tuan.inventory.domain.support.exception;

/**
 * ִ���쳣������ʱ�쳣
 * @author henry.yu
 * @date 2014/3/17
 */
public class CacheRunTimeException extends Exception {
	private static final long serialVersionUID = 4353196323557490148L;

	/**
	 * ����ʱ�쳣
	 * @param msg
	 */
	public CacheRunTimeException(String msg){  
        super(msg);  
    }  
	/**
	 * ����ʱ�쳣
	 * @param msg
	 * @param e
	 */
	public CacheRunTimeException(String msg,Exception e){  
        super(msg,e);  
    }  
}