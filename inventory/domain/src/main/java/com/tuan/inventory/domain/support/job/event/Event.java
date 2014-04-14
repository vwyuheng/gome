package com.tuan.inventory.domain.support.job.event;

import java.util.concurrent.atomic.AtomicInteger;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.domain.support.enu.EventType;

/**
 * ��Ϣ�¼�ʵ����
 * @author xianglei
 *
 */
public class Event extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;

	private Object data;//��Ϣ����
	
	private EventType eventCode;//��Ϣҵ����
	
	private AtomicInteger tryCount= new AtomicInteger(0);//��Ϣ���Դ���
	
	private long waitTime = 0;// ��Ϣ�ȴ�����ʱ��
	
	private String UUID;   //��Ϣ��ʶ

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
