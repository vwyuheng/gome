package com.tuan.inventory.domain.repository.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.repository.InventoryDeductReadWriteService;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.OrderGoodsSelectionModel;
import com.tuan.inventory.model.enu.InventoryEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
/**
 * 库存服务
 * @author henry.yu
 * @date 2014/03/10
 */
public class InventoryDeductReadWriteServiceImpl implements InventoryDeductReadWriteService {
	@Resource 
	JedisSentinelPool jedisSentinelPool;
	@Resource
	NullCacheInitService nullCacheInitService;
	
	private static Log log = LogFactory.getLog(InventoryDeductReadWriteServiceImpl.class);
	
	@Override
	public boolean createInventory(long goodsId,RedisInventoryDO riDO,List<RedisGoodsSelectionRelationDO> rgsrList,List<RedisGoodsSuppliersInventoryDO> rgsiList) throws Exception {
		Jedis jedis = jedisSentinelPool.getResource();
		boolean result = false;
		if(jedis== null) 
			return result;
			//开启事务
		 Transaction	ts = jedis.multi();
		 
		 if(jedis.exists(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId))){  //已存在返回false
			 return result;
		 }
			//1.保存商品库存主体信息
			String redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), ObjectUtil.convertBean(riDO));
			//商品是否添加配型 0：不添加；1：添加
			if(riDO.getIsAddGoodsSelection()==1) {  
				if(!CollectionUtils.isEmpty(rgsrList)) {
					//jedis.sadd(String.valueOf(goodsId), StringUtil.getSelectionRelationString(rgsrList));
					for(RedisGoodsSelectionRelationDO rgsrDO :rgsrList) {
						//2.商品id与选型关系
						jedis.sadd(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId),String.valueOf(rgsrDO.getId()));
						//3.保存选型库存主体信息
						redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(rgsrDO.getId()), ObjectUtil.convertBean(rgsrDO));
					}
					
				}
			}
			//商品销售是否需要指定分店 0：不指定；1：指定
			if(riDO.getIsDirectConsumption()==1) { 
				if(!CollectionUtils.isEmpty(rgsiList)) {
					//jedis.sadd(String.valueOf(goodsId), StringUtil.getSuppliersInventoryString(rgsiList));
					for(RedisGoodsSuppliersInventoryDO rgsiDO:rgsiList){
						//2.商品与商家分店关系
						jedis.sadd(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId),String.valueOf(rgsiDO.getId()));
						//3.保存商品分店库存主体信息
						redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(rgsiDO.getId()), ObjectUtil.convertBean(rgsiDO));
					}
				}
			}
			//执行事务
			//List<Object> redisAckList = 
					ts.exec();
			if(!redisAck.equalsIgnoreCase("ok")){
				log.error("InventoryRelationMainTainService:createInventory invoke error [goodsId="
						+ goodsId + "]");
			}else {
				//TODO 发送库存新增消息
				
				result = true;
			}
		
			if(ts!=null)
			   ts.discard();
			if(jedis!=null) 
				jedisSentinelPool.returnResource(jedis);
			//log.error("InventoryRelationMainTainService:createInventory invoke error [goodsId="
				//	+ goodsId + "]");
		
		return result;
	}

	@Override
	public String insertSingleInventory(long goodsId, String field, String value)
			throws Exception {
		Jedis jedis = jedisSentinelPool.getResource();
		String result = null;
		if(jedis== null) 
			return result;
		//新增返回1   :hsetnx 若域 field 已经存在，该操作无效。
		Long redisAck = jedis.hsetnx(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), field,value);
		if(redisAck==1){
			result = ResultStatusEnum.INSERT.getCode();
		}
		return result;
	}

	@Override
	public String updateSingleInventory(long goodsId, String field, String value)
			throws Exception {
		Jedis jedis = jedisSentinelPool.getResource();
		String result = null;
		if(jedis== null) 
			return result;
		//更新操作
		jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId));
		if(jedis.hexists(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), field)){
			//开启事务
			 Transaction	ts = jedis.multi();
			 Long redisAck = jedis.hset(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), field,value);
			//执行事务
			ts.exec();
			if(redisAck==1)
				result = ResultStatusEnum.UPDATE.getCode();
		}else {
			//保证下一个事务的执行不受影响
			jedis.unwatch();
			//TODO 初始化
			result = ResultStatusEnum.NOEXISTS.getCode();
		}
		
		return result;
	}

	@Override
	public boolean deleteInventory(long goodsId,List<OrderGoodsSelectionModel> goodsSelectionList) throws Exception {
		Jedis jedis = jedisSentinelPool.getResource();
		boolean result = false;
		if(jedis== null) 
			return result;
		Transaction	ts = null;
		long redisAck = 0;
		//级联删除操作
		jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
		if(jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
			//开启事务
			 ts = jedis.multi();
			 if (!CollectionUtils.isEmpty(goodsSelectionList)) {//if2
					for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {//if3
						if (orderGoodsSelectionModel.getSelectionRelationId()!= null
								&& orderGoodsSelectionModel.getSelectionRelationId()> 0) {  //更新商品选型库存
							//1.首先根据商品id检查获商品是否添加配型关系 
						    Set<String> setSelectionIds = jedis.smembers(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
						    if(!CollectionUtils.isEmpty(setSelectionIds)){
						    	for(String id:setSelectionIds){
						    		//根据选型id删除选型库存主体信息
							    	jedis.del(InventoryEnum.HASHCACHE+":"+id);
						    	}
						    	
						    }
						}
						
						if(orderGoodsSelectionModel.getSuppliersId()>0){  //更新商品分店的库存
							 //2.根据商品id检查商品销售是否指定分店
						    Set<String> setSuppliersIds = jedis.smembers(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
						    if(!CollectionUtils.isEmpty(setSuppliersIds)){
						    	for(String id:setSuppliersIds){
						    		//根据选型id删除选型库存主体信息
							    	jedis.del(InventoryEnum.HASHCACHE+":"+id);
						    	}
						    	
						    }
						}
					}//if3
				}//if2
			    //3.最后删除商品库存主体信息、选型库存主体信息及商品分店库存信息
			    redisAck = jedis.del(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
				//执行事务
				ts.exec();
		 }else {//if1
				//保证下一个事务的执行不受影响
				jedis.unwatch();
			}
		 
		 if(redisAck>0){
               //TODO 发送库存新增消息
				
				result = true;
			}
		 
			if(ts!=null)
			   ts.discard();
			if(jedis!=null) 
				jedisSentinelPool.returnResource(jedis);
			
		return result;
	}

	@Override
	public boolean updateInventory(long orderId,long goodsId,  int pindaoId,int num, int limitStorage,
			List<OrderGoodsSelectionModel> goodsSelectionList)
			throws Exception {
		log.info("orderId="+orderId+"goodsId="+goodsId+"pindoId="+pindaoId+"num="+num+"limitStorage="+limitStorage);
		Jedis jedis = jedisSentinelPool.getResource();
		boolean result = false;
		if(jedis== null) 
			return result;
		//级联更新操作
		jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));	
		//开启事务
		 Transaction	ts = jedis.multi();
		 long resultAck = 0;
		 if(goodsId>0&&limitStorage==1){
			 if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),"leftNumber")){  //key field 域不存在 初始化
				      this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
				}
				 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), "leftNumber", (-num));
				 if(resultAck<0) { //说明本次交易的库存不足
					 //还原扣减的库存
					 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), "leftNumber", (num));
					 return result;
				 }
			 
		 }
		 
		if (!CollectionUtils.isEmpty(goodsSelectionList)) {
			for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {
				if (orderGoodsSelectionModel.getSelectionRelationId()!= null
						&& orderGoodsSelectionModel.getSelectionRelationId()> 0) {  //更新商品选型库存
					if(!jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
						 this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
					}
					//1.首先根据商品id检查获商品是否添加配型关系 
				    Set<String> setSelectionIds = jedis.smembers(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
				    if(!CollectionUtils.isEmpty(setSelectionIds)){
				    	for(String id:setSelectionIds){
				    		if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+id,"leftNumber")){
				    			this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
								}
				    		//根据选型id删除选型库存主体信息
				    	    resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+id,"leftNumber", (-num));
				    	    if(resultAck<0) { //说明本次交易的库存不足
				    	    	//还原扣减的库存
				    	    	resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+id,"leftNumber", (num));
								 return result;
							 }
				    	}
				    	
				    }
				}
				
				if(orderGoodsSelectionModel.getSuppliersId()>0){  //更新商品分店的库存
					if(!jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
						this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
					}
					//2.根据商品id检查商品销售是否指定分店
				    Set<String> setSuppliersIds = jedis.smembers(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
				    if(!CollectionUtils.isEmpty(setSuppliersIds)){
				    	for(String id:setSuppliersIds){
				    		if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+id,"leftNumber")){
				    			this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
								
								}
				    		//根据选型id删除选型库存主体信息
				    		 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+id,"leftNumber", (-num)); 
				    		 if(resultAck<0) { //说明本次交易的库存不足
				    			//还原扣减的库存
				    			 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+id,"leftNumber", (num)); 
								 return result;
							 }
				    	}
				    	
				    }
				}
			}
		}
		//执行事务
		ts.exec();
		if(resultAck>=0) {//库存充足并扣减成功
			result = true; 
			//TODO 插入异步处理的库存日志流水信息
			//构建库存操作日志对象
			RedisInventoryLogDO rilogDO = new RedisInventoryLogDO();
			rilogDO.setId(SequenceUtil.getSequence(SEQNAME.seq_log, jedis));
			
			//TODO 插入异步库存处理的队列信息
			RedisInventoryQueueDO queueDO = new RedisInventoryQueueDO();
			queueDO.setId(SequenceUtil.getSequence(SEQNAME.seq_queue_send, jedis));
		  }
		  if(ts!=null)
			   ts.discard();
		
			if(jedis!=null) 
				jedisSentinelPool.returnResource(jedis);	
			
		return result;
	}
	
	

}
