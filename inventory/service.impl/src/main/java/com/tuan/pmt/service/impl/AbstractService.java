package com.tuan.pmt.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.core.common.service.AbstractServiceImpl;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.service.CouponServiceTemplate;

public abstract class AbstractService extends AbstractServiceImpl {
	 protected CouponServiceTemplate couponServiceTemplate;

	public CouponServiceTemplate getCouponServiceTemplate() {
		return couponServiceTemplate;
	}

	public void setCouponServiceTemplate(CouponServiceTemplate couponServiceTemplate) {
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

//	protected void writeLog(LogModel lm) {
//		if (log.isInfoEnabled()) {
//			log.info(lm.toJson());
//		}
//	}
}
