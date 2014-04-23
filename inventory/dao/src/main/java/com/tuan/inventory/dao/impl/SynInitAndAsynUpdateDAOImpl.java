package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.SynInitAndAsynUpdateDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;

public class SynInitAndAsynUpdateDAOImpl  extends SqlMapClientDaoSupport  implements SynInitAndAsynUpdateDAO {

	@Override
	public int insertGoodsInventoryDO(GoodsInventoryDO goodsDO) {
		return (Integer) super.getSqlMapClientTemplate().insert("insertSelectionRelation", goodsDO);
	}

	@Override
	public int updateGoodsInventoryDO(GoodsInventoryDO goodsDO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertGoodsSelectionDO(GoodsSelectionDO selectionDO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateGoodsSelectionDO(GoodsSelectionDO selectionDO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertGoodsSuppliersDO(GoodsSuppliersDO suppliersDO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateGoodsSuppliersDO(GoodsSuppliersDO suppliersDO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO) {
		// TODO Auto-generated method stub
		return 0;
	}


}
