package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.core.common.lock.eum.LockResultCodeEnum;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.core.common.lock.res.LockResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.result.CallResult;

public class WaterfloodAdjustmentDomain extends AbstractDomain {
	private static Log logger = LogFactory.getLog("INVENTORY.INIT");
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private AdjustWaterfloodParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private DLockImpl dLock;//分布式锁
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryDO;
	private GoodsSelectionDO selectionInventory;
	private GoodsSuppliersDO suppliersInventory;

	//商品id 参数
	private String goodsBaseId2str;
	private String goodsId2str;
	private String type;
	private String id;
	//注水调整值
	private int adjustNum;
	private String businessType;
	
	// 原商品注水
	private int originalgoodswfVal = 0;
	// 原选型或分店注水
	private int oriselOrSuppwfval = 0;
	
	// 调整后注水
	private int goodswfval = 0;
	// 调整后选型或分店注水:预留
	@SuppressWarnings("unused")
	private int selOrSuppwfval = 0;
		
	private Long goodsId;
	private Long selectionId;
	private Long suppliersId;
	private Long goodsBaseId;
	//调整后库存
	private long resultACK;
	private List<Long> ack;
	
	//选型
	private List<GoodsSelectionModel> selectionMsg;
	//分店
	private List<GoodsSuppliersModel> suppliersMsg;
	
