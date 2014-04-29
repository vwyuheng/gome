package com.tuan.inventory.model.enu.res;

public enum CreateInventoryResultEnum {
	SUCCESS						(0, 	"成功"),
	INVALID_USER_ID				(-1, 	"用户id无效"),
	FAIL_ADJUST_INVENTORY			(-2, 	"库存调整失败"),
	FAIL_ADJUST_WATERFLOOD			(-3, 	"注水调整失败"),
	SHORTAGE_STOCK_INVENTORY				(-4, 	"库存不足"),
	INVALID_PARAM				(-5, 	"参数无效"),
	INVALID_ORDER_ID			(-6, 	"订单ID无效"),
	REPEAT_REQUEST				(-7, 	"重复请求"),
	/**商品id无效*/
	INVALID_GOODSID         (-8, "商品id无效"),
	IS_EXISTED				(-9, 	"商品库存已存在"),
	
	DB_ERROR					(-99, 	"数据库错误"),
	SYS_ERROR					(-100, 	"系统错误"),
	INIT_INVENTORY_ERROR					(-10, 	"初始化库存发送错误"),
	INVALID_LOG_PARAM				(1, 	"无效的日志");
	/**商品库存*//*
	ADD_INVENTORY_GOODS        (1, "商品总库存"),
	ADD_INVENTORY_SELECTION				(2, 	"新增商品选型库存"),
	ADD_INVENTORY_SUPPLIERS				(3, 	"商品分店库存");*/
	
	
	
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
			case -10:
				return INIT_INVENTORY_ERROR;
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
