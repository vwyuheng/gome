package com.tuan.inventory.domain.job.event;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.job.event.result.EventResult;

/**
 * ��Ϣ�����̣߳���Ҫ������Ϣ�Ķ�ʱ����
 * 
 * @author shaolong zhang
 * @Date  2013-5-10 ����10:18:01
 */
public class EventScheduled  {
	
private final static Log logger = LogFactory.getLog(EventScheduled.class);
	
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
	
	 /**  ���м����ȴ�ʱ�� */
    private long waitTime = 200;
    
    public void addEvent(Event event){
		if (event != null) {
			eventQueue.add(event);
		}

    }
    
    /**
     * �������������Ŀͻ���
     */
    public EventScheduled(){
    	//��ʼ��ScheduledExecutorService ����
		scheduledExecutorService = Executors.newScheduledThreadPool(Runtime
				.getRuntime().availableProcessors() * POOL_SIZE);
    }
    
    
    /**
	 * ���������߳�,ֻ����ִ����Ҫ���ȵ���Ϣ
	 */
	public void start(){
		new  Thread() {
			public void run() {
				while(true){
					try{
		            	CountDownLatch latch = new CountDownLatch(1);
		                latch.await(waitTime,TimeUnit.MILLISECONDS);
		                //�Ӷ�����ȡ�¼�
						Event event=eventQueue.poll();//�̰߳�ȫ
						if(event==null){
							continue;
						}
						EventHandle eventHandle =EventHandleFactory.getInstance().getEventHandle(event.getEventType());
						Future<EventResult>  future = null;
						try {
							future = scheduledExecutorService.schedule(new EventWorker(event,eventHandle), event.getWaitTime(), TimeUnit.SECONDS);
							if(future != null) {
								EventResult result = future.get();
								if(result == null){
									if(logger.isDebugEnabled()){
										logger.debug("event scheduled return null");
									}
									return;
								} 
								if(logger.isDebugEnabled()) {
									logger.debug("event scheduled :" + result.toString());
								}
								//û�з��ͳɹ��ȴ��´η���
								if(!result.isSuccess()){
					/*				int tryCount = event.getTryCount().intValue();
									event.setWaitTime(QueueRuleUtil.getNextConsumeSecond(tryCount,0));
									addEvent(event);*/
								} else {
									// ���ͳɹ����ǿ������ݲ���û�гɹ�
			/*						String resultMsg = result.getEventResult();
									if(StringUtils.isNotBlank(resultMsg)&&resultMsg.equalsIgnoreCase("failed")){
										event.setEventType(EventType.ERROR);
										addEvent(event);
									}	*/
									
								}
							}
						} catch (InterruptedException e) {
							logger.error("event scheduled Interrupted exception :",e);
							 future.cancel(true);// �ж�ִ�д�������߳�  
						} catch (ExecutionException e) {
							logger.error("event scheduled Execution exception:" ,e);
							 future.cancel(true);// �ж�ִ�д�������߳�  
						}
					}catch(Throwable e){
						  logger.error("scheduled Exception:",e);
					}
				}
			}
		
		}.start();
	 }
	
	
	
}
