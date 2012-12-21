package com.tuan.pmt.model.result;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 代金券查询接口返回对象
 * @author duandj
 *
 */
public class CouponQueryResult<T> extends TuanBaseDO{
	private static final long serialVersionUID = -4799444298799962832L;

	/**
	 * 代金券查询结果
	 * 参考定义：com.tuan.pmt.model.constant.res.CouponQueryResConstant	
	 */
	private String result;
	/**业务结果对象*/
	private T resultObject;
	
	public CouponQueryResult(){
	    
	}

	public CouponQueryResult(String result, T resultObject) {
		super();
		this.result = result;
		this.resultObject = resultObject;
	}
	public T getResultObject() {
		return resultObject;
	}
	public void setResultObject(T resultObject) {
		this.resultObject = resultObject;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
