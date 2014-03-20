package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;


public interface LogOfWaterDAO {

	 int insertSelectionRelation(RedisGoodsSelectionRelationDO rgsrDo);

}

