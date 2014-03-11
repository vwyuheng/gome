package com.tuan.inventory.domain.repository;

import java.util.List;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.model.OrderGoodsSelectionModel;

public interface InventoryDeductReadWriteService {

	/**
	 * 新增库存信息
	 * @param goodsId[商品id],RedisInventoryDO[商品库存主体信息],
	 * List<RedisGoodsSelectionRelationDO>[商品选型],List<RedisGoodsSuppliersInventoryDO>[商品分店,保留暂不用]
	 * @return
	 */
	public boolean createInventory(long goodsId,RedisInventoryDO riDO,List<RedisGoodsSelectionRelationDO> rgsrList,List<RedisGoodsSuppliersInventoryDO> rgsiList) throws Exception;
	/**
	 * 更新库存
	 * @param orderId
	 * @param goodsId
	 * @param pindaoId
	 * @param num
	 * @param limitStorage
	 * @param goodsSelectionList
	 * @return
	 * @throws Exception
	 */
	public boolean updateInventory(long orderId,long goodsId, int pindaoId, int num,int limitStorage,
			List<OrderGoodsSelectionModel> goodsSelectionList) throws Exception;
	/**
	 * 新增库存相关的某一个域的值,原子操作
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String insertSingleInventory(long goodsId,String field,String value) throws Exception;
	
	/**
	 * 更新库存相关的某一个域的值,原子操作
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String updateSingleInventory(long goodsId,String field,String value) throws Exception;
	/**
	 * 删除商品时清除该商品的库存信息
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public boolean deleteInventory(long goodsId,List<OrderGoodsSelectionModel> goodsSelectionList) throws Exception;
}
