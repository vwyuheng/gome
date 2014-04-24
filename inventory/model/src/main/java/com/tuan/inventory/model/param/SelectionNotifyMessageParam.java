package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;

public class SelectionNotifyMessageParam extends TuanBaseDO{
	private static final long serialVersionUID = -2215765968154981320L;
	private static String notifyType = "3";	
	private Long id;  //选型id
	private Long userId;
	private Long goodsId;	// 商品id
	private java.lang.Integer totalNumber; 	// 当前选型总库存
	private java.lang.Integer leftNumber; 	// 当前选型剩余数库存默认值：0 
	private Integer waterfloodVal;  //注水值
	private int limitStorage;	//0:库存无限制；1：限制库存
	private String sales; //销量
	public static String getNotifyType() {
		return notifyType;
	}
	public static void setNotifyType(String notifyType) {
		SelectionNotifyMessageParam.notifyType = notifyType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
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
	public Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	public String getSales() {
		return sales;
	}
	public void setSales(String sales) {
		this.sales = sales;
	}
	
	
	
}
