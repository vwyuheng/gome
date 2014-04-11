package com.tuan.inventory.job.event;

/**
 * @author henry.yu
 *  ��������еĸ����¼�
 * @Date  2014-3-27
 */
public interface EventHandle {
	
	/**
	 * ����ʱ�䣬���سɹ���ʧ��
	 * @param event
	 * @return boolean
	 */
	public boolean handleEvent(final Event event) throws Exception;
	
	public static String HANDLE_LOG = "BUSINESS.TASK";

}
