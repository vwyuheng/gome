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
		
		/*GomeCallbackResult callBackResult = super.execute(
				new GomeServiceCallback() {
					public GomeCallbackResult executeAction() {
						try {
							//TODO 业务逻辑
						} catch (Exception e) {
							//异常处理
						}
						return null;
					}
					public GomeCallbackResult executeCheck() {
						//TODO
						return null;
					}
				}, null);
		
		//封装返回对象
		return new InventoryCallResult(callBackResult.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);*/
		
		
		long startTime = System.currentTimeMillis();
		String method = "DemoServiceImpl.demoMethod";
		final LogModel lm = LogModel.newLogModel("");
		
		log.info(lm.toJson(false));
		
		//构建领域对象
		final DemoDomain demoDomain = new DemoDomain(clientIp, clientName, param, lm);
		//注入仓储对象及必须的对象
		//demoDomain.set

		
		GomeCallbackResult result = super.execute(new GomeServiceCallback(){
			@Override
			public GomeCallbackResult executeParamsCheck() {
				//TODO 参数检查
				return null; //避免报错
			}

			@Override
			public GomeCallbackResult executeBusiCheck() {
				
				//ResultEnum resEnum = demoDomainRepository.busiCheck();
				
					//return GomeCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
					//业务检查
					return null;
				
			}

			@Override
			public GomeCallbackResult executeAction() {
				//CreateInventoryResultEnum resultEnum = demoDomainRepository.busAction();
				return null;
			}

			@Override
			public void executeAfter() {
				//事务提交后扫尾处理，比如发消息
			}
		});
		
		return new CallResult(result.getResultCode(), 
				"",null);
		
	}
}
