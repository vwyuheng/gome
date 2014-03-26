package com.tuan.inventory.domain.job.event.handle;

import javax.annotation.Resource;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.job.event.Event;
import com.tuan.inventory.domain.job.event.EventHandle;
import com.tuan.inventory.domain.job.event.result.EventResult;
import com.tuan.inventory.domain.repository.LogOfWaterHandleService;
import com.tuan.inventory.domain.support.exception.RedisRunException;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.model.result.CallResult;

/**
 * 日志异步处理事件
 * @author henry.yu
 * @Date  2014/3/21
 */
public class LogsEventHandle implements EventHandle {
	
	private final static LocalLogger log = LocalLogger.getLog("LogsEventHandle.LOG");
	
	/**
	 * 格式queueId，QueueType,queuestatus,tradeNo，money,payType,consumeCount，notifyUrl，thridMsg，resultMsg
	 */
	//private final static Log BUSINESS_LOG  = LogFactory.getLog(HANDLE_LOG);
	
	@Resource
	LogOfWaterHandleService logOfWaterHandleService;
	
	@Override
	public EventResult handleEvent(final Event event) throws Exception{
		if(event == null){
			return null;
		}
		LogModel lm = LogModel.newLogModel("LogsEventHandle.handleEvent");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("event",event)
				.addMetaData("startTime", startTime).toJson());
		boolean handlerResult = true;
		RedisInventoryLogDO logDO = null;
		 CallResult<Integer> callResult  = null;
		Event handEvent = null;
		try {
			logDO = (RedisInventoryLogDO) event.getData();
			//callResult = null;
			if (logDO == null) {
				handlerResult = false;
			} else {
				// 消费对列的信息
				callResult = logOfWaterHandleService.createLogOfWater(logDO);
			}
			handEvent = event;
		} catch (Exception e) {
			log.error(lm.addMetaData("event",event)
					.addMetaData("element",logDO)
					.addMetaData("callResult",callResult)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			throw new RedisRunException("LogsEventHandle.handleEvent run exception!",e);
		}
		
		log.info(lm.addMetaData("event",event)
				.addMetaData("element",logDO)
				.addMetaData("callResult",callResult)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return BaseEventHandler.handleMsg(handEvent,callResult,handlerResult);
	}
	


}
