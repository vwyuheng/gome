package com.tuan.inventory.domain;

import com.tuan.inventory.domain.support.BaseQueueDomain;
import com.tuan.inventory.model.GoodsInventoryActionModel;

/**
 *  ��־��������
 * @Date  2014-4-4
 */
public class LogQueueDomain extends BaseQueueDomain {

	public LogQueueDomain() {
		super();
	}

	/**
     * ����һ���µĶ������� �����ݴ��ݵĲ���
	 */
	public LogQueueDomain(GoodsInventoryActionModel logModel) {
		super(logModel);
	}

	
	
	
}