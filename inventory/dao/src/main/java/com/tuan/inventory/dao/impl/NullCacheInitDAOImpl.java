package com.tuan.inventory.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.NullCacheInitDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.WmsIsBeDeliveryDO;

public class NullCacheInitDAOImpl extends SqlMapClientDaoSupport  implements NullCacheInitDAO {

	@Override
	public GoodsInventoryDO selectRedisInventory(Long goodsId) {
		return (GoodsInventoryDO) super.getSqlMapClientTemplate().
				queryForObject("selectRedisInventoryByGoodsId", goodsId);
	}

	@Override
	public GoodsInventoryWMSDO selectGoodsInventoryWMS(String wmsGoodsId,int isBeDelivery) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(wmsGoodsId)) {
			paramMap.put("wmsGoodsId", wmsGoodsId);
		}
		
		paramMap.put("isBeDelivery", isBeDelivery);
		
		return (GoodsInventoryWMSDO) super.getSqlMapClientTemplate().
				queryForObject("selectGoodsInventoryWMS", paramMap);
	}

	@Override
	public GoodsInventoryWMSDO selectIsOrNotGoodsWMS(Long goodsId) {
		return (GoodsInventoryWMSDO) super.getSqlMapClientTemplate().
				queryForObject("selectIsOrNotGoodsWMS", goodsId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsInventoryDO> selectInventory4Wms(String wmsGoodsId) {
		return  super.getSqlMapClientTemplate().queryForList("selectInventoryListByWmsGoodsId", wmsGoodsId);
	}

	@Override
	public WmsIsBeDeliveryDO selectWmsIsBeDeliveryResult(String wmsGoodsId) {
		return  (WmsIsBeDeliveryDO) super.getSqlMapClientTemplate().
				queryForObject("selectWmsIsBeDeliveryResult", wmsGoodsId);
	}

	

}
