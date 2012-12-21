package com.tuan.pmt.model.constant.status;

public enum CouponActionCodeEnum {
	
	CREATE	("CREATE",	"����"),
	BIND	("BIND",	"��"),
	FREEZE	("FREEZE",	"����"),
	UNFREEZE("UNFREEZE","�ⶳ"),
	USE		("USE",		"ʹ��"),
	CANCEL	("CANCEL",	"����"),
	REFUNDMENT ("REFUNDMENT","�˿�");
	
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
