package com.tuan.inventory.domain.job.event.scheduled;

import java.util.Date;
import java.util.List;
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
import com.tuan.inventory.domain.job.event.Event;
import com.tuan.inventory.domain.job.event.EventHandle;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.support.enu.EventType;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;

/**
 * 调度线程，主要用于日志事件初始化调度
 * 
 * @author henry.yu
 * @Date 2014/3/24
 */
public class LogsEventScheduled {

	private final static Log logger = LogFactory
			.getLog(LogsEventScheduled.class);
	/**
	 * 调度线程池，用于初始化日志事件数据
	 */
	private ScheduledExecutorService scheduledExecutorService;
	/**
	 * 单个cpu的线程数
	 */
	private final int POOL_SIZE = 5;

	/** 队列监听等待时间 */
	private long waitTime = 200;
	// 默认初始化延时
	private static final long DEFAULTINITIALDELAY = 3 * 1000;
	// 默认两次开始执行最小间隔时间
	private static final long DEFAULTDELAY = 4 * 1000;
	// 支持配置
	private long initialDelay = 0;
	private long delay = 0;

	@Resource
	private InventoryProviderReadService inventoryProviderReadService;
	@Resource
	private EventHandle logsEventHandle;
	/**
	 * 构造带不带缓存的客户端
	 */
	public LogsEventScheduled() {
		// 初始化ScheduledExecutorService 服务
		this.scheduledExecutorService = Executors
				.newScheduledThreadPool(Runtime.getRuntime()
						.availableProcessors() * POOL_SIZE);
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
						System.out.println("execFixedRate4Logs1");
						future = scheduledExecutorService.scheduleAtFixedRate(
								new LogQueueConsumeTask(),
								(getInitialDelay() == 0 ? DEFAULTINITIALDELAY
										: getInitialDelay()),
								(getDelay() == 0 ? DEFAULTDELAY : getDelay()),
								TimeUnit.MILLISECONDS);
						System.out.println("execFixedRate4Logs2");
						if (future != null) {
							Object result = future.get();
							if (result == null) {
								if (logger.isDebugEnabled()) {
									logger.debug("ExecuteTask scheduled return null");
								}
								return;
							}
							if (logger.isDebugEnabled()) {
								logger.debug("ExecuteTask scheduled :"
										+ result.toString());
							}
							System.out.println("execFixedRate4Logs3");
						}else {
							System.out.println("execFixedRate4Logs4");
						}
					} catch (InterruptedException e) {
						logger.error(
								"ExecuteTask scheduled Interrupted exception :",
								e);
						future.cancel(true);// 中断执行此任务的线程
					} catch (ExecutionException e) {
						logger.error(
								"ExecuteTask scheduled Execution exception:", e);
						future.cancel(true);// 中断执行此任务的线程
					}
				} catch (Throwable e) {
					logger.error("scheduled Exception:", e);
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
			List<RedisInventoryLogDO> queueLogList = null;
			try {
				
				queueLogList = inventoryProviderReadService
						.getInventoryLogsQueue();
				System.out.println("LogQueueConsumeTask:run2="+queueLogList);
			} catch (Exception e) {
				logger.error("LogQueueConsumeTask.run error", e);
			}
			// 消费数据
			if (!CollectionUtils.isEmpty(queueLogList)) {
				logJSON.put("count", queueLogList.size());
				Event event = null;
				//Future<EventResult>  future = null;
				AtomicInteger realCount = new AtomicInteger();
				for (RedisInventoryLogDO model : queueLogList) {
					System.out.println("model="+model.getId());
					event = new Event();
					event.setData(model);
					// 发送的不再重新进行发送
					event.setTryCount(0);
					event.setEventType(getEventType(ResultStatusEnum.LOG
							.getCode()));
					event.setUUID(String.valueOf(model.getId()));
					try {
						 //从队列中取事件
						logsEventHandle.handleEvent(event);
					} catch (Exception e) {
						
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

		/**
		 * 通过队列类型，获取事件类型,充值和支付都算是支付的事件进行处理
		 * 
		 * @param queueTypeEnum
		 * @return EventType
		 */
		public EventType getEventType(final String status) {
			if (StringUtils.isEmpty(status)) {
				return null;
			}
			if (status.equals(ResultStatusEnum.LOCKED.getCode())) {
				return EventType.ABNORMAL;
			} else if (status.equals(ResultStatusEnum.ACTIVE.getCode())) {
				return EventType.NORMAL;
			} else if (status.equals(ResultStatusEnum.LOG.getCode())) {
				return EventType.LOG;
			} else {
				return null;
			}
		}

		/** 获取上一次的 活动时间 供检测用 */
		public long getLastActiveTime() {
			return lastStartTime;
		}
	}

}
