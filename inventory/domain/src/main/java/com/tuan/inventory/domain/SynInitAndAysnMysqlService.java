package com.tuan.inventory.domain;

import java.util.List;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.model.result.CallResult;

public interface SynInitAndAysnMysqlService {

	public CallResult<GoodsInventoryDO> saveGoodsInventory(GoodsInventoryDO inventoryInfoDO) throws Exception ;
	public CallResult<List<GoodsSelectionDO>> saveBatchGoodsSelection(long goodsId,List<GoodsSelectionDO> selectionInventoryList) throws Exception ;
	public CallResult<List<GoodsSuppliersDO>> saveBatchGoodsSuppliers(long goodsId,List<GoodsSuppliersDO> suppliersInventoryList) throws Exception ;
	
	
	public CallResult<GoodsInventoryDO> updateGoodsInventory(GoodsInventoryDO goodsDO) throws Exception;
	public CallResult<GoodsSelectionDO> updateGoodsSelection(GoodsSelectionDO selectionDO) throws Exception;
	public CallResult<GoodsSuppliersDO> updateGoodsSuppliers(GoodsSuppliersDO suppliersDO) throws Exception;
	
	public CallResult<List<GoodsSelectionDO>> updateBatchGoodsSelection(Long goodsId, List<GoodsSelectionDO> selectionDOList) throws Exception;
	public CallResult<List<GoodsSuppliersDO>> updateBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) throws Exception;
	
}
