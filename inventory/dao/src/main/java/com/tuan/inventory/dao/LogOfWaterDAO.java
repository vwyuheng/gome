package com.tuan.inventory.dao;

import java.util.List;

import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;


public interface LogOfWaterDAO {

	void insertInventoryQueue(List<GoodsInventoryActionDO> logDOList, int handleBatch);

}

