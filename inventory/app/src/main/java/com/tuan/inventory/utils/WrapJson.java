package com.tuan.inventory.utils;

import net.sf.json.JSONObject;

public class WrapJson {

	public static JSONObject getJsonErrorMsg(String message){
		JSONObject jsonError = new JSONObject();
		jsonError.put("isSuccess", false);
		jsonError.put("errMsg", message);
		return jsonError;
	}
}
