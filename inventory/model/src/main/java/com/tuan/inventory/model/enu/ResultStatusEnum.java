package com.tuan.inventory.model.enu;

public enum ResultStatusEnum {
	
	//队列状态
	ACTIVE		("1",	"正常：有效可处理（active）"),
	LOCKED		("3",	"初始状态（locked）"),
	EXCEPTION    ("5",   "异常队列"),
	DELETE  ("7", "处理完毕:标记删除（deleted）"),
	
	
	//LOG		("9",	"日志"),
	
	
	//业务类型type
	DEDUCTION   ("14",   "库存扣减"),
	INVENTORYINIT   ("15",   "初始化库存"),
	REFUNDRESTORE   ("16",   "商品退款还库存"),
	MANUALADAPT   ("17",   "手工调整库存"),
	FAULTCOMPENSATION   ("18",   "出错补偿还库存");
	
	
	
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
		System.out.println(ResultStatusEnum.ACTIVE.getCode());
	}
}
