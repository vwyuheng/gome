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
 * ��ʱ�������������߳�
 */
public class LogQueueConsumeTask implements Runnable {

	private static final Log logger = LogFactory.getLog(LogQueueConsumeTask.class);
	
	@Resource
	private InventoryProviderReadService inventoryProviderReadService;
	
	private volatile long lastStartTime = System.currentTimeMillis();
	
	/**
	 * �¼�����manager
	 */
	@Autowired
	private EventManager eventManager;
	
	public void run() {
		JSONObject  logJSON = new JSONObject();
		long startTime = System.currentTimeMillis();
		logJSON.put("QueueConsumeTask.run startTime",DataUtil.formatDate(new Date(startTime)));
		//ˢ����һ�λ�Ծʱ��
		lastStartTime = startTime;
		List<RedisInventoryLogDO> queueLogList = null;
		try {
			//��ȡ��Ҫִ�е�������Ϣ
			//queueList = this.inventoryProviderReadService
					//.getInventoryQueueByScoreStatus(Double
					//		.valueOf(ResultStatusEnum.ACTIVE.getCode()));
			queueLogList = this.inventoryProviderReadService.getInventoryLogsQueue();
		} catch (Exception e) {
			logger.error("LogQueueConsumeTask.run error", e);
		}
			
		//logJSON.put("count",count);
		//��������
		if (!CollectionUtils.isEmpty(queueLogList)) {
			Event event = null;
			AtomicInteger  realCount = new AtomicInteger();
			for (RedisInventoryLogDO model : queueLogList) {
				//if (validateQueue(model)) {
					event = new Event();
					event.setData(model);
					// ���͵Ĳ������½��з���
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
	 * ͨ���������ͣ���ȡ�¼�����,��ֵ��֧��������֧�����¼����д���
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
	
	
	/**
	 * ��֤����modle �ĺϷ���
	 * 
	 * @param model
	 * @return boolean
	 */
	/*public boolean validateQueue(final QueueModel model){
		int consumeCount = model.getConsumeCount();
		if (consumeCount >= QueueConstant.QUEUE_MAX_CONSUME_COUNT) {
			return false;
		}
		int consumeStatus = model.getConsumeStatus();
		if(consumeStatus == QueueConstant.QUEUE_STATUS_CONSUMER_SUCCESS){
			return false;
		}
	    QueueStatusEnum queueStatusEnum  = model.getQueueStatusEnum();
	    if(queueStatusEnum == null || queueStatusEnum != QueueStatusEnum.LOCKED){
	    	return false;
	    }
	    String jsonData = model.getJsonData();
	    if(StringUtils.isEmpty(jsonData)){
	    	return false;
	    }
	    return true;
	}*/

	
	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	/** ��ȡ��һ�ε� �ʱ�� ������� */
	public long getLastActiveTime() {
		return lastStartTime;
	}
}
