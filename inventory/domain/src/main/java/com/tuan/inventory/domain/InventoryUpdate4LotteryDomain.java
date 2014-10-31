package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.UpdateLotteryInventoryParam;
import com.tuan.inventory.model.result.CallResult;

public class InventoryUpdate4LotteryDomain extends AbstractDomain {
	protected static Log logupdate = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private UpdateLotteryInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private GoodsInventoryActionDO updateActionDO;
	//private GoodsInventoryQueueDO queueDO;
	//扣减后
	private GoodsInventoryDO inventoryInfoDO;
	private GoodsInventoryDO preInventoryInfoDO;
	private Long goodsId;
	private Long goodsBaseId;
	private String objectId;
	private boolean isEnough;
	
	// 需扣减的商品库存
	private int goodsDeductNum = 0;
	
	//商品扣减量:非限制库存商品取默认值
	private int limtStorgeDeNum = 0;
	private int limitStorage = 0;
	// 原库存
	private int originalGoodsInventory = 0;
	
	// 当前库存
	private List<Long> resultACK;
	private SequenceUtil sequenceUtil;
	
	//private boolean idemptent = false;
	public InventoryUpdate4LotteryDomain(String clientIp, String clientName,
			UpdateLotteryInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	
	private CreateInventoryResultEnum calculateInventory() {
		// 再次查询商品库存信息[确保最新数据]
		//this.inventoryInfoDO 
		GoodsInventoryDO	tmpInventory = this.goodsInventoryDomainRepository
				.queryGoodsInventory(goodsId);
		if(tmpInventory==null) {
			return CreateInventoryResultEnum.NO_GOODS;
		}
		//计算库存前保存现场
		setPreInventoryInfoDO(tmpInventory);
		// 商品本身扣减库存量
		int deductNum = param.getSaleCount();

		int originLeftNumTmp = 0;
		if(tmpInventory!=null) {
			originLeftNumTmp = tmpInventory.getLeftNumber();
		}
		// 原始库存
		setOriginalGoodsInventory(originLeftNumTmp);
	
		int limitStorageTmp = tmpInventory.getLimitStorage();
        setGoodsDeductNum(deductNum);
	
		// 扣减库存并返回扣减标识,计算库存并
		if (limitStorageTmp==1&&(originalGoodsInventory-goodsDeductNum) >= 0) { //限制库存商品
			setLimitStorage(limitStorageTmp);
			//param.setLimitStorage(limitStorageTmp);
			// 商品库存扣减后的量
			setLimtStorgeDeNum(goodsDeductNum);
			tmpInventory.setLeftNumber(this.originalGoodsInventory - goodsDeductNum); 
			tmpInventory.setGoodsSaleCount(goodsDeductNum);
			
			setInventoryInfoDO(tmpInventory);
		}else if(limitStorageTmp==0) { //非限制库存商品
			//此时要更新leftnum扣减量，以便累加销量
			tmpInventory.setGoodsSaleCount(goodsDeductNum);
			setInventoryInfoDO(tmpInventory);
		}else {
			return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		//幂等性检查
		/*if (param.getGoodsId()!=0) { // if
			setGoodsId(param.getGoodsId());
			this.idemptent = idemptent();
			if (idemptent) {
				return CreateInventoryResultEnum.SUCCESS;
			}
		}*/
		long startTime = System.currentTimeMillis();
		if(logupdate.isDebugEnabled()) {
			logupdate.debug(lm.addMetaData("init start", startTime)
					.toJson(true));
		}
		
		// 初始化检查
		CreateInventoryResultEnum resultEnum =	this.initCheck("from_InventoryUpdate4LotteryDomain");
		
		long endTime = System.currentTimeMillis();
		String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		if(logupdate.isDebugEnabled()) {
			logupdate.debug(lm.addMetaData("endTime", endTime).addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId)
					.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
		}
		
		
		if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
			return resultEnum;
		}
		// 真正的库存更新业务处理
		try {
			
			CreateInventoryResultEnum result =	calculateInventory();
		
			if(result!=null&&!(result.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return resultEnum;
			}else {
				this.isEnough = true;
			}
			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
							"InventoryUpdate4LotteryDomain busiCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存
	public CreateInventoryResultEnum updateInventory() {
		logupdate.info("扣减开始>"+",objectId="+objectId+",goodsId="+goodsId);
		/*if(idemptent) {  //幂等控制，已处理成功
			return CreateInventoryResultEnum.SUCCESS;
		}*/
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			
			// 更新商品库存
			if (isEnough) {
				//扣减开始记录日志
				logupdate.info("扣减>"+",isEnough="+isEnough+",objectId="+objectId+",goodsId="+goodsId+",goodsBaseId="+goodsBaseId+",扣减数量="+goodsDeductNum+",limtStorgeDeNum="+limtStorgeDeNum);
				// 扣减库存
				resultACK = this.goodsInventoryDomainRepository
						.updateGoodsInventory(goodsId,goodsBaseId, (limtStorgeDeNum!=0?-limtStorgeDeNum:limtStorgeDeNum),(-goodsDeductNum));
				logupdate.info("扣减结束>"+",resultACK="+resultACK);
				// 校验库存
				if (!DataUtil.verifyInventory(resultACK)) {
					logupdate.info("库存不足回滚库存开始>"+",resultACK="+resultACK+",objectId="+objectId+",goodsId="+goodsId+",goodsBaseId="+goodsBaseId+",回滚扣减数量="+goodsDeductNum+",limtStorgeDeNum="+limtStorgeDeNum);
					// 回滚库存
					List<Long> rollbackAck =	this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId,goodsBaseId, (limtStorgeDeNum),(goodsDeductNum));
					logupdate.info("库存不足回滚库存结束>"+",rollbackAck="+rollbackAck);
					
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			}else {
				String message = "InventoryUpdate4LotteryDomain.updateInventory>isEnough:"+isEnough+",goodsId:"+goodsId+",objectId="+objectId+",originalGoodsInventory:"+originalGoodsInventory+",goodsDeductNum:"+goodsDeductNum+",message:"+CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY.getDescription();
				logupdate.info(message);
				return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
			}
			
			//压入扣减队列处理
			//String queueKeyId = pushSendMsgQueue();
			///if(!StringUtils.isEmpty(queueKeyId))
			    // setQueueKeyId(queueKeyId);

		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryUpdate4LotteryDomain updateInventory error" + e.getMessage()).toJson(false), e);
			
			return CreateInventoryResultEnum.SYS_ERROR;
		}

		//处理成返回前设置tag
		/*if (goodsId!=null&&goodsId!=0) {
			goodsInventoryDomainRepository.setTag(
					DLockConstants.DEDUCT_INVENTORY_SUCCESS + "_" + goodsId,
					DLockConstants.IDEMPOTENT_DURATION_TIME,
					DLockConstants.HANDLER_SUCCESS);
		}*/
		return CreateInventoryResultEnum.SUCCESS;
	}

	
	// 初始化库存
	public CreateInventoryResultEnum initCheck(String initFromDesc) {
		setGoodsId(param.getGoodsId());
		setObjectId(String.valueOf(param.getObjectId()));
		if(param.getGoodsBaseId()==0&&goodsId!=0) {  //为了兼容参数goodsbaseid不传的情况
			GoodsInventoryDO temp = this.goodsInventoryDomainRepository
					.queryGoodsInventory(goodsId);
			if(temp!=null) {
				setGoodsBaseId(temp.getGoodsBaseId());
				if(goodsBaseId!=null&&goodsBaseId==0) {
					// 初始化商品库存信息
					CallResult<GoodsInventoryDO> callGoodsInventoryDOResult = this.synInitAndAysnMysqlService
							.selectGoodsInventoryByGoodsId(goodsId);
					if (callGoodsInventoryDOResult != null&&callGoodsInventoryDOResult.isSuccess()) {
						temp = 	callGoodsInventoryDOResult.getBusinessResult();
						if(temp!=null) {
							setGoodsBaseId(temp.getGoodsBaseId());
						}
					}
				}
			}
		}else {
			setGoodsBaseId(param.getGoodsBaseId());
		}
		
		CreateInventoryResultEnum resultEnum = null;
		
			InventoryInitDomain create = new InventoryInitDomain(goodsId, lm);
			// 注入相关Repository
		    //create.setWmsGoodsId(wmsGoodsId);
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			create.setInitFromDesc(initFromDesc);
			resultEnum = create.businessExecute();

		return resultEnum;
	}
	
	// 发送库存新增消息
	public boolean sendNotify() {
			try {
				InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
				if(notifyParam!=null) {
					this.goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryUpdate4LotteryDomain.toString(),JSONObject
							.fromObject(notifyParam));
				}
				
			} catch (Exception e) {
				logupdate.error(lm.addMetaData("errorMsg",
						"InventoryConfirmScheduledDomain sendNotify error" + e.getMessage()).toJson(false), e);
				return false;
			}
			return true;
		}

		// 填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() throws Exception{
			if(goodsId==null||(goodsId!=null&&goodsId==0)) {
				return null;
			}
			InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
			//准备消息数据
			GoodsInventoryDO	notifyMsgInventory = this.goodsInventoryDomainRepository
					.queryGoodsInventory(goodsId);
			if(notifyMsgInventory!=null) {
				notifyParam.setGoodsBaseId(goodsBaseId);
				notifyParam.setUserId(param.getUserId());
				notifyParam.setGoodsId(goodsId);
				notifyParam.setLimitStorage(notifyMsgInventory.getLimitStorage());
				notifyParam.setWaterfloodVal(notifyMsgInventory.getWaterfloodVal());
				notifyParam.setTotalNumber(notifyMsgInventory.getTotalNumber());
				notifyParam.setLeftNumber(notifyMsgInventory.getLeftNumber());
				//库存总数 减 库存剩余
				Integer sales = notifyMsgInventory.getGoodsSaleCount();
				//销量
				notifyParam.setSales(sales==null?0:sales);
			}else {
				return null;
			}
			GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
			if(baseInventoryDO!=null&&baseInventoryDO.getGoodsBaseId()!=null){
				notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
				notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
			}else {
				logupdate.info("[GoodsBaseInventoryDO,非法数据]查询goodsBaseId:("+goodsBaseId+"),redis中所存储baseInventoryDO状态:"+baseInventoryDO);
				return null;
			}
			
			return notifyParam;
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
				
				updateActionDO.setInventoryChange(StringUtil.strHandler(goodsDeductNum, 0, 0));
			}
			
			updateActionDO.setActionType(ResultStatusEnum.DEDUCTION_INVENTORY
					.getDescription());
		    updateActionDO.setUserId(param.getUserId());
		
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setOrderId(param.getObjectId());
			updateActionDO.setContent("before_deduct:"+(preInventoryInfoDO!=null?JSON.toJSONString(preInventoryInfoDO):"preInventoryInfoDO is null!")+",param:"+JsonUtils.convertObjectToString(param)); // 操作内容
			updateActionDO.setRemark("抽奖商品扣减库存");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"InventoryUpdate4LotteryDomain fillInventoryUpdateActionDO error" + e.getMessage()).toJson(false), e);
			setUpdateActionDO(null);
			return false;
		}
		setUpdateActionDO(updateActionDO);
		return true;
	}

	
	/*public boolean idemptent() {
		//根据key取已缓存的tokenid  
		String gettokenid = goodsInventoryDomainRepository.queryToken(DLockConstants.DEDUCT_INVENTORY + "_"+ goodsId);
		if(StringUtils.isEmpty(gettokenid)) {  //如果为空则任务是初始的http请求过来，将tokenid缓存起来
			if(goodsId!=0) {
				goodsInventoryDomainRepository.setTag(DLockConstants.DEDUCT_INVENTORY + "_"+ goodsId, DLockConstants.IDEMPOTENT_DURATION_TIME, String.valueOf(goodsId));
			}
					
		}else {  //否则比对token值
			if(goodsId!=0) {
				if(String.valueOf(goodsId).equalsIgnoreCase(gettokenid)) { //重复请求过来，判断是否处理成功
				//根据处理成功后设置的tag来判断之前http请求处理是否成功
				String gettag = goodsInventoryDomainRepository.queryToken(DLockConstants.DEDUCT_INVENTORY_SUCCESS + "_"+ goodsId);
				if(!StringUtils.isEmpty(gettag)&&gettag.equalsIgnoreCase(DLockConstants.HANDLER_SUCCESS)) { 
								return true;
							}
						}
					}
		}
		return false;
	}*/
	/**
	 * 参数检查
	 * 
	 * @return
	 */
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (param.getGoodsId()==0) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}
		if (param.getGoodsBaseId()==0) {
			return CreateInventoryResultEnum.INVALID_GOODSBASEID;
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

	public Long getGoodsId() {
		return goodsId;
	}

	public GoodsInventoryDO getInventoryInfoDO() {
		return inventoryInfoDO;
	}

	public void setInventoryInfoDO(GoodsInventoryDO inventoryInfoDO) {
		this.inventoryInfoDO = inventoryInfoDO;
	}

	public int getGoodsDeductNum() {
		return goodsDeductNum;
	}

	public void setGoodsDeductNum(int goodsDeductNum) {
		this.goodsDeductNum = goodsDeductNum;
	}

	public int getOriginalGoodsInventory() {
		return originalGoodsInventory;
	}

	public void setOriginalGoodsInventory(int originalGoodsInventory) {
		this.originalGoodsInventory = originalGoodsInventory;
	}

	public Long getGoodsBaseId() {
		return goodsBaseId;
	}

	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public GoodsInventoryActionDO getUpdateActionDO() {
		return updateActionDO;
	}

	public void setUpdateActionDO(GoodsInventoryActionDO updateActionDO) {
		this.updateActionDO = updateActionDO;
	}

	public int getLimtStorgeDeNum() {
		return limtStorgeDeNum;
	}

	public void setLimtStorgeDeNum(int limtStorgeDeNum) {
		this.limtStorgeDeNum = limtStorgeDeNum;
	}

	public int getLimitStorage() {
		return limitStorage;
	}

	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public GoodsInventoryDO getPreInventoryInfoDO() {
		return preInventoryInfoDO;
	}

	public void setPreInventoryInfoDO(GoodsInventoryDO preInventoryInfoDO) {
		this.preInventoryInfoDO = preInventoryInfoDO;
	}


}
