package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: GoodsSelectionAndSuppliersParam
 * @Description: 保存商品选型或分店的原始库存和扣减库存的对象
 * @author henry.yu
 * @date 2014-4-8
 */
public class GoodsSelectionAndSuppliersModel extends TuanBaseDO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	//需扣减的商品库存
	private int goodsInventory;
	//原库存
	private int originalGoodsInventory;
	public int getGoodsInventory() {
		return goodsInventory;
	}
	public void setGoodsInventory(int goodsInventory) {
		this.goodsInventory = goodsInventory;
	}
	public int getOriginalGoodsInventory() {
		return originalGoodsInventory;
	}
	public void setOriginalGoodsInventory(int originalGoodsInventory) {
		this.originalGoodsInventory = originalGoodsInventory;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
}