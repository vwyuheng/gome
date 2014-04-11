package com.tuan.inventory.service;

import com.tuan.core.common.service.TuanCallbackResult;

public interface InventoryServiceTemplate {
	/**
	 * ����д��ģ��ִ��
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(InventoryUpdateServiceCallback action);
	/**
	 * ��ѯģ��ִ��
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(InventoryQueryServiceCallback action);
}
