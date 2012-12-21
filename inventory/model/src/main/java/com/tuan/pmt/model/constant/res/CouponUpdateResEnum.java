package com.tuan.pmt.model.constant.res;

public enum CouponUpdateResEnum {
	SUCCESS			( 1,	"SUCCESS",			"成功"),
	INVALID_USERID	(-1,	"INVALID_USERID",	"用户id无效"),
	INVALID_COUPONID(-2,	"INVALID_COUPONID",	"代金券id无效"),
	INVALID_CODE	(-3,	"INVALID_CODE",		"代金券验证码无效"),
	INVALID_ORDERID	(-4,	"INVALID_ORDERID",	"无效订单"),
	INVALID_STATUS	(-5,	"INVALID_STATUS",	"代金券状态无效"),
	INVALID_BATCHID	(-6,	"INVALID_BATCHID",	"批次ID无效"),
	INVALID_USER	(-7,	"INVALID_USER",		"非法用户"),
	INVALID_TIME	(-8,	"INVALID_TIME",		"非法有效期"),

	INVALID_ISBIND  (-9,    "INVALID_ISBIND",   "该代金券已被绑定"),
	INVALID_PLATTYPE(-10,   "INVALID_PLATTYPE", "非法的平台类型"),
	INVALID_BATCHLIMIT(-11,   "INVALID_BATCHLIMIT","改批次绑定次数超过最大限制"),

	SAME_STATE		(-12,	"SAME_STATE",		"更新状态相同"),

	INVALID_PARAM	(-98,	"INVALID_PARAM",	"传入参数错误"),
	DB_ERROR		(-99,	"DB_ERROR",			"数据库错误"),
	SYS_ERROR		(-100,	"SYS_ERROR",		"系统错误"),
	UNKNOW			(-101,	"UNKNOW",			"未知");
	
	private int code;
	private String name;
	private String description;
	
	private CouponUpdateResEnum(int code,String name,String description){
		this.code = code ;
		this.name = name;
		this.description = description;
	}
	
	public static CouponUpdateResEnum valueOfEnum(int code) {
		switch (code) {
		case 1:
			return SUCCESS;
		case -1:
			return INVALID_USERID;
		case -2:
			return INVALID_COUPONID;
		case -3:
			return INVALID_CODE;
		case -4:
			return INVALID_ORDERID;
		case -5:
			return INVALID_STATUS;
		case -6:
			return INVALID_BATCHID;
		case -7:
			return INVALID_USER;
		case -8:
			return INVALID_TIME;
		case -9:
            return INVALID_ISBIND;
		case -10:
            return INVALID_PLATTYPE;
		case -11:
            return INVALID_BATCHLIMIT;
		case -12:
            return SAME_STATE;
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
		CouponUpdateResEnum enum1 = valueOfEnum(code);
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
