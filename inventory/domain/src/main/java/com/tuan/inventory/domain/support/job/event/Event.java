package com.tuan.inventory.domain.support.job.event;

import java.util.concurrent.atomic.AtomicInteger;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.domain.support.enu.EventType;

/**
 * 消息事件实体类
 * @author xianglei
 *
 */
public class Event extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;

	private Object data;//消息本体
	
	private EventType eventCode;//消息业务码
	
	private AtomicInteger tryCount= new AtomicInteger(0);//消息重试次数
	
	private long waitTime = 0;// 消息等待发送时间
	
	private String UUID;   //消息标识

	public EventType getEventType() {
		return eventCode;
	}

	public void setEventType(EventType eventCode) {
		this.eventCode = eventCode;
	}

	public AtomicInteger getTryCount() {
		return tryCount;
	}

	public void setTryCount(int count) {
		tryCount.set(count);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}
	
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String eventString(){
	    StringBuffer eventStr=new StringBuffer("centerevent =[");
	    eventStr.append("eventCode="+eventCode+";");
	    eventStr.append("tryCount="+tryCount+";");
	    eventStr.append("data="+data==null?"":data.toString()+";");
	    eventStr.append("waitTime="+waitTime+";");
	    eventStr.append("uuid="+UUID+";");
	    eventStr.append("]");
		return eventStr.toString();
	}

}
