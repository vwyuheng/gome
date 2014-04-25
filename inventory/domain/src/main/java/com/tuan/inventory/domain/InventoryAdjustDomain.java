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
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.SelectionNotifyMessageParam;
import com.tuan.inventory.model.param.SuppliersNotifyMessageParam;

public class InventoryAdjustDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private AdjustInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	//private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryDO;
	private GoodsSelectionDO selectionInventory;
	private GoodsSuppliersDO suppliersInventory;
	//选型
	private List<SelectionNotifyMessageParam> selectionMsg;
	//分店
	private List<SuppliersNotifyMessageParam> suppliersMsg;
	
	private String type;
	private String id;
	private int adjustNum;
	private String businessType;
	// 原库存
	private int originalGoodsInventory = 0;
	private Long goodsId;
	private long selectionId;
	private long suppliersId;
	//调整后库存
	private Long resultACK;
	//是否需要初始化
	//private boolean isInit;
	//初始化用
	//private List<GoodsSuppliersDO> suppliersInventoryList;
	//private List<GoodsSelectionDO> selectionInventoryList;
	
	public InventoryAdjustDomain(String clientIp, String clientName,
			AdjustInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		try {
			//初始化检查
			this.initCheck();
			/*this.initCheck();
			if (isInit) {
				this.init();
			}*/
			//真正的库存调整业务处理
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF.getDescription();
				//查询商品库存
				this.inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if(inventoryDO!=null) {
					this.originalGoodsInventory = inventoryDO.getLeftNumber();
				}
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION.getDescription();
				//查询商品选型库存
				this.selectionInventory = this.goodsInventoryDomainRepository.querySelectionRelationById(selectionId);
				if(selectionInventory!=null) {
					this.originalGoodsInventory = selectionInventory.getLeftNumber();
				}
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.suppliersId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SUPPLIERS.getDescription();
				//查询商品分店库存
				this.suppliersInventory = this.goodsInventoryDomainRepository.querySuppliersInventoryById(suppliersId);
				if(suppliersInventory!=null) {
					this.originalGoodsInventory = suppliersInventory.getLeftNumber();
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

	// 库存系统新增库存
	public CreateInventoryResultEnum adjustInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);

			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				//更新mysql
				boolean handlerResult = inventoryInitAndUpdateHandle.updateGoodsInventory(inventoryDO);
				if(handlerResult) {
					this.resultACK = this.goodsInventoryDomainRepository.updateGoodsInventory(goodsId, (adjustNum));
					if(!verifyInventory()) {
						//将库存还原到调整前
						this.goodsInventoryDomainRepository.updateGoodsInventory(goodsId, (-adjustNum));
						return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
					}
				}
				//this.synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryDO);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				
				//更新mysql
				boolean handlerResult = inventoryInitAndUpdateHandle.updateGoodsSelection(selectionInventory);
				if(handlerResult) {
					this.resultACK = this.goodsInventoryDomainRepository.updateSelectionInventoryById(selectionId, (adjustNum));
					if(!verifyInventory()) {
						//将库存还原到调整前
						this.goodsInventoryDomainRepository.updateSelectionInventoryById(selectionId, (-adjustNum));
						return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
					}
				}
				
				
				//更新选型的mysql
				//this.synInitAndAsynUpdateDomainRepository.updateGoodsSelection(selectionInventory);
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				
				//更新mysql
				boolean handlerResult = inventoryInitAndUpdateHandle.updateGoodsSuppliers(suppliersInventory);
				if(handlerResult) {
					this.resultACK = this.goodsInventoryDomainRepository.updateSuppliersInventoryById(suppliersId, (adjustNum));
					if(!verifyInventory()) {
						//将库存还原到调整前
						this.goodsInventoryDomainRepository.updateSuppliersInventoryById(suppliersId, (-adjustNum));
						return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
					}
				}
				
				
				//更新分店的mysql
				//this.synInitAndAsynUpdateDomainRepository.updateGoodsSuppliers(suppliersInventory);
			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("createInventory").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
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
				writeBusErrorLog(lm.addMetaData("errMsg", e.getMessage()), e);
			}
		}
		// 初始化参数
		private void fillParam() {
			// 2:商品 4：选型 6：分店
			this.type = param.getType();
			// 2：商品id 4：选型id 6 分店id
			this.id = param.getId();
			this.adjustNum = param.getNum();
			
		}
		//填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(){
			InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
			notifyParam.setUserId(Long.valueOf(param.getUserId()));
			notifyParam.setGoodsId(goodsId);
			if(inventoryDO!=null) {
				notifyParam.setLimitStorage(inventoryDO.getLimitStorage());
				notifyParam.setWaterfloodVal(inventoryDO.getWaterfloodVal());
				notifyParam.setTotalNumber((inventoryDO.getTotalNumber()+adjustNum));
				notifyParam.setLeftNumber(resultACK.intValue());
				//库存总数 减 库存剩余
				int sales = (inventoryDO.getTotalNumber()+adjustNum)-resultACK.intValue();
				//销量
				notifyParam.setSales(String.valueOf(sales));
			}
			if(!CollectionUtils.isEmpty(selectionMsg)){
				this.fillSelectionMsg();
				notifyParam.setSelectionRelation(selectionMsg);
			}
			if(!CollectionUtils.isEmpty(suppliersMsg)){
				this.fillSuppliersMsg();
				notifyParam.setSuppliersRelation(suppliersMsg);
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
		}*/
		/*public void init() {
			//保存商品库存
			if(inventoryDO!=null)
			      this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId, inventoryDO);
			//保选型库存
			if(!CollectionUtils.isEmpty(selectionInventoryList))
			      this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(goodsId, selectionInventoryList);
			//保存分店库存
			if(!CollectionUtils.isEmpty(suppliersInventoryList))
			      this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(goodsId, suppliersInventoryList);
		}*/
		//初始化库存
		public void initCheck() {
			this.fillParam();
			this.goodsId = Long.valueOf(id);
			InventoryInitDomain create = new InventoryInitDomain();
			//注入相关Repository
			create.setGoodsId(this.goodsId);
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setInitCacheDomainRepository(this.initCacheDomainRepository);
			create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
			//create.setSynInitAndAsynUpdateDomainRepository(this.synInitAndAsynUpdateDomainRepository);
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
					.valueOf(originalGoodsInventory));
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
			updateActionDO.setRemark("回调确认");
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
	private boolean verifyInventory() {
		if(resultACK>=0) {
			return true;
		}else {
			return false;
		}
	}
	
	public void fillSelectionMsg() {
		List<SelectionNotifyMessageParam> selectionMsg = new ArrayList<SelectionNotifyMessageParam>();
		SelectionNotifyMessageParam selMsg = new SelectionNotifyMessageParam();
		try {
			//selMsg.setGoodTypeId(selectionInventory.getGoodTypeId());
			selMsg.setGoodsId(goodsId);
			selMsg.setId(selectionId);
			selMsg.setLeftNumber(resultACK.intValue());  //调整后的库存值
			selMsg.setTotalNumber((selectionInventory.getTotalNumber()+adjustNum));
			selMsg.setUserId(Long.valueOf(param.getUserId()));
			selMsg.setLimitStorage(selectionInventory.getLimitStorage());
			selMsg.setWaterfloodVal(selectionInventory.getWaterfloodVal());
			int sales = selMsg.getTotalNumber()-selMsg.getLeftNumber();
			selMsg.setSales(String.valueOf(sales));
			selectionMsg.add(selMsg);
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("fillSelectionMsg")
					.addMetaData("errMsg", e.getMessage()), e);
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
			supMsg.setLeftNumber(resultACK.intValue());  //调整后的库存值
			supMsg.setTotalNumber((suppliersInventory.getTotalNumber()+adjustNum));
			supMsg.setUserId(Long.valueOf(param.getUserId()));
			supMsg.setLimitStorage(suppliersInventory.getLimitStorage());
			supMsg.setWaterfloodVal(suppliersInventory.getWaterfloodVal());
			int sales = supMsg.getTotalNumber()-supMsg.getLeftNumber();
			supMsg.setSales(String.valueOf(sales));
			suppliersMsg.add(supMsg);
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("fillSuppliersMsg")
					.addMetaData("errMsg", e.getMessage()), e);
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
		if (StringUtils.isEmpty(param.getId())) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getType())) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	public void setInitCacheDomainRepository(
			InitCacheDomainRepository initCacheDomainRepository) {
		this.initCacheDomainRepository = initCacheDomainRepository;
	}

	/*public void setSynInitAndAsynUpdateDomainRepository(
			SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository) {
		this.synInitAndAsynUpdateDomainRepository = synInitAndAsynUpdateDomainRepository;
	}*/
	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public void setInventoryInitAndUpdateHandle(
			InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle) {
		this.inventoryInitAndUpdateHandle = inventoryInitAndUpdateHandle;
	}
	public Long getGoodsId() {
		return goodsId;
	}

}
