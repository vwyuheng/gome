package com.tuan.inventory.domain.support.logs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@SuppressWarnings({ "rawtypes" })
public class LocalLogger {
	private final static String ERRORINFO = "ERRORINFO";
	private final Log log;

	private LocalLogger() {
		this.log = LogFactory.getLog(ERRORINFO);
	}

	private LocalLogger(String conf) {
		this.log = LogFactory.getLog(conf);
	}

	private LocalLogger(Class clazz) {

		this.log = LogFactory.getLog(clazz);
	}

	public static LocalLogger getLog() {
		LocalLogger logger = new LocalLogger();
		return logger;
	}

	public static LocalLogger getLog(String conf) {
		LocalLogger logger = new LocalLogger(conf);
		return logger;
	}

	public static LocalLogger getLog(Class clazz) {
		LogEnv logEnv = LogEnv.getEnvInstance();
		LocalLogger logger = new LocalLogger(clazz);
		if (logEnv.isDefault()) {
			logger = new LocalLogger(clazz);
		} else {
			logger = new LocalLogger(logEnv.getLogConf());
		}
		return logger;
	}
	
	public boolean isInfoEnabled(){
		return log.isInfoEnabled();
	}
	
	public boolean isWarnEnabled(){
		return log.isWarnEnabled();
	}
	
	public boolean isErrorEnabled(){
		return log.isErrorEnabled();
	}
	
	public boolean isDebugEnabled(){
		return log.isDebugEnabled();
	}

	public void info(String msg) {
		if (log.isInfoEnabled()) {
			log.info(msg);
		}
	}

	public void warn(String msg) {
		if (log.isWarnEnabled()) {
			log.warn(msg);
		}
	}

	public void warn(String msg, Throwable t) {
		if (log.isWarnEnabled()) {
			log.warn(msg, t);
		}
	}

	public void debug(String msg) {
		if (log.isDebugEnabled()) {
			log.debug(msg);
		}
	}

	public void debug(String msg, Throwable t) {
		if (log.isDebugEnabled()) {
			log.debug(msg, t);
		}
	}

	public void error(String msg, Throwable t) {
		if (log.isErrorEnabled()) {
			if (t == null) {
				log.error(msg);
			} else {
				log.error(msg, t);
			}
		}
	}
}
