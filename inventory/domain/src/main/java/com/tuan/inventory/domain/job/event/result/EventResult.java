package com.tuan.inventory.domain.job.event.result;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * �����¼�������󷵻���Ϣ
 * 
 * @author shaolong zhang
 * @Date  2013-5-9 ����4:33:45
 */
public class EventResult extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private boolean isSuccess;    
	
	/**
	 * ��Ӧʱ�����͵�������Ϣ
	 */
	private String eventResult;

		
	public EventResult(boolean isSuccess,String eventResult) {
		super();
		this.isSuccess = isSuccess;
		this.eventResult = eventResult;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}


	public String getEventResult() {
		return eventResult;
	}

	public void setBusinessResult(String eventResult) {
		this.eventResult = eventResult;
	}
	
	public String toString(){
	    StringBuffer eventStr=new StringBuffer("event =[");
	    eventStr.append("isSuccess="+isSuccess+";");
	    eventStr.append("eventResult="+eventResult);
	    eventStr.append("]");
		return eventStr.toString();
	}
}
