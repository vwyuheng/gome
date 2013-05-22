package com.tuan.inventory.domain.repository;

import java.util.List;

import com.tuan.inventory.dao.GoodTypeDAO;
import com.tuan.inventory.dao.data.GoodsAttributeInventoryDO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationGoodDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.OrderGoodsDO;
import com.tuan.inventory.dao.data.OrderInfoDetailDO;
import com.tuan.inventory.dao.data.WmsGoodsDO;

/**
 * @author tianzq
 * @date 2012.11.3
 * 
 */
public class GoodTypeDomainRepository {
	
	private GoodTypeDAO goodTypeDAO;

	/**
	 * @Title: selectSelectionRelationBySrId
	 * @Description: 根据分店选型关系表id取得所有配型
	 * @param selectionRelationId
	 * @return GoodsSelectionRelationDO
	 */

	public GoodsSelectionRelationDO selectSelectionRelationBySrId(int selectionRelationId) {
		return getGoodTypeDAO().selectSelectionRelationBySrId(selectionRelationId);
	}

	/**
	 * @Title: selectGoodsSuppliersInventoryBySiId
	 * @Description: 根据分店选型关系表id取得所有配型
	 * @param suppliersInventoryId
	 * @return GoodsSuppliersInventoryDO
	 */

	public GoodsSuppliersInventoryDO selectGoodsSuppliersInventoryBySiId(int suppliersInventoryId) {

		return getGoodTypeDAO().selectGoodsSuppliersInventoryBySiId(suppliersInventoryId);
	}

	public void updateGoodsAttributesLeftNumber(OrderGoodsDO orderGoods) {

		getGoodTypeDAO().updateGoodsAttributesLeftNumber(orderGoods);
	}

	public void updateSelectionRelationLeftNumber(OrderInfoDetailDO orderInfoDetail) {

		getGoodTypeDAO().updateSelectionRelationLeftNumber(orderInfoDetail);
	}

	public void updateSuppliersInventoryLeftNumber(OrderInfoDetailDO orderInfoDetail) {

		getGoodTypeDAO().updateSuppliersInventoryLeftNumber(orderInfoDetail);
	}

	/**
	 * 更新物流商品表库存
	 * 
	 * @param wmsGoods
	 */

	public void updataGoodsWmsLeftNumByID(long wmsGoodsId, int num) {

		WmsGoodsDO wmsGoods = new WmsGoodsDO();
		wmsGoods.setId(wmsGoodsId);
		wmsGoods.setLeftNum(num);
		wmsGoods.setTotalNum(num);
		getGoodTypeDAO().updataGoodsWmsLeftNumByID(wmsGoods);
	}
	
	public List<GoodsSelectionRelationGoodDO>  selectSelectionRelationBySrIdAndGoodsId(long selectionRelationId, long goodsId) {
		return getGoodTypeDAO().selectSelectionRelationBySrIdAndGoodsId(selectionRelationId, goodsId);
	}
	
	/**
	 * 根据ids查询对应选型对象 
	 * @param selectionRelationIdList
	 * @return
	 */
	public List<GoodsSelectionRelationGoodDO> selectSelectionRelationBySrIds(List<Long> selectionRelationIdList){
		return getGoodTypeDAO().selectSelectionRelationBySrIds(selectionRelationIdList);
	}
	
	public GoodsAttributeInventoryDO getNotSeleInventory(long goodsId) {
		return goodTypeDAO.getNotSeleInventory(goodsId);
	}

	public GoodTypeDAO getGoodTypeDAO() {
		return goodTypeDAO;
	}

	public void setGoodTypeDAO(GoodTypeDAO goodTypeDAO) {
		this.goodTypeDAO = goodTypeDAO;
	}

	
}
