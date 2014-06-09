package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.GoodNumUpdateDAO;
import com.tuan.inventory.dao.data.GoodsUpdateNumberDO;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;


/**
 * 
 * @author zhangbo
 *
 */
public class GoodNumberUpdateDAOImpl extends SqlMapClientDaoSupport implements GoodNumUpdateDAO {
	@Override
	public void updateGoodsAttributesNumber(GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getSqlMapClientTemplate().update("updateGoodsAttributesNumberByGID", goodsUpdateNumberDO);
		
	}
	@Override
	public void updateSelectionRelationNumber(
			GoodsUpdateNumberDO goodsUpdateNumberDO) {
			getSqlMapClientTemplate().update("updateSelectionRelationNumberBySID", goodsUpdateNumberDO);
	}
	@Override
	public void updateSuppliersInventoryNumber(
			GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getSqlMapClientTemplate().update("updateSuppliersInventoryNumberBySiID", goodsUpdateNumberDO);	
	}	

	public void updataGoodsWmsNumByID(GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getSqlMapClientTemplate().update("updataGoodsWmsNumByID", goodsUpdateNumberDO);
	}
	
	public void updataGoodsBaseNumByID(GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getSqlMapClientTemplate().update("updataGoodsBaseNumByID", goodsUpdateNumberDO);
	}
	
	public GoodsBaseInventoryDO selectGoodBaseBygoodsId(long goodsBaseId) {
		return (GoodsBaseInventoryDO) getSqlMapClientTemplate().queryForObject("selectGoodBaseBygoodsId", goodsBaseId);
	}
	
	public void updataGoodsNumByID(GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getSqlMapClientTemplate().update("updataGoodsNumByID", goodsUpdateNumberDO);
	}
}
