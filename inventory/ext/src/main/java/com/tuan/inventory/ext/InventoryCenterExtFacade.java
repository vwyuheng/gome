package com.tuan.inventory.ext;

import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.OrderQueryResult;

public interface InventoryCenterExtFacade {

	public CallResult<OrderQueryResult> queryOrderPayStatus(String clientName, String clientIp, String orderId);
	
	public CallResult<OrderQueryResult> queryNupayOrderGoodsNum(final String clientIp, final String clientName,
			final long goodsId) ;
}
