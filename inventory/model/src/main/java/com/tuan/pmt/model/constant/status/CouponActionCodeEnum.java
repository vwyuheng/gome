package com.tuan.pmt.model.constant.status;

public enum CouponActionCodeEnum {
	
	CREATE	("CREATE",	"创建"),
	BIND	("BIND",	"绑定"),
	FREEZE	("FREEZE",	"冻结"),
	UNFREEZE("UNFREEZE","解冻"),
	USE		("USE",		"使用"),
	CANCEL	("CANCEL",	"作废"),
	REFUNDMENT ("REFUNDMENT","退款");
	
	private String code;
	private String description;
	
	private CouponActionCodeEnum(String code,String description) {
		this.setCode(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
