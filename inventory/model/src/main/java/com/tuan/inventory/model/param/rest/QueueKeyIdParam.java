package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 返回给订单的队列id参数
 * @author henry.yu
 * @date 20140506
 */
public class QueueKeyIdParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private String queueKeyId = "";
	public String getQueueKeyId() {
		return queueKeyId;
	}
	public void setQueueKeyId(String queueKeyId) {
		this.queueKeyId = queueKeyId;
	}

	
}
