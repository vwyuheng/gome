package com.tuan.inventory.model.param;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
/**
 * 商品库存参数4商品改价
 * @author henry.yu
 * @date 20140705
 */
public class CreateInventory4GoodsCostParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private String tokenid;  //redis序列,解决接口幂等问题
	private Long userId;
	private Long preGoodsId;// 改价前商品ID
	private Long goodsId;// 商品ID(FK)
	private Long goodsBaseId;// 商品库存基本信息ID
	private int limitStorage; // 0:库存无限制；1：限制库存

	//选型
	private List<GoodsSelectionModel> goodsSelection;
	//分店
	private List<GoodsSuppliersModel> goodsSuppliers;
	public String getTokenid() {
		return tokenid;
	}
	public void setTokenid(String tokenid) {
		this.tokenid = tokenid;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getPreGoodsId() {
		return preGoodsId;
	}
	public void setPreGoodsId(Long preGoodsId) {
		this.preGoodsId = preGoodsId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
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
