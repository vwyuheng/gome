package com.tuan.inventory.domain.job.bean;

import java.io.Serializable;

public class NotifyMessage implements Serializable {

	private static final long serialVersionUID = 2235735587333702996L;

	private static String notifyType = "3";	
	private Long userId;
	private Long goodsId;
	private Long orderId;
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	private String variableQuantityJsonData;// 库存变化量 商品主体信息中的库存变化量、选型商品或分店则其分别对应的库存变化保存在json数据中
	private java.lang.Integer waterfloodVal;  //注水值
	
	public String getSendMsg() {
		StringBuffer msg = new StringBuffer();
		msg.append("userId=").append(getUserId()).append("|");
		msg.append("type=").append(notifyType).append("|");
		msg.append("goodsId=").append(getGoodsId()).append("|");
		msg.append("orderId=").append(getOrderId()).append("|");
		msg.append("limitStorage=").append(getLimitStorage()).append("|");
		msg.append("variableQuantityJsonData=").append(getVariableQuantityJsonData()).append("|");
		msg.append("waterfloodVal=").append(getWaterfloodVal());
		return msg.toString();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public java.lang.Integer getLimitStorage() {
		return limitStorage;
	}

	public void setLimitStorage(java.lang.Integer limitStorage) {
		this.limitStorage = limitStorage;
	}

	public String getVariableQuantityJsonData() {
		return variableQuantityJsonData;
	}

	public void setVariableQuantityJsonData(String variableQuantityJsonData) {
		this.variableQuantityJsonData = variableQuantityJsonData;
	}

	public java.lang.Integer getWaterfloodVal() {
		return waterfloodVal;
	}

	public void setWaterfloodVal(java.lang.Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}

	

}
