package com.tuan.inventory.client;

import com.tuan.core.common.annotation.product.ProductCode;
import com.tuan.core.common.annotation.product.ProductLogLevelEnum;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.result.CallResult;

public interface GoodsInventoryQueryClientService {
	
	/**
	 * 根据商品id获取商品库存信息，
	 * 包含(若有的话)选型的库存及分店的库存
	 * @param clientIp
	 * @param clientName
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	@ProductCode(code = "100001", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsInventoryModel> findGoodsInventoryByGoodsId(final String clientIp, final String clientName, 
			final long goodsId);
	/**
	 * 根据商品基本id获取销量信息
	 * @param clientIp
	 * @param clientName
	 * @param goodsBaseId
	 * @return
	 */
	@ProductCode(code = "100002", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<GoodsBaseModel> findSalesCountByGoodsBaseId(final String clientIp, final String clientName, 
			final long goodsBaseId);
}
