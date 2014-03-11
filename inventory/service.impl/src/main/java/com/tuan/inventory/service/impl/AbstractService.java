package com.tuan.inventory.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.core.common.service.AbstractServiceImpl;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.service.BusiServiceTemplate;
import com.tuan.inventory.service.InventoryServiceTemplate;

public abstract class AbstractService extends AbstractServiceImpl {
	
	 protected BusiServiceTemplate busiServiceTemplate;
	 protected InventoryServiceTemplate couponServiceTemplate;

	 
	 public BusiServiceTemplate getBusiServiceTemplate() {
		 return busiServiceTemplate;
	 }
	 
	 public void setBusiServiceTemplate(BusiServiceTemplate busiServiceTemplate) {
		 this.busiServiceTemplate = busiServiceTemplate;
	 }
	 
	public InventoryServiceTemplate getCouponServiceTemplate() {
		return couponServiceTemplate;
	}

	public void setCouponServiceTemplate(InventoryServiceTemplate couponServiceTemplate) {
		this.couponServiceTemplate = couponServiceTemplate;
	}

	<T> CallResult<T> makeResult(Class<T> t, TuanCallbackResult result) {
		return (CallResult<T>) new CallResult(result.isSuccess(),
				result.getBusinessObject(), result.getThrowable());
	}
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
