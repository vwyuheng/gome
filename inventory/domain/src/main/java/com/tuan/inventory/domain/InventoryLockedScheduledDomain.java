package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.config.InventoryConfig;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
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
import com.tuan.inventory.model.util.QueueConstant;
import com.tuan.ordercenter.backservice.OrderQueryService;
import com.tuan.ordercenter.model.enu.status.OrderInfoPayStatusEnum;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.OrderQueryResult;

public class InventoryLockedScheduledDomain extends AbstractDomain {
	private static Log logLock=LogFactory.getLog("LOCKED.JOB.LOG");
	private LogModel lm;
	private GoodsInventoryModel goodsInventoryModel;
	//库存需发更新消息的:需排重
	private ConcurrentHashSet<GoodsInventoryQueueModel> inventorySendMsg;
	//缓存回滚库存的队列id，供redis库存回滚用,回滚时不能排重
	private List<GoodsInventoryQueueModel> inventoryRollback = null;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	
	private InventoryScheduledParam param;
	private final int delStatus = 4;
	private List<GoodsSelectionDO> selectionInventoryList = null;
	private List<GoodsInventoryWMSDO> wmsList = null;
	private List<GoodsSuppliersDO> suppliersInventoryList = null;
	private GoodsInventoryDO inventoryInfoDO = null;
	private long goodsId = 0;
	private long goodsBaseId = 0;
	//private int limitStorage  = 0;
	//商品扣减量:非限制库存商品取默认值
	//private int limtStorgeDeNum = 0;
	
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
			//用于订单支付未成功时，缓存队列id
			//inventoryRollback = new ConcurrentHashSet<GoodsInventoryQueueModel>();
			inventoryRollback = new ArrayList<GoodsInventoryQueueModel>();
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
						long startTime = System.currentTimeMillis();
						String method = "OrderQueryService.queryOrderPayStatus,订单id:"+String.valueOf(model!=null?model.getOrderId():0);
						final LogModel lm = LogModel.newLogModel(method);
						logLock.info(lm.setMethod(method).addMetaData("start", startTime)
								.toJson(true));

