package com.tuan.pmt.dao;

import com.tuan.pmt.dao.data.CpnCouponLogDO;

public interface CpnCouponLogDAO {
	/**
	 * 插入记录，一次插入一条记录
	 * @param cpnCouponLogDO
	 * @return	插入成功返回插入纪录id，否则返回0
	 */
	public Long insert(CpnCouponLogDO cpnCouponLogDO);
}
