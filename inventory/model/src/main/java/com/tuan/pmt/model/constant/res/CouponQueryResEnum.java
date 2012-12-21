package com.tuan.pmt.model.constant.res;

public enum CouponQueryResEnum {
	SUCCESS			( 1,	"SUCCESS",			"成功"),
	INVALID_USERID	(-1,	"INVALID_USERID",	"用户id无效"),
	INVALID_COUPON	(-2,	"INVALID_COUPON",	"代金券id无效"),
	INVALID_COUPONCODE  (-3,    "INVALID_COUPONCODE",   "代金券验证码无效"),
	INVALID_PLATTYPE  (-4,    "INVALID_PLATTYPE",   "平台类型无效"),
	INVALID_PARAM	(-98,	"INVALID_PARAM",	"传入参数错误"),
	DB_ERROR		(-99,	"DB_ERROR",			"数据库错误"),
	SYS_ERROR		(-100,	"SYS_ERROR",		"系统错误"),
	UNKNOW			(-101,	"UNKNOW",			"未知");
	
	private int code;
	private String name;
	private String description;
	
	private CouponQueryResEnum(int code,String name,String description){
		this.code = code ;
		this.name = name;
		this.description = description;
	}
	
	public static CouponQueryResEnum valueOfEnum(int code) {
		switch (code) {
		case 1:
			return SUCCESS;
		case -1:
			return INVALID_USERID;
		case -2:
			return INVALID_COUPON;
		case -3:
            return INVALID_COUPONCODE;
		case -4:
            return INVALID_PLATTYPE;
		case -98:
			return INVALID_PARAM;
		case -99:
			return DB_ERROR;
		case -100:
			return SYS_ERROR;
		default:
			return UNKNOW;
		}
	}
	
	/**
	 * 根据code 直接获取描述信息
	 * @param code
	 * @return
	 */
	public static String value2Description(int code){
		CouponQueryResEnum enum1 = valueOfEnum(code);
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
