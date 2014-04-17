package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
/**
 * 库存系统myql存储dao
 * @author henry.yu
 * @date 2014/3/6
 */
public interface NullDbInitSelectionRelationDAO {

	 int insertSelectionRelation(GoodsSelectionDO rgsrDo);
	 int updateSelectionRelation(GoodsSelectionDO rgsrDo);
	 
	 int deleteSelectionRelationById(int id);
}

