package com.tuan.inventory.job.scheduled;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.job.event.Event;
import com.tuan.inventory.job.event.EventHandle;
import com.tuan.inventory.model.GoodsInventoryActionModel;

/**
 * 调度线程，主要用于日志事件初始化调度
 * 
 * @author henry.yu
 * @Date 2014/3/24
 */
public class LogsEventScheduled extends AbstractEventScheduled {

	private final static Log logger = LogFactory
			.getLog(LogsEventScheduled.class);
	
	// 默认初始化延时 单位：毫秒
	private static final long DEFAULTINITIALDELAY = 3 * 1000;
	// 默认两次开始执行最小间隔时间 单位：毫秒
	private static final long DEFAULTDELAY = 4 * 1000;
	// 支持配置
	private long initialDelay = 0;
	private long delay = 0;

	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	@Resource
	private EventHandle logsEventHandle;
	
	
	/**
	 * 构造带不带缓存的客户端
	 */
	public LogsEventScheduled() {
		super();
	}

	/**
	 * 负责按照一定的频率执行日志的消费事件的封装
	 */
	public void execFixedRate4Logs() {
		new Thread() {
			public void run() {
				try {
					CountDownLatch latch = new CountDownLatch(1);
					latch.await(waitTime, TimeUnit.MILLISECONDS);
					Future<?> future = null;
					try {
//						System.out.println("execFixedRate4Logs1");
						future = scheduledExecutorService.scheduleAtFixedRate(
								new LogQueueConsumeTask(),
								(getInitialDelay() == 0 ? DEFAULTINITIALDELAY
										: getInitialDelay()),
								(getDelay() == 0 ? DEFAULTDELAY : getDelay()),
								TimeUnit.MILLISECONDS);
//						System.out.println("execFixedRate4Logs2");
						if (future != null) {
							Object result = future.get();
							if (result == null) {
								if (logger.isDebugEnabled()) {
									logger.debug("LogsEventScheduled scheduled return null");
								}
								return;
							}
							if (logger.isDebugEnabled()) {
								logger.debug("LogsEventScheduled scheduled :"
										+ result.toString());
							}
							
						}
					} catch (InterruptedException e) {
						logger.error(
								"LogsEventScheduled scheduled Interrupted exception :",
								e);
						future.cancel(true);// 中断执行此任务的线程
					} catch (ExecutionException e) {
						logger.error(
								"LogsEventScheduled scheduled Execution exception:", e);
						future.cancel(true);// 中断执行此任务的线程
					}
				} catch (Throwable e) {
					logger.error("LogsEventScheduled scheduled Exception:", e);
				}
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

		private volatile long lastStartTime = System.currentTimeMillis();

		public void run() {
			JSONObject logJSON = new JSONObject();
			long startTime = System.currentTimeMillis();
			logJSON.put("LogQueueConsumeTask.run startTime",
					DataUtil.formatDate(new Date(startTime)));
			// 刷新上一次活跃时间
			lastStartTime = startTime;
			List<GoodsInventoryActionModel> queueLogList = null;
			try {
				//取日志队列信息
				queueLogList = goodsInventoryDomainRepository.queryLastIndexGoodsInventoryAction();
//				System.out.println("LogQueueConsumeTask:run2="+queueLogList);
			} catch (Exception e) {
				logger.error("LogQueueConsumeTask.run error", e);
			}
			// 消费数据
			if (!CollectionUtils.isEmpty(queueLogList)) {
				logJSON.put("count", queueLogList.size());
				Event event = null;
				//Future<EventResult>  future = null;
				AtomicInteger realCount = new AtomicInteger();
				for (GoodsInventoryActionModel model : queueLogList) {
					//System.out.println("model="+model.getId());
					event = new Event();
					event.setData(model);
					// 发送的不再重新进行发送
					event.setTryCount(0);
//					event.setEventType(getEventType(ResultStatusEnum.LOG
//							.getCode()));
					event.setUUID(String.valueOf(model.getId()));
					try {
						 //从队列中取事件
						boolean eventResult = logsEventHandle.handleEvent(event);
//						System.out.println("eventresult="+eventResult);
						if(eventResult) {  //落mysql成功的话,也就是消费日志消息成功
							//移除最后一个元素
							goodsInventoryDomainRepository.lremLogQueue(model);
						}
					} catch (Exception e) {
						logger.error("LogQueueConsumeTask.run error", e);
						e.printStackTrace();
					}
					realCount.incrementAndGet();
					
				}
				logJSON.put("realcount", realCount.get());
			
			}
			long endTime = System.currentTimeMillis();
			logJSON.put("costTime", endTime - startTime);
			if (logger.isDebugEnabled()) {
				logger.debug(logJSON.toString());
			}
			
		}

		/** 获取上一次的 活动时间 供检测用 */
		public long getLastActiveTime() {
			return lastStartTime;
		}
	}

}
