package com.tuan.inventory.model.enu;

public enum ResultStatusEnum {
	
	//队列状态
	CONFIRM		("1",	"确认：有效可处理（CONFIRM）"),
	LOCKED		("3",	"锁定:初始状态（locked）"),
	ROLLBACK    ("5",   "回滚:将库存还原"),
	DELETE      ("7", "处理完毕:标记删除（deleted）"),
	
	
	//LOG		("9",	"日志"),
	
	GOODS_SELF      ("2", "商品总库存"),
	GOODS_SELECTION      ("4", "选型商品库存"),
	GOODS_SUPPLIERS      ("6", "分店商品库存"),
	
	//业务类型type
	DELETE_INVENTORY   ("11",   "删除库存"),
	ADJUST_WATERFLOOD   ("12",   "调整注水"),
	CALLBACK_CONFIRM   ("13",   "回调确认"),
	DEDUCTION_INVENTORY  ("14",   "库存扣减"),
	ADD_INVENTORY   ("15",   "新增库存"),
	REFUND_RESTORE_INVENTORY   ("16",   "商品退款还库存"),
	ADJUST_INVENTORY   ("17",   "手工调整库存"),
	FAULT_COMPENSATION_INVENTORY   ("18",   "出错补偿还库存");
	
	
	
	private String code;
	private String description;

	
	private ResultStatusEnum(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static void main(String[] args) {
		
	}
}
