package com.tuan.inventory.service.impl;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Resource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tuan.inventory.dao.data.GoodsUpdateNumberDO;
import com.tuan.inventory.domain.repository.GoodUpdateNumberDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.SelectionNotifyMessageParam;
import com.tuan.inventory.model.param.SuppliersNotifyMessageParam;
import com.tuan.notifyserver.core.cclient.ConsumerReceiver;
import com.tuan.notifyserver.core.connect.net.pojo.Message;

public class InventoryUpdateCallBackServiceImpl extends AbstractService implements ConsumerReceiver {
	@Resource
	private GoodUpdateNumberDomainRepository goodUpdateNumberDomainRepository;
	
	@Override
	public boolean receive(Message message) {
		String method = "InventoryUpdateCallBackServiceImpl.receive";
		final LogModel lm = LogModel.newLogModel();
		if(null == message){
			writeSysLog(lm.setMethod(method).addMetaData("resultCode", "参数无效").toJson());
			return false;
		}
		InventoryNotifyMessageParam inventoryNotifyMessageParam=null;
		try {
			Type paramType = new TypeToken<InventoryNotifyMessageParam>(){}.getType();
			inventoryNotifyMessageParam = new Gson().fromJson(message.getContent(),
					paramType);
		} catch (JsonSyntaxException e) {
			writeSysLog(lm.setMethod(method).addMetaData("resultCode", "数据格式错误").toJson());
			return false;
		}
		//库存调整
		int totalNumber = inventoryNotifyMessageParam.getTotalNumber();
		int leftNumber = inventoryNotifyMessageParam.getLeftNumber();
		GoodsUpdateNumberDO goodsUpdateNumberDO=new GoodsUpdateNumberDO();
		goodsUpdateNumberDO.setLeftNum(leftNumber);
		goodsUpdateNumberDO.setTotalNum(totalNumber);
		goodsUpdateNumberDO.setId(inventoryNotifyMessageParam.getGoodsId());
		goodUpdateNumberDomainRepository.updateGoodsAttributesNumber(goodsUpdateNumberDO);
		List<SelectionNotifyMessageParam> selectionRelation =inventoryNotifyMessageParam.getSelectionRelation();
		if(null!=selectionRelation){
			for (SelectionNotifyMessageParam selectionNotifyMessageParam : selectionRelation) {
				goodsUpdateNumberDO.setLeftNum(selectionNotifyMessageParam.getLeftNumber());
				goodsUpdateNumberDO.setTotalNum(selectionNotifyMessageParam.getTotalNumber());
				goodsUpdateNumberDO.setId(selectionNotifyMessageParam.getId());
				goodUpdateNumberDomainRepository.updateSelectionRelationNumber(goodsUpdateNumberDO);
				String wmsGoodsId=selectionNotifyMessageParam.getWmsGoodsId();
				if(null!=wmsGoodsId){
					Long wmsId=Long.parseLong(wmsGoodsId);
					if(wmsId>0){
					goodsUpdateNumberDO.setId(wmsId);
					goodUpdateNumberDomainRepository.updataGoodsWmsNumByID(goodsUpdateNumberDO);
					}
				}
			}
		}
		List<SuppliersNotifyMessageParam> suppliersRelation =inventoryNotifyMessageParam.getSuppliersRelation();
		if(null!=suppliersRelation){
			for (SuppliersNotifyMessageParam suppliersNotifyMessageParam : suppliersRelation) {
				goodsUpdateNumberDO.setLeftNum(suppliersNotifyMessageParam.getLeftNumber());
				goodsUpdateNumberDO.setTotalNum(suppliersNotifyMessageParam.getTotalNumber());
				goodsUpdateNumberDO.setId(suppliersNotifyMessageParam.getId());
				goodUpdateNumberDomainRepository.updateSuppliersInventoryNumber(goodsUpdateNumberDO);
			}
		}
		return true;
	}

	public GoodUpdateNumberDomainRepository getGoodUpdateNumberDomainRepository() {
		return goodUpdateNumberDomainRepository;
	}

	public void setGoodUpdateNumberDomainRepository(
			GoodUpdateNumberDomainRepository goodUpdateNumberDomainRepository) {
		this.goodUpdateNumberDomainRepository = goodUpdateNumberDomainRepository;
	}
	
	
}
