package com.tuan.inventory.domain.job.log;

import com.tuan.notifyserver.core.domain.message.Message;

public interface ConsumerReceiverLogHandler {

	public boolean receive(Message message);
}
