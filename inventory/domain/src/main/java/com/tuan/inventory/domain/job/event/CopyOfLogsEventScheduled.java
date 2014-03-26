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
 * �����̣߳���Ҫ������־�¼���ʼ������
 * @author henry.yu
 * @Date  2014/3/24
 */
public class CopyOfLogsEventScheduled  {
	
	private final static Log logger = LogFactory.getLog(CopyOfLogsEventScheduled.class);
	/**
	 * �����̳߳أ����ڳ�ʼ����־�¼�����
	 */
	private ScheduledExecutorService scheduledExecutorService;
	/**
	 * ����cpu���߳���
	 */
	private final int  POOL_SIZE = 5;
	
	 /**  ���м����ȴ�ʱ�� */
    private long waitTime = 200;
    //Ĭ�ϳ�ʼ����ʱ
    private static final long DEFAULTINITIALDELAY = 3*1000;
    //Ĭ�����ο�ʼִ����С���ʱ��
    private static final long DEFAULTDELAY = 4*1000;
    //֧������
    private   long initialDelay = 0;
    private   long delay = 0;
   
  
    
    /**
     * �������������Ŀͻ���
     */
    public CopyOfLogsEventScheduled(){
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
							 future.cancel(true);// �ж�ִ�д�������߳�  
						} catch (ExecutionException e) {
							logger.error("ExecuteTask scheduled Execution exception:" ,e);
							 future.cancel(true);// �ж�ִ�д�������߳�  
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
