package com.gome.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gome.core.common.service.AbstractServiceImpl;
import com.gome.service.BusiServiceTemplate;

public abstract class AbstractService extends AbstractServiceImpl {
	
	 protected BusiServiceTemplate busiServiceTemplate;
	 
	 
	 public BusiServiceTemplate getBusiServiceTemplate() {
		 return busiServiceTemplate;
	 }
	 
	 public void setBusiServiceTemplate(BusiServiceTemplate busiServiceTemplate) {
		 this.busiServiceTemplate = busiServiceTemplate;
	 }
	

/*	<T> CallResult<T> makeResult(Class<T> t, TuanCallbackResult result) {
		return (CallResult<T>) new CallResult(result.isSuccess(),PublicCodeEnum.valuesOf(result.getResultCode()),
				result.getBusinessObject(), result.getThrowable());
	}*/
	protected static Log logBus = LogFactory.getLog("BUSINESS.USER");
	private static Log logSys = LogFactory.getLog("SYSERROR.LOG");
	
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

}
