package com.tuan.inventory.domain;

import com.tuan.inventory.domain.support.BaseQueueDomain;
import com.tuan.inventory.model.GoodsInventoryActionModel;

/**
 *  日志队列领域
 * @Date  2014-4-4
 */
public class LogQueueDomain extends BaseQueueDomain {

	public LogQueueDomain() {
		super();
	}

	/**
     * 创建一个新的队列领域 ，根据传递的参数
	 */
	public LogQueueDomain(GoodsInventoryActionModel logModel) {
		super(logModel);
	}

	
	
	
}