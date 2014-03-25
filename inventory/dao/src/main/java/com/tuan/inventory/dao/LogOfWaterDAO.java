package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;


public interface LogOfWaterDAO {

	void insertInventoryQueue(RedisInventoryLogDO logDO);

}

