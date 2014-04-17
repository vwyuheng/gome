package com.tuan.inventory.domain.support.util;


import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
/**
 * hessian工具类
 * @author henry.yu
 *
 */
public class HessianProxyUtil {
	
	private static HessianProxyFactory FACTORY = new HessianProxyFactory();
	public static Object getObject(@SuppressWarnings("rawtypes") Class clazz, String url)
			throws MalformedURLException {
		return FACTORY.create(clazz, url);
	}
	
}
