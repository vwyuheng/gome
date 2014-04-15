package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.InitCacheDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.UpdateInventoryParam;

public class InventoryUpdateDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private UpdateInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryQueueDO queueDO;
	private GoodsInventoryDO inventoryInfoDO;

	private List<GoodsSelectionModel> selectionList;
	private List<GoodsSuppliersModel> suppliersList;
	// ��ʼ����
	private List<GoodsSuppliersDO> suppliersInventoryList;
	private List<GoodsSelectionDO> selectionInventoryList;
	private Long goodsId;
	private Long userId;
	private boolean isEnough;
	private boolean isSelectionEnough = true;
	private boolean isSuppliersEnough = true;
	// �Ƿ���Ҫ��ʼ��
	private boolean isInit;
	// ��ۼ�����Ʒ���
	private int deductNum = 0;
	// ԭ���
	private int originalGoodsInventory = 0;
	// �����л���ѡ�ͺͷֵ�ԭʼ���Ϳۼ�����list
	private List<GoodsSelectionAndSuppliersResult> selectionParam;
	private List<GoodsSelectionAndSuppliersResult> suppliersParam;
	// ��ǰ���
	private long resultACK;
	private SequenceUtil sequenceUtil;

	public InventoryUpdateDomain(String clientIp, String clientName,
			UpdateInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	/**
	 * ����ѡ�Ϳ��
	 */
	private void selectionInventoryHandler() {
		if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // if1
			this.selectionList = param.getGoodsSelection();
			this.selectionParam = new ArrayList<GoodsSelectionAndSuppliersResult>();
			// this.selectionRelation = new
			// ArrayList<RedisGoodsSelectionRelationDO>();
			for (GoodsSelectionModel model : selectionList) { // for
				if (model.getId() != null && model.getId() > 0) { // ifѡ��
					GoodsSelectionAndSuppliersResult selection = null;
					Long selectionId = Long.valueOf(model.getId());
					// ��ѯ��Ʒѡ�Ϳ��
					GoodsSelectionDO selectionDO = this.goodsInventoryDomainRepository
							.querySelectionRelationById(selectionId);
					if (selectionDO != null
							&& selectionDO.getLimitStorage() == 1) {
						// �ۼ���沢���ؿۼ���ʶ,�����沢
						if ((selectionDO.getLeftNumber() - model.getNum()) <= 0) {
							// �ô�Ϊ�˱�ֻ֤Ҫ��һ��ѡ����Ʒ��治���򷵻ؿ�治��
							this.isSelectionEnough = false;
						} else {
							selection = new GoodsSelectionAndSuppliersResult();
							selection.setId(model.getId());
							// �ۼ��Ŀ����
							selection.setGoodsInventory(model.getNum());
							selection.setOriginalGoodsInventory(selectionDO
									.getLeftNumber());
							// ѡ�Ϳ�棬�����ǿ�����ʱ��
							this.selectionParam.add(selection);
							// ����selectionDO����Ŀ������ֵ
							selectionDO.setLeftNumber(selection
									.getOriginalGoodsInventory()
									- selection.getGoodsInventory());
							// this.selectionRelation.add(selectionDO);
						}

					}

				}// if

			}// for

		} // if selection
	}

	/**
	 * ����ֵ���
	 */
	public void suppliersInventoryHandler() {
		if (!CollectionUtils.isEmpty(param.getGoodsSuppliers())) { // if1
			this.suppliersList = param.getGoodsSuppliers();
			this.suppliersParam = new ArrayList<GoodsSelectionAndSuppliersResult>();
			for (GoodsSuppliersModel model : suppliersList) { // for
				if (model.getId() > 0) { // if�ֵ�
					GoodsSelectionAndSuppliersResult suppliers = null;
					Long suppliersId = Long.valueOf(model.getId());
					GoodsSuppliersDO suppliersDO = this.goodsInventoryDomainRepository
							.querySuppliersInventoryById(suppliersId);

					if (suppliersDO != null
							&& suppliersDO.getLimitStorage() == 1) {
						// �ۼ���沢���ؿۼ���ʶ,�����沢
						if ((suppliersDO.getLeftNumber() - model.getNum()) <= 0) {
							// �ô�Ϊ�˱�ֻ֤Ҫ��һ��ѡ����Ʒ��治���򷵻ؿ�治��
							this.isSuppliersEnough = false;

						} else {
							suppliers = new GoodsSelectionAndSuppliersResult();
							// �ۼ��Ŀ����
							suppliers.setId(model.getId());
							suppliers.setGoodsInventory(model.getNum());
							suppliers.setOriginalGoodsInventory(suppliersDO
									.getLeftNumber());
							this.suppliersParam.add(suppliers);
							// ����selectionDO����Ŀ������ֵ
							suppliersDO.setLeftNumber(suppliers
									.getOriginalGoodsInventory()
									- suppliers.getGoodsInventory());
						}

					}

				}// if
			}
		}
	}

	private void calculateInventory() {
		// �ٴβ�ѯ��Ʒ�����Ϣ[ȷ����������]
		this.inventoryInfoDO = this.goodsInventoryDomainRepository
				.queryGoodsInventory(goodsId);
		// �ۼ����
		this.deductNum = param.getNum();
		// ԭʼ���
		this.originalGoodsInventory = inventoryInfoDO.getLeftNumber();
		// �ۼ���沢���ؿۼ���ʶ,�����沢
		if ((this.originalGoodsInventory - this.deductNum) >= 0) {
			this.isEnough = true;
			// ����inventoryInfoDO����Ŀ������ֵ
			this.inventoryInfoDO.setLeftNumber(originalGoodsInventory
					- deductNum);
		}
	}

	private boolean verifyInventory() {
		if (resultACK >= 0) {
			return true;
		} else {
			return false;
		}
	}

	// ҵ����
	public CreateInventoryResultEnum busiCheck() {
		// ��ʼ�����
		this.initCheck();
		if (isInit) {
			this.init();
		}
		// �����Ŀ�����ҵ����
		try {// ���㲿��
			this.calculateInventory();
			// ��Ʒѡ�ʹ���
			this.selectionInventoryHandler();
			// ��Ʒ�ֵ괦��
			this.suppliersInventoryHandler();
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}

		return CreateInventoryResultEnum.SUCCESS;
	}

	// ���ϵͳ�������
	public CreateInventoryResultEnum updateInventory() {
		try {
			// ���������־��Ϣ
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// ������־
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			// ������Ʒ���
			if (isEnough) {
				// �ۼ����
				resultACK = this.goodsInventoryDomainRepository
						.updateGoodsInventory(goodsId, (-deductNum));
				// У����
				if (!verifyInventory()) {
					// �ع����
					this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId, (deductNum));
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			}
			// ����ѡ�Ϳ��
			if (isSelectionEnough) {
				resultACK = this.goodsInventoryDomainRepository
						.updateSelectionInventory(selectionParam);
				// У����
				if (!verifyInventory()) {
					// �ع����
					// �Ȼع��ܵ� �ٻع�ѡ�͵�
					this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId, (deductNum));
					this.goodsInventoryDomainRepository
							.rollbackSelectionInventory(selectionParam);
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			}
			// ���·ֵ���
			if (isSuppliersEnough) {
				resultACK = this.goodsInventoryDomainRepository
						.updateSuppliersInventory(suppliersParam);
				// У����
				if (!verifyInventory()) {
					// �ع����
					// �Ȼع��ܵ� �ٻع��ֵ��
					this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId, (deductNum));
					this.goodsInventoryDomainRepository
							.rollbackSuppliersInventory(suppliersParam);
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("createInventory").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	public void pushSendMsgQueue() {
		// ������
		if (fillInventoryQueueDO()) {
			this.goodsInventoryDomainRepository.pushQueueSendMsg(queueDO);
		}
	}

	// ��ʼ�����
	public void initCheck() {
		this.goodsId = Long.valueOf(param.getGoodsId());
		if (goodsId > 0 && param.getLimitStorage() == 1) { // limitStorage>0:��������ƣ�1�����ƿ��
			boolean isExists = this.goodsInventoryDomainRepository
					.isGoodsExists(goodsId);
			if (isExists) { // ������
				// ��ʼ�����
				this.isInit = true;
				// ��ʼ����Ʒ�����Ϣ
				this.inventoryInfoDO = this.initCacheDomainRepository
						.getInventoryInfoByGoodsId(goodsId);
				// ��ѯ����Ʒ�ֵ�����Ϣ
				selectionInventoryList = this.initCacheDomainRepository
						.querySelectionByGoodsId(goodsId);
				suppliersInventoryList = this.initCacheDomainRepository
						.selectGoodsSuppliersInventoryByGoodsId(goodsId);
			}
		}

	}

	public void init() {
		// ������Ʒ���
		if (inventoryInfoDO != null)
			this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
					inventoryInfoDO);
		// ��ѡ�Ϳ��
		if (!CollectionUtils.isEmpty(selectionInventoryList))
			this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(
					goodsId, selectionInventoryList);
		// ����ֵ���
		if (!CollectionUtils.isEmpty(suppliersInventoryList))
			this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
					goodsId, suppliersInventoryList);
	}

	// �����־��Ϣ
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			if (inventoryInfoDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SELF
						.getDescription());
				updateActionDO.setOriginalInventory(String
						.valueOf(originalGoodsInventory));
				updateActionDO.setInventoryChange(String
						.valueOf(deductNum));
			}
			if (!CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtil
						.getIdsStringSelection(selectionList));
				updateActionDO.setOriginalInventory(JSONObject.fromObject(
						selectionParam).toString());
				updateActionDO.setInventoryChange(JSONObject.fromObject(
						selectionParam).toString());
			}
			if (!CollectionUtils.isEmpty(suppliersList)) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription());
				updateActionDO.setItem(StringUtil
						.getIdsStringSuppliers(suppliersList));
				updateActionDO.setOriginalInventory(JSONObject.fromObject(
						suppliersParam).toString());
				updateActionDO.setInventoryChange(JSONObject.fromObject(
						suppliersParam).toString());
			}
			updateActionDO.setActionType(ResultStatusEnum.DEDUCTION_INVENTORY
					.getDescription());
			this.userId = (Long.valueOf(param.getUserId()));
			updateActionDO.setUserId(userId);
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setOrderId(Long.valueOf(param.getOrderId()));
			updateActionDO.setContent(JSONObject.fromObject(param).toString()); // ��������
			updateActionDO.setRemark("�޸Ŀ��");
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

	/**
	 * ����������Ϣ
	 * 
	 * @return
	 */
	public boolean fillInventoryQueueDO() {
		GoodsInventoryQueueDO queueDO = new GoodsInventoryQueueDO();
		try {
			queueDO.setId(sequenceUtil.getSequence(SEQNAME.seq_queue_send));
			queueDO.setGoodsId(goodsId);
			queueDO.setOrderId(Long.valueOf(param.getOrderId()));
			queueDO.setUserId(Long.valueOf(param.getUserId()));
			queueDO.setCreateTime(TimeUtil.getNowTimestamp10Long());
			// ��װ���仯��Ϣ������
			queueDO.setOriginalGoodsInventory(originalGoodsInventory);
			queueDO.setDeductNum(deductNum);
			queueDO.setSuppliersParam(suppliersParam);
			queueDO.setSelectionParam(selectionParam);

		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("fillInventoryQueueDO")
					.addMetaData("errMsg", e.getMessage()), e);
			this.queueDO = null;
			return false;
		}
		this.queueDO = queueDO;
		return true;
	}

	/**
	 * �������
	 * 
	 * @return
	 */
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getGoodsId())) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}
		if (StringUtils.isEmpty(param.getOrderId())) {
			return CreateInventoryResultEnum.INVALID_ORDER_ID;
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

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
