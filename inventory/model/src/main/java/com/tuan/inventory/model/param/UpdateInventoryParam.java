package com.tuan.inventory.model.param;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
/**
 * ��Ʒ������
 * @author henry.yu
 * @date 20140310
 */
public class UpdateInventoryParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private String userId;
	private String goodsId;// ��ƷID(FK)
	private String orderId; //����id
	private int limitStorage;  //0:��������ƣ�1�����ƿ��
	private Integer totalNumber;// ��ǰ�ܿ��999999��������
	private Integer leftNumber;// ��ǰʣ�������Ĭ��ֵ:0
	private Integer waterfloodVal;  //עˮֵ
	private int num;
	//ѡ��
	private List<GoodsSelectionModel> goodsSelection;
	//�ֵ�
	private List<GoodsSuppliersModel> goodsSuppliers;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	public Integer getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	public Integer getLeftNumber() {
		return leftNumber;
	}
	public void setLeftNumber(Integer leftNumber) {
		this.leftNumber = leftNumber;
	}
	public Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public List<GoodsSelectionModel> getGoodsSelection() {
		return goodsSelection;
	}
	public void setGoodsSelection(List<GoodsSelectionModel> goodsSelection) {
		this.goodsSelection = goodsSelection;
	}
	public List<GoodsSuppliersModel> getGoodsSuppliers() {
		return goodsSuppliers;
	}
	public void setGoodsSuppliers(List<GoodsSuppliersModel> goodsSuppliers) {
		this.goodsSuppliers = goodsSuppliers;
	}
	
	
}
