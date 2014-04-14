package com.tuan.inventory.service;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.inventory.model.param.InventoryScheduledParam;
import com.wowotrace.trace.model.Message;

public interface GoodsInventoryScheduledService {
	/***
	 * �ص�ȷ�Ϲ��Ķ��е����ѽӿ�
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	public void confirmQueueConsume(String clientIp,
			String clientName,Message traceMessage);
	/***
	 * ���������е����ѽӿ�
	 */
	@ProductCode(code = "00002", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	public void lockedQueueConsume(String clientIp,
			String clientName,InventoryScheduledParam param,Message traceMessage);
}
