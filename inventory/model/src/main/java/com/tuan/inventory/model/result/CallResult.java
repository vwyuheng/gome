package com.tuan.pmt.model.result;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * Service�㷵�ؽ����װ�࣬�����ҵ����󷺻�
 * @author wanghongwei
 *
 * @param <T> ������ҵ�񷵻ض���
 */
public class CallResult<T> extends TuanBaseDO  {

	private static final long serialVersionUID = 1797164626045714280L;

	/** ����״̬���Ƿ��ύ�������� */
	private boolean callResult = true;

    /** ҵ�񷵻ض��� */
    private T businessResult;

    /** ���ӵ�ҵ����� */
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
