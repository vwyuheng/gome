package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
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
	
	public GoodsInventoryDO selectGoodsInventoryDO(long goodsId);
	void insertGoodsInventoryDO(GoodsInventoryDO goodsDO);
	void updateGoodsInventoryDO(GoodsInventoryDO goodsDO);
	int deleteGoodsInventoryDO(long goodsId);
	
	public GoodsSelectionDO selectGoodsSelectionDO(long selectionId);
	void insertGoodsSelectionDO(GoodsSelectionDO selectionDO);
	void updateGoodsSelectionDO(GoodsSelectionDO selectionDO);
	int deleteGoodsSelectionDO(long selectionId);
	 
	public GoodsSuppliersDO selectGoodsSuppliersDO(long suppliersId);
	void insertGoodsSuppliersDO(GoodsSuppliersDO suppliersDO);
	void updateGoodsSuppliersDO(GoodsSuppliersDO suppliersDO);
	int deleteGoodsSuppliersDO(long suppliersId);
	 
	public GoodsInventoryWMSDO selectGoodsInventoryWMSDO(String wmsGoodsId);
	void insertGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO);
	void updateGoodsInventoryWMSDO(GoodsInventoryWMSDO wmsDO);
	void updateGoodsSelectionWmsDO(GoodsWmsSelectionResult selection);
	void insertGoodsBaseInventoryDO(GoodsBaseInventoryDO baseInventoryDO);
	void updateGoodsBaseInventoryDO(GoodsBaseInventoryDO baseInventoryDO);
	/**
	 * @Title: selectGoodBaseBygoodsId
	 * @Description: 通过goodsBaseId查询商品的基本信息的总销量和库存信息
	 * @param goodsBaseId return GoodsBaseInventoryDO
	 */
	public GoodsBaseInventoryDO selectGoodBaseBygoodsId(long goodsBaseId);
}

