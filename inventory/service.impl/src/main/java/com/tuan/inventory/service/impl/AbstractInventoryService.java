package com.tuan.inventory.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.service.InventoryServiceTemplate;

public abstract class AbstractInventoryService  {
	@Resource
	protected InventoryServiceTemplate inventoryServiceTemplate;

	protected static Log logSys = LogFactory.getLog("SYSERROR.LOG");
	protected static Log logSysUpdate = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	protected static Log logBus = LogFactory.getLog("SYS.QUERYRESULT.LOG");
	private static Log logInit=LogFactory.getLog("INVENTORY.INIT");
	private static Log logJob=LogFactory.getLog("INVENTORY.JOB.LOG");
	
	public AbstractInventoryService() {
		
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
	protected void writeSysLog(LogModel lm, boolean purge) {
		if (logSys.isInfoEnabled()) {
			logSys.info(lm.toJson(purge));
		}
	}
	protected void writeSysJobLog(LogModel lm, boolean purge) {
		if (logJob.isInfoEnabled()) {
			logJob.info(lm.toJson(purge));
		}
	}
	protected void writeSysUpdateLog(LogModel lm, boolean purge) {
		if (logSysUpdate.isInfoEnabled()) {
			logSysUpdate.info(lm.toJson(purge));
		}
	}
	protected void writeSysBusLog(LogModel lm, boolean purge) {
		if (logSys.isInfoEnabled()) {
			logSys.info(lm.toJson(purge));
		}
	}

	protected void writeSysLog4Debug(LogModel lm, boolean purge) {
		if (logSys.isDebugEnabled()) {
			logSys.debug(lm.toJson(purge));
		}
	}
	
	protected void writeBusLog(String message) {
		if (logBus.isInfoEnabled()) {
			logBus.info(message);
		}
	}

	protected void writeSysLog(String message) {
		if (logSys.isWarnEnabled()) {
			logSys.warn(message);
		}
	}

	protected void writeErrorLog(LogModel lm, Throwable e) {
		if (logSys.isErrorEnabled()) {
			logSys.warn(lm.toJson(false), e);
		}
	}

	
}
