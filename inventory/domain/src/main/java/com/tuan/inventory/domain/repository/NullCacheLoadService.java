package com.tuan.inventory.domain.repository;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;


public interface NullCacheLoadService{

	public RedisGoodsSelectionRelationDO getCacheSelectionRelationInfoById(int id);
	public GoodsSelectionRelationDO getCacheSelectionRelationDOById(int id);

	public RedisGoodsSuppliersInventoryDO getCacheSuppliersInventoryInfoById(int id) throws Exception;
	public GoodsSuppliersInventoryDO getCacheSuppliersInventoryDOById(int id) throws Exception;
	/**
	 * 根据商品id获取
	 * 商品库存主体信息
	 * @param goodsId
	 * @return
	 */
	public RedisInventoryDO getRedisInventoryInfoByGoodsId(Long goodsId);
}
