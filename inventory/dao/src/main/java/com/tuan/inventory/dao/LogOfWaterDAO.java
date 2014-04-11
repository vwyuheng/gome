package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;


public interface LogOfWaterDAO {

	void insertInventoryQueue(GoodsInventoryActionDO logDO);

}

