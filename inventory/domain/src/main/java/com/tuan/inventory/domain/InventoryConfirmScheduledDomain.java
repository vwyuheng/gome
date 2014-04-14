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
	//������Ʒid�������أ��Ա�鲢��ͬ��Ʒ����Ϣ���ʹ���
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
	 * ҵ����ǰ��Ԥ����
	 */
	public void preHandler() {
		try {
			listGoodsIdSends = new ConcurrentHashSet<Long>();
			listQueueIdMarkDelete = new ConcurrentHashSet<Long>();
			// ��Ʒ����Ƿ����
			//ȡ��ʼ״̬������Ϣ
			List<GoodsInventoryQueueModel> queueList = goodsInventoryDomainRepository
					.queryInventoryQueueListByStatus(Double
							.valueOf(ResultStatusEnum.CONFIRM.getCode()));
			if (!CollectionUtils.isEmpty(queueList)) {
				for (GoodsInventoryQueueModel model : queueList) {
					//�����������ݰ���Ʒid �鼯�� ������Ϣ��������׼��
					if (verifyId(model.getGoodsId()))
						listGoodsIdSends.add(model.getGoodsId());
					//����������������ɾ������׼��
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

	// ҵ����
	public CreateInventoryResultEnum businessHandler() {

		try {
			// ҵ����ǰ��Ԥ����
			this.preHandler();
			if (!CollectionUtils.isEmpty(listGoodsIdSends)) {
				for(long goodsId:listGoodsIdSends) {
					if(loadMessageData(goodsId)) {
						this.sendNotify();
					}
				}
				
			}
			//��Ϣ������ɺ�ȡ���Ķ��б��ɾ��״̬
			this.markDelete();

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	//������Ϣ����
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

	// ���Ϳ��������Ϣ
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

	// ���notifyserver���Ͳ���
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

	// �����б��ɾ�����߼�ɾ��
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
	 * У��id
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
