package com.tuan.inventory.service.impl;

import javax.annotation.Resource;

import com.tuan.inventory.domain.InventoryConfirmScheduledDomain;
import com.tuan.inventory.domain.InventoryLockedScheduledDomain;
import com.tuan.inventory.domain.InventoryLogsScheduledDomain;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.job.event.EventHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.param.InventoryScheduledParam;
import com.tuan.inventory.service.GoodsInventoryScheduledService;
import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageTypeEnum;

public class GoodsInventoryScheduledServiceImpl   extends AbstractInventoryService implements
		GoodsInventoryScheduledService {
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	@Resource
	private EventHandle logsEventHandle;
	
	@Override
	public void confirmQueueConsume(String clientIp,
			String clientName,Message traceMessage) {
		String method = "GoodsInventoryScheduledService.confirmQueueConsume";
		final LogModel lm = LogModel.newLogModel(traceMessage.getTraceHeader().getRootId());
		writeSysLog(lm.setMethod(method)
				.addMetaData("clientIp", clientIp)
				.addMetaData("clientName", clientName)
				.addMetaData("traceId",traceMessage.traceHeader.getRootId()), true);
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryScheduledService", "confirmQueueConsume");
		//构建领域对象
		final InventoryConfirmScheduledDomain inventoryConfirmScheduledDomain = new InventoryConfirmScheduledDomain(clientIp, clientName, lm);
		//注入仓储对象
		inventoryConfirmScheduledDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		//业务处理
		inventoryConfirmScheduledDomain.businessHandler();

	}

	@Override
	public void lockedQueueConsume(String clientIp,
			String clientName,InventoryScheduledParam param,Message traceMessage) {

		String method = "GoodsInventoryScheduledService.lockedQueueConsume";
		final LogModel lm = LogModel.newLogModel(traceMessage.getTraceHeader().getRootId());
		writeSysLog(lm.setMethod(method)
				.addMetaData("clientIp", clientIp)
				.addMetaData("clientName", clientName)
				.addMetaData("param", param.toString())
				.addMetaData("traceId",traceMessage.traceHeader.getRootId()), true);
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryScheduledService", "lockedQueueConsume");
		//构建领域对象
		final InventoryLockedScheduledDomain inventoryLockedScheduledDomain = new InventoryLockedScheduledDomain(clientIp, clientName,param, lm);
		//注入仓储对象
		inventoryLockedScheduledDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		//业务处理
		inventoryLockedScheduledDomain.businessHandler();

	}

	@Override
	public void logsQueueConsume(String clientIp, String clientName,
			Message traceMessage) {
		String method = "GoodsInventoryScheduledService.logsQueueConsume";
		final LogModel lm = LogModel.newLogModel(traceMessage.getTraceHeader().getRootId());
		writeSysLog(lm.setMethod(method)
				.addMetaData("clientIp", clientIp)
				.addMetaData("clientName", clientName)
				.addMetaData("traceId",traceMessage.traceHeader.getRootId()), true);
		TraceMessageUtil.traceMessagePrintS(
				traceMessage, MessageTypeEnum.CENTS, "Inventory", "GoodsInventoryScheduledService", "logsQueueConsume");
		//构建领域对象
		final InventoryLogsScheduledDomain inventoryLogsScheduledDomain = new InventoryLogsScheduledDomain(clientIp, clientName, lm);
		//注入仓储对象
		inventoryLogsScheduledDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryLogsScheduledDomain.setLogsEventHandle(logsEventHandle);
		//业务处理
		inventoryLogsScheduledDomain.businessHandler();

	}

}
