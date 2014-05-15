package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: WmsIsBeDeliveryModel
 * @Description: 物流发货仓库信息
 * @author henry.yu
 * @date 2014.05.15
 */
public class WmsIsBeDeliveryModel extends TuanBaseDO{

	private static final long serialVersionUID = 1L;
	
	private String isBeDelivery;
	private String description;
	
	
	public String getIsBeDelivery() {
		return isBeDelivery;
	}
	public void setIsBeDelivery(String isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
}
