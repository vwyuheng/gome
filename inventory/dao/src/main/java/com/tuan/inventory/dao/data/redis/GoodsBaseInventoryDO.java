package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存基表
 * @author zhangbo
 */
public class GoodsBaseInventoryDO extends TuanBaseDO {

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


