package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class WmsInventoryRestParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private String id;// 主键id
	private String wmsGoodsId;  //物流商品的一种编码
	private String goodsSupplier;  //供货商
	private String goodsName;    //商品名称
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private int isBeDelivery;
	private int num;
	//选型:这是一个json串 如：[{"limit":0,"id":2},{"limit":0,"id":1}] [List<CreaterGoodsSelectionParam>]
	private String goodsSelection;
	private String goodsIds; //商品id的json串
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWmsGoodsId() {
		return wmsGoodsId;
	}
	public void setWmsGoodsId(String wmsGoodsId) {
		this.wmsGoodsId = wmsGoodsId;
	}
	public String getGoodsSupplier() {
		return goodsSupplier;
	}
	public void setGoodsSupplier(String goodsSupplier) {
		this.goodsSupplier = goodsSupplier;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
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
	public int getIsBeDelivery() {
		return isBeDelivery;
	}
	public void setIsBeDelivery(int isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
	}
	public String getGoodsSelection() {
		return goodsSelection;
	}
	public void setGoodsSelection(String goodsSelection) {
		this.goodsSelection = goodsSelection;
	}
	public String getGoodsIds() {
		return goodsIds;
	}
	public void setGoodsIds(String goodsIds) {
		this.goodsIds = goodsIds;
	}
	

	
}
