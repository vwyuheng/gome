package com.tuan.inventory.model.enu;

public enum ResultStatusEnum {
	
	//����״̬
	CONFIRM		("1",	"ȷ�ϣ���Ч�ɴ���CONFIRM��"),
	LOCKED		("3",	"����:��ʼ״̬��locked��"),
	ROLLBACK    ("5",   "�ع�:����滹ԭ"),
	DELETE      ("7", "�������:���ɾ����deleted��"),
	
	
	//LOG		("9",	"��־"),
	
	GOODS_SELF      ("2", "��Ʒ�ܿ��"),
	GOODS_SELECTION      ("4", "ѡ����Ʒ���"),
	GOODS_SUPPLIERS      ("6", "�ֵ���Ʒ���"),
	
	//ҵ������type
	DELETE_INVENTORY   ("11",   "ɾ�����"),
	ADJUST_WATERFLOOD   ("12",   "����עˮ"),
	CALLBACK_CONFIRM   ("13",   "�ص�ȷ��"),
	DEDUCTION_INVENTORY  ("14",   "���ۼ�"),
	ADD_INVENTORY   ("15",   "�������"),
	REFUND_RESTORE_INVENTORY   ("16",   "��Ʒ�˿���"),
	ADJUST_INVENTORY   ("17",   "�ֹ��������"),
	FAULT_COMPENSATION_INVENTORY   ("18",   "�����������");
	
	
	
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
