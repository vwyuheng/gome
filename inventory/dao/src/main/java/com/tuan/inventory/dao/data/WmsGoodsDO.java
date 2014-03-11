package com.tuan.inventory.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 
 * @author tianzq
 * @date 2012.11.30
 *
 */
public class WmsGoodsDO extends TuanBaseDO {
	private static final long serialVersionUID = -2510930870450010869L;

	private long id;
	private String wmsGoodsId;
	private String goodsSupplier;
	private String goodsName;
	private long goodsId;
	private int totalNum;
	private int leftNum;
	private int isBeDelivery;
	private String category1;
	private String category2;
	
	
	
    public int getLeftNum() {
		return leftNum;
	}
	public void setLeftNum(int leftNum) {
		this.leftNum = leftNum;
	}
	public long getId() {
        return id;
    }
    public void setId(long id) {
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
    
    public long getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
    public int getTotalNum() {
        return totalNum;
    }
    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }
    public int getIsBeDelivery() {
        return isBeDelivery;
    }
    public void setIsBeDelivery(int isBeDelivery) {
        this.isBeDelivery = isBeDelivery;
    }
    public String getCategory1() {
        return category1;
    }
    public void setCategory1(String category1) {
        this.category1 = category1;
    }
    public String getCategory2() {
        return category2;
    }
    public void setCategory2(String category2) {
        this.category2 = category2;
    }
}
