package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.core.common.lock.eum.LockResultCodeEnum;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.core.common.lock.res.LockResult;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.WmsInventoryParam;

public class InventoryWmsUpdateDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private WmsInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	private DLockImpl dLock;//分布式锁
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryWMSDO wmsDO;
	private List<GoodsInventoryDO> goodsList;
	//选型商品类型的id列表
	private List<Long> selGoodsTypeIds;
	//缓存选型的id
	private List<Long> selIds;
	private String wmsGoodsId;  //物流商品的一种编码
	//private Long goodsId;
	private List<GoodsSelectionModel> selectionList;
	private boolean isEnough;
	private boolean isSelectionEnough = true;
	// 需扣减的商品库存
	private int wmsGoodsDeductNum = 0;
	private int wmsSelectionDeductNum = 0;
	// 原剩余库存
	private int orileftnum = 0;
	// 原总库存
	private int oritotalnum = 0;
	// 领域中缓存选型和分店原始库存和扣减库存的list
	private List<GoodsWmsSelectionResult> selectionParam;
	// 当前库存
	private List<Long> resultACK;
	private SequenceUtil sequenceUtil;

	public InventoryWmsUpdateDomain(String clientIp, String clientName,
			WmsInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	/**
	 * 处理选型库存
	 */
	private void selectionInventoryHandler() {
		try {
		if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // if1
			this.selectionList = param.getGoodsSelection();
			this.selectionParam = new ArrayList<GoodsWmsSelectionResult>();

			for (GoodsSelectionModel model : selectionList) { // for
				if (model.getId() != null && model.getId() > 0) { // if选型
					GoodsWmsSelectionResult selection = null;
					Long selectionId = Long.valueOf(model.getId());
					// 查询商品选型库存
					GoodsSelectionDO selectionDO = this.goodsInventoryDomainRepository
							.querySelectionRelationById(selectionId);
					if (selectionDO != null
							/*&& selectionDO.getLimitStorage() == 1*/) { //为了计算销量 不管是否限制库存的都要扣减
						// 扣减库存并返回扣减标识,计算库存并
						if ((selectionDO.getLeftNumber() + model.getNum()) <= 0) {
							// 该处为了保证只要有一个选型商品库存不足则返回库存不足
							this.isSelectionEnough = false;
						} else {
							selection = new GoodsWmsSelectionResult();
							//redis更新用
							selection.setId(model.getId());
							//mysql更新用
							selection.setGoodTypeId(model.getGoodTypeId());
							// 扣减的库存量
							selection.setLeftNum(model.getNum());
							selection.setTotalNum(model.getNum());
							// 选型库存，并且是库存充足时用
							this.selectionParam.add(selection);
							
						}

					}

				}// if

			}// for

		} else {// if selection
			//isSelectionEnough = false;
		}
		} catch (Exception e) {
			isSelectionEnough = false;
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"selectionInventoryHandler error" + e.getMessage()),false, e);
			
		}
	}

	

	private void calculateInventory() {
		// 商品本身扣减库存量
		int deductNum = param.getNum();
		// 再次查询物流商品库存信息[确保最新数据]
		this.wmsDO = this.goodsInventoryDomainRepository.queryGoodsInventoryWms(wmsGoodsId);
		//goodsList
		if(!CollectionUtils.isEmpty(param.getGoodsIds())) {
			goodsList = new ArrayList<GoodsInventoryDO>();
			for(Long goodsId:param.getGoodsIds()) {
				GoodsInventoryDO tmpGoodsDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if(tmpGoodsDO!=null) {
					if (((tmpGoodsDO.getLeftNumber()+deductNum) >= 0)||(tmpGoodsDO.getTotalNumber()+deductNum>=0)) {
						tmpGoodsDO.setLeftNumber(tmpGoodsDO.getLeftNumber()+deductNum);
						tmpGoodsDO.setTotalNumber(tmpGoodsDO.getTotalNumber()+deductNum);
					}
					goodsList.add(tmpGoodsDO);
				}
			}
			
			
		}
		
		// 原始库存
		this.orileftnum = wmsDO.getLeftNumber();
		this.oritotalnum = wmsDO.getTotalNumber();
		//赋值
		this.wmsGoodsDeductNum = deductNum;
		// 扣减库存并返回扣减标识,计算库存并
		if (((orileftnum+deductNum) >= 0)||(oritotalnum+deductNum>=0)) {
			this.isEnough = true;
			
		}
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
			return true;
		}
		return ret;
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		// 初始化检查
		CreateInventoryResultEnum resultEnum =	this.initCheck();
		
		// 真正的库存更新业务处理
		try {
			// 商品选型处理
			this.selectionInventoryHandler();
			
			if(isSelectionEnough) {
				//商品库存扣减的计算
				this.calculateInventory();
			}
			
			//}
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
			return resultEnum;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum updateAdjustWmsInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			//物流加分布式锁
			lm.addMetaData("updateAdjustWmsInventory","updateAdjustWmsInventory,start").addMetaData("wmsGoodsId", wmsGoodsId);
			writeSysUpdateLog(lm,false);
			LockResult<String> lockResult = null;
			String key = DLockConstants.ADJUST_LOCK_KEY+"_wmsGoodsId_" + wmsGoodsId;
			try {
				lockResult = dLock.lockManualByTimes(key, DLockConstants.ADJUSTK_LOCK_TIME, DLockConstants.ADJUST_LOCK_RETRY_TIMES);
				if (lockResult == null
						|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
								.getCode()) {
					writeSysUpdateLog(
							lm.setMethod("updateAdjustWmsInventory").addMetaData("wmsGoodsId",
									wmsGoodsId).addMetaData("errorMsg",
													"updateAdjustWmsInventory dlock error"), true);
				}
				// 更新商品库存
				if (isEnough) {
					if (wmsDO != null) {
						// 更新inventoryInfoDO对象的库存属性值
						this.wmsDO.setLeftNumber(this.orileftnum
								+ wmsGoodsDeductNum);
						this.wmsDO.setTotalNumber(this.oritotalnum
								+ wmsGoodsDeductNum);
					}
					if ((wmsDO != null&&wmsDO.getLeftNumber() < 0) || (wmsDO != null&&wmsDO.getTotalNumber() < 0)) {
						return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
					}

				}
				lm.addMetaData("updateAdjustWmsInventory","updateAdjustWmsInventory mysql,start").addMetaData("wmsGoodsId", wmsGoodsId).addMetaData("wmsDO", wmsDO).addMetaData("goodsList", goodsList).addMetaData("selectionParam", selectionParam);
				writeSysUpdateLog(lm,true);
				//
				//更新mysql
				boolean handlerResult = this.synUpdateMysqlInventory(wmsDO,
						goodsList, selectionParam);
				lm.addMetaData("updateAdjustWmsInventory","updateAdjustWmsInventory mysql,end").addMetaData("wmsGoodsId", wmsGoodsId).addMetaData("handlerResult", handlerResult);
				writeSysUpdateLog(lm,true);
				if (!handlerResult) {
					return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
				}
				lm.addMetaData("updateAdjustWmsInventory","updateAdjustWmsInventory redis,start").addMetaData("wmsGoodsId", wmsGoodsId).addMetaData("wmsGoodsDeductNum", wmsGoodsDeductNum).addMetaData("goodsList", goodsList);
				writeSysUpdateLog(lm,true);
				//redis
				// 调整物流库存
				resultACK = this.goodsInventoryDomainRepository
						.updateGoodsWmsInventory(wmsGoodsId,
								(wmsGoodsDeductNum));
				boolean verifyflg = this.goodsInventoryDomainRepository
						.updateBatchGoodsInventory(goodsList, wmsGoodsDeductNum);
				
				lm.addMetaData("updateAdjustWmsInventory","updateAdjustWmsInventory redis,end").addMetaData("resultwms", resultACK).addMetaData("resultgoods", verifyflg);
				writeSysUpdateLog(lm,true);
				// 校验库存
				if (!verifyInventory() || !verifyflg) {
					lm.addMetaData("updateAdjustWmsInventory","rollback redis,start").addMetaData("wmsGoodsId", wmsGoodsId).addMetaData("goodsList", goodsList).addMetaData("wmsGoodsDeductNum", wmsGoodsDeductNum);
					writeSysUpdateLog(lm,true);
					// 回滚库存
					List<Long> responseResult =	this.goodsInventoryDomainRepository
							.updateGoodsWmsInventory(wmsGoodsId,
									(-wmsGoodsDeductNum));

					boolean responseFlg = this.goodsInventoryDomainRepository
							.updateBatchGoodsInventory(goodsList,
									(-wmsGoodsDeductNum));
					lm.addMetaData("updateAdjustWmsInventory","rollback redis,end").addMetaData("responseResult", responseResult).addMetaData("responseFlg", responseFlg);
					writeSysUpdateLog(lm,true);
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
				// 更新选型库存
				if (isSelectionEnough&&!CollectionUtils.isEmpty(selectionList)) {
					lm.addMetaData("updateAdjustWmsInventory","selection mysql,start").addMetaData("selectionParam", selectionParam);
					writeSysUpdateLog(lm,true);
					resultACK = this.goodsInventoryDomainRepository
							.batchAdjustSelectionWms(selectionParam);
					lm.addMetaData("updateAdjustWmsInventory","selection mysql,end").addMetaData("resultACK", resultACK);
					writeSysUpdateLog(lm,true);
					// 校验库存
					if (!verifyInventory()) {
						lm.addMetaData("updateAdjustWmsInventory","rollback redis,start").addMetaData("wmsGoodsId", wmsGoodsId).addMetaData("wmsGoodsDeductNum", wmsGoodsDeductNum);
						writeSysUpdateLog(lm,true);
						// 回滚库存
						// 先回滚总的 再回滚选型的
						List<Long> rollbackResponse = this.goodsInventoryDomainRepository
								.updateGoodsWmsInventory(wmsGoodsId,
										(-wmsGoodsDeductNum));
						List<Long> rSelResponse = this.goodsInventoryDomainRepository
								.batchrollbackSelectionWms(selectionParam);
						lm.addMetaData("updateAdjustWmsInventory","rollback redis,end").addMetaData("rollbackResponse", rollbackResponse).addMetaData("rSelResponse", rSelResponse);
						writeSysUpdateLog(lm,true);
						return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
					}
				}
			} finally{
				dLock.unlockManual(key);
			}
			lm.addMetaData("result", "end");
			writeSysUpdateLog(lm,false);
			

		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"updateAdjustWmsInventory error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	//初始化库存
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum initCheck() {
		       //初始化物流编码
				this.wmsGoodsId = param.getWmsGoodsId();
				//初始化加分布式锁
				lm.addMetaData("InventoryWmsUpdateDomain initCheck","initCheck,start").addMetaData("initCheck[" + (wmsGoodsId) + "]", wmsGoodsId);
				writeBusInitLog(lm,false);
				LockResult<String> lockResult = null;
				CreateInventoryResultEnum resultEnum = null;
				String key = DLockConstants.INIT_LOCK_KEY+"_wmsGoodsId_" + wmsGoodsId;
				try {
					lockResult = dLock.lockManualByTimes(key, DLockConstants.INIT_LOCK_TIME, DLockConstants.INIT_LOCK_RETRY_TIMES);
					if (lockResult == null
							|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
									.getCode()) {
						writeBusInitLog(
								lm.addMetaData("InventoryWmsUpdateDomain initCheck dLock errorMsg",
										wmsGoodsId), true);
					}
					InventoryInitDomain create = new InventoryInitDomain();
					//注入相关Repository
					create.setWmsGoodsId(wmsGoodsId);
					create.setLm(lm);
					create.setGoodsTypeIdList(selGoodsTypeIds);
					//create.setSelIds(selIds);
					create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
					create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
					create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
					resultEnum = create.business4WmsExecute();
				} finally{
					dLock.unlockManual(key);
				}
				lm.addMetaData("result", resultEnum);
				lm.addMetaData("result", "end");
				writeBusInitLog(lm,false);
				return resultEnum;
			}
	
	//异步更新mysql商品库存
		public boolean synUpdateMysqlInventory(GoodsInventoryWMSDO wmsDO, List<GoodsInventoryDO> wmsInventoryList,List<GoodsWmsSelectionResult>  selectionParam) {
			InventoryInitDomain create = new InventoryInitDomain();
			//注入相关Repository
			create.setInventoryInitAndUpdateHandle(this.inventoryInitAndUpdateHandle);
			return create.updateWmsMysqlInventory(wmsDO, wmsInventoryList,selectionParam);
		}
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			if (wmsDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_WMS
						.getDescription());
				updateActionDO.setOriginalInventory("leftnum:"+String
						.valueOf(orileftnum)+",totalnum:"+String
						.valueOf(oritotalnum));
				
				updateActionDO.setInventoryChange(StringUtil.strHandler(wmsGoodsDeductNum, wmsSelectionDeductNum, 0));
			}
			if (!CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_WMS_SELECTION
						.getDescription():updateActionDO.getBusinessType()+",选型："+ResultStatusEnum.GOODS_WMS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSelection(selectionList):updateActionDO.getItem()+",选型item："+StringUtil
						.getIdsStringSelection(selectionList));
				updateActionDO.setOriginalInventory(StringUtils.isEmpty(updateActionDO.getOriginalInventory())?JsonUtils.convertObjectToString(selectionParam):updateActionDO.getOriginalInventory()+",选型初始库存："+JsonUtils.convertObjectToString(selectionParam));
				updateActionDO.setInventoryChange(StringUtils.isEmpty(updateActionDO.getInventoryChange())?JsonUtils.convertObjectToString(selectionParam):updateActionDO.getInventoryChange()+",选型库存变化量："+JsonUtils.convertObjectToString(selectionParam));
			}
			
			updateActionDO.setActionType(ResultStatusEnum.ADJUST_WMSINVENTORY
					.getDescription());
			
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			
			updateActionDO.setContent(JsonUtils.convertObjectToString(param)); // 操作内容
			updateActionDO.setRemark("物流库存调整");
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


	/**
	 * 参数检查
	 * 
	 * @return
	 */
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (param.getId()==null) { //因为这个在商品的attrbuti表里有存
			return CreateInventoryResultEnum.INVALID_WMSID;
		}
		if (param.getId()!=null&&param.getId()<=0) { //因为这个在商品的attrbuti表里有存
			return CreateInventoryResultEnum.INVALID_WMSID;
		}
		if (StringUtils.isEmpty(param.getWmsGoodsId())) {
			return CreateInventoryResultEnum.INVALID_WMSGOODSID;
		}
		List<GoodsSelectionModel> selList = param.getGoodsSelection();
		// 校验物流商品选型id
		if (!CollectionUtils.isEmpty(selList)) {
			selGoodsTypeIds = new ArrayList<Long>();
			selIds = new ArrayList<Long>();
			for (GoodsSelectionModel model : selList) {
				if (model.getId() <= 0) {
					return CreateInventoryResultEnum.INVALID_SELECTIONID;
				}
				if (model.getGoodTypeId() <= 0) {
					return CreateInventoryResultEnum.INVALID_GOODSTYPEID;
				}
				selIds.add(model.getId());
				selGoodsTypeIds.add(model.getGoodTypeId());
			}

			if (CollectionUtils.isEmpty(selIds)||CollectionUtils.isEmpty(selGoodsTypeIds)) {
				return CreateInventoryResultEnum.INVALID_SELIDANDGOODSTYPEID;
			}

		}
		//参数校验
		List<Long> goodsIds = param.getGoodsIds();
		if (!CollectionUtils.isEmpty(goodsIds)) {
			for(Long goodsId :goodsIds) {
				if(goodsId<=0) {
					return CreateInventoryResultEnum.INVALID_GOODSID;
				}
			}
			
		}
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

	public void setInventoryInitAndUpdateHandle(
			InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle) {
		this.inventoryInitAndUpdateHandle = inventoryInitAndUpdateHandle;
	}

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public void setdLock(DLockImpl dLock) {
		this.dLock = dLock;
	}

	

}
