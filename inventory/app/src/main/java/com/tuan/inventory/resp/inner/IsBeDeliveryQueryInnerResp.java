package com.tuan.inventory.resp.inner;

import java.util.SortedMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.tuan.inventory.model.WmsIsBeDeliveryModel;

@XmlRootElement(name = "resp")
public class IsBeDeliveryQueryInnerResp extends InnerResp{
	private static final long serialVersionUID = 7564831529850221447L;
	
	/**商品选型列表**/
	private WmsIsBeDeliveryModel isBeDelivery;
	

	public WmsIsBeDeliveryModel getIsBeDelivery() {
		return isBeDelivery;
	}


	public void setIsBeDelivery(WmsIsBeDeliveryModel isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
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
