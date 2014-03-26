package com.tuan.inventory.domain.job.task;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.job.event.Event;
import com.tuan.inventory.domain.job.event.EventManager;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.support.enu.EventType;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;

/**
 * 定时消费日志队列线程
 * 事实上就是一个用于产生日志事件的线程
 */
public class LogQueueConsumeTask implements Runnable {

	private static final Log logger = LogFactory.getLog(LogQueueConsumeTask.class);
	private volatile long lastStartTime = System.currentTimeMillis();
	@Resource
	private InventoryProviderReadService inventoryProviderReadService;
	/**
	 * 事件处理manager
	 */
	@Autowired
	private EventManager eventManager;
	
	public void run() {
		JSONObject  logJSON = new JSONObject();
		long startTime = System.currentTimeMillis();
		logJSON.put("LogQueueConsumeTask.run startTime",DataUtil.formatDate(new Date(startTime)));
		//刷新上一次活跃时间
		lastStartTime = startTime;
		List<RedisInventoryLogDO> queueLogList = null;
		try {
			//获取需要执行的数据信息
			//queueList = this.inventoryProviderReadService
					//.getInventoryQueueByScoreStatus(Double
					//		.valueOf(ResultStatusEnum.ACTIVE.getCode()));
			queueLogList = this.inventoryProviderReadService.getInventoryLogsQueue();
		} catch (Exception e) {
			logger.error("LogQueueConsumeTask.run error", e);
		}
		//消费数据
		if (!CollectionUtils.isEmpty(queueLogList)) {
			logJSON.put("count",queueLogList.size());
			Event event = null;
			AtomicInteger  realCount = new AtomicInteger();
			for (RedisInventoryLogDO model : queueLogList) {
				//if (validateQueue(model)) {
					event = new Event();
					event.setData(model);
					// 发送的不再重新进行发送
					event.setTryCount(0);
					event.setEventType(getEventType(ResultStatusEnum.LOG.getCode()));
					event.setUUID(String.valueOf(model.getId()));
					eventManager.addEventSyn(event);
					realCount.incrementAndGet();
				//}
			}
			logJSON.put("realcount",realCount.get());
		}
		long endTime = System.currentTimeMillis();
		logJSON.put("costTime",endTime-startTime);
		if(logger.isDebugEnabled()){
			logger.debug(logJSON.toString());
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
	
	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	/** 获取上一次的 活动时间 供检测用 */
	public long getLastActiveTime() {
		return lastStartTime;
	}
}
