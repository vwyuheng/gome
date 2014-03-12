package com.tuan.inventory.domain.repository;

import java.util.List;
import java.util.Map;

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
	public Map<String,String> updateInventory(long orderId,long goodsId, int pindaoId, int num,int limitStorage,
			List<OrderGoodsSelectionModel> goodsSelectionList,Long userId,String system,String clientIp) throws Exception;
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
	/**
	 * 库存回调确认接口
	 * 当调用方对库存的操作一切正常时 确认参数传1，当调用方发生异常时 在catch到异常时回调传确认参数为5
	 * @param ack 确认参数 1：正常 5：异常
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean inventoryCallbackAck(String ack,String key) throws Exception;
}
