package com.tuan.inventory.domain.job.event.manager;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.tuan.inventory.domain.job.event.Event;
import com.tuan.inventory.domain.job.event.EventHandle;
import com.tuan.inventory.domain.job.event.EventHandleFactory;
import com.tuan.inventory.domain.job.event.EventManager;
import com.tuan.inventory.domain.job.event.EventScheduled;
import com.tuan.inventory.domain.job.event.EventWorker;
import com.tuan.inventory.domain.job.event.result.EventResult;
import com.tuan.inventory.domain.support.enu.EventType;
import com.tuan.inventory.domain.support.util.QueueRuleUtil;

/**
 * 事件管理处理
 * @author shaolong zhang
 * @Date  2013-4-24 下午3:19:50
 */
public class EventManagerImpl implements EventManager{
	
	private final static Log logger = LogFactory.getLog(EventManagerImpl.class);
	
	/**  开启队列调度线程 */
	private EventScheduled eventScheduled;
	/**  线程池管理 */
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	/**
	 *  初始化异步调度线程
	 */
	public void init(){
		//构造调度任务
		eventScheduled = new EventScheduled();
		//启动调度线程
		eventScheduled.start();
	}

	/**
	 * 添加新的事件到队列中
	 * @param event
	 */
	@Override
	public void addEvent(final Event event){
		if(!validateEvent(event)){
			if(logger.isDebugEnabled()){
			    logger.debug("addEvent event is illega");
			}
			return;
		}
		eventScheduled.addEvent(event);
	}
	
	@Override
	public void addEventSyn(Event event) {
		if(!validateEvent(event)){
			if(logger.isDebugEnabled()){
			    logger.debug("addEventSyn event is illega");
			}
			return;
		}
		EventHandle eventHandle = EventHandleFactory.getInstance().getEventHandle(event.getEventType());
		Future<EventResult>  future = null;
		try {
			future = threadPoolTaskExecutor.getThreadPoolExecutor().submit(new EventWorker(event,eventHandle));
			if(future != null) {
				EventResult eventResult = future.get();
				if(eventResult == null){
					if(logger.isDebugEnabled()){
						logger.debug("submit eventResult return null");
					}
					return;
				} 
				if(logger.isDebugEnabled()) {
					logger.debug("event submit :" + eventResult.toString());
				}
				//没有发送成功等待下次发送
				if(!eventResult.isSuccess()){
					// 暂时不用线程调度，因为缓存的问题
					int tryCount = event.getTryCount().intValue();
					event.setWaitTime(QueueRuleUtil.getNextConsumeSecond(tryCount,0));
					addEvent(event);
				} else {
					// 发送成功但是可能数据操作没有成功,或者是其他原因,对于这些数据采取线程调度处理，不需要马上处理
					String resultMsg = eventResult.getEventResult();
					if (StringUtils.isNotBlank(resultMsg)
							&& !resultMsg.equalsIgnoreCase(EventType.SUCCESS
									.toString())) {

						if (resultMsg.equalsIgnoreCase(EventType.ERROR
								.toString())) {
							event.setEventType(EventType.ERROR);
							//一分钟后处理
							event.setWaitTime(QueueRuleUtil.getNextConsumeSecond(2,0));
							addEvent(event);
						} /*else if (resultMsg.equalsIgnoreCase(EventType.ALARM
								.toString())) {
							event.setEventType(EventType.ALARM);
							//一分钟后处理
							event.setWaitTime(QueueRuleUtil.getNextConsumeSecond(2,0));
							addEvent(event);
						} else {
                           //TODO 其他的暂时不用太处理
						}*/
					}
				}
			}
		} catch (InterruptedException e) {
			logger.error(this.getClass() + "event submit Interrupted exception :",e);
			future.cancel(true);// 中断执行此任务的线程  
		} catch (ExecutionException e) {
			logger.error(this.getClass() + "event submit Execution exception:" ,e);
			 future.cancel(true);// 中断执行此任务的线程  
		}
	}
	
	/**
	 * 验证事件是否有效
	 * 
	 * @param event
	 * @return boolean
	 */
	private boolean validateEvent(Event event) {
		if (event == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("addEventSyn is must not be null");
			}
			return false;
		}
		if (StringUtils.isBlank(event.getUUID()) || event.getData() == null
				|| event.getEventType() == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("event request params is must not be null");
			}
			return false;
		}
		return true;
	}
		
	public void setThreadPoolTaskExecutor(
			ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}


}
