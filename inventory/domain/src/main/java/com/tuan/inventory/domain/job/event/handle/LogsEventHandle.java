package com.tuan.inventory.domain.job.event.handle;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.job.event.Event;
import com.tuan.inventory.domain.job.event.EventHandle;
import com.tuan.inventory.domain.job.event.result.EventResult;
import com.tuan.inventory.domain.repository.LogOfWaterHandleService;
import com.tuan.inventory.model.result.CallResult;

/**
 * 日志异步处理事件
 * @author henry.yu
 * @Date  2014/3/21
 */
public class LogsEventHandle implements EventHandle {
	
	private static final Log logger = LogFactory.getLog(LogsEventHandle.class);
	
	/**
	 * 格式queueId，QueueType,queuestatus,tradeNo，money,payType,consumeCount，notifyUrl，thridMsg，resultMsg
	 */
	private final static Log BUSINESS_LOG  = LogFactory.getLog(HANDLE_LOG);
	
	@Resource
	LogOfWaterHandleService logOfWaterHandleService;
	
	@Override
	public EventResult handleEvent(final Event event) throws Exception{
		if(event == null){
			return null;
		}
		
		boolean handlerResult = true;
		 RedisInventoryLogDO logDO = (RedisInventoryLogDO)event.getData();
		//final String jsonData = queueModel.getJsonData();
		 CallResult<Integer> callResult = null;
		if(logDO==null) {
			handlerResult = false;
		}else {
			// 消费对列的信息
			callResult = logOfWaterHandleService.createLogOfWater(logDO);
		}
		
		Event handEvent = event;
		return BaseEventHandler.handleMsg(handEvent,callResult,handlerResult);
	}
	


}
