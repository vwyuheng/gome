package com.tuan.inventory.model.param;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;

public class InventoryNotifyMessageParam extends TuanBaseDO{
	private static final long serialVersionUID = -2215765968154981320L;
	private static String notifyType = "3";	
	private Long userId;
	private Long goodsId;
	private Long goodsBaseId;
	private int baseSaleCount;
	private int baseTotalCount;
	//private Long orderId;
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private java.lang.Integer waterfloodVal;  //注水值
	private String sales; //销量
	//选型
	private List<SelectionNotifyMessageParam> selectionRelation;
	//分店
	private List<SuppliersNotifyMessageParam> suppliersRelation;
	public static String getNotifyType() {
		return notifyType;
	}
	public static void setNotifyType(String notifyType) {
		InventoryNotifyMessageParam.notifyType = notifyType;
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
	
	public List<SelectionNotifyMessageParam> getSelectionRelation() {
		return selectionRelation;
	}
	public void setSelectionRelation(
			List<SelectionNotifyMessageParam> selectionRelation) {
		this.selectionRelation = selectionRelation;
	}
	public List<SuppliersNotifyMessageParam> getSuppliersRelation() {
		return suppliersRelation;
	}
	public void setSuppliersRelation(
			List<SuppliersNotifyMessageParam> suppliersRelation) {
		this.suppliersRelation = suppliersRelation;
	}
	public String getSales() {
		return sales;
	}
	public void setSales(String sales) {
		this.sales = sales;
	}
	public Long getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	public int getBaseSaleCount() {
		return baseSaleCount;
	}
	public void setBaseSaleCount(int baseSaleCount) {
		this.baseSaleCount = baseSaleCount;
	}
	public int getBaseTotalCount() {
		return baseTotalCount;
	}
	public void setBaseTotalCount(int baseTotalCount) {
		this.baseTotalCount = baseTotalCount;
	}
	
	
}
