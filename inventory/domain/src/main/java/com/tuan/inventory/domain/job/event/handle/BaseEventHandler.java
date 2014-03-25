package com.tuan.inventory.domain.job.event.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.job.event.Event;
import com.tuan.inventory.domain.job.event.result.EventResult;
import com.tuan.inventory.domain.support.enu.EventType;
import com.tuan.inventory.domain.support.util.QueueConstant;
import com.tuan.inventory.model.result.CallResult;

/**
 * 处理事件发送的公用判断
 * 
 * @author shaolong zhang
 * @Date 2013-8-22 下午4:05:41
 */
public class BaseEventHandler {

	private static final Log logger = LogFactory.getLog(BaseEventHandler.class);

	/** 发送的最大次数 */
	private static final int trySendMax = QueueConstant.QUEUE_MAX_CONSUME_COUNT;

	/**
	 * 处理消息 判断消息是否需要再次发送
	 * 
	 * @param handEvent
	 * @param callResult
	 * @param sendResut
	 * @param resultMsg
	 * @return EventResult
	 */
	public static EventResult handleMsg(Event handEvent,
			CallResult<Integer> callResult, boolean sendResut) {
		// 没有发送成功，直接从发
		final String values = handEvent.getUUID();
		final int tryCount = handEvent.getTryCount().intValue();
		boolean res = false;
		String resultMsg = EventType.SUCCESS.toString();
		if (sendResut) {
			res = true;
			// 数据没有处理成功,暂时不要删除标记
			if (callResult == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("queue:" + values
							+ " send success,but center consumer fail");
				}
				resultMsg = EventType.ERROR.toString();
			} else if (!callResult.getCallResult()) {

				if (logger.isDebugEnabled()) {
					logger.debug("queue:" + values
							+ " send success,but center consumer fail");
				}
				resultMsg = EventType.ERROR.toString();
			} else {
				// 处理成功删除缓存标记
				if (logger.isDebugEnabled()) {
					logger.debug("queue:" + values
							+ " send success,remove from queue");
				}
				// taskMemClient.delete(values);
			}
		} else {
			if (tryCount < trySendMax) {
				// 标记失败，等待下次发送
				if (logger.isDebugEnabled()) {
					logger.debug("queue:" + values + " send " + tryCount
							+ " failed,add to queue");
				}
				res = false;
			} else {
				// 尝试几次都没有发送成功，标记失败，废弃这条数据
				if (logger.isDebugEnabled()) {
					logger.debug("queue:" + values + " send " + tryCount
							+ " faild,remove from queue");
				}
				// taskMemClient.delete(values);
				// TODO 需要报警处理吗
				//resultMsg = EventType.ALARM.toString();
				res = true;
			}
		}
		return new EventResult(res, resultMsg);
	}

}
