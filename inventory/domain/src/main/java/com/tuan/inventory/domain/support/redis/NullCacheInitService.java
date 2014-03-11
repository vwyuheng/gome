package com.tuan.inventory.domain.support.redis;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.domain.repository.NullCacheLoadService;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.model.OrderGoodsSelectionModel;
import com.tuan.inventory.model.enu.InventoryEnum;
/**
 * �����ʼ��������
 * @author henry.yu
 * 
 */
public class NullCacheInitService {
	@Resource
	NullCacheLoadService nullCacheLoadService;
	
	private static Log log = LogFactory.getLog(NullCacheInitService.class);
	/**
	 * ��ʼ����Ʒ�ֵ���ѡ�͹�ϵ����
	 * @param jedis
	 * @param goodsId
	 * @param rsrDo
	 * @return
	 */
	public boolean initRedisInventoryCache(Jedis jedis ,Long goodsId,int limitStorage,
			List<OrderGoodsSelectionModel> goodsSelectionList) throws Exception{
		boolean result = false;
		//������Ʒ����������Ϣ
		RedisInventoryDO riDo = nullCacheLoadService.getRedisInventoryInfoByGoodsId(goodsId);
		if(riDo==null){
			return result;
		}else {
			// ��mysql�м��� ��set��redis
			String redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(goodsId), ObjectUtil.convertBean(riDo));
			if(!redisAck.equalsIgnoreCase("ok")){
				log.error("NullCacheInitService:initRedisInventoryCache invoke error [goodsId="
						+ goodsId + "]");
				return result;
			}
			//result = true;
		}
		if (!CollectionUtils.isEmpty(goodsSelectionList)) {//if1
			
			for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {//for
				if (orderGoodsSelectionModel.getSelectionRelationId()!= null
						&& orderGoodsSelectionModel.getSelectionRelationId()> 0) {  //������Ʒѡ�Ϳ��
					//result = false; //����result״̬
					//������Ʒѡ�Ϳ����Ϣ
					GoodsSelectionRelationDO gsrDO = nullCacheLoadService.getCacheSelectionRelationDOById(orderGoodsSelectionModel.getSelectionRelationId());
					if(gsrDO==null){
						return result;
					}else {
						RedisGoodsSelectionRelationDO rsrDo = new RedisGoodsSelectionRelationDO();
						rsrDo.setId(rsrDo.getId());
						rsrDo.setGoodsId(goodsId.intValue());
						rsrDo.setGoodTypeId(rsrDo.getGoodTypeId());
						rsrDo.setLeftNumber(rsrDo.getLeftNumber());
						rsrDo.setTotalNumber(rsrDo.getTotalNumber());
						rsrDo.setLimitStorage(rsrDo.getLimitStorage());
						//2.��Ʒid��ѡ�͹�ϵ
						jedis.sadd(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId),String.valueOf(rsrDo.getId()));
						// ��mysql�м��� ��set��redis
						String redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(rsrDo.getId()), ObjectUtil.convertBean(rsrDo));
						if(!redisAck.equalsIgnoreCase("ok")){
							log.error("NullCacheInitService:initRedisInventoryCache invoke error [SelectionRelationId="
									+ orderGoodsSelectionModel.getSelectionRelationId() + "]");
							return result;
						}
						//result = true; //����result״̬
					}
				}
				
