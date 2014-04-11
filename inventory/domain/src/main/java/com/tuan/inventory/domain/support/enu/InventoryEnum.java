package com.tuan.inventory.domain.support.enu;

public enum InventoryEnum {
	
	HASHCACHE("数据结构为hash类型"),SETCACHE("数据结构为set类型");
	
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

