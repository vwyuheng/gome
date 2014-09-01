package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.core.common.lock.eum.LockResultCodeEnum;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.core.common.lock.res.LockResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
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
	private List<GoodsInventoryDO> goodsList;  //调整后
	private List<GoodsInventoryDO> preGoodsList; //调整前
	//选型商品类型的id列表
	private List<Long> selGoodsTypeIds;
	private String wmsGoodsId;  //物流商品的一种编
	
	// 需扣减的商品库存
	private int wmsGoodsDeductNum = 0;
	// 原剩余库存
	private int wmsOrileftnum = 0;
	// 原总库存
	private int wmsOritotalnum = 0;
	private SequenceUtil sequenceUtil;

	public InventoryWmsUpdateDomain(String clientIp, String clientName,
			WmsInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	private CreateInventoryResultEnum calculateInventory() {
		// 商品本身扣减库存量
		int deductNum = param.getNum();
		// 再次查询物流商品库存信息[确保最新数据]
		GoodsInventoryWMSDO tmpwmsDO = this.goodsInventoryDomainRepository.queryGoodsInventoryWms(wmsGoodsId);
		if(tmpwmsDO==null) {
			return CreateInventoryResultEnum.NO_WMS_DATA;
		}else {  //计算wms调整后库存
			// 原始库存
			int tmporileftnum = tmpwmsDO.getLeftNumber();
			int tmporitotalnum = tmpwmsDO.getTotalNumber();
			setWmsOrileftnum(tmporileftnum);
			setWmsOritotalnum(tmporitotalnum);
			//赋值
			//setWmsGoodsDeductNum(deductNum);
			
			// 更新inventoryInfoDO对象的库存属性值
			if(tmporitotalnum
					+ deductNum<0) {
				tmpwmsDO.setLeftNumber(0);
				tmpwmsDO.setTotalNumber(0);
			}else {
				tmpwmsDO.setLeftNumber(tmporileftnum
						+ deductNum<0?0:tmporileftnum+ deductNum);
				tmpwmsDO.setTotalNumber(tmporitotalnum+ deductNum);
			}
			setWmsDO(tmpwmsDO);
		}
		
		//计算物流商品调整后库存
		if(!CollectionUtils.isEmpty(param.getGoodsIds())) {
			//调整后
			List<GoodsInventoryDO> 	goodsListTmp = new ArrayList<GoodsInventoryDO>();
			//调整前
			List<GoodsInventoryDO> 	preGoodsListTmp = new ArrayList<GoodsInventoryDO>();
			for(Long goodsId:param.getGoodsIds()) {
				GoodsInventoryDO tmpGoodsDO = null;
				if(goodsId!=null&&goodsId!=0) {
					tmpGoodsDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				} 
				if(tmpGoodsDO!=null) {
					//调整前
					preGoodsListTmp.add(tmpGoodsDO);
					//计算调整后库存
					if ((tmpGoodsDO.getTotalNumber()+deductNum<0)) {
						tmpGoodsDO.setLeftNumber(0);
						tmpGoodsDO.setTotalNumber(0);
					}else {
						tmpGoodsDO.setLeftNumber((tmpGoodsDO.getLeftNumber()+deductNum) < 0?0:tmpGoodsDO.getLeftNumber()+deductNum);
						tmpGoodsDO.setTotalNumber(tmpGoodsDO.getTotalNumber()+deductNum);
					}
					//调整后
					goodsListTmp.add(tmpGoodsDO);
				}else {
					return CreateInventoryResultEnum.NO_GOODS;
					
				}
			}
			setPreGoodsList(preGoodsListTmp);
			setGoodsList(goodsListTmp);
			
		}
		
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
			
			CreateInventoryResultEnum result =	calculateInventory();
			
			if(result!=null&&!(result.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return resultEnum;
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
				CreateInventoryResultEnum handlerResultEnum = this.synUpdateMysqlInventory(wmsDO,
						goodsList);
				if (handlerResultEnum != handlerResultEnum.SUCCESS) {
					logSysUpdate.info("wmsGoodsId:"+ wmsGoodsId+",wmsDO:"+wmsDO+",goodsList:"+goodsList+",handlerResult:"+handlerResultEnum.getDescription().toString());
					return handlerResultEnum;
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
			setWmsGoodsId(param.getWmsGoodsId());
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
		public CreateInventoryResultEnum synUpdateMysqlInventory(GoodsInventoryWMSDO wmsDO, List<GoodsInventoryDO> wmsInventoryList) {
			InventoryInitDomain create = new InventoryInitDomain();
			//注入相关Repository
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			create.setLm(lm);
			return create.updateWmsMysqlInventory(wmsDO, wmsInventoryList);
		}
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			if(!CollectionUtils.isEmpty(param.getGoodsIds())&&param.getGoodsIds().size()==1) {
				updateActionDO.setGoodsId(param.getGoodsIds().get(0));
			}
			if(!CollectionUtils.isEmpty(param.getGoodsIds())) {
				updateActionDO.setRemark(StringUtils.isEmpty(updateActionDO.getRemark())?"商品id:"+StringUtil.getGoodsIds(param.getGoodsIds())+"[调整数量:"+param.getNum()+"]":updateActionDO.getRemark()+",商品id:"+StringUtil.getGoodsIds(param.getGoodsIds())+"[调整数量:"+param.getNum()+"]");
			}
			if(!StringUtils.isEmpty(param.getWmsGoodsId())) {
				updateActionDO.setRemark(StringUtils.isEmpty(updateActionDO.getRemark())?"物流编码:"+param.getWmsGoodsId()+"[调整数量:"+param.getNum()+"]":updateActionDO.getRemark()+",物流编码:"+param.getWmsGoodsId()+"[调整数量:"+param.getNum()+"]");
			}
			
			if (wmsDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_WMS
						.getDescription());
				updateActionDO.setOriginalInventory("prewmsleftnum:"+String
						.valueOf(wmsOrileftnum)+",prewmstotalnum:"+String
						.valueOf(wmsOritotalnum)+",preGoodsList:"+JSON.toJSONString(preGoodsList));
				updateActionDO.setInventoryChange("wmsast_aftinfo:"+JSON.toJSONString(wmsDO)+",goodsast_aftinfo:"+JSON.toJSONString(goodsList));
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
		/*List<GoodsSelectionModel> selList = param.getGoodsSelection();
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

		}*/
		//参数校验
		List<Long> goodsIds = param.getGoodsIds();
		if (!CollectionUtils.isEmpty(goodsIds)) {
			for(Long goodsId :goodsIds) {
				if(goodsId<=0) {
					return CreateInventoryResultEnum.INVALID_GOODSID;
				}
			}
			
		}else {
			return CreateInventoryResultEnum.INVALID_GOODSID;
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

	public int getWmsOrileftnum() {
		return wmsOrileftnum;
	}

	public void setWmsOrileftnum(int wmsOrileftnum) {
		this.wmsOrileftnum = wmsOrileftnum;
	}

	public int getWmsOritotalnum() {
		return wmsOritotalnum;
	}

	public void setWmsOritotalnum(int wmsOritotalnum) {
		this.wmsOritotalnum = wmsOritotalnum;
	}

	public List<GoodsInventoryDO> getPreGoodsList() {
		return preGoodsList;
	}

	public void setPreGoodsList(List<GoodsInventoryDO> preGoodsList) {
		this.preGoodsList = preGoodsList;
	}

	public String getWmsGoodsId() {
		return wmsGoodsId;
	}

	public void setWmsGoodsId(String wmsGoodsId) {
		this.wmsGoodsId = wmsGoodsId;
	}
	

	

}
