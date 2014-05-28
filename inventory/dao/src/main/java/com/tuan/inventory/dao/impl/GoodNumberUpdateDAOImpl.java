package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.GoodNumUpdateDAO;
import com.tuan.inventory.dao.data.GoodsUpdateNumberDO;


/**
 * 
 * @author zhangbo
 *
 */
public class GoodNumberUpdateDAOImpl extends SqlMapClientDaoSupport implements GoodNumUpdateDAO {
	@Override
	public void updateGoodsAttributesNumber(GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getSqlMapClientTemplate().update("set_sql_model_user");
		getSqlMapClientTemplate().update("updateGoodsAttributesNumberByGID", goodsUpdateNumberDO);
		getSqlMapClientTemplate().update("set_sql_model_sys");
		
	}
	@Override
	public void updateSelectionRelationNumber(
			GoodsUpdateNumberDO goodsUpdateNumberDO) {
			getSqlMapClientTemplate().update("set_sql_model_user");
			getSqlMapClientTemplate().update("updateSelectionRelationNumberBySID", goodsUpdateNumberDO);
			getSqlMapClientTemplate().update("set_sql_model_sys");
	}
	@Override
	public void updateSuppliersInventoryNumber(
			GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getSqlMapClientTemplate().update("set_sql_model_user");
		getSqlMapClientTemplate().update("updateSuppliersInventoryLeftNumberBySiID", goodsUpdateNumberDO);	
		getSqlMapClientTemplate().update("set_sql_model_sys");
	}	

	public void updataGoodsWmsNumByID(GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getSqlMapClientTemplate().update("set_sql_model_user");
		getSqlMapClientTemplate().update("updataGoodsWmsNumByID", goodsUpdateNumberDO);
		getSqlMapClientTemplate().update("set_sql_model_sys");
	}
	
}
