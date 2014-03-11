package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存主体信息
 * @author henry.yu
 * @date 20140310
 */
public class RedisInventoryDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private java.lang.Integer goodsId;// 商品ID(FK)
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
	private int isAddGoodsSelection;  //商品是否添加配型 0：不添加；1：添加
	private int isDirectConsumption; //商品销售是否需要指定分店 0：不指定；1：指定
	private java.lang.Integer waterfloodVal;  //注水值
	//物流有关的
	private int isBeDelivery; //是否发货
	
	
	//商品商家库存有关的 1^m  :维护商品与分店关系
	//private Set<RedisGoodsSuppliersInventoryDO> rgsiList;
	//商品分店与选型关系1^m  :维护商品与选型关系
	//private Set<RedisGoodsSelectionRelationDO> rgsrList;
	
	
	public int getIsAddGoodsSelection() {
		return isAddGoodsSelection;
	}

	public void setIsAddGoodsSelection(int isAddGoodsSelection) {
		this.isAddGoodsSelection = isAddGoodsSelection;
	}

	public int getIsDirectConsumption() {
		return isDirectConsumption;
	}

	public void setIsDirectConsumption(int isDirectConsumption) {
		this.isDirectConsumption = isDirectConsumption;
	}

	public java.lang.Integer getWaterfloodVal() {
		return waterfloodVal;
	}

	public void setWaterfloodVal(java.lang.Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}

	public int getIsBeDelivery() {
		return isBeDelivery;
	}

	public void setIsBeDelivery(int isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
	}

	public java.lang.Integer getGoodsId() {
		return goodsId;
	}
	
	public void setGoodsId(java.lang.Integer goodsId) {
		this.goodsId = goodsId;
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
	
	public java.lang.Integer getLimitStorage() {
		return limitStorage;
	}
	
	public void setLimitStorage(java.lang.Integer limitStorage) {
		this.limitStorage = limitStorage;
	}
	
}
