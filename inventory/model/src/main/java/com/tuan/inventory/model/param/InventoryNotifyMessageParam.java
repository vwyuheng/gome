package com.tuan.inventory.model.param;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;

public class InventoryNotifyMessageParam extends TuanBaseDO{
	private static final long serialVersionUID = -2215765968154981320L;
	private static String notifyType = "3";	
	private Long userId;
	private Long goodsId;
	//private Long orderId;
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private java.lang.Integer waterfloodVal;  //注水值
	//选型
	private List<GoodsSelectionModel> selectionRelation;
	//分店
	private List<GoodsSuppliersModel> suppliersRelation;
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
	public List<GoodsSelectionModel> getSelectionRelation() {
		return selectionRelation;
	}
	public void setSelectionRelation(List<GoodsSelectionModel> selectionRelation) {
		this.selectionRelation = selectionRelation;
	}
	public List<GoodsSuppliersModel> getSuppliersRelation() {
		return suppliersRelation;
	}
	public void setSuppliersRelation(List<GoodsSuppliersModel> suppliersRelation) {
		this.suppliersRelation = suppliersRelation;
	}
	
	
	
	
}
