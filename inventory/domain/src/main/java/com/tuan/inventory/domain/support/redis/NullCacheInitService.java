package com.tuan.inventory.domain.support.redis;

import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryNumDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.RedisSelectionNumDO;
import com.tuan.inventory.dao.data.redis.RedisSuppliersNumDO;
import com.tuan.inventory.domain.repository.NullCacheLoadService;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.domain.support.util.QueueConstant;
import com.tuan.inventory.model.OrderGoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;

/**
 * �����ʼ��������
 * 
 * @author henry.yu
 * 
 */
public class NullCacheInitService {
	@Resource
	NullCacheLoadService nullCacheLoadService;

	private static Log log = LogFactory.getLog(NullCacheInitService.class);

	/**
	 * ��ʼ�������Ϣ������ʼ����������棨һ����
	 * 
	 * @param jedis
	 * @param goodsId
	 * @param rsrDo
	 * @return
	 */
	public boolean initRedisInventoryCache(Jedis jedis, Long goodsId,
			int limitStorage, List<OrderGoodsSelectionModel> goodsSelectionList)
			throws Exception {
		boolean result = false;
		// ������Ʒ����������Ϣ
		RedisInventoryDO riDo = nullCacheLoadService
				.getRedisInventoryInfoByGoodsId(goodsId);
		if (riDo == null) {
			return result;
		} else {
			// ��ʼ����Ʒ���������Ϣ��redis
			String redisAck = jedis.hmset(InventoryEnum.HASHCACHE + ":"
					+ String.valueOf(goodsId), ObjectUtil.convertBean(riDo));
			if (!redisAck.equalsIgnoreCase("ok")) {
				log.error("NullCacheInitService:initRedisInventoryCache invoke error [goodsId="
						+ goodsId + "]");
				return result;
			}
			// result = true;
		}
		if (!CollectionUtils.isEmpty(goodsSelectionList)) {// if1

			for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {// for
				if (orderGoodsSelectionModel.getSelectionRelationId() != null
						&& orderGoodsSelectionModel.getSelectionRelationId() > 0) { // ������Ʒѡ�Ϳ��
					// result = false; //����result״̬
					// ������Ʒѡ�Ϳ����Ϣ
					GoodsSelectionRelationDO gsrDO = nullCacheLoadService
							.getCacheSelectionRelationDOById(orderGoodsSelectionModel
									.getSelectionRelationId());
					if (gsrDO == null) {
						return result;
					} else {
						RedisGoodsSelectionRelationDO rsrDo = new RedisGoodsSelectionRelationDO();
						rsrDo.setId(rsrDo.getId());
						rsrDo.setGoodsId(goodsId.intValue());
						rsrDo.setGoodTypeId(rsrDo.getGoodTypeId());
						// ����Ʒ���޿��������Ʒʱ�����������Ϊinteger�����ֵ��
						if (gsrDO.getLimitStorage() == 0) {
							rsrDo.setLeftNumber(Integer.MAX_VALUE);
						} else {
							rsrDo.setLeftNumber(rsrDo.getLeftNumber());
						}
						rsrDo.setTotalNumber(rsrDo.getTotalNumber());
						rsrDo.setLimitStorage(rsrDo.getLimitStorage());
						// 2.��Ʒid��ѡ�͹�ϵ
						jedis.sadd(
								InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId),
								String.valueOf(rsrDo.getId()));
						// ��mysql�м��� ��set��redis
						String redisAck = jedis.hmset(InventoryEnum.HASHCACHE
								+ ":" + String.valueOf(rsrDo.getId()),
								ObjectUtil.convertBean(rsrDo));
						if (!redisAck.equalsIgnoreCase("ok")) {
							log.error("NullCacheInitService:initRedisInventoryCache invoke error [SelectionRelationId="
									+ orderGoodsSelectionModel
											.getSelectionRelationId() + "]");
							return result;
						}
						// result = true; //����result״̬
					}
				}

				if (orderGoodsSelectionModel.getSuppliersId() > 0) { // ������Ʒ�ֵ�Ŀ��
					// result = false; //����result״̬
					// ������Ʒ�ֵ�����Ϣ
					GoodsSuppliersInventoryDO gsiDO = nullCacheLoadService
							.getCacheSuppliersInventoryDOById(orderGoodsSelectionModel
									.getSuppliersId());
					if (gsiDO == null) {
						return result;
					} else {

						RedisGoodsSuppliersInventoryDO rgsiDO = new RedisGoodsSuppliersInventoryDO();
						rgsiDO.setId(gsiDO.getId());
						rgsiDO.setGoodsId(gsiDO.getGoodsId());
						// ����Ʒ���޿��������Ʒʱ�����������Ϊinteger�����ֵ��
						if (gsiDO.getLimitStorage() == 0) {
							rgsiDO.setLeftNumber(Integer.MAX_VALUE);
						} else {
							rgsiDO.setLeftNumber(gsiDO.getLeftNumber());
						}
						rgsiDO.setTotalNumber(gsiDO.getTotalNumber());
						rgsiDO.setSuppliersId(gsiDO.getSuppliersId());
						rgsiDO.setLimitStorage(gsiDO.getLimitStorage());
						// 2.��Ʒ���̼ҷֵ��ϵ
						jedis.sadd(
								InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId),
								String.valueOf(rgsiDO.getId()));
						// ��mysql�м��� ��set��redis
						String redisAck = jedis.hmset(InventoryEnum.HASHCACHE
								+ ":" + String.valueOf(rgsiDO.getId()),
								ObjectUtil.convertBean(rgsiDO));
						if (!redisAck.equalsIgnoreCase("ok")) {
							log.error("NullCacheInitService:initRedisInventoryCache invoke error [SuppliersInventoryId="
									+ orderGoodsSelectionModel.getSuppliersId()
									+ "]");
							return result;
						}
						// result = true; //����result״̬
					}
				}
			}// for

		}// if1
		result = true;
		return result;
	}

	public RedisInventoryDO initGoodsInventoryNotSelectionOrSuppliers(
			Jedis jedis, Long goodsId) throws Exception {
		// ������Ʒ����������Ϣ
		RedisInventoryDO riDo = nullCacheLoadService
				.getRedisInventoryInfoByGoodsId(goodsId);
		if (riDo == null) {
			return riDo;
		} else {
			// ��ʼ����Ʒ���������Ϣ��redis
			String redisAck = jedis.hmset(InventoryEnum.HASHCACHE + ":"
					+ String.valueOf(goodsId), ObjectUtil.convertBean(riDo));
			if (!redisAck.equalsIgnoreCase("ok")) {
				log.error("NullCacheInitService:initGoodsInventoryNotSelectionOrSuppliers invoke error [goodsId="
						+ goodsId + "]");
				return null;
			}
		}

		return riDo;
	}

	/**
	 * ��ʼ����Ʒѡ����Ϣ�����棬������ѡ����Ϣ���� [��¼��浽����:��¼��Ʒѡ����Ϣ������]
	 * 
	 * @param jedis
	 * @param SelectionRelationId
	 * @param rsrDo
	 * @return
	 */
	public GoodsSelectionRelationDO initSelectionRelation(Jedis jedis,
			int SelectionRelationId) throws Exception {

		GoodsSelectionRelationDO result = nullCacheLoadService
				.getCacheSelectionRelationDOById(SelectionRelationId);
		if (result == null) {
			return result;
		} else {
			RedisGoodsSelectionRelationDO rsrDo = new RedisGoodsSelectionRelationDO();
			rsrDo.setId(result.getId());
			rsrDo.setGoodTypeId(result.getGoodTypeId());
			// ����Ʒ���޿��������Ʒʱ�����������Ϊinteger�����ֵ��
			if (result.getLimitStorage() == 0) {
				rsrDo.setLeftNumber(Integer.MAX_VALUE);
			} else {
				rsrDo.setLeftNumber(result.getLeftNumber());
			}
			rsrDo.setTotalNumber(result.getTotalNumber());
			rsrDo.setLimitStorage(result.getLimitStorage());
			// ��mysql�м��� ��set��redis
			String redisAck = jedis.hmset(InventoryEnum.HASHCACHE + ":"
					+ String.valueOf(SelectionRelationId),
					ObjectUtil.convertBean(rsrDo));
			if (!redisAck.equalsIgnoreCase("ok")) {
				log.error("NullCacheInitService:initSelectionRelation invoke error [SelectionRelationId="
						+ SelectionRelationId + "]");
			}
		}

		return result;
	}

	/**
	 * ��ʼ����Ʒѡ����Ϣ�����棬������ѡ����Ϣ���� [��¼��浽����:��¼��Ʒѡ����Ϣ������]
	 * 
	 * @param jedis
	 * @param sIds
	 * 
	 * @return RedisGoodsSelectionRelationDO
	 */
	public RedisGoodsSelectionRelationDO initSRelation(Jedis jedis, Long sIds)
			throws Exception {
		RedisGoodsSelectionRelationDO result = null;
		GoodsSelectionRelationDO gsrDO = nullCacheLoadService
				.getCacheSelectionRelationDOById(sIds.intValue());
		if (gsrDO == null) {
			return result;
		} else {
			result = new RedisGoodsSelectionRelationDO();
			result.setId(result.getId());
			result.setGoodTypeId(result.getGoodTypeId());
			// ����Ʒ���޿��������Ʒʱ�����������Ϊinteger�����ֵ��
			if (gsrDO.getLimitStorage() == 0) {
				result.setLeftNumber(Integer.MAX_VALUE);
			} else {
				result.setLeftNumber(gsrDO.getLeftNumber());
			}
			result.setTotalNumber(result.getTotalNumber());
			result.setLimitStorage(result.getLimitStorage());
			// ��mysql�м��� ��set��redis
			String redisAck = jedis.hmset(InventoryEnum.HASHCACHE + ":"
					+ String.valueOf(sIds), ObjectUtil.convertBean(result));
			if (!redisAck.equalsIgnoreCase("ok")) {
				log.error("NullCacheInitService:initSRelation invoke error [sIds="
						+ sIds + "]");
			}
		}

		return result;
	}

	/**
	 * ��ʼ����Ʒ�ֵ���Ϣ�����棬��������Ʒ�ֵ���Ϣ���� [��¼��浽����:��¼��Ʒ�ֵ���Ϣ������]
	 * 
	 * @param jedis
	 * @param SuppliersInventoryId
	 * @return
	 * @throws Exception
	 */
	public GoodsSuppliersInventoryDO initSuppliersInventory(Jedis jedis,
			int SuppliersInventoryId) throws Exception {

		GoodsSuppliersInventoryDO result = nullCacheLoadService
				.getCacheSuppliersInventoryDOById(SuppliersInventoryId);
		if (result == null) {
			return result;
		} else {
			RedisGoodsSuppliersInventoryDO rgsiDo = new RedisGoodsSuppliersInventoryDO();
			rgsiDo.setId(result.getId());
			rgsiDo.setGoodsId(result.getGoodsId());
			// ����Ʒ���޿��������Ʒʱ�����������Ϊinteger�����ֵ��
			if (result.getLimitStorage() == 0) {
				rgsiDo.setLeftNumber(Integer.MAX_VALUE);
			} else {
				rgsiDo.setLeftNumber(result.getLeftNumber());
			}
			rgsiDo.setTotalNumber(result.getTotalNumber());
			rgsiDo.setSuppliersId(result.getSuppliersId());
			rgsiDo.setLimitStorage(result.getLimitStorage());
			// ��mysql�м��� ��set��redis
			String redisAck = jedis.hmset(InventoryEnum.HASHCACHE + ":"
					+ String.valueOf(SuppliersInventoryId),
					ObjectUtil.convertBean(rgsiDo));
			if (!redisAck.equalsIgnoreCase("ok")) {
				log.error("NullCacheInitService:initSuppliersInventory invoke error [SuppliersInventoryId="
						+ SuppliersInventoryId + "]");
			}
		}

		return result;
	}

	/***
	 * ��滹ԭ:�ع����
	 * 
	 * @param jedis
	 * @param goodsId
	 * @param numDO
	 * @return
	 * @throws Exception
	 */
	public boolean rollbackInventoryCache(Jedis jedis, Long goodsId,int limitStorage,
			RedisInventoryNumDO numDO) throws Exception {
		boolean result = false;
		long resultAck = 0;
		if (goodsId>0&&limitStorage==1) { // limitStorage>0:��������ƣ�1�����ƿ��
			// ��ԭ�ۼ�����Ʒ�ܿ��
			resultAck = jedis.hincrBy(
					InventoryEnum.HASHCACHE + ":" + String.valueOf(goodsId),
					HashFieldEnum.leftNumber.toString(), (numDO.getNum()));
			if (resultAck >= 0) { // ˵�������ɹ�
				result = true;
			}
		} else if (!CollectionUtils.isEmpty(numDO.getsLists())) { //��Ʒѡ��
			for (RedisSelectionNumDO selectDO : numDO.getsLists()) {
				if(selectDO.getSelectionRelationId()!=null&&selectDO.getSelectionRelationId()>0){
					// ��ԭ��Ʒѡ��,���ۼ��Ŀ��
					resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(selectDO.getSelectionRelationId()),
							HashFieldEnum.leftNumber.toString(),
							(selectDO.getSelectionNum()));
				}
				
				if (resultAck >= 0) { // ˵�������ɹ�
					result = true;
				}
			}

		} else if (!CollectionUtils.isEmpty(numDO.getSuppLists())) { //��Ʒ�ֵ�
			for (RedisSuppliersNumDO suppDO : numDO.getSuppLists()) {
				if(suppDO.getSuppliersId()>0) {
					// ��ԭ��Ʒ�ֵ�,���ۼ��Ŀ��
					resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(suppDO.getSuppliersId()),
							HashFieldEnum.leftNumber.toString(),
							(suppDO.getSupplierNum()));
				}
				if (resultAck >= 0) { // ˵�������ɹ�
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * ��ʼ����������Ʒ�̼ҿ�������Ϣ
	 * 
	 * @param jedis
	 * @param SuppliersInventoryId
	 * @return
	 * @throws Exception
	 */
	public int initSuppliersInventoryCache(Jedis jedis, int SuppliersInventoryId)
			throws Exception {
		int result = 0;
		RedisGoodsSuppliersInventoryDO rgsiDO = nullCacheLoadService
				.getCacheSuppliersInventoryInfoById(SuppliersInventoryId);
		if (rgsiDO == null) {
			return result;
		} else {
			// ��mysql�м��� ��set��redis
			String redisAck = jedis.hmset(String.valueOf(SuppliersInventoryId),
					ObjectUtil.convertBean(rgsiDO));
			if (!redisAck.equalsIgnoreCase("ok")) {
				log.error("NullCacheInitService:initSuppliersInventoryCache invoke error [SuppliersInventoryId="
						+ SuppliersInventoryId + "]");
			}
			if (rgsiDO.getLimitStorage() == 0) {
				result = Integer.MAX_VALUE;
			} else {
				result = rgsiDO.getLeftNumber();
			}

		}

		return result;
	}
	/**
	 * �������־������Ϣѹ�뵽redis list
	 * @param jedis
	 * @param logDO
	 */
	public void pushLogQueues(Jedis jedis,RedisInventoryLogDO logDO) {
		// �������־������Ϣѹ�뵽redis list
		jedis.lpush(QueueConstant.QUEUE_LOGS_MESSAGE, JSONObject
				.fromObject(logDO).toString());
	}
	/**
	 * �������¶�����Ϣѹ�뵽redis zset���� ����ͳ��
	 * @param jedis
	 * @param queueDO
	 */
	public void pushQueueSendMsg(Jedis jedis,RedisInventoryQueueDO queueDO) {
		// �������¶�����Ϣѹ�뵽redis zset���� ����ͳ��
		// job�����л�ÿ�ν�scoreΪ1��Ԫ��ȡ�����������Ϣ���µĴ������������key score member(��id����Ψһ�ģ����ÿ��member���ǲ�һ����)�������Դ�����е����Ԫ�أ����ظ�����
		// ���� ����ָ��scoreֵȡֵ ZADD salary 2500 jack
		// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
		// ��2ȡ����Ӧmember�󣬰���member����ɾ�� [ZREM key member]
		// ɾ��ָ��score��Ԫ�� ZREMRANGEBYSCORE salary 2500 2500
		String jsonMember = JSONObject.fromObject(queueDO)
				.toString();
		// ������е�key��member��Ϣ 1СʱʧЧ
		jedis.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
				+ String.valueOf(queueDO.getId()), 3600,
				jsonMember);
		// zset key score value ����score��Ϊstatus��
		jedis.zadd(QueueConstant.QUEUE_SEND_MESSAGE, Double
				.valueOf(ResultStatusEnum.LOCKED.getCode()),
				jsonMember);
		
	}
}
