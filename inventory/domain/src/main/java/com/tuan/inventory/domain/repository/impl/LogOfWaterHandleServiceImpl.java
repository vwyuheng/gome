package com.tuan.inventory.domain.repository.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.tuan.core.common.lang.TuanRuntimeException;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.core.common.service.TuanServiceTemplateImpl;
import com.tuan.inventory.dao.LogOfWaterDAO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.repository.LogOfWaterHandleService;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.QueueConstant;
import com.tuan.inventory.model.result.CallResult;

public class LogOfWaterHandleServiceImpl  extends TuanServiceTemplateImpl implements LogOfWaterHandleService {
	private static final Log logger = LogFactory.getLog(LogOfWaterHandleServiceImpl.class);
	@Resource
	private LogOfWaterDAO logOfWaterDAO;
	
	@Override
	public CallResult<RedisInventoryLogDO> createLogOfWater(final RedisInventoryLogDO logDO)
			throws Exception {
		
		    TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						
						try {
							logOfWaterDAO.insertInventoryQueue(logDO);
						} catch (Exception e) {
							logger.error(
									"LogOfWaterHandleServiceImpl.createLogOfWater error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + logDO.getId()
												+ "' for key 'id'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"LogOfWaterHandleServiceImpl.createLogOfWater error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								logDO);
					}
					public TuanCallbackResult executeCheck() {
						
						if (logDO == null) {
							 logger.error(this.getClass()+"_create param invalid ,RedisInventoryLogDO is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<RedisInventoryLogDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(RedisInventoryLogDO)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());

	}

	
	
	
}
