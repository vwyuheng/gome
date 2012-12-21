package com.tuan.pmt.model.constant.status;

public enum CouponStatusEnum {
	UNBOUND			(0,"UNBOUND",		 "未绑定"),
	UNUSED			(1,"UNUSED",		 "未使用"),
	FREEZED			(2,"FREEZED",		 "已冻结"),
	USED			(3,"USED",			 "已使用"),
	UNBOUND_EXPIRED	(4,"UNBOUND_EXPIRED","未绑定过期"),
	UNUSED_EXPIRED	(5,"UNUSED_EXPIRED", "未使用过期"),
	CANCEL			(6,"CANCEL",		 "已作废"),
	UNKNOWN			(7,"UNKNOWN",		 "未知状态");
	
	private int code;
	private String name;
	private String description;
	
	private CouponStatusEnum(int code,String name,String description){
		this.code = code ;
		this.name = name;
		this.description = description;
	}
	
	public static CouponStatusEnum valueOfEnum(int code) {
		switch (code) {
		case 0:
			return UNBOUND;
		case 1:
			return UNUSED;
		case 2:
			return FREEZED;
		case 3:
			return USED;
		case 4:
			return UNBOUND_EXPIRED;
		case 5:
			return UNUSED_EXPIRED;
		case 6:
			 return CANCEL;
		default:
			return UNKNOWN;
		}
	}
	
	/**
	 * 根据code 直接获取描述信息
	 * @param code
	 * @return
	 */
	public static String value2Description(int code){
		CouponStatusEnum enum1 = valueOfEnum(code);
		return enum1.getDescription();
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
