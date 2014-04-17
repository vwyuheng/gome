package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
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
	public void preHandler() {
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
			this.writeBusErrorLog(
					lm.setMethod("preHandler").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
		}
		
		
	}

	// 业务处理
	public CreateInventoryResultEnum businessHandler() {

		try {
			// 业务检查前的预处理
			this.preHandler();
			if (!CollectionUtils.isEmpty(listGoodsIdSends)) {
				for(long goodsId:listGoodsIdSends) {
					if(loadMessageData(goodsId)) {
						this.sendNotify();
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
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		notifyParam.setUserId(goodsInventoryModel.getUserId());
		notifyParam.setGoodsId(goodsInventoryModel.getGoodsId());
		notifyParam.setLimitStorage(goodsInventoryModel.getLimitStorage());
		notifyParam.setWaterfloodVal(goodsInventoryModel.getWaterfloodVal());
		notifyParam.setTotalNumber(goodsInventoryModel.getTotalNumber());
		notifyParam.setLeftNumber(goodsInventoryModel.getLeftNumber());
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSelectionList())) {
			notifyParam.setSelectionRelation(goodsInventoryModel.getGoodsSelectionList());
		}
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSuppliersList())) {
			notifyParam.setSuppliersRelation(goodsInventoryModel.getGoodsSuppliersList());
		}
		return notifyParam;
	}

	// 将队列标记删除：逻辑删除
	public void markDelete() {
		try {
			if (!CollectionUtils.isEmpty(listQueueIdMarkDelete)) {
				for(long queueId:listQueueIdMarkDelete) {
					this.goodsInventoryDomainRepository.markQueueStatus(String.valueOf(queueId), (delStatus));
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

}
