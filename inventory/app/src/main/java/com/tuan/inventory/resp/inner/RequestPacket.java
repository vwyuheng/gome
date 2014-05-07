package com.tuan.inventory.resp.inner;

import java.io.Serializable;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.resp.AbstractPacket;
import com.tuan.inventory.utils.JsonStrVerificationUtils;


/**
 * @description
 * 窝窝内部统一的请求报文头
 * @author tianzq
 * @date 2013.11.20
 */
@XmlRootElement(name = "Packet")
public class RequestPacket extends AbstractPacket implements Serializable {

	private static final long serialVersionUID = 3335312131615418151L;
	/**
	 * 客户端IP
	 */
	private String ip;
	/**
	 * 客户端名称
	 */
	private String client;
	/**
	 * 时间戳 10位
	 */
	private String t;
	/**
	 * trace父ID
	 */
	private String traceId;
	
	/**
	 * trace根ID
	 */
	private String traceRootId;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	public String getTraceRootId() {
		return traceRootId;
	}
	public void setTraceRootId(String traceRootId) {
		this.traceRootId = traceRootId;
	}
	@Override
	public String toString() {
		return "RequestPacket [ip=" + ip + ", client=" + client + ", t=" + t
				 + ", traceId=" + traceId 
				+ ", traceRootId=" + traceRootId + ", toString()="
				+ super.toString() + "]";
	}
	
	public ResultEnum checkParameter(){
		
		if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(ip))){
			return ResultEnum.INVALID_IP;
		}
		if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(client))){
			return ResultEnum.INVALID_CLIENT;
		}
		if(t == null || t.isEmpty() || Long.valueOf(t).longValue() <= 0){
			return ResultEnum.INVALID_TIME;
		}
		
		return ResultEnum.SUCCESS;
	}
	
	public void addParameterMap(SortedMap<String, String> parameterMap) {
		parameterMap.put("ip", ip);
		parameterMap.put("client", client);
		parameterMap.put("t", t + "");
		parameterMap.put("traceId", traceId);
		parameterMap.put("traceRootId", traceRootId);
		
	}
	
}
