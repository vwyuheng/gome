package com.tuan.inventory.model.enu;

public enum ResultStatusEnum {
	UPDATE		("0",	"����"),
	INSERT		("1",	"����"),
	NOEXISTS    ("3",   "������"),
	
	//ҵ������type
	DEDUCTION   ("14",   "���ۼ�"),
	INVENTORYINIT   ("15",   "��ʼ�����"),
	REFUNDRESTORE   ("16",   "��Ʒ�˿���"),
	MANUALADAPT   ("17",   "�ֹ��������"),
	FAULTCOMPENSATION   ("18",   "�����������");
	
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
