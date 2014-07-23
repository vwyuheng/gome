package com.tuan.inventory.domain;

import java.util.List;

import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.result.CallResult;

public interface LogOfWaterHandleService {

	public CallResult<List<GoodsInventoryActionModel>> createLogOfWater(List<GoodsInventoryActionModel> logList) throws Exception;
}
