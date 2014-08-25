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
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
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
import com.tuan.inventory.model.util.QueueConstant;

public class InventoryAdjustDomain extends AbstractDomain {
	//private static Log logger = LogFactory.getLog("INVENTORY.INIT");
	protected static Log logger = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private AdjustInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
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
		long startTime = System.currentTimeMillis();
		/*String method = "InventoryAdjustDomain";
		final LogModel lm = LogModel.newLogModel(method);*/
		logger.info(lm.addMetaData("init start", startTime)
				.toJson(false));
		CreateInventoryResultEnum resultEnum = null;
		try {
			//初始化检查
			resultEnum = this.initCheck();
			
			long endTime = System.currentTimeMillis();
			String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
					+ "milliseconds(毫秒)执行完成!";
			logger.info(lm.addMetaData("endTime", endTime).addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId)
					.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
			
			if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return resultEnum;
			}
			//真正的库存调整业务处理
			if(goodsId!=null&&goodsId>0) {
				//查询商品库存
				this.inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				limitStorage = inventoryDO.getLimitStorage();
				if(limitStorage==0) {  //非限制库存
					return CreateInventoryResultEnum.SUCCESS;
				}
				if(inventoryDO!=null) {
					this.origoodsleftnum = inventoryDO.getLeftNumber();
					this.origoodstotalnum = inventoryDO.getTotalNumber();
					
				}
			}
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF.getDescription();
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION.getDescription();
				
