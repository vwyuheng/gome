package com.tuan.pmt.dao;

import java.util.List;

import com.tuan.pmt.dao.data.CpnCouponBatchCatDO;

public interface CpnCouponBatchCatDAO {
	/**
	 * ��������ID�ͷ���ID����ѯ���η������
	 * @param batchId	����ID
	 * @param catId		����ID
	 * @return			���η������
	 */
	public CpnCouponBatchCatDO queryCpnCouponBatchCatByBatchIdAndCatId(long batchId,long catId);
	
	/**
	 * ��������ID����ѯ���η�������б�
	 * @param batchId	����ID
	 * @return			���η�������б�
	 */
	public List<CpnCouponBatchCatDO> queryCpnCouponBatchCatByBatchId(long batchId);
}
