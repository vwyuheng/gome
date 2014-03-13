package com.tuan.inventory.domain.repository;

import java.util.List;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.model.GoodsSelectionRelationModel;

public interface InventoryProviderReadService {

	/**
	 * ����ѡ��id��ȡѡ�Ϳ����Ϣ
	 * @param SelectionRelationId
	 * @return
	 * @throws Exception
	 */
	public GoodsSelectionRelationDO getSelectionRelationBySrId(int SelectionRelationId) throws Exception;
	/**
	 * ������Ʒ�ֵ�id��ȡfen
	 * @param SuppliersInventoryId
	 * @return
	 * @throws Exception
	 */
	public GoodsSuppliersInventoryDO getSuppliersInventoryBySiId(int SuppliersInventoryId) throws Exception;
	/**
	 * ��ȡ��Ʒѡ���б�
	 * @param selectionRelationIdList
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public List<GoodsSelectionRelationModel>  getSelectionRelationBySrIds(List<Long> selectionRelationIdList,long goodsId) throws Exception;
	/**
	 * ������Ʒid��ȡ��Ʒ�����Ϣ
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public RedisInventoryDO getNotSeleInventory (long goodsId) throws Exception;
}
