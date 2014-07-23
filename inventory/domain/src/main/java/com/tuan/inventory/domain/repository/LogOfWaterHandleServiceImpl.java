package com.tuan.inventory.domain.repository;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.core.common.service.TuanServiceTemplateImpl;
import com.tuan.inventory.domain.LogOfWaterHandleService;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.result.CallResult;

public class LogOfWaterHandleServiceImpl  extends TuanServiceTemplateImpl implements LogOfWaterHandleService {
	private static final Log logger = LogFactory.getLog("INVENTORY.JOB.LOG");
	@Resource
	private LogQueueDomainRepository logQueueDomainRepository;
	
	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsInventoryActionModel>> createLogOfWater(final List<GoodsInventoryActionModel> logList)
			throws Exception {
		
		    TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						//LogQueueDomain logDomain = logQueueDomainRepository.createQueueDomain(logModel);
						logQueueDomainRepository.saveLogOfWater(ObjectUtils.toDOList(logList));
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								logList);
					}
					public TuanCallbackResult executeCheck() {
						if (CollectionUtils.isEmpty(logList)) {
							 logger.error(this.getClass()+"_create param invalid ,logList is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<List<GoodsInventoryActionModel>>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsInventoryActionModel>)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());

	}

	
	
	
}
