package com.tuan.inventory.domain.support;

import java.util.Set;

import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;

public interface BaseDAOService {
	/**
	 * 根据商品id判断库存是否已存在
	 * 已存在返回false,不存在返回true
	 * @param goodsId
	 * @return
	 */
	public boolean isExists(Long goodsId);
	/**
	 * 根据key 判断商品库存hash中是否存该field
	 * @param goodsId
	 * @param field
	 * @return
	 */
	public boolean isGoodsExists(Long goodsId,String field);
	/**
	 * 根据选型id 判断商品选型库存 hash是否存在该field
	 * @param selectionId
	 * @param field
	 * @return
	 */
	public boolean isSelectionExists(Long selectionId,String field);
	/**
	 * 根据分店id 判断商品分店库存 hash是否存在该field
	 * @param suppliesId
	 * @param field
	 * @return
	 */
	public boolean isSupplierExists(Long suppliesId,String field);
	/**
	 * 压入日志队列
	 * @param logActionDO
	 */
	public void pushLogQueues(final GoodsInventoryActionDO logActionDO);
	public void pushQueueSendMsg(final GoodsInventoryQueueDO queueDO);
	/**
	 * 保存商品库存
	 * @param inventoryInfoDO
	 */
	public void saveInventory(Long goodsId,GoodsInventoryDO inventoryInfoDO);
	/**
	 * 保存商品选型库存
	 * @param goodsId
	 * @param selectionModel
	 */
	public void saveGoodsSelectionInventory(Long goodsId, GoodsSelectionDO selectionDO);
	/**
	 * 保存商品分店库存
	 * @param goodsId
	 * @param selectionModel
	 */
	public void saveGoodsSuppliersInventory(Long goodsId, GoodsSuppliersDO suppliersDO);
	/**
	 * 根据商品id查询库存信息
	 * @param goodsId
	 * @return
	 */
	public GoodsInventoryDO queryGoodsInventory(Long goodsId);
	/**
	 * 根据选型id查询商品选型库存
	 * @param selectionId
	 * @return
	 */
	public GoodsSelectionDO querySelectionRelationById(Long selectionId);
	/**
	 * 根据分店id查询商品分店库存
	 * @param suppliersId
	 * @return
	 */
	public GoodsSuppliersDO querySuppliersInventoryById(Long suppliersId);
	
	public Long updateGoodsInventory(Long goodsId,int num);
	public Long updateSelectionInventory(Long selectionId,int num);
	public Long updateSuppliersInventory(Long suppliersId,int num);
	public void markQueueStatus(String key,  int upStatusNum);
	
	public GoodsInventoryQueueDO queryInventoryQueueDO(String key);
	
	public Long adjustGoodsWaterflood(Long goodsId,int num);
	public Long adjustSelectionWaterflood(Long selectionId,int num);
	public Long adjustSuppliersWaterflood(Long suppliersId,int num);
	
	public Long deleteGoodsInventory(Long goodsId);
	public Long deleteSelectionInventory(Long selectionId);
	public Long deleteSuppliersInventory(Long suppliersId);
	
	public void lremLogQueue(final GoodsInventoryActionDO logActionDO);
	
	public Set<String> queryGoodsSelectionRelation(Long goodsId);
	public Set<String> queryGoodsSuppliersRelation(Long goodsId);
	public Set<String> queryInventoryQueueListByStatus (final Double status);
	
	public String queryLastIndexGoodsInventoryAction ();
	public Long deleteQueueMember(String key);
	//public GoodsInventoryDO queryGoodsInventory(Long goodsId) 
}
