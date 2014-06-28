package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.DeleteInventoryParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;

public class InventoryDeleteDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private DeleteInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;

	private List<GoodsSelectionModel> selectionList;
	private List<GoodsSuppliersModel> suppliersList;

	private Long goodsId;
	private Long userId;
	private boolean doDelete;

	// private boolean doDelGoodsInventory;

	public InventoryDeleteDomain(String clientIp, String clientName,
			DeleteInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {

		try {
			goodsId = Long.valueOf(param.getGoodsId());
			// 判断商品库存是否已经存在
			boolean isExists = this.goodsInventoryDomainRepository
					.isExists(goodsId);
			// 填充商品库存信息
			if (!isExists) {
				this.doDelete = true;
			}

			if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // 选型库存
				this.selectionList = param.getGoodsSelection();
			}

			if (!CollectionUtils.isEmpty(param.getGoodsSuppliers())) { // 分店库存
				this.suppliersList = param.getGoodsSuppliers();
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
	public CreateInventoryResultEnum deleteInventory() {

		try {
			if (!doDelete) {
				return CreateInventoryResultEnum.IS_EXISTED;
			}
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			// 删除商品库存信息
			this.goodsInventoryDomainRepository.deleteGoodsInventory(goodsId);
			if (!CollectionUtils.isEmpty(selectionList)) {
				// 删除商品所属选型库存信息
				this.goodsInventoryDomainRepository
						.deleteSelectionInventory(selectionList);
			}
			if (!CollectionUtils.isEmpty(suppliersList)) {
				// 删除商品所属分店库存信息
				this.goodsInventoryDomainRepository
						.deleteSuppliersInventory(suppliersList);
			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("deleteInventory").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 发送库存新增消息
	public void sendNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
			goodsInventoryDomainRepository.sendNotifyServerMessage("",JSONObject
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
			// if(inventoryInfoDO!=null) {
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
			// }
			if (!CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtil
						.getIdsStringSelection(selectionList));
			}
			if (!CollectionUtils.isEmpty(suppliersList)) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription());
				updateActionDO.setItem(StringUtil
						.getIdsStringSuppliers(suppliersList));
			}
			updateActionDO.setActionType(ResultStatusEnum.DELETE_INVENTORY
					.getDescription());
			this.userId = (Long.valueOf(param.getUserId()));
			updateActionDO.setUserId(userId);
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setOrderId(0l);
			updateActionDO.setContent(JSONObject.fromObject(param).toString()); // 操作内容
			updateActionDO.setRemark("删除库存");
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

	public CreateInventoryResultEnum checkParam() {

		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getGoodsId())) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}

		return CreateInventoryResultEnum.SUCCESS;
	}

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
