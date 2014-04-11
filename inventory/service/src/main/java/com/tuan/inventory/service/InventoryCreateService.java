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
	 * 创建库存接口，根据传入的参数创建库存
	 * @param clientIp String 客户端ip地址
	 * @param clientName String 客户端名称
	 * @param param UpdateInventoryParam 创建库存所需参数类对象
	 * @return InventoryCallResult 接口调用结果对象
	 * 
	 * @see	CreatorInventoryParam
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel=ProductLogLevelEnum.INFO)
	InventoryCallResult createInventory(final String clientIp, final String clientName
			,final CreatorInventoryParam param,Message traceMessage);
	/**
	 * 更新商品库存
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
