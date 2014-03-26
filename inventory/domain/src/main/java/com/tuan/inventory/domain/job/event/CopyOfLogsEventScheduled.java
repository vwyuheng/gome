package com.tuan.inventory.domain.job.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 调度线程，主要用于日志事件初始化调度
 * @author henry.yu
 * @Date  2014/3/24
 */
public class CopyOfLogsEventScheduled  {
	
	private final static Log logger = LogFactory.getLog(CopyOfLogsEventScheduled.class);
	/**
	 * 调度线程池，用于初始化日志事件数据
	 */
	private ScheduledExecutorService scheduledExecutorService;
	/**
	 * 单个cpu的线程数
	 */
	private final int  POOL_SIZE = 5;
	
	 /**  队列监听等待时间 */
    private long waitTime = 200;
    //默认初始化延时
    private static final long DEFAULTINITIALDELAY = 3*1000;
    //默认两次开始执行最小间隔时间
    private static final long DEFAULTDELAY = 4*1000;
    //支持配置
    private   long initialDelay = 0;
    private   long delay = 0;
   
  
    
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
				//while(true){
					try{
		            	CountDownLatch latch = new CountDownLatch(1);
		                latch.await(waitTime,TimeUnit.MILLISECONDS);
						Future<?>  future = null;
						try {
							System.out.println("execFixedRate4Logs1");
							future = scheduledExecutorService.scheduleAtFixedRate(new LogQueueConsumeTask(), (getInitialDelay()==0?DEFAULTINITIALDELAY:getInitialDelay()), (getDelay()==0?DEFAULTDELAY:getDelay()),TimeUnit.MILLISECONDS);
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
				//}
			}
		
		}.start();
	 }
	public long getInitialDelay() {
		return initialDelay;
	}
	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}
	public long getDelay() {
		return delay;
	}
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	class LogQueueConsumeTask implements Runnable {


		
		public void run() {
			System.out.println("test");
		}
		
		
		
	}
	
}
