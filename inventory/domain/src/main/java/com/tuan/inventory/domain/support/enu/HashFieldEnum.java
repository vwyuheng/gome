package com.tuan.inventory.domain.support.enu;
/**
 * 用于定义库存存储的数据结构>field常量
 * @author henry.yu
 * @date 2014/3/13
 */
public enum HashFieldEnum {
	totalNumber,leftNumber,limitStorage,isAddGoodsSelection,wmsGoodsId,
	isDirectConsumption,waterfloodVal,isBeDelivery,goodsSelectionIds,wmsId,suppliersSubId,suppliersInventoryId;
	
	public static void main(String[] args) {
		System.out.println(HashFieldEnum.totalNumber.toString());
	}
}

