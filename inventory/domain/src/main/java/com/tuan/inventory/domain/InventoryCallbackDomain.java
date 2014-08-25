package com.tuan.inventory.domain;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.core.common.lock.eum.LockResultCodeEnum;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.core.common.lock.res.LockResult;
import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.JsonUtils;
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
	private DLockImpl dLock;//分布式锁
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryQueueDO queueDO;
	private String ack;
	private String key;
	private int upStatusNum;
	//需扣减的商品库存量
	private int deductNum  = 0;
	private int limitStorage  = 0;
	//商品扣减量:非限制库存商品取默认值
	private int limtStorgeDeNum = 0;
	// 原库存
	private int originalGoodsInventory = 0;
	// 领域中缓存选型和分店原始库存和扣减库存的list
	private List<GoodsSelectionAndSuppliersResult> selectionParam;
	private List<GoodsSelectionAndSuppliersResult> suppliersParam;
	private Long goodsId;
	private Long goodsBaseId;
	private SequenceUtil sequenceUtil;
	private boolean isConfirm;
	private boolean isRollback;
	private boolean idemptent = false;
	
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
		// 填充参数
		this.fillParam();
		// 幂等性检查
		if (!StringUtils.isEmpty(key)) { // if
			this.idemptent = idemptent();
			if (idemptent) {
				return CreateInventoryResultEnum.SUCCESS;
			}
		}else {
			this.key = param.getKey();
		}
		try {
			//预处理
			this.preHandler();
			// 根据key查询缓存的队列信息
			this.queueDO = this.goodsInventoryDomainRepository
					.queryInventoryQueueDO(key);
			if (this.queueDO != null) {
				this.goodsId = queueDO.getGoodsId();
				this.goodsBaseId = queueDO.getGoodsBaseId();
				this.limitStorage = queueDO.getLimitStorage();
				this.deductNum = queueDO.getDeductNum();
				this.originalGoodsInventory = queueDO
						.getOriginalGoodsInventory();
				this.selectionParam = queueDO.getSelectionParam();
				this.suppliersParam = queueDO.getSuppliersParam();
				if(limitStorage==1) {
					limtStorgeDeNum = deductNum;
				}

			}

		} catch (Exception e) {
			/*this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()),false, e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}

		return CreateInventoryResultEnum.SUCCESS;
	}

	// 回调确认
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum ackInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			//确认:只变状态，此时不删缓存，异步处理时会删除的
			if (isConfirm) {
				String member = this.goodsInventoryDomainRepository.queryMember(key);
				lm.addMetaData("markqueue start isConfirm:["+isConfirm+",key:"+(key) + "]", key).addMetaData("member[" + (member) + "]", member);
				//writeSysDeductLog(lm,true);
				logSysUpdate.info(lm.toJson(false));
				if(!StringUtils.isEmpty(member)) {
					this.goodsInventoryDomainRepository.markQueueStatus(member, (upStatusNum));
				}else if(queueDO!=null){
					this.goodsInventoryDomainRepository.markQueueStatus(JSON.toJSONString(queueDO), (upStatusNum));
				}
				lm.addMetaData("markqueue end isConfirm:["+isConfirm+",key:"+(key) + "]", key).addMetaData("member[" + (member)+",upStatusNum:"+upStatusNum + "]", member);
				logSysUpdate.info(lm.toJson(false));
			}
			//回滚操作增加分布式锁
			lm.addMetaData("isRollback","ackInventory isRollback,start").addMetaData("isRollback[" + (isRollback+":"+goodsId) + "]", goodsId);
			logSysUpdate.info(lm.toJson(false));
			LockResult<String> lockResult = null;
			String key = DLockConstants.JOB_HANDLER+"_goodsId_" + goodsId;
			try {
				lockResult = dLock.lockManualByTimes(key, DLockConstants.ROLLBACK_LOCK_TIME, DLockConstants.ROLLBACK_LOCK_RETRY_TIMES);
				if (lockResult == null
						|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
								.getCode()) {
					logSysUpdate.info(lm.addMetaData("ackInventory","ackInventory").addMetaData("dLock rollback errorMsg",
							goodsId).toJson(false));
					/*writeSysDeductLog(
							lm.addMetaData("ackInventory","ackInventory").addMetaData("dLock rollback errorMsg",
									goodsId), false);*/
				}
				//回滚:即变状态，同时将缓存删除
				if (isRollback) {
					lm.addMetaData("isRollback","ackInventory isRollback,start").addMetaData("isRollback[" + (isRollback+":"+goodsId) + "]", goodsId);
					logSysUpdate.info(lm.toJson(false));
					// 回滚库存
					if (goodsId != null && goodsId > 0) {
						lm.addMetaData("isRollback goods[" + (isRollback+":"+goodsId) + "]", goodsId+",deductNum:"+deductNum);
						logSysUpdate.info(lm.toJson(false));
						List<Long> rollbackAftNum = this.goodsInventoryDomainRepository
								.updateGoodsInventory(goodsId,goodsBaseId,(limtStorgeDeNum), (deductNum));
					lm.addMetaData("isRollback after[" + (isRollback+":"+goodsId) + "]", goodsId+",rollbackAftNum:"+rollbackAftNum+",goodsBaseId:"+goodsBaseId);
					logSysUpdate.info(lm.toJson(false));
					}
					if (!CollectionUtils.isEmpty(selectionParam)) {
						lm.addMetaData("isRollback selection start[" + (isRollback+":"+selectionParam.toString()) + "]", selectionParam);
						logSysUpdate.info(lm.toJson(false));
						boolean rollbackSelAck = this.goodsInventoryDomainRepository
								.rollbackSelectionInventory(selectionParam);
						lm.addMetaData("isRollback selection end[" + (isRollback+":"+selectionParam.toString()) + "]", selectionParam+",rollbackselresult:"+rollbackSelAck);
						logSysUpdate.info(lm.toJson(false));
					}
					if (!CollectionUtils.isEmpty(suppliersParam)) {
						lm.addMetaData("isRollback suppliers start[" + (isRollback+":"+suppliersParam.toString()) + "]", suppliersParam);
						logSysUpdate.info(lm.toJson(false));
						boolean rollbackSuppAck =this.goodsInventoryDomainRepository
								.rollbackSuppliersInventory(suppliersParam);
						lm.addMetaData("isRollback suppliers end[" + (isRollback+":"+suppliersParam.toString()) + "]", suppliersParam+",rollbacksuppresult:"+rollbackSuppAck);
						logSysUpdate.info(lm.toJson(false));
					}
					if (queueDO != null) {
						// 将队列标记删除
						
						String member = this.goodsInventoryDomainRepository
								.queryMember(key);
						lm.addMetaData("markqueue start isRollback:[" +isRollback+",key:"+ (key) + "]", key).addMetaData("member[" + (member) + "]", member);
						logSysUpdate.info(lm.toJson(false));
						if (!StringUtils.isEmpty(member)) {
							if(!this.goodsInventoryDomainRepository
									.markQueueStatusAndDeleteCacheMember(
											member, (upStatusNum), key)){
								if(queueDO!=null) {
									this.goodsInventoryDomainRepository
									.markQueueStatusAndDeleteCacheMember(
											JSON.toJSONString(queueDO), (upStatusNum), key);
								}
								
							}
						}
						lm.addMetaData("markqueue end isRollback:["+isRollback+",key:"+ (key) + "]", key).addMetaData("member[" + (member)+",upStatusNum:"+upStatusNum + "]", member);
						logSysUpdate.info(lm.toJson(false));
					}

				}//isRollback
			} finally{
				dLock.unlockManual(key);
			}
			lm.addMetaData("result", "end");
			logSysUpdate.info(lm.toJson(false));
		} catch (Exception e) {
			/*this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"ackInventory error" + e.getMessage()),false, e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"ackInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		//处理成返回前设置tag
		if (StringUtils.isNotEmpty(key)) {
			goodsInventoryDomainRepository.setTag(
					DLockConstants.CALLBACK_INVENTORY_SUCCESS + "_" + key,
					DLockConstants.IDEMPOTENT_DURATION_TIME,
					DLockConstants.HANDLER_SUCCESS);
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			if(goodsId!=null&&goodsId!=0) {
				updateActionDO.setGoodsId(goodsId);
			}
			if(goodsBaseId!=null&&goodsBaseId!=0) {
				updateActionDO.setGoodsBaseId(goodsBaseId);
			}
			
			updateActionDO.setBusinessType("");
			updateActionDO.setOriginalInventory(String
					.valueOf(originalGoodsInventory));
			updateActionDO.setInventoryChange(String.valueOf(deductNum));
			updateActionDO.setActionType(ResultStatusEnum.CALLBACK_CONFIRM
					.getDescription());
			if(queueDO!=null) {
				updateActionDO.setUserId(queueDO.getUserId());
				updateActionDO.setOrderId(queueDO.getOrderId());
				updateActionDO
				.setContent(JsonUtils.convertObjectToString(queueDO)); // 操作内容
			}else {
				updateActionDO
				.setContent(JsonUtils.convertObjectToString(param)); // 操作内容
			}
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setRemark("回调确认");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			//this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillInventoryUpdateActionDO error" +e.getMessage()),false, e);
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"fillInventoryUpdateActionDO error" + e.getMessage()).toJson(false), e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}

	public boolean idemptent() {
		//根据key取已缓存的tokenid  
		String gettokenid = goodsInventoryDomainRepository.queryToken(DLockConstants.CALLBACK_INVENTORY + "_"+ key);
		if(StringUtils.isEmpty(gettokenid)) {  //如果为空则任务是初始的http请求过来，将tokenid缓存起来
			if(StringUtils.isNotEmpty(key)) {
				goodsInventoryDomainRepository.setTag(DLockConstants.CALLBACK_INVENTORY + "_"+ key, DLockConstants.IDEMPOTENT_DURATION_TIME, key);
			}
					
		}else {  //否则比对token值
			if(StringUtils.isNotEmpty(key)) {
				if(key.equalsIgnoreCase(gettokenid)) { //重复请求过来，判断是否处理成功
				//根据处理成功后设置的tag来判断之前http请求处理是否成功
				String gettag = goodsInventoryDomainRepository.queryToken(DLockConstants.CALLBACK_INVENTORY_SUCCESS + "_"+ key);
				if(!StringUtils.isEmpty(gettag)&&gettag.equalsIgnoreCase(DLockConstants.HANDLER_SUCCESS)) { 
								return true;
							}
						}
					}
		}
		return false;
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

	public void setdLock(DLockImpl dLock) {
		this.dLock = dLock;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
