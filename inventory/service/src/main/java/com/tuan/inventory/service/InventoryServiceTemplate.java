package com.tuan.inventory.service;

import com.tuan.core.common.service.TuanCallbackResult;

public interface InventoryServiceTemplate {
	/**
	 * 更新写入模板执行
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(InventoryUpdateServiceCallback action);
	/**
	 * 查询模板执行
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(InventoryQueryServiceCallback action);
}
