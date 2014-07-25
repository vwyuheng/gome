package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class RestoreInventoryParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private String orderIds;
	private Long userId;
	private Long preGoodsId;// 改价前商品ID
	private Long goodsId;// 商品ID(FK)
	private Long goodsBaseId;// 商品库存基本信息ID
	private int limitStorage; // 0:库存无限制；1：限制库存
	//必传参数
	//private String goodsId;
	//type 2:可为空，4:选型id，6:分店id
	private String id;
	private int num;
	//2:商品调整[也就是没有选型和分店的商品]，4.选型库存调整 6.分店库存调整
	private String type;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getPreGoodsId() {
		return preGoodsId;
	}
	public void setPreGoodsId(Long preGoodsId) {
		this.preGoodsId = preGoodsId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOrderIds() {
		return orderIds;
	}
	public void setOrderIds(String orderIds) {
		this.orderIds = orderIds;
	}
	
	
	
}
