package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.SynInitAndAsynUpdateDAO;
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


}
