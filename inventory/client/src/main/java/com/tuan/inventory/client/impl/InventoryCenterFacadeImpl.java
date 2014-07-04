package com.tuan.inventory.client.impl;

import java.util.List;

import com.tuan.inventory.client.InventoryCenterFacade;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.WmsIsBeDeliveryModel;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;
import com.tuan.inventory.model.param.CallbackParam;
import com.tuan.inventory.model.param.CreaterInventoryParam;
import com.tuan.inventory.model.param.InventoryScheduledParam;
import com.tuan.inventory.model.param.OverrideAdjustInventoryParam;
import com.tuan.inventory.model.param.UpdateInventoryParam;
import com.tuan.inventory.model.param.UpdateWmsDataParam;
import com.tuan.inventory.model.param.WmsInventoryParam;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.service.GoodsInventoryScheduledService;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.wowotrace.trace.model.Message;

public class InventoryCenterFacadeImpl implements InventoryCenterFacade {
	private GoodsInventoryQueryService  goodsInventoryQuery;
	private GoodsInventoryUpdateService goodsInventoryUpdate;
	private GoodsInventoryScheduledService goodsInventoryScheduled;
	@Override
	public CallResult<GoodsSelectionModel> queryGoodsSelection(String clientIp,
			String clientName, long goodsId, long selectionId) {
		return goodsInventoryQuery.findGoodsSelectionBySelectionId(clientIp, clientName, goodsId, selectionId);
	}
	@Override
	public CallResult<GoodsSuppliersModel> queryGoodsSuppliers(String clientIp,
			String clientName, long goodsId, long suppliersId) {
		return goodsInventoryQuery.findGoodsSuppliersBySuppliersId(clientIp, clientName, goodsId, suppliersId);
	}
	@Override
	public CallResult<GoodsInventoryModel> queryGoodsInventory(String clientIp,
			String clientName, long goodsId) {
		return goodsInventoryQuery.findGoodsInventoryByGoodsId(clientIp, clientName, goodsId);
	}
	@Override
	public CallResult<List<GoodsSelectionModel>> queryGoodsSelectionList(
			String clientIp, String clientName, long goodsId) {
		return goodsInventoryQuery.findGoodsSelectionListByGoodsId(clientIp, clientName, goodsId);
	}
	@Override
	public CallResult<List<GoodsSelectionModel>> queryGoodsSelectionListBySelectionIdList(
			String clientIp, String clientName, long goodsId,
			List<Long> selectionIdList) {
		return goodsInventoryQuery.findGoodsSelectionListBySelectionIdList(clientIp, clientName, goodsId, selectionIdList);
	}
	@Override
	public CallResult<List<GoodsSuppliersModel>> queryGoodsSuppliersListByGoodsId(
			String clientIp, String clientName, long goodsId) {
		return goodsInventoryQuery.findGoodsSuppliersListByGoodsId(clientIp, clientName, goodsId);
	}
	@Override
	public CallResult<List<GoodsSuppliersModel>> queryGoodsSuppliersListBySuppliersIdList(
			String clientIp, String clientName, long goodsId,
			List<Long> suppliersIdList) {
		return goodsInventoryQuery.findGoodsSuppliersListBySuppliersIdList(clientIp, clientName, goodsId, suppliersIdList);
	}
	@Override
	public CallResult<WmsIsBeDeliveryModel> queryWmsIsBeDeliveryByWmsGoodsId(
			String clientIp, String clientName, String wmsGoodsId,
			String isBeDelivery) {
		return goodsInventoryQuery.findWmsIsBeDeliveryByWmsGoodsId(clientIp, clientName, wmsGoodsId, isBeDelivery);
	}
	@Override
	public CallResult<GoodsBaseModel> querySalesCountByGoodsBaseId(
			String clientIp, String clientName, String goodsBaseId) {
		return goodsInventoryQuery.findSalesCountByGoodsBaseId(clientIp, clientName, goodsBaseId);
	}
	@Override
	public void confirmQueueConsume(String clientIp, String clientName,
			Message traceMessage) {
		goodsInventoryScheduled.confirmQueueConsume(clientIp, clientName, traceMessage);
	}
	@Override
	public void lockedQueueConsume(String clientIp, String clientName,
			InventoryScheduledParam param, Message traceMessage) {
		goodsInventoryScheduled.lockedQueueConsume(clientIp, clientName, param, traceMessage);		
	}
	@Override
	public void logsQueueConsume(String clientIp, String clientName,
			Message traceMessage) {
		goodsInventoryScheduled.logsQueueConsume(clientIp, clientName, traceMessage)	;	
	}
	@Override
	public InventoryCallResult createInventory(String clientIp,
			String clientName, CreaterInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdate.createInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult updateInventory(String clientIp,
			String clientName, UpdateInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdate.updateInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult callbackAckInventory(String clientIp,
			String clientName, CallbackParam param, Message traceMessage) {
		return goodsInventoryUpdate.callbackAckInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult adjustmentInventory(String clientIp,
			String clientName, AdjustInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdate.adjustmentInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult adjustmentWaterflood(String clientIp,
			String clientName, AdjustWaterfloodParam param, Message traceMessage) {
		return goodsInventoryUpdate.adjustmentWaterflood(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult createWmsInventory(String clientIp,
			String clientName, WmsInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdate.createWmsInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult adjustWmsInventory(String clientIp,
			String clientName, WmsInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdate.adjustWmsInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult overrideAdjustInventory(String clientIp,
			String clientName, OverrideAdjustInventoryParam param,
			Message traceMessage) {
		return goodsInventoryUpdate.overrideAdjustInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult updateWmsData(String clientIp,
			String clientName, UpdateWmsDataParam param, Message traceMessage) {
		return goodsInventoryUpdate.updateWmsData(clientIp, clientName, param, traceMessage);
	}
	public GoodsInventoryQueryService getGoodsInventoryQuery() {
		return goodsInventoryQuery;
	}
	public void setGoodsInventoryQuery(
			GoodsInventoryQueryService goodsInventoryQuery) {
		this.goodsInventoryQuery = goodsInventoryQuery;
	}
	public GoodsInventoryUpdateService getGoodsInventoryUpdate() {
		return goodsInventoryUpdate;
	}
	public void setGoodsInventoryUpdate(
			GoodsInventoryUpdateService goodsInventoryUpdate) {
		this.goodsInventoryUpdate = goodsInventoryUpdate;
	}
	public GoodsInventoryScheduledService getGoodsInventoryScheduled() {
		return goodsInventoryScheduled;
	}
	public void setGoodsInventoryScheduled(
			GoodsInventoryScheduledService goodsInventoryScheduled) {
		this.goodsInventoryScheduled = goodsInventoryScheduled;
	}

}
