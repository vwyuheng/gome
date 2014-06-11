package com.tuan.inventory.model;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存主体信息
 * @author henry.yu
 * @date 20140310
 */
public class GoodsInventoryModel extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private Long goodsId;// 商品ID(FK)
	private Long userId;// 商品ID(FK)
	private String goodsSelectionIds;//共享库存时所绑定的选型表ID集合，以逗号分隔开；如：1,2,3或2
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	private java.lang.Integer isAddGoodsSelection; // 商品是否添加配型 0：不添加；1：添加
	private java.lang.Integer isDirectConsumption; // 商品销售是否需要指定分店 0：不指定；1：指定
	private java.lang.Integer waterfloodVal;  //注水值
	private java.lang.Integer goodsSaleCount; //商品销量
	
	//商品选型list
	private List<GoodsSelectionModel> goodsSelectionList;
	//商品分店list
	private List<GoodsSuppliersModel> goodsSuppliersList;
	private Long goodsBaseId;
	
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
	public List<GoodsSelectionModel> getGoodsSelectionList() {
		return goodsSelectionList;
	}
	public void setGoodsSelectionList(List<GoodsSelectionModel> goodsSelectionList) {
		this.goodsSelectionList = goodsSelectionList;
	}
	public List<GoodsSuppliersModel> getGoodsSuppliersList() {
		return goodsSuppliersList;
	}
	public void setGoodsSuppliersList(List<GoodsSuppliersModel> goodsSuppliersList) {
		this.goodsSuppliersList = goodsSuppliersList;
	}
	public String getGoodsSelectionIds() {
		return goodsSelectionIds;
	}
	public void setGoodsSelectionIds(String goodsSelectionIds) {
		this.goodsSelectionIds = goodsSelectionIds;
	}
	public java.lang.Integer getIsAddGoodsSelection() {
		return isAddGoodsSelection;
	}
	public void setIsAddGoodsSelection(java.lang.Integer isAddGoodsSelection) {
		this.isAddGoodsSelection = isAddGoodsSelection;
	}
	public java.lang.Integer getIsDirectConsumption() {
		return isDirectConsumption;
	}
	public void setIsDirectConsumption(java.lang.Integer isDirectConsumption) {
		this.isDirectConsumption = isDirectConsumption;
	}
	public Long getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	public java.lang.Integer getGoodsSaleCount() {
		return goodsSaleCount;
	}
	public void setGoodsSaleCount(java.lang.Integer goodsSaleCount) {
		this.goodsSaleCount = goodsSaleCount;
	}
	
	
	
	
	
}


