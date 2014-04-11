package com.tuan.inventory.job.event;

/**
 * @author henry.yu
 *  处理队列中的各种事件
 * @Date  2014-3-27
 */
public interface EventHandle {
	
	/**
	 * 处理时间，返回成功与失败
	 * @param event
	 * @return boolean
	 */
	public boolean handleEvent(final Event event) throws Exception;
	
	public static String HANDLE_LOG = "BUSINESS.TASK";

}
