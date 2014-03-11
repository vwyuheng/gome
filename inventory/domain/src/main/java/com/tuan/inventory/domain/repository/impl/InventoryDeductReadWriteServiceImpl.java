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
 * ������
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
			//��������
		 Transaction	ts = jedis.multi();
		 
		 if(jedis.exists(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId))){  //�Ѵ��ڷ���false
			 return result;
		 }
			//1.������Ʒ���������Ϣ
			String redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), ObjectUtil.convertBean(riDO));
			//��Ʒ�Ƿ�������� 0������ӣ�1�����
			if(riDO.getIsAddGoodsSelection()==1) {  
				if(!CollectionUtils.isEmpty(rgsrList)) {
					//jedis.sadd(String.valueOf(goodsId), StringUtil.getSelectionRelationString(rgsrList));
					for(RedisGoodsSelectionRelationDO rgsrDO :rgsrList) {
						//2.��Ʒid��ѡ�͹�ϵ
						jedis.sadd(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId),String.valueOf(rgsrDO.getId()));
						//3.����ѡ�Ϳ��������Ϣ
						redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(rgsrDO.getId()), ObjectUtil.convertBean(rgsrDO));
					}
					
				}
			}
			//��Ʒ�����Ƿ���Ҫָ���ֵ� 0����ָ����1��ָ��
			if(riDO.getIsDirectConsumption()==1) { 
				if(!CollectionUtils.isEmpty(rgsiList)) {
					//jedis.sadd(String.valueOf(goodsId), StringUtil.getSuppliersInventoryString(rgsiList));
					for(RedisGoodsSuppliersInventoryDO rgsiDO:rgsiList){
						//2.��Ʒ���̼ҷֵ��ϵ
						jedis.sadd(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId),String.valueOf(rgsiDO.getId()));
						//3.������Ʒ�ֵ���������Ϣ
						redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(rgsiDO.getId()), ObjectUtil.convertBean(rgsiDO));
					}
				}
			}
			//ִ������
			//List<Object> redisAckList = 
					ts.exec();
			if(!redisAck.equalsIgnoreCase("ok")){
				log.error("InventoryRelationMainTainService:createInventory invoke error [goodsId="
						+ goodsId + "]");
			}else {
				//TODO ���Ϳ��������Ϣ
				
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
		//��������1   :hsetnx ���� field �Ѿ����ڣ��ò�����Ч��
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
		//���²���
		jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId));
		if(jedis.hexists(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), field)){
			//��������
			 Transaction	ts = jedis.multi();
			 Long redisAck = jedis.hset(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), field,value);
			//ִ������
			ts.exec();
			if(redisAck==1)
				result = ResultStatusEnum.UPDATE.getCode();
		}else {
			//��֤��һ�������ִ�в���Ӱ��
			jedis.unwatch();
			//TODO ��ʼ��
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
		//����ɾ������
		jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
		if(jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
			//��������
			 ts = jedis.multi();
			 if (!CollectionUtils.isEmpty(goodsSelectionList)) {//if2
					for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {//if3
						if (orderGoodsSelectionModel.getSelectionRelationId()!= null
								&& orderGoodsSelectionModel.getSelectionRelationId()> 0) {  //������Ʒѡ�Ϳ��
							//1.���ȸ�����Ʒid������Ʒ�Ƿ�������͹�ϵ 
						    Set<String> setSelectionIds = jedis.smembers(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
						    if(!CollectionUtils.isEmpty(setSelectionIds)){
						    	for(String id:setSelectionIds){
						    		//����ѡ��idɾ��ѡ�Ϳ��������Ϣ
							    	jedis.del(InventoryEnum.HASHCACHE+":"+id);
						    	}
						    	
						    }
						}
						
						if(orderGoodsSelectionModel.getSuppliersId()>0){  //������Ʒ�ֵ�Ŀ��
							 //2.������Ʒid�����Ʒ�����Ƿ�ָ���ֵ�
						    Set<String> setSuppliersIds = jedis.smembers(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
						    if(!CollectionUtils.isEmpty(setSuppliersIds)){
						    	for(String id:setSuppliersIds){
						    		//����ѡ��idɾ��ѡ�Ϳ��������Ϣ
							    	jedis.del(InventoryEnum.HASHCACHE+":"+id);
						    	}
						    	
						    }
						}
					}//if3
				}//if2
			    //3.���ɾ����Ʒ���������Ϣ��ѡ�Ϳ��������Ϣ����Ʒ�ֵ�����Ϣ
			    redisAck = jedis.del(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
				//ִ������
				ts.exec();
		 }else {//if1
				//��֤��һ�������ִ�в���Ӱ��
				jedis.unwatch();
			}
		 
		 if(redisAck>0){
               //TODO ���Ϳ��������Ϣ
				
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
		//�������²���
		jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));	
		//��������
		 Transaction	ts = jedis.multi();
		 long resultAck = 0;
		 if(goodsId>0&&limitStorage==1){
			 if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),"leftNumber")){  //key field �򲻴��� ��ʼ��
				      this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
				}
				 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), "leftNumber", (-num));
				 if(resultAck<0) { //˵�����ν��׵Ŀ�治��
					 //��ԭ�ۼ��Ŀ��
					 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), "leftNumber", (num));
					 return result;
				 }
			 
		 }
		 
		if (!CollectionUtils.isEmpty(goodsSelectionList)) {
			for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {
				if (orderGoodsSelectionModel.getSelectionRelationId()!= null
						&& orderGoodsSelectionModel.getSelectionRelationId()> 0) {  //������Ʒѡ�Ϳ��
					if(!jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
						 this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
					}
					//1.���ȸ�����Ʒid������Ʒ�Ƿ�������͹�ϵ 
				    Set<String> setSelectionIds = jedis.smembers(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
				    if(!CollectionUtils.isEmpty(setSelectionIds)){
				    	for(String id:setSelectionIds){
				    		if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+id,"leftNumber")){
				    			this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
								}
				    		//����ѡ��idɾ��ѡ�Ϳ��������Ϣ
				    	    resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+id,"leftNumber", (-num));
				    	    if(resultAck<0) { //˵�����ν��׵Ŀ�治��
				    	    	//��ԭ�ۼ��Ŀ��
				    	    	resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+id,"leftNumber", (num));
								 return result;
							 }
				    	}
				    	
				    }
				}
				
				if(orderGoodsSelectionModel.getSuppliersId()>0){  //������Ʒ�ֵ�Ŀ��
					if(!jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
						this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
					}
					//2.������Ʒid�����Ʒ�����Ƿ�ָ���ֵ�
				    Set<String> setSuppliersIds = jedis.smembers(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
				    if(!CollectionUtils.isEmpty(setSuppliersIds)){
				    	for(String id:setSuppliersIds){
				    		if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+id,"leftNumber")){
				    			this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
								
								}
				    		//����ѡ��idɾ��ѡ�Ϳ��������Ϣ
				    		 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+id,"leftNumber", (-num)); 
				    		 if(resultAck<0) { //˵�����ν��׵Ŀ�治��
				    			//��ԭ�ۼ��Ŀ��
				    			 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+id,"leftNumber", (num)); 
								 return result;
							 }
				    	}
				    	
				    }
				}
			}
		}
		//ִ������
		ts.exec();
		if(resultAck>=0) {//�����㲢�ۼ��ɹ�
			result = true; 
			//TODO �����첽����Ŀ����־��ˮ��Ϣ
			//������������־����
			RedisInventoryLogDO rilogDO = new RedisInventoryLogDO();
			rilogDO.setId(SequenceUtil.getSequence(SEQNAME.seq_log, jedis));
			
			//TODO �����첽��洦��Ķ�����Ϣ
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
