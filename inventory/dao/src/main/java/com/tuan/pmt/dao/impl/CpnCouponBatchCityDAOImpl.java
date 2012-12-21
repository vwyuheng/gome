package com.tuan.pmt.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.pmt.dao.CpnCouponBatchCityDAO;
import com.tuan.pmt.dao.data.CpnCouponBatchCityDO;

public class CpnCouponBatchCityDAOImpl extends SqlMapClientDaoSupport implements CpnCouponBatchCityDAO{

	@Override
	public CpnCouponBatchCityDO queryCpnCouponBatchCityByBatchIdAndCityId(long batchId, long cityId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("batchId", batchId);
		paramMap.put("cityId", cityId);
		Object obj = super.getSqlMapClientTemplate().queryForObject("queryCpnCouponBatchCityByBatchIdAndCityId", paramMap);
		return (CpnCouponBatchCityDO)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CpnCouponBatchCityDO> queryCpnCouponBatchCityByBatchId(long batchId) {
		Object obj = super.getSqlMapClientTemplate().queryForList("queryCpnCouponBatchCityByBatchId", batchId);
		return (List<CpnCouponBatchCityDO>)obj;
	}

}
