package com.tuan.inventory.model.enu;

public enum ResultStatusEnum {
	UPDATE		("0",	"更新"),
	INSERT		("1",	"新增"),
	NOEXISTS    ("3",   "不存在"),
	
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
}
