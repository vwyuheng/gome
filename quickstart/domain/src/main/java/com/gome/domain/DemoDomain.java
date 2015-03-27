package com.gome.domain;

import com.gome.domain.lang.LogModel;
import com.gome.domain.parent.AbstractDomain;
import com.gome.model.param.Param;

public class DemoDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private Param param;
	
	public DemoDomain(String clientIp, String clientName,Param param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	// 初始化参数
	private void fillParam() {
		

	}
	public void preHandler() {
		
	}

	// 业务检查
	public void busiCheck() {
	
	}

	//业务处理
	public void doBusHandler() {
		
	}

	
	/**
	 * 参数检查
	 * 
	 * @return
	 */
	public void checkParam() {
		
	}

	

}
