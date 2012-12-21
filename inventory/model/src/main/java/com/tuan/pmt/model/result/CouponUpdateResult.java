package com.tuan.pmt.model.result;

import com.tuan.core.common.lang.TuanBaseDO;

public class CouponUpdateResult extends TuanBaseDO{
	private static final long serialVersionUID = -1361509304255499369L;

	/**
	 * ����У����
	 * �ο����壺com.tuan.pmt.model.constant.res.CouponUpdateResConstant
	 */
	private String result;

	/**ҵ��������*/
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