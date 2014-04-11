package com.tuan.inventory.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.support.BaseDAOService;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;

public class GoodsInventoryDomainRepository extends AbstractInventoryRepository {
	@Resource
	private BaseDAOService  cacheDAOServiceImpl;
	@Resource
	private NotifyServerSendMessage notifyServerSendMessage;
	
	public void sendNotifyServerMessage(JSONObject jsonObj) {
		this.notifyServerSendMessage.sendNotifyServerMessage(jsonObj);
	}
	//������Ʒ���
	public void saveGoodsInventory(Long goodsId, GoodsInventoryDO inventoryInfoDO) {
		this.cacheDAOServiceImpl.saveInventory(goodsId, inventoryInfoDO);
	}
	//����־ѹ�����
	public void pushLogQueues(final GoodsInventoryActionDO logActionDO){
		this.cacheDAOServiceImpl.pushLogQueues(logActionDO);
	}
	public void pushQueueSendMsg(final GoodsInventoryQueueDO queueDO) {
		this.cacheDAOServiceImpl.pushQueueSendMsg(queueDO);
	}
	//�жϿ���Ƿ��Ѵ���
	public boolean isExists(Long goodsId) {
		//�Ѵ��ڷ���false,�����ڷ���true
		return cacheDAOServiceImpl.isExists(goodsId);
	}
	public boolean isGoodsExists(Long goodsId) {
		//�Ѵ��ڷ���false,�����ڷ���true
		return cacheDAOServiceImpl.isGoodsExists(goodsId,HashFieldEnum.leftNumber.toString());
	}
	public boolean isSelectionExists(Long selectionId) {
		//�Ѵ��ڷ���false,�����ڷ���true
		return cacheDAOServiceImpl.isSelectionExists(selectionId,HashFieldEnum.leftNumber.toString());
	}
	public boolean isSupplierExists(Long suppliesId) {
		//�Ѵ��ڷ���false,�����ڷ���true
		return cacheDAOServiceImpl.isSupplierExists(suppliesId,HashFieldEnum.leftNumber.toString());
	}
	
	
	public void saveGoodsSelectionInventory(Long goodsId, List<GoodsSelectionDO> selectionDO) {

		if (!CollectionUtils.isEmpty(selectionDO)) { // if1
			for (GoodsSelectionDO srDO : selectionDO) { // for
				if (srDO.getId() > 0) { // ifѡ��
					this.cacheDAOServiceImpl.saveGoodsSelectionInventory(goodsId, srDO);
				}
				
			}//for
		}//if1
			
	
	}
	
