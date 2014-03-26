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
 * �¼�������
 * @author shaolong zhang
 * @Date  2013-4-24 ����3:19:50
 */
public class EventManagerImpl implements EventManager{
	
	private final static Log logger = LogFactory.getLog(EventManagerImpl.class);
	
	/**  �������е����߳� */
	private EventScheduled eventScheduled;
	/**  �̳߳ع��� */
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	/**
	 *  ��ʼ���첽�����߳�
	 */
	public void init(){
		//�����������
		eventScheduled = new EventScheduled();
		//���������߳�
		eventScheduled.start();
	}

	/**
	 * ����µ��¼���������
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
				//û�з��ͳɹ��ȴ��´η���
				if(!eventResult.isSuccess()){
					// ��ʱ�����̵߳��ȣ���Ϊ���������
					int tryCount = event.getTryCount().intValue();
					event.setWaitTime(QueueRuleUtil.getNextConsumeSecond(tryCount,0));
					addEvent(event);
				} else {
					// ���ͳɹ����ǿ������ݲ���û�гɹ�,����������ԭ��,������Щ���ݲ�ȡ�̵߳��ȴ�������Ҫ���ϴ���
					String resultMsg = eventResult.getEventResult();
					if (StringUtils.isNotBlank(resultMsg)
							&& !resultMsg.equalsIgnoreCase(EventType.SUCCESS
									.toString())) {

						if (resultMsg.equalsIgnoreCase(EventType.ERROR
								.toString())) {
							event.setEventType(EventType.ERROR);
							//һ���Ӻ���
							event.setWaitTime(QueueRuleUtil.getNextConsumeSecond(2,0));
							addEvent(event);
						} /*else if (resultMsg.equalsIgnoreCase(EventType.ALARM
								.toString())) {
							event.setEventType(EventType.ALARM);
							//һ���Ӻ���
							event.setWaitTime(QueueRuleUtil.getNextConsumeSecond(2,0));
							addEvent(event);
						} else {
                           //TODO ��������ʱ����̫����
						}*/
					}
				}
			}
		} catch (InterruptedException e) {
			logger.error(this.getClass() + "event submit Interrupted exception :",e);
			future.cancel(true);// �ж�ִ�д�������߳�  
		} catch (ExecutionException e) {
			logger.error(this.getClass() + "event submit Execution exception:" ,e);
			 future.cancel(true);// �ж�ִ�д�������߳�  
		}
	}
	
	/**
	 * ��֤�¼��Ƿ���Ч
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
