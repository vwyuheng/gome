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
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.OverrideAdjustInventoryParam;
import com.tuan.inventory.model.param.SelectionNotifyMessageParam;
import com.tuan.inventory.model.param.SuppliersNotifyMessageParam;
import com.tuan.inventory.model.result.CallResult;

public class InventoryOverrideAdjustDomain extends AbstractDomain {
	
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private OverrideAdjustInventoryParam param;
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
	private String goodsId2str;
	private String type;
	private String id;
	private String tokenid;  //redis序列,解决接口幂等问题
	private String businessType;
	// 调整前剩余库存
	private int preleftnum = 0;
	// 调整前总库存
	private int pretotalnum = 0;
	// 调整前选型或分店剩余库存
	private int preselOrSuppleftnum = 0;
	// 调整前选型或分店总库存
	private int preselOrSupptotalnum = 0;
	
	// 调整后剩余库存
	private int aftleftnum = 0;
	// 调整后总库存:传过来的
	private int afttotalnum = 0;
	// 选型或分店调整后剩余库存
	private int aftSelOrSuppleftnum = 0;
	// 调整后总库存
	private int aftSelOrSupptotalnum = 0;
	private Long goodsId;
	private Long goodsBaseId;
	private long selectionId;
	private long suppliersId;
	private String goodsSelectionIds = "";
	boolean idemptent = false;
	
