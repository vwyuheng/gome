package com.tuan.inventory.model;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * ��Ʒ���������Ϣ
 * @author henry.yu
 * @date 20140310
 */
public class GoodsInventoryModel extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	
	private Long goodsId;// ��ƷID(FK)
	private Long userId;// ��ƷID(FK)
	private java.lang.Integer totalNumber;// ��ǰ�ܿ��999999��������
	private java.lang.Integer leftNumber;// ��ǰʣ�������Ĭ��ֵ:0
	private java.lang.Integer limitStorage; // 0:��������ƣ�1�����ƿ��
	private java.lang.Integer waterfloodVal;  //עˮֵ
	//��Ʒѡ��list
	private List<GoodsSelectionModel> goodsSelectionList;
	//��Ʒ�ֵ�list
	private List<GoodsSuppliersModel> goodsSuppliersList;
	
	
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	public java.lang.Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(java.lang.Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	public List<GoodsSelectionModel> getGoodsSelectionList() {
		return goodsSelectionList;
	}
	public void setGoodsSelectionList(List<GoodsSelectionModel> goodsSelectionList) {
		this.goodsSelectionList = goodsSelectionList;
	}
	public List<GoodsSuppliersModel> getGoodsSuppliersList() {
		return goodsSuppliersList;
	}
	public void setGoodsSuppliersList(List<GoodsSuppliersModel> goodsSuppliersList) {
		this.goodsSuppliersList = goodsSuppliersList;
	}
	
	
	
	
	
}


