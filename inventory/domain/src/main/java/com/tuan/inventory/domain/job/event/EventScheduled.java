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
 * 消息调度线程，主要用于消息的定时发送
 * 
 * @author shaolong zhang
 * @Date  2013-5-10 上午10:18:01
 */
public class EventScheduled  {
	
private final static Log logger = LogFactory.getLog(EventScheduled.class);
	
	/**
	 * 调度线程池，用于重发数据
	 */
	private ScheduledExecutorService scheduledExecutorService;
	/**  采用无界队列	 */
	private final static ConcurrentLinkedQueue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();
	/**
	 * 单个cpu的线程数
	 */
	private final int  POOL_SIZE = 5;
	
	 /**  队列监听等待时间 */
    private long waitTime = 200;
    
    public void addEvent(Event event){
		if (event != null) {
			eventQueue.add(event);
		}

    }
    
    /**
     * 构造带不带缓存的客户端
     */
    public EventScheduled(){
    	//初始化ScheduledExecutorService 服务
		scheduledExecutorService = Executors.newScheduledThreadPool(Runtime
				.getRuntime().availableProcessors() * POOL_SIZE);
    }
    
    
    /**
	 * 阻塞监听线程,只负责执行需要调度的消息
	 */
	public void start(){
		new  Thread() {
			public void run() {
				while(true){
					try{
		            	CountDownLatch latch = new CountDownLatch(1);
		                latch.await(waitTime,TimeUnit.MILLISECONDS);
		                //从队列中取事件
						Event event=eventQueue.poll();//线程安全
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
								//没有发送成功等待下次发送
								if(!result.isSuccess()){
					/*				int tryCount = event.getTryCount().intValue();
									event.setWaitTime(QueueRuleUtil.getNextConsumeSecond(tryCount,0));
									addEvent(event);*/
								} else {
									// 发送成功但是可能数据操作没有成功
			/*						String resultMsg = result.getEventResult();
									if(StringUtils.isNotBlank(resultMsg)&&resultMsg.equalsIgnoreCase("failed")){
										event.setEventType(EventType.ERROR);
										addEvent(event);
									}	*/
									
								}
							}
						} catch (InterruptedException e) {
							logger.error("event scheduled Interrupted exception :",e);
							 future.cancel(true);// 中断执行此任务的线程  
						} catch (ExecutionException e) {
							logger.error("event scheduled Execution exception:" ,e);
							 future.cancel(true);// 中断执行此任务的线程  
						}
					}catch(Throwable e){
						  logger.error("scheduled Exception:",e);
					}
				}
			}
		
		}.start();
	 }
	
	
	
}
