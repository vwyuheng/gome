package com.tuan.inventory.domain.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.jedistools.JedisFactory;
import com.tuan.inventory.domain.support.jedistools.JedisFactory.JWork;
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
	private static Log log = LogFactory.getLog(InventoryProviderReadServiceImpl.class);
	
	@Resource
	JedisFactory jedisFactory;
	@Resource
	NullCacheInitService nullCacheInitService;
	
	@Override
	public GoodsSelectionRelationDO getSelection(
			final int SelectionRelationId) throws Exception {
		
		log.info("InventoryProviderReadService.getSelectionRelationBySrId:"+"SelectionRelationId="+SelectionRelationId);
		
		return jedisFactory.withJedisDo(new JWork<GoodsSelectionRelationDO>() 
				{
					@Override
					public GoodsSelectionRelationDO work(Jedis j)
					{
						GoodsSelectionRelationDO result = null;
						try {
							// TODO 测试用
							//j.del(String.valueOf(InventoryEnum.HASHCACHE+ ":"+ SelectionRelationId));
							Map<String, String> objMap = j
									.hgetAll(InventoryEnum.HASHCACHE
											+ ":"
											+ String.valueOf(SelectionRelationId));
							if (!CollectionUtils.isEmpty(objMap)) { // if1

								result = (GoodsSelectionRelationDO) ObjectUtil
										.convertMap(
												GoodsSelectionRelationDO.class,
												objMap);

							} else {
								result = nullCacheInitService
										.initSelectionRelation(j,
												SelectionRelationId);
								
							}
							
						} catch (Exception e) {
							// TODO: handle exception
							log.error(
									"GoodsSelectionRelationDO:InventoryDeductReadWriteService.getSelectionRelationBySrId invoke error [SelectionRelationId="
											+ SelectionRelationId + "]", e);
						}
						return result;
					}			
				});
		
	}

	@Override
	public GoodsSelectionRelationDO getSelectionRelationBySrId(
			int SelectionRelationId) throws Exception {
		log.info("InventoryProviderReadService.getSelectionRelationBySrId:"+"SelectionRelationId="+SelectionRelationId);
		//Jedis jedis = jedisSentinelPool.getResource();
		Jedis jedis = JedisFactory.getRes();
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
		}finally {
			//将连接释放，还回到池子
			if(jedis!=null)
				JedisFactory.returnRes(jedis);
		}
		return result;
	}

	@Override
	public GoodsSuppliersInventoryDO getSuppliersInventoryBySiId(
			int SuppliersInventoryId) throws Exception {
		log.info("InventoryProviderReadService.getSuppliersInventoryBySiId:"+"SuppliersInventoryId="+SuppliersInventoryId);
//		Jedis jedis = jedisSentinelPool.getResource();
		Jedis jedis = JedisFactory.getRes();
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
				JedisFactory.returnRes(jedis);
//				jedisSentinelPool.returnResource(jedis);
		}
		return result;
	}

	@Override
	public List<GoodsSelectionRelationModel> getSelectionRelationBySrIds(
			List<Long> selectionRelationIdList,long goodsId) throws Exception {
		log.info("InventoryProviderReadService.getSelectionRelationBySrIds:"+"goodsId="+goodsId);
//		Jedis jedis = jedisSentinelPool.getResource();
		Jedis jedis = JedisFactory.getRes();
		List<GoodsSelectionRelationModel> result = null;
		if (jedis == null)
			return result;
		 if (!CollectionUtils.isEmpty(selectionRelationIdList)) { //if1
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
		 }//if1
		//释放资源
		 if (jedis != null)
			 JedisFactory.returnRes(jedis);
//			 jedisSentinelPool.returnResource(jedis);
		return result;
	}

	@Override
	public RedisInventoryDO getNotSeleInventory(long goodsId)
			throws Exception {
		log.info("InventoryProviderReadService.getNotSeleInventory:"+"goodsId="+goodsId);
//		Jedis jedis = jedisSentinelPool.getResource();
		Jedis jedis = JedisFactory.getRes();
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
			JedisFactory.returnRes(jedis);
//			jedisSentinelPool.returnResource(jedis);
		
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
