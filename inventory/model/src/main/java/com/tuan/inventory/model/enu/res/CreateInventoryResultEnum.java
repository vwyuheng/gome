package com.tuan.inventory.model.enu.res;

public enum CreateInventoryResultEnum {
	SUCCESS						(0, 	"�ɹ�"),
	INVALID_USER_ID				(-1, 	"�û�id��Ч"),
	FAIL_ADJUST_INVENTORY			(-2, 	"������ʧ��"),
	FAIL_ADJUST_WATERFLOOD			(-3, 	"עˮ����ʧ��"),
	SHORTAGE_STOCK_INVENTORY				(-4, 	"��治��"),
	INVALID_PARAM				(-5, 	"������Ч"),
	INVALID_ORDER_ID			(-6, 	"����ID��Ч"),
	REPEAT_REQUEST				(-7, 	"�ظ�����"),
	/**��Ʒid��Ч*/
	INVALID_GOODSID         (-8, "��Ʒid��Ч"),
	IS_EXISTED				(-9, 	"��Ʒ����Ѵ���"),
	
	DB_ERROR					(-99, 	"���ݿ����"),
	SYS_ERROR					(-100, 	"ϵͳ����"),
	
	INVALID_LOG_PARAM				(1, 	"��Ч����־");
	/**��Ʒ���*//*
	ADD_INVENTORY_GOODS        (1, "��Ʒ�ܿ��"),
	ADD_INVENTORY_SELECTION				(2, 	"������Ʒѡ�Ϳ��"),
	ADD_INVENTORY_SUPPLIERS				(3, 	"��Ʒ�ֵ���");*/
	
	
	
	private int code;
	private String description;

	private CreateInventoryResultEnum(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public static CreateInventoryResultEnum valueOfEnum(int code) {
		switch (code) {
			case 0:
				return SUCCESS;
			case -1:
				return INVALID_USER_ID;
			case -2:
				return FAIL_ADJUST_INVENTORY;
			case -3:
				return FAIL_ADJUST_WATERFLOOD;
			case -4:
				return SHORTAGE_STOCK_INVENTORY;
			case -5:
				return INVALID_PARAM;
			case -6:
				return INVALID_ORDER_ID;
			case -7:
				return REPEAT_REQUEST;
			case -8:
				return INVALID_GOODSID;
			case -9:
				return IS_EXISTED;
			case 1:
				return INVALID_LOG_PARAM;
				/*
			case 2:
				return ADD_INVENTORY_SELECTION;
			case 3:
				return ADD_INVENTORY_SUPPLIERS;*/
				
				
				
				
			case -99:
				return DB_ERROR;
			case -100:
				return SYS_ERROR;
			default:
				return SYS_ERROR;
		}
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
