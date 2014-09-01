package com.tuan.inventory.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lock.eum.LockResultCodeEnum;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.core.common.lock.res.LockResult;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.domain.InventoryAdjustDomain;
import com.tuan.inventory.domain.InventoryCallbackDomain;
import com.tuan.inventory.domain.InventoryCreate4GoodsCostDomain;
import com.tuan.inventory.domain.InventoryCreateDomain;
import com.tuan.inventory.domain.InventoryOverrideAdjustDomain;
import com.tuan.inventory.domain.InventoryRestoreDomain;
import com.tuan.inventory.domain.InventoryUpdate4LotteryDomain;
import com.tuan.inventory.domain.InventoryUpdateDomain;
import com.tuan.inventory.domain.InventoryWmsAdjust4SelectionDomain;
import com.tuan.inventory.domain.InventoryWmsCreateDomain;
import com.tuan.inventory.domain.InventoryWmsDataUpdateDomain;
import com.tuan.inventory.domain.InventoryWmsUpdateDomain;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.domain.WaterfloodAdjustmentDomain;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.SynInitAndAsynUpdateDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;
import com.tuan.inventory.model.param.CallbackParam;
import com.tuan.inventory.model.param.CreateInventory4GoodsCostParam;
import com.tuan.inventory.model.param.CreaterInventoryParam;
import com.tuan.inventory.model.param.OverrideAdjustInventoryParam;
import com.tuan.inventory.model.param.RestoreInventoryParam;
import com.tuan.inventory.model.param.UpdateInventoryParam;
import com.tuan.inventory.model.param.UpdateLotteryInventoryParam;
import com.tuan.inventory.model.param.UpdateWmsDataParam;
import com.tuan.inventory.model.param.WmsAdjustSelectionParam;
import com.tuan.inventory.model.param.WmsInventoryParam;
import com.tuan.inventory.model.param.rest.QueueKeyIdParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.service.InventoryUpdateServiceCallback;
import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageResultEnum;
import com.wowotrace.traceEnum.MessageTypeEnum;

public class GoodsInventoryUpdateServiceImpl  extends AbstractInventoryService implements GoodsInventoryUpdateService {
	protected static Log logSaveAndUpdate = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	@Resource
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	@Resource
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	@Resource
	private SequenceUtil sequenceUtil;
	@Resource
	private DLockImpl dLock;//分布式锁
	
