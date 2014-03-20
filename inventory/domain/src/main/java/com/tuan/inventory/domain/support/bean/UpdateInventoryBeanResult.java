package com.tuan.inventory.domain.support.bean;

import java.io.Serializable;

public class UpdateInventoryBeanResult implements Serializable{

	/**
	 * ���ڷ�װ�����ڲ����ȡ��ҵ������
	 */
	private static final long serialVersionUID = -1307061945260820699L;
	
	/** ����״̬���Ƿ��ύ�������� */
	private boolean callResult;
	private String selectType; // ����ѡ����Ʒ���������
	private String suppliersType; // �����ֵ���Ʒ���������
	private String jsonData;
	
	public boolean isCallResult() {
		return callResult;
	}
	public void setCallResult(boolean callResult) {
		this.callResult = callResult;
	}
	public String getSelectType() {
		return selectType;
	}
	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}
	public String getSuppliersType() {
		return suppliersType;
	}
	public void setSuppliersType(String suppliersType) {
		this.suppliersType = suppliersType;
	}
	public String getJsonData() {
		return jsonData;
	}
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
	
	
	
}
