package com.tuan.inventory.domain.job.event;


/**
 * 
 * @author xianglei
 *
 */
public interface EventManager {
	
	//public void init();
	/**
	 * 异步添加事件
	 * @param event
	 */
	public void addEvent(final Event event);
	
	/**
	 * 同步添加事件
	 * @param event
	 */
	public void addEventSyn(Event event);

}
