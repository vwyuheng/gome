package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
/**
 * ��ʼ��redis cache��Ʒ�����Ϣdao
 * @author henry.yu
 * @date 2014/3/11
 */
public interface NullCacheInitDAO {

	public RedisInventoryDO selectRedisInventory(Long goodsId);

}

