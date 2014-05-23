package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品物流数据更新的参数对象
 * @author henry.yu
 * @date 20140519
 */
public class UpdateWmsDataParam extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private String tokenid;  //redis序列,解决接口幂等问题
	private String goodsId;// 商品ID(FK)
	private String suppliersId;// 分店ID(FK)
	private String wmsGoodsId;  //物流商品的一种编码
	private String goodsTypeIds;  //选项类型表id集合,以逗号分隔开；如：1,2,3或2
	private String goodsSelectionIds;  //共享库存时所绑定的选型表ID集合，以逗号分隔开；如：1,2,3或2
	private int isBeDelivery;  //物流仓库类型 0:wowo   1:shangjia
	private int isAddGoodsSelection; // 商品是否添加配型 0：不添加；1：添加
	
	
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(String suppliersId) {
		this.suppliersId = suppliersId;
	}
	public String getWmsGoodsId() {
		return wmsGoodsId;
	}
	public void setWmsGoodsId(String wmsGoodsId) {
		this.wmsGoodsId = wmsGoodsId;
	}
	public String getGoodsSelectionIds() {
		return goodsSelectionIds;
	}
	public void setGoodsSelectionIds(String goodsSelectionIds) {
		this.goodsSelectionIds = goodsSelectionIds;
	}
	public int getIsAddGoodsSelection() {
		return isAddGoodsSelection;
	}
	public void setIsAddGoodsSelection(int isAddGoodsSelection) {
		this.isAddGoodsSelection = isAddGoodsSelection;
	}
	public String getGoodsTypeIds() {
		return goodsTypeIds;
	}
	public void setGoodsTypeIds(String goodsTypeIds) {
		this.goodsTypeIds = goodsTypeIds;
	}
	public int getIsBeDelivery() {
		return isBeDelivery;
	}
	public void setIsBeDelivery(int isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
	}
	public String getTokenid() {
		return tokenid;
	}
	public void setTokenid(String tokenid) {
		this.tokenid = tokenid;
	}
	
	
	
}
