package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.GoodsUpdateNumberDO;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;


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
	
	/**
	 * @Title: updataGoodsBaseNumByID
	 * @Description: 更新商品基本信息的总销量和库存信息
	 * @param GoodsUpdateNumberDO void
	 */
	public void updataGoodsBaseNumByID(GoodsUpdateNumberDO goodsUpdateNumberDO);
	/**
	 * @Title: selectGoodBaseBygoodsId
	 * @Description: 通过goodsBaseId查询商品的基本信息的总销量和库存信息
	 * @param goodsBaseId return GoodsBaseInventoryDO
	 */
	public GoodsBaseInventoryDO selectGoodBaseBygoodsId(long goodsBaseId);
	
	/**
	 * @Title: updataGoodsNumByID
	 * @Description: 通过goodsBaseId修改商品销量
	 * @param goodsUpdateNumberDO void 
	 */
	public void updataGoodsNumByID(GoodsUpdateNumberDO goodsUpdateNumberDO);
}
