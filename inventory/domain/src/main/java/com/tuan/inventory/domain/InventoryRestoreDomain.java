package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.SynInitAndAsynUpdateDomainRepository;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.RestoreInventoryParam;

public class InventoryRestoreDomain extends AbstractDomain {
	protected static Log logger = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private RestoreInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	private SequenceUtil sequenceUtil;
	
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryInfoDO4OldGoods;
	private GoodsInventoryDO inventoryInfoDO4NewGoods;
	
	private List<GoodsSelectionDO> selectionRelation;
	private List<GoodsSuppliersDO> suppliersRelation;
	private String orderIds;  //订单id串
	private Long preGoodsId;// 改价前商品ID
	private Long goodsId;
	private Long goodsBaseId;
	private int limitStorage; // 0:库存无限制；1：限制库存
	private Long userId;
	private String type = "2";
	//private String id;
	private int adjustNum;
	// 改价前商品是否存在：初始化后才有效
	boolean isOldGoodsExists = false;
	boolean idemptent = false;
	
	public InventoryRestoreDomain(String clientIp, String clientName,
			RestoreInventoryParam param, LogModel lm) {
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
			if(logger.isDebugEnabled()) {
				logger.debug(lm.setMethod(method).addMetaData("start", startTime)
						.toJson(false));
			}
			
			//初始化检查
			CreateInventoryResultEnum resultEnum = this.initPreGoodsCostCheck();
				
				long endTime = System.currentTimeMillis();
				String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
						+ "milliseconds(毫秒)执行完成!";
				if(logger.isDebugEnabled()) {
					logger.debug(lm.setMethod(method).addMetaData("endTime", endTime).addMetaData("goodsBaseId", goodsBaseId).addMetaData("preGoodsId", preGoodsId)
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
	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		try {
			this.type = StringUtils.isEmpty(param.getType())?"2":param.getType();
			this.adjustNum = param.getNum();
			this.orderIds = param.getOrderIds();
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
			if(goodsId==preGoodsId) {// 非改价商品的调整
				return CreateInventoryResultEnum.NO_RETORE;
			}
			GoodsInventoryDO	inventoryDO4OldGoods = this.goodsInventoryDomainRepository.queryGoodsInventory(preGoodsId);
			if(inventoryDO4OldGoods!=null) {
				limitStorage = inventoryDO4OldGoods.getLimitStorage();
			}else {
				return CreateInventoryResultEnum.NO_GOODS;
			}
			if(limitStorage==0) {  //非限制库存商品无需处理 ，0:库存无限制；1：限制库存
					return CreateInventoryResultEnum.SUCCESS;
				}
			
			//加载新商品
			GoodsInventoryDO	tmp4newGoods = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
			//GoodsBaseInventoryDO baseDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
			if(tmp4newGoods==null) {
				return CreateInventoryResultEnum.NO_GOODS;
			}
			
			//组装信息更新该老商品库	
			return fillInventoryDO(adjustNum, inventoryDO4OldGoods,tmp4newGoods);
			
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
							"InventoryRestoreDomain busiCheck error" + e.getMessage()).toJson(false), e);
			
			return CreateInventoryResultEnum.SYS_ERROR;
		}

	}

