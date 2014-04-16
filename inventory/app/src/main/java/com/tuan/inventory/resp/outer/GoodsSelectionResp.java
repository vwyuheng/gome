package com.tuan.inventory.resp.outer;

import java.io.Serializable;

/**
 * 选型查询接口返回的商品选型结果
 * @author henry.yu
 * @date 2014/4/16
 */
public class GoodsSelectionResp implements Serializable{
	private static final long serialVersionUID = -9208191910738187918L;
	private String selectionId;  //选型id
	//private Long userId;
	private String goodsId;	// 商品id
	private String goodTypeId; // 选型类型表ID(FK)关联到选型表ID
	private String totalNumber; 	// 当前选型总库存
	private String leftNumber; 	// 当前选型剩余数库存默认值：0 
	private String waterfloodVal;  //注水值
	private String limitStorage;	//0:库存无限制；1：限制库存
	private String suppliersInventoryId; //商家库存表ID(FK)
	public String getSelectionId() {
		return selectionId;
	}
	public void setSelectionId(String selectionId) {
		this.selectionId = selectionId;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodTypeId() {
		return goodTypeId;
	}
	public void setGoodTypeId(String goodTypeId) {
		this.goodTypeId = goodTypeId;
	}
	public String getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}
	public String getLeftNumber() {
		return leftNumber;
	}
	public void setLeftNumber(String leftNumber) {
		this.leftNumber = leftNumber;
	}
	public String getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(String waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	public String getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(String limitStorage) {
		this.limitStorage = limitStorage;
	}
	public String getSuppliersInventoryId() {
		return suppliersInventoryId;
	}
	public void setSuppliersInventoryId(String suppliersInventoryId) {
		this.suppliersInventoryId = suppliersInventoryId;
	}

	
}
