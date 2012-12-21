package com.tuan.pmt.model.result;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * ����ȯ��ѯ�ӿڷ��ض���
 * @author duandj
 *
 */
public class CouponQueryResult<T> extends TuanBaseDO{
	private static final long serialVersionUID = -4799444298799962832L;

	/**
	 * ����ȯ��ѯ���
	 * �ο����壺com.tuan.pmt.model.constant.res.CouponQueryResConstant	
	 */
	private String result;
	/**ҵ��������*/
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
