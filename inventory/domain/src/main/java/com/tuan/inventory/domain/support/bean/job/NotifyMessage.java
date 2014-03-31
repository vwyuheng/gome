package com.tuan.inventory.domain.support.bean.job;

import java.io.Serializable;
import java.util.List;

import com.tuan.inventory.model.OrderGoodsSelectionModel;

public class NotifyMessage implements Serializable {

	private static final long serialVersionUID = 2235735587333702996L;

	private static String notifyType = "3";	
	private Long userId;
	private Long goodsId;
	private Long orderId;
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	//private String variableQuantityJsonData;// 库存变化量 商品主体信息中的库存变化量、选型商品或分店则其分别对应的库存变化保存在json数据中
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private java.lang.Integer waterfloodVal;  //注水值
	//包括商品选型库存和商品分店库存信息
	private List<OrderGoodsSelectionModel> goodsSelectionList;
	public String getSendMsg() {
		StringBuffer msg = new StringBuffer();
		msg.append("userId=").append(getUserId()).append("|");
		msg.append("type=").append(notifyType).append("|");
		msg.append("goodsId=").append(getGoodsId()).append("|");
		msg.append("orderId=").append(getOrderId()).append("|");
		msg.append("limitStorage=").append(getLimitStorage()).append("|");
		msg.append("totalNumber=").append(getTotalNumber()).append("|");
		msg.append("leftNumber=").append(getLeftNumber()).append("|");
		msg.append("waterfloodVal=").append(getWaterfloodVal()).append("|");
		msg.append("goodsSelectionList=").append(getGoodsSelectionList());
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

	public java.lang.Integer getWaterfloodVal() {
		return waterfloodVal;
	}

	public void setWaterfloodVal(java.lang.Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}

	public List<OrderGoodsSelectionModel> getGoodsSelectionList() {
		return goodsSelectionList;
	}

	public void setGoodsSelectionList(
			List<OrderGoodsSelectionModel> goodsSelectionList) {
		this.goodsSelectionList = goodsSelectionList;
	}

	

}
