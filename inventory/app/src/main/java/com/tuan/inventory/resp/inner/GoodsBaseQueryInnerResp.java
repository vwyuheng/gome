package com.tuan.inventory.resp.inner;

import java.util.SortedMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.tuan.inventory.model.GoodsBaseModel;

@XmlRootElement(name = "resp")
public class GoodsBaseQueryInnerResp extends InnerResp{
	private static final long serialVersionUID = 7564831529850221447L;
	
	/**商品选型详情**/
	private GoodsBaseModel goodsBaseModel;



	public GoodsBaseModel getGoodsBaseModel() {
		return goodsBaseModel;
	}



	public void setGoodsBaseModel(GoodsBaseModel goodsBaseModel) {
		this.goodsBaseModel = goodsBaseModel;
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
