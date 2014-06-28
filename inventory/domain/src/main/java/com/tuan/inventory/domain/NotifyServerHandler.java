package com.tuan.inventory.domain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.notifyserver.core.pclient.ProducerClientQueue;

public class NotifyServerHandler {
	protected ExecutorService exec;
	protected int threadNum = 10;
	protected static Log log = LogFactory.getLog("NOTIFYSERVER.USER");
	public static NotifyServerHandler instance = new NotifyServerHandler();

	public static NotifyServerHandler create() {
		return instance;
	}

	public NotifyServerHandler() {
		super();
		this.exec = Executors.newFixedThreadPool(threadNum);
	}

	public void sendNotifyMessage(String sender,ProducerClientQueue client, JSONObject jsonObj) {
		NotifyTask task = new NotifyTask();
		task.setClient(client);
		task.setMessage(jsonObj.toString());
		try {
			log.warn("from "+sender+":" + jsonObj.toString());
			exec.submit(task);
		} catch (Exception e) {
			log.error("Send NotifyMsg Error by NotifyServerHandler [" + jsonObj.toString() + "]", e);
		}
	}

	class NotifyTask implements Runnable {

		ProducerClientQueue client;

		String message;

		public ProducerClientQueue getClient() {
			return client;
		}

		public void setClient(ProducerClientQueue client) {
			this.client = client;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
				//String messageId = client.send(message.getSendMsg());
				String messageId = client.send(message);
				log.warn("UUID:" + messageId + ":"+message);
			} catch (Exception e) {
				log.error("run,Message Error for Sending to MQ !", e);
			}
		}
	}

	public NotifyServerHandler(int threadNum) {
		super();
		this.threadNum = threadNum;
		this.exec = Executors.newFixedThreadPool(threadNum);
	}
	
}
