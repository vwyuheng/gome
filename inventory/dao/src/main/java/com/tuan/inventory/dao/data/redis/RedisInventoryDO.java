package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * ��Ʒ���������Ϣ
 * @author henry.yu
 * @date 20140310
 */
public class RedisInventoryDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private java.lang.Integer goodsId;// ��ƷID(FK)
	private java.lang.Integer totalNumber;// ��ǰ�ܿ��999999��������
	private java.lang.Integer leftNumber;// ��ǰʣ�������Ĭ��ֵ:0
	private java.lang.Integer limitStorage; // 0:��������ƣ�1�����ƿ��
	private int isAddGoodsSelection;  //��Ʒ�Ƿ�������� 0������ӣ�1�����
	private int isDirectConsumption; //��Ʒ�����Ƿ���Ҫָ���ֵ� 0����ָ����1��ָ��
	private java.lang.Integer waterfloodVal;  //עˮֵ
	//�����йص�
	private int isBeDelivery; //�Ƿ񷢻�
	
	
	//��Ʒ�̼ҿ���йص� 1^m  :ά����Ʒ��ֵ��ϵ
	//private Set<RedisGoodsSuppliersInventoryDO> rgsiList;
	//��Ʒ�ֵ���ѡ�͹�ϵ1^m  :ά����Ʒ��ѡ�͹�ϵ
	//private Set<RedisGoodsSelectionRelationDO> rgsrList;
	
	
	public int getIsAddGoodsSelection() {
		return isAddGoodsSelection;
	}

	public void setIsAddGoodsSelection(int isAddGoodsSelection) {
		this.isAddGoodsSelection = isAddGoodsSelection;
	}

	public int getIsDirectConsumption() {
		return isDirectConsumption;
	}

	public void setIsDirectConsumption(int isDirectConsumption) {
		this.isDirectConsumption = isDirectConsumption;
	}

	public java.lang.Integer getWaterfloodVal() {
		return waterfloodVal;
	}

	public void setWaterfloodVal(java.lang.Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}

	public int getIsBeDelivery() {
		return isBeDelivery;
	}

	public void setIsBeDelivery(int isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
	}

	public java.lang.Integer getGoodsId() {
		return goodsId;
	}
	
	public void setGoodsId(java.lang.Integer goodsId) {
		this.goodsId = goodsId;
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
