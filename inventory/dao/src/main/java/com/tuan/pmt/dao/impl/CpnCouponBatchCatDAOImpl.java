package com.tuan.pmt.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.pmt.dao.CpnCouponBatchCatDAO;
import com.tuan.pmt.dao.data.CpnCouponBatchCatDO;

public class CpnCouponBatchCatDAOImpl extends SqlMapClientDaoSupport implements CpnCouponBatchCatDAO{

	@Override
	public CpnCouponBatchCatDO queryCpnCouponBatchCatByBatchIdAndCatId(long batchId, long catId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("batchId", batchId);
		paramMap.put("catId", catId);
		Object obj = super.getSqlMapClientTemplate().queryForObject("queryCpnCouponBatchCatByBatchIdAndCatId", paramMap);
		return (CpnCouponBatchCatDO)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CpnCouponBatchCatDO> queryCpnCouponBatchCatByBatchId(long batchId) {
		Object obj = super.getSqlMapClientTemplate().queryForList("queryCpnCouponBatchCatByBatchId", batchId);
		return (List<CpnCouponBatchCatDO>)obj;
	}
}
