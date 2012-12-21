package com.tuan.pmt.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tuan.pmt.dao.CpnCouponLogDAO;
import com.tuan.pmt.dao.data.CpnCouponLogDO;

public class CpnCouponLogDAOImpl extends SqlMapClientDaoSupport implements CpnCouponLogDAO{

	@Override
	public Long insert(CpnCouponLogDO cpnCouponLogDO) {
		Object obj = super.getSqlMapClientTemplate().insert("insertCpnCouponLog", cpnCouponLogDO);
		return (Long)obj;
	}

}
