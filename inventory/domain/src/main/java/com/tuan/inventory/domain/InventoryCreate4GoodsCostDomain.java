package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.SynInitAndAsynUpdateDomainRepository;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.ext.InventoryCenterExtFacade;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.CreateInventory4GoodsCostParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.ordercenter.model.enu.ClientNameEnum;
import com.tuan.ordercenter.model.enu.res.UserOrderQueryEnum;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.OrderQueryResult;

public class InventoryCreate4GoodsCostDomain extends AbstractDomain {
	private static Log logupdate = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private CreateInventory4GoodsCostParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	private InventoryCenterExtFacade inventoryCenterExtFacade;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryInfoDO4OldGoods;
	private GoodsInventoryDO inventoryInfoDO4NewGoods;
	private List<GoodsSelectionDO> selectionRelation;
	private List<GoodsSuppliersDO> suppliersRelation;
	private String tokenid;  //redis序列,解决接口幂等问题
	private Long preGoodsId;// 改价前商品ID
	private Long goodsId;
	private Long goodsBaseId;
	private int limitStorage; // 0:库存无限制；1：限制库存
	private Long userId;
	// 改价前商品是否存在：初始化后才有效
	boolean isOldGoodsExists = false;
	boolean idemptent = false;
	
	public InventoryCreate4GoodsCostDomain(String clientIp, String clientName,
			CreateInventory4GoodsCostParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
		
	/***
	 * 改价前商品的处理
	 */
	public CreateInventoryResultEnum preGoodsCostHandler() {
		//改价前商品是否存在
		boolean	ispreExists = this.goodsInventoryDomainRepository.isExists(preGoodsId);
		if (!ispreExists) { //存在
			isOldGoodsExists = true;
			return CreateInventoryResultEnum.SUCCESS;
		} else { // 商品不存在
			//初始化改价前商品进来
			long startTime = System.currentTimeMillis();
			String method = "InventoryCreate4GoodsCostDomain,preGoodsId:"+preGoodsId;
			final LogModel lm = LogModel.newLogModel(method);
			if(logupdate.isDebugEnabled()) {
				logupdate.debug(lm.setMethod(method).addMetaData("start", startTime)
						.toJson(false));
			}
			
			//初始化检查
			CreateInventoryResultEnum resultEnum = this.initPreGoodsCostCheck();
				
				long endTime = System.currentTimeMillis();
				String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
						+ "milliseconds(毫秒)执行完成!";
				if(logupdate.isDebugEnabled()) {
					logupdate.debug(lm.setMethod(method).addMetaData("endTime", endTime).addMetaData("goodsBaseId", goodsBaseId).addMetaData("preGoodsId", preGoodsId)
							.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
				}
				
				
				if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
					return resultEnum;
				}else {
					isOldGoodsExists = true;
				}
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	//接口幂等控制
	public boolean idemptent() {
		//根据key取已缓存的tokenid  
		String gettokenid = goodsInventoryDomainRepository.queryToken(DLockConstants.CREATE_INVENTORY + "_"+ String.valueOf(goodsId));
		if(StringUtils.isEmpty(gettokenid)) {  //如果为空则任务是初始的http请求过来，将tokenid缓存起来
			if(StringUtils.isNotEmpty(tokenid)) {
				goodsInventoryDomainRepository.setTag(DLockConstants.CREATE_INVENTORY + "_"+ goodsId, DLockConstants.IDEMPOTENT_DURATION_TIME, tokenid);
			}
					
		}else {  //否则比对token值
			if(StringUtils.isNotEmpty(tokenid)) {
				if(tokenid.equalsIgnoreCase(gettokenid)) { //重复请求过来，判断是否处理成功
				//根据处理成功后设置的tag来判断之前http请求处理是否成功
				String gettag = goodsInventoryDomainRepository.queryToken(DLockConstants.CREATE_INVENTORY_SUCCESS + "_"+ tokenid);
				if(!StringUtils.isEmpty(gettag)&&gettag.equalsIgnoreCase(DLockConstants.HANDLER_SUCCESS)) { 
								return true;
							}
						}
					}
		}
		return false;
	}
	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		try {
			this.tokenid = param.getTokenid();
			 //幂等控制，已处理成功
			if (!StringUtils.isEmpty(tokenid)) { // if
				this.idemptent = idemptent();
				if(idemptent) {
					return CreateInventoryResultEnum.SUCCESS;
				}
			}
			this.preGoodsId = param.getPreGoodsId();
			this.goodsId = param.getGoodsId();
			this.goodsBaseId = param.getGoodsBaseId();
			limitStorage = param.getLimitStorage();
			this.userId = (param!=null&&param.getUserId()!=null)?param.getUserId():0;
			
			// 业务检查前的预处理
			CreateInventoryResultEnum preHander =	this.preGoodsCostHandler();
			if(preHander!=null&&!(preHander.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return preHander;
			}
			
			if(limitStorage==0) {  //非限制库存商品无需处理 ，0:库存无限制；1：限制库存
				//取老商品注水值
				GoodsInventoryDO	tmp4OldGoods = this.goodsInventoryDomainRepository.queryGoodsInventory(preGoodsId);
				//组装新商品信息
				this.fillNewInventoryDO(0,0,tmp4OldGoods.getWaterfloodVal());  //以供存储
				//this.inventoryInfoDO4OldGoods = tmp4OldGoods;
				return CreateInventoryResultEnum.SUCCESS;
			}
			
			//进行真正的业务处理
			//走hessian调用取订单支付状态
			/*OrderQueryService basic = (OrderQueryService) HessianProxyUtil
					.getObject(OrderQueryService.class,
							InventoryConfig.QUERY_URL);*/
			//初始化改价前商品进来
			long startTime = System.currentTimeMillis();
			String method = "OrderQueryService.queryNupayOrderGoodsNum,preGoodsId:"+preGoodsId;
			final LogModel lm = LogModel.newLogModel(method);
			logupdate.info(lm.setMethod(method).addMetaData("start", startTime)
					.toJson(false));
			CallResult<OrderQueryResult>  cllResult= inventoryCenterExtFacade.queryNupayOrderGoodsNum( "INVENTORY_"+ClientNameEnum.INNER_SYSTEM.getValue(),"", preGoodsId);
			UserOrderQueryEnum result = cllResult.getBusinessResult().getResult();
			int takeNum = 0;
			Long getTakeNum = 0L;
			if(result!=null&&result
					.equals(UserOrderQueryEnum.SUCCESS)) {
				//未支付订单占用库存量
				getTakeNum =  (Long) cllResult.getBusinessResult().getResultObject();
				takeNum = getTakeNum.intValue();
				long endTime = System.currentTimeMillis();
				String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
						+ "milliseconds(毫秒)执行完成!takeNum="+takeNum;
				logupdate.info(lm.setMethod(method).addMetaData("endTime", endTime).addMetaData("preGoodsId", preGoodsId)
						.addMetaData("runResult", runResult).addMetaData("message", result.getDescription()).toJson(false));
			} else {
				long endTime = System.currentTimeMillis();
				String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
						+ "milliseconds(毫秒)执行完成!";
				logupdate.info(lm.setMethod(method).addMetaData("endTime", endTime).addMetaData("preGoodsId", preGoodsId)
						.addMetaData("runResult", runResult).addMetaData("message", result.getDescription()).toJson(false));
				return CreateInventoryResultEnum.FAILED_ORDERQUERYSERVICE;
			}
			//加载goodsbase信息
			/*baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
			if(baseInventoryDO==null) {
				return CreateInventoryResultEnum.NO_GOODSBASE;
			}*/
			int oldTotalNum = 0;
			int oldLeftNum = 0;
			int atachNum = 0;
			GoodsInventoryDO inventoryDO4OldGoods = null;
			if (isOldGoodsExists) { // 改价前商品
				//将商品信息加载上来
				inventoryDO4OldGoods = this.goodsInventoryDomainRepository.queryGoodsInventory(preGoodsId);
				if(inventoryDO4OldGoods!=null) {
					//更新其总库存前保存下总库存数量
					oldTotalNum = inventoryDO4OldGoods.getTotalNumber();
					oldLeftNum = inventoryDO4OldGoods.getLeftNumber();
					atachNum = inventoryDO4OldGoods.getWaterfloodVal();
					/**
					 * 计算库存
	                 *老商品计算公式  总库存=已销售量+所占库存量  剩余库存=未付款订单所占库存
					 */
					//更新该老商品库存
					
					this.fillPreInventoryDO(inventoryDO4OldGoods.getGoodsSaleCount()+takeNum, takeNum, inventoryDO4OldGoods);
				}else {
					return CreateInventoryResultEnum.NO_GOODS;
				}
				
				
				
			}
		        // 改价商品
			/**
			 * 计算改价商品库存， 总库存=商品总库存【oldTotalNum】-inventoryInfoDO4OldGoods.getTotalNumber()[inventoryInfoDO4OldGoods.getGoodsSaleCount()+takeNum]
			 * 剩余库存= 总库存
			 */
			if(inventoryDO4OldGoods!=null) {
				if(takeNum>=oldLeftNum) {//占用库存量大于等于总库存数时
					//组装新商品信息
					this.fillNewInventoryDO(0,0,atachNum);  //以供存储
				}else {
					//组装信息
					this.fillNewInventoryDO(oldTotalNum-(inventoryDO4OldGoods.getGoodsSaleCount()+takeNum),oldTotalNum-(inventoryDO4OldGoods.getGoodsSaleCount()+takeNum),inventoryDO4OldGoods.getWaterfloodVal());  //以供存储
				}
				
			}
			
				
		} catch (Exception e) {
			
			logupdate.error(lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 新增库存
	public CreateInventoryResultEnum createInventory() {
		try {
			if(idemptent) {  //幂等控制，已处理成功
				return CreateInventoryResultEnum.SUCCESS;
			}
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			if(limitStorage==1) {  //非限制库存商品无需处理 ，0:库存无限制；1：限制库存
				//更新老商品的库存
				CreateInventoryResultEnum resultEnum = this.updatePreGoodsInventory();
				if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
					return resultEnum;
				}
			}
			
			
		} catch (Exception e) {
			
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryCreate4GoodsCostDomain createInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		//保存库存
		return this.saveNewGoodsInventory();
	}
	//保存改价商品
	public CreateInventoryResultEnum saveNewGoodsInventory() {
		InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
		//注入相关Repository
		create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
		create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		create.setLm(lm);
		return create.createInventory(tokenid,inventoryInfoDO4NewGoods, selectionRelation, suppliersRelation);
	}
	//更新改价前商品
	public CreateInventoryResultEnum updatePreGoodsInventory() {
		InventoryInitDomain create = new InventoryInitDomain(preGoodsId,lm);
		//注入相关Repository
		create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
		create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		create.setLm(lm);
		return create.updateInventory4GoodsCost(inventoryInfoDO4OldGoods, selectionRelation, suppliersRelation);
	}
	
	// 发送库存新增消息
	public void sendNotify() {
		try {
			if(limitStorage==1) {  //非限制库存商品无需处理 ，0:库存无限制；1：限制库存
				this.sendPreGoodsInventoryNotify();
				this.sendAftGoodsInventoryNotify();
			}
			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryCreate4GoodsCostDomain sendNotify error" + e.getMessage()).toJson(false), e);
		}
	}
	public void sendPreGoodsInventoryNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam(preGoodsId);
			goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryCreate4GoodsCostDomain_pre.toString(),JSONObject
					.fromObject(notifyParam));
			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryCreate4GoodsCostDomain sendPreGoodsInventoryNotify error" + e.getMessage()).toJson(false), e);
		}
	}
	public void sendAftGoodsInventoryNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam(goodsId);
			goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryCreate4GoodsCostDomain_aft.toString(),JSONObject
					.fromObject(notifyParam));
			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryCreate4GoodsCostDomain sendAftGoodsInventoryNotify error" + e.getMessage()).toJson(false), e);
		}
	}

