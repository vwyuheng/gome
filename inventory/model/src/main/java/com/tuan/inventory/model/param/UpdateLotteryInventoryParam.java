package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 抽奖商品库存参数
 * @author henry.yu
 * @date 20140819
 */
public class UpdateLotteryInventoryParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private long userId;
	private long goodsId;// 商品ID(FK)
	private long objectId;  //抽奖Id
	private int saleCount;
	private long goodsBaseId;// 商品库存基本信息ID
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public int getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(int saleCount) {
		this.saleCount = saleCount;
	}
	public long getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	
	
	
}
