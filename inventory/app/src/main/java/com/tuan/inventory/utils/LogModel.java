package com.tuan.inventory.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;

import net.sf.json.JSONObject;

public class LogModel {
	private Map<String, Object> datas;
	private String traceId;
	private final AtomicInteger serialId = new AtomicInteger(0); 
	
	private LogModel() {
		datas = new HashMap<String, Object>();
		traceId = StringUtils.makeSysSn();
		datas.put("_traceId", traceId);
	}
	
	private LogModel(String traceId) {
		datas = new HashMap<String, Object>();
		this.traceId = traceId;
		datas.put("_traceId", traceId);
	}

	public static LogModel newLogModel() {
		return new LogModel();
	}
	
	public static LogModel newLogModel(String traceId) {
		return new LogModel(traceId);
	}

	public LogModel addMetaData(String key, Object value) {
		if (value != null)
			datas.put(key, value);
		else
			datas.put(key, "");
		return this;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : datas.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	public LogModel setMethod(String method){
		datas.put("_method", method);
		return this;
	}

	public String toJson(boolean purge) {
		try {
			datas.put("_serialId", serialId.incrementAndGet());
			if (purge) {
				JSONObject ja = JSONObject.fromObject(datas);
				this.datas.clear();
				datasInit();
				return ja.toString();

			} else {
				Map<String, Object> map = toMap();
				if (map != null) {
					JSONObject ja = JSONObject.fromObject(map);
					return ja.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{data:error}";
	}
	
	private void datasInit(){
		datas.put("_traceId", this.traceId);
	}
	
	public String toJson(Object obj) {
		JSONObject ja = JSONObject.fromObject(obj);
		return ja.toString();
	}
	
	public String endJson() {
		return toJson(true);
	}
	public String toJson() {
		return toJson(true);
	}
	
	public String getTraceId() {
		return traceId;
	}

	public static void main(String[] args){
		Message messageRoot = TraceMessageUtil.newRootMessage();
		LogModel lm = LogModel.newLogModel(messageRoot.getTraceHeader().getRootId());
		lm.addMetaData("aaa", "aaa").addMetaData("bb", "bb").setMethod("ddj");
		System.out.println(lm.toJson());
		System.out.println(lm.setMethod("duandongjun").addMetaData("ttt", "ttt").toJson());
		System.out.println(lm.toJson());
	}
}
