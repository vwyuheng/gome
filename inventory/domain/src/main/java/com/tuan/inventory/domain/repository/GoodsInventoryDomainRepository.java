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
	private BaseDAOService  baseDAOService;
	@Resource
	private NotifyServerSendMessage notifyServerSendMessage;
	
	public void sendNotifyServerMessage(JSONObject jsonObj) {
		this.notifyServerSendMessage.sendNotifyServerMessage(jsonObj);
	}
	//保存商品库存
	public void saveGoodsInventory(Long goodsId, GoodsInventoryDO inventoryInfoDO) {
		inventoryInfoDO.setTotalNumber(inventoryInfoDO.getLimitStorage()==0?Integer.MAX_VALUE:inventoryInfoDO.getTotalNumber());
		inventoryInfoDO.setLeftNumber(inventoryInfoDO.getLimitStorage()==0?Integer.MAX_VALUE:inventoryInfoDO.getLeftNumber());
		this.baseDAOService.saveInventory(goodsId, inventoryInfoDO);
	}
	//将日志压入队列
	public void pushLogQueues(final GoodsInventoryActionDO logActionDO){
		this.baseDAOService.pushLogQueues(logActionDO);
	}
	public void pushQueueSendMsg(final GoodsInventoryQueueDO queueDO) {
		this.baseDAOService.pushQueueSendMsg(queueDO);
	}
	//判断库存是否已存在
	public boolean isExists(Long goodsId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isExists(goodsId);
	}
	public boolean isGoodsExists(Long goodsId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isGoodsExists(goodsId,HashFieldEnum.leftNumber.toString());
	}
	public boolean isSelectionExists(Long selectionId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isSelectionExists(selectionId,HashFieldEnum.leftNumber.toString());
	}
	public boolean isSupplierExists(Long suppliesId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isSupplierExists(suppliesId,HashFieldEnum.leftNumber.toString());
	}
	
	
	public void saveGoodsSelectionInventory(Long goodsId, List<GoodsSelectionDO> selectionDO) {

		if (!CollectionUtils.isEmpty(selectionDO)) { // if1
			for (GoodsSelectionDO srDO : selectionDO) { // for
				if (srDO.getId() > 0) { // if选型
					//首先根据选型id判断该选型是否已存在
					GoodsSelectionDO tmpSelDO = this.baseDAOService.querySelectionRelationById(srDO.getId());
					if(tmpSelDO==null) {  //不存在才创建
						//将商品id set到选型中
						srDO.setGoodsId(goodsId);
						srDO.setTotalNumber(srDO.getLimitStorage()==0?Integer.MAX_VALUE:srDO.getTotalNumber());
						srDO.setLeftNumber(srDO.getLimitStorage()==0?Integer.MAX_VALUE:srDO.getLeftNumber());
						this.baseDAOService.saveGoodsSelectionInventory(goodsId, srDO);
					}
					
				}
				
			}//for
		}//if1
			
	
	}
	
	public void saveGoodsSuppliersInventory(Long goodsId, List<GoodsSuppliersDO> suppliersDO) {
		if (!CollectionUtils.isEmpty(suppliersDO)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDO) { // for
				if (sDO.getId() > 0) { // if分店
					GoodsSuppliersDO tmpSuppDO = this.baseDAOService.querySuppliersInventoryById(sDO.getSuppliersId());
					if(tmpSuppDO==null) { //不存在才创建
						sDO.setGoodsId(goodsId);
						sDO.setTotalNumber(sDO.getLimitStorage()==0?Integer.MAX_VALUE:sDO.getTotalNumber());
						sDO.setLeftNumber(sDO.getLimitStorage()==0?Integer.MAX_VALUE:sDO.getLeftNumber());
						this.baseDAOService.saveGoodsSuppliersInventory(goodsId, sDO);
					}
					
				}
				
			}//for
		}//if1
			
	}
	
	
	//根据商品id查询库存信息
	public GoodsInventoryDO queryGoodsInventory(long goodsId) {
		return this.baseDAOService.queryGoodsInventory(goodsId);
	}
	
	// 根据商品选型id查询库存信息
	public GoodsSelectionDO querySelectionRelationById(long selectionId) {
		return this.baseDAOService.querySelectionRelationById(selectionId);
	}
	public GoodsSuppliersDO querySuppliersInventoryById(Long suppliersId) {
		return this.baseDAOService.querySuppliersInventoryById(suppliersId);
	}
	
	public Long updateGoodsInventory(Long goodsId, int num) {
		return this.baseDAOService.updateGoodsInventory(goodsId, num);
	}
	
	public Long updateSelectionInventoryById(Long selectionId, int num) {
		return this.baseDAOService.updateSelectionInventory(selectionId, (num));
	}
	public Long updateSuppliersInventoryById(Long suppliersId, int num) {
		return this.baseDAOService.updateSuppliersInventory(suppliersId, (num));
	}
	public Long updateSelectionInventory(List<GoodsSelectionAndSuppliersResult> selectionParam) {
		long result = 0;
		if (!CollectionUtils.isEmpty(selectionParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : selectionParam) { // for
				if (param.getId() > 0) { // if选型
					result = this.baseDAOService.updateSelectionInventory(param.getId(), (-param.getGoodsInventory()));
				}
			}
		}
		return result;
		
	}
	//回滚选型库存
	public Long rollbackSelectionInventory(List<GoodsSelectionAndSuppliersResult> selectionParam) {
		long result = 0;
		if (!CollectionUtils.isEmpty(selectionParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : selectionParam) { // for
				if (param.getId() > 0) { // if选型
					result = this.baseDAOService.updateSelectionInventory(param.getId(), (param.getGoodsInventory()));
				}
			}
		}
		return result;
		
	}
	public Long updateSuppliersInventory(List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		long result = 0;
		if (!CollectionUtils.isEmpty(suppliersParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : suppliersParam) { // for
				if (param.getId() > 0) { // if选型
					result = this.baseDAOService.updateSuppliersInventory(param.getId(), (-param.getGoodsInventory()));
				}
			}
		}
		return result;
		
	}
	//回滚分店库存
	public Long rollbackSuppliersInventory(List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		long result = 0;
		if (!CollectionUtils.isEmpty(suppliersParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : suppliersParam) { // for
				if (param.getId() > 0) { // if选型
					result = this.baseDAOService.updateSuppliersInventory(param.getId(), (param.getGoodsInventory()));
				}
			}
		}
		return result;
		
	}
	/**
	 * 标记队列状态
	 * @param member
	 * @param upStatusNum :该参数特别说明：当你想减值时传负值，当是增加值时传正整数值
	 * @throws Exception
	 */
	public void markQueueStatus(String member,int upStatusNum) {
		this.baseDAOService.markQueueStatus(member, upStatusNum);
	}
	public void markQueueStatusAndDeleteCacheMember(String member,int upStatusNum,String delkey) {
		this.baseDAOService.markQueueStatusAndDeleteCacheMember(member, upStatusNum,delkey);
	}
	
	public GoodsInventoryQueueDO queryInventoryQueueDO(String key) {
		return this.baseDAOService.queryInventoryQueueDO(key);
	}
	
	public Long adjustGoodsWaterflood(Long goodsId, int num) {
		return this.baseDAOService.adjustGoodsWaterflood(goodsId, num);
	}
	
	public Long adjustSelectionWaterfloodById(Long selectionId, int num) {
		return this.baseDAOService.adjustSelectionWaterflood(selectionId, (num));
	}
	public Long adjustSuppliersWaterfloodById(Long suppliersId, int num) {
		return this.baseDAOService.adjustSuppliersWaterflood(suppliersId, (num));
	}
	
	public Long deleteGoodsInventory(Long goodsId) {
		return this.baseDAOService.deleteGoodsInventory(goodsId);
	}
	public Long deleteSelectionInventory(List<GoodsSelectionModel> selectionList){
		long result = 0;
		if (!CollectionUtils.isEmpty(selectionList)) { // if1
			for (GoodsSelectionModel param : selectionList) { // for
				if (param.getId() > 0) { // if选型
					result = this.baseDAOService.deleteSelectionInventory(param.getId());
				}
			}
		}
		return result;
	}
	public Long deleteSuppliersInventory(List<GoodsSuppliersModel> suppliersList){
		long result = 0;
		if (!CollectionUtils.isEmpty(suppliersList)) { // if1
			for (GoodsSuppliersModel param : suppliersList) { // for
				if (param.getId() > 0) { // if选型
					result = this.baseDAOService.deleteSuppliersInventory(param.getId());
				}
			}
		}
		return result;
	}
	/**
	 * 删除日志
	 * @param logActionDO
	 */
	public void lremLogQueue(GoodsInventoryActionDO logActionDO) {
		 this.baseDAOService.lremLogQueue(logActionDO);
	}
	/**
	 * 根据选型id获取选型库存
	 * @param selectionId
	 * @return
	 */
	public GoodsSelectionModel queryGoodsSelectionBySelectionId(long selectionId) {
		
		return ObjectUtils.toModel(this.querySelectionRelationById(selectionId));
	}
	/**
	 * 根据商品id获取商品 所有的选型库存列表
	 * @param goodsId
	 * @return
	 */
	public List<GoodsSelectionModel> queryGoodsSelectionListByGoodsId(long goodsId) {
		List<GoodsSelectionModel> result = null;
		Set<String> selectionIdsSet = this.baseDAOService.queryGoodsSelectionRelation(goodsId);
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
	 * 根据选型id的list获取选型信息列表
	 * @param selectionIdList
	 * @return
	 */
	public List<GoodsSelectionModel> queryGoodsSelectionListBySelectionIdList(List<Long> selectionIdList) {
		List<GoodsSelectionModel> result = null;
		//Set<String> selectionIdsSet = this.baseDAOService.queryGoodsSelectionRelation(goodsId);
		if(!CollectionUtils.isEmpty(selectionIdList)) {
			result = new ArrayList<GoodsSelectionModel>();
			for(long selectionId: selectionIdList) {
				GoodsSelectionModel gsModel = this.queryGoodsSelectionBySelectionId(selectionId);
				if(gsModel!=null) {
					result.add(gsModel);
				}
				
			}
		}
		return result;
	}
	/**
	 * 根据商品分店id获取分店库存
	 * @param suppliersId
	 * @return
	 */
	public GoodsSuppliersModel queryGoodsSuppliersBySuppliersId(long suppliersId) {
		
		return ObjectUtils.toModel(this.querySuppliersInventoryById(suppliersId));
	}
	/**
	 * 根据分店id list获取分店列表
	 * @param suppliersIdList
	 * @return
	 */
	public List<GoodsSuppliersModel> queryGoodsSuppliersListBySuppliersIdList(List<Long> suppliersIdList) {
		List<GoodsSuppliersModel> result = null;
		//Set<String> suppliersIdsSet = this.baseDAOService.queryGoodsSuppliersRelation(goodsId);
		if(!CollectionUtils.isEmpty(suppliersIdList)) {
			result = new ArrayList<GoodsSuppliersModel>();
			for(long suppliersId: suppliersIdList) {
				GoodsSuppliersModel gsModel = this.queryGoodsSuppliersBySuppliersId(suppliersId);
				if(gsModel!=null) {
					result.add(gsModel);
				}
				
			}
		}
		return result;
	}
	/**
	 * 根据商品id获取商品所属分店库存信息列表
	 * @param goodsId
	 * @return
	 */
	public List<GoodsSuppliersModel> queryGoodsSuppliersListByGoodsId(long goodsId) {
		List<GoodsSuppliersModel> result = null;
		Set<String> suppliersIdsSet = this.baseDAOService.queryGoodsSuppliersRelation(goodsId);
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
	 * 根据商品id获取商品库存信息，
	 * 包含(若有的话)选型的库存及分店的库存
	 * @param goodsId
	 * @return
	 */
	public GoodsInventoryModel queryGoodsInventoryByGoodsId(long goodsId) {
		return ObjectUtils.toModel(this.queryGoodsInventory(goodsId)
				//,this.queryGoodsSelectionListByGoodsId(goodsId)
				//,this.queryGoodsSuppliersListByGoodsId(goodsId)
				);
	}
	/**
	 * 根据队列状态取队列列表
	 * @param status
	 * @return
	 */
	public List<GoodsInventoryQueueModel> queryInventoryQueueListByStatus (final Double status) {
		
		return ObjectUtils.convertSet(this.baseDAOService.queryInventoryQueueListByStatus(status));
	}
	/**
	 * 获取list中最后一条日志
	 * @param status
	 * @return
	 */
	public List<GoodsInventoryActionModel> queryLastIndexGoodsInventoryAction () {
		
		return ObjectUtils.getList(this.baseDAOService.queryLastIndexGoodsInventoryAction());
	}
	
	public GoodsInventoryQueueModel queryGoodsInventoryQueue(String key) {
		
		return ObjectUtils.toModel(this.queryInventoryQueueDO(key));
	}
	/**
	 * 删除日志
	 * @param model
	 */
	public void lremLogQueue(GoodsInventoryActionModel model) {
		 this.baseDAOService.lremLogQueue(ObjectUtils.toDO(model));
	}
	/**
	 * 删除队列member
	 * @param key
	 * @return
	 */
	public Long deleteQueueMember(String key) {
		return this.baseDAOService.deleteQueueMember(key);
	}
	/**
	 * 根据key获取缓存的member[队列]
	 * @param key
	 * @return
	 */
	public String queryMember(String key) {
		return this.baseDAOService.queryMember(key);
	}
}
