package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class CreaterInventoryRestParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private String tokenid;  //redis序列,解决接口幂等问题
	private String userId;
	private String goodsId;// 商品ID(FK)
	private Integer totalNumber;// 当前总库存999999：无限制
	private Integer leftNumber;// 当前剩余数库存默认值:0
	private int limitStorage; // 0:库存无限制；1：限制库存
	private String goodsBaseId;
	private Integer waterfloodVal;  //注水值
	//选型:这是一个json串 如：[{"limit":0,"id":2},{"limit":0,"id":1}] [List<CreaterGoodsSelectionParam>]
	private String goodsSelection;
	//分店：这是一个json串 如：[{"limit":0,"id":2},{"limit":0,"id":1}]  [List<CreaterGoodsSuppliersParam>]
	private String goodsSuppliers;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public Integer getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	public Integer getLeftNumber() {
		return leftNumber;
	}
	public void setLeftNumber(Integer leftNumber) {
		this.leftNumber = leftNumber;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	public Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	public String getGoodsSelection() {
		return goodsSelection;
	}
	public void setGoodsSelection(String goodsSelection) {
		this.goodsSelection = goodsSelection;
	}
	public String getGoodsSuppliers() {
		return goodsSuppliers;
	}
	public void setGoodsSuppliers(String goodsSuppliers) {
		this.goodsSuppliers = goodsSuppliers;
	}
	public String getTokenid() {
		return tokenid;
	}
	public void setTokenid(String tokenid) {
		this.tokenid = tokenid;
	}
	public String getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(String goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	
}
