package com.tuan.inventory.dao;

import java.util.List;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
/**
 * 初始化redis cache商品库存信息dao
 * @author henry.yu
 * @date 2014/3/11
 */
public interface NullCacheInitDAO {

	public GoodsInventoryDO selectRedisInventory(Long goodsId);
	public GoodsInventoryWMSDO selectGoodsInventoryWMS(String wmsGoodsId);
	public GoodsInventoryWMSDO selectIsOrNotGoodsWMS(Long goodsId);
	public List<GoodsInventoryDO> selectInventory4Wms(String wmsGoodsId);

}

