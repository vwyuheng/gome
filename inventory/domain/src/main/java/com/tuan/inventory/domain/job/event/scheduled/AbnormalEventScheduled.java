package com.tuan.inventory.domain.job.event.scheduled;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.back.model.SingleOrderModel;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.job.event.Event;
import com.tuan.inventory.domain.job.event.EventHandle;
import com.tuan.inventory.domain.repository.InventoryDeductWriteService;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.support.config.InventoryConfig;
import com.tuan.inventory.domain.support.enu.EventType;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.domain.support.util.HessianProxyUtil;
import com.tuan.inventory.model.enu.ClientNameEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.util.DateUtils;
import com.tuan.ordercenter.backservice.OrderQueryService;
import com.tuan.ordercenter.model.enu.status.OrderInfoPayStatusEnum;
import com.tuan.ordercenter.model.param.OrderQueryIncParam;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.SingleOrderQueryResult;

/**
 * 调度线程，主要用于非正常情况下的库存消息更新事件调度
 * 
 * @author henry.yu
 * @Date 2014/3/24
 */
public class AbnormalEventScheduled extends AbstractEventScheduled {

	private final static Log logger = LogFactory
			.getLog(AbnormalEventScheduled.class);
	
	// 默认初始化延时 单位：毫秒
	private static final long DEFAULTINITIALDELAY = 3 * 1000;
	// 默认两次开始执行最小间隔时间 单位：毫秒
	private static final long DEFAULTDELAY = 4 * 1000;
	//默认间隔时长,与当前时间相比  单位:分种
	private static final int DEFAULTPERIOD = 5;
	// 支持配置
	private long initialDelay = 0;
	private long delay = 0;
	private int period = 0;

	@Resource
	private InventoryProviderReadService inventoryProviderReadService;
	@Resource
	private InventoryDeductWriteService inventoryDeductWriteService;
	@Resource
	private EventHandle logsEventHandle;
	@Resource
	InventoryQueueService inventoryQueueService;
	
	/**
	 * 构造带不带缓存的客户端
	 */
	public AbnormalEventScheduled() {
		super();
	}

	/**
	 * 负责按照一定的频率执行日志的消费事件的封装
	 */
	public void execFixedRate4Abnormal() {
		new Thread() {
			public void run() {
				try {
					CountDownLatch latch = new CountDownLatch(1);
					latch.await(waitTime, TimeUnit.MILLISECONDS);
					Future<?> future = null;
					try {
						System.out.println("execFixedRate4Logs1");
						future = scheduledExecutorService.scheduleAtFixedRate(
								new AbnormalQueueConsumeTask(),
								(getInitialDelay() == 0 ? DEFAULTINITIALDELAY
										: getInitialDelay()),
								(getDelay() == 0 ? DEFAULTDELAY : getDelay()),
								TimeUnit.MILLISECONDS);
						System.out.println("execFixedRate4Logs2");
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

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	class AbnormalQueueConsumeTask implements Runnable {

		private volatile long lastStartTime = System.currentTimeMillis();

		public void run() {
			JSONObject logJSON = new JSONObject();
			long startTime = System.currentTimeMillis();
			logJSON.put("AbnormalQueueConsumeTask.run startTime",
					DataUtil.formatDate(new Date(startTime)));
			// 刷新上一次活跃时间
			lastStartTime = startTime;
			List<RedisInventoryQueueDO> queueList = null;
			try {
				//取初始状态队列信息
				queueList = inventoryProviderReadService
						.getInventoryQueueByScoreStatus(Double
								.valueOf(ResultStatusEnum.LOCKED.getCode()));
				System.out.println("AbnormalQueueConsumeTask:run2="+queueList);
			} catch (Exception e) {
				logger.error("AbnormalQueueConsumeTask.run error", e);
			}
			// 消费数据
			if (!CollectionUtils.isEmpty(queueList)) {
				logJSON.put("count", queueList.size());
				Event event = null;
				//Future<EventResult>  future = null;
				AtomicInteger realCount = new AtomicInteger();
				for (RedisInventoryQueueDO model : queueList) {
					System.out.println("model="+model.getId());
					//判断该队列创建时间与当前时间相比，是否过了5分钟还未被处理(更新状态为：ACTIVE 1 正常：有效可处理),此时将它作为异常队列处理
					if(model.getCreateTime()<=DateUtils.getBeforXTimestamp10Long(getPeriod()==0?DEFAULTPERIOD:getPeriod())) {
						try {
							OrderQueryService basic = (OrderQueryService) HessianProxyUtil
									.getObject(OrderQueryService.class,
											InventoryConfig.QUERY_URL);
							final OrderQueryIncParam incParam = new OrderQueryIncParam();
							CallResult<SingleOrderQueryResult>  cllResult= basic.singleOrderQuery("", ClientNameEnum.INNER_SYSTEM.getValue(), String.valueOf(model.getOrderId()), model.getUserId(), null,incParam);
							if(cllResult.getCallResult())
							{
								//SingleOrderQueryResult singleOrderQueryResult =cllResult.getBusinessResult();
								SingleOrderModel smodel = (SingleOrderModel) cllResult.getBusinessResult().getResultObject();
								//1.当订单状态为已付款时
								if(smodel.getOrderInfoModel().getPayStatus().equals(OrderInfoPayStatusEnum.PAIED)) {
									//TODO 1 发送notifyserver消息通知 2.将队列状态标记删除
								}else {  //仍然为初始状态时:3
									//TODO 还原被扣减的库存
									try {
										//还款库存并标记删除队列信息
										inventoryQueueService.rollbackInventoryCache(String.valueOf(model.getId()), (4));
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									
									
									
								}
							}
						} catch (MalformedURLException e) {
							logger.error(e);
							e.printStackTrace();
						}
						//CallResult<SingleOrderQueryResult>  cllResult=  orderCenterFacade.singleOrderQuery("61.135.132.59", "USER_CENTER", 38110009159L, 19204477, null, null);
					}
					event = new Event();
					event.setData(model);
					// 发送的不再重新进行发送
					event.setTryCount(0);
					event.setEventType(getEventType(ResultStatusEnum.LOG
							.getCode()));
					event.setUUID(String.valueOf(model.getId()));
					try {
						 //从队列中取事件
						boolean eventResult = logsEventHandle.handleEvent(event);
						System.out.println("eventresult="+eventResult);
						if(eventResult) {  //落mysql成功的话,也就是消费日志消息成功
							//移除最后一个元素
							//inventoryQueueService.lremLogQueue(model);
						}
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