				//查询商品选型库存
				this.selectionInventory = this.goodsInventoryDomainRepository.querySelectionRelationById(selectionId);
				limitStorage = selectionInventory.getLimitStorage();
				if(limitStorage==0) {  //非限制库存
					return CreateInventoryResultEnum.SUCCESS;
				}
				if(selectionInventory!=null) {
					this.oriselOrSuppleftnum = selectionInventory.getLeftNumber();
					this.oriselOrSupptotalnum = selectionInventory.getTotalNumber();
					
					
				}
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.suppliersId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SUPPLIERS.getDescription();
				//查询商品分店库存
				this.suppliersInventory = this.goodsInventoryDomainRepository.querySuppliersInventoryById(suppliersId);
				limitStorage = suppliersInventory.getLimitStorage();
				if(limitStorage==0) {  //非限制库存
					return CreateInventoryResultEnum.SUCCESS;
				}
				if(suppliersInventory!=null) {
					this.oriselOrSuppleftnum = suppliersInventory.getLeftNumber();
					this.oriselOrSupptotalnum = suppliersInventory.getTotalNumber();
					
				}
			}
			
		} catch (Exception e) {
			/*this.writeBusUpdateErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()),false, e);*/
			logger.error(lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存 正数：+ 负数：-
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
			lm.addMetaData("adjustInventory","adjustInventory,start").addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId).addMetaData("type", type);
			//writeSysUpdateLog(lm,false);
			logger.info(lm.toJson(false));
			
				if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
					
					if (inventoryDO != null) {
						if(limitStorage==0) {  //非限制库存
							return CreateInventoryResultEnum.NONE_LIMIT_STORAGE;
						}
						if(origoodstotalnum+(adjustNum)<0) {
							// 调整剩余库存数量
							inventoryDO.setLeftNumber(0);
							inventoryDO.setTotalNumber(0);
							inventoryDO.setLimitStorage(0);  //变为无限量
							limitStorage = 0;
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
						lm.addMetaData("adjustInventory","adjustInventory mysql,start").addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("limitStorage", limitStorage);
						//writeSysUpdateLog(lm,true);
						logger.info(lm.toJson(false));
						 // 消费对列的信息
						
						CallResult<GoodsInventoryDO> callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,this.goodsBaseId,(adjustNum),limitStorage,inventoryDO);
						PublicCodeEnum publicCodeEnum = callResult.getPublicCodeEnum();
                        if(publicCodeEnum == PublicCodeEnum.SUCCESS){
                        	
                        }
						if (publicCodeEnum != PublicCodeEnum.SUCCESS) { //
							// 消息数据不存并且不成功
							message = "adjustInventory_error["
									+ publicCodeEnum.getMessage() + "]goodsId:"
									+ goodsId;
							return CreateInventoryResultEnum
									.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "adjustInventory_success[save success]goodsId:"
									+ goodsId;
							this.goodsleftnum = inventoryDO.getLeftNumber();
							this.goodstotalnum = inventoryDO.getTotalNumber();
						}
						lm.addMetaData("adjustInventory","adjustInventory mysql,end").addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("limitStorage", limitStorage).addMetaData("message", message);
						logger.info(lm.toJson(false));
						//writeSysUpdateLog(lm,true);
					}
				} else if (type
						.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
								.getCode())) {
					if (selectionInventory != null) {
						//调整前校验
						if(limitStorage==0) {  //非限制库存
							return CreateInventoryResultEnum.NONE_LIMIT_STORAGE;
						}
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
					
					lm.addMetaData("adjustInventory","adjustInventory mysql,start").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("selectionInventory", selectionInventory);
					logger.info(lm.toJson(false));
					//writeSysUpdateLog(lm,true);
					
					CallResult<GoodsSelectionDO> callResult  = null;
					// 消费对列的信息
					callResult = synInitAndAysnMysqlService.updateGoodsSelection(inventoryDO,origoodstotalnum,adjustNum,selectionInventory);
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
						this.goodsleftnum = inventoryDO.getLeftNumber();
						this.goodstotalnum = inventoryDO.getTotalNumber();
						this.selOrSuppleftnum = selectionInventory
								.getLeftNumber();
						this.selOrSupptotalnum = selectionInventory
								.getTotalNumber();
					
					}
					
					lm.addMetaData("adjustInventory","adjustInventory mysql,end").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("selectionInventory", selectionInventory).addMetaData("message", message);
					logger.info(lm.toJson(false));
					//writeSysUpdateLog(lm,true);

				} else if (type
						.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
								.getCode())) {
					if (suppliersInventory != null) {
						if(limitStorage==0) {  //非限制库存
							return CreateInventoryResultEnum.NONE_LIMIT_STORAGE;
						}
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
					
					lm.addMetaData("adjustInventory","adjustInventory suppliers mysql,start").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("suppliersInventory", suppliersInventory);
					logger.info(lm.toJson(false));
					
					//writeSysUpdateLog(lm,true);
				
					CallResult<GoodsSuppliersDO> callResult = synInitAndAysnMysqlService.updateGoodsSuppliers(inventoryDO,origoodstotalnum,adjustNum,suppliersInventory);
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
						this.goodsleftnum = inventoryDO.getLeftNumber();
						this.goodstotalnum = inventoryDO.getTotalNumber();
						this.selOrSuppleftnum = suppliersInventory
								.getLeftNumber();
						this.selOrSupptotalnum = suppliersInventory
								.getTotalNumber();
					}
					lm.addMetaData("adjustInventory","adjustInventory mysql,end").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO).addMetaData("suppliersInventory", suppliersInventory).addMetaData("message", message);
					logger.info(lm.toJson(false));
					//writeSysUpdateLog(lm,true);
				
				}//else SUPPLIERS
			/*} finally{
				dLock.unlockManual(key);
			}*/
			lm.addMetaData("result", "end");
			logger.info(lm.toJson(false));
			//writeSysUpdateLog(lm,false);
		} catch (Exception e) {
			/*this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"adjustInventory error" + e.getMessage()),false, e);*/
			logger.error(lm.addMetaData("errorMsg",
							"adjustInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	//发送库存新增消息
		public void sendNotify(){
			try {
				InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
				goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryAdjustDomain.toString(),JSONObject.fromObject(notifyParam));
				/*Type orderParamType = new TypeToken<NotifyCardOrder4ShopCenterParam>(){}.getType();
				String paramJson = new Gson().toJson(notifyParam, orderParamType);
				extensionService.sendNotifyServer(paramJson, lm.getTraceId());*/
			} catch (Exception e) {
				/*this.writeBusUpdateErrorLog(
						lm.setMethod("sendNotify").addMetaData("errorMsg",e.getMessage()),false, e);*/
				logger.error(lm.addMetaData("errorMsg",
						"sendNotify error" + e.getMessage()).toJson(false), e);
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
				notifyParam.setUserId(Long.valueOf(param!=null&&StringUtils.isNotEmpty(param.getUserId())?param.getUserId():"0"));
				notifyParam.setGoodsId(goodsId);
				if(inventoryDO!=null) {
					notifyParam.setLimitStorage(limitStorage);
					notifyParam.setWaterfloodVal(inventoryDO.getWaterfloodVal());
					notifyParam.setTotalNumber(this.goodstotalnum);
					notifyParam.setLeftNumber(this.goodsleftnum);
					//库存总数 减 库存剩余
					Integer sales = inventoryDO.getGoodsSaleCount();
					//销量
					notifyParam.setSales(sales==null?0:sales);
					//库存基表信息发送
					GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
					notifyParam.setGoodsBaseId(goodsBaseId);
					if(baseInventoryDO!=null) {
						notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
						notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
					}
					

				}
				if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
						.getCode()) && selectionInventory != null) {
					this.fillSelectionMsg();
					if(!CollectionUtils.isEmpty(selectionMsg)){
						notifyParam.setSelectionRelation(selectionMsg);
					}
				}
				if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
						.getCode()) && suppliersInventory != null) {
					this.fillSuppliersMsg();
					if(!CollectionUtils.isEmpty(suppliersMsg)){
						notifyParam.setSuppliersRelation(suppliersMsg);
					}
				}
				
			} catch (Exception e) {
				/*this.writeBusInitErrorLog(
						lm.setMethod("fillInventoryNotifyMessageParam").addMetaData("errorMsg",e.getMessage()),false, e);*/
				logger.error(lm.addMetaData("errorMsg",
						"fillInventoryNotifyMessageParam error" + e.getMessage()).toJson(false), e);
			}
			return notifyParam;
		}
		//初始化库存
		//@SuppressWarnings("unchecked")
		public CreateInventoryResultEnum initCheck() {
			this.fillParam();
			this.goodsId =  StringUtils.isEmpty(goodsId2str)?0:Long.valueOf(goodsId2str);
			//this.goodsBaseId =  StringUtils.isEmpty(goodsBaseId2str)?0:Long.valueOf(goodsBaseId2str);
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
			lm.addMetaData("initCheck","initCheck,start").addMetaData("initCheck[" + (goodsId) + "]", goodsId);

			logger.info(lm.toJson(false));

			CreateInventoryResultEnum resultEnum = null;


				InventoryInitDomain create = new InventoryInitDomain(goodsId,
						lm);
				//注入相关Repository
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
				resultEnum = create.businessExecute();

			lm.addMetaData("result", resultEnum);
			lm.addMetaData("result", "end");
			logger.info(lm.toJson(false));
			
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
			if(param.getTask()==QueueConstant.TASK_RESTORE_INVENTORY) {
				updateActionDO.setActionType(ResultStatusEnum.TASK_RESTORE_INVENTORY
						.getDescription());
			}else{
				updateActionDO.setActionType(ResultStatusEnum.ADJUST_INVENTORY
						.getDescription());
			}
			if(!StringUtils.isEmpty(param.getUserId())) {
				updateActionDO.setUserId(Long.valueOf(param.getUserId()));
			}
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			//updateActionDO.setOrderId(0l);
			updateActionDO
					.setContent(JSON.toJSONString(param)); // 操作内容
			if(param.getTask()==QueueConstant.TASK_RESTORE_INVENTORY) {
				updateActionDO.setRemark("商品改价还原库存");
			}else {
				updateActionDO.setRemark("库存调整");
			}
			
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
			
		} catch (Exception e) {
			//this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillInventoryUpdateActionDO error"+e.getMessage()),false, e);
			logger.error(lm.addMetaData("errorMsg",
					"fillInventoryUpdateActionDO error" + e.getMessage()).toJson(false), e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
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
			selMsg.setUserId(Long.valueOf(param!=null&&StringUtils.isNotEmpty(param.getUserId())?param.getUserId():"0"));
			selMsg.setLimitStorage(selectionInventory.getLimitStorage());
			selMsg.setWaterfloodVal(selectionInventory.getWaterfloodVal());
			selMsg.setWmsGoodsId(selectionInventory.getWmsGoodsId());
			selectionMsg.add(selMsg);
		} catch (Exception e) {
			/*this.writeBusUpdateErrorLog(lm.setMethod("fillSelectionMsg")
					.addMetaData("errMsg", "fillSelectionMsg error"+e.getMessage()),false, e);*/
			logger.error(lm.addMetaData("errorMsg",
					"fillSelectionMsg error" + e.getMessage()).toJson(false), e);
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
			supMsg.setUserId(Long.valueOf(param!=null&&StringUtils.isNotEmpty(param.getUserId())?param.getUserId():"0"));
			supMsg.setLimitStorage(suppliersInventory.getLimitStorage());
			supMsg.setWaterfloodVal(suppliersInventory.getWaterfloodVal());
			suppliersMsg.add(supMsg);
		} catch (Exception e) {
			/*this.writeBusUpdateErrorLog(lm
					.addMetaData("errMsg", "fillSuppliersMsg error"+e.getMessage()),false, e);*/
			logger.error(lm.addMetaData("errorMsg",
					"fillSuppliersMsg error" + e.getMessage()).toJson(false), e);
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

	public Long getGoodsId() {
		return goodsId;
	}

}
