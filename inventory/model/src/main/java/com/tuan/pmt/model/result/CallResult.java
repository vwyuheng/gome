package com.tuan.pmt.model.result;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * Service层返回结果封装类，将结果业务对象泛化
 * @author wanghongwei
 *
 * @param <T> 泛化的业务返回对象
 */
public class CallResult<T> extends TuanBaseDO  {

	private static final long serialVersionUID = 1797164626045714280L;

	/** 事务状态，是否提交完整事务 */
	private boolean callResult = true;

    /** 业务返回对象 */
    private T businessResult;

    /** 附加的业务对象 */
    private Object businessObject;

	public CallResult(boolean callResult, T businessResult,
			Object businessObject) {
		super();
		this.callResult = callResult;
		this.businessResult = businessResult;
		this.businessObject = businessObject;
	}
	
	public CallResult() {
		super();
	}

	public boolean getCallResult() {
		return callResult;
	}

	public void setCallResult(boolean callResult) {
		this.callResult = callResult;
	}

	public T getBusinessResult() {
		return businessResult;
	}

	public void setBusinessResult(T businessResult) {
		this.businessResult = businessResult;
	}

	public Object getBusinessObject() {
		return businessObject;
	}

	public void setBusinessObject(Object businessObject) {
		this.businessObject = businessObject;
	}
}
