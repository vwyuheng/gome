package com.tuan.inventory.domain.support.enu;

public enum InventoryEnum {
	
	HASHCACHE("���ݽṹΪhash����"),SETCACHE("���ݽṹΪset����");
	
	private String description;

	private InventoryEnum(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}

