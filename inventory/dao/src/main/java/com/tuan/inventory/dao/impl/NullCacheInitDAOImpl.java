package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.NullCacheInitDAO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;

public class NullCacheInitDAOImpl extends SqlMapClientDaoSupport  implements NullCacheInitDAO {

	@Override
	public RedisInventoryDO selectRedisInventory(Long goodsId) {
		return (RedisInventoryDO) super.getSqlMapClientTemplate().
				queryForObject("selectRedisInventoryByGoodsId", goodsId);
	}

	

}
