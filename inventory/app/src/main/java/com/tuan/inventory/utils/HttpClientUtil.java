package com.tuan.inventory.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpClientUtil {
	//private static Logger logger = Logger.getLogger(HttpClientUtil.class);
	private static Log logger = LogFactory.getLog(HttpClientUtil.class);
	public static String sendPost( Map<String, String> params,String url,String encode,LogModel lm) throws Exception {
		String method = "HttpClientUtil.sendPost";
		String response = null;
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setContentCharset(encode);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(3000);
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		NameValuePair[] postData = new NameValuePair[params.size()];// POST需要通过NameValuePair类来设置参数名称和它所对应的值
		if (params != null) {
			int count = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				postData[count] = new NameValuePair(entry.getKey(),
						entry.getValue());
				count++;
			}
			postMethod.addParameters(postData);
		}
		try {
			long startTime = System.currentTimeMillis();
			logger.info(lm.setMethod(method).addMetaData("Parameters", postData).addMetaData("url", url).toJson());
			httpClient.executeMethod(postMethod);// 执行，类似于回车键
			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
			}
			logger.info(lm.setMethod(method)
					.addMetaData("StatusCode", postMethod.getStatusCode())
					.addMetaData("response", response)
					.addMetaData("runTimes", DateTimeUtils.getRunTime(startTime)).toJson());
		} catch (Exception e) {
			throw new Exception("url:" + url + "postData:" + postData ,e);
		} finally {
			postMethod.releaseConnection();// 释放
		}
		return response;
	}
	
	public static String sendGet(String url,String encode,LogModel lm) throws Exception {
		String method = "HttpClientUtil.sendGet";
		String response = null;
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setContentCharset(encode);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(3000);
	    GetMethod getMethod = new GetMethod(url);
	    getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		try {
			long startTime = System.currentTimeMillis();
			logger.info(lm.setMethod(method).addMetaData("url", url).toJson());
			httpClient.executeMethod(getMethod);// 执行，类似于回车键
			if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = getMethod.getResponseBodyAsString();
			}
			logger.info(lm.setMethod(method).addMetaData("StatusCode", getMethod.getStatusCode())
					.addMetaData("response", response)
					.addMetaData("runTimes", DateTimeUtils.getRunTime(startTime)).toJson());
		} catch (Exception e) {
			throw new Exception("url:" + url ,e);
		} finally {
			getMethod.releaseConnection();// 释放
		}
		return response;
	}
	
	public static void makeParameterMap(
			HttpServletRequest request,SortedMap<String, String> parameterMap){
		Enumeration<String> e = request.getParameterNames();
        String parameterName, parameterValue;
        while(e.hasMoreElements()){
            parameterName = e.nextElement();
            parameterValue = request.getParameter(parameterName);
            parameterMap.put(parameterName, parameterValue);
        }
	}
	
	public static void main(String[] args){
		final LogModel lm = LogModel.newLogModel();
		Map<String,String> map = new HashMap<String,String>();
		try {
			System.out.println(sendPost(map, "http://10.9.241.18:8080/rest/j/card/sign", "UTF-8",lm));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
