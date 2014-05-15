package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: WmsIsBeDeliveryModel
 * @Description: 物流发货仓库信息
 * @author henry.yu
 * @date 2014.05.15
 */
public class WmsIsBeDeliveryDO extends TuanBaseDO{

	private static final long serialVersionUID = 1L;
	
	private int isBeDelivery;
	private String description;
	
	public int getIsBeDelivery() {
		return isBeDelivery;
	}
	public void setIsBeDelivery(int isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
}
