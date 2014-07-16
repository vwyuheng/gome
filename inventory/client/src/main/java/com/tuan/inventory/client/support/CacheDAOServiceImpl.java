package com.tuan.inventory.client.support;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.tuan.inventory.client.support.jedistools.RedisClientCacheUtil;
import com.tuan.inventory.client.support.utils.LogModel;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.util.QueueConstant;

public class CacheDAOServiceImpl implements CacheDAOService {
	private static final Log logger = LogFactory.getLog("INVENTORY.CLIENT.LOG");
	@Resource
	RedisClientCacheUtil redisCacheUtil;
	
	@Override
	public GoodsInventoryModel queryGoodsInventory(Long goodsId) {
		long startTime = System.currentTimeMillis();
		String method = "queryGoodsInventory";
		final LogModel lm = LogModel.newLogModel(method);
		logger.info(lm.setMethod(method).addMetaData("start", startTime)
				.toJson(true));
		try {
			Map<String, String> objMap = this.redisCacheUtil
					.hgetAll(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
							+ String.valueOf(goodsId));
			if (!CollectionUtils.isEmpty(objMap)) {
				
				return JSON.parseObject(JSON.toJSONString(objMap),
						GoodsInventoryModel.class);
			}
		} finally {
			long endTime = System.currentTimeMillis();
			String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
					+ "milliseconds(毫秒)执行完成!";
			logger.info(lm.setMethod(method).addMetaData("endTime", endTime)
					.addMetaData("runResult", runResult).toJson(true));
		}
		return null;
	}

	
	/*@Override
	public GoodsInventoryWMSDO queryWmsInventoryById(String wmsGoodsId) {
		long startTime = System.currentTimeMillis();
		String method = "queryWmsInventoryById";
		final LogModel lm = LogModel.newLogModel(method);
		logger.info(lm.setMethod(method).addMetaData("start", startTime)
				.toJson(true));
		try {
			Map<String, String> objMap = this.redisCacheUtil
					.hgetAll(QueueConstant.WMS_INVENTORY_PREFIX + ":"
							+ wmsGoodsId);
			if (!CollectionUtils.isEmpty(objMap)) {
				return JsonUtils.convertStringToObject(
						JsonUtils.convertObjectToString(objMap),
						GoodsInventoryWMSDO.class);
				return JSON.parseObject(JSON.toJSONString(objMap),
						GoodsInventoryWMSDO.class);
			}
		} finally {
			long endTime = System.currentTimeMillis();
			String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
					+ "milliseconds(毫秒)执行完成!";
			logger.info(lm.setMethod(method).addMetaData("endTime", endTime)
					.addMetaData("runResult", runResult).toJson(true));
		}
		return null;
	}
*/
	
	
	@Override
	public GoodsBaseModel queryGoodsBaseById(Long goodsBaseId) {
		long startTime = System.currentTimeMillis();
		String method = "queryGoodsBaseById";
		final LogModel lm = LogModel.newLogModel(method);
		logger.info(lm.setMethod(method).addMetaData("start", startTime)
				.toJson(true));
		try {
			Map<String, String> objMap = this.redisCacheUtil
					.hgetAll(QueueConstant.GOODS_BASE_INVENTORY_PREFIX + ":"
							+ String.valueOf(goodsBaseId));
			if (!CollectionUtils.isEmpty(objMap)) {
				
				return JSON.parseObject(JSON.toJSONString(objMap),
						GoodsBaseModel.class);
			}
		} finally {
			long endTime = System.currentTimeMillis();
			String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
					+ "milliseconds(毫秒)执行完成!";
			logger.info(lm.setMethod(method).addMetaData("endTime", endTime)
					.addMetaData("runResult", runResult).toJson(true));
		}
		return null;
	}

}
