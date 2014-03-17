package com.tuan.inventory.domain.support.exception;

/**
 * 执行异常：运行时异常
 * @author henry.yu
 * @date 2014/3/17
 */
public class RedisRunException extends Exception {
	private static final long serialVersionUID = 4353196323557490148L;

	/**
	 * 运行时异常
	 * @param msg
	 */
	public RedisRunException(String msg){  
        super(msg);  
    }  
	/**
	 * 运行时异常
	 * @param msg
	 * @param e
	 */
	public RedisRunException(String msg,Exception e){  
        super(msg,e);  
    }  
}
