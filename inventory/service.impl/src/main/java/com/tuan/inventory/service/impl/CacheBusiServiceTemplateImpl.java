package com.tuan.inventory.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.core.common.lang.TuanRuntimeException;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceConstants;
import com.tuan.core.common.service.TuanServiceException;
import com.tuan.inventory.service.InventoryQueryServiceCallback;
import com.tuan.inventory.service.InventoryUpdateServiceCallback;
import com.tuan.inventory.service.InventoryServiceTemplate;

/**
 * 库存业务处理模板实现类
 * 
 * @author henry.yu
 * @date 2012.10.25
 */
public class CacheBusiServiceTemplateImpl implements InventoryServiceTemplate {

	private final static Log logger = LogFactory
			.getLog(CacheBusiServiceTemplateImpl.class);
	/**
	 * 更新写入用模板
	 */
	public TuanCallbackResult execute(final InventoryUpdateServiceCallback action) {

		if (logger.isDebugEnabled()) {
			logger.debug("进入模板方法开始处理");
		}
		TuanCallbackResult result = TuanCallbackResult.success();
		try {
			// 业务检查
			result = action.executeParamsCheck();

			if (result.isSuccess()) {

				result = action.executeBusiCheck();

				if (result.isSuccess()) {
					// 3. 回调业务逻辑
					// 3.1 通过annotation来实现某些option类型的扩展
					TuanCallbackResult iNresult = action.executeAction();
					if (null == iNresult) {
						throw new TuanServiceException(
								TuanServiceConstants.SERVICE_NO_RESULT);
					}
					if (iNresult.isFailure()) {
						// status.setRollbackOnly();
						return iNresult;
					}
					templateExtensionAfterTransaction(result, action);
					return iNresult;

				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("正常退出模板方法");
			}

		} catch (TuanServiceException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("异常退出模板方法A点", e);
			}
			result = TuanCallbackResult.failure(e.getErrorCode(), e);

		} catch (TuanRuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("异常退出模板方法B点", e);
			}
			result = TuanCallbackResult.failure(e.getErrorCode(), e);

		} catch (Throwable e) {

			if (logger.isErrorEnabled()) {
				logger.error("异常退出模板方法C点", e);
			}
			result = TuanCallbackResult.failure(
					TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);

		} finally {

		}
		return result;
	}
	/**
	 * 
     * 处理redis连接的,暂未注入数据源
     *
	 * 查询用模板
	 * @param action
	 * @return
	 */
	public TuanCallbackResult execute(final InventoryQueryServiceCallback action) {

		if (logger.isDebugEnabled()) {
			logger.debug("进入模板方法开始处理");
		}
		TuanCallbackResult result = TuanCallbackResult.success();
		try {
			// 预处理
			result = action.preHandler();

			if (result.isSuccess()) {

				result = action.doWork();

				if (null == result) {
					throw new TuanServiceException(
							TuanServiceConstants.SERVICE_NO_RESULT);
				}
				if (result.isFailure()) {
					return result;
				}

			}
			if (logger.isDebugEnabled()) {
				logger.debug("正常退出模板方法");
			}

		} catch (TuanServiceException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("异常退出模板方法A点", e);
			}
			result = TuanCallbackResult.failure(e.getErrorCode(), e);

		} catch (TuanRuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("异常退出模板方法B点", e);
			}
			result = TuanCallbackResult.failure(e.getErrorCode(), e);

		} catch (Throwable e) {

			if (logger.isErrorEnabled()) {
				logger.error("异常退出模板方法C点", e);
			}
			result = TuanCallbackResult.failure(
					TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);

		}
		return result;
	}

	/**
	 * 扩展点：模板提供的允许不同类型业务在<b>事务内</b>进行扩展的一个点
	 * 
	 * @param serviceContext
	 * @param domain
	 */
	protected void templateExtensionAfterTransaction(TuanCallbackResult result,
			final InventoryUpdateServiceCallback action) {
		// DUMY
		try {
			action.executeAfter();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Execute error after transaction !", e);
			}
		}
	}

	protected void templateExtensionAfterExecute(TuanCallbackResult result,
			final InventoryUpdateServiceCallback action) {
		// DUMY
		try {
			action.executeAfter();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Extension Execute error !", e);
			}
		}
	}
	@Override
	public TuanCallbackResult initQuery(InventoryQueryServiceCallback action) {
		// TODO 无用只是为了保证不出错,真正的处理在InventoryServiceTemplateImpl
		return null;
	}

}
