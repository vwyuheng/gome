package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class AdjustWaterfloodParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	//type 2:商品id，4:选型id，6:分店id
	private String id;
	private String userId;
	private int num;
	//2:商品调整，4.选型库存调整 6.分店库存调整
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
	
	
	
}
