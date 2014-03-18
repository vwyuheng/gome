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
import com.tuan.inventory.dao.data.redis.RedisInventoryNumDO;
import com.tuan.inventory.dao.data.redis.RedisSelectionNumDO;
import com.tuan.inventory.dao.data.redis.RedisSuppliersNumDO;
import com.tuan.inventory.domain.repository.NullCacheLoadService;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.model.OrderGoodsSelectionModel;

/**
 * 缓存初始化工具类
 * 
 * @author henry.yu
 * 
 */
public class NullCacheInitService {
	@Resource
	NullCacheLoadService nullCacheLoadService;

	private static Log log = LogFactory.getLog(NullCacheInitService.class);

	/**
	 * 初始化库存信息【库存初始化：新增库存（一）】
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
		// 加载商品库存的主体信息
		RedisInventoryDO riDo = nullCacheLoadService
				.getRedisInventoryInfoByGoodsId(goodsId);
		if (riDo == null) {
			return result;
		} else {
			// 初始化商品库存主体信息到redis
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
						&& orderGoodsSelectionModel.getSelectionRelationId() > 0) { // 更新商品选型库存
					// result = false; //重置result状态
					// 加载商品选型库存信息
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
						// 当商品是无库存限制商品时，修正库存量为integer最大数值量
						if (gsrDO.getLimitStorage() == 0) {
							rsrDo.setLeftNumber(Integer.MAX_VALUE);
						} else {
							rsrDo.setLeftNumber(rsrDo.getLeftNumber());
						}
						rsrDo.setTotalNumber(rsrDo.getTotalNumber());
						rsrDo.setLimitStorage(rsrDo.getLimitStorage());
						// 2.商品id与选型关系
						jedis.sadd(
								InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId),
								String.valueOf(rsrDo.getId()));
						// 从mysql中加载 并set到redis
						String redisAck = jedis.hmset(InventoryEnum.HASHCACHE
								+ ":" + String.valueOf(rsrDo.getId()),
								ObjectUtil.convertBean(rsrDo));
						if (!redisAck.equalsIgnoreCase("ok")) {
							log.error("NullCacheInitService:initRedisInventoryCache invoke error [SelectionRelationId="
									+ orderGoodsSelectionModel
											.getSelectionRelationId() + "]");
							return result;
						}
						// result = true; //重置result状态
					}
				}

				if (orderGoodsSelectionModel.getSuppliersId() > 0) { // 更新商品分店的库存
					// result = false; //重置result状态
					// 加载商品分店库存信息
					GoodsSuppliersInventoryDO gsiDO = nullCacheLoadService
							.getCacheSuppliersInventoryDOById(orderGoodsSelectionModel
									.getSuppliersId());
					if (gsiDO == null) {
						return result;
					} else {

						RedisGoodsSuppliersInventoryDO rgsiDO = new RedisGoodsSuppliersInventoryDO();
						rgsiDO.setId(gsiDO.getId());
						rgsiDO.setGoodsId(gsiDO.getGoodsId());
						// 当商品是无库存限制商品时，修正库存量为integer最大数值量
						if (gsiDO.getLimitStorage() == 0) {
							rgsiDO.setLeftNumber(Integer.MAX_VALUE);
						} else {
							rgsiDO.setLeftNumber(gsiDO.getLeftNumber());
						}
						rgsiDO.setTotalNumber(gsiDO.getTotalNumber());
						rgsiDO.setSuppliersId(gsiDO.getSuppliersId());
						rgsiDO.setLimitStorage(gsiDO.getLimitStorage());
						// 2.商品与商家分店关系
						jedis.sadd(
								InventoryEnum.SETCACHE + ":"
										+ String.valueOf(goodsId),
								String.valueOf(rgsiDO.getId()));
						// 从mysql中加载 并set到redis
						String redisAck = jedis.hmset(InventoryEnum.HASHCACHE
								+ ":" + String.valueOf(rgsiDO.getId()),
								ObjectUtil.convertBean(rgsiDO));
						if (!redisAck.equalsIgnoreCase("ok")) {
							log.error("NullCacheInitService:initRedisInventoryCache invoke error [SuppliersInventoryId="
									+ orderGoodsSelectionModel.getSuppliersId()
									+ "]");
							return result;
						}
						// result = true; //重置result状态
					}
				}
			}// for

		}// if1
		result = true;
		return result;
	}

	public RedisInventoryDO initGoodsInventoryNotSelectionOrSuppliers(
			Jedis jedis, Long goodsId) throws Exception {
		// 加载商品库存的主体信息
		RedisInventoryDO riDo = nullCacheLoadService
				.getRedisInventoryInfoByGoodsId(goodsId);
		if (riDo == null) {
			return riDo;
		} else {
			// 初始化商品库存主体信息到redis
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
	 * 初始化商品选型信息到缓存，并返回选型信息对象 [补录库存到缓存:补录商品选型信息到缓存]
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
			// 当商品是无库存限制商品时，修正库存量为integer最大数值量
			if (result.getLimitStorage() == 0) {
				rsrDo.setLeftNumber(Integer.MAX_VALUE);
			} else {
				rsrDo.setLeftNumber(result.getLeftNumber());
			}
			rsrDo.setTotalNumber(result.getTotalNumber());
			rsrDo.setLimitStorage(result.getLimitStorage());
			// 从mysql中加载 并set到redis
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
	 * 初始化商品选型信息到缓存，并返回选型信息对象 [补录库存到缓存:补录商品选型信息到缓存]
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
			// 当商品是无库存限制商品时，修正库存量为integer最大数值量
			if (gsrDO.getLimitStorage() == 0) {
				result.setLeftNumber(Integer.MAX_VALUE);
			} else {
				result.setLeftNumber(gsrDO.getLeftNumber());
			}
			result.setTotalNumber(result.getTotalNumber());
			result.setLimitStorage(result.getLimitStorage());
			// 从mysql中加载 并set到redis
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
	 * 初始化商品分店信息到缓存，并返回商品分店信息对象 [补录库存到缓存:补录商品分店信息到缓存]
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
			// 当商品是无库存限制商品时，修正库存量为integer最大数值量
			if (result.getLimitStorage() == 0) {
				rgsiDo.setLeftNumber(Integer.MAX_VALUE);
			} else {
				rgsiDo.setLeftNumber(result.getLeftNumber());
			}
			rgsiDo.setTotalNumber(result.getTotalNumber());
			rgsiDo.setSuppliersId(result.getSuppliersId());
			rgsiDo.setLimitStorage(result.getLimitStorage());
			// 从mysql中加载 并set到redis
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
	 * 库存还原:回滚库存
	 * 
	 * @param jedis
	 * @param goodsId
	 * @param numDO
	 * @return
	 * @throws Exception
	 */
	public boolean rollbackInventoryCache(Jedis jedis, Long goodsId,
			RedisInventoryNumDO numDO) throws Exception {
		boolean result = false;
		long resultAck = 0;
		if (numDO.getNum() != 0) {
			// 还原扣减的商品总库存
			resultAck = jedis.hincrBy(
					InventoryEnum.HASHCACHE + ":" + String.valueOf(goodsId),
					HashFieldEnum.leftNumber.toString(), (numDO.getNum()));
			if (resultAck >= 0) { // 说明操作成功
				result = true;
			}
		} else if (!CollectionUtils.isEmpty(numDO.getsLists())) { //商品选型
			for (RedisSelectionNumDO selectDO : numDO.getsLists()) {
				// 还原商品选型,被扣减的库存
				resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(selectDO.getSelectionRelationId()),
						HashFieldEnum.leftNumber.toString(),
						(selectDO.getSelectionNum()));
				if (resultAck >= 0) { // 说明操作成功
					result = true;
				}
			}

		} else if (!CollectionUtils.isEmpty(numDO.getSuppLists())) { //商品分店
			for (RedisSuppliersNumDO suppDO : numDO.getSuppLists()) {
				// 还原商品分店,被扣减的库存
				resultAck = jedis.hincrBy(InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(suppDO.getSuppliersId()),
						HashFieldEnum.leftNumber.toString(),
						(suppDO.getSupplierNum()));
				if (resultAck >= 0) { // 说明操作成功
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * 初始化并返回商品商家库存相关信息
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
			// 从mysql中加载 并set到redis
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

}
