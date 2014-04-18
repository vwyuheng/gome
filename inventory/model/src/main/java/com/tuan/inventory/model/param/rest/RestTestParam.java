package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class RestTestParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private String goodsId;// 商品ID(FK)
	private int limit; // 0:库存无限制；1：限制库存
	//选型
	//private List<TestParam> goodsSelection;
	private String goodsSelection;
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
//	public List<TestParam> getGoodsSelection() {
//		return goodsSelection;
//	}
//	public void setGoodsSelection(List<TestParam> goodsSelection) {
//		this.goodsSelection = goodsSelection;
//	}
	public String getGoodsSelection() {
		return goodsSelection;
	}
	public void setGoodsSelection(String goodsSelection) {
		this.goodsSelection = goodsSelection;
	}
	
	
}
