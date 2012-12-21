package com.tuan.pmt.model.result;

import com.tuan.core.common.lang.TuanBaseDO;

public class CouponUpdateResult extends TuanBaseDO{
	private static final long serialVersionUID = -1361509304255499369L;

	/**
	 * 订单校验结果
	 * 参考定义：com.tuan.pmt.model.constant.res.CouponUpdateResConstant
	 */
	private String result;

	/**业务结果对象*/
	private Object resultObject;

	public CouponUpdateResult(String result, Object resultObject) {
		super();
		this.result = result;
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
	public Object getResultObject() {
		return resultObject;
	}
	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}
}