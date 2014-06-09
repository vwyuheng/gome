package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: GoodsBaseModel
 * @Description: 库存基本信息
 * @date 2014.03.06
 */
public class GoodsBaseModel extends TuanBaseDO{

	private static final long serialVersionUID = 1L;

	private Long goodsBaseId;// 商品基本ID
	private int baseSaleCount;//商品总销量
	private int baseTotalCount;//商品总库存
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
