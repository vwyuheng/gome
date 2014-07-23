package com.tuan.inventory.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.tuan.inventory.dao.LogOfWaterDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
/**
 * 用于处理[插入]日志流水记录的 dao
 * @author henry.yu
 * @date 2014/3/19
 */
public class LogOfWaterDAOImpl extends SqlMapClientDaoSupport implements
		LogOfWaterDAO {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void insertInventoryQueue(final List<GoodsInventoryActionDO> logDOList, final int handleBatch) {
		//super.getSqlMapClientTemplate().insert("insertInventoryQueue", logDOList);
		super.getSqlMapClientTemplate().execute(new SqlMapClientCallback(){

			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				int batch = 0;
				for (GoodsInventoryActionDO actionDO : logDOList) {
					executor.insert("insertInventoryQueue",actionDO);
					batch++;
					// 每handleBatch条批量提交一次。
					if (batch == handleBatch) {
						executor.executeBatch();
						batch = 0;
					}
				}
				if (batch > 0)
					executor.executeBatch();
				return null;
			}
		});
	}

}
