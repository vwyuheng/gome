package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * �ֵ���仯��bean
 * @author henry.yu
 * @date 2014/03/18
 */
public class RedisSuppliersNumDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private int suppliersId;//�ֵ�id
	private int supplierNum;//TOTAL_SUPPLIERS_NUM("�ֵ���Ʒ���仯��"),
	private long supplierLeftNum;//TOTAL_SUPPLIERS_LEFTNUM("�ֵ���Ʒ���ʣ����");
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
