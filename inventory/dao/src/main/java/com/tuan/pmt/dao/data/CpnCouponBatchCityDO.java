package com.tuan.pmt.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 代金券批次城市表（cpn_coupon_batch_city）
 * @ClassName: CpnCouponBatchCity
 * @author duandongjun
 * @date 2012-11-15
 */
public class CpnCouponBatchCityDO extends TuanBaseDO{
	/** 序列化标识 */
	private static final long serialVersionUID = -2475680321203081329L;

	/** id  */
	private Long id;
	/** 代金券批次表id  */
	private Long batchId;
	/** 城市id  */
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
