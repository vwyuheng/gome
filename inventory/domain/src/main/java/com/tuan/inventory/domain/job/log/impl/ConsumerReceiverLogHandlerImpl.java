package com.tuan.inventory.domain.job.log.impl;

import com.tuan.inventory.domain.job.log.ConsumerReceiverLogHandler;
import com.tuan.notifyserver.core.domain.message.Message;
/***
 * 用于消费日志流水队列，插入mysql表
 * @author henry.yu
 * @date 2014/3/19
 */
public class ConsumerReceiverLogHandlerImpl implements
		ConsumerReceiverLogHandler {

	@Override
	public boolean receive(Message message) {
		// TODO Auto-generated method stub
		return false;
	}

}
