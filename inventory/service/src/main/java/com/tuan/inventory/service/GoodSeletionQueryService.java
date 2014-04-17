package com.tuan.inventory.service;

import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryQueryResult;

public interface GoodSeletionQueryService {
	/**
	 * 库存信息查询
	 * @param clientIp
	 * @param clientName
	 * @param goodsId 商品id
	 * @param SelectionRelationId 选型id
	 * @return
	 */
	public CallResult<InventoryQueryResult> querySelectionRelation(final String clientIp,
			final String clientName,final long goodsId,final long SelectionRelationId);
}
