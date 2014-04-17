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
	//库存需发更新消息的
	private ConcurrentHashSet<Long> inventorySendMsg;
	//库存需回滚的
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
	 * 业务处理前的预处理
	 */
	public void preHandler() {
		try {
			inventorySendMsg = new ConcurrentHashSet<Long>();
			inventoryRollback = new ConcurrentHashSet<Long>();
			// 商品库存是否存在
			//取初始状态队列信息
			List<GoodsInventoryQueueModel> queueList = goodsInventoryDomainRepository
					.queryInventoryQueueListByStatus(Double
							.valueOf(ResultStatusEnum.LOCKED.getCode()));
			if (!CollectionUtils.isEmpty(queueList)) {
				for (GoodsInventoryQueueModel model : queueList) {
					if(model.getCreateTime()<=DateUtils.getBeforXTimestamp10Long(param.getPeriod())) {
						//走hessian调用取订单支付状态
						OrderQueryService basic = (OrderQueryService) HessianProxyUtil
								.getObject(OrderQueryService.class,
										InventoryConfig.QUERY_URL);
						CallResult<OrderQueryResult>  cllResult= basic.queryOrderPayStatus( ClientNameEnum.INNER_SYSTEM.getValue(),"", String.valueOf(model.getOrderId()));
						OrderInfoPayStatusEnum statEnum = (OrderInfoPayStatusEnum) cllResult.getBusinessResult().getResultObject();
						if(statEnum!=null) {
							//1.当订单状态为已付款时
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

	// 业务处理
	public CreateInventoryResultEnum businessHandler() {

		try {
			// 业务检查前的预处理
			this.preHandler();
			if (!CollectionUtils.isEmpty(inventorySendMsg)) {
				for(long goodsId:inventorySendMsg) {
					if(loadMessageData(goodsId)) {
						this.sendNotify();
					}
				}
				
			}
			//消息发送完成后将取出的队列标记删除状态
			this.rollbackAndMarkDelete();

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

	// 回滚库存并将相关队列标记删除：逻辑删除
	public void rollbackAndMarkDelete() {
		try {
			if (!CollectionUtils.isEmpty(inventoryRollback)) {
				for(long queueId:inventoryRollback) {
					if(rollback(String.valueOf(queueId))) {
						//将缓存的队列删除
						this.goodsInventoryDomainRepository.deleteQueueMember(String.valueOf(queueId));
						//标记删除
						this.goodsInventoryDomainRepository.markQueueStatus(String.valueOf(queueId), (delStatus));
					}
					
				}
			}
			
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("markDelete")
					.addMetaData("errMsg", e.getMessage()), e);
			
		}
	}
	//回滚库存
	public boolean rollback(String key) {
		try {
			//库存回滚
			GoodsInventoryQueueDO queueDO = this.goodsInventoryDomainRepository
					.queryInventoryQueueDO(key);
			if (queueDO != null) {
				// 回滚库存
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
