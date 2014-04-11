package com.tuan.inventory.service.impl;

import javax.annotation.Resource;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.domain.InventoryCreatorDomain;
import com.tuan.inventory.domain.InventoryUpdateDomain;
import com.tuan.inventory.domain.repository.InitCacheDomainRepository;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;
import com.tuan.inventory.model.param.CallbackParam;
import com.tuan.inventory.model.param.CreatorInventoryParam;
import com.tuan.inventory.model.param.DeleteInventoryParam;
import com.tuan.inventory.model.param.UpdateInventoryParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.service.InventoryUpdateServiceCallback;
import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageResultEnum;
import com.wowotrace.traceEnum.MessageTypeEnum;

public class GoodsInventoryUpdateServiceImpl  extends AbstractInventoryService implements GoodsInventoryUpdateService {
	@Resource
	private GoodsInventoryDomainRepository updateInventoryDomainRepository;
	@Resource
	private InitCacheDomainRepository initCacheDomainRepository;
	@Resource
	private SequenceUtil sequenceUtil;
	/**
	 * 新增库存
	 */
	@Override
	public InventoryCallResult createInventory(String clientIp,
			String clientName, CreatorInventoryParam param,Message traceMessage) {
		if(traceMessage == null){
			return new InventoryCallResult(CreateInventoryResultEnum.INVALID_PARAM.getCode()
					,CreateInventoryResultEnum.INVALID_PARAM.getDescription(), null);
		}
		String method = "InventoryCreateService.createInventory";
		final LogModel lm = LogModel.newLogModel(traceMessage.getTraceHeader().getRootId());
		writeSysLog(lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("traceId",traceMessage.traceHeader.getRootId()), true);
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "InventoryCreateService", "createInventory");
		//构建领域对象
		final InventoryCreatorDomain inventoryCreatorDomain = new InventoryCreatorDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryCreatorDomain.setUpdateInventoryDomainRepository(updateInventoryDomainRepository);
		inventoryCreatorDomain.setSequenceUtil(sequenceUtil);
		TuanCallbackResult result = this.busiServiceTemplate.execute(new InventoryUpdateServiceCallback(){
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
				inventoryCreatorDomain.sendNotify();
			}
		});

		lm.setMethod(method).addMetaData("resultCode", result.getResultCode())
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", inventoryCreatorDomain.getGoodsId());
		writeSysLog(lm, true);
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	/**
	 * 更新库存
	 */
	@Override
	public InventoryCallResult updateInventory(String clientIp,
			String clientName, UpdateInventoryParam param, Message traceMessage) {
		if(traceMessage == null){
			return new InventoryCallResult(CreateInventoryResultEnum.INVALID_PARAM.getCode()
					,CreateInventoryResultEnum.INVALID_PARAM.getDescription(), null);
		}
		String method = "InventoryCreateService.updateInventory";
		final LogModel lm = LogModel.newLogModel(traceMessage.getTraceHeader().getRootId());
		writeSysLog(lm.setMethod(method).addMetaData("clientIp", clientIp).addMetaData("clientName", clientName)
				.addMetaData("param", param.toString()).addMetaData("traceId",traceMessage.traceHeader.getRootId()), true);
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "InventoryCreateService", "updateInventory");
		//构建领域对象
		final InventoryUpdateDomain inventoryUpdateDomain = new InventoryUpdateDomain(clientIp, clientName, param, lm);
		//注入仓储对象
		inventoryUpdateDomain.setUpdateInventoryDomainRepository(updateInventoryDomainRepository);
		inventoryUpdateDomain.setInitCacheDomainRepository(initCacheDomainRepository);
		inventoryUpdateDomain.setSequenceUtil(sequenceUtil);
		TuanCallbackResult result = this.busiServiceTemplate.execute(new InventoryUpdateServiceCallback(){
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
					return TuanCallbackResult.success();
				}else{
					return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
				}
			}

			@Override
			public void executeAfter() {
				inventoryUpdateDomain.pushSendMsgQueue();
			}
		});

		lm.setMethod(method).addMetaData("resultCode", result.getResultCode())
		.addMetaData("description", CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name())
		.addMetaData("goodsId", inventoryUpdateDomain.getGoodsId());
		writeSysLog(lm, true);
		TraceMessageUtil.traceMessagePrintE(traceMessage, MessageResultEnum.SUCCESS);
		return new InventoryCallResult(result.getResultCode(), 
				CreateInventoryResultEnum.valueOfEnum(result.getResultCode()).name(),null);
	}
	@Override
	public InventoryCallResult callbackAckInventory(String clientIp,
			String clientName, CallbackParam param, Message traceMessage) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public InventoryCallResult adjustmentInventory(String clientIp,
			String clientName, AdjustInventoryParam param, Message traceMessage) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public InventoryCallResult adjustmentWaterflood(String clientIp,
			String clientName, AdjustWaterfloodParam param,
			Message traceMessage) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public InventoryCallResult deleteInventory(String clientIp,
			String clientName, DeleteInventoryParam param, Message traceMessage) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	
}
