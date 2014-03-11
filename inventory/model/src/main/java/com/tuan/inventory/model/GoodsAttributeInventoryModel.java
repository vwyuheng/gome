package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;

public class GoodsAttributeInventoryModel extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private java.lang.Integer goodsId;// 商品ID(FK)
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	
	
	public java.lang.Integer getGoodsId() {
		return goodsId;
	}
	
	public void setGoodsId(java.lang.Integer goodsId) {
		this.goodsId = goodsId;
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
	
}
