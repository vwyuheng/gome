package com.tuan.inventory.model.enu;

public enum ResultStatusEnum {
	
	//����״̬
	ACTIVE		("1",	"��������Ч�ɴ���active��"),
	LOCKED		("3",	"��ʼ״̬��locked��"),
	EXCEPTION    ("5",   "�쳣����"),
	DELETE  ("7", "�������:���ɾ����deleted��"),
	
	
	//LOG		("9",	"��־"),
	
	
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
	
	public static void main(String[] args) {
		System.out.println(ResultStatusEnum.ACTIVE.getCode());
	}
}
