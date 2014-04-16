package com.tuan.inventory.model.enu;

public enum InterfaceActionType {
	CONSUMER		(0, 	"消费"),
	CORRECT			(1, 	"冲正"),
	REVOKE			(2, 	"撤销"),
	REFUND			(3, 	"退款"),
	VERIFY			(4, 	"卡及门店验证"),
	CARD_SIGN		(5, 	"卡签名"),
	CARD_REGIST		(6, 	"卡注册"),
	CARD_CANCLE		(7, 	"卡注销"),
	STORE_REGIST	(8, 	"门店注册"),
	STORE_CANCLE	(9, 	"门店注销"),
	STORE_QUERY		(10, 	"门店查询"),
	
	UNKNOWN			(99, 	"未知");
	
	private int 	code;
	private String 	description;
	
	private InterfaceActionType(int code, String description) {
		this.code = code;
		this.description = description;
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
