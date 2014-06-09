package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class AdjustInventoryParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	//必传参数
	private String goodsId;
	//type 2:可为空，4:选型id，6:分店id
	private String id;
	private String userId;
	private int num;
	//2:商品调整[也就是没有选型和分店的商品]，4.选型库存调整 6.分店库存调整
	private String type;
	private String goodsBaseId;// 商品库存基本信息ID
	
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
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(String goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	
	
	
}
