package com.tuan.inventory.domain.job.event.scheduled;

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

import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.repository.NotifyServerSendMessage;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.model.RedisInventoryModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;

/**
 * 调度线程，主要用于确认回调ok情况下的库存消息更新事件调度
 * 
 * @author henry.yu
 * @Date 2014/04/01
 */
public class NormalEventScheduled extends AbstractEventScheduled {

	private final static Log logger = LogFactory
			.getLog(NormalEventScheduled.class);
	
	// 默认初始化延时 单位：毫秒
	private static final long DEFAULTINITIALDELAY = 3 * 1000;
	// 默认两次开始执行最小间隔时间 单位：毫秒
	private static final long DEFAULTDELAY = 4 * 1000;
	// 支持配置
	private long initialDelay = 0;
	private long delay = 0;

	@Resource
	private InventoryProviderReadService inventoryProviderReadService;
	@Resource
	InventoryQueueService inventoryQueueService;
	@Resource
	NotifyServerSendMessage notifyServerSendMessage;
	
	/**
	 * 构造带不带缓存的客户端
	 */
	public NormalEventScheduled() {
		super();
	}

	/**
	 * 负责按照一定的频率执行日志的消费事件的封装
	 */
	public void execFixedRate4Normal() {
		new Thread() {
			public void run() {
				try {
					CountDownLatch latch = new CountDownLatch(1);
					latch.await(waitTime, TimeUnit.MILLISECONDS);
					Future<?> future = null;
					try {
						System.out.println("execFixedRate4Normal1");
						future = scheduledExecutorService.scheduleAtFixedRate(
								new NormalQueueConsumeTask(),
								(getInitialDelay() == 0 ? DEFAULTINITIALDELAY
										: getInitialDelay()),
								(getDelay() == 0 ? DEFAULTDELAY : getDelay()),
								TimeUnit.MILLISECONDS);
						System.out.println("execFixedRate4Normal2");
						if (future != null) {
							Object result = future.get();
							if (result == null) {
								if (logger.isDebugEnabled()) {
									logger.debug("NormalEventScheduled scheduled return null");
								}
								return;
							}
							if (logger.isDebugEnabled()) {
								logger.debug("NormalEventScheduled scheduled :"
										+ result.toString());
							}
							
						}
					} catch (InterruptedException e) {
						logger.error(
								"NormalEventScheduled scheduled Interrupted exception :",
								e);
						future.cancel(true);// 中断执行此任务的线程
					} catch (ExecutionException e) {
						logger.error(
								"NormalEventScheduled scheduled Execution exception:", e);
						future.cancel(true);// 中断执行此任务的线程
					}
				} catch (Throwable e) {
					logger.error("NormalEventScheduled scheduled Exception:", e);
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

	class NormalQueueConsumeTask implements Runnable {

		private volatile long lastStartTime = System.currentTimeMillis();

		public void run() {
			JSONObject logJSON = new JSONObject();
			long startTime = System.currentTimeMillis();
			logJSON.put("NormalQueueConsumeTask.run startTime",
					DataUtil.formatDate(new Date(startTime)));
			// 刷新上一次活跃时间
			lastStartTime = startTime;
			List<RedisInventoryQueueDO> queueList = null;
			try {
				//取初始状态队列信息
				queueList = inventoryProviderReadService
						.getInventoryQueueByScoreStatus(Double
								.valueOf(ResultStatusEnum.ACTIVE.getCode()));
				System.out.println("NormalQueueConsumeTask:run2="+queueList);
			} catch (Exception e) {
				logger.error("NormalQueueConsumeTask.run error", e);
			}
			//System.out.println("queueList1="+queueList.size());
			// 消费数据
			if (!CollectionUtils.isEmpty(queueList)) {
				logJSON.put("count", queueList.size());
				AtomicInteger realCount = new AtomicInteger();
				System.out.println("queueList2="+queueList.size());
				for (RedisInventoryQueueDO model : queueList) {
					System.out.println("model="+model.getId());
						try {
									//根据key取消息实体
									RedisInventoryModel result = null;
									result = inventoryProviderReadService.getInventoryInfosByKey(String.valueOf(model.getGoodsId()));
									System.out.println("result="+result);
									if(result!=null) {
										//发送库存新增消息[立即发送]，不在走队列发更新消息了
										notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(ObjectUtil.asemblyNotifyMessage(model.getUserId(),result)));
										System.out.println("notifyServerSendMessage,sended");
									}
									//将该正常状况队列状态由活跃状态：ACTIVE		("1",	"正常：有效可处理（active）")，置为删除:7
									inventoryQueueService.markQueueStatus(String.valueOf(model.getId()), (6));
									realCount.incrementAndGet();
						} catch (Exception e) {
							logger.error(e);
							e.printStackTrace();
						}

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
