package com.tuan.inventory.service;

import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryQueryResult;

public interface GoodSeletionQueryService {
	/**
	 * �����Ϣ��ѯ
	 * @param clientIp
	 * @param clientName
	 * @param goodsId ��Ʒid
	 * @param SelectionRelationId ѡ��id
	 * @return
	 */
	public CallResult<InventoryQueryResult> querySelectionRelation(final String clientIp,
			final String clientName,final long goodsId,final long SelectionRelationId);
}
