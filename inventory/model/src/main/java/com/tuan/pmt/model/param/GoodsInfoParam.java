package com.tuan.pmt.model.param;

import java.math.BigDecimal;

import com.tuan.core.common.lang.TuanBaseDO;

public class GoodsInfoParam extends TuanBaseDO {
    private static final long serialVersionUID = 1598419649556748773L;

    /** ��ƷID **/
    private long goodsId;
    /** ����ID **/
    private long cityId;
    /** ��������ID **/
    private long secondCatId;
    /** ��Ʒ���� **/
    private BigDecimal goodsPrice;
    /** ��Ʒ���ܽ�� **/
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
