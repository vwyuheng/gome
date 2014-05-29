package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.config.InventoryConfig;
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
	private ConcurrentHashSet<GoodsInventoryQueueModel> inventorySendMsg;
	//缓存订单支付成功的队列id，以便处理完后将队列标记删除
	//private ConcurrentHashSet<Long> markDelAftersendMsg;
	//缓存回滚库存的队列id，供redis库存回滚用
	private ConcurrentHashSet<GoodsInventoryQueueModel> inventoryRollback;
	//缓存需回滚的商品id，供mysql回滚库存用
	//private ConcurrentHashSet<Long> inventoryRollback4Mysql;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	//private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	private InventoryScheduledParam param;
	private final int delStatus = 4;
	private List<GoodsSelectionDO> selectionInventoryList = null;
	private List<GoodsInventoryWMSDO> wmsList = null;
	private List<GoodsSuppliersDO> suppliersInventoryList = null;
	private GoodsInventoryDO inventoryInfoDO = null;
	private long goodsId = 0;
	
	
	//回滚
	//扣减的商品库存量
	//private int deductNum  = 0;
	//private List<GoodsSelectionAndSuppliersResult> selectionParam;
	//private List<GoodsSelectionAndSuppliersResult> suppliersParam;
	
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
	public boolean preHandler() {
		boolean preresult = true;
		try {
			//用于归集缓存商品id
			inventorySendMsg = new ConcurrentHashSet<GoodsInventoryQueueModel>();
			//用于缓存队列id:正常的
			//markDelAftersendMsg = new ConcurrentHashSet<Long>();
			//用于订单支付未成功时，缓存队列id
			inventoryRollback = new ConcurrentHashSet<GoodsInventoryQueueModel>();
			//inventoryRollback4Mysql = new ConcurrentHashSet<Long>();
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
						CallResult<OrderQueryResult>  cllResult= basic.queryOrderPayStatus( "INVENTORY_"+ClientNameEnum.INNER_SYSTEM.getValue(),"", String.valueOf(model.getOrderId()));
						OrderInfoPayStatusEnum statEnum = (OrderInfoPayStatusEnum) cllResult.getBusinessResult().getResultObject();
						if(statEnum!=null) {
							//1.当订单状态为已付款时
							if (statEnum
									.equals(OrderInfoPayStatusEnum.PAIED)) {
								if (verifyId(model.getGoodsId())) {
									this.inventorySendMsg.add(model);
									//this.inventorySendMsg.add(model.getGoodsId());
									//缓存订单支付成功的队列id，以便处理完后将队列标记删除
									//this.markDelAftersendMsg.add(model.getId());
								}
								   
							}else {
								if (verifyId(model.getId())) {
									//this.inventoryRollback.add(model.getId());
									this.inventoryRollback.add(model);
									//this.inventoryRollback4Mysql.add(model.getGoodsId());
								}
								   
							}
						}
					}
					
				}
			}else {
				writeJobLog("[LockedTask]获取队列:("+ResultStatusEnum.LOCKED.getDescription()+"),状态为：("+ResultStatusEnum.LOCKED.getCode()+")的队列为空！");
			}
		} catch (Exception e) {
			preresult = false;
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"preHandler error" + e.getMessage()),false, e);
		}
		return preresult;
		
	}

	// 业务处理
	@SuppressWarnings("static-access")
	public CreateInventoryResultEnum businessHandler() {
		try {
			 CreateInventoryResultEnum  updateDataEnum = null;
				// 业务检查前的预处理
				if(!preHandler()){
					return CreateInventoryResultEnum.SYS_ERROR;
				}
				
			if (!CollectionUtils.isEmpty(inventorySendMsg)) {
				for(GoodsInventoryQueueModel queueModel:inventorySendMsg) {
					long goodsId = queueModel.getGoodsId();
					long queueId = queueModel.getId();
					if(loadMessageData(goodsId)) {
						//将数据更新到mysql
						if(this.fillParamAndUpdate()) {
							  writeJobLog("[fillParamAndUpdate,start]更新goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+")");
							  //订单为已支付的，首先进行数据同步
					          updateDataEnum =  this.asynUpdateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
					          if (updateDataEnum != updateDataEnum.SUCCESS) { 
									 writeJobLog("[fillParamAndUpdate]更新goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+"),message("+updateDataEnum.getDescription()+")");
								}else {
									//发送消息
									this.sendNotify();
									//已支付成功订单，消息发送后，将队列标记删除
									this.markDeleteAfterSendMsgSuccess(queueId);
									 writeJobLog("[fillParamAndUpdate,end]更新goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+"),message("+updateDataEnum.getDescription()+")");
								}
						}
					}
				}
				
			}
			
			//处理回滚的库存
			if (!CollectionUtils.isEmpty(inventoryRollback)) {
				for (GoodsInventoryQueueModel rollbackModel : inventoryRollback) {
				   //this.rollbackAndMarkDelete();
					if (rollbackModel != null) {
						long queueId = rollbackModel.getId();
						long goodsId = rollbackModel.getGoodsId();
						int  deductNum = rollbackModel.getDeductNum();
						List<GoodsSelectionAndSuppliersResult> selectionParamResult = ObjectUtils.toGoodsSelectionAndSuppliersList(rollbackModel.getSelectionParam());
						List<GoodsSelectionAndSuppliersResult> suppliersParamResult = ObjectUtils.toGoodsSelectionAndSuppliersList( rollbackModel.getSuppliersParam());
						//先回滚redis库存，
						if(this.rollback(goodsId, deductNum, selectionParamResult, suppliersParamResult)){
							if(loadMessageData(goodsId)) {
								if(this.fillParamAndUpdate()) {
									 writeJobLog("[rollback,start]更新goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+")");
									  //订单为已支付的，首先进行数据同步:再更新mysql库存,
							          updateDataEnum =  this.asynUpdateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
							          if (updateDataEnum != updateDataEnum.SUCCESS) { 
											 writeJobLog("[rollback]更新goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+"),message("+updateDataEnum.getDescription()+")");
										}else {
											//发送消息:再发送消息
											this.sendNotify();
											//已支付成功订单，消息发送后，将队列标记删除
											this.markDeleteAfterSendMsgSuccess(queueId);
											 writeJobLog("[rollback,end]queueId:"+queueId+",goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+"),message("+updateDataEnum.getDescription()+")");
										}
					
								}
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"businessHandler error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	//加载消息数据
	public boolean loadMessageData(long goodsId) {
		try {
		this.goodsInventoryModel =	this.goodsInventoryDomainRepository.queryAllInventoryDataByGoodsId(goodsId);
		if(this.goodsInventoryModel==null){
			return false;
		}
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"loadMessageData error" + e.getMessage()),false, e);
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
			writeBusJobErrorLog(lm.addMetaData("errMsg", "sendNotify error"+e.getMessage()),false, e);
		}
	}
	
	//异步更新mysql商品库存
		public CreateInventoryResultEnum asynUpdateMysqlInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList,List<GoodsInventoryWMSDO> wmsList) {
			InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
			//create.setGoodsId(goodsId);
			//注入相关Repository
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			//create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
			return create.updateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
		}
		/**
		 * 组装数据并更新
		 */
		private boolean fillParamAndUpdate() {
			
			try {
				 inventoryInfoDO = new GoodsInventoryDO();
				 goodsId = goodsInventoryModel.getGoodsId();
				inventoryInfoDO.setGoodsId(goodsId);
				inventoryInfoDO.setLimitStorage(goodsInventoryModel
						.getLimitStorage());
				inventoryInfoDO.setWaterfloodVal(goodsInventoryModel
						.getWaterfloodVal());
				inventoryInfoDO.setTotalNumber(goodsInventoryModel
						.getTotalNumber());
				inventoryInfoDO.setLeftNumber(goodsInventoryModel
						.getLeftNumber());
				if (!CollectionUtils.isEmpty(goodsInventoryModel
						.getGoodsSelectionList())) {
					selectionInventoryList = new ArrayList<GoodsSelectionDO>();
					wmsList = new ArrayList<GoodsInventoryWMSDO>();
					List<GoodsSelectionModel> selectionModelList = goodsInventoryModel
							.getGoodsSelectionList();
					if (!CollectionUtils.isEmpty(selectionModelList)) {
						for (GoodsSelectionModel selModel : selectionModelList) {
							selectionInventoryList.add(ObjectUtils
									.toSelectionDO(selModel));
							wmsList.add(ObjectUtils.toWmsDO(selModel));//物流更新
						}
					}
				}
				if (!CollectionUtils.isEmpty(goodsInventoryModel
						.getGoodsSuppliersList())) {
					suppliersInventoryList = new ArrayList<GoodsSuppliersDO>();
					List<GoodsSuppliersModel> suppliersModelList = goodsInventoryModel
							.getGoodsSuppliersList();
					if (!CollectionUtils.isEmpty(suppliersModelList)) {
						for (GoodsSuppliersModel supModel : suppliersModelList) {
							suppliersInventoryList.add(ObjectUtils
									.toSuppliersDO(supModel));
						}
					}
				}
				//调用数据同步
				//return this.asynUpdateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
			} catch (Exception e) {
				this.writeBusJobErrorLog(
						lm.addMetaData("errorMsg",
								"fillParamAndUpdate error" + e.getMessage()),false, e);
				return false;
			}
			return true;
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
	public void markDeleteAfterSendMsgSuccess(long queueId) {
		try {
			if(verifyId(queueId)) {
						String member = this.goodsInventoryDomainRepository.queryMember(String.valueOf(queueId));
						if(!StringUtils.isEmpty(member)) {
							//标记删除【队列】,同时将缓存的队列删除
							this.goodsInventoryDomainRepository.markQueueStatusAndDeleteCacheMember(member, (delStatus),String.valueOf(queueId));
						}
						
					}
					
			
		} catch (Exception e) {
			this.writeBusJobErrorLog(lm
					.addMetaData("errMsg", "markDeleteAfterSendMsgSuccess error"+e.getMessage()),false, e);
			
		}
	}
	
	public boolean rollback(long goodsId,int  deductNum,List<GoodsSelectionAndSuppliersResult> selectionParam,List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		boolean success = true;
		try {
			// 回滚库存
			if (goodsId > 0) {
				writeJobLog("rollback goodsId=" + (goodsId) + "],"
						+ "deductNum=" + deductNum);
				Long rollbackAftNum = this.goodsInventoryDomainRepository
						.updateGoodsInventory(goodsId, (deductNum));

				writeJobLog("isRollback after[" + goodsId + "]"
						+ ",rollbackAftNum:" + rollbackAftNum);
			}
			if (!CollectionUtils.isEmpty(selectionParam)) {
				writeJobLog("rollback selectionParam=" + selectionParam);
				boolean rollbackSelAck = this.goodsInventoryDomainRepository
						.rollbackSelectionInventory(selectionParam);
				writeJobLog("isRollback selection end[" + selectionParam
						+ "],rollbackselresult:" + rollbackSelAck);
			}
			if (!CollectionUtils.isEmpty(suppliersParam)) {
				writeJobLog("isRollback suppliers start[" + suppliersParam
						+ "]");
				boolean rollbackSuppAck = this.goodsInventoryDomainRepository
						.rollbackSuppliersInventory(suppliersParam);
				writeJobLog("isRollback suppliers start[" + suppliersParam
						+ "],rollbacksuppresult:" + rollbackSuppAck);
			}
		} catch (Exception e) {
			success = false;
			this.writeBusJobErrorLog(lm
					.addMetaData("errMsg", "rollback error"+e.getMessage()),false, e);
		}
		return success;
	}

	//回滚库存 :暂留
	public boolean rollback(String key) {
		try {
			//库存回滚
			GoodsInventoryQueueDO queueDO = this.goodsInventoryDomainRepository
					.queryInventoryQueueDO(key);
			if (queueDO != null) {
				long goodsId = queueDO.getGoodsId();
				// 回滚库存
				if (goodsId > 0) {
					writeJobLog("rollback goodsId=" + (goodsId) + "],"
							+ "deductNum=" + queueDO.getDeductNum());
					Long rollbackAftNum =		this.goodsInventoryDomainRepository.updateGoodsInventory(
							queueDO.getGoodsId(), (queueDO.getDeductNum()));
					writeJobLog("isRollback after[" + goodsId + "]"
							+ ",rollbackAftNum:" + rollbackAftNum);
				}
				if (!CollectionUtils.isEmpty(queueDO.getSelectionParam())) {
					writeJobLog("rollback selectionParam=" + queueDO.getSelectionParam());
					boolean rollbackSelAck = 	this.goodsInventoryDomainRepository
							.rollbackSelectionInventory(queueDO
									.getSelectionParam());
					writeJobLog("isRollback selection end,rollbackselresult:" + rollbackSelAck);
				}
				if (!CollectionUtils.isEmpty(queueDO.getSuppliersParam())) {
					writeJobLog("isRollback suppliers start[" + queueDO.getSuppliersParam()
							+ "]");
					boolean rollbackSuppAck =	this.goodsInventoryDomainRepository
							.rollbackSuppliersInventory(queueDO
									.getSuppliersParam());
					writeJobLog("isRollback suppliers start,rollbacksuppresult:" + rollbackSuppAck);
				}
			}
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"rollback error" + e.getMessage()),false, e);
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

	public void setSynInitAndAysnMysqlService(
			SynInitAndAysnMysqlService synInitAndAysnMysqlService) {
		this.synInitAndAysnMysqlService = synInitAndAysnMysqlService;
	}
	/*public void setInventoryInitAndUpdateHandle(
			InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle) {
		this.inventoryInitAndUpdateHandle = inventoryInitAndUpdateHandle;
	}*/
	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}

}
