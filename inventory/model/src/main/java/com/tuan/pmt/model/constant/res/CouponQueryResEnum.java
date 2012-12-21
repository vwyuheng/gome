package com.tuan.pmt.model.constant.res;

public enum CouponQueryResEnum {
	SUCCESS			( 1,	"SUCCESS",			"�ɹ�"),
	INVALID_USERID	(-1,	"INVALID_USERID",	"�û�id��Ч"),
	INVALID_COUPON	(-2,	"INVALID_COUPON",	"����ȯid��Ч"),
	INVALID_COUPONCODE  (-3,    "INVALID_COUPONCODE",   "����ȯ��֤����Ч"),
	INVALID_PLATTYPE  (-4,    "INVALID_PLATTYPE",   "ƽ̨������Ч"),
	INVALID_PARAM	(-98,	"INVALID_PARAM",	"�����������"),
	DB_ERROR		(-99,	"DB_ERROR",			"���ݿ����"),
	SYS_ERROR		(-100,	"SYS_ERROR",		"ϵͳ����"),
	UNKNOW			(-101,	"UNKNOW",			"δ֪");
	
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
	 * ����code ֱ�ӻ�ȡ������Ϣ
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
