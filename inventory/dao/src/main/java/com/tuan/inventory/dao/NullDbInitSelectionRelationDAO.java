package com.tuan.inventory.dao;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
/**
 * ���ϵͳmyql�洢dao
 * @author henry.yu
 * @date 2014/3/6
 */
public interface NullDbInitSelectionRelationDAO {

	 int insertSelectionRelation(RedisGoodsSelectionRelationDO rgsrDo);
	 int updateSelectionRelation(RedisGoodsSelectionRelationDO rgsrDo);
	 
	 int deleteSelectionRelationById(int id);
}

