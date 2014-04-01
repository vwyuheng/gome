package com.tuan.inventory.model;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存主体信息
 * @author henry.yu
 * @date 20140310
 */
public class RedisInventoryModel extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private Long goodsId;// 商品ID(FK)
	private java.lang.Integer totalNumber;// 当前总库存999999：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
//	private int isAddGoodsSelection;  //商品是否添加配型 0：不添加；1：添加
//	private int isDirectConsumption; //商品销售是否需要指定分店 0：不指定；1：指定
	private java.lang.Integer waterfloodVal;  //注水值
	//包括了商品的分店、商品选型信息
	private List<OrderGoodsSelectionModel> goodsSelectionList;
	//物流有关的
	//private int isBeDelivery; //是否发货
	
	
	//商品商家库存有关的 1^m  :维护商品与分店关系
	//private Set<RedisGoodsSuppliersInventoryDO> rgsiList;
	//商品分店与选型关系1^m  :维护商品与选型关系
	//private Set<RedisGoodsSelectionRelationDO> rgsrList;
	
	public java.lang.Integer getWaterfloodVal() {
		return waterfloodVal;
	}

	public List<OrderGoodsSelectionModel> getGoodsSelectionList() {
		return goodsSelectionList;
	}

	public void setGoodsSelectionList(
			List<OrderGoodsSelectionModel> goodsSelectionList) {
		this.goodsSelectionList = goodsSelectionList;
	}

	public void setWaterfloodVal(java.lang.Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
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
