package com.tuan.inventory.domain.job.bean;

import java.io.Serializable;

public class NotifyMessage implements Serializable {

	private static final long serialVersionUID = 2235735587333702996L;

	private static String notifyType = "3";	
	private Long userId;
	private Long goodsId;
	private Long orderId;
	private java.lang.Integer limitStorage; // 0:��������ƣ�1�����ƿ��
	private String variableQuantityJsonData;// ���仯�� ��Ʒ������Ϣ�еĿ��仯����ѡ����Ʒ��ֵ�����ֱ��Ӧ�Ŀ��仯������json������
	private java.lang.Integer waterfloodVal;  //עˮֵ
	
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
