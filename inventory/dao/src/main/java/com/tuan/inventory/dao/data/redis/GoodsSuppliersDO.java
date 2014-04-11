package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: RedisGoodsSuppliersInventoryDO
 * @Description: 商品商家库存
 * @author henry.yu
 * @date 2014.3.10
 */
public class GoodsSuppliersDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long  id;
	private Long goodsId;// 商品ID(FK)
	private Long userId;// 商品ID(FK)
	private Long suppliersId;// 分店ID(FK)
	private java.lang.Integer totalNumber;// 当前分店总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前分店剩余数库存默认值:0
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	private Integer waterfloodVal;  //注水值
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(Long suppliersId) {
		this.suppliersId = suppliersId;
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
	public Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	

	
}
