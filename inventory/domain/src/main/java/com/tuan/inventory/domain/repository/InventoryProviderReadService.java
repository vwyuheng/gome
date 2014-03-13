package com.tuan.inventory.domain.repository;

import java.util.List;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.model.GoodsSelectionRelationModel;

public interface InventoryProviderReadService {

	/**
	 * 根据选型id获取选型库存信息
	 * @param SelectionRelationId
	 * @return
	 * @throws Exception
	 */
	public GoodsSelectionRelationDO getSelectionRelationBySrId(int SelectionRelationId) throws Exception;
	/**
	 * 根据商品分店id获取fen
	 * @param SuppliersInventoryId
	 * @return
	 * @throws Exception
	 */
	public GoodsSuppliersInventoryDO getSuppliersInventoryBySiId(int SuppliersInventoryId) throws Exception;
	/**
	 * 获取商品选型列表
	 * @param selectionRelationIdList
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public List<GoodsSelectionRelationModel>  getSelectionRelationBySrIds(List<Long> selectionRelationIdList,long goodsId) throws Exception;
	/**
	 * 根据商品id获取商品库存信息
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public RedisInventoryDO getNotSeleInventory (long goodsId) throws Exception;
}
