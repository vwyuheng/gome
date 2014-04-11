package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: RedisGoodsSelectionRelationDO
 * @Description: ��Ʒ��ѡ�͹�ϵ
 * @author henry.yu
 * @date 2014.03.06
 */
public class GoodsSelectionDO extends TuanBaseDO{

	private static final long serialVersionUID = 1L;
	private Long id;  //ѡ��id
	private Long goodsId;	// ��Ʒid
	private Long userId;	// �û�id
	private Long goodTypeId; // ѡ�����ͱ�ID(FK)������ѡ�ͱ�ID
	private java.lang.Integer totalNumber; 	// ��ǰѡ���ܿ��
	private java.lang.Integer leftNumber; 	// ��ǰѡ��ʣ�������Ĭ��ֵ��0 
	private Integer waterfloodVal;  //עˮֵ
	private int limitStorage;	//0:��������ƣ�1�����ƿ��
	private long suppliersInventoryId; //�̼ҿ���ID(FK)
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
	public Long getGoodTypeId() {
		return goodTypeId;
	}
	public void setGoodTypeId(Long goodTypeId) {
		this.goodTypeId = goodTypeId;
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
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	public long getSuppliersInventoryId() {
		return suppliersInventoryId;
	}
	public void setSuppliersInventoryId(long suppliersInventoryId) {
		this.suppliersInventoryId = suppliersInventoryId;
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
