package com.tuan.inventory.model.enu.res;

public enum InventoryQueryEnum {
	/**�ɹ�*/
	SUCCESS(1, "�ɹ�"),
	
	/**�����������*/
	INVALID_PARAM(-1, "�����������"),
	/**��Ʒid��Ч*/
	INVALID_GOODSID(-2, "��Ʒid��Ч"),
	/**�û�id��Ч*/
	INVALID_SELECTIONID(-3, "ѡ��id��Ч"),
	/**��Ч�ͻ���*/
	INVALID_SUPPLIERSID(-4, "�ֵ�id��Ч"),
	/**���ݿ����*/
	DB_ERROR(-99, "���ݿ����"),
	/**ϵͳ����*/
	SYS_ERROR(-100, "ϵͳ����");
	
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
