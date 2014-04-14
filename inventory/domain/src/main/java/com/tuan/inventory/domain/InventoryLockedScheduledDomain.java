package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.config.InventoryConfig;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.HessianProxyUtil;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
import com.tuan.inventory.model.enu.ClientNameEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.InventoryScheduledParam;
import com.tuan.inventory.model.util.DateUtils;
import com.tuan.ordercenter.backservice.OrderQueryService;
import com.tuan.ordercenter.model.enu.status.OrderInfoPayStatusEnum;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.OrderQueryResult;

public class InventoryLockedScheduledDomain extends AbstractDomain {
	private LogModel lm;
	private GoodsInventoryModel goodsInventoryModel;
	//����跢������Ϣ��
	private ConcurrentHashSet<Long> inventorySendMsg;
	//�����ع���
	private ConcurrentHashSet<Long> inventoryRollback;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InventoryScheduledParam param;
	private final int delStatus = 4;
	public InventoryLockedScheduledDomain(String clientIp,
			String clientName,InventoryScheduledParam param,LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	/***
	 * ҵ����ǰ��Ԥ����
	 */
	public void preHandler() {
		try {
			inventorySendMsg = new ConcurrentHashSet<Long>();
			inventoryRollback = new ConcurrentHashSet<Long>();
			// ��Ʒ����Ƿ����
			//ȡ��ʼ״̬������Ϣ
			List<GoodsInventoryQueueModel> queueList = goodsInventoryDomainRepository
					.queryInventoryQueueListByStatus(Double
							.valueOf(ResultStatusEnum.LOCKED.getCode()));
			if (!CollectionUtils.isEmpty(queueList)) {
				for (GoodsInventoryQueueModel model : queueList) {
					if(model.getCreateTime()<=DateUtils.getBeforXTimestamp10Long(param.getPeriod())) {
						//��hessian����ȡ����֧��״̬
						OrderQueryService basic = (OrderQueryService) HessianProxyUtil
								.getObject(OrderQueryService.class,
										InventoryConfig.QUERY_URL);
						CallResult<OrderQueryResult>  cllResult= basic.queryOrderPayStatus( ClientNameEnum.INNER_SYSTEM.getValue(),"", String.valueOf(model.getOrderId()));
						OrderInfoPayStatusEnum statEnum = (OrderInfoPayStatusEnum) cllResult.getBusinessResult().getResultObject();
						if(statEnum!=null) {
							//1.������״̬Ϊ�Ѹ���ʱ
							if (statEnum
									.equals(OrderInfoPayStatusEnum.PAIED)) {
								if (verifyId(model.getGoodsId()))
								   this.inventorySendMsg.add(model.getGoodsId());
							}else {
								if (verifyId(model.getId()))
								   this.inventoryRollback.add(model.getId());
							}
						}
					}
					
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
			if (!CollectionUtils.isEmpty(inventorySendMsg)) {
				for(long goodsId:inventorySendMsg) {
					if(loadMessageData(goodsId)) {
						this.sendNotify();
					}
				}
				
			}
			//��Ϣ������ɺ�ȡ���Ķ��б��ɾ��״̬
			this.rollbackAndMarkDelete();

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

	// �ع���沢����ض��б��ɾ�����߼�ɾ��
	public void rollbackAndMarkDelete() {
		try {
			if (!CollectionUtils.isEmpty(inventoryRollback)) {
				for(long queueId:inventoryRollback) {
					if(rollback(String.valueOf(queueId))) {
						//���ɾ��
						this.goodsInventoryDomainRepository.markQueueStatus(String.valueOf(queueId), (delStatus));
					}
					
				}
			}
			
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("markDelete")
					.addMetaData("errMsg", e.getMessage()), e);
			
		}
	}
	//�ع����
	public boolean rollback(String key) {
		try {
			//���ع�
			GoodsInventoryQueueDO queueDO = this.goodsInventoryDomainRepository
					.queryInventoryQueueDO(key);
			if (queueDO != null) {
				// �ع����
				if (queueDO.getGoodsId() > 0) {
					this.goodsInventoryDomainRepository.updateGoodsInventory(
							queueDO.getGoodsId(), (queueDO.getDeductNum()));
				}
				if (!CollectionUtils.isEmpty(queueDO.getSelectionParam())) {
					this.goodsInventoryDomainRepository
							.rollbackSelectionInventory(queueDO
									.getSelectionParam());
				}
				if (!CollectionUtils.isEmpty(queueDO.getSuppliersParam())) {
					this.goodsInventoryDomainRepository
							.rollbackSuppliersInventory(queueDO
									.getSuppliersParam());
				}
			}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("rollback").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return false;
		}
		return true;
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
