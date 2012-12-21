package com.tuan.pmt.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.pmt.dao.CpnCouponBatchDAO;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;

public class CpnCouponBatchDAOImpl extends SqlMapClientDaoSupport implements CpnCouponBatchDAO{

	@Override
	public CpnCouponBatchDO queryCpnCouponBatchByBatchId(Long batchId) {
		Object obj = super.getSqlMapClientTemplate().queryForObject("queryCpnCouponBatchByBatchId", batchId);
		return (CpnCouponBatchDO)obj;
	}

	@Override
	public Integer update(CpnCouponBatchDO cpnCouponBatchDO) {
		Object obj = super.getSqlMapClientTemplate().update("updateCpnCouponBatchByBatchId", cpnCouponBatchDO);
		return (Integer)obj;
	}

}
