package com.tuan.inventory.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.inventory.dao.LogOfWaterDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
/**
 * 用于处理[插入]日志流水记录的 dao
 * @author henry.yu
 * @date 2014/3/19
 */
public class LogOfWaterDAOImpl extends SqlMapClientDaoSupport implements
		LogOfWaterDAO {

	@Override
	public void insertInventoryQueue(GoodsInventoryActionDO logDO) {
		super.getSqlMapClientTemplate().insert("insertInventoryQueue", logDO);
		//return (Long) objResult;
	}

}
