package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140414
 */
public class InventoryScheduledParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private int period;
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	
	
	
	
}
