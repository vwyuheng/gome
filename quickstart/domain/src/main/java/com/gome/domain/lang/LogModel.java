package com.gome.domain.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;

public class LogModel {
	private Map<String, Object> datas;
	private String traceId;
	private final AtomicInteger serialId = new AtomicInteger(0); 
	
	private LogModel(String traceId) {
		datas = new HashMap<String, Object>();
		this.traceId = traceId;
		datas.put("_traceId", traceId);
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
				this.datas.clear();
				datasInit();
				return JSON.toJSONString(datas);

			} else {
				Map<String, Object> map = toMap();
				if (map != null) {
					return JSON.toJSONString(map);
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
		return JSON.toJSONString(obj);
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
}
