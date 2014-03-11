package com.tuan.inventory.service;

import com.tuan.core.common.service.TuanCallbackResult;

/**
 * ���������Զ������ģ��ӿڣ����������ڲ�ʹ�ã�
 * @author tianzq
 * @date 2012.11.19
 */
public interface BusiServiceTemplate {
	
	/**
	 * ģ��ִ��
	 * @param action
	 * @return
	 */
	TuanCallbackResult execute(OrderServiceCallback action);

	TuanCallbackResult executeMaster(final OrderServiceCallback action);
}