				if(orderGoodsSelectionModel.getSuppliersId()>0){  //������Ʒ�ֵ�Ŀ��
					//result = false; //����result״̬
					//������Ʒ�ֵ�����Ϣ
					GoodsSuppliersInventoryDO gsiDO = nullCacheLoadService.getCacheSuppliersInventoryDOById(orderGoodsSelectionModel.getSuppliersId());
					if(gsiDO==null){
						return result;
					}else {
						
						RedisGoodsSuppliersInventoryDO rgsiDO = new RedisGoodsSuppliersInventoryDO();
						rgsiDO.setId(gsiDO.getId());
						rgsiDO.setGoodsId(gsiDO.getGoodsId());
						rgsiDO.setLeftNumber(gsiDO.getLeftNumber());
						rgsiDO.setTotalNumber(gsiDO.getTotalNumber());
						rgsiDO.setSuppliersId(gsiDO.getSuppliersId());
						rgsiDO.setLimitStorage(gsiDO.getLimitStorage());
						//2.��Ʒ���̼ҷֵ��ϵ
						jedis.sadd(InventoryEnum.SETCACHE+":"+String.valueOf(goodsId),String.valueOf(rgsiDO.getId()));
						// ��mysql�м��� ��set��redis
						String redisAck = jedis.hmset(InventoryEnum.HASHCACHE+":"+String.valueOf(rgsiDO.getId()), ObjectUtil.convertBean(rgsiDO));
						if(!redisAck.equalsIgnoreCase("ok")){
							log.error("NullCacheInitService:initRedisInventoryCache invoke error [SuppliersInventoryId="
									+ orderGoodsSelectionModel.getSuppliersId() + "]");
							return result;
						}
						//result = true; //����result״̬
					}
				}
			}//for
			
		}//if1
		result = true;
		return result;
	}
	
	/**
	 * ��ʼ����Ʒ�ֵ���ѡ�͹�ϵ����
	 * @param jedis
	 * @param SelectionRelationId
	 * @param rsrDo
	 * @return
	 */
	public GoodsSelectionRelationDO initSelectionRelation(Jedis jedis ,int SelectionRelationId) throws Exception{
		
		GoodsSelectionRelationDO result = nullCacheLoadService.getCacheSelectionRelationDOById(SelectionRelationId);
		if(result==null){
			return result;
		}else {
			RedisGoodsSelectionRelationDO rsrDo = new RedisGoodsSelectionRelationDO();
			rsrDo.setId(result.getId());
			rsrDo.setGoodTypeId(result.getGoodTypeId());
			rsrDo.setLeftNumber(result.getLeftNumber());
			rsrDo.setTotalNumber(result.getTotalNumber());
			rsrDo.setLimitStorage(result.getLimitStorage());
			// ��mysql�м��� ��set��redis
			String redisAck = jedis.hmset(String.valueOf(SelectionRelationId), ObjectUtil.convertBean(rsrDo));
			if(!redisAck.equalsIgnoreCase("ok")){
				log.error("NullCacheInitService:initSelectionRelation invoke error [SelectionRelationId="
						+ SelectionRelationId + "]");
			}
		}
	
		return result;
	}
	
	/**
	 * ��ʼ����������Ʒ�̼ҿ�������Ϣ
	 * @param jedis
	 * @param SuppliersInventoryId
	 * @return
	 * @throws Exception
	 */
	public int initSuppliersInventoryCache(Jedis jedis ,int SuppliersInventoryId) throws Exception{
		int result = 0;
		RedisGoodsSuppliersInventoryDO rgsiDO = nullCacheLoadService.getCacheSuppliersInventoryInfoById(SuppliersInventoryId);
		if(rgsiDO==null){
			return result;
		}else {
			// ��mysql�м��� ��set��redis
			String redisAck = jedis.hmset(String.valueOf(SuppliersInventoryId), ObjectUtil.convertBean(rgsiDO));
			if(!redisAck.equalsIgnoreCase("ok")){
				log.error("NullCacheInitService:initSuppliersInventoryCache invoke error [SuppliersInventoryId="
						+ SuppliersInventoryId + "]");
			}
			if (rgsiDO.getLimitStorage() == 0) {
				result =  Integer.MAX_VALUE;
			} else {
				result =  rgsiDO.getLeftNumber();
			}
			
		}
	
		return result;
	}
	
	
public GoodsSuppliersInventoryDO initSuppliersInventory(Jedis jedis ,int SuppliersInventoryId) throws Exception{
		
	   GoodsSuppliersInventoryDO result = nullCacheLoadService.getCacheSuppliersInventoryDOById(SuppliersInventoryId);
		if(result==null){
			return result;
		}else {
			RedisGoodsSuppliersInventoryDO rgsiDo = new RedisGoodsSuppliersInventoryDO();
			rgsiDo.setId(result.getId());
			rgsiDo.setGoodsId(result.getGoodsId());
			rgsiDo.setLeftNumber(result.getLeftNumber());
			rgsiDo.setTotalNumber(result.getTotalNumber());
			rgsiDo.setSuppliersId(result.getSuppliersId());
			rgsiDo.setLimitStorage(result.getLimitStorage());
			// ��mysql�м��� ��set��redis
			String redisAck = jedis.hmset(String.valueOf(SuppliersInventoryId), ObjectUtil.convertBean(rgsiDo));
			if(!redisAck.equalsIgnoreCase("ok")){
				log.error("NullCacheInitService:initSuppliersInventory invoke error [SuppliersInventoryId="
						+ SuppliersInventoryId + "]");
			}
		}
	
		return result;
	}
}
