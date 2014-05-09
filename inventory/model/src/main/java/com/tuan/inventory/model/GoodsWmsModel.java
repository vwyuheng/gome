package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: RedisGoodsSelectionRelationDO
 * @Description: 物流
 * @author henry.yu
 * @date 2014.05.08
 */
public class GoodsWmsModel extends TuanBaseDO{

	private static final long serialVersionUID = 1L;
	private Long id;// 主键id
	//private Long goodsId;// 商品ID(FK)
	private String wmsGoodsId;  //物流商品的一种编码
	private String goodsSupplier;  //供货商
	private String goodsName;    //商品名称
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	//private int limitStorage; // 0:库存无限制；1：限制库存
	private int isBeDelivery;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWmsGoodsId() {
		return wmsGoodsId;
	}
	public void setWmsGoodsId(String wmsGoodsId) {
		this.wmsGoodsId = wmsGoodsId;
	}
	public String getGoodsSupplier() {
		return goodsSupplier;
	}
	public void setGoodsSupplier(String goodsSupplier) {
		this.goodsSupplier = goodsSupplier;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
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
	public int getIsBeDelivery() {
		return isBeDelivery;
	}
	public void setIsBeDelivery(int isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
	}
	
	
	
}
