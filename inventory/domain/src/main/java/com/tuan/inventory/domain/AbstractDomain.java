package com.tuan.inventory.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.support.logs.LogModel;

public abstract class AbstractDomain {
	
	public final static String CLIENT_NAME 		= "INVENTORY";
	
	public String clientIp; // 客户端ip地址
	public String clientName; // 客户端名称
	
	private static Log logBus=LogFactory.getLog("BUSINESS.USER");
	
	protected void writeBusLog(LogModel lm) {
		if (logBus.isInfoEnabled()) {
			logBus.info(lm.toJson());
		}
	}
	protected void writeBusLog(String message) {
		if (logBus.isInfoEnabled()) {
			logBus.info(message);
		}
	}
	protected void writeBusErrorLog(String message,Exception e) {
		if (logBus.isErrorEnabled()) {
			logBus.error(message,e);
		}
	}
	protected void writeBusErrorLog(LogModel lm,Exception e) {
		if (logBus.isErrorEnabled()) { 
			logBus.error(lm.toJson(),e);
		}
	}
}
