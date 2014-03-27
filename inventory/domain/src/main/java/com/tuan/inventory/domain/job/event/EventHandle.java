package com.tuan.inventory.domain.job.event;

import com.tuan.inventory.domain.job.event.result.EventResult;



/**
 *  处理队列中的各种事件
 * @Date  2013-4-23 上午10:43:17
 */
public interface EventHandle {
	
	/**
	 * 处理时间，返回成功与失败
	 * @param event
	 * @return boolean
	 */
	public boolean handleEvent(final Event event) throws Exception;
	//public boolean handleEvent(final Event event) throws Exception;
	
	public static String HANDLE_LOG = "BUSINESS.TASK";

}
