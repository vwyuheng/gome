package com.tuan.inventory.domain.support.job.event;

import java.util.concurrent.atomic.AtomicInteger;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 消息事件实体类
 * @author xianglei
 *
 */
public class Event extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;

	private Object data;//消息本体
	
	private AtomicInteger tryCount= new AtomicInteger(0);//消息重试次数
	
	private int handleBatch = 0;//
	
	private String UUID;   //消息标识

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

	public int getHandleBatch() {
		return handleBatch;
	}

	public void setHandleBatch(int handleBatch) {
		this.handleBatch = handleBatch;
	}

	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String eventString(){
	    StringBuffer eventStr=new StringBuffer("centerevent =[");
	    eventStr.append("tryCount="+tryCount+";");
	    eventStr.append("data="+data==null?"":data.toString()+";");
	    eventStr.append("handleBatch="+handleBatch+";");
	    eventStr.append("uuid="+UUID+";");
	    eventStr.append("]");
		return eventStr.toString();
	}

}
