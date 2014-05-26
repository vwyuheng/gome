package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: GoodsSelectionAndSuppliersParam
 * @Description: 保存商品选型或分店的原始库存和扣减库存的对象
 * @author henry.yu
 * @date 2014-4-8
 */
public class GoodsSelectionRestParam extends TuanBaseDO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//选型
	private Long selectionId;
	//选型需扣减的商品库存
	private int stNum;
	
	private String wmsGoodsId;  //物流编码
	
	
	public int getStNum() {
		return stNum;
	}
	public void setStNum(int stNum) {
		this.stNum = stNum;
	}
	public Long getSelectionId() {
		return selectionId;
	}
	public void setSelectionId(Long selectionId) {
		this.selectionId = selectionId;
	}
	public String getWmsGoodsId() {
		return wmsGoodsId;
	}
	public void setWmsGoodsId(String wmsGoodsId) {
		this.wmsGoodsId = wmsGoodsId;
	}
	
	
	
	
}