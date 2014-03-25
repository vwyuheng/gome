package com.tuan.inventory.domain.repository.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.core.common.service.TuanServiceTemplateImpl;
import com.tuan.inventory.dao.LogOfWaterDAO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.repository.LogOfWaterHandleService;
import com.tuan.inventory.model.result.CallResult;

public class LogOfWaterHandleServiceImpl extends TuanServiceTemplateImpl implements LogOfWaterHandleService {
	private static final Log logger = LogFactory.getLog(LogOfWaterHandleServiceImpl.class);

	@Resource
	private LogOfWaterDAO logOfWaterDAO;
	
	@Override
	public CallResult<Integer> createLogOfWater(final RedisInventoryLogDO logDO)
			throws Exception {
		    TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						
						logOfWaterDAO.insertInventoryQueue(logDO);
						return TuanCallbackResult
								.success();
					}
					public TuanCallbackResult executeCheck() {
						if (logDO == null) {
							logger.error(this.getClass()+"_consumeQueue param invalid ,queueTypeEnum is null ");
							return TuanCallbackResult
									.failure(-1);
						}
						return TuanCallbackResult.success();
					}
				}, null);
		
		return new CallResult<Integer>(callBackResult.isSuccess(),
				(Integer)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());

	}

	
	
	
}
