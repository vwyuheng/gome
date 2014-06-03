package com.tuan.inventory.service.impl;

import java.lang.reflect.Type;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.core.common.service.TuanServiceTemplateImpl;
import com.tuan.inventory.domain.InventoryUpdateByNotifyMessageDomain;
import com.tuan.inventory.domain.repository.GoodUpdateNumberDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.notifyserver.core.cclient.ConsumerReceiver;
import com.tuan.notifyserver.core.connect.net.pojo.Message;

public class InventoryUpdateCallBackServiceImpl extends TuanServiceTemplateImpl
		implements ConsumerReceiver {
	@Resource
	private GoodUpdateNumberDomainRepository goodUpdateNumberDomainRepository;
	private final Log log = LogFactory.getLog("COMPENSATION.NOTIFY.LOG");
	private final String method = "InventoryUpdateCallBackServiceImpl.receive";

	@Override
	public boolean receive(Message message) {
		final LogModel lm = LogModel.newLogModel();
		if (null == message) {
			log.info(lm.setMethod(method).addMetaData("resultCode", "参数无效")
					.toJson());
			return false;
		}
		log.info(lm.setMethod(method)
				.addMetaData("message", message.getContent()).toJson());
		InventoryNotifyMessageParam inventoryNotifyMessageParam = null;
		try {
			Type paramType = new TypeToken<InventoryNotifyMessageParam>() {
			}.getType();
			inventoryNotifyMessageParam = new Gson().fromJson(
					message.getContent(), paramType);
		} catch (JsonSyntaxException e) {
			log.info(lm.setMethod(method).addMetaData("resultCode", "数据格式错误")
					.toJson());
			return false;
		}
		final InventoryUpdateByNotifyMessageDomain inventoryUpdateByNotifyMessageDomain = new InventoryUpdateByNotifyMessageDomain(
				inventoryNotifyMessageParam, lm);
		inventoryUpdateByNotifyMessageDomain
				.setGoodUpdateNumberDomainRepository(goodUpdateNumberDomainRepository);

		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						return inventoryUpdateByNotifyMessageDomain.reWrite();
					}

					public TuanCallbackResult executeCheck() {
						return TuanCallbackResult.success();
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		log.info(lm.setMethod(method).addMetaData("resultCode", resultCode).toJson());
		if (resultCode == 0) {
			return true;
		}
		return false;
	}

	public GoodUpdateNumberDomainRepository getGoodUpdateNumberDomainRepository() {
		return goodUpdateNumberDomainRepository;
	}

	public void setGoodUpdateNumberDomainRepository(
			GoodUpdateNumberDomainRepository goodUpdateNumberDomainRepository) {
		this.goodUpdateNumberDomainRepository = goodUpdateNumberDomainRepository;
	}
}