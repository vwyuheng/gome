package com.tuan.pmt.dao;

import java.util.List;

import com.tuan.pmt.dao.data.CpnCouponBatchCityDO;

public interface CpnCouponBatchCityDAO {
	/**
	 * 根据批次ID和城市ID，查询批次城市对象
	 * @param batchId	批次ID
	 * @param cityId	城市ID
	 * @return			批次城市对象
	 */
	public CpnCouponBatchCityDO queryCpnCouponBatchCityByBatchIdAndCityId(long batchId,long cityId);
	
	/**
	 * 根据批次ID，查询批次城市对象列表
	 * @param batchId	批次ID
	 * @return			批次城市对象列表
	 */
	public List<CpnCouponBatchCityDO> queryCpnCouponBatchCityByBatchId(long batchId);
}
