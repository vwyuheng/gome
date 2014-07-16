package com.tuan.inventory.client;

import javax.annotation.Resource;

import com.tuan.inventory.service.InventoryServiceTemplate;

public abstract class AbstractInventoryClientService  {
	@Resource
	protected InventoryServiceTemplate inventoryServiceTemplate;

	
	
	public AbstractInventoryClientService() {
		
	}
	
	
}
