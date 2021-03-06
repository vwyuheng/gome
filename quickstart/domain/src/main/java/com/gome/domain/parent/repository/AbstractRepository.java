package com.gome.domain.parent.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gome.domain.lang.LogModel;

public abstract class AbstractRepository {

	private static Log logBus = LogFactory.getLog("BUSINESS.USER");

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

	protected void writeBusErrorLog(String message, Exception e) {
		if (logBus.isErrorEnabled()) {
			logBus.error(message, e);
		}
	}

	protected void writeBusErrorLog(LogModel lm, Exception e) {
		if (logBus.isErrorEnabled()) {
			logBus.error(lm.toJson(), e);
		}
	}
}
