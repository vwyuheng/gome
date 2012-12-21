package com.tuan.pmt.model.constant.status;

public enum CouponBatchStatusEnum {
	SAVED		(0,"�ѱ���"),
	CREATING	(1,"������"),
	CREATED		(2,"������"),
	CANCEL		(3,"������");
	
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