	public void saveGoodsSuppliersInventory(Long goodsId, List<GoodsSuppliersDO> suppliersDO) {
		if (!CollectionUtils.isEmpty(suppliersDO)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDO) { // for
				if (sDO.getId() > 0) { // if�ֵ�
					this.cacheDAOServiceImpl.saveGoodsSuppliersInventory(goodsId, sDO);
				}
				
			}//for
		}//if1
			
	}
	
	
	//������Ʒid��ѯ�����Ϣ
	public GoodsInventoryDO queryGoodsInventory(long goodsId) {
		return this.cacheDAOServiceImpl.queryGoodsInventory(goodsId);
	}
	
	// ������Ʒѡ��id��ѯ�����Ϣ
	public GoodsSelectionDO querySelectionRelationById(long selectionId) {
		return this.cacheDAOServiceImpl.querySelectionRelationById(selectionId);
	}
	public GoodsSuppliersDO querySuppliersInventoryById(Long suppliersId) {
		return this.cacheDAOServiceImpl.querySuppliersInventoryById(suppliersId);
	}
	
	public Long updateGoodsInventory(Long goodsId, int num) {
		return this.cacheDAOServiceImpl.updateGoodsInventory(goodsId, num);
	}
	
	public Long updateSelectionInventoryById(Long selectionId, int num) {
		return this.cacheDAOServiceImpl.updateSelectionInventory(selectionId, (num));
	}
	public Long updateSuppliersInventoryById(Long suppliersId, int num) {
		return this.cacheDAOServiceImpl.updateSelectionInventory(suppliersId, (num));
	}
	public Long updateSelectionInventory(List<GoodsSelectionAndSuppliersResult> selectionParam) {
		long result = 0;
		if (!CollectionUtils.isEmpty(selectionParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : selectionParam) { // for
				if (param.getId() > 0) { // ifѡ��
					result = this.cacheDAOServiceImpl.updateSelectionInventory(param.getId(), (-param.getGoodsInventory()));
				}
			}
		}
		return result;
		
	}
	//�ع�ѡ�Ϳ��
	public Long rollbackSelectionInventory(List<GoodsSelectionAndSuppliersResult> selectionParam) {
		long result = 0;
		if (!CollectionUtils.isEmpty(selectionParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : selectionParam) { // for
				if (param.getId() > 0) { // ifѡ��
					result = this.cacheDAOServiceImpl.updateSelectionInventory(param.getId(), (param.getGoodsInventory()));
				}
			}
		}
		return result;
		
	}
	public Long updateSuppliersInventory(List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		long result = 0;
		if (!CollectionUtils.isEmpty(suppliersParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : suppliersParam) { // for
				if (param.getId() > 0) { // ifѡ��
					result = this.cacheDAOServiceImpl.updateSuppliersInventory(param.getId(), (-param.getGoodsInventory()));
				}
			}
		}
		return result;
		
	}
	//�ع��ֵ���
	public Long rollbackSuppliersInventory(List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		long result = 0;
		if (!CollectionUtils.isEmpty(suppliersParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : suppliersParam) { // for
				if (param.getId() > 0) { // ifѡ��
					result = this.cacheDAOServiceImpl.updateSuppliersInventory(param.getId(), (param.getGoodsInventory()));
				}
			}
		}
		return result;
		
	}
	/**
	 * ��Ƕ���״̬
	 * @param key
	 * @param upStatusNum :�ò����ر�˵�����������ֵʱ����ֵ����������ֵʱ��������ֵ
	 * @throws Exception
	 */
	public void markQueueStatus(String key,int upStatusNum) {
		this.cacheDAOServiceImpl.markQueueStatus(key, upStatusNum);
	}
	
	public GoodsInventoryQueueDO queryInventoryQueueDO(String key) {
		return this.cacheDAOServiceImpl.queryInventoryQueueDO(key);
	}
	
	public Long adjustGoodsWaterflood(Long goodsId, int num) {
		return this.cacheDAOServiceImpl.adjustGoodsWaterflood(goodsId, num);
	}
	
	public Long adjustSelectionWaterfloodById(Long selectionId, int num) {
		return this.cacheDAOServiceImpl.adjustSelectionWaterflood(selectionId, (num));
	}
	public Long adjustSuppliersWaterfloodById(Long suppliersId, int num) {
		return this.cacheDAOServiceImpl.adjustSuppliersWaterflood(suppliersId, (num));
	}
	
	public Long deleteGoodsInventory(Long goodsId) {
		return this.cacheDAOServiceImpl.deleteGoodsInventory(goodsId);
	}
	public Long deleteSelectionInventory(List<GoodsSelectionModel> selectionList){
		long result = 0;
		if (!CollectionUtils.isEmpty(selectionList)) { // if1
			for (GoodsSelectionModel param : selectionList) { // for
				if (param.getId() > 0) { // ifѡ��
					result = this.cacheDAOServiceImpl.deleteSelectionInventory(param.getId());
				}
			}
		}
		return result;
	}
	public Long deleteSuppliersInventory(List<GoodsSuppliersModel> suppliersList){
		long result = 0;
		if (!CollectionUtils.isEmpty(suppliersList)) { // if1
			for (GoodsSuppliersModel param : suppliersList) { // for
				if (param.getId() > 0) { // ifѡ��
					result = this.cacheDAOServiceImpl.deleteSuppliersInventory(param.getId());
				}
			}
		}
		return result;
	}
	/**
	 * ɾ����־
	 * @param logActionDO
	 */
	public void lremLogQueue(GoodsInventoryActionDO logActionDO) {
		 this.cacheDAOServiceImpl.lremLogQueue(logActionDO);
	}
	/**
	 * ����ѡ��id��ȡѡ�Ϳ��
	 * @param selectionId
	 * @return
	 */
	public GoodsSelectionModel queryGoodsSelectionBySelectionId(long selectionId) {
		
		return ObjectUtils.toModel(this.querySelectionRelationById(selectionId));
	}
	/**
	 * ������Ʒid��ȡ��Ʒ ���е�ѡ�Ϳ���б�
	 * @param goodsId
	 * @return
	 */
	public List<GoodsSelectionModel> queryGoodsSelectionListByGoodsId(long goodsId) {
		List<GoodsSelectionModel> result = null;
		Set<String> selectionIdsSet = this.cacheDAOServiceImpl.queryGoodsSelectionRelation(goodsId);
		if(!CollectionUtils.isEmpty(selectionIdsSet)) {
			result = new ArrayList<GoodsSelectionModel>();
			for(String selectionId: selectionIdsSet) {
				GoodsSelectionModel gsModel = this.queryGoodsSelectionBySelectionId(Long.parseLong(selectionId));
				if(gsModel!=null) {
					result.add(gsModel);
				}
				
			}
		}
		return result;
	}
	/**
	 * ������Ʒ�ֵ�id��ȡ�ֵ���
	 * @param suppliersId
	 * @return
	 */
	public GoodsSuppliersModel queryGoodsSuppliersBySuppliersId(long suppliersId) {
		
		return ObjectUtils.toModel(this.querySuppliersInventoryById(suppliersId));
	}
	/**
	 * ������Ʒid��ȡ��Ʒ�����ֵ�����Ϣ�б�
	 * @param goodsId
	 * @return
	 */
	public List<GoodsSuppliersModel> queryGoodsSuppliersListByGoodsId(long goodsId) {
		List<GoodsSuppliersModel> result = null;
		Set<String> suppliersIdsSet = this.cacheDAOServiceImpl.queryGoodsSuppliersRelation(goodsId);
		if(!CollectionUtils.isEmpty(suppliersIdsSet)) {
			result = new ArrayList<GoodsSuppliersModel>();
			for(String suppliersId: suppliersIdsSet) {
				GoodsSuppliersModel gsModel = this.queryGoodsSuppliersBySuppliersId(Long.parseLong(suppliersId));
				if(gsModel!=null) {
					result.add(gsModel);
				}
				
			}
		}
		return result;
	}
	/**
	 * ������Ʒid��ȡ��Ʒ�����Ϣ��
	 * ����(���еĻ�)ѡ�͵Ŀ�漰�ֵ�Ŀ��
	 * @param goodsId
	 * @return
	 */
	public GoodsInventoryModel queryGoodsInventoryByGoodsId(long goodsId) {
		return ObjectUtils.toModel(this.queryGoodsInventory(goodsId)
				,this.queryGoodsSelectionListByGoodsId(goodsId)
				,this.queryGoodsSuppliersListByGoodsId(goodsId));
	}
	/**
	 * ���ݶ���״̬ȡ�����б�
	 * @param status
	 * @return
	 */
	public List<GoodsInventoryQueueModel> queryInventoryQueueListByStatus (final Double status) {
		
		return ObjectUtils.convertSet(this.cacheDAOServiceImpl.queryInventoryQueueListByStatus(status));
	}
	/**
	 * ��ȡlist�����һ����־
	 * @param status
	 * @return
	 */
	public List<GoodsInventoryActionModel> queryLastIndexGoodsInventoryAction () {
		
		return ObjectUtils.getList(this.cacheDAOServiceImpl.queryLastIndexGoodsInventoryAction());
	}
	
	public GoodsInventoryQueueModel queryGoodsInventoryQueue(String key) {
		
		return ObjectUtils.toModel(this.queryInventoryQueueDO(key));
	}
	/**
	 * ɾ����־
	 * @param model
	 */
	public void lremLogQueue(GoodsInventoryActionModel model) {
		 this.cacheDAOServiceImpl.lremLogQueue(ObjectUtils.toDO(model));
	}
}
