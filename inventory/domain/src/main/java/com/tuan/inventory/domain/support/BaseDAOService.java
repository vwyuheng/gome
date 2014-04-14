package com.tuan.inventory.domain.support;

import java.util.Set;

import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;

public interface BaseDAOService {
	/**
	 * ������Ʒid�жϿ���Ƿ��Ѵ���
	 * �Ѵ��ڷ���false,�����ڷ���true
	 * @param goodsId
	 * @return
	 */
	public boolean isExists(Long goodsId);
	/**
	 * ����key �ж���Ʒ���hash���Ƿ���field
	 * @param goodsId
	 * @param field
	 * @return
	 */
	public boolean isGoodsExists(Long goodsId,String field);
	/**
	 * ����ѡ��id �ж���Ʒѡ�Ϳ�� hash�Ƿ���ڸ�field
	 * @param selectionId
	 * @param field
	 * @return
	 */
	public boolean isSelectionExists(Long selectionId,String field);
	/**
	 * ���ݷֵ�id �ж���Ʒ�ֵ��� hash�Ƿ���ڸ�field
	 * @param suppliesId
	 * @param field
	 * @return
	 */
	public boolean isSupplierExists(Long suppliesId,String field);
	/**
	 * ѹ����־����
	 * @param logActionDO
	 */
	public void pushLogQueues(final GoodsInventoryActionDO logActionDO);
	public void pushQueueSendMsg(final GoodsInventoryQueueDO queueDO);
	/**
	 * ������Ʒ���
	 * @param inventoryInfoDO
	 */
	public void saveInventory(Long goodsId,GoodsInventoryDO inventoryInfoDO);
	/**
	 * ������Ʒѡ�Ϳ��
	 * @param goodsId
	 * @param selectionModel
	 */
	public void saveGoodsSelectionInventory(Long goodsId, GoodsSelectionDO selectionDO);
	/**
	 * ������Ʒ�ֵ���
	 * @param goodsId
	 * @param selectionModel
	 */
	public void saveGoodsSuppliersInventory(Long goodsId, GoodsSuppliersDO suppliersDO);
	/**
	 * ������Ʒid��ѯ�����Ϣ
	 * @param goodsId
	 * @return
	 */
	public GoodsInventoryDO queryGoodsInventory(Long goodsId);
	/**
	 * ����ѡ��id��ѯ��Ʒѡ�Ϳ��
	 * @param selectionId
	 * @return
	 */
	public GoodsSelectionDO querySelectionRelationById(Long selectionId);
	/**
	 * ���ݷֵ�id��ѯ��Ʒ�ֵ���
	 * @param suppliersId
	 * @return
	 */
	public GoodsSuppliersDO querySuppliersInventoryById(Long suppliersId);
	
	public Long updateGoodsInventory(Long goodsId,int num);
	public Long updateSelectionInventory(Long selectionId,int num);
	public Long updateSuppliersInventory(Long suppliersId,int num);
	public void markQueueStatus(String key,  int upStatusNum);
	
	public GoodsInventoryQueueDO queryInventoryQueueDO(String key);
	
	public Long adjustGoodsWaterflood(Long goodsId,int num);
	public Long adjustSelectionWaterflood(Long selectionId,int num);
	public Long adjustSuppliersWaterflood(Long suppliersId,int num);
	
	public Long deleteGoodsInventory(Long goodsId);
	public Long deleteSelectionInventory(Long selectionId);
	public Long deleteSuppliersInventory(Long suppliersId);
	
	public void lremLogQueue(final GoodsInventoryActionDO logActionDO);
	
	public Set<String> queryGoodsSelectionRelation(Long goodsId);
	public Set<String> queryGoodsSuppliersRelation(Long goodsId);
	public Set<String> queryInventoryQueueListByStatus (final Double status);
	
	public String queryLastIndexGoodsInventoryAction ();
	public Long deleteQueueMember(String key);
	//public GoodsInventoryDO queryGoodsInventory(Long goodsId) 
}
