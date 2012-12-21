package com.tuan.pmt.dao;

import com.tuan.pmt.dao.data.CpnCouponBatchDO;

public interface CpnCouponBatchDAO {
	/**
	 * ��������ID����ѯ���ζ���
	 * @param batchId	����ID
	 * @return	���ζ���
	 */
	public CpnCouponBatchDO queryCpnCouponBatchByBatchId(Long batchId);
	
	public Integer update(CpnCouponBatchDO cpnCouponBatchDO); 
}
