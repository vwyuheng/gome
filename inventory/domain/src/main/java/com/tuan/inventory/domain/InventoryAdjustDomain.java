package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

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
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.SelectionNotifyMessageParam;
import com.tuan.inventory.model.param.SuppliersNotifyMessageParam;
import com.tuan.inventory.model.result.CallResult;

public class InventoryAdjustDomain extends AbstractDomain {
	
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private AdjustInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	//private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	private DLockImpl dLock;//分布式锁
	
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryDO;
	private GoodsSelectionDO selectionInventory;
	private GoodsSuppliersDO suppliersInventory;
	//选型
	private List<SelectionNotifyMessageParam> selectionMsg;
	//分店
	private List<SuppliersNotifyMessageParam> suppliersMsg;
	private String goodsBaseId2str;
	private String goodsId2str;
	private String type;
	private String id;
	private int adjustNum;
	private String businessType;
	// 原剩余库存
	private int origoodsleftnum = 0;
	// 原总库存
	private int origoodstotalnum = 0;
	// 原选型剩余库存
	private int oriselOrSuppleftnum = 0;
	// 原选型总库存
	private int oriselOrSupptotalnum = 0;
	
	// 调整后剩余库存
	private int goodsleftnum = 0;
	// 调整后总库存
	private int goodstotalnum = 0;
	// 调整后剩余库存
	private int selOrSuppleftnum = 0;
	// 调整后总库存
	private int selOrSupptotalnum = 0;
	private Long goodsId;
	private Long goodsBaseId;
	private long selectionId;
	private long suppliersId;
	//调整后库存
	private List<Long> resultACK;
	
	private int limitStorage = 0;
	
