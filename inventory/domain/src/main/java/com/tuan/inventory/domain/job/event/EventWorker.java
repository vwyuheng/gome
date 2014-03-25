package com.tuan.inventory.domain.job.event;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.job.event.result.EventResult;

/**
 * ����ִ���¼�����
 * @author shaolong zhang
 * @Date  2013-5-10 ����12:25:54
 */
public class EventWorker implements Callable<EventResult> {
	
	private final static Log logger = LogFactory.getLog(EventScheduled.class);
	
	/**
	 * ����ʱ���handle
	 */
	private EventHandle eventHandle;
	
	/**
	 * ��Ҫ������¼����� 
	 */
	private Event event;
	
	
	public EventWorker(final Event event, final EventHandle eventHandle) {
		this.event = event;
		this.eventHandle = eventHandle;
	}
	
	@Override
	public EventResult call() throws Exception {
		if(eventHandle == null) {
			if(logger.isDebugEnabled()) {
			    logger.debug("eventHandle can not be null");
			}
			return null;
		}
		if(event == null) {
			if(logger.isDebugEnabled()) {
			    logger.debug("event can not be null");
			}
			return null;
		}
		EventResult eventResult  = null;
		try {
		    eventResult = eventHandle.handleEvent(event);//�����¼�
			if(logger.isDebugEnabled()) {
			    logger.debug(this.getClass().getName()+"_eventWork:" + event.eventString());
			}
		} catch (Exception e) {
			logger.error(this.getClass() + "handle event error",e);
		}
		return eventResult;
	}

}
