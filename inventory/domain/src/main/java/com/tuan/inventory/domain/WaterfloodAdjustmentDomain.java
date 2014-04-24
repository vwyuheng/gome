package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.InitCacheDomainRepository;
import com.tuan.inventory.domain.repository.SynInitAndAsynUpdateDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;

public class WaterfloodAdjustmentDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private AdjustWaterfloodParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryDO;
	private GoodsSelectionDO selectionInventory;
	private GoodsSuppliersDO suppliersInventory;
	//初始化用
	//private List<GoodsSuppliersDO> suppliersInventoryList;
	//private List<GoodsSelectionDO> selectionInventoryList;
	private String type;
	private String id;
	//注水调整值
	private int adjustNum;
	private String businessType;
	// 原注水
	private int originalWaterfloodVal = 0;
	private Long goodsId;
	private Long selectionId;
	private Long suppliersId;
	//调整后库存
	private long resultACK;
	
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
		try {
			// 初始化检查
			this.initCheck();
			/*if (isInit) {
				this.init();
			}*/
			// 真正的业务检查处理
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF
						.getDescription();
				if (inventoryDO != null) {
					this.originalWaterfloodVal = inventoryDO.getWaterfloodVal();
				} else {
					return CreateInventoryResultEnum.IS_EXISTED;
				}
			} else if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
					.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION
						.getDescription();
				// 查询商品选型库存
				this.selectionInventory = this.goodsInventoryDomainRepository
						.querySelectionRelationById(selectionId);
				if (selectionInventory != null) {
					this.originalWaterfloodVal = selectionInventory
							.getWaterfloodVal();
				} else {
					return CreateInventoryResultEnum.IS_EXISTED;
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
					this.originalWaterfloodVal = suppliersInventory
							.getWaterfloodVal();
				} else {
					return CreateInventoryResultEnum.IS_EXISTED;
				}
			}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 调整注水
	public CreateInventoryResultEnum adjustWaterfloodVal() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);

			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.adjustGoodsWaterflood(goodsId, (adjustNum));
				if(!verifyWaterflood()) {
					//将注水还原到调整前
					this.goodsInventoryDomainRepository.adjustGoodsWaterflood(goodsId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
				//更新mysql:为了避免因mysql更新异常导致的redis数据不一致，故一定要在redis处理完成后再调mysql的处理逻辑
				this.synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryDO);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.adjustSelectionWaterfloodById(selectionId, (adjustNum));
				if(!verifyWaterflood()) {
					//将注水还原到调整前
					this.goodsInventoryDomainRepository.adjustSelectionWaterfloodById(selectionId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
				//更新选型的mysql:为了避免因mysql更新异常导致的redis数据不一致，故一定要在redis处理完成后再调mysql的处理逻辑
				this.synInitAndAsynUpdateDomainRepository.updateGoodsSelection(selectionInventory);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.adjustSuppliersWaterfloodById(suppliersId, (adjustNum));
				if(!verifyWaterflood()) {
					//将注水还原到调整前
					this.goodsInventoryDomainRepository.adjustSuppliersWaterfloodById(suppliersId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
				//更新分店的mysql:为了避免因mysql更新异常导致的redis数据不一致，故一定要在redis处理完成后再调mysql的处理逻辑
				this.synInitAndAsynUpdateDomainRepository.updateGoodsSuppliers(suppliersInventory);
			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("adjustWaterfloodVal").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	// 初始化参数
	private void fillParam() {
		// 2:商品 4：选型 6：分店
		this.type = param.getType();
		// 2：商品id 4：选型id 6 分店id
		this.id = param.getId();
		this.adjustNum = param.getNum();
		
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
				writeBusErrorLog(lm.addMetaData("errMsg", e.getMessage()), e);
			}
		}
		
		//填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(){
					InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
					notifyParam.setUserId(Long.valueOf(param.getUserId()));
					notifyParam.setGoodsId(goodsId);
					if(inventoryDO!=null) {
						notifyParam.setLimitStorage(inventoryDO.getLimitStorage());
						notifyParam.setWaterfloodVal((int)resultACK);
						notifyParam.setTotalNumber(inventoryDO.getTotalNumber());
						notifyParam.setLeftNumber(inventoryDO.getLeftNumber());
						//库存总数 减 库存剩余
						int sales = (inventoryDO.getTotalNumber())-inventoryDO.getLeftNumber();
						//销量
						notifyParam.setSales(String.valueOf(sales));
					}
					if(!CollectionUtils.isEmpty(selectionMsg)){
						this.fillSelectionMsg();
						notifyParam.setSelectionRelation(ObjectUtils.toSelectionMsgList(selectionMsg));
					}
					if(!CollectionUtils.isEmpty(suppliersMsg)){
						this.fillSuppliersMsg();
						notifyParam.setSuppliersRelation(ObjectUtils.toSuppliersMsgList(suppliersMsg));
					}
					return notifyParam;
				}
	//初始化检查
	/*public void initCheck() {
		this.fillParam();
		this.goodsId = Long.valueOf(id);
		//查询商品库存
		this.inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
		if(inventoryDO==null) {
			//初始化库存
			this.isInit = true;
			//初始化商品库存信息
			this.inventoryDO = this.initCacheDomainRepository
					.getInventoryInfoByGoodsId(goodsId);
			//查询该商品分店库存信息
			selectionInventoryList = this.initCacheDomainRepository.querySelectionByGoodsId(goodsId);
			suppliersInventoryList =  this.initCacheDomainRepository.selectGoodsSuppliersInventoryByGoodsId(goodsId);
		}
	}
	public void init() {
		//保存商品库存
		if(inventoryDO!=null)
		      this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId, inventoryDO);
		//保选型库存
		if(!CollectionUtils.isEmpty(selectionInventoryList))
		      this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(goodsId, selectionInventoryList);
		//保存分店库存
		if(!CollectionUtils.isEmpty(suppliersInventoryList))
		      this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(goodsId, suppliersInventoryList);
	}
	*/
	//初始化库存
	public void initCheck() {
				this.fillParam();
				this.goodsId = Long.valueOf(id);
				InventoryInitDomain create = new InventoryInitDomain();
				//注入相关Repository
				create.setGoodsId(this.goodsId);
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setInitCacheDomainRepository(this.initCacheDomainRepository);
				create.setSynInitAndAsynUpdateDomainRepository(this.synInitAndAsynUpdateDomainRepository);
				create.busiCheck();
			}
			
	
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			updateActionDO.setBusinessType(businessType);
			updateActionDO.setItem(id);
			updateActionDO.setOriginalInventory(String
					.valueOf(originalWaterfloodVal));
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
					.setContent(JSONObject.fromObject(param).toString()); // 操作内容
			updateActionDO.setRemark("注水调整");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("fillInventoryUpdateActionDO")
					.addMetaData("errMsg", e.getMessage()), e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}
	private boolean verifyWaterflood() {
		if(resultACK>=0) {
			return true;
		}else {
			return false;
		}
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
		if (StringUtils.isEmpty(param.getId())) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getType())) {
			return CreateInventoryResultEnum.INVALID_PARAM;
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
			this.writeBusErrorLog(lm.setMethod("fillSelectionMsg")
					.addMetaData("errMsg", e.getMessage()), e);
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
			this.writeBusErrorLog(lm.setMethod("fillSuppliersMsg")
					.addMetaData("errMsg", e.getMessage()), e);
			this.suppliersMsg = null;
		}
		this.suppliersMsg = suppliersMsg;
	}
	
	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}

	public void setInitCacheDomainRepository(
			InitCacheDomainRepository initCacheDomainRepository) {
		this.initCacheDomainRepository = initCacheDomainRepository;
	}

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}
	
	public void setSynInitAndAsynUpdateDomainRepository(
			SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository) {
		this.synInitAndAsynUpdateDomainRepository = synInitAndAsynUpdateDomainRepository;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
