package com.tuan.inventory.model.param.rest;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.model.param.CreaterGoodsSelectionParam;
import com.tuan.inventory.model.param.CreaterGoodsSuppliersParam;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class CreaterInventoryRestParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private String userId;
	private String goodsId;// 商品ID(FK)
	private Integer totalNumber;// 当前总库存999999：无限制
	private Integer leftNumber;// 当前剩余数库存默认值:0
	private int limitStorage; // 0:库存无限制；1：限制库存
	private Integer waterfloodVal;  //注水值
	//选型
	private List<CreaterGoodsSelectionParam> goodsSelection;
	//分店
	private List<CreaterGoodsSuppliersParam> goodsSuppliers;
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
	public List<CreaterGoodsSelectionParam> getGoodsSelection() {
		return goodsSelection;
	}
	public void setGoodsSelection(List<CreaterGoodsSelectionParam> goodsSelection) {
		this.goodsSelection = goodsSelection;
	}
	public List<CreaterGoodsSuppliersParam> getGoodsSuppliers() {
		return goodsSuppliers;
	}
	public void setGoodsSuppliers(List<CreaterGoodsSuppliersParam> goodsSuppliers) {
		this.goodsSuppliers = goodsSuppliers;
	}
	
	
	
}
