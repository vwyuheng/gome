package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.LogOfWaterDAO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
/**
 * ���ڴ���[����]��־��ˮ��¼�� dao
 * @author henry.yu
 * @date 2014/3/19
 */
public class LogOfWaterDAOImpl extends SqlMapClientDaoSupport implements
		LogOfWaterDAO {

	@Override
	public int insertSelectionRelation(RedisGoodsSelectionRelationDO rgsrDo) {
		// TODO Auto-generated method stub
		return 0;
	}

}
