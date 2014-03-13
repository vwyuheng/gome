package com.tuan.inventory.domain.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.model.GoodsSelectionRelationModel;
/**
 * 用于库存相关的读取服务接口
 * @author henry.yu
 * @date 2014/3/13
 */
public class InventoryProviderReadServiceImpl implements
		InventoryProviderReadService {

	@Resource 
	JedisSentinelPool jedisSentinelPool;
	@Resource
	NullCacheInitService nullCacheInitService;
	private static Log log = LogFactory.getLog(InventoryProviderReadServiceImpl.class);
	
	@Override
	public GoodsSelectionRelationDO getSelectionRelationBySrId(
			int SelectionRelationId) throws Exception {
		log.info("InventoryProviderReadService.getSelectionRelationBySrId:"+"SelectionRelationId="+SelectionRelationId);
		Jedis jedis = jedisSentinelPool.getResource();
		GoodsSelectionRelationDO result = null;
		if (jedis == null)
			return result;
		// TODO 测试用
		// jedis.del(String.valueOf(SelectionRelationId));
		try {
			Map<String, String> objMap = jedis.hgetAll(InventoryEnum.HASHCACHE+":"+String
					.valueOf(SelectionRelationId));
			if (!CollectionUtils.isEmpty(objMap)) { // if1
				//result = new GoodsSelectionRelationDO();
				result = (GoodsSelectionRelationDO) ObjectUtil.convertMap(
						GoodsSelectionRelationDO.class, objMap);

			} else {
				result = this.nullCacheInitService.initSelectionRelation(jedis,
						SelectionRelationId);
			}
		} catch (Exception e) {
			log.error(
					"GoodsSelectionRelationDO:InventoryDeductReadWriteService.getSelectionRelationBySrId invoke error [SelectionRelationId="
							+ SelectionRelationId + "]", e);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public GoodsSuppliersInventoryDO getSuppliersInventoryBySiId(
			int SuppliersInventoryId) throws Exception {
		log.info("InventoryProviderReadService.getSuppliersInventoryBySiId:"+"SuppliersInventoryId="+SuppliersInventoryId);
		Jedis jedis = jedisSentinelPool.getResource();
		GoodsSuppliersInventoryDO result = null;
		if(jedis== null)
			 return null;
		//TODO 测试用
		//jedis.del(String.valueOf(SelectionRelationId));
		try {
			Map<String, String> objMap = jedis.hgetAll(InventoryEnum.HASHCACHE+":"+String.valueOf(SuppliersInventoryId));
			if(!CollectionUtils.isEmpty(objMap)){ //if1
			//result = new GoodsSuppliersInventoryDO();
			result	= (GoodsSuppliersInventoryDO) ObjectUtil.convertMap(GoodsSuppliersInventoryDO.class, objMap);	
			
			}else {result = this.nullCacheInitService.initSuppliersInventory(jedis, SuppliersInventoryId);}
		
		} catch (Exception e) {
			log.error("GoodsSuppliersInventoryDO:InventoryDeductReadWriteService.getSuppliersInventoryBySiId invoke error [SuppliersInventoryId="
					+ SuppliersInventoryId + "]", e);
			e.printStackTrace();
		}finally {  //释放资源
			if(jedis!=null)
				jedisSentinelPool.returnResource(jedis);
		}
		return result;
	}

	@Override
	public List<GoodsSelectionRelationModel> getSelectionRelationBySrIds(
			List<Long> selectionRelationIdList,long goodsId) throws Exception {
		log.info("InventoryProviderReadService.getSelectionRelationBySrIds:"+"goodsId="+goodsId);
		Jedis jedis = jedisSentinelPool.getResource();
		List<GoodsSelectionRelationModel> result = null;
		if (jedis == null)
			return result;
		 if (!CollectionUtils.isEmpty(selectionRelationIdList)) {
			 result = new ArrayList<GoodsSelectionRelationModel>();
			 for(Long lIds:selectionRelationIdList) {
				 RedisGoodsSelectionRelationDO resultTmp = null;
				 Map<String, String> objMap = jedis.hgetAll(InventoryEnum.HASHCACHE+":"+String.valueOf(lIds));
				 if(!CollectionUtils.isEmpty(objMap)){ 
					 resultTmp	= (RedisGoodsSelectionRelationDO) ObjectUtil.convertMap(RedisGoodsSelectionRelationDO.class, objMap);
					 if(resultTmp!=null) {
						 result.add(this.asembly(resultTmp));
					 }
					
				 }else {
					 resultTmp = this.nullCacheInitService.initSRelation(jedis,
							 lIds);
					 if(resultTmp!=null) {
						 result.add(this.asembly(resultTmp));
					 }
				 }
			 }
		 }
		return result;
	}

	@Override
	public RedisInventoryDO getNotSeleInventory(long goodsId)
			throws Exception {
		log.info("InventoryProviderReadService.getNotSeleInventory:"+"goodsId="+goodsId);
		Jedis jedis = jedisSentinelPool.getResource();
		RedisInventoryDO result = null;
		if (jedis == null)
			return result;
		Map<String, String> objMap = jedis.hgetAll(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId));
		
		if(!CollectionUtils.isEmpty(objMap)){ //if1
			//result = new RedisInventoryDO();
			result	= (RedisInventoryDO) ObjectUtil.convertMap(RedisInventoryDO.class, objMap);	
			
			}else {result = this.nullCacheInitService.initGoodsInventoryNotSelectionOrSuppliers(jedis, goodsId);}
		
		//释放资源
		if(jedis!=null)
			jedisSentinelPool.returnResource(jedis);
		
		return result;
	}
	/**
	 * 装配选型model对象信息
	 * @param resultTmp
	 * @return
	 */
	private  GoodsSelectionRelationModel asembly(RedisGoodsSelectionRelationDO resultTmp) {
		 GoodsSelectionRelationModel gsrModel = new GoodsSelectionRelationModel();
		 gsrModel.setId(resultTmp.getId());
		 gsrModel.setGoodId(resultTmp.getGoodsId());
		 gsrModel.setLeftNumber(resultTmp.getLeftNumber());
		 gsrModel.setTotalNumber(resultTmp.getTotalNumber());
		 gsrModel.setLimitStorage(resultTmp.getLimitStorage());
		 gsrModel.setGoodTypeId(resultTmp.getGoodTypeId());
		 
		 return gsrModel;
	}
}
