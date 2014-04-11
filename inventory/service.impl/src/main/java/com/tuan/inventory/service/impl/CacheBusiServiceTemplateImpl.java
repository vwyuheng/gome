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
 * ���ҵ����ģ��ʵ����
 * 
 * @author henry.yu
 * @date 2012.10.25
 */
public class CacheBusiServiceTemplateImpl implements InventoryServiceTemplate {

	private final static Log logger = LogFactory
			.getLog(CacheBusiServiceTemplateImpl.class);
	/**
	 * ����д����ģ��
	 */
	public TuanCallbackResult execute(final InventoryUpdateServiceCallback action) {

		if (logger.isDebugEnabled()) {
			logger.debug("����ģ�巽����ʼ����");
		}
		TuanCallbackResult result = TuanCallbackResult.success();
		try {
			// ҵ����
			result = action.executeParamsCheck();

			if (result.isSuccess()) {

				result = action.executeBusiCheck();

				if (result.isSuccess()) {
					// 3. �ص�ҵ���߼�
					// 3.1 ͨ��annotation��ʵ��ĳЩoption���͵���չ
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
				logger.debug("�����˳�ģ�巽��");
			}

		} catch (TuanServiceException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("�쳣�˳�ģ�巽��A��", e);
			}
			result = TuanCallbackResult.failure(e.getErrorCode(), e);

		} catch (TuanRuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("�쳣�˳�ģ�巽��B��", e);
			}
			result = TuanCallbackResult.failure(e.getErrorCode(), e);

		} catch (Throwable e) {

			if (logger.isErrorEnabled()) {
				logger.error("�쳣�˳�ģ�巽��C��", e);
			}
			result = TuanCallbackResult.failure(
					TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);

		} finally {

		}
		return result;
	}
	/**
	 * ��ѯ��ģ��
	 * @param action
	 * @return
	 */
	public TuanCallbackResult execute(final InventoryQueryServiceCallback action) {

		if (logger.isDebugEnabled()) {
			logger.debug("����ģ�巽����ʼ����");
		}
		TuanCallbackResult result = TuanCallbackResult.success();
		try {
			// Ԥ����
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
				logger.debug("�����˳�ģ�巽��");
			}

		} catch (TuanServiceException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("�쳣�˳�ģ�巽��A��", e);
			}
			result = TuanCallbackResult.failure(e.getErrorCode(), e);

		} catch (TuanRuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("�쳣�˳�ģ�巽��B��", e);
			}
			result = TuanCallbackResult.failure(e.getErrorCode(), e);

		} catch (Throwable e) {

			if (logger.isErrorEnabled()) {
				logger.error("�쳣�˳�ģ�巽��C��", e);
			}
			result = TuanCallbackResult.failure(
					TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);

		}
		return result;
	}

	/**
	 * ��չ�㣺ģ���ṩ������ͬ����ҵ����<b>������</b>������չ��һ����
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

}
