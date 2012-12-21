package com.tuan.pmt.model.param;

import java.math.BigDecimal;

import com.tuan.core.common.lang.TuanBaseDO;

public class GoodsInfoParam extends TuanBaseDO {
    private static final long serialVersionUID = 1598419649556748773L;

    /** 商品ID **/
    private long goodsId;
    /** 城市ID **/
    private long cityId;
    /** 二极分类ID **/
    private long secondCatId;
    /** 商品单价 **/
    private BigDecimal goodsPrice;
    /** 商品的总金额 **/
    private BigDecimal goodsAmount;
    
    public long getGoodsId() {
        return goodsId;
    }
    
    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
    public long getCityId() {
        return cityId;
    }
    public void setCityId(long cityId) {
        this.cityId = cityId;
    }
    public long getSecondCatId() {
        return secondCatId;
    }
    public void setSecondCatId(long secondCatId) {
        this.secondCatId = secondCatId;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }
    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }
    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }
}
