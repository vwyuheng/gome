package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.core.common.lock.eum.LockResultCodeEnum;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.core.common.lock.res.LockResult;
import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.UpdateInventoryParam;
import com.tuan.inventory.model.result.CallResult;

public class InventoryUpdateDomain extends AbstractDomain {
	protected static Log logSysDeduct = LogFactory.getLog("INVENTORY.DEDUCT.LOG");
	private final static LocalLogger logHis = LocalLogger.getLog("INVENTORY.HIS.LOG");
	private static Log logger = LogFactory.getLog("INVENTORY.INIT");
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private UpdateInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private DLockImpl dLock;//分布式锁
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryQueueDO queueDO;
	private GoodsInventoryDO inventoryInfoDO;

	private List<GoodsSelectionModel> selectionList;
	private List<GoodsSuppliersModel> suppliersList;
	
	private Long goodsId;
	private Long goodsBaseId;
	//private String wmsGoodsId;
	private Long userId;
	private boolean isEnough;
	private boolean isSelectionEnough = false;
	private boolean isSuppliersEnough = true;
	//是否选型商品
	private boolean isSelection;
	// 是否分店商品
	private boolean isSupplier;
	// 需扣减的商品库存
	private int goodsDeductNum = 0;
	private int selectionDeductNum = 0;
	private int suppliersDeductNum = 0;
	//商品扣减量:非限制库存商品取默认值
	private int limtStorgeDeNum = 0;
	private int limitStorage = 0;
	// 原库存
	private int originalGoodsInventory = 0;
	// 领域中缓存选型和分店原始库存和扣减库存的list
	private List<GoodsSelectionAndSuppliersResult> selectionParam;
	private List<GoodsSelectionAndSuppliersResult> suppliersParam;
	// 当前库存
	private List<Long> resultACK;
	private SequenceUtil sequenceUtil;

	public InventoryUpdateDomain(String clientIp, String clientName,
			UpdateInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	/**
	 * 处理选型库存
	 */
	private boolean selectionInventoryHandler() {
		try {
			if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // if1
				this.selectionList = param.getGoodsSelection();
				this.selectionParam = new ArrayList<GoodsSelectionAndSuppliersResult>();

				for (GoodsSelectionModel model : selectionList) { // for
					if (model.getId() != null && model.getId() > 0) { // if选型
						GoodsSelectionAndSuppliersResult selection = null;
						Long selectionId = Long.valueOf(model.getId());
						// 查询商品选型库存
						GoodsSelectionDO selectionDO = this.goodsInventoryDomainRepository
								.querySelectionRelationById(selectionId);
						if (selectionDO != null
								&& selectionDO.getLimitStorage() == 1) { // 只处理限制库存商品
							// 扣减库存并返回扣减标识,计算库存并
							if ((selectionDO.getLeftNumber() - model.getNum()) < 0) {
								// 该处为了保证只要有一个选型商品库存不足则返回库存不足
								
								return false;
							} else {
								selection = new GoodsSelectionAndSuppliersResult();
								selection.setId(model.getId());
								selection.setWmsGoodsId(model.getWmsGoodsId()); // 物流
								// 扣减的库存量
								selection.setGoodsInventory(model.getNum());
								selection.setOriginalGoodsInventory(selectionDO
										.getLeftNumber());
								// 选型库存，并且是库存充足时用
								this.selectionParam.add(selection);

							}

						}else if(selectionDO != null
								&& selectionDO.getLimitStorage() == 0){
							return true;
						}

					}// if选型

				}// for

			}
		} catch (Exception e) {
			
			this.writeBusUpdateErrorLog(lm.addMetaData("errorMsg",
					"selectionInventoryHandler error" + e.getMessage()), false,
					e);
			return false;
		}
		return true;
	}

