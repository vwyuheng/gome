package com.tuan.inventory.domain.support.bean.message;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * ����עˮֵ��Ϣ����bean
 * @author henry.yu
 * @date 20140320
 */
public class WaterfloodValAdjustment extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private Long goodsId;// ��ƷID(FK)
	private java.lang.Integer waterfloodVal;  //עˮֵ
	
	
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public java.lang.Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(java.lang.Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	
	
	
}
