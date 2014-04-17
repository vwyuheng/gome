package com.tuan.inventory.resp.inner;

import java.util.SortedMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.tuan.inventory.model.GoodsSuppliersModel;

@XmlRootElement(name = "resp")
public class GoodsSuppliersQueryInnerResp extends InnerResp{
	private static final long serialVersionUID = 7564831529850221447L;
	
	/**商品选型详情**/
	private GoodsSuppliersModel goodsSuppliers;

	public GoodsSuppliersModel getGoodsSuppliers() {
		return goodsSuppliers;
	}

	public void setGoodsSuppliers(GoodsSuppliersModel goodsSuppliers) {
		this.goodsSuppliers = goodsSuppliers;
	}

	/**
	 * 为相应签名填充参数
	 * @param reqMap
	 * @param resp
	 */
	public void addHeadParameMap4Resp(SortedMap<String, String> reqMap){
		
		super.addHeadParameMap4Resp(reqMap);
	}
	
}
