package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;

public class InventoryConfirmScheduledDomain extends AbstractDomain {
	private LogModel lm;
	private GoodsInventoryModel goodsInventoryModel;
	//缓存商品id，并排重，以便归并相同商品的消息发送次数
	private ConcurrentHashSet<Long> listGoodsIdSends;
	private ConcurrentHashSet<Long> listQueueIdMarkDelete;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	
	private final int delStatus = 6;
	public InventoryConfirmScheduledDomain(String clientIp,
			String clientName,LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.lm = lm;
	}
	/***
	 * 业务处理前的预处理
	 */
	public boolean preHandler() {
		boolean preresult = true;
		try {
			listGoodsIdSends = new ConcurrentHashSet<Long>();
			listQueueIdMarkDelete = new ConcurrentHashSet<Long>();
			// 商品库存是否存在
			//取初始状态队列信息
			List<GoodsInventoryQueueModel> queueList = goodsInventoryDomainRepository
					.queryInventoryQueueListByStatus(Double
							.valueOf(ResultStatusEnum.CONFIRM.getCode()));
			if (!CollectionUtils.isEmpty(queueList)) {
				for (GoodsInventoryQueueModel model : queueList) {
					//队列数据数据按商品id 归集后 发送消息更新数据准备
					if (verifyId(model.getGoodsId()))
						listGoodsIdSends.add(model.getGoodsId());
					//队列数据消费完标记删除数据准备
					if (verifyId(model.getId()))
						listQueueIdMarkDelete.add(model.getId());
				}
			}
		} catch (Exception e) {
			preresult = false;
			this.writeBusErrorLog(
					lm.setMethod("preHandler").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
		}
		
		return preresult;
	}

	// 业务处理
	public CreateInventoryResultEnum businessHandler() {

		try {
			// 业务检查前的预处理
			if(!preHandler()){
				return CreateInventoryResultEnum.DB_ERROR;
			}
			
			if (!CollectionUtils.isEmpty(listGoodsIdSends)) {
				for(long goodsId:listGoodsIdSends) {
					if(loadMessageData(goodsId)) {
						
						//将已更新的数据更新到mysql
						if(this.fillParamAndUpdate()) {  //数据同步成功后再发消息
							this.sendNotify(); 
						}
					}
				}
				
			}
			//消息发送完成后将取出的队列标记删除状态
			this.markDelete();
				
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	//加载消息数据
	public boolean loadMessageData(long goodsId) {
		try {
		this.goodsInventoryModel =	this.goodsInventoryDomainRepository.queryGoodsInventoryByGoodsId(goodsId);
		if(this.goodsInventoryModel==null){
			return false;
		}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("loadMessageData").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return false;
		}
		return true;
	}

	//异步更新mysql商品库存
	public boolean asynUpdateMysqlInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
		InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
		//create.setGoodsId(goodsId);
		//注入相关Repository
		create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
		create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		create.setInventoryInitAndUpdateHandle(this.inventoryInitAndUpdateHandle);
		return create.updateMysqlInventory(inventoryInfoDO, selectionInventoryList, suppliersInventoryList);
	}
	/**
	 * 组装数据并更新
	 */
	private boolean fillParamAndUpdate() {
		List<GoodsSelectionDO> selectionInventoryList = null;
		List<GoodsSuppliersDO> suppliersInventoryList = null;
		GoodsInventoryDO inventoryInfoDO = new GoodsInventoryDO();
		long goodsId = goodsInventoryModel.getGoodsId();
		inventoryInfoDO.setGoodsId(goodsId);
		inventoryInfoDO.setLimitStorage(goodsInventoryModel.getLimitStorage());
		inventoryInfoDO.setWaterfloodVal(goodsInventoryModel.getWaterfloodVal());
		inventoryInfoDO.setTotalNumber(goodsInventoryModel.getTotalNumber());
		inventoryInfoDO.setLeftNumber(goodsInventoryModel.getLeftNumber());
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSelectionList())) {
			selectionInventoryList = new ArrayList<GoodsSelectionDO>();
			List<GoodsSelectionModel> selectionModelList = goodsInventoryModel.getGoodsSelectionList();
			if(!CollectionUtils.isEmpty(selectionModelList)) {
				for(GoodsSelectionModel selModel:selectionModelList) {
					selectionInventoryList.add(ObjectUtils.toSelectionDO(selModel));
				}
			}
		}
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSuppliersList())) {
			 suppliersInventoryList = new ArrayList<GoodsSuppliersDO>();
			List<GoodsSuppliersModel> suppliersModelList = goodsInventoryModel.getGoodsSuppliersList();
			if(!CollectionUtils.isEmpty(suppliersModelList)) {
				for(GoodsSuppliersModel supModel:suppliersModelList) {
					suppliersInventoryList.add(ObjectUtils.toSuppliersDO(supModel));
				}
			}
		}
	   //调用数据同步
		return this.asynUpdateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList);
	}
	// 发送库存新增消息
	public void sendNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
			this.goodsInventoryDomainRepository.sendNotifyServerMessage(JSONObject
					.fromObject(notifyParam));
			/*
			 * Type orderParamType = new
			 * TypeToken<NotifyCardOrder4ShopCenterParam>(){}.getType(); String
			 * paramJson = new Gson().toJson(notifyParam, orderParamType);
			 * extensionService.sendNotifyServer(paramJson, lm.getTraceId());
			 */
		} catch (Exception e) {
			writeBusErrorLog(lm.setMethod("sendNotify").addMetaData("errMsg", e.getMessage()), e);
		}
	}

	// 填充notifyserver发送参数
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() {
		if(goodsInventoryModel==null) {
			return null;
		}
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		
		notifyParam.setUserId(goodsInventoryModel.getUserId());
		notifyParam.setGoodsId(goodsInventoryModel.getGoodsId());
		notifyParam.setLimitStorage(goodsInventoryModel.getLimitStorage());
		notifyParam.setWaterfloodVal(goodsInventoryModel.getWaterfloodVal());
		notifyParam.setTotalNumber(goodsInventoryModel.getTotalNumber());
		notifyParam.setLeftNumber(goodsInventoryModel.getLeftNumber());
		//库存总数 减 库存剩余
		int sales = goodsInventoryModel.getTotalNumber()-goodsInventoryModel.getLeftNumber();
		//销量
		notifyParam.setSales(String.valueOf(sales));
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSelectionList())) {
			notifyParam.setSelectionRelation(ObjectUtils.toSelectionMsgList(goodsInventoryModel.getGoodsSelectionList()));
		}
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSuppliersList())) {
			notifyParam.setSuppliersRelation(ObjectUtils.toSuppliersMsgList(goodsInventoryModel.getGoodsSuppliersList()));
		}
		return notifyParam;
	}

	// 将队列标记删除：逻辑删除[队列]\物理删除缓存的member
	public void markDelete() {
		
		try {
			if (!CollectionUtils.isEmpty(listQueueIdMarkDelete)) {
				for(long queueId:listQueueIdMarkDelete) {
					String member = this.goodsInventoryDomainRepository.queryMember(String.valueOf(queueId));
					if(!StringUtils.isEmpty(member)) {
						this.goodsInventoryDomainRepository.markQueueStatusAndDeleteCacheMember(member, (delStatus),String.valueOf(queueId));
					}
				}
			}
			
		} catch (Exception e) {
			
			this.writeBusErrorLog(lm.setMethod("markDelete")
					.addMetaData("errMsg", e.getMessage()), e);
			
		}
		
	}
	
	
	/**
	 * 校验id
	 * @param id
	 * @return
	 */
   public boolean verifyId(long id) {
	   if(id<=0) {
		   return false;
	   }else {
		   return true;
	   }
   }

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	
	public void setSynInitAndAysnMysqlService(
			SynInitAndAysnMysqlService synInitAndAysnMysqlService) {
		this.synInitAndAysnMysqlService = synInitAndAysnMysqlService;
	}
	public void setInventoryInitAndUpdateHandle(
			InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle) {
		this.inventoryInitAndUpdateHandle = inventoryInitAndUpdateHandle;
	}
	
	
}
