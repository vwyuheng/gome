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
	protected static Log logBus = LogFactory.getLog("BUSINESS.USER");
	
	public AbstractInventoryService() {
		
	}
	protected void writeSysLog(LogModel lm, boolean purge) {
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
