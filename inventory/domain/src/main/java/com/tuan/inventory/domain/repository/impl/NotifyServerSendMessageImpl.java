package com.tuan.inventory.domain.repository.impl;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.tuan.inventory.domain.NotifyServerHandler;
import com.tuan.inventory.domain.repository.NotifyServerSendMessage;
import com.tuan.notifyserver.core.pclient.ProducerClientQueue;
/**
 * 用于发送notifyserver消息的服务类
 * @author henry.yu
 * @date 2014/3/19
 */
public class NotifyServerSendMessageImpl implements NotifyServerSendMessage {

	@Resource
	ProducerClientQueue notifyClient;
	@Override
	public void sendNotifyServerMessage(JSONObject jsonObj) {
		//构建mq对象
		NotifyServerHandler handler = NotifyServerHandler.create();
		
		handler.sendNotifyMessage(notifyClient, jsonObj);

	}

}
