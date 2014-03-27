package com.tuan.inventory.model.result;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.model.enu.PublicCodeEnum;

/**
 * ������� 
 * 		�Ƿ�ɹ� --������ҵ����
 *      ϵͳ����ö��
 *      ҵ��������    ���������ҵ��ö��
 *      ҵ�񸽼�����һ�����쳣�������������Ϣ��
 * @param <T extends Serializable > 
 */
public class CallResult<T> extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private boolean isSuccess;    
	private PublicCodeEnum publicCodeEnum;
	private T businessResult;
	private Object businessObject;
	
	
	public CallResult(boolean isSuccess, PublicCodeEnum publicCodeEnum,
			T businessResult,Object businessObject) {
		super();
		this.isSuccess = isSuccess;
		this.publicCodeEnum = publicCodeEnum;
		this.businessResult = businessResult;
		this.businessObject = businessObject;
	}
	
	public CallResult() {
		super();
	}
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public PublicCodeEnum getPublicCodeEnum() {
		return publicCodeEnum;
	}

	public void setPublicCodeEnum(PublicCodeEnum publicCodeEnum) {
		this.publicCodeEnum = publicCodeEnum;
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
