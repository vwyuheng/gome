package com.tuan.inventory.domain.base;

import java.util.SortedMap;
import java.util.TreeMap;

import com.tuan.inventory.model.enu.ResultEnum;

public abstract class AbstractGoodsInventoryDomain {
	public final static String clientName = "Inventory-app";
	public final static String clientIp = 	"127.0.0.1";
	public SortedMap<String, String> parameterMap = new TreeMap<String, String>();
	public SortedMap<String, String> parameterRespMap = new TreeMap<String, String>();
	
	/**
	 * 接口参数校验
	 * @return
	 */
	public abstract ResultEnum checkParameter();
	
	/**
	 * 业务处理
	 * @return
	 */
	public abstract ResultEnum doBusiness();
	
	/**
	 * 拼装返回值
	 * @return
	 */
	public abstract Object makeResult(ResultEnum resultStatusEnum);
	
	/**
	 * 将wowo传入的参数添加到SortedMap中。
	 * @param parameterMap
	 */
	public abstract void makeParameterMap(SortedMap<String, String> parameterMap) ;
}
