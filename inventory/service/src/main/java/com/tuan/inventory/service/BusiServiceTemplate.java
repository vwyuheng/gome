package com.tuan.inventory.service;

import com.tuan.core.common.service.TuanCallbackResult;

/**
 * 订单中心自定义服务模版接口（订单中心内部使用）
 * @author tianzq
 * @date 2012.11.19
 */
public interface BusiServiceTemplate {
	
	/**
	 * 模板执行
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(OrderServiceCallback action);

	TuanCallbackResult executeMaster(final OrderServiceCallback action);
}
