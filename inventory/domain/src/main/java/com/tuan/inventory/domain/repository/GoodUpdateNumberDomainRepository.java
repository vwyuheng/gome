package com.tuan.inventory.domain.repository;

import com.tuan.inventory.dao.GoodNumUpdateDAO;
import com.tuan.inventory.dao.data.GoodsUpdateNumberDO;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;

/**
 * @author zhangbo
 * 
 */
public class GoodUpdateNumberDomainRepository {
	
	private GoodNumUpdateDAO  goodNumUpdateDAO;


	public void updateGoodsAttributesNumber(GoodsUpdateNumberDO goodsUpdateNumberDO) {

		getGoodNumUpdateDAO().updateGoodsAttributesNumber(goodsUpdateNumberDO);
	}

	public void updateSelectionRelationNumber(GoodsUpdateNumberDO goodsUpdateNumberDO) {

		getGoodNumUpdateDAO().updateSelectionRelationNumber(goodsUpdateNumberDO);
	}

	public void updateSuppliersInventoryNumber(GoodsUpdateNumberDO goodsUpdateNumberDO) {

		getGoodNumUpdateDAO().updateSuppliersInventoryNumber(goodsUpdateNumberDO);
	}
	
	public void updataGoodsBaseNumber(GoodsUpdateNumberDO goodsUpdateNumberDO) {

		getGoodNumUpdateDAO().updataGoodsBaseNumByID(goodsUpdateNumberDO);
	}
	
	public GoodsBaseInventoryDO getGoodBaseBygoodsId(long goodsBaseId) {

		return getGoodNumUpdateDAO().selectGoodBaseBygoodsId(goodsBaseId);
	}
	
	public void updataGoodsNum(GoodsUpdateNumberDO goodsUpdateNumberDO) {

		getGoodNumUpdateDAO().updataGoodsNumByID(goodsUpdateNumberDO);
	}
	
	/**
	 * 更新物流商品表库存
	 * 
	 * @param wmsGoods
	 */

	public void updataGoodsWmsNumByID(GoodsUpdateNumberDO goodsUpdateNumberDO) {
		getGoodNumUpdateDAO().updataGoodsWmsNumByID(goodsUpdateNumberDO);
	}

	public GoodNumUpdateDAO getGoodNumUpdateDAO() {
		return goodNumUpdateDAO;
	}

	public void setGoodNumUpdateDAO(GoodNumUpdateDAO goodNumUpdateDAO) {
		this.goodNumUpdateDAO = goodNumUpdateDAO;
	}
	
}
