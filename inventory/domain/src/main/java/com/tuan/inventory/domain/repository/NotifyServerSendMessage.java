package com.tuan.inventory.domain.repository;

import net.sf.json.JSONObject;


public interface NotifyServerSendMessage {

	public void sendNotifyServerMessage(String sender,JSONObject jsonObj); 
}
