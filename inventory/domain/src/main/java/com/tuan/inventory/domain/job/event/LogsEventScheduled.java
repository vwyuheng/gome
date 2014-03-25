package com.tuan.inventory.domain.job.event;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.support.enu.EventType;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;

/**
 * �����̣߳���Ҫ������־�¼���ʼ������
 * @author henry.yu
 * @Date  2014/3/24
 */
public class LogsEventScheduled  {
	
	private final static Log logger = LogFactory.getLog(LogsEventScheduled.class);
	/**
	 * �����̳߳أ������ط�����
	 */
	private ScheduledExecutorService scheduledExecutorService;
	/**  �����޽����	 */
	private final static ConcurrentLinkedQueue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();
	
	/**
	 * ����cpu���߳���
	 */
	private final int  POOL_SIZE = 5;
	
	@Resource
	private InventoryProviderReadService inventoryProviderReadService;
	/**
	 * �¼�����manager
	 */
	@Resource
	private EventManager eventManager;
	
	 /**  ���м����ȴ�ʱ�� */
    private long waitTime = 200;
    private volatile long lastStartTime = System.currentTimeMillis();
    public void addEvent(Event event){
		if (event != null) {
			eventQueue.add(event);
		}

    }
    
    /**
     * �������������Ŀͻ���
     */
    public LogsEventScheduled(){
    	//��ʼ��ScheduledExecutorService ����
		this.scheduledExecutorService = Executors.newScheduledThreadPool(Runtime
				.getRuntime().availableProcessors() * POOL_SIZE);
    }
	/**
	 * ������һ����Ƶ��ִ����־�������¼��ķ�װ
	 */
	public void execFixedRate4Logs(){
		new  Thread() {
			public void run() {
				while(true){
					try{
		            	CountDownLatch latch = new CountDownLatch(1);
		                latch.await(waitTime,TimeUnit.MILLISECONDS);
						//Event event=eventQueue.poll();//�̰߳�ȫ
					//	if(event==null){
						//	continue;
						//}
						//EventHandle eventHandle =EventHandleFactory.getInstance().getEventHandle(event.getEventType());
						Future<?>  future = null;
						try {
							System.out.println("execFixedRate4Logs1");
							future = scheduledExecutorService.scheduleAtFixedRate(new ExecuteTask(inventoryProviderReadService,getEventType(ResultStatusEnum.LOG.getCode())), 3000, 4000,TimeUnit.SECONDS);
							System.out.println("execFixedRate4Logs2");
							if(future != null) {
								Object result = future.get();
								if(result == null){
									if(logger.isDebugEnabled()){
										logger.debug("ExecuteTask scheduled return null");
									}
									return;
								} 
								if(logger.isDebugEnabled()) {
									logger.debug("ExecuteTask scheduled :" + result.toString());
								}
								System.out.println("execFixedRate4Logs3");
							}
						} catch (InterruptedException e) {
							logger.error("ExecuteTask scheduled Interrupted exception :",e);
							 future.cancel(true);// �ж�ִ�д�������߳�  
						} catch (ExecutionException e) {
							logger.error("ExecuteTask scheduled Execution exception:" ,e);
							 future.cancel(true);// �ж�ִ�д�������߳�  
						}
					}catch(Throwable e){
						  logger.error("scheduled Exception:",e);
					}
				}
			}
		
		}.start();
	 }
	
	class ExecuteTask implements Runnable {
		
	   InventoryProviderReadService client;
		
	   EventType eventType;

		public InventoryProviderReadService getClient() {
			return client;
		}

		public void setClient(InventoryProviderReadService client) {
			this.client = client;
		}

		public EventType getEventType() {
			return eventType;
		}
		public ExecuteTask(InventoryProviderReadService client, EventType eventType) {
			this.client = client;
			this.eventType = eventType;
			
		}
		public void setEventType(EventType eventType) {
			this.eventType = eventType;
		}

		@Override
		public void run() {
			System.out.println("ExecuteTask1");
			JSONObject  logJSON = new JSONObject();
			long startTime = System.currentTimeMillis();
			logJSON.put("QueueConsumeTask.run startTime",DataUtil.formatDate(new Date(startTime)));
			//ˢ����һ�λ�Ծʱ��
			lastStartTime = startTime;
			List<RedisInventoryLogDO> queueLogList = null;
			try {
				queueLogList = this.client.getInventoryLogsQueue("", 30);
			} catch (Exception e) {
				logger.error("LogQueueConsumeTask.run error", e);
			}
				
			//logJSON.put("count",count);
			//��������
			if (!CollectionUtils.isEmpty(queueLogList)) {
				Event event = null;
				AtomicInteger  realCount = new AtomicInteger();
				for (RedisInventoryLogDO model : queueLogList) {
					//if (validateQueue(model)) {
						event = new Event();
						event.setData(model);
						// ���͵Ĳ������½��з���
						event.setTryCount(0);
						event.setEventType(eventType);
						event.setUUID(String.valueOf(model.getId()));
						eventManager.addEventSyn(event);
						realCount.incrementAndGet();
					//}
				}
				logJSON.put("realcount",realCount.get());
			}
			long endTime = System.currentTimeMillis();
			logJSON.put("costTime",endTime-startTime);
			if(logger.isDebugEnabled()){
				logger.debug(logJSON.toString());
			}	
		}
	}
	
	
	/**
	 * ͨ���������ͣ���ȡ�¼�����,��ֵ��֧��������֧�����¼����д���
	 * 
	 * @param queueTypeEnum
	 * @return EventType
	 */
	public EventType  getEventType(final String status){
		if(StringUtils.isEmpty(status)){
			return null;
		}
		if(status.equals(ResultStatusEnum.LOCKED.getCode())){
			return EventType.ABNORMAL;
		} else if(status.equals(ResultStatusEnum.ACTIVE.getCode())){
			return EventType.NORMAL;
		}else if(status.equals(ResultStatusEnum.LOG.getCode())){
			return EventType.LOG;
		}else {
			return null;
		}
	}
	
	
}
