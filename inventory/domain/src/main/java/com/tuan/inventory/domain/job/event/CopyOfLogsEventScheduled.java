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
 * 调度线程，主要用于日志事件初始化调度
 * @author henry.yu
 * @Date  2014/3/24
 */
public class CopyOfLogsEventScheduled  {
	
	private final static Log logger = LogFactory.getLog(CopyOfLogsEventScheduled.class);
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
	/**
	 * 事件处理manager
	 */
	@Resource
	private EventManager eventManager;
	
	 /**  队列监听等待时间 */
    private long waitTime = 200;

   
    
    /**
     * 构造带不带缓存的客户端
     */
    public CopyOfLogsEventScheduled(){
    	//初始化ScheduledExecutorService 服务
		this.scheduledExecutorService = Executors.newScheduledThreadPool(Runtime
				.getRuntime().availableProcessors() * POOL_SIZE);
    }
	/**
	 * 负责按照一定的频率执行日志的消费事件的封装
	 */
	public void execFixedRate4Logs(){
		new  Thread() {
			public void run() {
				while(true){
					try{
		            	CountDownLatch latch = new CountDownLatch(1);
		                latch.await(waitTime,TimeUnit.MILLISECONDS);
						//Event event=eventQueue.poll();//线程安全
					//	if(event==null){
						//	continue;
						//}
						//EventHandle eventHandle =EventHandleFactory.getInstance().getEventHandle(event.getEventType());
						Future<?>  future = null;
						try {
							System.out.println("execFixedRate4Logs1");
							future = scheduledExecutorService.scheduleAtFixedRate(new ExecuteTask(), 3000, 4000,TimeUnit.SECONDS);
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
							 future.cancel(true);// 中断执行此任务的线程  
						} catch (ExecutionException e) {
							logger.error("ExecuteTask scheduled Execution exception:" ,e);
							 future.cancel(true);// 中断执行此任务的线程  
						}
					}catch(Throwable e){
						  logger.error("scheduled Exception:",e);
					}
				}
			}
		
		}.start();
	 }
	
	class ExecuteTask implements Runnable {
		
	  
		@Override
		public void run() {
			System.out.println("ExecuteTask1");
			
		}
	}
	
	
	/**
	 * 通过队列类型，获取事件类型,充值和支付都算是支付的事件进行处理
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
