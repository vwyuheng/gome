package com.tuan.inventory.resp.inner;

import java.util.SortedMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resp")
public class GoodsQueryInnerResp extends InnerResp{
	private static final long serialVersionUID = 7564831529850221447L;
	
	/**商品选型详情**/
	private String jsonResult;

	

	public String getJsonResult() {
		return jsonResult;
	}



	public void setJsonResult(String jsonResult) {
		this.jsonResult = jsonResult;
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
