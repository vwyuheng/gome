package com.tuan.inventory.client.support;

import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;


public interface CacheDAOService {
	/**
	 * 根据商品id查询库存信息
	 * @param goodsId
	 * @return
	 */
	public GoodsInventoryModel queryGoodsInventory(Long goodsId);
	/**
	 * 根据物流商品id查询物流商品库存
	 * @param wmsGoodsId
	 * @return
	 */
	//public GoodsInventoryWMSDO queryWmsInventoryById(String wmsGoodsId);
	public GoodsBaseModel queryGoodsBaseById(Long goodsBaseId);
}
