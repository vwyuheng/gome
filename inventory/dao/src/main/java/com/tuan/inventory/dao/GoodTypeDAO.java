package com.tuan.inventory.dao;

import java.util.List;

import com.tuan.inventory.dao.data.GoodsAttributeInventoryDO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationGoodDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.OrderGoodsDO;
import com.tuan.inventory.dao.data.OrderInfoDetailDO;
import com.tuan.inventory.dao.data.WmsGoodsDO;


/**
 * @author tianzq
 * @date 2012.11.30
 *
 */
public interface GoodTypeDAO {
	/**
	 * @Title: selectSelectionRelationBySrId
	 * @Description: 根据分店选型关系表id取得所有配型
	 * @param selectionRelationId
	 * @return GoodsSelectionRelationDO
	 */
	public GoodsSelectionRelationDO selectSelectionRelationBySrId(int selectionRelationId);
	
	/**
	 * @Title: selectGoodsSuppliersInventoryBySiId
	 * @Description: 根据分店选型关系表id取得所有配型
	 * @param suppliersInventoryId
	 * @return GoodsSuppliersInventoryDO
	 */
	public GoodsSuppliersInventoryDO selectGoodsSuppliersInventoryBySiId(int suppliersInventoryId);
	
	/**
	 * @Title: updateSuppliersInventoryLeftNumber
	 * @Description: 修改分店库存 
	 * @param orderInfoDetail void
	 */
	public void updateSuppliersInventoryLeftNumber(OrderInfoDetailDO orderInfoDetail);
	/**
	 * @Title: updateSelectionRelationLeftNumber
	 * @Description: 修改选型库存 
	 * @param orderInfoDetail void
	 */
	public void updateSelectionRelationLeftNumber(OrderInfoDetailDO orderInfoDetail);
	/**
	 * @Title: updateGoodsAttributesLeftNumber
	 * @Description: 修改商品库存
	 * @param orderGoods void
	 */
	public void updateGoodsAttributesLeftNumber(OrderGoodsDO orderGoods);
	
	/**
	 * 更新物流商品表库存
	 * @param wmsGoods
	 */
	public void updataGoodsWmsLeftNumByID(WmsGoodsDO wmsGoods);
	
	/**
	 * @Title: selectGoodsSuppliersInventoryBySiId
	 * @Description: 根据分店选型关系表id取得所有配型
	 * @param suppliersInventoryId
	 * @param goodsId
	 * @return GoodsSuppliersInventoryDO
	 */
	public List<GoodsSelectionRelationGoodDO> selectSelectionRelationBySrIdAndGoodsId(
			long selectionRelationId, long goodsId);
	
	/**
	 * 根据分店选型关系表id列表取得所有对应配型
	 * @param selectionRelationIdList
	 * @return
	 */
	public List<GoodsSelectionRelationGoodDO> selectSelectionRelationBySrIds(List<Long> selectionRelationIdList);
	
	public GoodsAttributeInventoryDO getNotSeleInventory(long goodsId);
	
}
