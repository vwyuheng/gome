package com.tuan.inventory.domain.job.event;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jeehe.common.lang.spring.SpringContextUtil;
import com.tuan.inventory.domain.support.enu.EventType;

/**
 * handle  工厂处理类
 * 
 * @author shaolong zhang
 * @Date  2013-5-10 下午4:06:47
 * 
 */
public class EventHandleFactory {
	
	private final static Log log = LogFactory.getLog(EventHandleFactory.class);

	private static EventHandleFactory INSTANCE = new EventHandleFactory();

	@SuppressWarnings("unchecked")
	private final Map<EventType,EventHandle> handleMap = (HashMap<EventType,EventHandle>)SpringContextUtil.getBean("handleMap");

	private EventHandleFactory(){}
		
	public static EventHandleFactory getInstance(){
		if( INSTANCE == null)
			INSTANCE = new EventHandleFactory();
		return INSTANCE;
	}

	/**
	 * EventHandle 
	 * @param name
	 * @return
	 */
	public EventHandle getEventHandle(EventType type) {
		if (type == null)
			return null;
		EventHandle handler = handleMap.get(type);
		if (null == handler) {
			if(log.isDebugEnabled()) {
			   log.debug(type + " EventHandler is not found from properties");
			}
			return null;
		}
		return handler;
	}
}
