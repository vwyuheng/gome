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
	 * @Description: ���ݷֵ�ѡ�͹�ϵ��idȡ����������
	 * @param selectionRelationId
	 * @return GoodsSelectionRelationDO
	 */
	public GoodsSelectionRelationDO selectSelectionRelationBySrId(int selectionRelationId);
	
	/**
	 * @Title: selectGoodsSuppliersInventoryBySiId
	 * @Description: ���ݷֵ�ѡ�͹�ϵ��idȡ����������
	 * @param suppliersInventoryId
	 * @return GoodsSuppliersInventoryDO
	 */
	public GoodsSuppliersInventoryDO selectGoodsSuppliersInventoryBySiId(int suppliersInventoryId);
	
	/**
	 * @Title: updateSuppliersInventoryLeftNumber
	 * @Description: �޸ķֵ��� 
	 * @param orderInfoDetail void
	 */
	public void updateSuppliersInventoryLeftNumber(OrderInfoDetailDO orderInfoDetail);
	/**
	 * @Title: updateSelectionRelationLeftNumber
	 * @Description: �޸�ѡ�Ϳ�� 
	 * @param orderInfoDetail void
	 */
	public void updateSelectionRelationLeftNumber(OrderInfoDetailDO orderInfoDetail);
	/**
	 * @Title: updateGoodsAttributesLeftNumber
	 * @Description: �޸���Ʒ���
	 * @param orderGoods void
	 */
	public void updateGoodsAttributesLeftNumber(OrderGoodsDO orderGoods);
	
	/**
	 * ����������Ʒ����
	 * @param wmsGoods
	 */
	public void updataGoodsWmsLeftNumByID(WmsGoodsDO wmsGoods);
	
	/**
	 * @Title: selectGoodsSuppliersInventoryBySiId
	 * @Description: ���ݷֵ�ѡ�͹�ϵ��idȡ����������
	 * @param suppliersInventoryId
	 * @param goodsId
	 * @return GoodsSuppliersInventoryDO
	 */
	public List<GoodsSelectionRelationGoodDO> selectSelectionRelationBySrIdAndGoodsId(
			long selectionRelationId, long goodsId);
	
	/**
	 * ���ݷֵ�ѡ�͹�ϵ��id�б�ȡ�����ж�Ӧ����
	 * @param selectionRelationIdList
	 * @return
	 */
	public List<GoodsSelectionRelationGoodDO> selectSelectionRelationBySrIds(List<Long> selectionRelationIdList);
	
	public GoodsAttributeInventoryDO getNotSeleInventory(long goodsId);
	
}
