package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 分店库存变化量bean
 * @author henry.yu
 * @date 2014/03/18
 */
public class RedisSuppliersNumDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private int suppliersId;//分店id
	private int supplierNum;//TOTAL_SUPPLIERS_NUM("分店商品库存变化量"),
	private long supplierLeftNum;//TOTAL_SUPPLIERS_LEFTNUM("分店商品库存剩余量");
	public int getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(int suppliersId) {
		this.suppliersId = suppliersId;
	}
	public int getSupplierNum() {
		return supplierNum;
	}
	public void setSupplierNum(int supplierNum) {
		this.supplierNum = supplierNum;
	}
	public long getSupplierLeftNum() {
		return supplierLeftNum;
	}
	public void setSupplierLeftNum(long supplierLeftNum) {
		this.supplierLeftNum = supplierLeftNum;
	}
	
	
	
}