	// 填充notifyserver发送参数
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(long goodsId) {
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		notifyParam.setUserId(this.userId);
		
		notifyParam.setGoodsId(goodsId);
		GoodsInventoryDO inventoryInfo  = goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
		if(inventoryInfo!=null) {
			notifyParam.setGoodsBaseId(inventoryInfo.getGoodsBaseId());
			notifyParam.setLimitStorage(inventoryInfo.getLimitStorage());
			notifyParam.setWaterfloodVal(inventoryInfo.getWaterfloodVal());
			notifyParam.setTotalNumber(inventoryInfo.getTotalNumber());
			notifyParam.setLeftNumber(inventoryInfo.getLeftNumber());
			//销量
			notifyParam.setSales(inventoryInfo.getGoodsSaleCount()==null?0:inventoryInfo.getGoodsSaleCount());
		}
		
		//库存基本信息
		GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById((goodsBaseId==null||goodsBaseId==0)?inventoryInfo.getGoodsBaseId():goodsBaseId);
		if(baseInventoryDO!=null) {
			notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
			notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
		}
		
		/*if (!CollectionUtils.isEmpty(selectionList)) {
			notifyParam.setSelectionRelation(ObjectUtils.toSelectionMsgList(selectionList));
		}
		if (!CollectionUtils.isEmpty(suppliersList)) {
			notifyParam.setSuppliersRelation(ObjectUtils.toSuppliersMsgList(suppliersList));
		}*/
		return notifyParam;
	}

	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsBaseId(goodsBaseId);
			updateActionDO.setGoodsId(goodsId);
			if (inventoryInfoDO4NewGoods != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SELF
						.getDescription());
				updateActionDO
						.setOriginalInventory(inventoryInfoDO4NewGoods.getLimitStorage() == 1 ? String
								.valueOf(inventoryInfoDO4NewGoods.getLeftNumber()) : String
								.valueOf(0));
				updateActionDO
						.setInventoryChange(inventoryInfoDO4NewGoods.getLimitStorage() == 1 ? String
								.valueOf(inventoryInfoDO4NewGoods.getLeftNumber()) : String
								.valueOf(0));
			}
			
			updateActionDO.setActionType(ResultStatusEnum.ADJUST_GOODSPRICE
					.getDescription());
			updateActionDO.setUserId(userId);
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setOrderId(0l);
			if (isOldGoodsExists) { // 改价前商品
			//将商品信息加载上来
		    GoodsInventoryDO oldGoods = this.goodsInventoryDomainRepository.queryGoodsInventory(preGoodsId);
			updateActionDO.setContent("inventoryInfoDO4NewGoods:["+(inventoryInfoDO4NewGoods!=null?JSON.toJSONString(inventoryInfoDO4NewGoods):"inventoryInfoDO4NewGoods is null!")+"],inventoryInfoDO4OldGoods:["+(oldGoods!=null?JSON.toJSONString(oldGoods):"oldGoods is null!")+"]"); // 操作内容
			}
			updateActionDO.setRemark("商品改价,preGoodsId("+preGoodsId+"),goodsId("+goodsId+")");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryCreate4GoodsCostDomain fillInventoryUpdateActionDO error" + e.getMessage()).toJson(false), e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}

	// 参数检查
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		//改价商品id校验
		if (param.getGoodsId()==null) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}
		if(param.getGoodsId()==0) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}
		//改价前商品id校验
		if (param.getPreGoodsId()==null) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}
		if (param.getPreGoodsId()==0) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}
		
		if (param.getGoodsBaseId()==null) {
			return CreateInventoryResultEnum.INVALID_GOODSBASEID;
		}
		if (param.getGoodsBaseId()!=null&&param.getGoodsBaseId()==0) {
			return CreateInventoryResultEnum.INVALID_GOODSBASEID;
		}

		return CreateInventoryResultEnum.SUCCESS;
	}

	//组装改价商品信息
	public void fillPreInventoryDO(int totalNumber,int takeNum,GoodsInventoryDO oldDO) {
		GoodsInventoryDO preInventoryInfoDO = new GoodsInventoryDO();
		try {
			if(takeNum>=oldDO.getLeftNumber()) {//占用库存量大于等于总库存数时
				preInventoryInfoDO = oldDO;
			}else {
				preInventoryInfoDO.setGoodsId(preGoodsId);
				preInventoryInfoDO.setGoodsBaseId(goodsBaseId);
				preInventoryInfoDO.setLeftNumber(takeNum);
				preInventoryInfoDO.setTotalNumber(totalNumber);
				preInventoryInfoDO.setLimitStorage(oldDO.getLimitStorage());
				preInventoryInfoDO.setUserId(oldDO.getUserId());
				preInventoryInfoDO.setWaterfloodVal(oldDO.getWaterfloodVal());
				//商品库存销量
				preInventoryInfoDO.setGoodsSaleCount(oldDO.getGoodsSaleCount());
				preInventoryInfoDO.setGoodsSelectionIds(oldDO.getGoodsSelectionIds());
				preInventoryInfoDO.setIsAddGoodsSelection(oldDO.getIsAddGoodsSelection());
				preInventoryInfoDO.setIsDirectConsumption(oldDO.getIsDirectConsumption());
				preInventoryInfoDO.setWmsId(oldDO.getWmsId());
			}
			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryCreate4GoodsCostDomain fillPreInventoryDO error" + e.getMessage()).toJson(false), e);
			this.inventoryInfoDO4OldGoods = null;
		}
		this.inventoryInfoDO4OldGoods = preInventoryInfoDO;
	}
	//组装改价商品信息
	public void fillNewInventoryDO(int totalNumber,int leftNumber,int attachSales) {
		GoodsInventoryDO inventoryInfoDO = new GoodsInventoryDO();
		try {
			inventoryInfoDO.setGoodsId(goodsId);
		    inventoryInfoDO.setGoodsBaseId(goodsBaseId);
			inventoryInfoDO.setLeftNumber(leftNumber);
			inventoryInfoDO.setTotalNumber(totalNumber);
			inventoryInfoDO.setLimitStorage(param.getLimitStorage());
			inventoryInfoDO.setUserId(userId);
			inventoryInfoDO.setWaterfloodVal(attachSales);
			//商品库存销量
			inventoryInfoDO.setGoodsSaleCount(0);

		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryCreate4GoodsCostDomain fillInventoryDO error" + e.getMessage()).toJson(false), e);
			this.inventoryInfoDO4NewGoods = null;
		}
		this.inventoryInfoDO4NewGoods = inventoryInfoDO;
	}

	// 初始化改价前商品
	public CreateInventoryResultEnum initPreGoodsCostCheck() {
		CreateInventoryResultEnum resultEnum = null;

			InventoryInitDomain create = new InventoryInitDomain(preGoodsId, lm);
			// 注入相关Repository
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			resultEnum = create.businessExecute();

		return resultEnum;
	}
	
	
	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	
	public void setSynInitAndAysnMysqlService(
			SynInitAndAysnMysqlService synInitAndAysnMysqlService) {
		this.synInitAndAysnMysqlService = synInitAndAysnMysqlService;
	}
	
	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}
	public SynInitAndAsynUpdateDomainRepository getSynInitAndAsynUpdateDomainRepository() {
		return synInitAndAsynUpdateDomainRepository;
	}
	public void setSynInitAndAsynUpdateDomainRepository(
			SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository) {
		this.synInitAndAsynUpdateDomainRepository = synInitAndAsynUpdateDomainRepository;
	}

	public void setInventoryCenterExtFacade(
			InventoryCenterExtFacade inventoryCenterExtFacade) {
		this.inventoryCenterExtFacade = inventoryCenterExtFacade;
	}
	

}
