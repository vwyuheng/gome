package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: RedisGoodsSuppliersInventoryDO
 * @Description: ��Ʒ�̼ҿ��
 * @author henry.yu
 * @date 2014.3.10
 */
public class GoodsSuppliersDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long  id;
	private Long goodsId;// ��ƷID(FK)
	private Long userId;// ��ƷID(FK)
	private Long suppliersId;// �ֵ�ID(FK)
	private java.lang.Integer totalNumber;// ��ǰ�ֵ��ܿ��999999��������
	private java.lang.Integer leftNumber;// ��ǰ�ֵ�ʣ�������Ĭ��ֵ:0
	private java.lang.Integer limitStorage; // 0:��������ƣ�1�����ƿ��
	private Integer waterfloodVal;  //עˮֵ
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(Long suppliersId) {
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
	public Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	

	
}
