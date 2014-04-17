package com.tuan.inventory.service;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.inventory.model.param.InventoryScheduledParam;
import com.wowotrace.trace.model.Message;

public interface GoodsInventoryScheduledService {
	/***
	 * 回调确认过的队列的消费接口
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	public void confirmQueueConsume(String clientIp,
			String clientName,Message traceMessage);
	/***
	 * 被锁定队列的消费接口
	 */
	@ProductCode(code = "00002", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	public void lockedQueueConsume(String clientIp,
			String clientName,InventoryScheduledParam param,Message traceMessage);
	/**
	 * 日志队列的消费接口
	 * @param clientIp
	 * @param clientName
	 * @param traceMessage
	 */
	@ProductCode(code = "00003", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	public void logsQueueConsume(String clientIp,
			String clientName,Message traceMessage);
}
