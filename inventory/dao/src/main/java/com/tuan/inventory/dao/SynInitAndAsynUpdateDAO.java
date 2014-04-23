package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
/**
 * 同步初始化数据，异步更新数据
 * @author henry.yu
 * @date 2014/3/6
 */
public interface  SynInitAndAsynUpdateDAO {

	 int insertGoodsInventoryDO(GoodsInventoryDO goodsDO);
	 int updateGoodsInventoryDO(GoodsInventoryDO goodsDO);
	 
	 int insertGoodsSelectionDO(GoodsSelectionDO selectionDO);
	 int updateGoodsSelectionDO(GoodsSelectionDO selectionDO);
	 
	 int insertGoodsSuppliersDO(GoodsSuppliersDO suppliersDO);
	 int updateGoodsSuppliersDO(GoodsSuppliersDO suppliersDO);
	 
	 int insertGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO);
	 int updateGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO);
}

