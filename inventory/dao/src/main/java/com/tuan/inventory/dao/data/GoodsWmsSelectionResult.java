package com.tuan.inventory.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: GoodsSelectionAndSuppliersParam
 * @Description: 保存商品选型或分店的原始库存和扣减库存的对象
 * @author henry.yu
 * @date 2014-4-8
 */
public class GoodsWmsSelectionResult extends TuanBaseDO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//选型id
	private Long id ;
	//选型id
	private Long goodTypeId;
	//调整的剩余库存量
	private int leftNum;
	//调整的库存总量
	private int totalNum;
	
	public Long getGoodTypeId() {
		return goodTypeId;
	}
	public void setGoodTypeId(Long goodTypeId) {
		this.goodTypeId = goodTypeId;
	}
	public int getLeftNum() {
		return leftNum;
	}
	public void setLeftNum(int leftNum) {
		this.leftNum = leftNum;
	}
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	
	
}