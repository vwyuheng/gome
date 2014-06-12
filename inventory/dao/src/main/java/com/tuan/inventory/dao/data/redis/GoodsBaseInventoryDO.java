package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存基表
 * @author zhangbo
 */
public class GoodsBaseInventoryDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private Long goodsBaseId;// 商品基本ID
	private Integer baseSaleCount;//商品总销量
	private Integer baseTotalCount;//商品总库存

	public Long getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	public Integer getBaseSaleCount() {
		return baseSaleCount;
	}
	public void setBaseSaleCount(Integer baseSaleCount) {
		this.baseSaleCount = baseSaleCount;
	}
	public Integer getBaseTotalCount() {
		return baseTotalCount;
	}
	public void setBaseTotalCount(Integer baseTotalCount) {
		this.baseTotalCount = baseTotalCount;
	}
	
}


