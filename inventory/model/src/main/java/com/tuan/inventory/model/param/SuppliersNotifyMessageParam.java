package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;

public class SuppliersNotifyMessageParam extends TuanBaseDO{
	private static final long serialVersionUID = -2215765968154981320L;
	private static String notifyType = "3";	
	private Long  id;
	private Long goodsId;// 商品ID(FK)
	private Long userId;
	private java.lang.Integer totalNumber;// 当前分店总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前分店剩余数库存默认值:0
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	private Integer waterfloodVal;  //注水值
	//private String sales; //销量
	public static String getNotifyType() {
		return notifyType;
	}
	public static void setNotifyType(String notifyType) {
		SuppliersNotifyMessageParam.notifyType = notifyType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	
}
