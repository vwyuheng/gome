package com.tuan.inventory.service;

import java.util.List;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.WmsIsBeDeliveryModel;
import com.tuan.inventory.model.result.CallResult;

public interface GoodsInventoryQueryService {
	/**
	 * 根据选型id获取选型库存信息
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param selectionId
	 * @return
	 */
	@ProductCode(code = "00001", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsSelectionModel> findGoodsSelectionBySelectionId(final String clientIp, final String clientName, final long goodsId,
			final long selectionId);
	/**
	 * 根据商品分店id获取分店库存
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param suppliersId
	 * @return
	 */
	@ProductCode(code = "00002", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsSuppliersModel> findGoodsSuppliersBySuppliersId(final String clientIp, final String clientName, final long goodsId,
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
	@ProductCode(code = "00003", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsInventoryModel> findGoodsInventoryByGoodsId(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * 根据商品id获取商品选型库存列表
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	@ProductCode(code = "00004", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<List<GoodsSelectionModel>> findGoodsSelectionListByGoodsId(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * 根据选型id 列表获取选型列表
	 * @param clientIp
	 * @param clientName
	 * @param goodsId
	 * @param selectionIdList
	 * @return
	 */
	@ProductCode(code = "00005", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<List<GoodsSelectionModel>> findGoodsSelectionListBySelectionIdList(final String clientIp, final String clientName, 
			final long goodsId,final List<Long> selectionIdList);
	/**
	 * 根据商品id获取商品分店库存列表
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	@ProductCode(code = "00006", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<List<GoodsSuppliersModel>> findGoodsSuppliersListByGoodsId(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * 根据分店id list获取分店列表
	 * @param clientIp
	 * @param clientName
	 * @param goodsId
	 * @param suppliersIdList
	 * @return
	 */
	@ProductCode(code = "00007", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<List<GoodsSuppliersModel>> findGoodsSuppliersListBySuppliersIdList(final String clientIp, final String clientName, 
			final long goodsId,final List<Long> suppliersIdList);
	/***
	 * 根据物流码查询发货仓库信息
	 * @param clientIp
	 * @param clientName
	 * @param goodsId
	 * @return
	 */
	@ProductCode(code = "00008", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<WmsIsBeDeliveryModel> findWmsIsBeDeliveryByWmsGoodsId(final String clientIp, final String clientName, 
			final String wmsGoodsId,final String isBeDelivery);
	
	/**
	 * 根据商品基本id获取销量信息
	 * @param clientIp
	 * @param clientName
	 * @param goodsBaseId
	 * @return
	 */
	@ProductCode(code = "00009", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsBaseModel> findSalesCountByGoodsBaseId(final String clientIp, final String clientName, 
			final String goodsBaseId);
}
