package com.tuan.inventory.client.impl;

import com.tuan.inventory.client.InventoryCenterFacade;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryQueryResult;
import com.tuan.inventory.service.GoodSeletionQueryService;

public class InventoryCenterFacadeImpl implements InventoryCenterFacade {

	private GoodSeletionQueryService goodSeletionQueryService;

	@Override
	public CallResult<InventoryQueryResult> querySelectionRelation(final String clientIp,
			final String clientName,final long goodsId,final long SelectionRelationId) {
		return goodSeletionQueryService.querySelectionRelation(clientIp, clientName, goodsId, SelectionRelationId);
	}
}
