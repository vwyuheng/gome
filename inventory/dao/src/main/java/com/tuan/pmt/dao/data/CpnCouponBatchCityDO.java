package com.tuan.pmt.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * ����ȯ���γ��б�cpn_coupon_batch_city��
 * @ClassName: CpnCouponBatchCity
 * @author duandongjun
 * @date 2012-11-15
 */
public class CpnCouponBatchCityDO extends TuanBaseDO{
	/** ���л���ʶ */
	private static final long serialVersionUID = -2475680321203081329L;

	/** id  */
	private Long id;
	/** ����ȯ���α�id  */
	private Long batchId;
	/** ����id  */
	private Long cityId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public Long getCityId() {
		return cityId;
	}
	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
