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
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;

public class InventoryAdjustDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private AdjustInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryDO;
	private GoodsSelectionDO selectionInventory;
	private GoodsSuppliersDO suppliersInventory;
	//ѡ��
	private List<GoodsSelectionModel> selectionMsg;
	//�ֵ�
	private List<GoodsSuppliersModel> suppliersMsg;
	
	private String type;
	private String id;
	private int adjustNum;
	private String businessType;
	// ԭ���
	private int originalGoodsInventory = 0;
	private Long goodsId;
	private long selectionId;
	private long suppliersId;
	//��������
	private Long resultACK;
	//�Ƿ���Ҫ��ʼ��
	private boolean isInit;
	//��ʼ����
	private List<GoodsSuppliersDO> suppliersInventoryList;
	private List<GoodsSelectionDO> selectionInventoryList;
	
	public InventoryAdjustDomain(String clientIp, String clientName,
			AdjustInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	// ҵ����
	public CreateInventoryResultEnum busiCheck() {
		try {
			//��ʼ�����
			this.initCheck();
			if (isInit) {
				this.init();
			}
			//�����Ŀ�����ҵ����
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF.getDescription();
				//��ѯ��Ʒ���
				this.inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if(inventoryDO!=null) {
					this.originalGoodsInventory = inventoryDO.getLeftNumber();
				}
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION.getDescription();
				//��ѯ��Ʒѡ�Ϳ��
				this.selectionInventory = this.goodsInventoryDomainRepository.querySelectionRelationById(selectionId);
				if(selectionInventory!=null) {
					this.originalGoodsInventory = selectionInventory.getLeftNumber();
				}
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.suppliersId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SUPPLIERS.getDescription();
				//��ѯ��Ʒ�ֵ���
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

	// ���ϵͳ�������
	public CreateInventoryResultEnum adjustInventory() {
		try {
			// ���������־��Ϣ
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// ������־
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);

			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.updateGoodsInventory(goodsId, (adjustNum));
				if(!verifyInventory()) {
					//����滹ԭ������ǰ
					this.goodsInventoryDomainRepository.updateGoodsInventory(goodsId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
				}
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.updateSelectionInventoryById(selectionId, (adjustNum));
				if(!verifyInventory()) {
					//����滹ԭ������ǰ
					this.goodsInventoryDomainRepository.updateSelectionInventoryById(selectionId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
				}
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.resultACK = this.goodsInventoryDomainRepository.updateSuppliersInventoryById(suppliersId, (adjustNum));
				if(!verifyInventory()) {
					//����滹ԭ������ǰ
					this.goodsInventoryDomainRepository.updateSuppliersInventoryById(suppliersId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
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

	//���Ϳ��������Ϣ
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
		// ��ʼ������
		private void fillParam() {
			// 2:��Ʒ 4��ѡ�� 6���ֵ�
			this.type = param.getType();
			// 2����Ʒid 4��ѡ��id 6 �ֵ�id
			this.id = param.getId();
			this.adjustNum = param.getNum();
			
		}
		//���notifyserver���Ͳ���
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(){
			InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
			notifyParam.setUserId(Long.valueOf(param.getUserId()));
			notifyParam.setGoodsId(goodsId);
			if(inventoryDO!=null) {
				notifyParam.setLimitStorage(inventoryDO.getLimitStorage());
				notifyParam.setWaterfloodVal(inventoryDO.getWaterfloodVal());
				notifyParam.setTotalNumber((inventoryDO.getTotalNumber()+adjustNum));
				notifyParam.setLeftNumber(resultACK.intValue());
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
					.valueOf(originalGoodsInventory));
			updateActionDO.setInventoryChange(String.valueOf(adjustNum));
			updateActionDO.setActionType(ResultStatusEnum.CALLBACK_CONFIRM
					.getDescription());
			updateActionDO.setUserId(Long.valueOf(param.getUserId()));
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			//updateActionDO.setOrderId(0l);
			updateActionDO
					.setContent(JSONObject.fromObject(param).toString()); // ��������
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
	private boolean verifyInventory() {
		if(resultACK>=0) {
			return true;
		}else {
			return false;
		}
	}
	
	public void fillSelectionMsg() {
		List<GoodsSelectionModel> selectionMsg = new ArrayList<GoodsSelectionModel>();
		GoodsSelectionModel gsModel = new GoodsSelectionModel();
		try {
			gsModel.setGoodTypeId(selectionInventory.getGoodTypeId());
			gsModel.setGoodsId(goodsId);
			gsModel.setId(selectionId);
			gsModel.setLeftNumber(resultACK.intValue());  //������Ŀ��ֵ
			gsModel.setTotalNumber((selectionInventory.getTotalNumber()+adjustNum));
			gsModel.setUserId(Long.valueOf(param.getUserId()));
			gsModel.setLimitStorage(selectionInventory.getLimitStorage());
			gsModel.setWaterfloodVal(selectionInventory.getWaterfloodVal());
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
			gsModel.setLeftNumber(resultACK.intValue());  //������Ŀ��ֵ
			gsModel.setTotalNumber((suppliersInventory.getTotalNumber()+adjustNum));
			gsModel.setUserId(Long.valueOf(param.getUserId()));
			gsModel.setLimitStorage(suppliersInventory.getLimitStorage());
			gsModel.setWaterfloodVal(suppliersInventory.getWaterfloodVal());
			suppliersMsg.add(gsModel);
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("fillSuppliersMsg")
					.addMetaData("errMsg", e.getMessage()), e);
			this.suppliersMsg = null;
		}
		this.suppliersMsg = suppliersMsg;
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
