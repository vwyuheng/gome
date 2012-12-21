package com.tuan.pmt.model.constant.res;

public enum CouponCheckResEnum {
	SUCCESS			( 1,	"SUCCESS",			"�ɹ�"),
	INVALID_USERID	(-1,	"INVALID_USERID",	"�û�id��Ч"),
	INVALID_COUPONID(-2,	"INVALID_COUPONID",	"����ȯid��Ч"),
	INVALID_CODE	(-3,	"INVALID_CODE",		"����ȯ��֤����Ч"),
	INVALID_ORDERID	(-4,	"INVALID_ORDERID",	"��Ч����"),
	INVALID_STATUS	(-5,	"INVALID_STATUS",	"����ȯ״̬��Ч"),
	INVALID_BATCHID	(-6,	"INVALID_BATCHID",	"����ID��Ч"),
	INVALID_USER	(-7,	"INVALID_USER",		"�Ƿ��û�"),
	INVALID_TIME	(-8,	"INVALID_TIME",		"�Ƿ���Ч��"),
	INVALID_GOODSPARAM	(-9,	"INVALID_GOODID",		"��Ʒ������Ч"),
	INVALID_ORDERFROM	(-10,	"INVALID_ORDERFROM",	"������Դ��Ч"),
	INVALID_GOODSPRICE	(-11,	"INVALID_GOODSPRICE",	"��Ʒ�۸���Ч"),
	INVALID_GOODSAMOUNT	(-12,	"INVALID_GOODSAMOUNT",	"��Ʒ���Ͻ����Ч"),
	INVALID_GOODSID	(-13,	"INVALID_GOODSID",	"��ƷID��Ч"),
	GOODSID_NOT_USED(-14,	"GOODSID_NOT_USED",	"�������ڴ���Ʒ��"),
	INVALID_CITY	(-15,	"INVALID_CITY",		"�Ƿ�����"),
	INVALID_CAT		(-16,	"INVALID_CAT",		"�Ƿ�����"),
	INVALID_PARAM	(-98,	"INVALID_PARAM",	"�����������"),
	DB_ERROR		(-99,	"DB_ERROR",			"���ݿ����"),
	SYS_ERROR		(-100,	"SYS_ERROR",		"ϵͳ����"),
	UNKNOW			(-101,	"UNKNOW",			"δ֪");
	
	private int code;
	private String name;
	private String description;
	
	private CouponCheckResEnum(int code,String name,String description){
		this.code = code ;
		this.name = name;
		this.description = description;
	}
	
	public static CouponCheckResEnum valueOfEnum(int code) {
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
			return INVALID_GOODSPARAM;
		case -10:
			return INVALID_ORDERFROM;
		case -11:
			return INVALID_GOODSPRICE;
		case -12:
			return INVALID_GOODSAMOUNT;
		case -13:
			return INVALID_GOODSID;
		case -14:
			return GOODSID_NOT_USED;
		case -15:
			return INVALID_CITY;
		case -16:
			return INVALID_CAT;
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
		CouponCheckResEnum enum1 = valueOfEnum(code);
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
