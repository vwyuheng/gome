package com.tuan.inventory.domain.support.bean.message;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 调整注水值消息对象bean
 * @author henry.yu
 * @date 20140320
 */
public class WaterfloodValAdjustment extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private Long goodsId;// 商品ID(FK)
	private java.lang.Integer waterfloodVal;  //注水值
	
	
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
