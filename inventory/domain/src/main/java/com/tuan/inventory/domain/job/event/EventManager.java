package com.tuan.inventory.domain.job.event;


/**
 * 
 * @author xianglei
 *
 */
public interface EventManager {
	
	//public void init();
	/**
	 * �첽����¼�
	 * @param event
	 */
	public void addEvent(final Event event);
	
	/**
	 * ͬ������¼�
	 * @param event
	 */
	public void addEventSyn(Event event);

}