	/**
	 * 处理分店库存
	 */
	public void suppliersInventoryHandler() {
		try {
			if (!CollectionUtils.isEmpty(param.getGoodsSuppliers())) { // if1
				this.suppliersList = param.getGoodsSuppliers();
				this.suppliersParam = new ArrayList<GoodsSelectionAndSuppliersResult>();
				for (GoodsSuppliersModel model : suppliersList) { // for
					if (model.getSuppliersId() > 0) { // if分店
						GoodsSelectionAndSuppliersResult suppliers = null;
						Long suppliersId = Long.valueOf(model.getSuppliersId());
						GoodsSuppliersDO suppliersDO = this.goodsInventoryDomainRepository
								.querySuppliersInventoryById(suppliersId);

						if (suppliersDO != null
								&& suppliersDO.getLimitStorage() == 1) {
							// 扣减库存并返回扣减标识,计算库存并
							if ((suppliersDO.getLeftNumber() - model.getNum()) < 0) {
								// 该处为了保证只要有一个选型商品库存不足则返回库存不足
								this.isSuppliersEnough = false;

							} else {
								suppliers = new GoodsSelectionAndSuppliersResult();
								// 扣减的库存量
								suppliers.setId(model.getSuppliersId());
								suppliers.setGoodsInventory(model.getNum());
								suppliers.setOriginalGoodsInventory(suppliersDO
										.getLeftNumber());
								this.suppliersParam.add(suppliers);

							}

						}

					}// if
				}
			}
		} catch (Exception e) {
			isSuppliersEnough = false;
			this.writeBusUpdateErrorLog(lm.addMetaData("errorMsg",
					"suppliersInventoryHandler error" + e.getMessage()), false,
					e);

		}

	}
	
