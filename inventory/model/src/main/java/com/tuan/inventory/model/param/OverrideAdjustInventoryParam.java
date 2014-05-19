package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 覆盖调整库存量参数
 * @author henry.yu
 * @date 20140516
 */
public class OverrideAdjustInventoryParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	//必传参数
	private String goodsId;
	//type 2:可为空，4:选型id，6:分店id
	private String id;
	private String userId;
	//需调整的库存总量
	private int totalnum;
	//是否限制库存
	private int limitStorage;
	//2:商品调整[也就是没有选型和分店的商品]，4.选型库存调整 6.分店库存调整
	private String type;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public int getTotalnum() {
		return totalnum;
	}
	public void setTotalnum(int totalnum) {
		this.totalnum = totalnum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	
	
	
}
