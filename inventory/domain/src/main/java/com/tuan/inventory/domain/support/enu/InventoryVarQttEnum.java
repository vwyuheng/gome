package com.tuan.inventory.domain.support.enu;
/**
 * ���ڶ�����仯�����ݽṹ>key
 * @author henry.yu
 * @date 2014/3/18
 */
public enum InventoryVarQttEnum {
	
	num("��Ʒ�ܿ��仯��"),
	leftNum("��Ʒ�ܿ��ʣ����");
	//selectionNum("ѡ����Ʒ���仯��"),
	//selectionLeftNum("ѡ����Ʒ���ʣ����"),
	//supplierNum("�ֵ���Ʒ���仯��");
	//supplierLeftNum("�ֵ���Ʒ���ʣ����");
	
	private String description;
	
	private InventoryVarQttEnum(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public static void main(String[] args) {
		System.out.println(InventoryVarQttEnum.num);
	}
}

