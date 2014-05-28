package com.tuan.inventory.domain;

import java.util.List;
import java.util.Map;

import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.dao.data.redis.WmsIsBeDeliveryDO;
import com.tuan.inventory.model.result.CallResult;

public interface SynInitAndAysnMysqlService {
	public CallResult<Boolean> batchUpdateGoodsWms(final GoodsInventoryWMSDO wmsDO,final List<GoodsInventoryDO> wmsInventoryList,final List<GoodsWmsSelectionResult> selectionList) throws Exception;
	//public CallResult<Boolean> updateBatchGoodsWms(final GoodsInventoryWMSDO wmsDO,final List<GoodsSelectionDO> selectionList) throws Exception;
	public CallResult<Boolean> saveGoodsWmsInventory(final GoodsInventoryWMSDO wmsDO,final List<GoodsSelectionDO> selectionList) throws Exception ;
	public CallResult<Boolean> saveGoodsWmsInventory(long goodsId,final GoodsInventoryWMSDO wmsDO,final List<GoodsSelectionDO> selectionList) throws Exception ;
	public CallResult<Boolean> saveGoodsWmsInventory(long goodsId,final GoodsInventoryWMSDO wmsDO,final List<GoodsInventoryDO> wmsInventoryList,final List<GoodsSelectionDO> selectionList) throws Exception ;
	public CallResult<Boolean> saveGoodsInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) throws Exception ;
	public CallResult<Boolean> saveGoodsInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList,GoodsInventoryWMSDO wmsInventory,GoodsInventoryWMSDO wmsInventory4wmsGoodsId) throws Exception ;
	public CallResult<List<GoodsSelectionDO>> saveBatchGoodsSelection(long goodsId,List<GoodsSelectionDO> selectionInventoryList) throws Exception ;
	public CallResult<List<GoodsSuppliersDO>> saveBatchGoodsSuppliers(long goodsId,List<GoodsSuppliersDO> suppliersInventoryList) throws Exception ;
	
	public CallResult<Boolean> saveGoodsSuppliers(long goodsId,GoodsSuppliersDO suppliersDO) throws Exception ;
	
	public CallResult<Boolean> updateGoodsInventory(long goodsId,final GoodsInventoryDO goodsDO,final List<GoodsSelectionDO> selectionInventoryList,final List<GoodsSuppliersDO> suppliersInventoryList,List<GoodsInventoryWMSDO> wmsInventoryList) throws Exception;
	public CallResult<GoodsInventoryDO> updateGoodsInventory(Long goodsId, int adjustNum,int limitStorage,GoodsInventoryDO goodsDO) throws Exception;
	public CallResult<GoodsInventoryDO> updateGoodsInventory(GoodsInventoryDO goodsDO) throws Exception;
	public CallResult<GoodsInventoryDO> updateGoodsInventory(long goodsId,GoodsInventoryDO goodsDO) throws Exception;
	public CallResult<GoodsInventoryDO> updateGoodsInventory(long goodsId,int adjustNum,GoodsInventoryDO goodsDO) throws Exception;
	public CallResult<GoodsInventoryDO> updateGoodsInventory(long goodsId,Map<String, String> hash,GoodsInventoryDO goodsDO) throws Exception;
	public CallResult<GoodsSelectionDO> updateGoodsSelection(GoodsInventoryDO goodsDO,GoodsSelectionDO selectionDO) throws Exception;
	public CallResult<GoodsSuppliersDO> updateGoodsSuppliers(GoodsInventoryDO goodsDO,GoodsSuppliersDO suppliersDO) throws Exception;
	
	public CallResult<List<GoodsSelectionDO>> updateBatchGoodsSelection(Long goodsId, List<GoodsSelectionDO> selectionDOList) throws Exception;
	public CallResult<List<GoodsSuppliersDO>> updateBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) throws Exception;
	
	
	//查询：
	public CallResult<List<GoodsSelectionDO>> selectSelectionByGoodsTypeIds(final List<Long> goodsTypeIdList);
	public CallResult<GoodsInventoryDO> selectGoodsInventoryByGoodsId(long goodsId);
	public  CallResult<List<GoodsSelectionDO>> selectGoodsSelectionListByGoodsId(long goodsId);
	public  CallResult<List<GoodsSuppliersDO>> selectGoodsSuppliersListByGoodsId(long goodsId);
	public CallResult<GoodsInventoryWMSDO> selectGoodsInventoryWMSByWmsGoodsId(String wmsGoodsId,int isBeDelivery);
	public CallResult<GoodsInventoryWMSDO> selectIsOrNotGoodsWMSByGoodsId(long goodsId);
	public CallResult<WmsIsBeDeliveryDO> selectWmsIsBeDeliveryResult(String wmsGoodsId);
	public CallResult<List<GoodsInventoryDO>> selectInventoryList4Wms(String wmsGoodsId);
	//删除
	public CallResult<Integer> deleteGoodsInventory(long goodsId) throws Exception;
	public CallResult<List<GoodsSelectionDO>> deleteBatchGoodsSelection(List<GoodsSelectionDO> selectionDOList) throws Exception;
	public CallResult<List<GoodsSuppliersDO>> deleteBatchGoodsSuppliers(List<GoodsSuppliersDO> suppliersDOList) throws Exception;
	
	
}
