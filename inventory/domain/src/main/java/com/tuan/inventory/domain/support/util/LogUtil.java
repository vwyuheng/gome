package com.tuan.inventory.domain.support.util;



import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author wanghua
 * 日志格式转换工具
 * 2012-7-3 下午03:31:44
 */
public class LogUtil {
	//private static Logger logger=Logger.getLogger(LogUtil.class);
	private static Log logger = LogFactory.getLog(LogUtil.class);
	/**
	 *  
	 * @param jsonObj  对象非集合,且要是能转换成json对象
	 * @return json串
	 * 2012-7-3 下午02:12:07 wh
	 */
	public static String formatObjLog(Object jsonObj){
		String json=null;
		try {
			json=JSONObject.fromObject(jsonObj).toString();
			
		} catch (Exception e) {
			Map<String, String> map=new HashMap<String, String>();
			//map.put("errorMsg", Arrays.toString(new ThrowableInformation(e).getThrowableStrRep()));
			map.put("errorMsg", Arrays.toString(e.getStackTrace()));
			logger.error(JSONObject.fromObject(map).toString());
		}
		return json;
	}
	
	public static Object jsonToObject(String message,@SuppressWarnings("rawtypes") Class clazz) {
		return JSONObject.toBean(JSONObject.fromObject(message), clazz);
	}
	/**
	 * 
	 * @param jsonObj  对象为集合
	 * @return json串
	 * 2012-7-3 下午02:21:48 wh
	 */
	public static String formatListLog(Object jsonObj){
		String json=null;
		try {
			json = JSONArray.fromObject(jsonObj).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Map<String, String> map=new HashMap<String, String>();
			//map.put("errorMsg", Arrays.toString(new ThrowableInformation(e).getThrowableStrRep()));
			map.put("errorMsg", Arrays.toString(e.getStackTrace()));
			logger.error(JSONObject.fromObject(map).toString());
		}
		
		return json;
	}

	
    
	/*public static String errorLog(String msg,Exception e){
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
	}*/
	
	public static long getRunTime(long startTime){
		return System.currentTimeMillis() - startTime;
	}
} 
