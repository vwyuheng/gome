package com.tuan.inventory.domain.support.logs;

public class LogEnv {
	private String logConf;
	private boolean isDefault=true;
	private LogEnv(){
		
	}
	private static class SingletonEnvHolder {
		private static LogEnv instance = new LogEnv();
		}

	public static LogEnv getEnvInstance() {
		return SingletonEnvHolder.instance;
		
	}
	public void setLogConf(String logConf){
		this.logConf=logConf;
		this.isDefault=false;
	}
	public String getLogConf() {
		return logConf;
	}
	public boolean isDefault() {
		return isDefault;
	}
	
}