	// 新增库存
	public CreateInventoryResultEnum restoreInventory() {
		try {
			/*if(idemptent) {  //幂等控制，已处理成功
				return CreateInventoryResultEnum.SUCCESS;
			}*/
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			if(limitStorage==0) {  //非限制库存商品无需处理 ，0:库存无限制；1：限制库存
				return CreateInventoryResultEnum.SUCCESS;
			}
			//更新老商品的库存
			CreateInventoryResultEnum resultEnum = this.updatePreGoodsInventory();
			if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return resultEnum;
			}
			
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"InventoryRestoreDomain createInventory error" + e.getMessage()).toJson(false), e);
			
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		//保存库存
		return this.updateNewGoodsInventory();
	}
	//保存改价商品
	public CreateInventoryResultEnum updateNewGoodsInventory() {
		InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
		//注入相关Repository
		create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
		create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		create.setLm(lm);
		return create.updateInventory4GoodsCost(inventoryInfoDO4NewGoods, selectionRelation, suppliersRelation);
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
			logger.error(lm.addMetaData("errorMsg",
					"InventoryRestoreDomain sendNotify error" + e.getMessage()).toJson(false), e);
		}
	}
	public void sendPreGoodsInventoryNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam(preGoodsId);
			goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryCreate4GoodsCostDomain_pre.toString(),JSONObject
					.fromObject(notifyParam));
			
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"InventoryRestoreDomain sendPreGoodsInventoryNotify error" + e.getMessage()).toJson(false), e);
		}
	}
	public void sendAftGoodsInventoryNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam(goodsId);
			goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryCreate4GoodsCostDomain_aft.toString(),JSONObject
					.fromObject(notifyParam));
			
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"InventoryRestoreDomain sendAftGoodsInventoryNotify error" + e.getMessage()).toJson(false), e);
		}
	}

	// 填充notifyserver发送参数
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(long goodsId) {
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		
		notifyParam.setUserId(this.userId);
		notifyParam.setGoodsId(goodsId);
		GoodsInventoryDO inventoryInfo  = goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
		if(inventoryInfo!=null) {
			notifyParam.setLimitStorage(inventoryInfo.getLimitStorage());
			notifyParam.setWaterfloodVal(inventoryInfo.getWaterfloodVal());
			notifyParam.setTotalNumber(inventoryInfo.getTotalNumber());
			notifyParam.setLeftNumber(inventoryInfo.getLeftNumber());
			notifyParam.setGoodsBaseId(inventoryInfo.getGoodsBaseId());
			//销量
			notifyParam.setSales(inventoryInfo.getGoodsSaleCount()==null?0:inventoryInfo.getGoodsSaleCount());
		}
		
		//库存基本信息
		GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
		if(baseInventoryDO!=null) {
			notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
			notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
		}
		
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
			
			updateActionDO.setActionType(ResultStatusEnum.TASK_RESTORE_INVENTORY
					.getDescription());
			updateActionDO.setUserId(userId);
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setOrderId(0l);
			if (isOldGoodsExists) { // 改价前商品
			//将商品信息加载上来
		    GoodsInventoryDO oldGoods = this.goodsInventoryDomainRepository.queryGoodsInventory(preGoodsId);
			updateActionDO.setContent("inventoryInfoDO4NewGoods:["+JSON.toJSONString(inventoryInfoDO4NewGoods)+"],inventoryInfoDO4OldGoods:["+JSON.toJSONString(oldGoods)+"]"); // 操作内容
			}
			updateActionDO.setRemark("还原库存,preGoodsId("+preGoodsId+"),goodsId("+goodsId+"),orderIds("+orderIds+")");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
			
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"InventoryRestoreDomain fillInventoryUpdateActionDO error" + e.getMessage()).toJson(false), e);
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

		return CreateInventoryResultEnum.SUCCESS;
	}

	//组装改价商品信息
	public CreateInventoryResultEnum fillInventoryDO(int restoreNum,GoodsInventoryDO oldDO,GoodsInventoryDO newDO) {
		//GoodsBaseInventoryDO tmpbaseDO = new GoodsBaseInventoryDO();
		GoodsInventoryDO preInventoryInfoDO = new GoodsInventoryDO();
		GoodsInventoryDO newInventoryInfoDO = new GoodsInventoryDO();
		try {
			
			if((restoreNum>oldDO.getLeftNumber())&&oldDO.getLeftNumber()>0) {//占用库存量大于等于剩余库存数时,意味着最多只能还leftnumber个
				oldDO.setLeftNumber(0);
				oldDO.setTotalNumber(oldDO.getTotalNumber()-oldDO.getLeftNumber());
				oldDO.setUserId(userId);
				preInventoryInfoDO = oldDO;
				
				newDO.setLeftNumber(newDO.getLeftNumber()+oldDO.getLeftNumber());
				newDO.setTotalNumber(newDO.getTotalNumber()+oldDO.getLeftNumber());
				newDO.setUserId(userId);
				newInventoryInfoDO = newDO;
				
			}else if((restoreNum<=oldDO.getLeftNumber())&&oldDO.getLeftNumber()>0) { //当你还的库存数量《=当前的剩余库存,并且当前剩余库存》0的,当剩余库存为0时不再处理
				oldDO.setLeftNumber(oldDO.getLeftNumber()-restoreNum);
				oldDO.setTotalNumber(oldDO.getTotalNumber()-restoreNum);
				oldDO.setUserId(userId);
				preInventoryInfoDO = oldDO;
				
				newDO.setLeftNumber(newDO.getLeftNumber()+restoreNum);
				newDO.setTotalNumber(newDO.getTotalNumber()+restoreNum);
				newDO.setUserId(userId);
				newInventoryInfoDO = newDO;
			}else if(oldDO.getLeftNumber()==0){  //库存已还完时
				return CreateInventoryResultEnum.HAD_RETORE_COMPLETED;
			}
			
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"InventoryRestoreDomain fillPreInventoryDO error" + e.getMessage()).toJson(false), e);
			this.inventoryInfoDO4OldGoods = null;
			this.inventoryInfoDO4NewGoods = null;
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		this.inventoryInfoDO4OldGoods = preInventoryInfoDO;
		this.inventoryInfoDO4NewGoods = newInventoryInfoDO;
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 初始化改价前商品
	//@SuppressWarnings("unchecked")
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
	

}
