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
	//需扣减的商品库存量
	private int deductNum  = 0;
	// 原库存
	private int originalGoodsInventory = 0;
	// 领域中缓存选型和分店原始库存和扣减库存的list
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

	// 初始化参数
	private void fillParam() {
		// ack:确认码
		this.ack = param.getAck();
		// key
		this.key = param.getKey();

	}
	public void preHandler() {
		// 确认
		if (this.ack.equalsIgnoreCase(ResultStatusEnum.CONFIRM.getCode())) {
			this.isConfirm = true;
			// 将队列状态由初始锁定状态3，更新为确定状态：1
			this.upStatusNum = -2;
		}
		//回滚
		if (ack.equalsIgnoreCase(ResultStatusEnum.ROLLBACK.getCode())) {
			this.isRollback = true;
			// 将该非正常状况队列状态由初始状态：锁定：3，置为删除:7
			this.upStatusNum = 4;
		}
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {

		try {
			// 填充参数
			this.fillParam();
			//预处理
			this.preHandler();
			if (isRollback) {
				// 根据key查询缓存的队列信息
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

	// 回调确认
	public CreateInventoryResultEnum ackInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			//确认
			if (isConfirm) {
				this.goodsInventoryDomainRepository.markQueueStatus(key,
						(upStatusNum));
			}
			//回滚
			if (isRollback) {
				// 回滚库存
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
				// 将队列标记删除
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

	// 填充日志信息
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
					.setContent(JSONObject.fromObject(queueDO).toString()); // 操作内容
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

	/**
	 * 参数检查
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
