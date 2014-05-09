package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.NullCacheInitDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;

public class NullCacheInitDAOImpl extends SqlMapClientDaoSupport  implements NullCacheInitDAO {

	@Override
	public GoodsInventoryDO selectRedisInventory(Long goodsId) {
		return (GoodsInventoryDO) super.getSqlMapClientTemplate().
				queryForObject("selectRedisInventoryByGoodsId", goodsId);
	}

	@Override
	public GoodsInventoryWMSDO selectGoodsInventoryWMS(String wmsGoodsId) {
		return (GoodsInventoryWMSDO) super.getSqlMapClientTemplate().
				queryForObject("selectGoodsInventoryWMS", wmsGoodsId);
	}

	@Override
	public GoodsInventoryWMSDO selectIsOrNotGoodsWMS(Long goodsId) {
		return (GoodsInventoryWMSDO) super.getSqlMapClientTemplate().
				queryForObject("selectIsOrNotGoodsWMS", goodsId);
	}

	

}
