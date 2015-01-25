package com.tuan.inventory.client.impl;

import java.util.List;

import javax.annotation.Resource;

import com.tuan.inventory.client.InventoryCenterFacade;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.WmsIsBeDeliveryModel;
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
import com.tuan.inventory.model.param.WmsInventoryParam;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.wowotrace.trace.model.Message;

public class InventoryCenterFacadeImpl implements InventoryCenterFacade {
	@Resource
	private GoodsInventoryQueryService  goodsInventoryQueryService;
	@Resource
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	
	@Override
	public CallResult<GoodsSelectionModel> queryGoodsSelection(String clientIp,
			String clientName, long goodsId, long selectionId) {
		return goodsInventoryQueryService.findGoodsSelectionBySelectionId(clientIp, clientName, goodsId, selectionId);
	}
	@Override
	public CallResult<GoodsSuppliersModel> queryGoodsSuppliers(String clientIp,
			String clientName, long goodsId, long suppliersId) {
		return goodsInventoryQueryService.findGoodsSuppliersBySuppliersId(clientIp, clientName, goodsId, suppliersId);
	}
	@Override
	public CallResult<GoodsInventoryModel> queryGoodsInventory(String clientIp,
			String clientName, long goodsId) {
		return goodsInventoryQueryService.findGoodsInventoryByGoodsId(clientIp, clientName, goodsId);
	}
	@Override
	public CallResult<List<GoodsSelectionModel>> queryGoodsSelectionList(
			String clientIp, String clientName, long goodsId) {
		return goodsInventoryQueryService.findGoodsSelectionListByGoodsId(clientIp, clientName, goodsId);
	}
	@Override
	public CallResult<List<GoodsSelectionModel>> queryGoodsSelectionListBySelectionIdList(
			String clientIp, String clientName, long goodsId,
			List<Long> selectionIdList) {
		return goodsInventoryQueryService.findGoodsSelectionListBySelectionIdList(clientIp, clientName, goodsId, selectionIdList);
	}
	@Override
	public CallResult<List<GoodsSuppliersModel>> queryGoodsSuppliersListByGoodsId(
			String clientIp, String clientName, long goodsId) {
		return goodsInventoryQueryService.findGoodsSuppliersListByGoodsId(clientIp, clientName, goodsId);
	}
	@Override
	public CallResult<List<GoodsSuppliersModel>> queryGoodsSuppliersListBySuppliersIdList(
			String clientIp, String clientName, long goodsId,
			List<Long> suppliersIdList) {
		return goodsInventoryQueryService.findGoodsSuppliersListBySuppliersIdList(clientIp, clientName, goodsId, suppliersIdList);
	}
	@Override
	public CallResult<WmsIsBeDeliveryModel> queryWmsIsBeDeliveryByWmsGoodsId(
			String clientIp, String clientName, String wmsGoodsId,
			String isBeDelivery) {
		return goodsInventoryQueryService.findWmsIsBeDeliveryByWmsGoodsId(clientIp, clientName, wmsGoodsId, isBeDelivery);
	}
	@Override
	public CallResult<GoodsBaseModel> querySalesCountByGoodsBaseId(
			String clientIp, String clientName, long goodsBaseId) {
		return goodsInventoryQueryService.findSalesCountByGoodsBaseId(clientIp, clientName, goodsBaseId);
	}
	
	@Override
	public InventoryCallResult createInventory(String clientIp,
			String clientName, CreaterInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdateService.createInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult updateInventory(String clientIp,
			String clientName, UpdateInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdateService.updateInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult callbackAckInventory(String clientIp,
			String clientName, CallbackParam param, Message traceMessage) {
		return goodsInventoryUpdateService.callbackAckInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult adjustmentInventory(String clientIp,
			String clientName, AdjustInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdateService.adjustmentInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult adjustmentWaterflood(String clientIp,
			String clientName, AdjustWaterfloodParam param, Message traceMessage) {
		return goodsInventoryUpdateService.adjustmentWaterflood(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult createWmsInventory(String clientIp,
			String clientName, WmsInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdateService.createWmsInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult adjustWmsInventory(String clientIp,
			String clientName, WmsInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdateService.adjustWmsInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult overrideAdjustInventory(String clientIp,
			String clientName, OverrideAdjustInventoryParam param,
			Message traceMessage) {
		return goodsInventoryUpdateService.overrideAdjustInventory(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult updateWmsData(String clientIp,
			String clientName, UpdateWmsDataParam param, Message traceMessage) {
		return goodsInventoryUpdateService.updateWmsData(clientIp, clientName, param, traceMessage);
	}
	
	@Override
	public InventoryCallResult createInventory4GoodsCost(String clientIp,
			String clientName, CreateInventory4GoodsCostParam param, Message traceMessage) {
		return goodsInventoryUpdateService.createInventory4GoodsCost(clientIp, clientName, param, traceMessage);
	}
	@Override
	public InventoryCallResult restoreInventory(String clientIp,
			String clientName, RestoreInventoryParam param, Message traceMessage) {
		return goodsInventoryUpdateService.restoreInventory(clientIp, clientName, param, traceMessage);
		}
	@Override
	public InventoryCallResult incrGoodsSaleCount(String clientIp,
			String clientName, UpdateLotteryInventoryParam param,
			Message traceMessage) {
		return goodsInventoryUpdateService.incrGoodsSaleCount(clientIp, clientName, param, traceMessage);
	}
	
	
	public GoodsInventoryQueryService getGoodsInventoryQueryService() {
		return goodsInventoryQueryService;
	}
	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}
	
	public GoodsInventoryUpdateService getGoodsInventoryUpdateService() {
		return goodsInventoryUpdateService;
	}
	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}
	@Override
	public void test() {
		this.goodsInventoryQueryService.test();
		
	}
	
	
	

}
