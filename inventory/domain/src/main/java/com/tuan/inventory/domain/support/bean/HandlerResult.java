package com.tuan.inventory.domain.support.bean;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 
 * @author henry.yu
 *
 * @param <T>
 */
public class HandlerResult<T> extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private T businessResult;
	
	
	public HandlerResult(T businessResult) {
		super();
		this.businessResult = businessResult;
	}
	
	public HandlerResult() {
		super();
	}

	public T getBusinessResult() {
		return businessResult;
	}

	public void setBusinessResult(T businessResult) {
		this.businessResult = businessResult;
	}
	
	
}
