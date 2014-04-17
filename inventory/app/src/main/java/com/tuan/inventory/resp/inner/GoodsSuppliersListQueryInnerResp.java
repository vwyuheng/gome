package com.tuan.inventory.resp.inner;

import java.util.List;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.tuan.inventory.model.GoodsSuppliersModel;

@XmlRootElement(name = "resp")
public class GoodsSuppliersListQueryInnerResp extends InnerResp{
	private static final long serialVersionUID = 7564831529850221447L;
	
	/**商品选型列表**/
	private List<GoodsSuppliersModel> gSuppliersList;

	
	public List<GoodsSuppliersModel> getgSuppliersList() {
		return gSuppliersList;
	}


	public void setgSuppliersList(List<GoodsSuppliersModel> gSuppliersList) {
		this.gSuppliersList = gSuppliersList;
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
