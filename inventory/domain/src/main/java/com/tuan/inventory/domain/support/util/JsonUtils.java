package com.tuan.inventory.domain.support.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuan.inventory.model.param.rest.TestParam;

public class JsonUtils {

	private static final Log logger = LogFactory.getLog(JsonUtils.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static Type type = new TypeToken<List<TestParam>>(){}.getType();
	/**
	 * 将对象转换为json字符串
	 * 
	 * @param object
	 * @return
	 */
	public static String convertObjectToString(Object object) {
		if (object == null) {
			throw new IllegalArgumentException(
					"invalid argument , object mast be not null!");
		}
		String res = null;
		try {
			res = objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
			throw new RuntimeException("object  conver json exception ");
		}
		return res;
	}

	/**
	 * 将json字符串转换为 对象
	 * 
	 * @param jsonData
	 * @return
	 */
	public static <T> T convertStringToObject(String jsonData,
			Class<T> classType) {
		if (jsonData == null || jsonData.trim().length() == 0) {
			throw new IllegalArgumentException("jsonData is empty");
		}
		if (classType == null) {
			throw new IllegalArgumentException("classType is empty");
		}
		T res = null;
		try {
			res = (T) objectMapper.readValue(jsonData, classType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
			throw new RuntimeException("json conver object exception ");
		}
		return res;
	}
	public static void main(String[] args) {
		List<TestParam> goodsSelection = new ArrayList<TestParam>();
		for(int i=2;i>0;i--) {
			TestParam test = new TestParam();
			test.setId((long) i);
			test.setLimit(0);
			goodsSelection.add(test);
		}
		String jsonResult = JsonUtils.convertObjectToString(goodsSelection);
		System.out.println(jsonResult);
		List<TestParam> tet1= (List<TestParam>)new Gson().fromJson(jsonResult, type);
		
		System.out.println("tet1="+tet1+",size="+tet1.size());
	}
	
}
