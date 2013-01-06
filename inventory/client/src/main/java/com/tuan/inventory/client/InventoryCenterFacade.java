package com.tuan.inventory.client;

import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryQueryResult;

public interface InventoryCenterFacade {

	public CallResult<InventoryQueryResult> querySelectionRelation(final String clientIp,
			final String clientName,final long goodsId,final long SelectionRelationId);
}
