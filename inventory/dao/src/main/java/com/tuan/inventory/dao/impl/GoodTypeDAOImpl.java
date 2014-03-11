package com.tuan.inventory.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.GoodTypeDAO;
import com.tuan.inventory.dao.data.GoodsAttributeInventoryDO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationGoodDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.OrderGoodsDO;
import com.tuan.inventory.dao.data.OrderInfoDetailDO;
import com.tuan.inventory.dao.data.WmsGoodsDO;


/**
 * 
 * @author tianzq
 * @date 2012.11.30
 *
 */
public class GoodTypeDAOImpl extends SqlMapClientDaoSupport implements GoodTypeDAO {
	@Override
	public GoodsSelectionRelationDO selectSelectionRelationBySrId(
			int selectionRelationId) {
		if(selectionRelationId<=0){
			return null;
		}
		return (GoodsSelectionRelationDO) super.getSqlMapClientTemplate().queryForObject("selectSelectionRelationBySrId", selectionRelationId);
	}

	@Override
	public GoodsSuppliersInventoryDO selectGoodsSuppliersInventoryBySiId(
			int suppliersInventoryId) {
		if(suppliersInventoryId<=0){
			return null;
		}
		return (GoodsSuppliersInventoryDO) super.getSqlMapClientTemplate().queryForObject("selectGoodsSuppliersInventoryBySiId", suppliersInventoryId);
	}
	@Override
	public void updateGoodsAttributesLeftNumber(OrderGoodsDO orderGoods) {
		getSqlMapClientTemplate().update("set_sql_model_user");
		getSqlMapClientTemplate().update("updateGoodsAttributesLeftNumberByGID", orderGoods);
		getSqlMapClientTemplate().update("set_sql_model_sys");
		
	}
	@Override
	public void updateSelectionRelationLeftNumber(
			OrderInfoDetailDO orderInfoDetail) {
		if(orderInfoDetail.getSuppliersId()!=null){
			getSqlMapClientTemplate().update("set_sql_model_user");
			getSqlMapClientTemplate().update("updateSelectionRelationLeftNumberBySID", orderInfoDetail);
			getSqlMapClientTemplate().update("set_sql_model_sys");
		}	
	}
	@Override
	public void updateSuppliersInventoryLeftNumber(
			OrderInfoDetailDO orderInfoDetail) {
		getSqlMapClientTemplate().update("set_sql_model_user");
		getSqlMapClientTemplate().update("updateSuppliersInventoryLeftNumberBySiID", orderInfoDetail);	
		getSqlMapClientTemplate().update("set_sql_model_sys");
	}	

	public void updataGoodsWmsLeftNumByID(WmsGoodsDO wmsGoods) {
		getSqlMapClientTemplate().update("set_sql_model_user");
		getSqlMapClientTemplate().update("updataGoodsWmsLeftNumByID", wmsGoods);
		getSqlMapClientTemplate().update("set_sql_model_sys");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsSelectionRelationGoodDO> selectSelectionRelationBySrIdAndGoodsId(
			long selectionRelationId, long goodsId) {
		if(goodsId <=0 ){
			return null;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (selectionRelationId > 0) {
			paramMap.put("selectionRelationId", selectionRelationId);
		}
		if (goodsId > 0) {
			paramMap.put("goodsId", goodsId);
		}
		return super.getSqlMapClientTemplate().queryForList("selectSelectionRelationBySrIdAndGoodsId", paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsSelectionRelationGoodDO> selectSelectionRelationBySrIds(List<Long> selectionRelationIdList, long goodsId){
		if(selectionRelationIdList == null || selectionRelationIdList.isEmpty()){
			return null;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("selectionRelationIdList", selectionRelationIdList);
		if (goodsId > 0) {
			paramMap.put("goodsId", goodsId);
		}
		
		return super.getSqlMapClientTemplate().queryForList("selectSelectionRelationBySrIds", paramMap);
	}
	
	@Override
	public GoodsAttributeInventoryDO getNotSeleInventory(long goodsId) {
		return (GoodsAttributeInventoryDO) super.getSqlMapClientTemplate().
				queryForObject("selectGoodsNotSelectionInventoryByGoodsId", goodsId);
	}

}
