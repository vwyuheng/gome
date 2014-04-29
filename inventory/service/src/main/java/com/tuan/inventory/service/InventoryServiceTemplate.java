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
	 * 
     * 处理redis连接的,暂未注入数据源
	 * 查询模板执行
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(InventoryQueryServiceCallback action);
	/**
	 * 初始化处理,无事务，只是加载初始化数据用
	 * @param action
	 * @return
	 */
	TuanCallbackResult initQuery(InventoryQueryServiceCallback action);
}
