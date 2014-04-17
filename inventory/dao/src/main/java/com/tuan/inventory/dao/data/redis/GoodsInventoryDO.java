package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存主体信息
 * @author henry.yu
 * @date 20140310
 */
public class GoodsInventoryDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private Long goodsId;// 商品ID(FK)
	private Long userId;// 商品ID(FK)
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	private java.lang.Integer waterfloodVal;  //注水值
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public java.lang.Integer getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(java.lang.Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	public java.lang.Integer getLeftNumber() {
		return leftNumber;
	}
	public void setLeftNumber(java.lang.Integer leftNumber) {
		this.leftNumber = leftNumber;
	}
	public java.lang.Integer getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(java.lang.Integer limitStorage) {
		this.limitStorage = limitStorage;
	}
	public java.lang.Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(java.lang.Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	
	
	
	
	
}


