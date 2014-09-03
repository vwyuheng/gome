package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class WmsSelectionInventoryRestParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private String wmsGoodsId;  //物流商品的一种编码
	//选型:这是一个json串 如：[{"limit":0,"id":2},{"limit":0,"id":1}] [List<CreaterGoodsSelectionParam>]
	private String goodsSelection;
	private String goodsId; //商品id
	//商品id是否存在:1代表存在 | 0：代表不存在
	private String isexistgoods;
	
	public String getWmsGoodsId() {
		return wmsGoodsId;
	}
	public void setWmsGoodsId(String wmsGoodsId) {
		this.wmsGoodsId = wmsGoodsId;
	}
	public String getGoodsSelection() {
		return goodsSelection;
	}
	public void setGoodsSelection(String goodsSelection) {
		this.goodsSelection = goodsSelection;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getIsexistgoods() {
		return isexistgoods;
	}
	public void setIsexistgoods(String isexistgoods) {
		this.isexistgoods = isexistgoods;
	}
	
	
	
}
