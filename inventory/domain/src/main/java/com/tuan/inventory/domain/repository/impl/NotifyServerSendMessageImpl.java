package com.tuan.inventory.domain.repository.impl;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.tuan.inventory.domain.NotifyServerHandler;
import com.tuan.inventory.domain.repository.NotifyServerSendMessage;
import com.tuan.notifyserver.core.pclient.ProducerClientQueue;
/**
 * ���ڷ���notifyserver��Ϣ�ķ�����
 * @author henry.yu
 * @date 2014/3/19
 */
public class NotifyServerSendMessageImpl implements NotifyServerSendMessage {

	@Resource
	ProducerClientQueue notifyClient;
	@Override
	public void sendNotifyServerMessage(JSONObject jsonObj) {
		//����mq����
		NotifyServerHandler handler = NotifyServerHandler.create();
		
		handler.sendNotifyMessage(notifyClient, jsonObj);

	}

}
