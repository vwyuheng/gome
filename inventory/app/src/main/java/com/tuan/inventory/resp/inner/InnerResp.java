package com.tuan.inventory.resp.inner;

import java.io.Serializable;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @description
 * 窝窝内部统一的应答报文头
 * @author tianzq
 * @date 2013.11.20
 */
@XmlRootElement(name = "resp")
public class InnerResp implements Serializable  {

	private static final long serialVersionUID = -4053677814027979227L;
	/**
	 * 应答码
	 */
	private String respCode;
	/**
	 * 应答描述
	 */
	private String respMessage;
	
	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMessage() {
		return respMessage;
	}

	public void setRespMessage(String respMessage) {
		this.respMessage = respMessage;
	}
	
	public void setResult(String respCode,String respMessage) {
		setRespCode(respCode);
		setRespMessage(respMessage);
	}
	
	@Override
	public String toString() {
		return "InnerResp [respCode=" + respCode + ", respMessage="
				+ respMessage + "]";
	}
	
	/**
	 * 为相应签名填充参数
	 * @param reqMap
	 * @param resp
	 */
	public void addHeadParameMap4Resp(SortedMap<String, String> reqMap){
		reqMap.put("respCode", respCode);
		reqMap.put("respMessage", respMessage);
	}
}
