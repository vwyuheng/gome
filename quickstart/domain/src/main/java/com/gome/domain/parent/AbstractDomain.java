package com.gome.domain.parent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gome.domain.lang.LogModel;

public abstract class AbstractDomain {
	
	public final static String CLIENT_NAME 		= "quickstart";
	
	public String clientIp; // 客户端ip地址
	public String clientName; // 客户端名称
	
	private static Log logBus=LogFactory.getLog("COMMON.BUSINESS");

	
	protected void writeBusLog(LogModel lm) {
		if (logBus.isInfoEnabled()) {
			logBus.info(lm.toJson());
		}
	}
	/**
	 * 通过传递true|false来控制输出异常信息链
	 * @param lm
	 * @param toJson
	 */
	protected void writeBusLog(LogModel lm,boolean toJson) {
		if (logBus.isInfoEnabled()) {
			logBus.info(lm.toJson(toJson));
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
	
	
	/**
	 * 通过传递true|false来控制输出异常信息链
	 * @param lm
	 * @param toJson
	 * @param e
	 */
	protected void writeBusErrorLog(LogModel lm,boolean toJson,Exception e) {
		if (logBus.isErrorEnabled()) { 
			logBus.error(lm.toJson(toJson),e);
		}
	}
	
}
