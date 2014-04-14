package com.tuan.inventory.service.impl;

import javax.annotation.Resource;

import com.tuan.inventory.domain.InventoryConfirmScheduledDomain;
import com.tuan.inventory.domain.InventoryLockedScheduledDomain;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
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
		//�����������
		final InventoryConfirmScheduledDomain inventoryConfirmScheduledDomain = new InventoryConfirmScheduledDomain(clientIp, clientName, lm);
		//ע��ִ�����
		inventoryConfirmScheduledDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		//ҵ����
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
		//�����������
		final InventoryLockedScheduledDomain inventoryLockedScheduledDomain = new InventoryLockedScheduledDomain(clientIp, clientName,param, lm);
		//ע��ִ�����
		inventoryLockedScheduledDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		//ҵ����
		inventoryLockedScheduledDomain.businessHandler();

	}

}
