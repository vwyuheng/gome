package com.tuan.inventory.domain.support.logs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONObject;

public class LogModel {
	/** 附加信息集 */
	Map<String, Object> datas;
	String method;
	long start=0;
	private final AtomicInteger serialId = new AtomicInteger(0);   
	private LogModel(String name) {
		datas = new HashMap<String, Object>();
		start=System.currentTimeMillis();
		method = name + "#" +start  + "#";
		datas.put("_method", method);
	}

	public static LogModel newLogModel(String method) {
		return new LogModel(method);
	}

	public LogModel setResultMessage(long result, String message) {
		addMetaData("_result", result).addMetaData("_message", message);
		return this;
	}

	public LogModel addMetaData(String key, Object value) {
		if (value != null)
			datas.put(key, value);
		else
			datas.put(key, "");
		return this;
	}
	public LogModel delMetaData(String key) {
		if (key != null)
			datas.remove(key); 		
		return this;
	}
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : datas.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	public String toJson(boolean purge) {
		try {
			datas.put("_serialId", serialId.incrementAndGet());
			if (purge) {
				datas.put("handleCost", System.currentTimeMillis()-start);
				JSONObject ja = JSONObject.fromObject(datas);
				purge();
				return ja.toString();

			} else {
				Map<String, Object> map = toMap();
				map.put("handleCost", System.currentTimeMillis()-start);
				if (map != null) {
					JSONObject ja = JSONObject.fromObject(map);
					return ja.toString();
				}
			}
		} catch (Exception e) {
//			System.out.println(e.getMessage()+"### datas ="+datas);
			e.printStackTrace();
		}

		return "{data:error}";
	}

	private void purge() {
		this.datas.clear();
		datas.put("_method", method);

	}
	public String endJson() {
		return toJson(true);

	}
	public String toJson() {
		return toJson(true);

	}
}
