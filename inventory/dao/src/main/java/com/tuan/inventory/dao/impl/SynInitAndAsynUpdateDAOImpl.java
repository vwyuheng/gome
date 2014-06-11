package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.SynInitAndAsynUpdateDAO;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;

public class SynInitAndAsynUpdateDAOImpl  extends SqlMapClientDaoSupport  implements SynInitAndAsynUpdateDAO {

	@Override
	public void insertGoodsInventoryDO(GoodsInventoryDO goodsDO) {
		 super.getSqlMapClientTemplate().insert("insertGoodsInventory", goodsDO);
	}

	@Override
	public void updateGoodsInventoryDO(GoodsInventoryDO goodsDO) {
		super.getSqlMapClientTemplate().update("updateGoodsInventory", goodsDO);
	}

	@Override
	public void insertGoodsSelectionDO(GoodsSelectionDO selectionDO) {
		 super.getSqlMapClientTemplate().insert("insertGoodsSelection", selectionDO);
	}

	@Override
	public void updateGoodsSelectionDO(GoodsSelectionDO selectionDO) {
		super.getSqlMapClientTemplate().update("updateGoodsSelection", selectionDO);
	}

	@Override
	public void insertGoodsSuppliersDO(GoodsSuppliersDO suppliersDO) {
		 super.getSqlMapClientTemplate().insert("insertGoodsSuppliers", suppliersDO);
	}

	@Override
	public void updateGoodsSuppliersDO(GoodsSuppliersDO suppliersDO) {
		super.getSqlMapClientTemplate().update("updateGoodsSuppliers", suppliersDO);
		
	}

	@Override
	public void insertGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO) {
	
		 super.getSqlMapClientTemplate().insert("insertGoodsInventoryWMS", wmsDO);
	}

	@Override
	public void updateGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO) {
		super.getSqlMapClientTemplate().update("updateGoodsInventoryWMS", wmsDO);
		
	}

	@Override
	public int deleteGoodsInventoryDO(long goodsId) {
		 return super.getSqlMapClientTemplate().delete("deleteGoodsInventory", goodsId);
		
	}

	@Override
	public int deleteGoodsSelectionDO(long selectionId) {
		return super.getSqlMapClientTemplate().delete("deleteGoodsSelection", selectionId);
		
	}

	@Override
	public int deleteGoodsSuppliersDO(long suppliersId) {
		return super.getSqlMapClientTemplate().delete("deleteGoodsSuppliers", suppliersId);
		
	}
	//更新物流选型库存 
	@Override
	public void updateGoodsSelectionWmsDO(GoodsWmsSelectionResult selection) {
		super.getSqlMapClientTemplate().update("adjustGoodsWmsSelection", selection);
	}

	@Override
	public GoodsInventoryDO selectGoodsInventoryDO(long goodsId) {
		return (GoodsInventoryDO) super.getSqlMapClientTemplate().queryForObject("selectInventoryAttributeBygoodsId", goodsId);
	}

	@Override
	public GoodsSelectionDO selectGoodsSelectionDO(long selectionId) {
		return (GoodsSelectionDO) super.getSqlMapClientTemplate().queryForObject("selectSelectionInventoryBySelId", selectionId);
	}

	@Override
	public GoodsSuppliersDO selectGoodsSuppliersDO(long suppliersId) {
		return (GoodsSuppliersDO) super.getSqlMapClientTemplate().queryForObject("selectSuppliersInventoryBySuppId", suppliersId);
	}

	@Override
	public GoodsInventoryWMSDO selectGoodsInventoryWMSDO(String wmsGoodsId) {
		return (GoodsInventoryWMSDO) super.getSqlMapClientTemplate().queryForObject("selectWmsInventoryByWmsGoodsId", wmsGoodsId);
	}

	@Override
	public void insertGoodsBaseInventoryDO(GoodsBaseInventoryDO baseInventoryDO) {
		 super.getSqlMapClientTemplate().insert("insertGoodsBaseInventory", baseInventoryDO);
	}
	
	@Override
	public void updateGoodsBaseInventoryDO(GoodsBaseInventoryDO baseInventoryDO) {
		 super.getSqlMapClientTemplate().update("updataGoodsBaseNumByID", baseInventoryDO);
	}
	
	@Override
	public GoodsBaseInventoryDO selectGoodBaseBygoodsId(long goodsBaseId) {
		return (GoodsBaseInventoryDO) getSqlMapClientTemplate().queryForObject("selectGoodBaseBygoodsId", goodsBaseId);
	}

	@Override
	public GoodsBaseInventoryDO selectInventoryBase4Init(long goodsBaseId) {
		return (GoodsBaseInventoryDO) getSqlMapClientTemplate().queryForObject("selectInventoryBase4Init", goodsBaseId);
	}
	
}
