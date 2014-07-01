package com.tuan.inventory.dao.data;

public class GoodsUpdateNumberDO {
   private int totalNum;
   private int leftNum;
   private int limitStorage;
   private int goodsSaleCount;
   private Long id;
   private String wmsGoodsId;
public int getTotalNum() {
	return totalNum;
}
public void setTotalNum(int totalNum) {
	this.totalNum = totalNum;
}
public int getLeftNum() {
	return leftNum;
}
public void setLeftNum(int leftNum) {
	this.leftNum = leftNum;
}
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public int getGoodsSaleCount() {
	return goodsSaleCount;
}
public void setGoodsSaleCount(int goodsSaleCount) {
	this.goodsSaleCount = goodsSaleCount;
}
public String getWmsGoodsId() {
	return wmsGoodsId;
}
public void setWmsGoodsId(String wmsGoodsId) {
	this.wmsGoodsId = wmsGoodsId;
}
public int getLimitStorage() {
	return limitStorage;
}
public void setLimitStorage(int limitStorage) {
	this.limitStorage = limitStorage;
}


   
}
