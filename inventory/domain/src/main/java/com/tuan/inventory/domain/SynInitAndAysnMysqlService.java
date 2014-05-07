package com.tuan.inventory.domain;

import java.util.List;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.model.result.CallResult;

public interface SynInitAndAysnMysqlService {

	public CallResult<Boolean> saveGoodsInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) throws Exception ;
	public CallResult<List<GoodsSelectionDO>> saveBatchGoodsSelection(long goodsId,List<GoodsSelectionDO> selectionInventoryList) throws Exception ;
	public CallResult<List<GoodsSuppliersDO>> saveBatchGoodsSuppliers(long goodsId,List<GoodsSuppliersDO> suppliersInventoryList) throws Exception ;
	
	public CallResult<Boolean> updateGoodsInventory(final long goodsId,final GoodsInventoryDO goodsDO,final List<GoodsSelectionDO> selectionInventoryList,final List<GoodsSuppliersDO> suppliersInventoryList) throws Exception;
	public CallResult<GoodsInventoryDO> updateGoodsInventory(GoodsInventoryDO goodsDO) throws Exception;
	public CallResult<GoodsSelectionDO> updateGoodsSelection(GoodsInventoryDO goodsDO,GoodsSelectionDO selectionDO) throws Exception;
	public CallResult<GoodsSuppliersDO> updateGoodsSuppliers(GoodsSuppliersDO suppliersDO) throws Exception;
	
	public CallResult<List<GoodsSelectionDO>> updateBatchGoodsSelection(Long goodsId, List<GoodsSelectionDO> selectionDOList) throws Exception;
	public CallResult<List<GoodsSuppliersDO>> updateBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) throws Exception;
	
	
	//查询：
	public CallResult<GoodsInventoryDO> selectGoodsInventoryByGoodsId(long goodsId);
	public  CallResult<List<GoodsSelectionDO>> selectGoodsSelectionListByGoodsId(long goodsId);
	public  CallResult<List<GoodsSuppliersDO>> selectGoodsSuppliersListByGoodsId(long goodsId);
	
	//删除
	public CallResult<Integer> deleteGoodsInventory(long goodsId) throws Exception;
	public CallResult<List<GoodsSelectionDO>> deleteBatchGoodsSelection(List<GoodsSelectionDO> selectionDOList) throws Exception;
	public CallResult<List<GoodsSuppliersDO>> deleteBatchGoodsSuppliers(List<GoodsSuppliersDO> suppliersDOList) throws Exception;
	
	
}
