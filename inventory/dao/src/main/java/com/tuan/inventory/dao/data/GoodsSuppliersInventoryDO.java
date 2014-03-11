package com.tuan.inventory.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: GoodsSuppliersInventoryDO
 * @Description: 商品商家库存
 * @author tianzq
 * @date 2012.11.30
 */
public class GoodsSuppliersInventoryDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;
	private java.lang.Integer goodsId;// 商品ID(FK)
	private java.lang.Integer suppliersId;// 分店ID(FK)
	private java.lang.Integer status;// 0:当前记录有效1：当前记录无效;默认值：0
	private java.lang.Integer totalNumber;// 当前分店总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前分店剩余数库存默认值:0
	private java.lang.Integer addTotalNumber;// 当前分店增加库存总量默认值:0
	private java.lang.Integer reduceTotalNumber;// 当前分店减少库存总量默认值:0
	private java.lang.Integer limitNumber;// 当前分店商家限购数量;0：无限制
	private java.lang.Integer settlement;// 当前分店结算方式;0:统一结算1：独立结算
	private java.lang.Integer validMsgGet;// 当前分店验证信息调取; 0:不调取1：调取
	private java.lang.Integer totalNumDisplayFont; // '0：不显示；1：显示',
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(java.lang.Integer goodsId) {
		this.goodsId = goodsId;
	}

	public java.lang.Integer getSuppliersId() {
		return suppliersId;
	}

	public void setSuppliersId(java.lang.Integer suppliersId) {
		this.suppliersId = suppliersId;
	}

	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
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

	public java.lang.Integer getAddTotalNumber() {
		return addTotalNumber;
	}

	public void setAddTotalNumber(java.lang.Integer addTotalNumber) {
		this.addTotalNumber = addTotalNumber;
	}

	public java.lang.Integer getReduceTotalNumber() {
		return reduceTotalNumber;
	}

	public void setReduceTotalNumber(java.lang.Integer reduceTotalNumber) {
		this.reduceTotalNumber = reduceTotalNumber;
	}

	public java.lang.Integer getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(java.lang.Integer limitNumber) {
		this.limitNumber = limitNumber;
	}

	public java.lang.Integer getSettlement() {
		return settlement;
	}

	public void setSettlement(java.lang.Integer settlement) {
		this.settlement = settlement;
	}

	public java.lang.Integer getValidMsgGet() {
		return validMsgGet;
	}

	public void setValidMsgGet(java.lang.Integer validMsgGet) {
		this.validMsgGet = validMsgGet;
	}

	public java.lang.Integer getTotalNumDisplayFont() {
		return totalNumDisplayFont;
	}

	public void setTotalNumDisplayFont(java.lang.Integer totalNumDisplayFont) {
		this.totalNumDisplayFont = totalNumDisplayFont;
	}

	public java.lang.Integer getLimitStorage() {
		return limitStorage;
	}

	public void setLimitStorage(java.lang.Integer limitStorage) {
		this.limitStorage = limitStorage;
	}

}
