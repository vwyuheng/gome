package com.tuan.inventory.model.result;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 业务返回对象
 * @author henry.yu
 * @param <T>
 *
 * @param <T>
 */
public class InventoryCallResult extends TuanBaseDO{
	private static final long serialVersionUID = 8136212258133597383L;
	
	/** 返回码 **/
	public int code;
	/** 返回描述（英文） **/
	public String description;
	/** 返回对象 **/
	public Object businessResult;
	
	public InventoryCallResult(){
		super();
	}
	
	public InventoryCallResult(int code,String description,Object businessResult){
		super();
		this.code = code;
		this.description = description;
		this.businessResult = businessResult;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Object getBusinessResult() {
		return businessResult;
	}
	public void setBusinessResult(Object businessResult) {
		this.businessResult = businessResult;
	}
	
	
}
