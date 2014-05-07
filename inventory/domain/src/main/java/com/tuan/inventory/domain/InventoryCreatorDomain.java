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
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.CreaterInventoryParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;

public class InventoryCreatorDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private CreaterInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryInfoDO;
	private List<GoodsSelectionDO> selectionRelation;
	private List<GoodsSuppliersDO> suppliersRelation;

	private List<GoodsSelectionModel> selectionList;
	private List<GoodsSuppliersModel> suppliersList;

	private Long goodsId;
	private Long userId;
	// 商品库存是否存在
	boolean isExists = false;
	// 新增选型
	boolean addSelection = false;
	// 新增分店
	boolean addSuppliers = false;

	public InventoryCreatorDomain(String clientIp, String clientName,
			CreaterInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	/***
	 * 业务处理前的预处理
	 */
	public void preHandler() {

		this.goodsId = Long.valueOf(param.getGoodsId());
		// 商品库存是否存在
		isExists = this.goodsInventoryDomainRepository.isExists(goodsId);
		if (isExists) { // 不存在
			// 根据接口参数填充商品库存信息
			this.fillRedisInventoryDO();
			// 填充商品选型库信息
			this.fillSelection();
			// 填充商品分店库存信息
			this.fillSuppliers();
		} else { // 商品已存在
			if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // 选型库存
				addSelection = true;
			}
			if (!CollectionUtils.isEmpty(param.getGoodsSuppliers())) { // 分店库存
				addSuppliers = true;
			}
		}
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {

		try {
			// 业务检查前的预处理
			this.preHandler();

			if (addSelection) { // 选型库存
				// 填充商品选型库库存信息
				this.fillSelection();

			}
			if (addSuppliers) { // 分店库存
				// 填充商品分店库存信息
				this.fillSuppliers();

			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		if (!isExists && !addSelection && !addSuppliers) {
			return CreateInventoryResultEnum.IS_EXISTED;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 新增库存
	public CreateInventoryResultEnum createInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("createInventory").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		//保存库存
		return this.saveInventory();
	}

	public CreateInventoryResultEnum saveInventory() {
		InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
		//注入相关Repository
		create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
		create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
		return create.createInventory(inventoryInfoDO, selectionRelation, suppliersRelation);
	}
	// 发送库存新增消息
	public void sendNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
			goodsInventoryDomainRepository.sendNotifyServerMessage(JSONObject
					.fromObject(notifyParam));
			/*
			 * Type orderParamType = new
			 * TypeToken<NotifyCardOrder4ShopCenterParam>(){}.getType(); String
			 * paramJson = new Gson().toJson(notifyParam, orderParamType);
			 * extensionService.sendNotifyServer(paramJson, lm.getTraceId());
			 */
		} catch (Exception e) {
			writeBusErrorLog(lm.addMetaData("errMsg", e.getMessage()), e);
		}
	}

	// 填充notifyserver发送参数
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() {
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		notifyParam.setUserId(this.userId);
		notifyParam.setGoodsId(goodsId);
		notifyParam.setLimitStorage(param.getLimitStorage());
		notifyParam.setWaterfloodVal(param.getWaterfloodVal());
		notifyParam.setTotalNumber(param.getTotalNumber());
		notifyParam.setLeftNumber(param.getLeftNumber());
		//库存总数 减 库存剩余
		int sales = param.getTotalNumber()-param.getLeftNumber();
		//销量
		notifyParam.setSales(String.valueOf(sales));
		
		if (!CollectionUtils.isEmpty(selectionList)) {
			notifyParam.setSelectionRelation(ObjectUtils.toSelectionMsgList(selectionList));
		}
		if (!CollectionUtils.isEmpty(suppliersList)) {
			notifyParam.setSuppliersRelation(ObjectUtils.toSuppliersMsgList(suppliersList));
		}
		return notifyParam;
	}

	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			if (isExists && inventoryInfoDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SELF
						.getDescription());
				updateActionDO
						.setOriginalInventory(param.getLimitStorage() == 1 ? String
								.valueOf(param.getLeftNumber()) : String
								.valueOf(Integer.MAX_VALUE));
				updateActionDO
						.setInventoryChange(param.getLimitStorage() == 1 ? String
								.valueOf(param.getLeftNumber()) : String
								.valueOf(Integer.MAX_VALUE));
			}
			if (addSelection && !CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_SELECTION
						.getDescription():updateActionDO.getBusinessType()+",选型："+ResultStatusEnum.GOODS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSelection(selectionList):updateActionDO.getItem()+",选型item："+StringUtil
						.getIdsStringSelection(selectionList));
			}
			if (addSuppliers && !CollectionUtils.isEmpty(suppliersList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription():updateActionDO.getBusinessType()+",分店："+ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSuppliers(suppliersList):updateActionDO.getItem()+",分店item："+StringUtil
						.getIdsStringSuppliers(suppliersList));
			}
			updateActionDO.setActionType(ResultStatusEnum.ADD_INVENTORY
					.getDescription());
			if(!StringUtils.isEmpty(param.getUserId())) {
				this.userId = (Long.valueOf(param.getUserId()));
			}
			updateActionDO.setUserId(userId);
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setOrderId(0l);
			updateActionDO.setContent(JSONObject.fromObject(param).toString()); // 操作内容
			updateActionDO.setRemark("新增库存");
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

	// 参数检查
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getGoodsId())) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}

		return CreateInventoryResultEnum.SUCCESS;
	}

	// 商品选型库存
	public void fillSelection() {
		try {
			selectionList = param.getGoodsSelection();
			if (!CollectionUtils.isEmpty(selectionList)) {
				selectionRelation = new ArrayList<GoodsSelectionDO>();
				for (GoodsSelectionModel model : selectionList) {
					if (model.getId() != null && model.getId() > 0) {
						GoodsSelectionDO selection = new GoodsSelectionDO();
						selection.setGoodsId(goodsId);
						selection.setId(model.getId());
						selection.setLeftNumber(model.getLeftNumber());
						selection.setTotalNumber(model.getTotalNumber());
						selection.setLimitStorage(model.getLimitStorage());
						selection.setWaterfloodVal(model.getWaterfloodVal());
						selection.setGoodTypeId(model.getGoodTypeId());
						selection.setSuppliersInventoryId(model
								.getSuppliersInventoryId());
						selection.setUserId(userId);
						selectionRelation.add(selection);
					}

				}

			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("fillSelectionAndSuppliersModel").addMetaData(
							"errMsg", e.getMessage()), e);
			this.selectionRelation = null;
		}
	}

	// 商品选型库存
	public void fillSuppliers() {
		try {
			suppliersList = param.getGoodsSuppliers();
			if (!CollectionUtils.isEmpty(suppliersList)) {
				suppliersRelation = new ArrayList<GoodsSuppliersDO>();
				for (GoodsSuppliersModel model : suppliersList) {
					if (model.getSuppliersId() > 0) {
						GoodsSuppliersDO suppliers = new GoodsSuppliersDO();
						suppliers.setGoodsId(goodsId);
						//主键自己生成
						suppliers.setId(sequenceUtil
								.getSequence(SEQNAME.seq_suppliers));
						//suppliers.setId(model.getId());
						suppliers.setLeftNumber(model.getLeftNumber());
						suppliers.setTotalNumber(model.getTotalNumber());
						suppliers.setLimitStorage(model.getLimitStorage());
						suppliers.setWaterfloodVal(model.getWaterfloodVal());
						suppliers.setSuppliersId(model.getSuppliersId());
						suppliers.setUserId(userId);
						suppliersRelation.add(suppliers);
					}

				}

			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("fillSuppliers").addMetaData("errMsg",
							e.getMessage()), e);
			this.suppliersRelation = null;
		}
	}

	public void fillRedisInventoryDO() {
		GoodsInventoryDO inventoryInfoDO = new GoodsInventoryDO();
		try {
			inventoryInfoDO.setGoodsId(goodsId);
			inventoryInfoDO.setLeftNumber(param.getLeftNumber());
			inventoryInfoDO.setTotalNumber(param.getTotalNumber());
			inventoryInfoDO.setLimitStorage(param.getLimitStorage());
			inventoryInfoDO.setUserId(userId);
			inventoryInfoDO.setWaterfloodVal(param.getWaterfloodVal());

		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("fillCardOrderInfoDO")
					.addMetaData("errMsg", e.getMessage()), e);
			this.inventoryInfoDO = null;
		}
		this.inventoryInfoDO = inventoryInfoDO;
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

	public Long getGoodsId() {
		return goodsId;
	}

}
