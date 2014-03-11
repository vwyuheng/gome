package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
/**
 * 初始化redis cache商品库存信息dao
 * @author henry.yu
 * @date 2014/3/11
 */
public interface NullCacheInitDAO {

	public RedisInventoryDO selectRedisInventory(Long goodsId);

}

