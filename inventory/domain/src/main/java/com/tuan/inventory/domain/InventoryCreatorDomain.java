package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.CreatorInventoryParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;

public class InventoryCreatorDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private CreatorInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryInfoDO;
	private List<GoodsSelectionDO> selectionRelation;
	private List<GoodsSuppliersDO> suppliersRelation;

	private List<GoodsSelectionModel> selectionList;
	private List<GoodsSuppliersModel> suppliersList;

	private Long goodsId;
	private Long userId;
	// ��Ʒ����Ƿ����
	boolean isExists = false;
	// ����ѡ��
	boolean addSelection = false;
	// �����ֵ�
	boolean addSuppliers = false;

	public InventoryCreatorDomain(String clientIp, String clientName,
			CreatorInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	/***
	 * ҵ����ǰ��Ԥ����
	 */
	public void preHandler() {

		this.goodsId = Long.valueOf(param.getGoodsId());
		// ��Ʒ����Ƿ����
		isExists = this.goodsInventoryDomainRepository.isExists(goodsId);
		if (isExists) { // ������
			// ���ݽӿڲ��������Ʒ�����Ϣ
			this.fillRedisInventoryDO();
			// �����Ʒѡ�Ϳ���Ϣ
			this.fillSelection();
			// �����Ʒ�ֵ�����Ϣ
			this.fillSuppliers();
		} else { // ��Ʒ�Ѵ���
			if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // ѡ�Ϳ��
				addSelection = true;
			}
			if (!CollectionUtils.isEmpty(param.getGoodsSuppliers())) { // �ֵ���
				addSuppliers = true;
			}
		}
	}

	// ҵ����
	public CreateInventoryResultEnum busiCheck() {

		try {
			// ҵ����ǰ��Ԥ����
			this.preHandler();

			if (addSelection) { // ѡ�Ϳ��
				// �����Ʒѡ�Ϳ�����Ϣ
				this.fillSelection();

			}
			if (addSuppliers) { // �ֵ���
				// �����Ʒ�ֵ�����Ϣ
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

	// �������
	public CreateInventoryResultEnum createInventory() {
		try {
			// ���������־��Ϣ
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// ������־
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			// ������Ʒ���
			if (isExists && inventoryInfoDO != null)
				this.goodsInventoryDomainRepository.saveGoodsInventory(
						goodsId, inventoryInfoDO);
			// ��ѡ�Ϳ��
			if (!CollectionUtils.isEmpty(selectionRelation))
				this.goodsInventoryDomainRepository
						.saveGoodsSelectionInventory(goodsId, selectionRelation);
			// ����ֵ���
			if (!CollectionUtils.isEmpty(suppliersRelation))
				this.goodsInventoryDomainRepository
						.saveGoodsSuppliersInventory(goodsId, suppliersRelation);

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("createInventory").addMetaData("errorMsg",
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
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtil
						.getIdsStringSelection(selectionList));
			}
			if (addSuppliers && !CollectionUtils.isEmpty(suppliersList)) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription());
				updateActionDO.setItem(StringUtil
						.getIdsStringSuppliers(suppliersList));
			}
			updateActionDO.setActionType(ResultStatusEnum.ADD_INVENTORY
					.getDescription());
			this.userId = (Long.valueOf(param.getUserId()));
			updateActionDO.setUserId(userId);
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setOrderId(0l);
			updateActionDO.setContent(JSONObject.fromObject(param).toString()); // ��������
			updateActionDO.setRemark("�������");
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

	// �������
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getGoodsId())) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}

		return CreateInventoryResultEnum.SUCCESS;
	}

	// ��Ʒѡ�Ϳ��
	public void fillSelection() {
		try {
			selectionList = param.getGoodsSelection();
			if (!CollectionUtils.isEmpty(selectionList)) {
				selectionRelation = new ArrayList<GoodsSelectionDO>();
				for (GoodsSelectionModel model : selectionList) {
					if (model.getId() != null && model.getId() > 0) {
						GoodsSelectionDO selection = new GoodsSelectionDO();
						selection.setGoodsId(goodsId);
						selection.setId(sequenceUtil
								.getSequence(SEQNAME.seq_selection));
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

	// ��Ʒѡ�Ϳ��
	public void fillSuppliers() {
		try {
			suppliersList = param.getGoodsSuppliers();
			if (!CollectionUtils.isEmpty(suppliersList)) {
				suppliersRelation = new ArrayList<GoodsSuppliersDO>();
				for (GoodsSuppliersModel model : suppliersList) {
					if (model.getId() > 0) {
						GoodsSuppliersDO suppliers = new GoodsSuppliersDO();
						suppliers.setGoodsId(goodsId);
						suppliers.setId(sequenceUtil
								.getSequence(SEQNAME.seq_suppliers));
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
	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
