package com.tuan.inventory.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.core.common.service.TuanServiceTemplateImpl;
import com.tuan.inventory.domain.LogQueueDomain;
import com.tuan.inventory.domain.repository.LogQueueDomainRepository;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.service.LogOfWaterHandleService;

public class LogOfWaterHandleServiceImpl  extends TuanServiceTemplateImpl implements LogOfWaterHandleService {
	private static final Log logger = LogFactory.getLog(LogOfWaterHandleServiceImpl.class);
	@Resource
	private LogQueueDomainRepository logQueueDomainRepository;
	
	@Override
	public CallResult<GoodsInventoryActionModel> createLogOfWater(final GoodsInventoryActionModel logModel)
			throws Exception {
		
		    TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						LogQueueDomain logDomain = logQueueDomainRepository.createQueueDomain(logModel);
						logQueueDomainRepository.saveLogOfWater(logDomain);
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								logModel);
					}
					public TuanCallbackResult executeCheck() {
						if (logModel == null) {
							 logger.error(this.getClass()+"_create param invalid ,RedisInventoryLogDO is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<GoodsInventoryActionModel>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(GoodsInventoryActionModel)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());

	}

	
	
	
}
