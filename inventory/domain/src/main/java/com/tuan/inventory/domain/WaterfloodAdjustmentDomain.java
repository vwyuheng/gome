package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.domain.repository.InitCacheDomainRepository;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;

public class WaterfloodAdjustmentDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private AdjustWaterfloodParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryDO;
	private GoodsSelectionDO selectionInventory;
	private GoodsSuppliersDO suppliersInventory;
	//��ʼ����
	private List<GoodsSuppliersDO> suppliersInventoryList;
	private List<GoodsSelectionDO> selectionInventoryList;
	private String type;
	private String id;
	//עˮ����ֵ
	private int adjustNum;
	private String businessType;
	// ԭעˮ
	private int originalWaterfloodVal = 0;
	private Long goodsId;
	private Long selectionId;
	private Long suppliersId;
	//��������
	private long resultACK;
	//�Ƿ���Ҫ��ʼ��
	private boolean isInit;
	
	public WaterfloodAdjustmentDomain(String clientIp, String clientName,
			AdjustWaterfloodParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	// ҵ����
	public CreateInventoryResultEnum busiCheck() {
		try {
			// ��ʼ�����
			this.initCheck();
			if (isInit) {
				this.init();
			}
			// ������ҵ���鴦��
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
				// ��ѯ��Ʒѡ�Ϳ��
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
				// ��ѯ��Ʒ�ֵ���
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

	// ����עˮ
	public CreateInventoryResultEnum adjustWaterfloodVal() {
		try {
			// ���������־��Ϣ
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// ������־
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);

			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.adjustGoodsWaterflood(goodsId, (adjustNum));
				if(!verifyWaterflood()) {
					//��עˮ��ԭ������ǰ
					this.goodsInventoryDomainRepository.adjustGoodsWaterflood(goodsId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.adjustSelectionWaterfloodById(selectionId, (adjustNum));
				if(!verifyWaterflood()) {
					//��עˮ��ԭ������ǰ
					this.goodsInventoryDomainRepository.adjustSelectionWaterfloodById(selectionId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.adjustSuppliersWaterfloodById(suppliersId, (adjustNum));
				if(!verifyWaterflood()) {
					//��עˮ��ԭ������ǰ
					this.goodsInventoryDomainRepository.adjustSuppliersWaterfloodById(suppliersId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("adjustWaterfloodVal").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	// ��ʼ������
	private void fillParam() {
		// 2:��Ʒ 4��ѡ�� 6���ֵ�
		this.type = param.getType();
		// 2����Ʒid 4��ѡ��id 6 �ֵ�id
		this.id = param.getId();
		this.adjustNum = param.getNum();
		
	}
	//��ʼ�����
	public void initCheck() {
		this.fillParam();
		this.goodsId = Long.valueOf(id);
		//��ѯ��Ʒ���
		this.inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
		if(inventoryDO==null) {
			//��ʼ�����
			this.isInit = true;
			//��ʼ����Ʒ�����Ϣ
			this.inventoryDO = this.initCacheDomainRepository
					.getInventoryInfoByGoodsId(goodsId);
			//��ѯ����Ʒ�ֵ�����Ϣ
			selectionInventoryList = this.initCacheDomainRepository.querySelectionByGoodsId(goodsId);
			suppliersInventoryList =  this.initCacheDomainRepository.selectGoodsSuppliersInventoryByGoodsId(goodsId);
		}
	}
	public void init() {
		//������Ʒ���
		if(inventoryDO!=null)
		      this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId, inventoryDO);
		//��ѡ�Ϳ��
		if(!CollectionUtils.isEmpty(selectionInventoryList))
		      this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(goodsId, selectionInventoryList);
		//����ֵ���
		if(!CollectionUtils.isEmpty(suppliersInventoryList))
		      this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(goodsId, suppliersInventoryList);
	}
	// �����־��Ϣ
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
			updateActionDO.setUserId(Long.valueOf(param.getUserId()));
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			//updateActionDO.setOrderId(0l);
			updateActionDO
					.setContent(JSONObject.fromObject(param).toString()); // ��������
			updateActionDO.setRemark("עˮ����");
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
	 * �������
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

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
