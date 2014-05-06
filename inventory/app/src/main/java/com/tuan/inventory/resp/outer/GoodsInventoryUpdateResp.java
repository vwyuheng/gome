package com.tuan.inventory.resp.outer;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

/**
 * @description
 * 库存更新写入操作的应答报文头
 * @author henry.yu
 * @date 2014.1.18
 */
@XmlRootElement(name = "resp")
public class GoodsInventoryUpdateResp implements Serializable  {
	
	private static final long serialVersionUID = -6491803731954631586L;
	/**
	 * 出错应答描述
	 */
	private String errorMsg = "";
	/**
	 * 出错应答码
	 */
	private String errorCode = "";
	/**
	 * 应答描述[success||error]
	 */
	private String result = "";
	
	private String queueKeyId = "";

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		
			return "GoodsInventoryUpdateResp result:" + result + "[,errorCode=" + errorCode
					+ ", errorMsg=" + errorMsg + "]";

	}

	public String getQueueKeyId() {
		return queueKeyId;
	}

	public void setQueueKeyId(String queueKeyId) {
		this.queueKeyId = queueKeyId;
	}

	/**
	 * 为应答报文组织数据
	 */
	public SortedMap<String, String> fillRespMap(){
		SortedMap<String, String> reqMap = new TreeMap<String, String>();
		reqMap.put("result", result);
		if(!StringUtils.isEmpty(errorCode)){
			reqMap.put("errorCode", errorCode);
		}
		if(!StringUtils.isEmpty(errorMsg)){
			reqMap.put("errorMsg", errorMsg);
		}
		if(!StringUtils.isEmpty(queueKeyId)){
			reqMap.put("queueKeyId", queueKeyId);
		}
		return reqMap;
	}
}
