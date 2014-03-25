package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.LogOfWaterDAO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
/**
 * ���ڴ���[����]��־��ˮ��¼�� dao
 * @author henry.yu
 * @date 2014/3/19
 */
public class LogOfWaterDAOImpl extends SqlMapClientDaoSupport implements
		LogOfWaterDAO {

	@Override
	public void insertInventoryQueue(RedisInventoryLogDO logDO) {
		super.getSqlMapClientTemplate().insert("insertInventoryQueue", logDO);
		//return (Long) objResult;
	}

}
