package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: GoodsSelectionAndSuppliersParam
 * @Description: 保存商品选型或分店的原始库存和扣减库存的对象
 * @author henry.yu
 * @date 2014-4-8
 */
public class GoodsSuppliersRestParam extends TuanBaseDO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//分店
	private Long suppliersId;
	//分店需扣减的商品库存
	private int ssNum;
	
	
	public int getSsNum() {
		return ssNum;
	}
	public void setSsNum(int ssNum) {
		this.ssNum = ssNum;
	}
	public Long getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(Long suppliersId) {
		this.suppliersId = suppliersId;
	}
	
	
	
}