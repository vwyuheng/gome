package com.tuan.inventory.job.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description
 * 窝窝内部统一的请求报文头
 * @author tianzq
 * @date 2013.11.20
 */
@XmlRootElement(name = "Packet")
public class RequestPacket implements Serializable {

	private static final long serialVersionUID = 3335312131615418151L;
	/**
	 * 客户端IP
	 */
	private String ip;
	/**
	 * 客户端名�?
	 */
	private String client;
	/**
	 * 时间�?10�?
	 */
	private String t;
	
	/**
	 * 合作�?
	 */
	private String cooperator;
	
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
	
	public String getCooperator() {
		return cooperator;
	}
	public void setCooperator(String cooperator) {
		this.cooperator = cooperator;
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
				+ ", cooperator=" + cooperator + ", traceId=" + traceId 
				+ ", traceRootId=" + traceRootId + ", toString()="
				+ super.toString() + "]";
	}
	
	
	
}
