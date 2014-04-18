package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class UpdateInventoryRestParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private String userId;
	private String goodsId;// 商品ID(FK)
	private String orderId; //订单id
	private int num;// 扣减的库存
	//选型
	private String goodsSelection;
	//分店
	private String goodsSuppliers;
//	//选型
//	private List<GoodsSelectionRestParam> goodsSelection;
//	//分店
//	private List<GoodsSuppliersRestParam> goodsSuppliers;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getGoodsSelection() {
		return goodsSelection;
	}
	public void setGoodsSelection(String goodsSelection) {
		this.goodsSelection = goodsSelection;
	}
	public String getGoodsSuppliers() {
		return goodsSuppliers;
	}
	public void setGoodsSuppliers(String goodsSuppliers) {
		this.goodsSuppliers = goodsSuppliers;
	}
	
	
	
}
