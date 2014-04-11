package com.tuan.inventory.model.param;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class UpdateInventoryParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private String userId;
	private String goodsId;// 商品ID(FK)
	private String orderId; //订单id
	private int limitStorage;  //0:库存无限制；1：限制库存
	private Integer totalNumber;// 当前总库存999999：无限制
	private Integer leftNumber;// 当前剩余数库存默认值:0
	private Integer waterfloodVal;  //注水值
	private int num;
	//选型
	private List<GoodsSelectionModel> goodsSelection;
	//分店
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
