package com.tuan.inventory.model.enu.res;

public enum InventoryQueryEnum {
	/**成功*/
	SUCCESS(1, "成功"),
	/**商品id无效*/
	INVALID_GOODSID(-5, "商品id无效"),
	/**用户id无效*/
	INVALID_SELECTIONID(-3, "选型id无效"),
	/**无效客户端*/
	INVALID_SUPPLIERSID(-6, "分店id无效"),
	/**传入参数错误*/
	INVALID_PARAM(-7, "传入参数错误"),
	/**数据库错误*/
	DB_ERROR(-99, "数据库错误"),
	/**系统错误*/
	SYS_ERROR(-100, "系统错误");
	
	private int code;
	private String description;

	private InventoryQueryEnum(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isSuccess() {
		return (code == 1) ? true : false;
	}
}
