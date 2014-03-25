package com.tuan.inventory.domain.job.event.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.job.event.Event;
import com.tuan.inventory.domain.job.event.result.EventResult;
import com.tuan.inventory.domain.support.enu.EventType;
import com.tuan.inventory.domain.support.util.QueueConstant;
import com.tuan.inventory.model.result.CallResult;

/**
 * �����¼����͵Ĺ����ж�
 * 
 * @author shaolong zhang
 * @Date 2013-8-22 ����4:05:41
 */
public class BaseEventHandler {

	private static final Log logger = LogFactory.getLog(BaseEventHandler.class);

	/** ���͵������� */
	private static final int trySendMax = QueueConstant.QUEUE_MAX_CONSUME_COUNT;

	/**
	 * ������Ϣ �ж���Ϣ�Ƿ���Ҫ�ٴη���
	 * 
	 * @param handEvent
	 * @param callResult
	 * @param sendResut
	 * @param resultMsg
	 * @return EventResult
	 */
	public static EventResult handleMsg(Event handEvent,
			CallResult<Integer> callResult, boolean sendResut) {
		// û�з��ͳɹ���ֱ�Ӵӷ�
		final String values = handEvent.getUUID();
		final int tryCount = handEvent.getTryCount().intValue();
		boolean res = false;
		String resultMsg = EventType.SUCCESS.toString();
		if (sendResut) {
			res = true;
			// ����û�д���ɹ�,��ʱ��Ҫɾ�����
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
				// ����ɹ�ɾ��������
				if (logger.isDebugEnabled()) {
					logger.debug("queue:" + values
							+ " send success,remove from queue");
				}
				// taskMemClient.delete(values);
			}
		} else {
			if (tryCount < trySendMax) {
				// ���ʧ�ܣ��ȴ��´η���
				if (logger.isDebugEnabled()) {
					logger.debug("queue:" + values + " send " + tryCount
							+ " failed,add to queue");
				}
				res = false;
			} else {
				// ���Լ��ζ�û�з��ͳɹ������ʧ�ܣ�������������
				if (logger.isDebugEnabled()) {
					logger.debug("queue:" + values + " send " + tryCount
							+ " faild,remove from queue");
				}
				// taskMemClient.delete(values);
				// TODO ��Ҫ����������
				//resultMsg = EventType.ALARM.toString();
				res = true;
			}
		}
		return new EventResult(res, resultMsg);
	}

}
