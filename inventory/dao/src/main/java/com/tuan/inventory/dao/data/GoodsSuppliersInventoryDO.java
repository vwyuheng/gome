package com.tuan.inventory.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: GoodsSuppliersInventoryDO
 * @Description: ��Ʒ�̼ҿ��
 * @author tianzq
 * @date 2012.11.30
 */
public class GoodsSuppliersInventoryDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;
	private java.lang.Integer goodsId;// ��ƷID(FK)
	private java.lang.Integer suppliersId;// �ֵ�ID(FK)
	private java.lang.Integer status;// 0:��ǰ��¼��Ч1����ǰ��¼��Ч;Ĭ��ֵ��0
	private java.lang.Integer totalNumber;// ��ǰ�ֵ��ܿ��999999��������
	private java.lang.Integer leftNumber;// ��ǰ�ֵ�ʣ�������Ĭ��ֵ:0
	private java.lang.Integer addTotalNumber;// ��ǰ�ֵ����ӿ������Ĭ��ֵ:0
	private java.lang.Integer reduceTotalNumber;// ��ǰ�ֵ���ٿ������Ĭ��ֵ:0
	private java.lang.Integer limitNumber;// ��ǰ�ֵ��̼��޹�����;0��������
	private java.lang.Integer settlement;// ��ǰ�ֵ���㷽ʽ;0:ͳһ����1����������
	private java.lang.Integer validMsgGet;// ��ǰ�ֵ���֤��Ϣ��ȡ; 0:����ȡ1����ȡ
	private java.lang.Integer totalNumDisplayFont; // '0������ʾ��1����ʾ',
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

	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
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

	public java.lang.Integer getAddTotalNumber() {
		return addTotalNumber;
	}

	public void setAddTotalNumber(java.lang.Integer addTotalNumber) {
		this.addTotalNumber = addTotalNumber;
	}

	public java.lang.Integer getReduceTotalNumber() {
		return reduceTotalNumber;
	}

	public void setReduceTotalNumber(java.lang.Integer reduceTotalNumber) {
		this.reduceTotalNumber = reduceTotalNumber;
	}

	public java.lang.Integer getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(java.lang.Integer limitNumber) {
		this.limitNumber = limitNumber;
	}

	public java.lang.Integer getSettlement() {
		return settlement;
	}

	public void setSettlement(java.lang.Integer settlement) {
		this.settlement = settlement;
	}

	public java.lang.Integer getValidMsgGet() {
		return validMsgGet;
	}

	public void setValidMsgGet(java.lang.Integer validMsgGet) {
		this.validMsgGet = validMsgGet;
	}

	public java.lang.Integer getTotalNumDisplayFont() {
		return totalNumDisplayFont;
	}

	public void setTotalNumDisplayFont(java.lang.Integer totalNumDisplayFont) {
		this.totalNumDisplayFont = totalNumDisplayFont;
	}

	public java.lang.Integer getLimitStorage() {
		return limitStorage;
	}

	public void setLimitStorage(java.lang.Integer limitStorage) {
		this.limitStorage = limitStorage;
	}

}
