package com.tuan.inventory.domain.job.event;

import com.tuan.inventory.domain.job.event.result.EventResult;



/**
 *  ��������еĸ����¼�
 * @Date  2013-4-23 ����10:43:17
 */
public interface EventHandle {
	
	/**
	 * ����ʱ�䣬���سɹ���ʧ��
	 * @param event
	 * @return boolean
	 */
	public boolean handleEvent(final Event event) throws Exception;
	//public boolean handleEvent(final Event event) throws Exception;
	
	public static String HANDLE_LOG = "BUSINESS.TASK";

}
