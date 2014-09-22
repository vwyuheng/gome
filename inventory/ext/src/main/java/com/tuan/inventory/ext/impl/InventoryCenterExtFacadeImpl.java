package com.tuan.inventory.ext.impl;

import javax.annotation.Resource;

import com.tuan.inventory.ext.InventoryCenterExtFacade;
import com.tuan.ordercenter.OrderCenterFacade;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.OrderQueryResult;

public class InventoryCenterExtFacadeImpl implements InventoryCenterExtFacade {
    @Resource 
    OrderCenterFacade  orderCenterFacade;
    
	@Override
	public CallResult<OrderQueryResult> queryOrderPayStatus(String clientName,
			String clientIp, String orderId) {
		return orderCenterFacade.queryOrderPayStatus(clientName, clientIp, orderId);
	}

	@Override
	public CallResult<OrderQueryResult> queryNupayOrderGoodsNum(
			String clientIp, String clientName, long goodsId) {
		return orderCenterFacade.queryNupayOrderGoodsNum(clientIp, clientName, goodsId);
	}

}
