package com.tuan.pmt.dao;

import java.util.List;

import com.tuan.pmt.dao.data.CpnCouponBatchCityDO;

public interface CpnCouponBatchCityDAO {
	/**
	 * ��������ID�ͳ���ID����ѯ���γ��ж���
	 * @param batchId	����ID
	 * @param cityId	����ID
	 * @return			���γ��ж���
	 */
	public CpnCouponBatchCityDO queryCpnCouponBatchCityByBatchIdAndCityId(long batchId,long cityId);
	
	/**
	 * ��������ID����ѯ���γ��ж����б�
	 * @param batchId	����ID
	 * @return			���γ��ж����б�
	 */
	public List<CpnCouponBatchCityDO> queryCpnCouponBatchCityByBatchId(long batchId);
}
