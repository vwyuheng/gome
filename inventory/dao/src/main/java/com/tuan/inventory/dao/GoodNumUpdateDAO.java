package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.GoodsUpdateNumberDO;


/**
 * @author zhangbo
 *
 */
public interface GoodNumUpdateDAO {
	
	/**
	 * @Title: updateSuppliersInventoryNumber
	 * @Description: 修改分店库存和总量 
	 * @param GoodsUpdateNumberDO void
	 */
	public void updateSuppliersInventoryNumber(GoodsUpdateNumberDO goodsUpdateNumberDO);
	/**
	 * @Title: updateSelectionRelationNumber
	 * @Description: 修改选型库存和总量 
	 * @param GoodsUpdateNumberDO void
	 */
	public void updateSelectionRelationNumber(GoodsUpdateNumberDO goodsUpdateNumberDO);
	/**
	 * @Title: updateGoodsAttributesNumber
	 * @Description: 修改商品库存和总量 
	 * @param GoodsUpdateNumberDO void
	 */
	public void updateGoodsAttributesNumber(GoodsUpdateNumberDO goodsUpdateNumberDO);
	/**
	 * @Title: updataGoodsWmsNumByID
	 * @Description: 更新物流商品表库存和总量 
	 * @param GoodsUpdateNumberDO void
	 */
	public void updataGoodsWmsNumByID(GoodsUpdateNumberDO goodsUpdateNumberDO);
	
}
