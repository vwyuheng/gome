package com.tuan.inventory.service;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;
import com.tuan.inventory.model.param.CallbackParam;
import com.tuan.inventory.model.param.CreatorInventoryParam;
import com.tuan.inventory.model.param.DeleteInventoryParam;
import com.tuan.inventory.model.param.UpdateInventoryParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.wowotrace.trace.model.Message;

public interface InventoryCreateService {
	/** 
	 * �������ӿڣ����ݴ���Ĳ����������
	 * @param clientIp String �ͻ���ip��ַ
	 * @param clientName String �ͻ�������
	 * @param param UpdateInventoryParam �������������������
	 * @return InventoryCallResult �ӿڵ��ý������
	 * 
	 * @see	CreatorInventoryParam
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	InventoryCallResult createInventory(final String clientIp, final String clientName
			,final CreatorInventoryParam param,Message traceMessage);
	/**
	 * ������Ʒ���
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	@ProductCode(code = "00002", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	InventoryCallResult updateInventory(
			final String clientIp, final String clientName,final UpdateInventoryParam param,Message traceMessage);
	
	@ProductCode(code = "00003", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	InventoryCallResult callbackAckInventory(
			final String clientIp, final String clientName,final CallbackParam param,Message traceMessage);
	
	@ProductCode(code = "00004", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	InventoryCallResult adjustmentInventory(
			final String clientIp, final String clientName,final AdjustInventoryParam param,Message traceMessage);
	
	@ProductCode(code = "00005", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	InventoryCallResult adjustmentWaterflood(
			final String clientIp, final String clientName,final AdjustWaterfloodParam param,Message traceMessage);
	@ProductCode(code = "00006", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	InventoryCallResult deleteInventory(
			final String clientIp, final String clientName,final DeleteInventoryParam param,Message traceMessage);
}
