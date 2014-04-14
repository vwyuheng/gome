package com.tuan.inventory.job.scheduled;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.job.result.RequestPacket;
import com.tuan.inventory.job.util.JobUtils;
import com.tuan.inventory.model.enu.ClientNameEnum;
import com.tuan.inventory.service.GoodsInventoryScheduledService;
import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageTypeEnum;

/**
 * 调度线程，主要用于确认回调ok情况下的库存消息更新事件调度
 * 
 * @author henry.yu
 * @Date 2014/04/01
 */
public class ConfirmEventScheduled extends AbstractEventScheduled {

	private final static Log logger = LogFactory
			.getLog(ConfirmEventScheduled.class);
	
	// 默认初始化延时 单位：毫秒
	private static final long DEFAULTINITIALDELAY = 3 * 1000;
	// 默认两次开始执行最小间隔时间 单位：毫秒
	private static final long DEFAULTDELAY = 4 * 1000;
	// 支持配置
	private long initialDelay = 0;
	private long delay = 0;

	@Resource
	private GoodsInventoryScheduledService goodsInventoryScheduledService;
	
	
	/**
	 * 构造带不带缓存的客户端
	 */
	public ConfirmEventScheduled() {
		super();
	}

	/**
	 * 负责按照一定的频率执行日志的消费事件的封装
	 */
	public void execFixedRate4Confirm() {
		new Thread() {
			public void run() {
				try {
					CountDownLatch latch = new CountDownLatch(1);
					latch.await(waitTime, TimeUnit.MILLISECONDS);
					Future<?> future = null;
					try {
						System.out.println("execFixedRate4Confirm1");
						future = scheduledExecutorService.scheduleAtFixedRate(
								new ConfirmQueueConsumeTask(),
								(getInitialDelay() == 0 ? DEFAULTINITIALDELAY
										: getInitialDelay()),
								(getDelay() == 0 ? DEFAULTDELAY : getDelay()),
								TimeUnit.MILLISECONDS);
						System.out.println("execFixedRate4Confirm2");
						if (future != null) {
							Object result = future.get();
							if (result == null) {
								if (logger.isDebugEnabled()) {
									logger.debug("execFixedRate4Confirm scheduled return null");
								}
								return;
							}
							if (logger.isDebugEnabled()) {
								logger.debug("execFixedRate4Confirm scheduled :"
										+ result.toString());
							}
							
						}
					} catch (InterruptedException e) {
						logger.error(
								"execFixedRate4Confirm scheduled Interrupted exception :",
								e);
						future.cancel(true);// 中断执行此任务的线程
					} catch (ExecutionException e) {
						logger.error(
								"execFixedRate4Confirm scheduled Execution exception:", e);
						future.cancel(true);// 中断执行此任务的线程
					}
				} catch (Throwable e) {
					logger.error("execFixedRate4Confirm scheduled Exception:", e);
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

	class ConfirmQueueConsumeTask implements Runnable {

		private volatile long lastStartTime = System.currentTimeMillis();

		public void run() {
			JSONObject logJSON = new JSONObject();
			long startTime = System.currentTimeMillis();
			logJSON.put("ConfirmQueueConsumeTask.run startTime",
					DataUtil.formatDate(new Date(startTime)));
			// 刷新上一次活跃时间
			lastStartTime = startTime;
			RequestPacket packet = new RequestPacket();
			packet.setTraceId(UUID.randomUUID().toString());
			packet.setTraceRootId(UUID.randomUUID().toString());
			Message traceMessage = JobUtils.makeTraceMessage(packet);
			TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "ConfirmQueueConsumeTask", "thread");
			try {
				
				goodsInventoryScheduledService.confirmQueueConsume(ClientNameEnum.INNER_SYSTEM.getValue(),"", traceMessage);
			} catch (Exception e) {
				logger.error("ConfirmQueueConsumeTask.run error", e);
			}
			//System.out.println("queueList1="+queueList.size());
		
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