	private boolean calculateInventory() {
		// 再次查询商品库存信息[确保最新数据]
		//this.inventoryInfoDO 
		GoodsInventoryDO	tmpInventory = this.goodsInventoryDomainRepository
				.queryGoodsInventory(goodsId);
		if(tmpInventory==null) {
			return false;
		}
		// 商品本身扣减库存量
		int deductNum = param.getNum();
		int deSelectionNum = 0;
		int selNum = 0;
		int deSuppliersNum = 0;
		int supNum = 0;
		
		//其下所属选型扣减库存的量
		if (!CollectionUtils.isEmpty(selectionParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : selectionParam) { // for
				selNum = param.getGoodsInventory();
				if (selNum > 0) { // if选型
					//BigDecimal sel = new BigDecimal(Integer.toString(selNum));
					deSelectionNum=deSelectionNum+selNum;
				}
			}
		}
		//其下所属分店扣减库存的量
		if (!CollectionUtils.isEmpty(suppliersParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : suppliersParam) { // for
				supNum = param.getGoodsInventory();
				if (supNum > 0) { // if选型
					//BigDecimal sup = new BigDecimal(Integer.toString(supNum));
					deSuppliersNum=deSuppliersNum+supNum;
				}
			}
		}
		int originLeftNumTmp = 0;
		if(tmpInventory!=null) {
			originLeftNumTmp = tmpInventory.getLeftNumber();
		}
		// 原始库存
		this.originalGoodsInventory = originLeftNumTmp;
		this.selectionDeductNum = deSelectionNum;
		this.suppliersDeductNum = deSuppliersNum;
		//limitStorage = inventoryInfoDO.getLimitStorage();
		int limitStorageTmp = tmpInventory.getLimitStorage();
		
		if(isSelection&&!isSupplier) {  //只包含选型的
			this.goodsDeductNum = (selectionDeductNum);
		}else if(isSupplier&&!isSelection) {  //只包含分店的
			this.goodsDeductNum = (suppliersDeductNum);
		}else if(isSupplier&&isSelection) {  //分店选型都有的
			this.goodsDeductNum = (selectionDeductNum + suppliersDeductNum);
		}else {
			this.goodsDeductNum = (deductNum);
		}
		// 扣减库存并返回扣减标识,计算库存并
		if (limitStorageTmp==1&&(originalGoodsInventory-goodsDeductNum) >= 0) { //限制库存商品
			//this.isEnough = true;
			limitStorage = limitStorageTmp;
			// 商品库存扣减后的量
			limtStorgeDeNum = goodsDeductNum;
			tmpInventory.setLeftNumber(this.originalGoodsInventory - goodsDeductNum); 
			tmpInventory.setGoodsSaleCount(goodsDeductNum);
			//this.inventoryInfoDO.setLeftNumber(this.originalGoodsInventory - goodsDeductNum); 
			//inventoryInfoDO.setGoodsSaleCount(goodsDeductNum);
			this.inventoryInfoDO  = tmpInventory;
		}else if(limitStorageTmp==0) { //非限制库存商品
			//this.isEnough = true;
			//此时要更新leftnum扣减量，以便累加销量
			tmpInventory.setGoodsSaleCount(goodsDeductNum);
			//inventoryInfoDO.setGoodsSaleCount(goodsDeductNum);
			this.inventoryInfoDO  = tmpInventory;
		}
		return true;
	}

	
	/*private boolean verifyInventory() {
		if (!CollectionUtils.isEmpty(resultACK)) {
			return true;
		} else {
			return false;
		}
	}*/
	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		long startTime = System.currentTimeMillis();
		String method = "InventoryUpdateDomain";
		final LogModel lm = LogModel.newLogModel(method);
		logger.info(lm.setMethod(method).addMetaData("start", startTime)
				.toJson(true));
		// 初始化检查
		CreateInventoryResultEnum resultEnum =	this.initCheck();
		
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		logger.info(lm.setMethod(method).addMetaData("endTime", endTime).addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId)
				.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(true));
		
		if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
			return resultEnum;
		}else {
			//查询商品所属的选型
			List<GoodsSelectionModel> selResult = goodsInventoryDomainRepository
					.queryGoodsSelectionListByGoodsId(Long.valueOf(param.getGoodsId()));
			//检查商品所属选型商品
			if(!CollectionUtils.isEmpty(selResult)) {  //若商品存在选型，则为选型商品，
				isSelection = true;
			}
			//查询商品所属的分店
			List<GoodsSuppliersModel> suppResult = goodsInventoryDomainRepository
							.queryGoodsSuppliersListByGoodsId(Long.valueOf(param.getGoodsId()));
			//这个逻辑比较清晰，首先若存在选型或分店的商品，则该商品所传参数中，选型或分店参数不能为空，否则校验不通过
			//检查商品所属分店商品
			if(!CollectionUtils.isEmpty(suppResult)) { //若商品存在分店，则为分店商品，
						isSupplier = true;
					}
			/*if(isSelection&&!isSupplier) {  //只包含选型的
				if (CollectionUtils.isEmpty(param.getGoodsSelection())) {
					return CreateInventoryResultEnum.SELECTION_GOODS;
				}
			}*/
			/*if(isSupplier&&!isSelection) {  //只包含分店的
				if (CollectionUtils.isEmpty(param.getGoodsSuppliers())) {
					return CreateInventoryResultEnum.SUPPLIERS_GOODS;
				}
			}*/
			
			/*if(isSupplier&&isSelection) {  //分店选型都有的
				if (CollectionUtils.isEmpty(param.getGoodsSuppliers())||CollectionUtils.isEmpty(param.getGoodsSelection())) {
					return CreateInventoryResultEnum.SEL_SUPP_GOODS;
				}
			}*/
			//校验商品选型id
			if (!CollectionUtils.isEmpty(param.getGoodsSelection())) {
				
				List<Long> selectionIdlist = null;
				//List<Long> wmsIdlist = null;
				if(!CollectionUtils.isEmpty(selResult)) {
					selectionIdlist = new ArrayList<Long>();
					//wmsIdlist = new ArrayList<Long>();
					 for(GoodsSelectionModel model : selResult) {
						 selectionIdlist.add(model.getId());
						 //wmsIdlist.add(model.getWmsId());
					 }
				}
				if(!CollectionUtils.isEmpty(selectionIdlist)) {
					for(GoodsSelectionModel gsmdoel : param.getGoodsSelection()) {
						if(!selectionIdlist.contains(gsmdoel.getId())||gsmdoel.getId()<=0) {
							//TODO 将这个不存在的刷进来，处理历史数据问题
							//从redis缓存中加载选型信息
							GoodsSelectionDO tmpSelDO = goodsInventoryDomainRepository.querySelectionRelationById(gsmdoel.getId());
							if(tmpSelDO!=null) {  //存在
								//从商品库存查最新数据
								CallResult<GoodsSelectionDO> cselResult = synInitAndAysnMysqlService.selectGoodsSelectionBySelId(gsmdoel.getId());
								if (cselResult != null&&cselResult.isSuccess()) {
									 tmpSelDO = cselResult.getBusinessResult();
								}
								if(tmpSelDO!=null) {
									tmpSelDO.setGoodsId(goodsId);
									//tmpSelDO.setWmsGoodsId(wmsGoodsId);
									String message = StringUtils.EMPTY;
									CallResult<Boolean> callResult  = null;
									long startTimeHis = System.currentTimeMillis();
									//保存更新数据
									try {
										callResult =	synInitAndAysnMysqlService.updateGoodsWmsSel(goodsId, tmpSelDO);
										PublicCodeEnum publicCodeEnum = callResult
												.getPublicCodeEnum();
										if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //更新数据
											// 消息数据不存并且不成功
											message = "InventoryUpdateDomain_error[" + publicCodeEnum.getMessage()
													+ "]goodsId:" + goodsId+",tmpSelDO:"+tmpSelDO;
											return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
										} else {   
											message = "InventoryUpdateDomain_success,goodsId="+goodsId+",tmpSelDO="+tmpSelDO;
											
											
										}
									} catch (Exception e) {
										logHis.error("errorMsg:"+message+
												",InventoryUpdateDomain error " + e.getMessage(), e);
										
									}finally {
										logHis.info(lm.addMetaData("goodsId",goodsId)
												.addMetaData("tmpSelDO",tmpSelDO)
												.addMetaData("endTime", System.currentTimeMillis())
												.addMetaData("message",message)
												.addMetaData("useTime", JsonUtils.getRunTime(startTimeHis)).toJson());
									}
								}
									
								
							}else {
								return CreateInventoryResultEnum.INVALID_SELECTIONID;
							}
							
						}
						if(gsmdoel.getNum()<0) {
							return CreateInventoryResultEnum.INVALID_SELECTIONNUM;
						}
					}
				}
				
			}
			
			//校验商品分店id，若存在的话
			/*if (!CollectionUtils.isEmpty(param.getGoodsSuppliers())) {
				List<Long> suppliersIdlist = null;
				
				if(!CollectionUtils.isEmpty(suppResult)) {
					 suppliersIdlist = new ArrayList<Long>();
					 for(GoodsSuppliersModel model : suppResult) {
						 suppliersIdlist.add(model.getSuppliersId());
					 }
				}
				if(!CollectionUtils.isEmpty(suppliersIdlist)) {
					for(GoodsSuppliersModel smdoel : param.getGoodsSuppliers()) {
						if(!suppliersIdlist.contains(smdoel.getSuppliersId())||smdoel.getSuppliersId()<=0) {
							return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
						}
						if(smdoel.getNum()<0) {
							return CreateInventoryResultEnum.INVALID_SUPPLIERSNUM;
						}
					}
				}
				
			}*/
		}
		// 真正的库存更新业务处理
		try {
			// 商品选型处理
			if(!this.selectionInventoryHandler()) {
				return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
			}else {
				this.isSelectionEnough = true;
			}
			// 商品分店处理
			//this.suppliersInventoryHandler();
			//if(isSelectionEnough&&isSuppliersEnough) {
				//商品库存扣减的计算
				
			//}
			if(!this.calculateInventory()) {
				return CreateInventoryResultEnum.NO_GOODS;
			}else {
				this.isEnough = true;
			}
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum updateInventory() {
		//初始化加分布式锁
		lm.addMetaData("updateInventory","updateInventory,start").addMetaData("updateInventory[" + (goodsId) + "]", goodsId);
		writeSysDeductLog(lm,false);
		LockResult<String> lockResult = null;
		String key = DLockConstants.DEDUCT_LOCK_KEY+"_goodsId_" + goodsId;
		try {
			lockResult = dLock.lockManualByTimes(key, 5000L, 5);
			if (lockResult == null
					|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
							.getCode()) {
				writeSysDeductLog(
						lm.setMethod("dLock").addMetaData("deduct lock errorMsg",
								goodsId), true);
			}
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			
			// 更新商品库存
			if (isEnough) {
				//扣减开始记录日志
				lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("goodsDeductNum", goodsDeductNum).addMetaData("start", "start deduct inventory!");
				writeSysDeductLog(lm,true);
				// 扣减库存
				resultACK = this.goodsInventoryDomainRepository
						.updateGoodsInventory(goodsId,goodsBaseId, (limtStorgeDeNum!=0?-limtStorgeDeNum:limtStorgeDeNum),(-goodsDeductNum));
				lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("goodsDeductNum", goodsDeductNum).addMetaData("resultACK", resultACK).addMetaData("deduct,end", "end deduct inventory!");
				writeSysDeductLog(lm,true);
				// 校验库存
				if (!DataUtil.verifyInventory(resultACK)) {
					lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("rollback,start", "start rollback inventory!");
					writeSysDeductLog(lm,true);
					// 回滚库存
					List<Long> rollbackAck =	this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId,goodsBaseId, (limtStorgeDeNum),(goodsDeductNum));
					lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("robackNum", goodsDeductNum).addMetaData("rollbackAck", rollbackAck).addMetaData("rollback,end", "end rollback inventory!");
					writeSysDeductLog(lm,true);
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			}else {
				String message = "InventoryUpdateDomain.updateInventory>isEnough:"+isEnough+",goodsId:"+goodsId+",originalGoodsInventory:"+originalGoodsInventory+",goodsDeductNum:"+goodsDeductNum+",message:"+CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY.getDescription();
				logSysDeduct.info(message);
				return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
			}
			// 更新选型库存
			if (isSelectionEnough) {
				if(!CollectionUtils.isEmpty(selectionParam)) {
					//扣减开始记录日志
					lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("selectionParam", selectionParam).addMetaData("start", "start deduct selection inventory!");
					writeSysDeductLog(lm,true);
					boolean rACK = this.goodsInventoryDomainRepository
							.updateSelectionInventory(selectionParam);
					
					lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("selectionParam", selectionParam).addMetaData("resultAck", rACK).addMetaData("end", "end deduct selection inventory!");
					writeSysDeductLog(lm,true);
					// 校验库存
					if (!rACK) {
						// 回滚库存
						lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("selectionParam", selectionParam).addMetaData("rollback,start", "start rollback selection inventory!");
						writeSysDeductLog(lm,true);
						// 先回滚总的 再回滚选型的
						List<Long> rollbackAck =	this.goodsInventoryDomainRepository.updateGoodsInventory(
								goodsId,goodsBaseId, (limtStorgeDeNum),(goodsDeductNum));
						boolean rbackACK = this.goodsInventoryDomainRepository
								.rollbackSelectionInventory(selectionParam);
						lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("selectionParam", selectionParam).addMetaData("resultAck", rollbackAck+",selectionRollback:"+rbackACK).addMetaData("rollback,end", "start rollback selection inventory!");
						writeSysDeductLog(lm,true);
						return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
					}
				}
				
			}else {
				String message = "InventoryUpdateDomain.updateInventory>isEnough:"+isEnough+",goodsId:"+goodsId+",isSelectionEnough:"+isSelectionEnough+",selectionParam:"+selectionParam+",message:"+CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY.getDescription();
				logSysDeduct.info(message);
				return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
			}
			// 更新分店库存
			/*if (isSuppliersEnough) {
				if(!CollectionUtils.isEmpty(suppliersParam)) {
					//扣减开始记录日志
					lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("suppliersParam", suppliersParam).addMetaData("start", "start deduct suppliers inventory!");
					writeSysDeductLog(lm,true);
					boolean rACK4Supp = this.goodsInventoryDomainRepository
							.updateSuppliersInventory(suppliersParam);
					lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("suppliersParam", suppliersParam).addMetaData("resultAck", rACK4Supp).addMetaData("end", "end deduct suppliers inventory!");
					writeSysDeductLog(lm,true);
					// 校验库存
					if (!rACK4Supp) {
						// 回滚库存
						lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("suppliersParam", suppliersParam).addMetaData("rollback,start", "start rollback suppliers inventory!");
						writeSysDeductLog(lm,true);
						// 先回滚总的 再回滚分店的
						List<Long> rbackAck4Supp = this.goodsInventoryDomainRepository.updateGoodsInventory(
								goodsId,goodsBaseId, (limtStorgeDeNum),(goodsDeductNum));
						boolean rbackACK4Supp = this.goodsInventoryDomainRepository
								.rollbackSuppliersInventory(suppliersParam);
						
						lm.setMethod("InventoryUpdateDomain.updateInventory").addMetaData("goodsId", goodsId).addMetaData("suppliersParam", suppliersParam).addMetaData("resultAck", rbackAck4Supp+",suppliersRollback:"+rbackACK4Supp).addMetaData("rollback,end", "start rollback suppliers inventory!");
						writeSysDeductLog(lm,true);
						
						return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
					}
				}
				
			}else {
				String message = "InventoryUpdateDomain.updateInventory>isEnough:"+isEnough+",goodsId:"+goodsId+",isSuppliersEnough:"+isSuppliersEnough+",suppliersParam:"+suppliersParam+",message:"+CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY.getDescription();
				logSysDeduct.info(message);
				return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
			}*/

		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"updateInventory error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally{
			dLock.unlockManual(key);
		}
		lm.addMetaData("result", "end");
		writeSysDeductLog(lm,false);
		return CreateInventoryResultEnum.SUCCESS;
	}

	public String pushSendMsgQueue() {
		// 填充队列
		if (fillInventoryQueueDO()) {
			if(queueDO!=null&&queueDO.getId()!=null&&queueDO.getId()!=0) {
				return this.goodsInventoryDomainRepository.pushQueueSendMsg(queueDO);
			}
			return null;
		}else {
			return null;
		}
	}

	
	// 初始化库存
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum initCheck() {
		this.goodsId = Long.valueOf(StringUtils.isEmpty(param.getGoodsId())?"0":param.getGoodsId());
		if(StringUtils.isEmpty(param.getGoodsBaseId())&&goodsId!=0) {  //为了兼容参数goodsbaseid不传的情况
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
			this.goodsBaseId = Long.valueOf(param.getGoodsBaseId());
		}
		
		// 初始化加分布式锁
		lm.addMetaData("initCheck", "initCheck,start").addMetaData(
				"initCheck[" + (goodsId) + "]", goodsId);
		writeBusInitLog(lm, false);
		LockResult<String> lockResult = null;
		CreateInventoryResultEnum resultEnum = null;
		String key = DLockConstants.INIT_LOCK_KEY + "_goodsId_" + goodsId;
		try {
			lockResult = dLock.lockManualByTimes(key, 5000L, 5);
			if (lockResult == null
					|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
							.getCode()) {
				writeBusInitLog(
						lm.setMethod("dLock").addMetaData("errorMsg", goodsId),
						false);
			}
			InventoryInitDomain create = new InventoryInitDomain(goodsId, lm);
			// 注入相关Repository
		    //create.setWmsGoodsId(wmsGoodsId);
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			resultEnum = create.businessExecute();
		} finally {
			dLock.unlockManual(key);
		}
		lm.addMetaData("result", resultEnum);
		lm.addMetaData("result", "end");
		writeBusInitLog(lm, false);
		return resultEnum;
	}
	
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			updateActionDO.setGoodsBaseId(goodsBaseId);
			if (inventoryInfoDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SELF
						.getDescription());
				updateActionDO.setOriginalInventory(String
						.valueOf(originalGoodsInventory));
				
				updateActionDO.setInventoryChange(StringUtil.strHandler(goodsDeductNum, selectionDeductNum, suppliersDeductNum));
			}
			if (!CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_SELECTION
						.getDescription():updateActionDO.getBusinessType()+",选型："+ResultStatusEnum.GOODS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSelection(selectionList):updateActionDO.getItem()+",选型item："+StringUtil
						.getIdsStringSelection(selectionList));
				updateActionDO.setOriginalInventory(StringUtils.isEmpty(updateActionDO.getOriginalInventory())?JsonUtils.convertObjectToString(selectionParam):updateActionDO.getOriginalInventory()+",选型初始库存："+JsonUtils.convertObjectToString(selectionParam));
				updateActionDO.setInventoryChange(StringUtils.isEmpty(updateActionDO.getInventoryChange())?JsonUtils.convertObjectToString(selectionParam):updateActionDO.getInventoryChange()+",选型库存变化量："+JsonUtils.convertObjectToString(selectionParam));
			}
			if (!CollectionUtils.isEmpty(suppliersList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription():updateActionDO.getBusinessType()+",分店："+ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSuppliers(suppliersList):updateActionDO.getItem()+",分店item："+StringUtil
						.getIdsStringSuppliers(suppliersList));
				updateActionDO.setOriginalInventory(StringUtils.isEmpty(updateActionDO.getOriginalInventory())?JsonUtils.convertObjectToString(suppliersParam):updateActionDO.getOriginalInventory()+",分店初始库存："+JsonUtils.convertObjectToString(suppliersParam));
				updateActionDO.setInventoryChange(StringUtils.isEmpty(updateActionDO.getInventoryChange())?JsonUtils.convertObjectToString(suppliersParam):updateActionDO.getInventoryChange()+",分店库存变化量："+JsonUtils.convertObjectToString(suppliersParam));
			}
			updateActionDO.setActionType(ResultStatusEnum.DEDUCTION_INVENTORY
					.getDescription());
			if(!StringUtils.isEmpty(param.getUserId())) {
				this.userId = (Long.valueOf(param.getUserId()));
			}
			updateActionDO.setUserId(userId);
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			if(!StringUtils.isEmpty(param.getOrderId())) {
				updateActionDO.setOrderId(Long.valueOf(param.getOrderId()));
			}
			updateActionDO.setContent(JsonUtils.convertObjectToString(param)); // 操作内容
			updateActionDO.setRemark("扣减库存");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillInventoryUpdateActionDO error" + e.getMessage()),false, e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}

	/**
	 * 填充库存队列信息
	 * 
	 * @return
	 */
	public boolean fillInventoryQueueDO() {
		GoodsInventoryQueueDO queueDO = new GoodsInventoryQueueDO();
		try {
			Long queueId = sequenceUtil.getSequence(SEQNAME.seq_queue_send);
			queueDO.setId(queueId);
			queueDO.setGoodsId(goodsId);
	        queueDO.setGoodsBaseId(goodsBaseId);
	        queueDO.setLimitStorage(limitStorage);
			if(!StringUtils.isEmpty(param.getOrderId())) {
				queueDO.setOrderId(Long.valueOf(param.getOrderId()));
			}
			if(!StringUtils.isEmpty(param.getUserId())) {
				queueDO.setUserId(Long.valueOf(param.getUserId()));
			}
			queueDO.setCreateTime(TimeUtil.getNowTimestamp10Long());
			// 封装库存变化信息到队列
			queueDO.setOriginalGoodsInventory(originalGoodsInventory);
			queueDO.setDeductNum(goodsDeductNum);
			queueDO.setSuppliersParam(suppliersParam);
			queueDO.setSelectionParam(selectionParam);

		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillInventoryQueueDO error" +e.getMessage()),false, e);
			this.queueDO = null;
			return false;
		}
		this.queueDO = queueDO;
		return true;
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
		//选型或分店商品校验
		//GoodsVerificationDomain vfDomian = new GoodsVerificationDomain(Long.valueOf(param.getGoodsId()),param.getGoodsSelection(),param.getGoodsSuppliers());
		//vfDomian.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		//return vfDomian.checkParam();

		return CreateInventoryResultEnum.SUCCESS;
	}

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	
	public void setSynInitAndAysnMysqlService(
			SynInitAndAysnMysqlService synInitAndAysnMysqlService) {
		this.synInitAndAysnMysqlService = synInitAndAysnMysqlService;
	}

	public void setdLock(DLockImpl dLock) {
		this.dLock = dLock;
	}

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
