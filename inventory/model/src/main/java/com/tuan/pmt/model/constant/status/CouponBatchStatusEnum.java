package com.tuan.pmt.model.constant.status;

public enum CouponBatchStatusEnum {
	SAVED		(0,"已保存"),
	CREATING	(1,"生成中"),
	CREATED		(2,"已生成"),
	CANCEL		(3,"已作废");
	
	private int code;
	private String description;
	
	private CouponBatchStatusEnum(int code,String description) {
		this.setCode(code);
		this.setCode(code);
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
}