						CallResult<OrderQueryResult>  cllResult= basic.queryOrderPayStatus( "INVENTORY_"+ClientNameEnum.INNER_SYSTEM.getValue(),"", String.valueOf(model.getOrderId()));
						OrderInfoPayStatusEnum statEnum = (OrderInfoPayStatusEnum) cllResult.getBusinessResult().getResultObject();
						if(statEnum!=null) {
							long endTime = System.currentTimeMillis();
							String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
									+ "milliseconds(毫秒)执行完成!(订单支付状态)statEnum="+statEnum.getDescription();
							logLock.info(lm.setMethod(method).addMetaData("endTime", endTime).addMetaData("订单id", model!=null?model.getOrderId():0)
									.addMetaData("runResult", runResult).addMetaData("message", statEnum.getDescription()).toJson(true));
							//1.当订单状态为已付款时
							if (statEnum
									.equals(OrderInfoPayStatusEnum.PAIED)) {
								if (verifyId(model.getGoodsId()!=null?model.getGoodsId():0)) {
									this.inventorySendMsg.add(model);
									
								}else {
									logLock.info("[订单状态为已付款时商品id不合法]队列详细信息model:("+model+")");
								}
								   
							}else {
								if (verifyId(model.getId()!=null?model.getId():0)) {
									this.inventoryRollback.add(model);
								}else {
									logLock.info("[订单状态为未付款时商品id不合法]队列详细信息model:("+model+")");
								}
								   
							}
						}//if
					}
					
				}
			}else {
				logLock.info("[InventoryLockedScheduledDomain:LockedTask]获取队列:("+ResultStatusEnum.LOCKED.getDescription()+"),状态为：("+ResultStatusEnum.LOCKED.getCode()+")的队列为空！");
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
				if(logLock.isDebugEnabled()) {
					logLock.info("[订单状态为已付款时商品]队列详细信息inventorySendMsg:("+inventorySendMsg+")");
				}
				for(GoodsInventoryQueueModel queueModel:inventorySendMsg) {
					long goodsId = 0;
					long queueId = 0;
					if(queueModel!=null) {
						 goodsId = queueModel.getGoodsId();
						 queueId = queueModel.getId();
						 if(!this.markDeleteAfterSendMsgSuccess(queueId,JSON.toJSONString(queueModel))) {
								logLock.info("[将队列状态标记为删除及删除缓存的队列状态失败!],queueId:("+queueId+")!!!");
							}else {
								//logLock.info("[队列状态标记删除状态及删除缓存的队列成功],queueId:("+queueId+"),end");
							}
					}
					
					
					//再加载数据发送消息
					if(loadMessageData(goodsId)) {
						//发送消息:支付成功此时redis中数据是最新的故直接发送消息删除状态后再更新mysql数据库
						if(!this.sendNotify()) {
							//消息
							logLock.info("[消息发送失败,]涉及队列queueId:("+queueId+")!!!");
						}
						//将数据更新到mysql
						if(this.fillParamAndUpdate()) {
							  //订单为已支付的，首先进行数据同步
					          updateDataEnum =  this.asynUpdateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
					          if (updateDataEnum!=null&&!(updateDataEnum.compareTo(updateDataEnum.SUCCESS) == 0)) {
					        	  logLock.info("[数据同步失败,failed]更新goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+"),message("+updateDataEnum.getDescription()+")"); 
								}else {
									//logLock.info("[同步数据成功,success],message("+updateDataEnum.getDescription()+")");
								}
						}else {
							logLock.info("[fillParamAndUpdate,填充数据失败]goodsId:("+goodsId+")");
						}
					}else {
						logLock.info("[loadMessageData,加载数据失败]goodsId:("+goodsId+")");
					}
				}
				
			}else {
				logLock.info("本次调度无确认已付款成功的队列需要处理!!!");
			}
			
			//处理回滚的库存
			if (!CollectionUtils.isEmpty(inventoryRollback)) {
				if(logLock.isDebugEnabled()) {
					logLock.info("[订单状态为未付款时商品]队列详细信息inventoryRollback:("+inventoryRollback+")");
				}
				for (GoodsInventoryQueueModel rollbackModel : inventoryRollback) {
				   //this.rollbackAndMarkDelete();
					if (rollbackModel != null) {
						long queueId = rollbackModel.getId();
						long goodsId = rollbackModel.getGoodsId();
						long goodsBaseId = rollbackModel.getGoodsBaseId();
						int limitStorage = rollbackModel.getLimitStorage();
						int  deductNum = rollbackModel.getDeductNum();
						long orderId = rollbackModel.getOrderId();
						List<GoodsSelectionAndSuppliersResult> selectionParamResult = ObjectUtils.toGoodsSelectionAndSuppliersList(rollbackModel.getSelectionParam());
						List<GoodsSelectionAndSuppliersResult> suppliersParamResult = ObjectUtils.toGoodsSelectionAndSuppliersList( rollbackModel.getSuppliersParam());
						//先回滚redis库存，
						if(this.rollback(orderId,goodsId, goodsBaseId,limitStorage,deductNum, selectionParamResult, suppliersParamResult)){
							//当redis回滚成功后，立即删除缓存的队列状态，以免被重复处理
							if(!this.markDeleteAfterSendMsgSuccess(queueId,JSON.toJSONString(rollbackModel))) {
								logLock.info("[标记队列状态为删除和删除缓存的队列失败!],涉及队列queueId:("+queueId+")!!!");
							}else {
								//logLock.info("[队列状态标记删除状态及删除缓存的队列成功],queueId:("+queueId+"),end");
							}
							//随后发送消息
							if(!this.sendNotify()) {
									logLock.info("[after rollback redis,sendmessage failed],queueId:"+queueId+",goodsId:("+goodsId+")");
								}
							//再同步mysql数据
							if(loadMessageData(goodsId)) {
								if(this.fillParamAndUpdate()) {
									// writeJobLog("[rollback,start]更新goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+"),goodsBaseId:("+goodsBaseId+")");
									  //订单为已支付的，首先进行数据同步:再更新mysql库存,
							          //updateDataEnum =  this.asynRestoreUpdateMysqlInventory(goodsId,goodsBaseId,limitStorage,deductNum,selectionParamResult,suppliersParamResult,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
							          updateDataEnum =  this.asynUpdateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
							          if (updateDataEnum!=null&&!(updateDataEnum.compareTo(updateDataEnum.SUCCESS) == 0)) {
							        	  logLock.info("[回滚库存时同步数据失败,failed]涉及goodsId:("+goodsId+"),inventoryInfoDO：("+inventoryInfoDO+"),selectionInventoryList:("+selectionInventoryList+"),wmsList:("+wmsList+"),message("+updateDataEnum.getDescription()+")");
										}else {
											// logLock.info("[回滚库存时同步数据成功,success],message("+updateDataEnum.getDescription()+")");
										}
					
								}
							}
						}else {
							logLock.info("库存回滚失败队列详细信息rollbackModel:("+rollbackModel+")");
						}
					}
				}
			}else {
				logLock.info("本次调度无确认付款未成功,库存需要回滚的队列需要处理!!!");
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
	public boolean sendNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
			if(notifyParam!=null) {
				this.goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryLockedScheduledDomain.toString(),JSONObject
						.fromObject(notifyParam));
			}
			
			/*
			 * Type orderParamType = new
			 * TypeToken<NotifyCardOrder4ShopCenterParam>(){}.getType(); String
			 * paramJson = new Gson().toJson(notifyParam, orderParamType);
			 * extensionService.sendNotifyServer(paramJson, lm.getTraceId());
			 */
		} catch (Exception e) {
			writeBusJobErrorLog(lm.addMetaData("errMsg", "sendNotify error"+e.getMessage()),false, e);
			return false;
		}
		return true;
	}
	
	//异步更新mysql商品库存
		public CreateInventoryResultEnum asynUpdateMysqlInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList,List<GoodsInventoryWMSDO> wmsList) {
			InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
			//create.setGoodsId(goodsId);
			//注入相关Repository
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			create.setLm(lm);
			return create.asynUpdateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
		}
		//异步还商品库存
		public CreateInventoryResultEnum asynRestoreUpdateMysqlInventory(long goodsId,long goodsBaseId,int limitStorage,int  deductNum,List<GoodsSelectionAndSuppliersResult> selectionParam,List<GoodsSelectionAndSuppliersResult> suppliersParam,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList,List<GoodsInventoryWMSDO> wmsList) {
			InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
			//create.setGoodsId(goodsId);
			//注入相关Repository
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			create.setLm(lm);
			return create.asynRestorUpdateInventory(goodsId,goodsBaseId,limitStorage,deductNum,selectionParam,suppliersParam,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsList);
		}
		/**
		 * 组装数据并更新
		 */
		private boolean fillParamAndUpdate() {
			
			try {
				 inventoryInfoDO = new GoodsInventoryDO();
				 goodsId = goodsInventoryModel.getGoodsId();
				 goodsBaseId = goodsInventoryModel.getGoodsBaseId();
				inventoryInfoDO.setGoodsId(goodsId);
				inventoryInfoDO.setGoodsBaseId(goodsBaseId);
				inventoryInfoDO.setGoodsSaleCount(goodsInventoryModel.getGoodsSaleCount());
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
				
			} catch (Exception e) {
				this.writeBusJobErrorLog(
						lm.addMetaData("errorMsg",
								"fillParamAndUpdate error" + e.getMessage()),false, e);
				return false;
			}
			return true;
		}
	
	
	
	// 填充notifyserver发送参数
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() throws Exception{
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		notifyParam.setUserId(goodsInventoryModel.getUserId());
		notifyParam.setGoodsId(goodsInventoryModel.getGoodsId());
		notifyParam.setGoodsBaseId(goodsBaseId);
		notifyParam.setLimitStorage(goodsInventoryModel.getLimitStorage());
		notifyParam.setWaterfloodVal(goodsInventoryModel.getWaterfloodVal());
		notifyParam.setTotalNumber(goodsInventoryModel.getTotalNumber());
		notifyParam.setLeftNumber(goodsInventoryModel.getLeftNumber());
		//库存总数 减 库存剩余
		Integer sales = goodsInventoryModel.getGoodsSaleCount();
		//销量
		notifyParam.setSales(sales==null?0:sales);
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSelectionList())) {
			notifyParam.setSelectionRelation(ObjectUtils.toSelectionMsgList(goodsInventoryModel.getGoodsSelectionList()));
		}
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSuppliersList())) {
			notifyParam.setSuppliersRelation(ObjectUtils.toSuppliersMsgList(goodsInventoryModel.getGoodsSuppliersList()));
		}
		GoodsBaseInventoryDO baseInventoryDO =goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
		if(baseInventoryDO!=null&&baseInventoryDO.getGoodsBaseId()!=null){
			notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
			notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
		}else {
			logLock.info("[GoodsBaseInventoryDO,非法数据]查询goodsBaseId:("+goodsBaseId+"),redis所存储baseInventoryDO状态:"+baseInventoryDO);
		}
		
		return notifyParam;
	}
	
	// 发送消息成功后将队列标记删除:逻辑删除
	public boolean markDeleteAfterSendMsgSuccess(long queueId,String queuemember) {
		try {
			if (verifyId(queueId)) {
				String member = this.goodsInventoryDomainRepository
						.queryMember(String.valueOf(queueId));
				if (!StringUtils.isEmpty(member)) {
					// 标记删除【队列】,同时将缓存的队列删除
					return this.goodsInventoryDomainRepository
							.markQueueStatusAndDeleteCacheMember(member,
									(delStatus), String.valueOf(queueId));
				}else {

					Double recv= this.goodsInventoryDomainRepository.zincrby(QueueConstant.QUEUE_SEND_MESSAGE, (delStatus),queuemember);
					if(recv== null||recv==0) {
						logLock.info("[获取缓存的队列为null,并且标记删除队列时也匹配不上]queueId:("+queueId+")");
						return true;
					}else {
						logLock.info("[获取缓存的队列为null,将队列标记删除成功!]queueId:("+queueId+")");
						return true;
					}
				}

			}else {
				//return false;
			}
			return true;
		} catch (Exception e) {
			this.writeBusJobErrorLog(lm.addMetaData("errMsg",
					"markDeleteAfterSendMsgSuccess error" + e.getMessage()),
					false, e);
			 return false;

		}
		 //return true;
	}
	
	public boolean rollback(long orderId,long goodsId,long goodsBaseId,int limitStorage,int  deductNum,List<GoodsSelectionAndSuppliersResult> selectionParam,List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		boolean success = true;
		try {
			// 回滚库存
			if (goodsId > 0) {
				logLock.info("rollback start!!!");
				int limtStorgeDeNum = 0;
				if(limitStorage==1) {
					limtStorgeDeNum = deductNum;
				}
				long startTime = System.currentTimeMillis();
				String method = "InventoryLockedScheduledDomain.rollback,订单id:"+String.valueOf(orderId);
				final LogModel lm = LogModel.newLogModel(method);
				logLock.info(lm.setMethod(method)
						.addMetaData("goodsId", goodsId)
						.addMetaData("goodsBaseId", goodsBaseId)
						.addMetaData("deductNum", deductNum)
						.addMetaData("limtStorgeDeNum", limtStorgeDeNum)
						.addMetaData("selectionParam", selectionParam)
						.addMetaData("suppliersParam", suppliersParam)
						.addMetaData("start", startTime)
						.toJson(true));
				List<Long> rollbackAftNum = this.goodsInventoryDomainRepository
						.updateGoodsInventory(goodsId,goodsBaseId,(limtStorgeDeNum), (deductNum));

				long endTime = System.currentTimeMillis();
				String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
						+ "milliseconds(毫秒)执行完成!(订单支付状态)rollbackAftNum="+rollbackAftNum;
				logLock.info(lm.setMethod(method).addMetaData("endTime", endTime)
						.addMetaData("runResult", runResult));
			}
			if (!CollectionUtils.isEmpty(selectionParam)) {
				long startTime = System.currentTimeMillis();
				String method = "InventoryLockedScheduledDomain.rollback,selectionParam订单id:"+String.valueOf(orderId);
				final LogModel lm = LogModel.newLogModel(method);
				logLock.info(lm.setMethod(method)
						.addMetaData("goodsId", goodsId)
						.addMetaData("goodsBaseId", goodsBaseId)
						.addMetaData("deductNum", deductNum)
						.addMetaData("selectionParam", selectionParam)
						.addMetaData("start", startTime)
						.toJson(true));
				boolean rollbackSelAck = this.goodsInventoryDomainRepository
						.rollbackSelectionInventory(selectionParam);
				long endTime = System.currentTimeMillis();
				String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
						+ "milliseconds(毫秒)执行完成!(订单支付状态)rollbackSelAck="+rollbackSelAck;
				logLock.info(lm.setMethod(method).addMetaData("endTime", endTime)
						.addMetaData("runResult", runResult));
			}
			if (!CollectionUtils.isEmpty(suppliersParam)) {
				long startTime = System.currentTimeMillis();
				String method = "InventoryLockedScheduledDomain.rollback,suppliersParam订单id:"+String.valueOf(orderId);
				final LogModel lm = LogModel.newLogModel(method);
				logLock.info(lm.setMethod(method)
						.addMetaData("goodsId", goodsId)
						.addMetaData("goodsBaseId", goodsBaseId)
						.addMetaData("deductNum", deductNum)
						.addMetaData("suppliersParam", suppliersParam)
						.addMetaData("start", startTime)
						.toJson(true));
				boolean rollbackSuppAck = this.goodsInventoryDomainRepository
						.rollbackSuppliersInventory(suppliersParam);
				long endTime = System.currentTimeMillis();
				String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
						+ "milliseconds(毫秒)执行完成!(订单支付状态)rollbackSuppAck="+rollbackSuppAck;
				logLock.info(lm.setMethod(method).addMetaData("endTime", endTime)
						.addMetaData("runResult", runResult));
			}
		} catch (Exception e) {
			success = false;
			this.writeBusJobErrorLog(lm
					.addMetaData("errMsg", "rollback error"+e.getMessage()),false, e);
		}
		return success;
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
	
	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}

}
