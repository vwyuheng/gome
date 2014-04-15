package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.CallbackParam;

public class InventoryCallbackDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private CallbackParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryQueueDO queueDO;
	private String ack;
	private String key;
	private int upStatusNum;
	//��ۼ�����Ʒ�����
	private int deductNum  = 0;
	// ԭ���
	private int originalGoodsInventory = 0;
	// �����л���ѡ�ͺͷֵ�ԭʼ���Ϳۼ�����list
	private List<GoodsSelectionAndSuppliersResult> selectionParam;
	private List<GoodsSelectionAndSuppliersResult> suppliersParam;
	private Long goodsId;
	private SequenceUtil sequenceUtil;
	private boolean isConfirm;
	private boolean isRollback;
	
	public InventoryCallbackDomain(String clientIp, String clientName,
			CallbackParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	// ��ʼ������
	private void fillParam() {
		// ack:ȷ����
		this.ack = param.getAck();
		// key
		this.key = param.getKey();

	}
	public void preHandler() {
		// ȷ��
		if (this.ack.equalsIgnoreCase(ResultStatusEnum.CONFIRM.getCode())) {
			this.isConfirm = true;
			// ������״̬�ɳ�ʼ����״̬3������Ϊȷ��״̬��1
			this.upStatusNum = -2;
		}
		//�ع�
		if (ack.equalsIgnoreCase(ResultStatusEnum.ROLLBACK.getCode())) {
			this.isRollback = true;
			// ���÷�����״������״̬�ɳ�ʼ״̬��������3����Ϊɾ��:7
			this.upStatusNum = 4;
		}
	}

	// ҵ����
	public CreateInventoryResultEnum busiCheck() {

		try {
			// ������
			this.fillParam();
			//Ԥ����
			this.preHandler();
			if (isRollback) {
				// ����key��ѯ����Ķ�����Ϣ
				this.queueDO = this.goodsInventoryDomainRepository
						.queryInventoryQueueDO(key);
				if (this.queueDO != null) {
					this.goodsId = queueDO.getGoodsId();
					this.deductNum = queueDO.getDeductNum();
					this.originalGoodsInventory = queueDO
							.getOriginalGoodsInventory();
					this.selectionParam = queueDO.getSelectionParam();
					this.suppliersParam = queueDO.getSuppliersParam();

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

	// �ص�ȷ��
	public CreateInventoryResultEnum ackInventory() {
		try {
			// ���������־��Ϣ
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// ������־
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			//ȷ��
			if (isConfirm) {
				this.goodsInventoryDomainRepository.markQueueStatus(key,
						(upStatusNum));
			}
			//�ع�
			if (isRollback) {
				// �ع����
				if (goodsId > 0) {
					this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId, (deductNum));
				}
				if(!CollectionUtils.isEmpty(selectionParam)) {
					 this.goodsInventoryDomainRepository
						.rollbackSelectionInventory(selectionParam);
				}
				if(!CollectionUtils.isEmpty(suppliersParam)) {
					this.goodsInventoryDomainRepository
					.rollbackSuppliersInventory(suppliersParam);
				}
				// �����б��ɾ��
				this.goodsInventoryDomainRepository.markQueueStatus(key,
						(upStatusNum));
			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("createInventory").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// �����־��Ϣ
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			updateActionDO.setBusinessType("");
			updateActionDO.setOriginalInventory(String
					.valueOf(originalGoodsInventory));
			updateActionDO.setInventoryChange(String.valueOf(deductNum));
			updateActionDO.setActionType(ResultStatusEnum.CALLBACK_CONFIRM
					.getDescription());
			updateActionDO.setUserId(queueDO.getUserId());
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setOrderId(queueDO.getOrderId());
			updateActionDO
					.setContent(JSONObject.fromObject(queueDO).toString()); // ��������
			updateActionDO.setRemark("�ص�ȷ��");
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
	 * �������
	 * 
	 * @return
	 */
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getAck())) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getKey())) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	public GoodsInventoryDomainRepository getGoodsInventoryDomainRepository() {
		return goodsInventoryDomainRepository;
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
