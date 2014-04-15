package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
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

	// ҵ����
	public CreateInventoryResultEnum busiCheck() {

		try {
			goodsId = Long.valueOf(param.getGoodsId());
			// �ж���Ʒ����Ƿ��Ѿ�����
			boolean isExists = this.goodsInventoryDomainRepository
					.isExists(goodsId);
			// �����Ʒ�����Ϣ
			if (!isExists) {
				this.doDelete = true;
			}

			if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // ѡ�Ϳ��
				this.selectionList = param.getGoodsSelection();
			}

			if (!CollectionUtils.isEmpty(param.getGoodsSuppliers())) { // �ֵ���
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

	// ���ϵͳ�������
	public CreateInventoryResultEnum deleteInventory() {

		try {
			if (!doDelete) {
				return CreateInventoryResultEnum.IS_EXISTED;
			}
			// ���������־��Ϣ
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// ������־
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			// ɾ����Ʒ�����Ϣ
			this.goodsInventoryDomainRepository.deleteGoodsInventory(goodsId);
			if (!CollectionUtils.isEmpty(selectionList)) {
				// ɾ����Ʒ����ѡ�Ϳ����Ϣ
				this.goodsInventoryDomainRepository
						.deleteSelectionInventory(selectionList);
			}
			if (!CollectionUtils.isEmpty(suppliersList)) {
				// ɾ����Ʒ�����ֵ�����Ϣ
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

	// ���Ϳ��������Ϣ
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

	// ���notifyserver���Ͳ���
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() {
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		notifyParam.setUserId(this.userId);
		notifyParam.setGoodsId(goodsId);
		notifyParam.setLimitStorage(param.getLimitStorage());
		notifyParam.setWaterfloodVal(param.getWaterfloodVal());
		notifyParam.setTotalNumber(param.getTotalNumber());
		notifyParam.setLeftNumber(param.getLeftNumber());
		if (!CollectionUtils.isEmpty(selectionList)) {
			notifyParam.setSelectionRelation(selectionList);
		}
		if (!CollectionUtils.isEmpty(suppliersList)) {
			notifyParam.setSuppliersRelation(suppliersList);
		}
		return notifyParam;
	}

	// �����־��Ϣ
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
			updateActionDO.setContent(JSONObject.fromObject(param).toString()); // ��������
			updateActionDO.setRemark("ɾ�����");
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
