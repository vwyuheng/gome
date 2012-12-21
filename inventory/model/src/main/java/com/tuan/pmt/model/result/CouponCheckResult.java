package com.tuan.pmt.model.result;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 代金券校验返回结果类
 * @author duandj
 *
 */
public class CouponCheckResult extends TuanBaseDO{
	private static final long serialVersionUID = -6204253052069266812L;

	/**
	 * 订单校验结果  
	 * 参考：com.tuan.pmt.model.constant.res.CouponCheckResConstant
	 */
	private String result;

	/**业务结果对象*/
	private Object resultObject;

	public CouponCheckResult(String result, Object resultObject) {
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
