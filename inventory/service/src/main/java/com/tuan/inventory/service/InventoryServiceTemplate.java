package com.tuan.inventory.service;

import com.tuan.core.common.service.TuanCallbackResult;

public interface InventoryServiceTemplate {
	/**
	 * ģ��ִ��
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(InventoryServiceCallback action);
}
