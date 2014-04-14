package com.tuan.inventory.job.util;

import com.tuan.inventory.job.result.RequestPacket;
import com.wowotrace.trace.model.Message;

public class JobUtils {

	/**
	 * 创建trace对象
	 * @param packet
	 * @return
	 */
	public static Message makeTraceMessage(RequestPacket packet){
		if(packet == null || packet.getTraceId() == null || packet.getTraceRootId() == null
				|| packet.getTraceId().isEmpty() || packet.getTraceRootId().isEmpty()){
			return null;
		}
		Message traceMessage = Message.getMessage(packet.getTraceRootId(),packet.getTraceId());
		return traceMessage;
	}
}
