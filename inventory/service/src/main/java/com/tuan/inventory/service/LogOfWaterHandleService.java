package com.tuan.inventory.service;

import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.result.CallResult;

public interface LogOfWaterHandleService {

	public CallResult<GoodsInventoryActionModel> createLogOfWater(GoodsInventoryActionModel logModel) throws Exception;
}