	/**
	 * 新增库存
	 */
	@Override
	public InventoryCallResult createInventory(String clientIp,
			String clientName, CreaterInventoryParam param,Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.createInventory";
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "createInventory");
		//构建领域对象
		final InventoryCreateDomain inventoryCreatorDomain = new InventoryCreateDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryCreatorDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryCreatorDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryCreatorDomain.setSynInitAndAsynUpdateDomainRepository(synInitAndAsynUpdateDomainRepository);
		inventoryCreatorDomain.setSequenceUtil(sequenceUtil);
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryCreatorDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = inventoryCreatorDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryCreatorDomain.createInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				//inventoryCreatorDomain.sendNotify();
			}
		});
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	/**
	 * 物流商品创建接口
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	@Override
	public InventoryCallResult createWmsInventory(String clientIp,
			String clientName, WmsInventoryParam param,Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.createWmsInventory";
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "createWmsInventory");
		//构建领域对象
		final InventoryWmsCreateDomain inventoryWmsCreaterDomain = new InventoryWmsCreateDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryWmsCreaterDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryWmsCreaterDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryWmsCreaterDomain.setSequenceUtil(sequenceUtil);
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryWmsCreaterDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}
			
			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = inventoryWmsCreaterDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}
			
			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryWmsCreaterDomain.createWmsInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}
			
			@Override
			public void executeAfter() {
				//TODO 暂时不发为好
//				inventoryWmsCreaterDomain.sendNotify();
			}
		});
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("wmsGoodsId", param.getWmsGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	/**
	 * 更新库存
	 */
	@SuppressWarnings("unchecked")
	@Override
	public InventoryCallResult updateInventory(String clientIp,
			String clientName, UpdateInventoryParam param, Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.updateInventory_"+startTime;
		final LogModel lm = LogModel.newLogModel(method);
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "updateInventory");
		//构建领域对象
		final InventoryUpdateDomain inventoryUpdateDomain = new InventoryUpdateDomain(clientIp, clientName, param, lm);
		//构建库存生成的队列id对象
		final QueueKeyIdParam queueIdParam = new QueueKeyIdParam();
		//注入仓储对象
		inventoryUpdateDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryUpdateDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryUpdateDomain.setSequenceUtil(sequenceUtil);
		String goodsId = "";
		if(param != null &&!StringUtils.isEmpty(param.getGoodsId())){
			goodsId = param.getGoodsId();
		}
		String lockKey = DLockConstants.JOB_HANDLER + "_goodsId_" + goodsId;
		LockResult<String> lockResult = dLock.lockManualByTimes(lockKey, DLockConstants.JOB_LOCK_TIME, DLockConstants.DEDUCT_LOCK_RETRY_TIMES);
		if(lockResult == null || lockResult.getCode() != LockResultCodeEnum.SUCCESS.getCode()){//获取锁失败
			CreateInventoryResultEnum enumRes = CreateInventoryResultEnum.DLOCK_ERROR;
			TuanCallbackResult failureResult = TuanCallbackResult.failure(enumRes.getCode(), null, enumRes.getDescription());
			return new InventoryCallResult(failureResult.getResultCode(), 
					CreateInventoryResultEnum.valueOfEnum(failureResult.getResultCode()).name(),null);

		}
		
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryUpdateDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = inventoryUpdateDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryUpdateDomain.updateInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					queueIdParam.setQueueKeyId(inventoryUpdateDomain.getQueueKeyId());
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				//String queueKeyId = inventoryUpdateDomain.pushSendMsgQueue();
				//if(!StringUtils.isEmpty(queueKeyId))
				    //  queueIdParam.setQueueKeyId(queueKeyId);
			}
		});
		dLock.unlockManual(lockKey);
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", inventoryUpdateDomain.getGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),queueIdParam);
	}
	@Override
	public InventoryCallResult callbackAckInventory(String clientIp,
			String clientName, CallbackParam param, Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.callbackAckInventory";
		final LogModel lm = LogModel.newLogModel(method);
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "callbackAckInventory");
		//构建领域对象
		final InventoryCallbackDomain inventoryCallbackDomain = new InventoryCallbackDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryCallbackDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryCallbackDomain.setSequenceUtil(sequenceUtil);
		inventoryCallbackDomain.setdLock(dLock);
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryCallbackDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = inventoryCallbackDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryCallbackDomain.ackInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				
			}
		});
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", inventoryCallbackDomain.getGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	@SuppressWarnings("unchecked")
	@Override
	public InventoryCallResult adjustmentInventory(String clientIp,
			String clientName, AdjustInventoryParam param, Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.adjustmentInventory";
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "adjustmentInventory");
		//构建领域对象
		final InventoryAdjustDomain inventoryAdjustDomain = new InventoryAdjustDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryAdjustDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryAdjustDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryAdjustDomain.setSequenceUtil(sequenceUtil);
		//inventoryAdjustDomain.setdLock(dLock);
		String goodsId = "";
		if(param != null &&!StringUtils.isEmpty(param.getGoodsId())){
			goodsId = param.getGoodsId();
		}
		String lockKey = DLockConstants.JOB_HANDLER + "_goodsId_" + goodsId;
		LockResult<String> lockResult = dLock.lockManualByTimes(lockKey, DLockConstants.JOB_LOCK_TIME, DLockConstants.DEDUCT_LOCK_RETRY_TIMES);
		if(lockResult == null || lockResult.getCode() != LockResultCodeEnum.SUCCESS.getCode()){//获取锁失败
			CreateInventoryResultEnum enumRes = CreateInventoryResultEnum.DLOCK_ERROR;
			TuanCallbackResult failureResult = TuanCallbackResult.failure(enumRes.getCode(), null, enumRes.getDescription());
			return new InventoryCallResult(failureResult.getResultCode(), 
					CreateInventoryResultEnum.valueOfEnum(failureResult.getResultCode()).name(),null);
		}
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryAdjustDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				
				CreateInventoryResultEnum resEnum = inventoryAdjustDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryAdjustDomain.adjustInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				if(!inventoryAdjustDomain.isSendMsg()) {
					inventoryAdjustDomain.sendNotify();
				}
				
			}
		});
		dLock.unlockManual(lockKey);
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", inventoryAdjustDomain.getGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	@Override
	public InventoryCallResult adjustmentWaterflood(String clientIp,
			String clientName, AdjustWaterfloodParam param,
			Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.adjustmentWaterflood";
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "adjustmentWaterflood");
		//构建领域对象
		final WaterfloodAdjustmentDomain waterfloodAdjustmentDomain = new WaterfloodAdjustmentDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		waterfloodAdjustmentDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		waterfloodAdjustmentDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		waterfloodAdjustmentDomain.setSequenceUtil(sequenceUtil);
		waterfloodAdjustmentDomain.setdLock(dLock);
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = waterfloodAdjustmentDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = waterfloodAdjustmentDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = waterfloodAdjustmentDomain.adjustWaterfloodVal();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				waterfloodAdjustmentDomain.sendNotify();
			}
		});
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", waterfloodAdjustmentDomain.getGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	@SuppressWarnings("unchecked")
	@Override
	public InventoryCallResult adjustWmsInventory(String clientIp,
			String clientName, WmsInventoryParam param, Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.adjustWmsInventory_"+startTime;
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "adjustWmsInventory");
		//构建领域对象
		final InventoryWmsUpdateDomain adjustWmsDomain = new InventoryWmsUpdateDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		adjustWmsDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		adjustWmsDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		adjustWmsDomain.setSequenceUtil(sequenceUtil);
		//adjustWmsDomain.setdLock(dLock);
		adjustWmsDomain.setLm(lm);
		String goodsId = "";
		if(param != null &&!CollectionUtils.isEmpty(param.getGoodsIds())) {
			goodsId = String.valueOf(param.getGoodsIds().get(0)!=null?param.getGoodsIds().get(0):0);
		}
		String lockKey = DLockConstants.JOB_HANDLER + "_goodsId_" + goodsId;
		LockResult<String> lockResult = dLock.lockManualByTimes(lockKey, DLockConstants.JOB_LOCK_TIME, DLockConstants.DEDUCT_LOCK_RETRY_TIMES);
		if(lockResult == null || lockResult.getCode() != LockResultCodeEnum.SUCCESS.getCode()){//获取锁失败
			CreateInventoryResultEnum enumRes = CreateInventoryResultEnum.DLOCK_ERROR;
			TuanCallbackResult failureResult = TuanCallbackResult.failure(enumRes.getCode(), null, enumRes.getDescription());
			return new InventoryCallResult(failureResult.getResultCode(), 
					CreateInventoryResultEnum.valueOfEnum(failureResult.getResultCode()).name(),null);
		}
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = adjustWmsDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = adjustWmsDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = adjustWmsDomain.updateAdjustWmsInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				//更新base信息
				adjustWmsDomain.aftUpdateGoodsBaseInfo(adjustWmsDomain.getGoodsBaseIdsList());
			}
		});
		dLock.unlockManual(lockKey);
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("wmsGoodsId", param.getWmsGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	@SuppressWarnings("unchecked")
	@Override
	public InventoryCallResult overrideAdjustInventory(String clientIp,
			String clientName, OverrideAdjustInventoryParam param,
			Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.overrideAdjustInventory";
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "overrideAdjustInventory");
		//构建领域对象
		final InventoryOverrideAdjustDomain inventoryAdjustDomain = new InventoryOverrideAdjustDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryAdjustDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryAdjustDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryAdjustDomain.setSequenceUtil(sequenceUtil);
		//inventoryAdjustDomain.setdLock(dLock);
		String goodsId = "";
		if(param != null &&!StringUtils.isEmpty(param.getGoodsId())){
			goodsId = param.getGoodsId();
		}
		String lockKey = DLockConstants.JOB_HANDLER + "_goodsId_" + goodsId;
		LockResult<String> lockResult = dLock.lockManualByTimes(lockKey, DLockConstants.JOB_LOCK_TIME, DLockConstants.DEDUCT_LOCK_RETRY_TIMES);
		if(lockResult == null || lockResult.getCode() != LockResultCodeEnum.SUCCESS.getCode()){//获取锁失败
			CreateInventoryResultEnum enumRes = CreateInventoryResultEnum.DLOCK_ERROR;
			TuanCallbackResult failureResult = TuanCallbackResult.failure(enumRes.getCode(), null, enumRes.getDescription());
			return new InventoryCallResult(failureResult.getResultCode(), 
					CreateInventoryResultEnum.valueOfEnum(failureResult.getResultCode()).name(),null);
		}
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryAdjustDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				
				CreateInventoryResultEnum resEnum = inventoryAdjustDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryAdjustDomain.adjustInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				if(!inventoryAdjustDomain.isSendMsg()) {  //库存不调整时不发消息
					inventoryAdjustDomain.sendNotify();
				}
			}
		});
		dLock.unlockManual(lockKey);
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", inventoryAdjustDomain.getGoodsId())
		.addMetaData("是否发送消息", inventoryAdjustDomain.isSendMsg()==false?"发消息":"库存没有调整故无需发送消息")
		.addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	@Override
	public InventoryCallResult updateWmsData(String clientIp,
			String clientName, UpdateWmsDataParam param, Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.updateWmsData";
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "updateWmsData");
		//构建领域对象
		final InventoryWmsDataUpdateDomain wmsDataUpdateDomain = new InventoryWmsDataUpdateDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		wmsDataUpdateDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		wmsDataUpdateDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		wmsDataUpdateDomain.setSequenceUtil(sequenceUtil);
		wmsDataUpdateDomain.setdLock(dLock);
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = wmsDataUpdateDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				
				CreateInventoryResultEnum resEnum = wmsDataUpdateDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = wmsDataUpdateDomain.updateAndInsertWmsData();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				wmsDataUpdateDomain.sendNotify();
			}
		});
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", wmsDataUpdateDomain.getGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	/**
	 * 新增库存4商品改价
	 */
	@SuppressWarnings("unchecked")
	@Override
	public InventoryCallResult createInventory4GoodsCost(String clientIp,
			String clientName, CreateInventory4GoodsCostParam param, Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.createInventory4GoodsCost";
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "createInventory4GoodsCost");
		//构建领域对象
		final InventoryCreate4GoodsCostDomain inventoryCreateDomain = new InventoryCreate4GoodsCostDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryCreateDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryCreateDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryCreateDomain.setSynInitAndAsynUpdateDomainRepository(synInitAndAsynUpdateDomainRepository);
		inventoryCreateDomain.setSequenceUtil(sequenceUtil);
		inventoryCreateDomain.setdLock(dLock);
		Long goodsId = 0L;
		if(param != null && param.getGoodsId() != null){
			goodsId = param.getGoodsId();
		}
		String lockKey = DLockConstants.JOB_HANDLER + "_goodsId_" + goodsId;
		LockResult<String> lockResult = dLock.lockManualByTimes(lockKey, DLockConstants.JOB_LOCK_TIME, DLockConstants.DEDUCT_LOCK_RETRY_TIMES);
		if(lockResult == null || lockResult.getCode() != LockResultCodeEnum.SUCCESS.getCode()){//获取锁失败
			CreateInventoryResultEnum enumRes = CreateInventoryResultEnum.DLOCK_ERROR;
			TuanCallbackResult failureResult = TuanCallbackResult.failure(enumRes.getCode(), null, enumRes.getDescription());
			return new InventoryCallResult(failureResult.getResultCode(), 
					CreateInventoryResultEnum.valueOfEnum(failureResult.getResultCode()).name(),null);
		}
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryCreateDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = inventoryCreateDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryCreateDomain.createInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				inventoryCreateDomain.sendNotify();
			}
		});
		dLock.unlockManual(lockKey);
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	@SuppressWarnings("unchecked")
	@Override
	public InventoryCallResult restoreInventory(String clientIp,
			String clientName, RestoreInventoryParam param, Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.restoreInventory";
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "restoreInventory");
		//构建领域对象
		final InventoryRestoreDomain inventoryRestoreDomain = new InventoryRestoreDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryRestoreDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryRestoreDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryRestoreDomain.setSequenceUtil(sequenceUtil);
		inventoryRestoreDomain.setdLock(dLock);
		Long goodsId = 0L;
		if(param != null && param.getGoodsId() != null){
			goodsId = param.getGoodsId();
		}
		String lockKey = DLockConstants.JOB_HANDLER + "_goodsId_" + goodsId;
		LockResult<String> lockResult = dLock.lockManualByTimes(lockKey, DLockConstants.JOB_LOCK_TIME, DLockConstants.DEDUCT_LOCK_RETRY_TIMES);
		if(lockResult == null || lockResult.getCode() != LockResultCodeEnum.SUCCESS.getCode()){//获取锁失败
			CreateInventoryResultEnum enumRes = CreateInventoryResultEnum.DLOCK_ERROR;
			TuanCallbackResult failureResult = TuanCallbackResult.failure(enumRes.getCode(), null, enumRes.getDescription());
			return new InventoryCallResult(failureResult.getResultCode(), 
					CreateInventoryResultEnum.valueOfEnum(failureResult.getResultCode()).name(),null);
		}
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryRestoreDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				
				CreateInventoryResultEnum resEnum = inventoryRestoreDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryRestoreDomain.restoreInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				inventoryRestoreDomain.sendNotify();
			}
		});
		dLock.unlockManual(lockKey);
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", inventoryRestoreDomain.getGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	@SuppressWarnings("unchecked")
	@Override
	public InventoryCallResult incrGoodsSaleCount(String clientIp,
			String clientName, UpdateLotteryInventoryParam param,
			Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.updateInventory4Lottery";
		final LogModel lm = LogModel.newLogModel(method);
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "updateInventory4Lottery");
		//构建领域对象
		final InventoryUpdate4LotteryDomain inventoryUpdateDomain = new InventoryUpdate4LotteryDomain(clientIp, clientName, param, lm);
		//构建库存生成的队列id对象
		final QueueKeyIdParam queueIdParam = new QueueKeyIdParam();
		//注入仓储对象
		inventoryUpdateDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryUpdateDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryUpdateDomain.setSequenceUtil(sequenceUtil);
		//inventoryUpdateDomain.setdLock(dLock);
		String goodsId = "";
		if(param != null &&param.getGoodsId()!=0){
			goodsId = String.valueOf(param.getGoodsId());
		}
		String lockKey = DLockConstants.JOB_HANDLER + "_goodsId_" + goodsId;
		LockResult<String> lockResult = dLock.lockManualByTimes(lockKey, DLockConstants.JOB_LOCK_TIME, DLockConstants.DEDUCT_LOCK_RETRY_TIMES);
		if(lockResult == null || lockResult.getCode() != LockResultCodeEnum.SUCCESS.getCode()){//获取锁失败
			CreateInventoryResultEnum enumRes = CreateInventoryResultEnum.DLOCK_ERROR;
			TuanCallbackResult failureResult = TuanCallbackResult.failure(enumRes.getCode(), null, enumRes.getDescription());
			return new InventoryCallResult(failureResult.getResultCode(), 
					CreateInventoryResultEnum.valueOfEnum(failureResult.getResultCode()).name(),null);
		}
		
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = inventoryUpdateDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = inventoryUpdateDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = inventoryUpdateDomain.updateInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					queueIdParam.setQueueKeyId(inventoryUpdateDomain.getQueueKeyId());
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {}
		});
		dLock.unlockManual(lockKey);
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", inventoryUpdateDomain.getGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),queueIdParam);
	}
	@SuppressWarnings("unchecked")
	@Override
	public InventoryCallResult adjustWmsSelectionInventory(String clientIp,
			String clientName, WmsAdjustSelectionParam param,
			Message traceMessage) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryUpdateService.adjustWmsSelectionInventory"+startTime;
		final LogModel lm = LogModel.newLogModel(traceMessage == null?method:traceMessage.getTraceHeader().getRootId());
		lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("start",startTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryUpdateService", "adjustWmsSelectionInventory");
		//构建领域对象
		final InventoryWmsAdjust4SelectionDomain adjustWmsDomain = new InventoryWmsAdjust4SelectionDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		adjustWmsDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		adjustWmsDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		adjustWmsDomain.setSequenceUtil(sequenceUtil);
		//adjustWmsDomain.setdLock(dLock);
		adjustWmsDomain.setLm(lm);
		String goodsId = "";
		if(param != null &&param.getGoodsId()!=null&&param.getGoodsId()!=0){
			goodsId = String.valueOf(param.getGoodsId());
		}
		String lockKey = DLockConstants.JOB_HANDLER + "_goodsId_" + goodsId;
		LockResult<String> lockResult = dLock.lockManualByTimes(lockKey, DLockConstants.JOB_LOCK_TIME, DLockConstants.DEDUCT_LOCK_RETRY_TIMES);
		if(lockResult == null || lockResult.getCode() != LockResultCodeEnum.SUCCESS.getCode()){//获取锁失败
			CreateInventoryResultEnum enumRes = CreateInventoryResultEnum.DLOCK_ERROR;
			TuanCallbackResult failureResult = TuanCallbackResult.failure(enumRes.getCode(), null, enumRes.getDescription());
			return new InventoryCallResult(failureResult.getResultCode(), 
					CreateInventoryResultEnum.valueOfEnum(failureResult.getResultCode()).name(),null);
		}
		TuanCallbackResult result = this.inventoryServiceTemplate.execute(new InventoryUpdateServiceCallback(){
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CreateInventoryResultEnum resultEnum = adjustWmsDomain.checkParam();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CreateInventoryResultEnum resEnum = adjustWmsDomain.busiCheck();
				if (resEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0) {
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resEnum.getCode(), null,resEnum.getDescription());
				}
			}

			@Override
			public TuanCallbackResult executeAction() {
				CreateInventoryResultEnum resultEnum = adjustWmsDomain.updateAdjustWmsInventory();
				if(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0){
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				
			}
		});
		dLock.unlockManual(lockKey);
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.setMethod(method).addMetaData("resultCode", result.getResultCode()).addMetaData("runResult", runResult)
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("wmsGoodsId", param.getWmsGoodsId()).addMetaData("end", endTime);
		logSaveAndUpdate.info(lm.toJson(false));
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	
	

	
	
}
