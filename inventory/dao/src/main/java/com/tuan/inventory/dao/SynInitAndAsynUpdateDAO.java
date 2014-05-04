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

	void insertGoodsInventoryDO(GoodsInventoryDO goodsDO);
	void updateGoodsInventoryDO(GoodsInventoryDO goodsDO);
	int deleteGoodsInventoryDO(long goodsId);
	
	void insertGoodsSelectionDO(GoodsSelectionDO selectionDO);
	void updateGoodsSelectionDO(GoodsSelectionDO selectionDO);
	int deleteGoodsSelectionDO(long selectionId);
	 
	void insertGoodsSuppliersDO(GoodsSuppliersDO suppliersDO);
	void updateGoodsSuppliersDO(GoodsSuppliersDO suppliersDO);
	int deleteGoodsSuppliersDO(long suppliersId);
	 
	void insertGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO);
	void updateGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO);
}

