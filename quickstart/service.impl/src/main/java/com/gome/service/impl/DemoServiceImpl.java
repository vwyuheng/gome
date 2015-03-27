package com.gome.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gome.core.common.service.GomeCallbackResult;
import com.gome.core.common.service.GomeServiceCallback;
import com.gome.core.common.service.GomeServiceTemplateImpl;
import com.gome.domain.DemoDomain;
import com.gome.domain.lang.LogModel;
import com.gome.domain.repository.DemoDomainRepository;
import com.gome.model.param.Param;
import com.gome.model.result.CallResult;
import com.gome.service.DemoService;

public class DemoServiceImpl  extends GomeServiceTemplateImpl implements DemoService {
	private static final Log log = LogFactory.getLog("xx");
	
	@Resource
	private DemoDomainRepository demoDomainRepository;

	@Override
	public CallResult<Boolean> demoMethod(String clientIp, String clientName, Param param) {
		final LogModel lm = LogModel.newLogModel("");
		//构建领域对象
		final DemoDomain demoDomain = new DemoDomain(clientIp, clientName, param, lm);
		//注入仓储对象及必须的对象
		//demoDomain.set

		
		GomeCallbackResult result = super.execute(new GomeServiceCallback(){
			@Override
			public GomeCallbackResult executeParamsCheck() {
				//TODO 参数检查
				demoDomain.checkParam();
				return null; //避免报错
			}

			@Override
			public GomeCallbackResult executeBusiCheck() {
				
				//ResultEnum resEnum = demoDomain.busiCheck();
				
					//return GomeCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
					//业务检查
					return null;
				
			}

			@Override
			public GomeCallbackResult executeAction() {
				//CreateInventoryResultEnum resultEnum = demoDomain.busAction();
				return null;
			}

			@Override
			public void executeAfter() {
				//事务提交后扫尾处理，比如发消息
				//demoDomain.
			}
		});
		
		return new CallResult<Boolean>(result.getResultCode(), 
				"",null);
		
	}
}
