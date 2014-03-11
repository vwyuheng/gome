package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: RedisGoodsSuppliersInventoryDO
 * @Description: ��Ʒ�̼ҿ��
 * @author henry.yu
 * @date 2014.3.10
 */
public class RedisGoodsSuppliersInventoryDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;
	private java.lang.Integer goodsId;// ��ƷID(FK)
	private java.lang.Integer suppliersId;// �ֵ�ID(FK)
	private java.lang.Integer totalNumber;// ��ǰ�ֵ��ܿ��999999��������
	private java.lang.Integer leftNumber;// ��ǰ�ֵ�ʣ�������Ĭ��ֵ:0
	private java.lang.Integer limitStorage; // 0:��������ƣ�1�����ƿ��
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	public java.lang.Integer getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(java.lang.Integer goodsId) {
		this.goodsId = goodsId;
	}
	public java.lang.Integer getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(java.lang.Integer suppliersId) {
		this.suppliersId = suppliersId;
	}
	public java.lang.Integer getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(java.lang.Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	public java.lang.Integer getLeftNumber() {
		return leftNumber;
	}
	public void setLeftNumber(java.lang.Integer leftNumber) {
		this.leftNumber = leftNumber;
	}
	public java.lang.Integer getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(java.lang.Integer limitStorage) {
		this.limitStorage = limitStorage;
	}

	
}
