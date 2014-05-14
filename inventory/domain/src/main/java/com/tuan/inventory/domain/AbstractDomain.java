package com.tuan.inventory.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.support.logs.LogModel;

public abstract class AbstractDomain {
	
	public final static String CLIENT_NAME 		= "INVENTORY";
	
	public String clientIp; // 客户端ip地址
	public String clientName; // 客户端名称
	
	private static Log logBus=LogFactory.getLog("COMMON.BUSINESS");
	private static Log logInit=LogFactory.getLog("INVENTORY.INIT");
	protected static Log logSysUpdate = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	protected static Log logSysDeduct = LogFactory.getLog("INVENTORY.DEDUCT.LOG");
	
	
	protected void writeSysUpdateLog(LogModel lm, boolean purge) {
		if (logSysUpdate.isInfoEnabled()) {
			logSysUpdate.info(lm.toJson(purge));
		}
	}
	protected void writeSysDeductLog(LogModel lm, boolean purge) {
		if (logSysDeduct.isInfoEnabled()) {
			logSysDeduct.info(lm.toJson(purge));
		}
	}
	
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
	/**
	 * 记录库存初始化日志
	 * @param lm
	 * @param toJson
	 */
	protected void writeBusInitLog(LogModel lm,boolean toJson) {
		if (logInit.isInfoEnabled()) {
			logInit.info(lm.toJson(toJson));
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
	protected void writeBusUpdateErrorLog(LogModel lm,boolean toJson,Exception e) {
		if (logSysUpdate.isErrorEnabled()) { 
			logSysUpdate.error(lm.toJson(toJson),e);
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
	protected void writeBusInitErrorLog(LogModel lm,boolean toJson,Exception e) {
		if (logInit.isErrorEnabled()) { 
			logInit.error(lm.toJson(toJson),e);
		}
	}
}
