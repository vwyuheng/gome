package com.tuan.inventory.client;

import java.util.List;

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
import com.wowotrace.trace.model.Message;

public interface InventoryCenterFacade {
	
	/**
	 * 根据选型id获取选型库存信息
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param selectionId
	 * @return
	 */
	public CallResult<GoodsSelectionModel> queryGoodsSelection(final String clientIp, final String clientName, final long goodsId,
			final long selectionId);
	/**
	 * 根据商品分店id获取分店库存
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param suppliersId
	 * @return
	 */
	public CallResult<GoodsSuppliersModel> queryGoodsSuppliers(final String clientIp, final String clientName, final long goodsId,
			final long suppliersId);
	/**
	 * 根据商品id获取商品库存信息，
	 * 包含(若有的话)选型的库存及分店的库存
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public CallResult<GoodsInventoryModel> queryGoodsInventory(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * 根据商品id获取商品选型库存列表
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public CallResult<List<GoodsSelectionModel>> queryGoodsSelectionList(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * 根据选型id 列表获取选型列表
	 * @param clientIp
	 * @param clientName
	 * @param goodsId
	 * @param selectionIdList
	 * @return
	 */
	public CallResult<List<GoodsSelectionModel>> queryGoodsSelectionListBySelectionIdList(final String clientIp, final String clientName, 
			final long goodsId,final List<Long> selectionIdList);
	/**
	 * 根据商品id获取商品分店库存列表
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public CallResult<List<GoodsSuppliersModel>> queryGoodsSuppliersListByGoodsId(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * 根据分店id list获取分店列表
	 * @param clientIp
	 * @param clientName
	 * @param goodsId
	 * @param suppliersIdList
	 * @return
	 */
	public CallResult<List<GoodsSuppliersModel>> queryGoodsSuppliersListBySuppliersIdList(final String clientIp, final String clientName, 
			final long goodsId,final List<Long> suppliersIdList);
	/***
	 * 根据物流码查询发货仓库信息
	 * @param clientIp
	 * @param clientName
	 * @param goodsId
	 * @return
	 */
	public CallResult<WmsIsBeDeliveryModel> queryWmsIsBeDeliveryByWmsGoodsId(final String clientIp, final String clientName, 
			final String wmsGoodsId,final String isBeDelivery);
	/**
	 * 根据商品基本id获取销量信息
	 * @param clientIp
	 * @param clientName
	 * @param goodsBaseId
	 * @return
	 */
	public CallResult<GoodsBaseModel> querySalesCountByGoodsBaseId(final String clientIp, final String clientName, 
			final String goodsBaseId);
	/**
	 * 回调确认过的队列的消费接口
	 * @param clientIp
	 * @param clientName
	 * @param traceMessage
	 */
	public void confirmQueueConsume(String clientIp,
			String clientName,Message traceMessage);
	/**
	 * 被锁定队列的消费接口
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 */
	public void lockedQueueConsume(String clientIp,
			String clientName,InventoryScheduledParam param,Message traceMessage);
	/**
	 * 日志队列的消费接口
	 * @param clientIp
	 * @param clientName
	 * @param traceMessage
	 */
	public void logsQueueConsume(String clientIp,
			String clientName,Message traceMessage);
	/** 
	 * 创建库存接口，根据传入的参数创建库存
	 * @param clientIp String 客户端ip地址
	 * @param clientName String 客户端名称
	 * @param param UpdateInventoryParam 创建库存所需参数类对象
	 * @return InventoryCallResult 接口调用结果对象
	 */
	public InventoryCallResult createInventory(final String clientIp, final String clientName
			,final CreaterInventoryParam param,Message traceMessage);
	/**
	 * 更新商品库存
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	public InventoryCallResult updateInventory(
			final String clientIp, final String clientName,final UpdateInventoryParam param,Message traceMessage);
	/***
	 * 库存扣减完成后，回调确认接口
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	public InventoryCallResult callbackAckInventory(
			final String clientIp, final String clientName,final CallbackParam param,Message traceMessage);
	/**
	 * 手工调整库存
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	public InventoryCallResult adjustmentInventory(
			final String clientIp, final String clientName,final AdjustInventoryParam param,Message traceMessage);
	/**
	 * 手工调整注水值
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	public InventoryCallResult adjustmentWaterflood(
			final String clientIp, final String clientName,final AdjustWaterfloodParam param,Message traceMessage);
	/**
	 * 创建物流库存接口,根据传入的参数创建物流相关库存
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	public InventoryCallResult createWmsInventory(final String clientIp, final String clientName
			,final WmsInventoryParam param,Message traceMessage);
	/**
	 * 调整物流库存：增量调整
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	public InventoryCallResult adjustWmsInventory(
			final String clientIp, final String clientName,final WmsInventoryParam param,Message traceMessage);
	/**
	 * 库存调整：全量调整
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	public InventoryCallResult overrideAdjustInventory(
			final String clientIp, final String clientName,final OverrideAdjustInventoryParam param,Message traceMessage);
	/**
	 * 物流关系数据更新接口
	 * @param clientIp
	 * @param clientName
	 * @param param
	 * @param traceMessage
	 * @return
	 */
	public InventoryCallResult updateWmsData(
			final String clientIp, final String clientName,final UpdateWmsDataParam param,Message traceMessage);
}
