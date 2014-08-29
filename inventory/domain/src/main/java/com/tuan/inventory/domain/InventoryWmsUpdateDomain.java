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
	private DLockImpl dLock;//分布式锁
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryWMSDO wmsDO;
	private List<GoodsInventoryDO> goodsList;
	//选型商品类型的id列表
	private List<Long> selGoodsTypeIds;
	//缓存选型的id
	private List<Long> selIds;
	private String wmsGoodsId;  //物流商品的一种编码
	private List<GoodsSelectionModel> selectionList;
	private boolean isEnough;
	private boolean isSelectionEnough = false;
	// 需扣减的商品库存
	private int wmsGoodsDeductNum = 0;
	private int wmsSelectionDeductNum;
	// 原剩余库存
	private int orileftnum = 0;
	// 原总库存
	private int oritotalnum = 0;
	// 领域中缓存选型和分店原始库存和扣减库存的list
	private List<GoodsWmsSelectionResult> selectionParam;
	// 当前库存
	//private List<Long> resultACK;
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
	private boolean selectionInventoryHandler() {
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
							&& selectionDO.getLimitStorage() == 1) { //为了计算销量 不管是否限制库存的都要扣减
						// 扣减库存并返回扣减标识,计算库存并
						/*if ((selectionDO.getLeftNumber() + model.getNum()) <= 0) {
							// 该处为了保证只要有一个选型商品库存不足则返回库存不足
							//this.isSelectionEnough = false;
							return false;
						} else {*/
						   
						    setWmsSelectionDeductNum(model.getNum());
							selection = new GoodsWmsSelectionResult();
							//redis更新用
							selection.setId(model.getId());
							//mysql更新用
							selection.setGoodTypeId(model.getGoodTypeId());
							// 扣减的库存量
							if ((selectionDO.getLeftNumber() + model.getNum()) < 0) {
								selection.setLeftNum(0);
							}else {
								selection.setLeftNum(selectionDO.getLeftNumber() + model.getNum());
							}
							if ((selectionDO.getTotalNumber() + model.getNum()) < 0) {
								selection.setLeftNum(0);
								selection.setTotalNum(0);
							}else {
								selection.setTotalNum(selectionDO.getTotalNumber() + model.getNum());
							}
							
							
							// 选型库存，并且是库存充足时用
							this.selectionParam.add(selection);
							
						//}

					}else if(selectionDO != null
							&& selectionDO.getLimitStorage() == 0){
						return true;
					}

				}// if

			}// for

		} 
		} catch (Exception e) {
			logSysUpdate.error(lm.addMetaData("errorMsg",
							"selectionInventoryHandler error" + e.getMessage()).toJson(false), e);
			return false;
			
		}
		return true;
	}

	

	private CreateInventoryResultEnum calculateInventory() {
		// 商品本身扣减库存量
		int deductNum = param.getNum();
		// 再次查询物流商品库存信息[确保最新数据]
		GoodsInventoryWMSDO tmpwmsDO = this.goodsInventoryDomainRepository.queryGoodsInventoryWms(StringUtils.isEmpty(wmsGoodsId)?param.getWmsGoodsId():wmsGoodsId);
		if(tmpwmsDO==null) {
			return CreateInventoryResultEnum.NO_WMS_DATA;
		}
		
		//goodsList
		if(!CollectionUtils.isEmpty(param.getGoodsIds())) {
			List<GoodsInventoryDO> 	goodsListTmp = new ArrayList<GoodsInventoryDO>();
			for(Long goodsId:param.getGoodsIds()) {
				GoodsInventoryDO tmpGoodsDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if(tmpGoodsDO!=null) {
					if (((tmpGoodsDO.getLeftNumber()+deductNum) >= 0)) {
						tmpGoodsDO.setLeftNumber(tmpGoodsDO.getLeftNumber()+deductNum);
					}else {
						tmpGoodsDO.setLeftNumber(0);
					}
					if ((tmpGoodsDO.getTotalNumber()+deductNum>=0)) {
						tmpGoodsDO.setTotalNumber(tmpGoodsDO.getTotalNumber()+deductNum);
					}else {
						tmpGoodsDO.setLeftNumber(0);
						tmpGoodsDO.setTotalNumber(0);
					}
					goodsListTmp.add(tmpGoodsDO);
				}
			}
			setGoodsList(goodsListTmp);
			
		}
		// 原始库存
		int tmporileftnum = tmpwmsDO.getLeftNumber();
		int tmporitotalnum = tmpwmsDO.getTotalNumber();
		
		this.orileftnum = tmporileftnum;
		this.oritotalnum = tmporitotalnum;
		//赋值
		setWmsGoodsDeductNum(deductNum);
		setWmsDO(tmpwmsDO);
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		long startTime = System.currentTimeMillis();
		
		lm.addMetaData("init start", startTime)
				.toJson(true);
		if(logSysUpdate.isDebugEnabled()) {
			logSysUpdate.debug(lm.toJson(false));
		}
		// 初始化检查
		CreateInventoryResultEnum resultEnum =	this.initCheck();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.addMetaData("endTime", endTime).addMetaData("wmsGoodsId", wmsGoodsId)
				.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription());
		if(logSysUpdate.isDebugEnabled()) {
			logSysUpdate.debug(lm.toJson(false));
		}
		
		if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
			return resultEnum;
		}
		// 真正的库存更新业务处理
		try {
			// 商品选型处理
			if(!this.selectionInventoryHandler()) {
				return CreateInventoryResultEnum.SYS_ERROR;
			}else {
				this.isSelectionEnough = true;
			}
			CreateInventoryResultEnum result =	calculateInventory();
			
			if(result!=null&&!(result.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return resultEnum;
			}else {
				this.isEnough = true;
			}
			
		} catch (Exception e) {
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"busiCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存
	@SuppressWarnings({ "unchecked", "static-access" })
	public CreateInventoryResultEnum updateAdjustWmsInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			LockResult<String> lockResult = null;
			String key = DLockConstants.JOB_HANDLER+"_wmsGoodsId_" + wmsGoodsId;
			try {
				lockResult = dLock.lockManualByTimes(key, DLockConstants.ADJUSTK_LOCK_TIME, DLockConstants.ADJUST_LOCK_RETRY_TIMES);
				if (lockResult == null
						|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
								.getCode()) {
					logSysUpdate.info(lm.addMetaData("wmsGoodsId",
							wmsGoodsId).addMetaData("errorMsg",
									"updateAdjustWmsInventory dlock error").toJson(false));
				}
				// 更新商品库存
				if (isEnough) {
					if (wmsDO != null) {
						// 更新inventoryInfoDO对象的库存属性值
						if(this.orileftnum
								+ wmsGoodsDeductNum<0) {
							this.wmsDO.setLeftNumber(0);
						}else {
							this.wmsDO.setLeftNumber(this.orileftnum
									+ wmsGoodsDeductNum);
						}
						if(this.oritotalnum
								+ wmsGoodsDeductNum<0) {
							this.wmsDO.setLeftNumber(0);
							this.wmsDO.setTotalNumber(0);
						}else {
							this.wmsDO.setTotalNumber(this.oritotalnum
									+ wmsGoodsDeductNum);
						}
						
						
						
					}else {
						return CreateInventoryResultEnum.NO_WMS_DATA;
					}

				}else {
					String message = "InventoryWmsUpdateDomain.updateAdjustWmsInventory>isEnough:"+isEnough+",orileftnum:"+orileftnum+",oritotalnum="+oritotalnum+",wmsGoodsDeductNum="+wmsGoodsDeductNum+",message="+CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY.getDescription();
					lm.addMetaData("message", message);
					logSysUpdate.info(lm.toJson(false));
					return CreateInventoryResultEnum.AFT_ADJUST_INVENTORY;
				}
				//
				//更新mysql
				CreateInventoryResultEnum handlerResultEnum = this.synUpdateMysqlInventory(wmsDO,
						goodsList, selectionParam);
				if (handlerResultEnum != handlerResultEnum.SUCCESS) {
					logSysUpdate.info("wmsGoodsId:"+ wmsGoodsId+",wmsDO:"+wmsDO+",goodsList:"+goodsList+",selectionParam:"+selectionParam+",handlerResult:"+handlerResultEnum.getDescription().toString());
					return handlerResultEnum;
				}
				
				//redis
				// 调整物流库存
				/*resultACK = this.goodsInventoryDomainRepository
						.updateGoodsWmsInventory(wmsGoodsId,
								(wmsGoodsDeductNum));*/
				String ackOk = goodsInventoryDomainRepository.saveAndUpdateGoodsWmsInventory(wmsDO);
				boolean verifyflg = false;
				if(!StringUtils.isEmpty(ackOk)&&ackOk.equalsIgnoreCase("ok")) {
					 verifyflg = this.goodsInventoryDomainRepository
								.updateBatchGoodsInventory(goodsList, wmsGoodsDeductNum);
					 //verifyflg = verifyInventory();
				}
				logSysUpdate.info("updateAdjustWmsInventory redis,end"+",resultwms:"+ackOk+",resultgoods:"+verifyflg);
				// 校验库存
				if (!verifyflg&&!CollectionUtils.isEmpty(goodsList)) {
					// 回滚库存
					List<Long> responseResult =	this.goodsInventoryDomainRepository
							.updateGoodsWmsInventory(wmsGoodsId,
									(-wmsGoodsDeductNum));

					boolean responseFlg = this.goodsInventoryDomainRepository
							.updateBatchGoodsInventory(goodsList,
									(-wmsGoodsDeductNum));
					logSysUpdate.info("updateAdjustWmsInventory rollback redis,end,responseResult:"+responseResult+",responseFlg:"+responseFlg);
					return CreateInventoryResultEnum.AFT_ADJUST_INVENTORY;
				}
				
				// 更新选型库存
				if (isSelectionEnough&&!CollectionUtils.isEmpty(selectionList)) {
					String aCKok = this.goodsInventoryDomainRepository
							.batchAdjustSelectionWms(selectionParam);
					logSysUpdate.info("selection mysql,end,resultACK:"+aCKok);
					if(StringUtils.isEmpty(aCKok)||!StringUtils.isEmpty(aCKok)&&!aCKok.equalsIgnoreCase("ok")) {
						// 先回滚总的 再回滚选型的
						List<Long> rollbackResponse = this.goodsInventoryDomainRepository
								.updateGoodsWmsInventory(wmsGoodsId,
										(-wmsGoodsDeductNum));
						List<Long> rSelResponse = this.goodsInventoryDomainRepository
								.batchrollbackSelectionWms(selectionParam);
						logSysUpdate.info("rollback redis,end,rollbackResponse:"+ rollbackResponse+",rSelResponse:"+rSelResponse);
					}
					
				}
			} finally{
				dLock.unlockManual(key);
			}

		} catch (Exception e) {
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"updateAdjustWmsInventory error" + e.getMessage()).toJson(false), e);
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
			CreateInventoryResultEnum resultEnum = null;
			LockResult<String> lockResult = null;
			
			String key = DLockConstants.INIT_LOCK_KEY+"_wmsGoodsId_" + wmsGoodsId;
			try {
				lockResult = dLock.lockManualByTimes(key, DLockConstants.INIT_LOCK_TIME, DLockConstants.INIT_LOCK_RETRY_TIMES);
				if (lockResult == null
						|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
								.getCode()) {
					logSysUpdate.info(lm.addMetaData("InventoryWmsUpdateDomain initCheck dLock errorMsg",
							wmsGoodsId).toJson(false));
				}
				InventoryInitDomain create = new InventoryInitDomain();
				//注入相关Repository
				create.setWmsGoodsId(wmsGoodsId);
				create.setLm(lm);
				create.setGoodsTypeIdList(selGoodsTypeIds);
				//create.setSelIds(selIds);
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
				resultEnum = create.business4WmsExecute();
			} finally{
				dLock.unlockManual(key);
			}
			return resultEnum;
			}
	
	//异步更新mysql商品库存
		public CreateInventoryResultEnum synUpdateMysqlInventory(GoodsInventoryWMSDO wmsDO, List<GoodsInventoryDO> wmsInventoryList,List<GoodsWmsSelectionResult>  selectionParam) {
			InventoryInitDomain create = new InventoryInitDomain();
			//注入相关Repository
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			create.setLm(lm);
			return create.updateWmsMysqlInventory(wmsDO, wmsInventoryList,selectionParam);
		}
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			if(!CollectionUtils.isEmpty(param.getGoodsIds())) {
				updateActionDO.setRemark(StringUtils.isEmpty(updateActionDO.getRemark())?"商品id:"+StringUtil.getGoodsIds(param.getGoodsIds())+"[调整数量:"+param.getNum()+"]":updateActionDO.getRemark()+",商品id:"+StringUtil.getGoodsIds(param.getGoodsIds())+"[调整数量:"+param.getNum()+"]");
			}
			if(!StringUtils.isEmpty(param.getWmsGoodsId())) {
				updateActionDO.setRemark(StringUtils.isEmpty(updateActionDO.getRemark())?"物流编码:"+param.getWmsGoodsId()+"[调整数量:"+param.getNum()+"]":updateActionDO.getRemark()+",物流编码:"+param.getWmsGoodsId()+"[调整数量:"+param.getNum()+"]");
			}
			
			if (wmsDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_WMS
						.getDescription());
				updateActionDO.setOriginalInventory("leftnum:"+String
						.valueOf(orileftnum)+",totalnum:"+String
						.valueOf(oritotalnum));
				
				updateActionDO.setInventoryChange(StringUtil.strHandler(wmsGoodsDeductNum, getWmsSelectionDeductNum(), 0));
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
			updateActionDO.setRemark(StringUtils.isEmpty(updateActionDO.getRemark())?"物流库存调整":updateActionDO.getRemark()+",物流库存调整");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"fillInventoryUpdateActionDO error" + e.getMessage()).toJson(false), e);
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

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public void setdLock(DLockImpl dLock) {
		this.dLock = dLock;
	}

	public void setWmsDO(GoodsInventoryWMSDO wmsDO) {
		this.wmsDO = wmsDO;
	}

	public void setGoodsList(List<GoodsInventoryDO> goodsList) {
		this.goodsList = goodsList;
	}
	
	public void setLm(LogModel lm) {
		this.lm = lm;
	}

	public int getWmsGoodsDeductNum() {
		return wmsGoodsDeductNum;
	}

	public void setWmsGoodsDeductNum(int wmsGoodsDeductNum) {
		this.wmsGoodsDeductNum = wmsGoodsDeductNum;
	}

	public int getWmsSelectionDeductNum() {
		return wmsSelectionDeductNum;
	}

	public void setWmsSelectionDeductNum(int wmsSelectionDeductNum) {
		this.wmsSelectionDeductNum = wmsSelectionDeductNum;
	}

	

}
