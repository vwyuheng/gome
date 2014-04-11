package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.NullCacheInitDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;

public class NullCacheInitDAOImpl extends SqlMapClientDaoSupport  implements NullCacheInitDAO {

	@Override
	public GoodsInventoryDO selectRedisInventory(Long goodsId) {
		return (GoodsInventoryDO) super.getSqlMapClientTemplate().
				queryForObject("selectRedisInventoryByGoodsId", goodsId);
	}

	

}
