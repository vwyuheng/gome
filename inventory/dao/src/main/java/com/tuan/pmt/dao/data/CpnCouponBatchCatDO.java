package com.tuan.pmt.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 代金券批次分类表（cpn_coupon_batch_cat）
 * @ClassName: CpnCouponBatchCat
 * @author duandongjun
 * @date 2012-11-15
 */
public class CpnCouponBatchCatDO extends TuanBaseDO{
	/** 序列化标识 */
	private static final long serialVersionUID = 3546428007360198761L;

	/** id  */
	private Long id;
	/** 代金券批次表id  */
	private Long batchId;
	/** 分类id  */
	private Long catId;
	
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
	public Long getCatId() {
		return catId;
	}
	public void setCatId(Long catId) {
		this.catId = catId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
