package com.tuan.inventory.model.enu.res;

public enum InventoryQueryEnum {
	/**成功*/
	SUCCESS(1, "成功"),
	
	/**传入参数错误*/
	INVALID_PARAM(-1, "传入参数错误"),
	/**商品id无效*/
	INVALID_GOODSID(-2, "商品id无效"),
	/**商品id无效*/
	INVALID_WMSGOODSID(1032, "物流编码无效"),
	INVALID_GOODSBASEID        (1043, "商品基本id无效"),
	NO_GOODSBASE        (1044, "商品基本信息不存在！"),
	NO_GOODS       (1046, "商品信息不存在！"),
	/**用户id无效*/
	INVALID_SELECTIONID(-3, "选型id无效"),
	/**无效客户端*/
	INVALID_SUPPLIERSID(-4, "分店id无效"),
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
