package com.gome.service;

import com.gome.model.param.Param;
import com.gome.model.result.CallResult;


public interface DemoService {
	/** 
	 * 
	 * @param clientIp String 客户端ip地址
	 * @param clientName String 客户端名称
	 * @param param Param 创建库存所需参数类对象
	 * @return CallResult 接口调用结果对象
	 * 
	 * @see	Param
	 */
	CallResult<Boolean> demoMethod(final String clientIp, final String clientName
			,final Param param);
	
}
