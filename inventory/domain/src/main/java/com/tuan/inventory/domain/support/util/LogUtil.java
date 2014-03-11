package com.tuan.inventory.domain.support.util;



import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.ThrowableInformation;
/**
 * 
 * @author wanghua
 * ��־��ʽת������
 * 2012-7-3 ����03:31:44
 */
public class LogUtil {
	private static Logger logger=Logger.getLogger(LogUtil.class);
	
	/**
	 *  
	 * @param jsonObj  ����Ǽ���,��Ҫ����ת����json����
	 * @return json��
	 * 2012-7-3 ����02:12:07 wh
	 */
	public static String formatObjLog(Object jsonObj){
		String json=null;
		try {
			json=JSONObject.fromObject(jsonObj).toString();
			
		} catch (Exception e) {
			Map<String, String> map=new HashMap<String, String>();
			map.put("errorMsg", Arrays.toString(new ThrowableInformation(e).getThrowableStrRep()));
			logger.error(JSONObject.fromObject(map).toString());
		}
		return json;
	}
	
	public static Object jsonToObject(String message,@SuppressWarnings("rawtypes") Class clazz) {
		return JSONObject.toBean(JSONObject.fromObject(message), clazz);
	}
	/**
	 * 
	 * @param jsonObj  ����Ϊ����
	 * @return json��
	 * 2012-7-3 ����02:21:48 wh
	 */
	public static String formatListLog(Object jsonObj){
		String json=null;
		try {
			json = JSONArray.fromObject(jsonObj).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Map<String, String> map=new HashMap<String, String>();
			map.put("errorMsg", Arrays.toString(new ThrowableInformation(e).getThrowableStrRep()));
			logger.error(JSONObject.fromObject(map).toString());
		}
		
		return json;
	}

	
    
	public static String errorLog(String msg,Exception e){
		Map<String, String> map=new HashMap<String, String>();
		map.put("errorName", msg);
		map.put("errorMsg", Arrays.toString(new ThrowableInformation(e).getThrowableStrRep()));
		try {
			return JSONObject.fromObject(map).toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
} 