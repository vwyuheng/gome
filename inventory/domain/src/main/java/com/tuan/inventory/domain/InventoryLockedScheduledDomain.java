package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.InitCacheDomainRepository;
import com.tuan.inventory.domain.support.config.InventoryConfig;
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.HessianProxyUtil;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
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
	//缓存订单支付成功的队列id，以便处理完后将队列标记删除
	private ConcurrentHashSet<Long> markDelAftersendMsg;
	//缓存回滚库存的队列id，供redis库存回滚用
	private ConcurrentHashSet<Long> inventoryRollback;
	//缓存需回滚的商品id，供mysql回滚库存用
	private ConcurrentHashSet<Long> inventoryRollback4Mysql;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
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
			markDelAftersendMsg = new ConcurrentHashSet<Long>();
			inventoryRollback = new ConcurrentHashSet<Long>();
			inventoryRollback4Mysql = new ConcurrentHashSet<Long>();
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
								if (verifyId(model.getGoodsId())) {
									this.inventorySendMsg.add(model.getGoodsId());
									//缓存订单支付成功的队列id，以便处理完后将队列标记删除
									this.markDelAftersendMsg.add(model.getId());
								}
								   
							}else {
								if (verifyId(model.getId())) {
									this.inventoryRollback.add(model.getId());
									this.inventoryRollback4Mysql.add(model.getGoodsId());
								}
								   
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
						
						//将数据更新到mysql
						if(this.fillParamAndUpdate()) {
							this.sendNotify();
						}
					}
				}
				
			}
			//回滚异常库存并将相应异常队列标记删除
			this.rollbackAndMarkDelete();
			//已支付成功订单，消息发送后，将队列标记删除
			this.markDeleteAfterSendMsgSuccess();
			//回滚mysql库存
			this.rollback4Mysql();
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
	//回滚mysql数据
	public void rollback4Mysql() {
		try {
			if (!CollectionUtils.isEmpty(inventoryRollback4Mysql)) {
				for(long goodsId:inventoryRollback4Mysql) {
					if(verifyId(goodsId)) {
						if(loadMessageData(goodsId)) {
							//将数据更新到mysql
							this.fillParamAndUpdate();
						}
					}
					
				}
			}
			
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("rollback4Mysql")
					.addMetaData("errMsg", e.getMessage()), e);
			
		}
	}
	
	//异步更新mysql商品库存
		public boolean asynUpdateMysqlInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
			InventoryInitDomain create = new InventoryInitDomain();
			create.setGoodsId(goodsId);
			//注入相关Repository
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setInitCacheDomainRepository(this.initCacheDomainRepository);
			create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
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
	
	
	
	// 填充notifyserver发送参数
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() {
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
	//发送消息成功后将队列标记删除:逻辑删除
	public void markDeleteAfterSendMsgSuccess() {
		try {
			if (!CollectionUtils.isEmpty(markDelAftersendMsg)) {
				for(long queueId:markDelAftersendMsg) {
					if(verifyId(queueId)) {
						String member = this.goodsInventoryDomainRepository.queryMember(String.valueOf(queueId));
						if(!StringUtils.isEmpty(member)) {
							//标记删除【队列】,同时将缓存的队列删除
							this.goodsInventoryDomainRepository.markQueueStatusAndDeleteCacheMember(member, (delStatus),String.valueOf(queueId));
						}
						
					}
					
				}
			}
			
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("markDeleteAfterSendMsgSuccess")
					.addMetaData("errMsg", e.getMessage()), e);
			
		}
	}
	
	// 回滚库存并将相关队列标记删除：逻辑删除
	public void rollbackAndMarkDelete() {
		try {
			if (!CollectionUtils.isEmpty(inventoryRollback)) {
				for(long queueId:inventoryRollback) {
					if(rollback(String.valueOf(queueId))) {
						
						String member = this.goodsInventoryDomainRepository.queryMember(String.valueOf(queueId));
						if(!StringUtils.isEmpty(member)) {
							//标记删除【队列】,同时将缓存的队列删除
							this.goodsInventoryDomainRepository.markQueueStatusAndDeleteCacheMember(member, (delStatus),String.valueOf(queueId));
						}
						
					}
					
				}
			}
			
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("rollbackAndMarkDelete")
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

	
	public void setInitCacheDomainRepository(
			InitCacheDomainRepository initCacheDomainRepository) {
		this.initCacheDomainRepository = initCacheDomainRepository;
	}
	
	public void setInventoryInitAndUpdateHandle(
			InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle) {
		this.inventoryInitAndUpdateHandle = inventoryInitAndUpdateHandle;
	}
	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}

}
