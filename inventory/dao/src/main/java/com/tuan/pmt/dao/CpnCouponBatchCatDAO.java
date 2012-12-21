package com.tuan.pmt.dao;

import java.util.List;

import com.tuan.pmt.dao.data.CpnCouponBatchCatDO;

public interface CpnCouponBatchCatDAO {
	/**
	 * 根据批次ID和分类ID，查询批次分类对象
	 * @param batchId	批次ID
	 * @param catId		分类ID
	 * @return			批次分类对象
	 */
	public CpnCouponBatchCatDO queryCpnCouponBatchCatByBatchIdAndCatId(long batchId,long catId);
	
	/**
	 * 根据批次ID，查询批次分类对象列表
	 * @param batchId	批次ID
	 * @return			批次分类对象列表
	 */
	public List<CpnCouponBatchCatDO> queryCpnCouponBatchCatByBatchId(long batchId);
}
