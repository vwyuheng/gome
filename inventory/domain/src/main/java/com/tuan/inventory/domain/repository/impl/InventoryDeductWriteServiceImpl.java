package com.tuan.inventory.domain.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.repository.InventoryDeductWriteService;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.domain.support.util.QueueConstant;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.OrderGoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
/**
 * ���ۼ����д����
 * ������桢���ۼ�[����]�������º�Ļص�ȷ�ϡ�����עˮ���޸�עˮֵ��ɾ�����
 * @author henry.yu
 * @date 2014/03/10
 */
public class InventoryDeductWriteServiceImpl implements InventoryDeductWriteService {
	@Resource 
	JedisSentinelPool jedisSentinelPool;
	@Resource
	NullCacheInitService nullCacheInitService;
	
	private static Log log = LogFactory.getLog(InventoryDeductWriteServiceImpl.class);
	
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
	public String insertSingleInventoryInfoNotExist(long goodsId, String field, String value)
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
	public String updateOverrideSingleInventoryInfo(long goodsId, String field, String value)
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
		//jedis.watch(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
		if(jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
			//��������
			 ts = jedis.multi();
			 if (!CollectionUtils.isEmpty(goodsSelectionList)) {//if2
					for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {//if3
						if (orderGoodsSelectionModel.getSelectionRelationId()!= null
								&& orderGoodsSelectionModel.getSelectionRelationId()> 0) {  //������Ʒѡ�Ϳ��
						    		//����ѡ��idɾ��ѡ�Ϳ����Ϣ
							    	jedis.del(InventoryEnum.HASHCACHE+":"+orderGoodsSelectionModel.getSelectionRelationId());
						    }	
						if(orderGoodsSelectionModel.getSuppliersId()>0){  //������Ʒ�ֵ�Ŀ��
						    		//����ѡ��idɾ��ѡ�ͷֵ�����Ϣ
							    	jedis.del(InventoryEnum.HASHCACHE+":"+orderGoodsSelectionModel.getSuppliersId());
						}
					}//if3
				}//if2
			    //3.���ɾ����Ʒ���������Ϣ����Ʒid��ѡ��id����Ʒ�ֵ�id֮��Ķ�Ӧ��ϵ
			    redisAck = jedis.del(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),InventoryEnum.SETCACHE+":"+String.valueOf(goodsId));
				//ִ������
				ts.exec();
		 }//else {//if1
				//��֤��һ�������ִ�в���Ӱ��
				//jedis.unwatch();
			//}
		 
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
	public Map<String,String> updateInventory(long orderId,long goodsId,  int pindaoId,int num, int limitStorage,
			List<OrderGoodsSelectionModel> goodsSelectionList,Long userId,String system,String clientIp)
			throws Exception {
		log.info("orderId="+orderId+"goodsId="+goodsId+"pindoId="+pindaoId+"num="+num+"limitStorage="+limitStorage);
		String selectType = null; //����ѡ����Ʒ���������
		String suppliersType = null; //�����ֵ���Ʒ���������
		Map<String,String> mapResult = null;
		//�������仯��json����
		JSONObject jsonData = new JSONObject();
		Jedis jedis = jedisSentinelPool.getResource();
		boolean result = false;
		if(jedis== null) {
			mapResult = new HashMap<String,String>();
			mapResult.put("result", String.valueOf(result));
			return mapResult;
		}
		//��������
		// Transaction	ts = jedis.multi();
		 long resultAck = 0;
		 if(goodsId>0&&limitStorage==1){  //limitStorage>0:��������ƣ�1�����ƿ��
			 if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId),"leftNumber")){  //key field �򲻴��� ��ʼ��
				 result =  this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
				 if(!result){
					 mapResult = new HashMap<String,String>();
					 mapResult.put("result", String.valueOf(result));
					 return mapResult;
				 }
				}
			     jsonData.put("��Ʒ�ܿ��仯��", num);
				 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), "leftNumber", (-num));
				 if(resultAck<0) { //˵�����ν��׵Ŀ�治��
					 //��ԭ�ۼ��Ŀ��
					 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), "leftNumber", (num));
					 mapResult = new HashMap<String,String>();
					 mapResult.put("result", String.valueOf(result));
					 return mapResult;
				 }else {
					 jsonData.put("��Ʒ�ܿ��ʣ����", resultAck);
				 }
				 
			 
		 }
		 
		if (!CollectionUtils.isEmpty(goodsSelectionList)) { //if1
			//ѡ����Ʒ���ֵ
			selectType = StringUtil.getIdsString(goodsSelectionList);
			//�ֵ���Ʒ���ֵ
			suppliersType = StringUtil.getIdsString(goodsSelectionList);
			for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) { //for
				if (orderGoodsSelectionModel.getSelectionRelationId()!= null
						&& orderGoodsSelectionModel.getSelectionRelationId()> 0) { //ifѡ�� //������Ʒѡ�Ϳ��
					if(!jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
						 result =  this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
						 if(!result){
							 mapResult = new HashMap<String,String>();
							 mapResult.put("result", String.valueOf(result));
							 return mapResult;
						 }
					}
					//1.���Ȳ���������Ʒ�Ƿ�������͹�ϵ 
				    		if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+orderGoodsSelectionModel.getSelectionRelationId(),"leftNumber")){
				    			result =  this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
				    			if(!result){
									 mapResult = new HashMap<String,String>();
									 mapResult.put("result", String.valueOf(result));
									 return mapResult;
								 }
				    		}
				    		jsonData.put("��Ʒѡ�Ϳ��仯��", (orderGoodsSelectionModel.getCount().intValue()));
				    		//����ѡ��idɾ��ѡ�Ϳ��������Ϣ
				    	    resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+orderGoodsSelectionModel.getSelectionRelationId(),"leftNumber", (-(orderGoodsSelectionModel.getCount().intValue())));
				    	    if(resultAck<0) { //˵�����ν��׵Ŀ�治��
				    	    	//���Ȼ�ԭ��Ʒѡ�ͱ��ۼ��Ŀ��
				    	    	resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+orderGoodsSelectionModel.getSelectionRelationId(),"leftNumber", (orderGoodsSelectionModel.getCount().intValue()));
				    	    	//�ٻ�ԭ��Ʒ������Ϣ�б��ۼ��Ŀ��
				    	    	resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), "leftNumber", (num));
				    	    	mapResult = new HashMap<String,String>();
				    	    	mapResult.put("result", String.valueOf(result));
				    			return mapResult;
							 }else {
								 jsonData.put("��Ʒѡ�Ϳ��ʣ����", (resultAck));
							 }
				    	
				} //ifѡ��	
				if(orderGoodsSelectionModel.getSuppliersId()>0){ //if�ֵ� //������Ʒ�ֵ�Ŀ��
					if(!jedis.exists(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId))){
						result =  this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
						if(!result){
							 mapResult = new HashMap<String,String>();
							 mapResult.put("result", String.valueOf(result));
							 return mapResult;
						 }
					}
					//2.������Ʒid�����Ʒ�����Ƿ�ָ���ֵ�
				    		if(!jedis.hexists(InventoryEnum.HASHCACHE+":"+orderGoodsSelectionModel.getSuppliersId(),"leftNumber")){
				    			result =  this.nullCacheInitService.initRedisInventoryCache(jedis, goodsId, limitStorage, goodsSelectionList);
				    			if(!result){
									 mapResult = new HashMap<String,String>();
									 mapResult.put("result", String.valueOf(result));
									 return mapResult;
								 }
				    		}
				    		jsonData.put("��Ʒ�ֵ���仯��", (orderGoodsSelectionModel.getCount().intValue()));
				    		//����ѡ��idɾ��ѡ�Ϳ��������Ϣ
				    		 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+orderGoodsSelectionModel.getSuppliersId(),"leftNumber", (-(orderGoodsSelectionModel.getCount().intValue()))); 
				    		 if(resultAck<0) { //˵�����ν��׵Ŀ�治��
				    			//���Ȼ�ԭ��Ʒ�ֵ��б��ۼ��Ŀ��
				    			 resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+orderGoodsSelectionModel.getSuppliersId(),"leftNumber", (orderGoodsSelectionModel.getCount().intValue())); 
				    			//�ٻ�ԭ��Ʒ������Ϣ�б��ۼ��Ŀ��
					    	     resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), "leftNumber", (num));
					    	     mapResult = new HashMap<String,String>();
					    	     mapResult.put("result", String.valueOf(result));
					 			 return mapResult;
							 }else {
								 jsonData.put("��Ʒ�ֵ���ʣ����", (resultAck));
							 }
				  
				}//if�ֵ�
			}//for
		} //if1
		//ִ������
		//ts.exec();
		if(resultAck>=0) {//�����㲢�ۼ��ɹ�
			result = true; 
			mapResult = new HashMap<String,String>();
			mapResult.put("result", String.valueOf(result));
			//���������µĶ�����Ϣ ÿ�����г�Ա(member)���Լ���Ψһ������idֵ
			RedisInventoryQueueDO queueDO = this.asemblyQueueDO(SequenceUtil.getSequence(SEQNAME.seq_queue_send, jedis),goodsId, orderId, ResultStatusEnum.LOCKED.getCode(), selectType, suppliersType, jsonData.toString());
			//������������־����
			RedisInventoryLogDO logDO = this.asemblyLogDO(SequenceUtil.getSequence(SEQNAME.seq_log, jedis), goodsId, orderId, selectType, suppliersType, jsonData.toString(), ResultStatusEnum.DEDUCTION.getDescription(), userId, system, clientIp, "", this.asemblyJsonData(queueDO, pindaoId));
			//�������¶�����Ϣѹ�뵽redis set���� ����ͳ�� job�����л�ÿ�ν�ĳ�����Ԫ���ƶ�����һ������set��ȥ�������Ŀ��set�������������Ϣ���µĴ�����������������ü��ϣ����ظ�����
			//���� ����ָ��scoreֵȡֵ ZADD salary 2500 jack
			//ZRANGEBYSCORE salary 2500 2500 WITHSCORES  ��2ȡ����Ӧmember�󣬰���member����ɾ�� [ZREM key member]
			//ɾ��ָ��score��Ԫ�� ZREMRANGEBYSCORE salary 2500 2500
			String jsonMember = JSONObject.fromObject(queueDO).toString();
			mapResult.put(QueueConstant.QUEUE_KEY_ID, String.valueOf(queueDO.getId()));
			//������е�key��member��Ϣ 1СʱʧЧ
			jedis.setex(QueueConstant.QUEUE_KEY_MEMBER+":"+String.valueOf(queueDO.getId()),3600, jsonMember);
			//zset key score value
			jedis.zadd(QueueConstant.QUEUE_SEND_MESSAGE/*+":"+String.valueOf(queueDO.getId())*/, Double.valueOf(queueDO.getStatus()),jsonMember);
			//�������־������Ϣѹ�뵽redis list
			jedis.lpush(QueueConstant.QUEUE_LOGS_MESSAGE/*+":"+String.valueOf(logDO.getId())*/, JSONObject.fromObject(logDO).toString());
					
		  }
		 // if(ts!=null)
			  // ts.discard();
		
			if(jedis!=null) 
				jedisSentinelPool.returnResource(jedis);
		
			return mapResult;
		//return result;
	}
	
	@Override
	public boolean inventoryCallbackAck(String ack,String key) throws Exception {
		Jedis jedis = jedisSentinelPool.getResource();
		boolean result = false;
		if(jedis== null) 
			return result;
		//�򿪶����key�ļ��
		jedis.watch(QueueConstant.QUEUE_KEY_MEMBER+":"+key,QueueConstant.QUEUE_SEND_MESSAGE+":"+key);
		//��������
		Transaction	ts =  null;
		if(StringUtils.isNotBlank(ack)&&ack.equalsIgnoreCase(ResultStatusEnum.ACTIVE.getCode())) {
			//��������
		    ts = jedis.multi();
		    //����keyȡ������Ķ��󣬽�ϵͳ��������ʱ���ã���Ϊ������Ч��Ĭ����60����
			String member = jedis.get(QueueConstant.QUEUE_KEY_MEMBER+":"+key);
			//����Ϣ���Ͷ���״̬����Ϊ:1>��������Ч�ɴ���active��
			Double scoreAck = jedis.zincrby(QueueConstant.QUEUE_SEND_MESSAGE/*+":"+key*/, -(2), member);
			//ִ������
			ts.exec();
			if(scoreAck==1) {
				result = true;
			}
		}else {
			//��֤��һ�������ִ�в���Ӱ��
			jedis.unwatch();
		}
		//��������
		if(ts!=null)
		     ts.discard();
		//�ͷ�����
		if(jedis!=null) 
			jedisSentinelPool.returnResource(jedis);
		
		return result;
	}
	
	@Override
	public boolean inventoryAdjustment(String key, String field, int num)
			throws Exception {
		Jedis jedis = jedisSentinelPool.getResource();
		boolean result = false;
		if(jedis== null) 
			return result;
		//hincrby���ص���field���º��ֵ
		Long resultAck = jedis.hincrBy(key, field, (num));
		if(resultAck>=0) {  //��Ϊ��桢עˮ��ֵ����Ϊ����
			result = true;
		}
		return result;
	}

	@Override
	public boolean waterfloodValAdjustment(String key, int num)
			throws Exception {
		Jedis jedis = jedisSentinelPool.getResource();
		boolean result = false;
		if(jedis== null) 
			return result;
		//hincrby���ص���field���º��ֵ
		Long resultAck = jedis.hincrBy(key, HashFieldEnum.waterfloodVal.toString(), (num));
		if(resultAck>=0) {  //��Ϊעˮֵ����Ϊ����
			result = true;
		}
		return result;
	}
	
	/**
	 * ����װ����־����
	 * @param id
	 * @param goodsId
	 * @param orderId
	 * @param status
	 * @param selectType
	 * @param suppliersType
	 * @param num
	 * @param operateType
	 * @param userId
	 * @param system
	 * @param clientIp
	 * @param remark
	 * @return
	 */
	private  RedisInventoryLogDO  asemblyLogDO(Long id,Long goodsId,Long orderId,
			String selectType,String suppliersType,String num,String operateType,Long userId,String system,String clientIp,String remark,String jsonContent) {
		RedisInventoryLogDO logDO = new RedisInventoryLogDO();
		logDO.setId(id);
		logDO.setGoodsId(goodsId);
		logDO.setOrderId(orderId);
		if(selectType!=null) {
			logDO.setType(QueueConstant.SELECTION);
			logDO.setItem(selectType);
		}else if(suppliersType!=null) {
			logDO.setType(QueueConstant.SUBBRANCH);
			logDO.setItem(suppliersType);
		}else {
			logDO.setType(QueueConstant.GOODS);
		}
		logDO.setVariableQuantity(num);
		logDO.setUserId(userId);
		logDO.setOperateType(operateType);
		logDO.setSystem(system);
		logDO.setClientIp(clientIp);
		logDO.setContent(jsonContent);
		logDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		logDO.setRemark(remark);
    	
		return logDO;
    }
	/**
	 * ����װ����ж���
	 * @param id
	 * @param goodsId
	 * @param orderId
	 * @param status
	 * @param selectType
	 * @param suppliersType
	 * @param num
	 * @return
	 */
	private  RedisInventoryQueueDO  asemblyQueueDO(Long id,Long goodsId,Long orderId,String status,String selectType,String suppliersType,String num) {
    	//����һ�����ӳ�bean����
		RedisInventoryQueueDO queueDO = new RedisInventoryQueueDO();
		queueDO.setId(id);
		queueDO.setGoodsId(goodsId);
		queueDO.setOrderId(orderId);
		//���г�ʼ״̬
		queueDO.setStatus(status);
		if(StringUtils.isNotEmpty(selectType)) {
			 queueDO.setType(QueueConstant.SELECTION);
			 queueDO.setItem(selectType);
		}else if(StringUtils.isNotEmpty(suppliersType)) {
			queueDO.setType(QueueConstant.SUBBRANCH);
			queueDO.setItem(suppliersType);
		}else {
			queueDO.setType(QueueConstant.GOODS);
		}
		queueDO.setVariableQuantityJsonData(num);
		//queueDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		//queueDO.setUpdateTime(TimeUtil.getNowTimestamp10Int());
    	
		return queueDO;
    }
	/**
	 * װ����־��¼���json����
	 * @param queueDO
	 * @param pindaoId
	 * @param resultAck
	 * @return
	 */
	private  String  asemblyJsonData(RedisInventoryQueueDO queueDO,int pindaoId) {
		JSONObject json = JSONObject.fromObject(queueDO);
		json.put("Ƶ��", pindaoId);
		//json.put("���仯",jsonData);
		return json.toString();
    }

	

	
}
