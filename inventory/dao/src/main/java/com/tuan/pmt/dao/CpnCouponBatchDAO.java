package com.tuan.pmt.dao;

import com.tuan.pmt.dao.data.CpnCouponBatchDO;

public interface CpnCouponBatchDAO {
	/**
	 * 根据批次ID，查询批次对象
	 * @param batchId	批次ID
	 * @return	批次对象
	 */
	public CpnCouponBatchDO queryCpnCouponBatchByBatchId(Long batchId);
	
	public Integer update(CpnCouponBatchDO cpnCouponBatchDO); 
}