	public InventoryOverrideAdjustDomain(String clientIp, String clientName,
			OverrideAdjustInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		CreateInventoryResultEnum resultEnum = null;
		try {
			if (!StringUtils.isEmpty(tokenid)) { // if
				this.idemptent = idemptent();
				if(idemptent) {
					return CreateInventoryResultEnum.SUCCESS;
				}
			}
			//初始化检查
			resultEnum = this.initCheck();
			if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return resultEnum;
			}
			//真正的库存调整业务处理
			if(goodsId!=null&&goodsId>0) {
				//查询商品库存
				this.inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if(inventoryDO!=null) {
					this.preleftnum = inventoryDO.getLeftNumber();
					this.pretotalnum = inventoryDO.getTotalNumber();
				}
			}
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF.getDescription();
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION.getDescription();
				//查询商品选型库存
				this.selectionInventory = this.goodsInventoryDomainRepository.querySelectionRelationById(selectionId);
				if(selectionInventory!=null) {
					this.preselOrSuppleftnum = selectionInventory.getLeftNumber();
					this.preselOrSupptotalnum = selectionInventory.getTotalNumber();
				}
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.suppliersId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SUPPLIERS.getDescription();
				//查询商品分店库存
				this.suppliersInventory = this.goodsInventoryDomainRepository.querySuppliersInventoryById(suppliersId);
				if(suppliersInventory!=null) {
					this.preselOrSuppleftnum = suppliersInventory.getLeftNumber();
					this.preselOrSupptotalnum = suppliersInventory.getTotalNumber();
				}
			}
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()),false, e);
			
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}
	//接口幂等处理
	public boolean idemptent() {
		//根据key取已缓存的tokenid  
		String gettokenid = goodsInventoryDomainRepository.queryToken(DLockConstants.OVERRIDE_ADJUST_INVENTORY + "_"+ String.valueOf(goodsId));
		if(StringUtils.isEmpty(gettokenid)) {  //如果为空则任务是初始的http请求过来，将tokenid缓存起来
			if(StringUtils.isNotEmpty(tokenid)) {
				goodsInventoryDomainRepository.setTag(DLockConstants.OVERRIDE_ADJUST_INVENTORY + "_"+ goodsId, DLockConstants.IDEMPOTENT_DURATION_TIME, tokenid);
			}
					
		}else {  //否则比对token值
			if(StringUtils.isNotEmpty(tokenid)) {
				if(tokenid.equalsIgnoreCase(gettokenid)) { //重复请求过来，判断是否处理成功
				//根据处理成功后设置的tag来判断之前http请求处理是否成功
				String gettag = goodsInventoryDomainRepository.queryToken(DLockConstants.OVERRIDE_ADJUST_INVENTORY_SUCCESS + "_"+ tokenid);
				if(!StringUtils.isEmpty(gettag)&&gettag.equalsIgnoreCase(DLockConstants.HANDLER_SUCCESS)) { 
								return true;
							}
						}
					}
		}
		return false;
	}
	
	
	// 库存系统新增库存 正数：+ 负数：-
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum adjustInventory() {
		String message = StringUtils.EMPTY;
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
					if (inventoryDO != null) {
						goodsSelectionIds = inventoryDO.getGoodsSelectionIds();
						
						if(afttotalnum>preleftnum) {  //计算剩余库存
							//调整的剩余库存量
							int adjustnum =  afttotalnum - preleftnum;
							//计算剩余库存调整后的数量量
							aftleftnum = preleftnum+adjustnum;
						}else {
							aftleftnum = afttotalnum;
						}
						
						//此时调整后数量检查
						if(aftleftnum<0) {
							aftleftnum = 0;
						}
						if(afttotalnum<0) {
							afttotalnum = 0;
						}
						// 调整后剩余库存数量
						inventoryDO.setLeftNumber(aftleftnum);
						// 调整商品总库存数量
						inventoryDO.setTotalNumber(afttotalnum);
						//清除选型关系
						inventoryDO.setWmsId(0l);
						inventoryDO.setGoodsSelectionIds("");
						if (inventoryDO.getLimitStorage() == 0&&inventoryDO.getTotalNumber() != 0) {
							inventoryDO.setLimitStorage(1); // 更新数据库用
							//return CreateInventoryResultEnum.NONE_LIMIT_STORAGE;
						} else if (inventoryDO.getTotalNumber() == 0
								&& inventoryDO.getLimitStorage() == 1) {// 当将限制库存的(limitstorage为1的)总库存调整为0时,更新库存限制标志为非限制库存(0)
							inventoryDO.setLimitStorage(0); // 更新数据库用
							inventoryDO.setLeftNumber(Integer.MAX_VALUE);
							inventoryDO.setTotalNumber(Integer.MAX_VALUE);
						}
					}
					
					//更新mysql
					//boolean handlerResult = inventoryInitAndUpdateHandle
					//		.updateGoodsInventory(goodsId,inventoryDO);
					CallResult<GoodsInventoryDO> callResult  = null;
						
					if (goodsId>0&&inventoryDO != null) {
						lm.addMetaData("adjustInventory","adjustInventory start").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO.toString());
						writeSysUpdateLog(lm,true);
						// 消费对列的信息
						if(!StringUtils.isEmpty(param.getGoodsBaseId())&&StringUtils.isNumeric(param.getGoodsBaseId())){
							goodsBaseId = Long.valueOf(param.getGoodsBaseId());
						}
						callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,goodsBaseId,goodsSelectionIds,inventoryDO);
						PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();
						
						if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
							// 消息数据不存并且不成功
							message = "updateGoodsInventory_error[" + publicCodeEnum.getMessage()
									+ "]goodsId:" + goodsId;
							return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "updateGoodsInventory_success[save success]goodsId:" + goodsId;
						}
					lm.addMetaData("adjustInventory","adjustInventory end").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO.toString()).addMetaData("message", message);
					writeSysUpdateLog(lm,true);
					}
						
				} else if (type
						.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
								.getCode())) {
					if (selectionInventory.getLimitStorage() == 0&&selectionInventory.getTotalNumber() != 0) {
						inventoryDO.setLimitStorage(1); // 更新数据库用
						selectionInventory.setLimitStorage(1);
						//return CreateInventoryResultEnum.NONE_LIMIT_STORAGE;
					} 
					
					if (selectionInventory != null) {
						//调整的总量
						//int adjustnum = aftSelOrSupptotalnum - preselOrSupptotalnum;
						//计算剩余库存调整后的数量量
						//aftSelOrSuppleftnum = adjustnum + preselOrSuppleftnum;
						//此时调整后数量检查
						if(aftSelOrSuppleftnum<0) {
							aftSelOrSuppleftnum = 0;
						}
						//计算剩余库存调整后的数量量
						//aftleftnum = adjustnum+preleftnum;
					
						//调整剩余库存数量
						selectionInventory.setLeftNumber(afttotalnum);
						//调整商品选型总库存数量
						selectionInventory.setTotalNumber(afttotalnum);
						//计算总的
						//afttotalnum =  pretotalnum + adjustnum;
						//计算剩余库存调整后的数量量
						//aftleftnum = adjustnum+preleftnum;
						
						if(afttotalnum<=0) {
							afttotalnum = 0;
							inventoryDO.setLimitStorage(0);
							aftleftnum=Integer.MAX_VALUE;
							afttotalnum=Integer.MAX_VALUE;
						}
						//同时调整商品总的库存的剩余和总库存量
						//调整剩余库存数量
						inventoryDO.setLeftNumber(aftleftnum);
						//调整商品总库存数量
						inventoryDO.setTotalNumber(afttotalnum);
					}
					//更新mysql
					/*boolean selhandlerResult = inventoryInitAndUpdateHandle
							.updateGoodsSelection(inventoryDO,
									selectionInventory);*/
					CallResult<GoodsSelectionDO> callResult = null;

					if (inventoryDO != null && selectionInventory != null) {
						lm.addMetaData("adjustInventory",
								"adjustInventory start")
								.addMetaData("goodsId", goodsId)
								.addMetaData("type", type)
								.addMetaData("inventoryDO", inventoryDO)
								.addMetaData("selectionInventory",
										selectionInventory);
						writeSysUpdateLog(lm, true);
						// 消费对列的信息
						callResult = synInitAndAysnMysqlService
								.updateGoodsSelection(inventoryDO,
										selectionInventory);
						PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();

						if (publicCodeEnum != PublicCodeEnum.SUCCESS) { //
							// 消息数据不存并且不成功
							message = "updateGoodsSelection_error["
									+ publicCodeEnum.getMessage()
									+ "]selectionId:"
									+ selectionInventory.getId();
							return CreateInventoryResultEnum
									.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "updateGoodsSelection_success[save success]selectionId:"
									+ selectionInventory.getId();
						}
						lm.addMetaData("adjustInventory",
								"adjustInventory mysql,end")
								.addMetaData("goodsId", goodsId)
								.addMetaData("type", type)
								.addMetaData("inventoryDO", inventoryDO)
								.addMetaData("selectionInventory",
										selectionInventory)
								.addMetaData("message",
										message);
						writeSysUpdateLog(lm, true);
					}
							
				/*		
					if (selhandlerResult) {
						lm.addMetaData("adjustInventory","adjustInventory redis,start").addMetaData("goodsId", goodsId).addMetaData("selectionId", selectionId).addMetaData("type", type).addMetaData("selectionInventory", selectionInventory.toString()).addMetaData("inventoryDO", inventoryDO.toString());
						writeSysUpdateLog(lm,true);
						this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(goodsId, selectionInventory);
						this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId, inventoryDO);
						lm.addMetaData("adjustInventory","adjustInventory redis,end").addMetaData("goodsId", goodsId).addMetaData("selectionId", selectionId).addMetaData("type", type).addMetaData("selectionInventory", selectionInventory.toString()).addMetaData("inventoryDO", inventoryDO.toString());
						writeSysUpdateLog(lm,true);
						
					}*/

				} else if (type
						.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
								.getCode())) {
					if (suppliersInventory != null) {
						if (suppliersInventory.getLimitStorage() == 0&&suppliersInventory.getTotalNumber() != 0) {
							inventoryDO.setLimitStorage(1); // 更新数据库用
							suppliersInventory.setLimitStorage(1);
						} 
						//调整的总量
						int adjustnum = aftSelOrSupptotalnum - preselOrSupptotalnum;
						//计算剩余库存调整后的数量量
						aftSelOrSuppleftnum = adjustnum + preselOrSuppleftnum;
						//此时调整后数量检查
						if(aftSelOrSuppleftnum<0) {
							aftSelOrSuppleftnum = 0;
						}
						//计算剩余库存调整后的数量量
						aftleftnum = adjustnum+preleftnum;
						//此时调整后数量检查
						if(aftleftnum<0) {
							aftleftnum = 0;
						}
						//调整剩余库存数量
						suppliersInventory.setLeftNumber(aftSelOrSuppleftnum);
						//调整商品分店总库存数量
						suppliersInventory.setTotalNumber(aftSelOrSupptotalnum);
						afttotalnum =  pretotalnum + adjustnum;
						//计算剩余库存调整后的数量量
						aftleftnum = adjustnum+preleftnum;
						//此时调整后数量检查
						if(aftleftnum<0) {
							aftleftnum = 0;
						}
						if(afttotalnum<=0) {
							afttotalnum = 0;
							inventoryDO.setLimitStorage(0);
							aftleftnum=Integer.MAX_VALUE;
							afttotalnum=Integer.MAX_VALUE;
						}
						//同时调整商品总的库存的剩余和总库存量
						//调整剩余库存数量
						inventoryDO.setLeftNumber(aftleftnum);
						//调整商品总库存数量
						inventoryDO.setTotalNumber(afttotalnum);
					}
					
				
					//更新mysql
					/*boolean handlerResult = inventoryInitAndUpdateHandle
							.updateGoodsSuppliers(inventoryDO,suppliersInventory);*/
					CallResult<GoodsSuppliersDO> callResult = null;

					if (inventoryDO != null && suppliersInventory != null) {
						lm.addMetaData("adjustInventory",
								"adjustInventory suppliers mysql,start")
								.addMetaData("goodsId", goodsId)
								.addMetaData("type", type)
								.addMetaData("inventoryDO", inventoryDO)
								.addMetaData("suppliersInventory",
										suppliersInventory);
						writeSysUpdateLog(lm, true);
						// 消费对列的信息
						callResult = synInitAndAysnMysqlService
								.updateGoodsSuppliers(inventoryDO,
										suppliersInventory);
						PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();

						if (publicCodeEnum != PublicCodeEnum.SUCCESS) { //
							// 消息数据不存并且不成功
							message = "updateGoodsSuppliers_error["
									+ publicCodeEnum.getMessage()
									+ "]suppliersId:" + suppliersInventory.getSuppliersId();
							return CreateInventoryResultEnum
									.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "updateGoodsSuppliers_success[save success]suppliersId:"
									+ suppliersInventory.getSuppliersId();
						}
						lm.addMetaData("adjustInventory",
								"adjustInventory mysql,end")
								.addMetaData("goodsId", goodsId)
								.addMetaData("type", type)
								.addMetaData("inventoryDO", inventoryDO)
								.addMetaData("suppliersInventory",
										suppliersInventory)
								.addMetaData("message", message);
						writeSysUpdateLog(lm, true);
					}
							
				/*		
					if (handlerResult) {
						lm.addMetaData("adjustInventory","adjustInventory redis,start").addMetaData("goodsId", goodsId).addMetaData("suppliersId", suppliersId).addMetaData("type", type);
						writeSysUpdateLog(lm,true);
						this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(goodsId, suppliersInventory);
						this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId, inventoryDO);
						lm.addMetaData("adjustInventory","adjustInventory redis,end").addMetaData("goodsId", goodsId).addMetaData("suppliersId", suppliersId).addMetaData("type", type);
						writeSysUpdateLog(lm,true);
						
					}*/

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
		if(!StringUtils.isEmpty(tokenid)) {
			//处理成返回前设置tag
			goodsInventoryDomainRepository.setTag(DLockConstants.OVERRIDE_ADJUST_INVENTORY_SUCCESS + "_"+ tokenid, DLockConstants.IDEMPOTENT_DURATION_TIME, DLockConstants.HANDLER_SUCCESS);
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
			this.tokenid = param.getTokenid();
			//商品id，必传参数，该参数无论是选型还是分店都要传过来
			this.goodsId2str = param.getGoodsId();
			// 2:商品 4：选型 6：分店
			this.type = param.getType();
			// 2：可不传 4：选型id 6 分店id
			this.id = param.getId();
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.afttotalnum = param.getTotalnum();
			}else if (type
					.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
							.getCode())) {
				this.aftSelOrSupptotalnum = param.getTotalnum();
			}else if (type
					.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
							.getCode())) {
				this.aftSelOrSupptotalnum = param.getTotalnum();
			}
			
			
		}
		//填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(){
			InventoryNotifyMessageParam notifyParam = null;
			try {
				notifyParam = new InventoryNotifyMessageParam();
				notifyParam.setUserId(Long.valueOf(param!=null&&param.getUserId()!=null?param.getUserId():"0"));
				notifyParam.setGoodsId(goodsId);
				if(inventoryDO!=null) {
					notifyParam.setLimitStorage(inventoryDO.getLimitStorage());
					notifyParam.setWaterfloodVal(inventoryDO.getWaterfloodVal());
					notifyParam.setTotalNumber(afttotalnum);
					notifyParam.setLeftNumber(aftleftnum);
					//库存总数 减 库存剩余
					int sales = (this.afttotalnum-this.aftleftnum);
					//销量
					notifyParam.setSales(String.valueOf(sales));
					
					//发送库存基表信息
					notifyParam.setGoodsBaseId(Long.valueOf(param.getGoodsBaseId()));
					notifyParam.setBaseTotalCount(afttotalnum);
					notifyParam.setBaseSaleCount(sales);
				}
				if(!CollectionUtils.isEmpty(selectionMsg)){
					this.fillSelectionMsg();
					notifyParam.setSelectionRelation(selectionMsg);
				}
				if(!CollectionUtils.isEmpty(suppliersMsg)){
					this.fillSuppliersMsg();
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
			updateActionDO.setGoodsId(goodsId);
			updateActionDO.setBusinessType(businessType);
			updateActionDO.setItem(id);
			updateActionDO.setOriginalInventory(StringUtil.handlerOriInventory(type, preleftnum, pretotalnum, preselOrSuppleftnum, preselOrSupptotalnum));
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				updateActionDO.setInventoryChange(String.valueOf(afttotalnum));
			}else if (type
					.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
							.getCode())) {
				updateActionDO.setInventoryChange(String.valueOf(aftSelOrSupptotalnum));
			}else if (type
					.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
							.getCode())) {
				updateActionDO.setInventoryChange(String.valueOf(aftSelOrSupptotalnum));
			}
			
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
			if(!StringUtils.isEmpty(param.getGoodsBaseId())&&StringUtils.isNumeric(param.getGoodsBaseId())){
				goodsBaseId = Long.valueOf(param.getGoodsBaseId());
			}
			updateActionDO.setGoodsBaseId(goodsBaseId);
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillInventoryUpdateActionDO error"+e.getMessage()),false, e);
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
			selMsg.setLeftNumber(this.aftSelOrSuppleftnum);  //调整后的库存值
			selMsg.setTotalNumber(this.aftSelOrSupptotalnum);
			selMsg.setUserId(Long.valueOf(param.getUserId()));
			selMsg.setLimitStorage(selectionInventory.getLimitStorage());
			selMsg.setWaterfloodVal(selectionInventory.getWaterfloodVal());
			int sales = selMsg.getTotalNumber()-selMsg.getLeftNumber();
			selMsg.setSales(String.valueOf(sales));
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
			supMsg.setLeftNumber(this.aftSelOrSuppleftnum);  //调整后的库存值
			supMsg.setTotalNumber(this.aftSelOrSupptotalnum);
			supMsg.setUserId(Long.valueOf(param.getUserId()));
			supMsg.setLimitStorage(suppliersInventory.getLimitStorage());
			supMsg.setWaterfloodVal(suppliersInventory.getWaterfloodVal());
			int sales = supMsg.getTotalNumber()-supMsg.getLeftNumber();
			supMsg.setSales(String.valueOf(sales));
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
		//构建校验领域:TODO 根据cms后台的要求，目前暂时注掉该校验逻辑，增加将修改库存时，清除下物流关系处理
		//GoodsVerificationDomain vfDomain = new GoodsVerificationDomain(Long.valueOf(param.getGoodsId()),param.getType(),param.getId());
		//注入仓储对象
		//vfDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		//return vfDomain.checkSelOrSupp();
		return CreateInventoryResultEnum.SUCCESS;
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