	public InventoryAdjustDomain(String clientIp, String clientName,
			AdjustInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		CreateInventoryResultEnum resultEnum = null;
		try {
			//初始化检查
			resultEnum = this.initCheck();
			
			lm.addMetaData("init","init,after").addMetaData("init[InventoryAdjustDomain," + (goodsId) + "]", goodsId).addMetaData("message", resultEnum.getDescription());
			writeBusInitLog(lm,true);
			
			if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return resultEnum;
			}
			//真正的库存调整业务处理
			if(goodsId!=null&&goodsId>0) {
				//查询商品库存
				this.inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if(inventoryDO!=null&&inventoryDO.getLimitStorage()==1) {
					this.origoodsleftnum = inventoryDO.getLeftNumber();
					this.origoodstotalnum = inventoryDO.getTotalNumber();
					//此时调整后数量检查
					/*if(origoodsleftnum+(adjustNum)<0||origoodstotalnum+(adjustNum)<0) {
						return CreateInventoryResultEnum.AFT_ADJUST_INVENTORY;
					}*/
				}
			}
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF.getDescription();
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION.getDescription();
				
				//查询商品选型库存
				this.selectionInventory = this.goodsInventoryDomainRepository.querySelectionRelationById(selectionId);
				if(selectionInventory!=null&&selectionInventory.getLimitStorage()==1) {
					this.oriselOrSuppleftnum = selectionInventory.getLeftNumber();
					this.oriselOrSupptotalnum = selectionInventory.getTotalNumber();
					
					//此时调整后数量检查
					/*if(oriselOrSuppleftnum+(adjustNum)<0||oriselOrSupptotalnum+(adjustNum)<0) {
						return CreateInventoryResultEnum.AFT_ADJUST_INVENTORY;
					}*/
				}
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.suppliersId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SUPPLIERS.getDescription();
				//查询商品分店库存
				this.suppliersInventory = this.goodsInventoryDomainRepository.querySuppliersInventoryById(suppliersId);
				if(suppliersInventory!=null&&suppliersInventory.getLimitStorage()==1) {
					this.oriselOrSuppleftnum = suppliersInventory.getLeftNumber();
					this.oriselOrSupptotalnum = suppliersInventory.getTotalNumber();
					
					//此时调整后数量检查
					/*if(oriselOrSuppleftnum+(adjustNum)<0||oriselOrSupptotalnum+(adjustNum)<0) {
						return CreateInventoryResultEnum.AFT_ADJUST_INVENTORY;
					}*/
				}
			}
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()),false, e);
			
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存 正数：+ 负数：-
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum adjustInventory() {
		String message = StringUtils.EMPTY;
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			//初始化加分布式锁
			lm.addMetaData("adjustInventory","adjustInventory,start").addMetaData("goodsId", goodsId).addMetaData("type", type);
			writeSysUpdateLog(lm,false);
			LockResult<String> lockResult = null;
			String key = DLockConstants.ADJUST_LOCK_KEY+"_goodsId_" + goodsId+"_type_"+type;
			try {
				lockResult = dLock.lockManualByTimes(key, DLockConstants.ADJUSTK_LOCK_TIME, DLockConstants.ADJUST_LOCK_RETRY_TIMES);
				if (lockResult == null
						|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
								.getCode()) {
					writeSysUpdateLog(
							lm.setMethod("adjustInventory").addMetaData("goodsId",
									goodsId).addMetaData("type",
											type).addMetaData("errorMsg",
													"adjustInventory dlock error"), true);
				}
				if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
					if (inventoryDO != null&&inventoryDO.getLimitStorage()==1) {
						
						if(origoodstotalnum+(adjustNum)<0) {
							// 调整剩余库存数量
							inventoryDO.setLeftNumber(0);
							inventoryDO.setTotalNumber(0);
							inventoryDO.setLimitStorage(0);  //变为无限量
						}else {
							// 调整剩余库存数量
							inventoryDO.setLeftNumber(inventoryDO.getLeftNumber()
									+ (adjustNum));
							// 调整商品总库存数量
							inventoryDO.setTotalNumber(inventoryDO.getTotalNumber()
									+ (adjustNum));
						}
						inventoryDO.setGoodsBaseId(goodsBaseId);  //更新goodsbaseid
					}
					if(inventoryDO!=null&&goodsId>0) {
						lm.addMetaData("adjustInventory","adjustInventory mysql,start").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("limitStorage", limitStorage);
						writeSysUpdateLog(lm,true);
						 // 消费对列的信息
						//String goodsBaseId =param.getGoodsBaseId();
						
						CallResult<GoodsInventoryDO> callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,this.goodsBaseId,(adjustNum),limitStorage,inventoryDO);
						PublicCodeEnum publicCodeEnum = callResult.getPublicCodeEnum();
                        if(publicCodeEnum == PublicCodeEnum.SUCCESS){
                        	
                        }
						if (publicCodeEnum != PublicCodeEnum.SUCCESS) { //
							// 消息数据不存并且不成功
							message = "updateGoodsInventory_error["
									+ publicCodeEnum.getMessage() + "]goodsId:"
									+ goodsId;
							return CreateInventoryResultEnum
									.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "updateGoodsInventory_success[save success]goodsId:"
									+ goodsId;
							this.goodsleftnum = inventoryDO.getLeftNumber();
							this.goodstotalnum = inventoryDO.getTotalNumber();
						}
						lm.addMetaData("adjustInventory","adjustInventory mysql,end").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("limitStorage", limitStorage).addMetaData("message", message);
						writeSysUpdateLog(lm,true);
					}
				} else if (type
						.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
								.getCode())) {
					if (selectionInventory != null) {
						//调整前校验

						//调整剩余库存数量
						selectionInventory.setLeftNumber(selectionInventory
								.getLeftNumber() + (adjustNum));
						//调整商品选型总库存数量
						selectionInventory.setTotalNumber(selectionInventory
								.getTotalNumber() + (adjustNum));

						//同时调整商品总的库存的剩余和总库存量
						//调整剩余库存数量
						inventoryDO.setLeftNumber(inventoryDO.getLeftNumber()
								+ (adjustNum));
						//调整商品总库存数量
						inventoryDO.setTotalNumber(inventoryDO.getTotalNumber()
								+ (adjustNum));
					}
					if (inventoryDO.getLeftNumber() < 0
							|| inventoryDO.getTotalNumber() < 0
							|| selectionInventory.getLeftNumber() < 0
							|| selectionInventory.getTotalNumber() < 0) {
						return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
					}
					lm.addMetaData("adjustInventory","adjustInventory mysql,start").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("selectionInventory", selectionInventory);
					writeSysUpdateLog(lm,true);
					
					CallResult<GoodsSelectionDO> callResult  = null;
					// 消费对列的信息
					callResult = synInitAndAysnMysqlService.updateGoodsSelection(inventoryDO,selectionInventory);
					PublicCodeEnum publicCodeEnum = callResult
							.getPublicCodeEnum();
					
					if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
						// 消息数据不存并且不成功
						message = "updateGoodsSelection_error[" + publicCodeEnum.getMessage()
								+ "]selectionId:" + selectionInventory==null?String.valueOf(0):String.valueOf(selectionInventory.getId());
						return CreateInventoryResultEnum
								.valueOfEnum(publicCodeEnum.getCode());
					} else {
						message = "updateGoodsSelection_success[save success]selectionId:" + selectionInventory==null?String.valueOf(0):String.valueOf(selectionInventory.getId());
					}
					
					lm.addMetaData("adjustInventory","adjustInventory mysql,end").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("selectionInventory", selectionInventory).addMetaData("message", message);
					writeSysUpdateLog(lm,true);
					lm.addMetaData("adjustInventory","adjustInventory redis,start").addMetaData("goodsId", goodsId).addMetaData("selectionId", selectionId).addMetaData("type", type).addMetaData("adjustNum", adjustNum);
					writeSysUpdateLog(lm,true);
						this.resultACK = this.goodsInventoryDomainRepository
								.adjustSelectionInventoryById(goodsId,
										selectionId, (adjustNum));
						lm.addMetaData("adjustInventory","adjustInventory redis,end").addMetaData("goodsId", goodsId).addMetaData("selectionId", selectionId).addMetaData("type", type).addMetaData("adjustNum", adjustNum).addMetaData("resultACK", resultACK);
						writeSysUpdateLog(lm,true);
						if (!verifyInventory()) { //TODO 这地有问题，暂时影响可能不大
							
							lm.addMetaData("adjustInventory","rollback redis,start").addMetaData("goodsId", goodsId).addMetaData("selectionId", selectionId).addMetaData("type", type).addMetaData("adjustNum", adjustNum);
							writeSysUpdateLog(lm,true);
							//将库存还原到调整前
							List<Long> rollbackResponeResult = this.goodsInventoryDomainRepository
									.adjustSelectionInventoryById(goodsId,
											selectionId, (-adjustNum));
							lm.addMetaData("adjustInventory","rollback redis,end").addMetaData("goodsId", goodsId).addMetaData("selectionId", selectionId).addMetaData("type", type).addMetaData("adjustNum", adjustNum).addMetaData("rollbackResponeResult", rollbackResponeResult);
							writeSysUpdateLog(lm,true);
							return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
						} else {
							this.goodsleftnum = inventoryDO.getLeftNumber();
							this.goodstotalnum = inventoryDO.getTotalNumber();
							this.selOrSuppleftnum = selectionInventory
									.getLeftNumber();
							this.selOrSupptotalnum = selectionInventory
									.getTotalNumber();
						}

				} else if (type
						.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
								.getCode())) {
					if (suppliersInventory != null) {
						//调整剩余库存数量
						suppliersInventory.setLeftNumber(suppliersInventory
								.getLeftNumber() + (adjustNum));
						//调整商品分店总库存数量
						suppliersInventory.setTotalNumber(suppliersInventory
								.getTotalNumber() + (adjustNum));

						//同时调整商品总的库存的剩余和总库存量
						//调整剩余库存数量
						inventoryDO.setLeftNumber(inventoryDO.getLeftNumber()
								+ (adjustNum));
						//调整商品总库存数量
						inventoryDO.setTotalNumber(inventoryDO.getTotalNumber()
								+ (adjustNum));
					}
					if (inventoryDO.getLeftNumber() < 0
							|| inventoryDO.getTotalNumber() < 0
							|| selectionInventory.getLeftNumber() < 0
							|| selectionInventory.getTotalNumber() < 0) {
						return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
					}
					lm.addMetaData("adjustInventory","adjustInventory suppliers mysql,start").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("suppliersInventory", suppliersInventory);
					writeSysUpdateLog(lm,true);
				
					CallResult<GoodsSuppliersDO> callResult = synInitAndAysnMysqlService.updateGoodsSuppliers(inventoryDO,suppliersInventory);
					PublicCodeEnum publicCodeEnum = callResult
							.getPublicCodeEnum();
					
					if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
						// 消息数据不存并且不成功
						message = "updateGoodsSuppliers_error[" + publicCodeEnum.getMessage()
								+ "]suppliersId:" + suppliersInventory==null?"0":String.valueOf(suppliersInventory.getId());
						return CreateInventoryResultEnum
								.valueOfEnum(publicCodeEnum.getCode());
					} else {
						message = "updateGoodsSuppliers_success[save success]suppliersId:" + suppliersInventory==null?"0":String.valueOf(suppliersInventory.getId());
					}
					lm.addMetaData("adjustInventory","adjustInventory mysql,end").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("suppliersInventory", suppliersInventory).addMetaData("message", message);
					writeSysUpdateLog(lm,true);
				
						lm.addMetaData("adjustInventory","adjustInventory redis,start").addMetaData("goodsId", goodsId).addMetaData("suppliersId", suppliersId).addMetaData("type", type).addMetaData("adjustNum", adjustNum);
						writeSysUpdateLog(lm,true);
						this.resultACK = this.goodsInventoryDomainRepository
								.adjustSuppliersInventoryById(goodsId,
										suppliersId, (adjustNum));
						lm.addMetaData("adjustInventory","adjustInventory redis,end").addMetaData("goodsId", goodsId).addMetaData("suppliersId", suppliersId).addMetaData("type", type).addMetaData("adjustNum", adjustNum).addMetaData("resultACK", resultACK);
						writeSysUpdateLog(lm,true);
						if (!verifyInventory()) { //TODO 这地有问题，暂时影响可能不大
							lm.addMetaData("adjustInventory","rollback redis,start").addMetaData("goodsId", goodsId).addMetaData("suppliersId", suppliersId).addMetaData("type", type).addMetaData("adjustNum", adjustNum);
							writeSysUpdateLog(lm,true);
							//将库存还原到调整前
							List<Long> rollbackResponeResult = this.goodsInventoryDomainRepository
									.adjustSuppliersInventoryById(goodsId,
											suppliersId, (-adjustNum));
							lm.addMetaData("adjustInventory","rollback redis,end").addMetaData("goodsId", goodsId).addMetaData("suppliersId", suppliersId).addMetaData("type", type).addMetaData("adjustNum", adjustNum).addMetaData("rollbackResponeResult", rollbackResponeResult);
							writeSysUpdateLog(lm,true);
							return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
						} else {
							this.goodsleftnum = inventoryDO.getLeftNumber();
							this.goodstotalnum = inventoryDO.getTotalNumber();
							this.selOrSuppleftnum = suppliersInventory
									.getLeftNumber();
							this.selOrSupptotalnum = suppliersInventory
									.getTotalNumber();
						}

				}//else SUPPLIERS
			} finally{
				dLock.unlockManual(key);
			}
			lm.addMetaData("result", "end");
			writeSysUpdateLog(lm,false);
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"adjustInventory error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	//发送库存新增消息
		public void sendNotify(){
			try {
				InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
				goodsInventoryDomainRepository.sendNotifyServerMessage(JSONObject.fromObject(notifyParam));
				/*Type orderParamType = new TypeToken<NotifyCardOrder4ShopCenterParam>(){}.getType();
				String paramJson = new Gson().toJson(notifyParam, orderParamType);
				extensionService.sendNotifyServer(paramJson, lm.getTraceId());*/
			} catch (Exception e) {
				this.writeBusUpdateErrorLog(
						lm.setMethod("sendNotify").addMetaData("errorMsg",e.getMessage()),false, e);
			}
		}
		// 初始化参数
		private void fillParam() {
			this.goodsBaseId2str = param.getGoodsBaseId();
			//商品id，必传参数，该参数无论是选型还是分店都要传过来
			this.goodsId2str = param.getGoodsId();
			// 2:商品 4：选型 6：分店
			this.type = param.getType();
			// 2：可不传 4：选型id 6 分店id
			this.id = param.getId();
			this.adjustNum = param.getNum();
			
		}
		//填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(){
			InventoryNotifyMessageParam notifyParam = null;
			try {
				notifyParam = new InventoryNotifyMessageParam();
				notifyParam.setUserId(Long.valueOf(param.getUserId()));
				notifyParam.setGoodsId(goodsId);
				if(inventoryDO!=null) {
					notifyParam.setLimitStorage(inventoryDO.getLimitStorage());
					notifyParam.setWaterfloodVal(inventoryDO.getWaterfloodVal());
					notifyParam.setTotalNumber(this.goodstotalnum);
					notifyParam.setLeftNumber(this.goodsleftnum);
					//库存总数 减 库存剩余
					int sales = inventoryDO.getGoodsSaleCount();
					//销量
					notifyParam.setSales(String.valueOf(sales));
					//库存基表信息发送
					GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
					notifyParam.setGoodsBaseId(goodsBaseId);
					if(baseInventoryDO!=null) {
						notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
						notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
					}
					

				}
				this.fillSelectionMsg();
				if(!CollectionUtils.isEmpty(selectionMsg)){
					notifyParam.setSelectionRelation(selectionMsg);
				}
				this.fillSuppliersMsg();
				if(!CollectionUtils.isEmpty(suppliersMsg)){
					notifyParam.setSuppliersRelation(suppliersMsg);
				}
			} catch (Exception e) {
				this.writeBusInitErrorLog(
						lm.setMethod("fillInventoryNotifyMessageParam").addMetaData("errorMsg",e.getMessage()),false, e);
			}
			return notifyParam;
		}
		//初始化库存
		@SuppressWarnings("unchecked")
		public CreateInventoryResultEnum initCheck() {
			this.fillParam();
			this.goodsId =  StringUtils.isEmpty(goodsId2str)?0:Long.valueOf(goodsId2str);
			this.goodsBaseId =  StringUtils.isEmpty(goodsBaseId2str)?0:Long.valueOf(goodsBaseId2str);
			//初始化加分布式锁
			lm.addMetaData("initCheck","initCheck,start").addMetaData("initCheck[" + (goodsId) + "]", goodsId);
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
							lm.setMethod("dLock").addMetaData("errorMsg",
									goodsId), false);
				}
				InventoryInitDomain create = new InventoryInitDomain(goodsId,
						lm);
				//注入相关Repository
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
				//create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
				//create.setSynInitAndAsynUpdateDomainRepository(this.synInitAndAsynUpdateDomainRepository);
				resultEnum = create.businessExecute();
			}finally{
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
			updateActionDO.setGoodsBaseId(goodsBaseId);
			updateActionDO.setGoodsId(goodsId);
			updateActionDO.setBusinessType(businessType);
			updateActionDO.setItem(id);
			updateActionDO.setOriginalInventory(StringUtil.handlerOriInventory(type, origoodsleftnum, origoodstotalnum, oriselOrSuppleftnum, oriselOrSupptotalnum));
			updateActionDO.setInventoryChange(String.valueOf(adjustNum));
			updateActionDO.setActionType(ResultStatusEnum.CALLBACK_CONFIRM
					.getDescription());
			if(!StringUtils.isEmpty(param.getUserId())) {
				updateActionDO.setUserId(Long.valueOf(param.getUserId()));
			}
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			//updateActionDO.setOrderId(0l);
			updateActionDO
					.setContent(JSONObject.fromObject(param).toString()); // 操作内容
			updateActionDO.setRemark("库存调整");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
			
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillInventoryUpdateActionDO error"+e.getMessage()),false, e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}
	private boolean verifyInventory() {
		boolean ret = true;
		//if(resultACK) {
		if(!CollectionUtils.isEmpty(resultACK)) {
			for(long result:resultACK) {
				if(result<0) {  //如果结果中存在小于0的则返回false
					ret = false;
					break;
				}
			}
			
		}else {
			ret= false;
		}
		return ret;
	}
	
	public void fillSelectionMsg() {
		List<SelectionNotifyMessageParam> selectionMsg = new ArrayList<SelectionNotifyMessageParam>();
		SelectionNotifyMessageParam selMsg = new SelectionNotifyMessageParam();
		try {
			//selMsg.setGoodTypeId(selectionInventory.getGoodTypeId());
			selMsg.setGoodsId(goodsId);
			selMsg.setId(selectionId);
			selMsg.setLeftNumber(this.selOrSuppleftnum);  //调整后的库存值
			selMsg.setTotalNumber(this.selOrSupptotalnum);
			selMsg.setUserId(Long.valueOf(param.getUserId()));
			selMsg.setLimitStorage(selectionInventory.getLimitStorage());
			selMsg.setWaterfloodVal(selectionInventory.getWaterfloodVal());
			selMsg.setWmsGoodsId(selectionInventory.getWmsGoodsId());
			selectionMsg.add(selMsg);
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm.setMethod("fillSelectionMsg")
					.addMetaData("errMsg", "fillSelectionMsg error"+e.getMessage()),false, e);
			this.selectionMsg = null;
		}
		this.selectionMsg = selectionMsg;
	}
	public void fillSuppliersMsg() {
		List<SuppliersNotifyMessageParam> suppliersMsg = new ArrayList<SuppliersNotifyMessageParam>();
		SuppliersNotifyMessageParam supMsg = new SuppliersNotifyMessageParam();
		try {
			//supMsg.setSuppliersId(suppliersInventory.getSuppliersId());
			supMsg.setGoodsId(goodsId);
			supMsg.setId(suppliersId);
			supMsg.setLeftNumber(this.selOrSuppleftnum);  //调整后的库存值
			supMsg.setTotalNumber(this.selOrSupptotalnum);
			supMsg.setUserId(Long.valueOf(param.getUserId()));
			supMsg.setLimitStorage(suppliersInventory.getLimitStorage());
			supMsg.setWaterfloodVal(suppliersInventory.getWaterfloodVal());
			suppliersMsg.add(supMsg);
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm
					.addMetaData("errMsg", "fillSuppliersMsg error"+e.getMessage()),false, e);
			this.suppliersMsg = null;
		}
		this.suppliersMsg = suppliersMsg;
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
		//简单的校验检查
		if (param.getType().equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())&&StringUtils.isEmpty(param.getId())) {
				return CreateInventoryResultEnum.INVALID_SELECTIONID;
			}
		if (param.getType().equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())&&StringUtils.isEmpty(param.getId())) {
				return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
			}
		//构建校验领域
		GoodsVerificationDomain vfDomain = new GoodsVerificationDomain(Long.valueOf(param.getGoodsId()),param.getType(),param.getId());
		//注入仓储对象
		vfDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		return vfDomain.checkSelOrSupp();
		//return CreateInventoryResultEnum.SUCCESS;
	}

	public void setdLock(DLockImpl dLock) {
		this.dLock = dLock;
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

	/*public void setInventoryInitAndUpdateHandle(
			InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle) {
		this.inventoryInitAndUpdateHandle = inventoryInitAndUpdateHandle;
	}*/
	public Long getGoodsId() {
		return goodsId;
	}

}
