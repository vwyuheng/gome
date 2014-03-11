package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: RedisGoodsSelectionRelationDO
 * @Description: 商品与选型关系（新版）
 * @author henry.yu
 * @date 2014.03.06
 */
public class RedisGoodsSelectionRelationDO extends TuanBaseDO{

	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;
	private java.lang.Integer goodsId;	// 商品id
	private java.lang.Integer goodTypeId; // 选型类型表ID(FK)关联到选型表ID
	private java.lang.Integer totalNumber; 	// 当前选型总库存
	private java.lang.Integer leftNumber; 	// 当前选型剩余数库存默认值：0 
	private java.lang.Integer limitStorage;	//0:库存无限制；1：限制库存
	private java.lang.Integer suppliersInventoryId; //商家库存表ID(FK)
	
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
	public java.lang.Integer getGoodTypeId() {
		return goodTypeId;
	}
	public void setGoodTypeId(java.lang.Integer goodTypeId) {
		this.goodTypeId = goodTypeId;
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
	public java.lang.Integer getSuppliersInventoryId() {
		return suppliersInventoryId;
	}
	public void setSuppliersInventoryId(java.lang.Integer suppliersInventoryId) {
		this.suppliersInventoryId = suppliersInventoryId;
	}
	
	
}
