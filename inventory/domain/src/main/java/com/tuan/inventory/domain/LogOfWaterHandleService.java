package com.tuan.inventory.domain;

import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.result.CallResult;

public interface LogOfWaterHandleService {

	public CallResult<GoodsInventoryActionModel> createLogOfWater(GoodsInventoryActionModel logModel) throws Exception;
}