	public WaterfloodAdjustmentDomain(String clientIp, String clientName,
			AdjustWaterfloodParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		long startTime = System.currentTimeMillis();
		String method = "WaterfloodAdjustmentDomain";
		final LogModel lm = LogModel.newLogModel(method);
		logger.info(lm.setMethod(method).addMetaData("start", startTime)
				.toJson(true));
		CreateInventoryResultEnum resultEnum = null;
		try {
			// 初始化检查
			resultEnum = this.initCheck();
			long endTime = System.currentTimeMillis();
			String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
					+ "milliseconds(毫秒)执行完成!";
			logger.info(lm.setMethod(method).addMetaData("endTime", endTime).addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId)
					.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(true));
			if(goodsId!=null&&goodsId>0) {
				//查询商品库存
				this.inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if (inventoryDO != null) {
					this.originalgoodswfVal = inventoryDO.getWaterfloodVal();
				} 
			}
			
			// 真正的业务检查处理
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF
						.getDescription();
				
			} else if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
					.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION
						.getDescription();
				// 查询商品选型库存
				this.selectionInventory = this.goodsInventoryDomainRepository
						.querySelectionRelationById(selectionId);
				if (selectionInventory != null) {
					this.oriselOrSuppwfval = selectionInventory
							.getWaterfloodVal();
				} 
			} else if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
					.getCode())) {
				this.suppliersId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription();
				// 查询商品分店库存
				this.suppliersInventory = this.goodsInventoryDomainRepository
						.querySuppliersInventoryById(suppliersId);
				if (suppliersInventory != null) {
					this.oriselOrSuppwfval = suppliersInventory
							.getWaterfloodVal();
				} 
			}
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("WaterfloodAdjustmentDomain errorMsg",
							"busiCheck error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
			return resultEnum;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 调整注水 正数：+ 负数：-
	public CreateInventoryResultEnum adjustWaterfloodVal() {
		String message = StringUtils.EMPTY;
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);

			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				if(inventoryDO!=null) {
					//调整注水数量
					inventoryDO.setWaterfloodVal(inventoryDO.getWaterfloodVal()+(adjustNum));
				}
				if(inventoryDO!=null&&inventoryDO.getWaterfloodVal()<0) {
					return CreateInventoryResultEnum.AFT_ADJUST_WATERFLOOD;
				}
				lm.addMetaData("adjustWaterfloodVal","adjustWaterfloodVal start").addMetaData("goodsId", goodsId).addMetaData("inventoryDO", inventoryDO);
				writeSysUpdateLog(lm,true);
				if(goodsId>0&&inventoryDO!=null) {
					CallResult<GoodsInventoryDO> callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,adjustNum,inventoryDO);
					PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();
						
						if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
							// 消息数据不存并且不成功
							message = "WaterfloodAdjustmentDomain.adjustWaterfloodVal_error[" + publicCodeEnum.getMessage()
									+ "]goodsId:" + goodsId;
							return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "WaterfloodAdjustmentDomain.adjustWaterfloodVal_success[save success]goodsId:" + goodsId;
							this.goodswfval = inventoryDO.getWaterfloodVal();
						}
						lm.addMetaData("adjustWaterfloodVal","adjustWaterfloodVal start").addMetaData("goodsId", goodsId).addMetaData("inventoryDO", inventoryDO).addMetaData("adjustNum", adjustNum).addMetaData("message", message);
						writeSysUpdateLog(lm,true);
				}
				lm.addMetaData("adjustWaterfloodVal","adjustWaterfloodVal end").addMetaData("message", message);
				writeSysUpdateLog(lm,true);
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				if(selectionInventory!=null) {
					//调整注水数量
					selectionInventory.setWaterfloodVal(selectionInventory.getWaterfloodVal()+(adjustNum));
					
					//选型的注水调整后也要调整商品总的注水量
					//调整注水数量
					inventoryDO.setWaterfloodVal(inventoryDO.getWaterfloodVal()+(adjustNum));
				}
				if(inventoryDO.getWaterfloodVal()<0) {  //注水暂只调总的，故目前只检查总的
					return CreateInventoryResultEnum.AFT_ADJUST_WATERFLOOD;
				}
				
				// 消费对列的信息
				CallResult<GoodsSelectionDO>  callResult = synInitAndAysnMysqlService.updateGoodsSelection(inventoryDO,selectionInventory);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					message = "updateGoodsSelection_error[" + publicCodeEnum.getMessage()
							+ "]selectionId:" + selectionInventory==null?"0":String.valueOf(selectionInventory.getId());
					return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
				} else {
					message = "updateGoodsSelection_success[save success]selectionId:" + selectionInventory==null?"0":String.valueOf(selectionInventory.getId());
					
				}

				this.ack = this.goodsInventoryDomainRepository.adjustSelectionWaterfloodById(goodsId,selectionId, (adjustNum));
					if(!verifyselOrsuppWf()) { //TODO maybe有问题
						//将注水还原到调整前
						this.goodsInventoryDomainRepository.adjustSelectionWaterfloodById(goodsId,selectionId, (-adjustNum));
						return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
					}else {
						this.goodswfval = inventoryDO.getWaterfloodVal();
						this.selOrSuppwfval = selectionInventory.getWaterfloodVal();
					}
				
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				if(suppliersInventory!=null) {
					//调整注水数量
					suppliersInventory.setWaterfloodVal(suppliersInventory.getWaterfloodVal()+(adjustNum));
					//分店的注水调整后也要调整商品总的注水量
					//调整注水数量
					inventoryDO.setWaterfloodVal(inventoryDO.getWaterfloodVal()+(adjustNum));
				}
				if(inventoryDO.getWaterfloodVal()<0) {  //注水暂只调总的，故目前只检查总的
					return CreateInventoryResultEnum.AFT_ADJUST_WATERFLOOD;
				}
				
				CallResult<GoodsSuppliersDO> callResult = synInitAndAysnMysqlService.updateGoodsSuppliers(inventoryDO,suppliersInventory);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					message = "updateGoodsSuppliers_error[" + publicCodeEnum.getMessage()
							+ "]suppliersId:" + suppliersInventory==null?"0":String.valueOf(suppliersInventory.getId());
					return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
				} else {
					message = "updateGoodsSuppliers_success[save success]suppliersId:" + suppliersInventory==null?"0":String.valueOf(suppliersInventory.getId());
				}
			
					this.ack = this.goodsInventoryDomainRepository.adjustSuppliersWaterfloodById(goodsId,suppliersId, (adjustNum));
					if(!verifyselOrsuppWf()) {  //TODO maybe有问题
						//将注水还原到调整前
						this.goodsInventoryDomainRepository.adjustSuppliersWaterfloodById(goodsId,suppliersId, (-adjustNum));
						return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
					}else {
						this.goodswfval = inventoryDO.getWaterfloodVal();
						this.selOrSuppwfval = selectionInventory.getWaterfloodVal();
					}
			}

		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"adjustWaterfloodVal error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	// 初始化参数
	private void fillParam() {
		this.goodsBaseId2str = param.getGoodsBaseId();
		//商品id，必传参数，该参数无论是选型还是分店都要传过来
		this.goodsId2str = param.getGoodsId();
		// 2:商品 4：选型 6：分店
		this.type = param.getType();
		// 2：可为空 4：选型id 6 分店id
		this.id = param.getId();
		this.adjustNum = param.getNum();
		
	}
	
	

	//发送库存新增消息
		public void sendNotify(){
			try {
				InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
				goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.WaterfloodAdjustmentDomain.toString(),JSONObject.fromObject(notifyParam));
				/*Type orderParamType = new TypeToken<NotifyCardOrder4ShopCenterParam>(){}.getType();
				String paramJson = new Gson().toJson(notifyParam, orderParamType);
				extensionService.sendNotifyServer(paramJson, lm.getTraceId());*/
			} catch (Exception e) {
				writeBusUpdateErrorLog(lm.addMetaData("errMsg", "sendNotify error"+e.getMessage()),false, e);
			}
		}
		
		//填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(){
					InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
					notifyParam.setUserId(Long.valueOf(param!=null&&StringUtils.isNotEmpty(param.getUserId())?param.getUserId():"0"));
					notifyParam.setGoodsBaseId(goodsBaseId);
					notifyParam.setGoodsId(goodsId);
					if(inventoryDO!=null) {
						notifyParam.setLimitStorage(inventoryDO.getLimitStorage());
						notifyParam.setWaterfloodVal(goodswfval);
						notifyParam.setTotalNumber(inventoryDO.getTotalNumber());
						notifyParam.setLeftNumber(inventoryDO.getLeftNumber());
						//库存总数 减 库存剩余
						Integer sales = inventoryDO.getGoodsSaleCount();
						//销量
						notifyParam.setSales(sales==null?0:sales);
						
					}
					//发送库存基表信息
					GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
					if(baseInventoryDO!=null) {
						notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
						notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
					}
					this.fillSelectionMsg();
					if(!CollectionUtils.isEmpty(selectionMsg)){
						
						notifyParam.setSelectionRelation(ObjectUtils.toSelectionMsgList(selectionMsg));
					}
					this.fillSuppliersMsg();
					if(!CollectionUtils.isEmpty(suppliersMsg)){
						
						notifyParam.setSuppliersRelation(ObjectUtils.toSuppliersMsgList(suppliersMsg));
					}
					return notifyParam;
				}
	
	//初始化库存
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum initCheck() {
				this.fillParam();
				this.goodsId = StringUtils.isEmpty(goodsId2str)?0:Long.valueOf(goodsId2str);
				if(StringUtils.isEmpty(goodsBaseId2str)) {  //为了兼容参数goodsbaseid不传的情况
					GoodsInventoryDO temp = this.goodsInventoryDomainRepository
							.queryGoodsInventory(goodsId);
					if(temp!=null) {
						this.goodsBaseId = temp.getGoodsBaseId();
						if(goodsBaseId!=null&&goodsBaseId==0) {
							// 初始化商品库存信息
							CallResult<GoodsInventoryDO> callGoodsInventoryDOResult = this.synInitAndAysnMysqlService
									.selectGoodsInventoryByGoodsId(goodsId);
							if (callGoodsInventoryDOResult != null&&callGoodsInventoryDOResult.isSuccess()) {
								temp = 	callGoodsInventoryDOResult.getBusinessResult();
								if(temp!=null) {
									this.goodsBaseId = temp.getGoodsBaseId();
								}
							}
						}
					}
				}else {
					this.goodsBaseId = Long.valueOf(goodsBaseId2str);
				}
				//初始化加分布式锁
				lm.addMetaData("WaterfloodAdjustmentDomain initCheck","initCheck,start").addMetaData("initCheck[" + (goodsId) + "]", goodsId);
				writeBusInitLog(lm,false);
				LockResult<String> lockResult = null;
				CreateInventoryResultEnum resultEnum = null;
				String key = DLockConstants.INIT_LOCK_KEY+"_goodsId_" + goodsId;
				try {
					lockResult = dLock.lockManualByTimes(key, DLockConstants.INIT_LOCK_TIME, DLockConstants.INIT_LOCK_RETRY_TIMES);
					if (lockResult == null
							|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
									.getCode()) {
						writeBusInitLog(
								lm.setMethod("WaterfloodAdjustmentDomain initCheck dLock").addMetaData("errorMsg",
										goodsId), false);
					}
					
					InventoryInitDomain create = new InventoryInitDomain(
							goodsId, lm);
					//注入相关Repository
					create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
					create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
					resultEnum = create.businessExecute();
				} finally{
					dLock.unlockManual(key);
				}
				lm.addMetaData("result", resultEnum);
				lm.addMetaData("result", "end");
				writeBusInitLog(lm,false);
				
				return resultEnum;
			}
			
	
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			updateActionDO.setGoodsBaseId(goodsBaseId);
			updateActionDO.setBusinessType(businessType);
			updateActionDO.setItem(StringUtil.handlerItem(type, goodsId2str, id));
			updateActionDO.setOriginalInventory(StringUtil.handlerOriWf(type, originalgoodswfVal, oriselOrSuppwfval));
			updateActionDO.setInventoryChange(String.valueOf(adjustNum));
			updateActionDO.setActionType(ResultStatusEnum.ADJUST_WATERFLOOD
					.getDescription());
			if(!StringUtils.isEmpty(param.getUserId())){
				updateActionDO.setUserId(Long.valueOf(param.getUserId()));
			}
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			//updateActionDO.setOrderId(0l);
			updateActionDO
					.setContent(JSON.toJSONString(param)); // 操作内容
			updateActionDO.setRemark("注水调整");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
			
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm
					.addMetaData("errMsg", "fillInventoryUpdateActionDO error"+e.getMessage()),false, e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}
	/*private boolean verifyWaterflood() {
		if(resultACK>=0) {
			return true;
		}else {
			return false;
		}
	}*/
	private boolean verifyselOrsuppWf() {
		boolean ret = true;
		//if(resultACK) {
		if(!CollectionUtils.isEmpty(ack)) {
			for(long result:ack) {
				if(result<0) {  //如果结果中存在小于0的则返回false
					ret = false;
					break;
				}
			}
			
		}else {
			return true;
		}
		return ret;
	}
	/**
	 * 参数检查
	 * 
	 * @return
	 */
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getGoodsId())) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}
		if (StringUtils.isEmpty(param.getType())) {
			return CreateInventoryResultEnum.INVALID_TYPE;
		}
		if (param.getType().equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())&&StringUtils.isEmpty(param.getId())) {
			return CreateInventoryResultEnum.INVALID_SELECTIONID;
		}
	    if (param.getType().equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())&&StringUtils.isEmpty(param.getId())) {
			return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	public void fillSelectionMsg() {
		List<GoodsSelectionModel> selectionMsg = new ArrayList<GoodsSelectionModel>();
		GoodsSelectionModel gsModel = new GoodsSelectionModel();
		try {
			gsModel.setGoodTypeId(selectionInventory.getGoodTypeId());
			gsModel.setGoodsId(goodsId);
			gsModel.setId(selectionId);
			gsModel.setLeftNumber(selectionInventory.getLeftNumber());  //调整后的库存值
			gsModel.setTotalNumber((selectionInventory.getTotalNumber()));
			gsModel.setUserId(Long.valueOf(param.getUserId()));
			gsModel.setLimitStorage(selectionInventory.getLimitStorage());
			gsModel.setWaterfloodVal((int)resultACK);
			selectionMsg.add(gsModel);
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm
					.addMetaData("errMsg", "fillSelectionMsg error"+e.getMessage()),false, e);
			this.selectionMsg = null;
		}
		this.selectionMsg = selectionMsg;
	}
	public void fillSuppliersMsg() {
		List<GoodsSuppliersModel> suppliersMsg = new ArrayList<GoodsSuppliersModel>();
		GoodsSuppliersModel gsModel = new GoodsSuppliersModel();
		try {
			gsModel.setSuppliersId(suppliersInventory.getSuppliersId());
			gsModel.setGoodsId(goodsId);
			gsModel.setId(suppliersId);
			gsModel.setLeftNumber(suppliersInventory.getLeftNumber());  //调整后的库存值
			gsModel.setTotalNumber((suppliersInventory.getTotalNumber()));
			gsModel.setUserId(Long.valueOf(param.getUserId()));
			gsModel.setLimitStorage(suppliersInventory.getLimitStorage());
			gsModel.setWaterfloodVal((int)resultACK);
			suppliersMsg.add(gsModel);
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm
					.addMetaData("errMsg", "fillSuppliersMsg error"+e.getMessage()),false, e);
			this.suppliersMsg = null;
		}
		this.suppliersMsg = suppliersMsg;
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

	public void setdLock(DLockImpl dLock) {
		this.dLock = dLock;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
