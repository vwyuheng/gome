package com.tuan.inventory.domain.repository;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.model.result.CallResult;

public interface LogOfWaterHandleService {

	public CallResult<RedisInventoryLogDO> createLogOfWater(RedisInventoryLogDO logDO) throws Exception;
}
