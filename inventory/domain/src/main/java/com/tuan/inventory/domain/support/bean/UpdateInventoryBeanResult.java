package com.tuan.inventory.domain.support.bean;

import java.io.Serializable;

public class UpdateInventoryBeanResult implements Serializable{

	/**
	 * 用于封装匿名内部类获取的业务数据
	 */
	private static final long serialVersionUID = -1307061945260820699L;
	
	/** 事务状态，是否提交完整事务 */
	private boolean callResult;
	private String selectType; // 声明选型商品的属性类别
	private String suppliersType; // 声明分店商品的属性类别
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
