package com.tuan.inventory.domain.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.support.enu.InventoryEnum;
import com.tuan.inventory.domain.support.exception.RedisRunException;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory.JWork;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.domain.support.util.ObjectUtil;
import com.tuan.inventory.model.GoodsSelectionRelationModel;

/**
 * ���ڿ����صĶ�ȡ����ӿ�
 * 
 * @author henry.yu
 * @date 2014/3/13
 */
public class InventoryProviderReadServiceImpl implements
		InventoryProviderReadService {
	private final static LocalLogger log = LocalLogger.getLog("InventoryProviderReadService.LOG");
	@Resource
	ReadJedisFactory readJedisFactory;
	@Resource
	NullCacheInitService nullCacheInitService;
	@Override
	public GoodsSelectionRelationDO getSelectionRelationBySrId(
			final int SelectionRelationId) throws Exception {
		
		return readJedisFactory.withJedisDo(new JWork<GoodsSelectionRelationDO>() {
			@Override
			public GoodsSelectionRelationDO work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getSelectionRelationBySrId(SelectionRelationId)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", SelectionRelationId)
						.addMetaData("startTime", startTime).toJson());
			    GoodsSelectionRelationDO result = null;
				// TODO ������
				// j.del(String.valueOf(InventoryEnum.HASHCACHE+ ":"+
				// SelectionRelationId));
				if (j == null)
					return result;
				try {
					Map<String, String> objMap = j
							.hgetAll(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(SelectionRelationId));
					if (!CollectionUtils.isEmpty(objMap)) { // if1
						result = (GoodsSelectionRelationDO) ObjectUtil
								.convertMap(GoodsSelectionRelationDO.class,
										objMap);
					} else {
						result = nullCacheInitService.initSelectionRelation(j,
								SelectionRelationId);

					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(SelectionRelationId))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getSelectionRelationBySrId run exception!",e);
				}
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(SelectionRelationId))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
		});

	}
	@Override
	public GoodsSuppliersInventoryDO getSuppliersInventoryBySiId(
			final int SuppliersInventoryId) throws Exception {
		return readJedisFactory.withJedisDo(new JWork<GoodsSuppliersInventoryDO>() {
			@Override
			public GoodsSuppliersInventoryDO work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getSuppliersInventoryBySiId(SuppliersInventoryId)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(SuppliersInventoryId))
						.addMetaData("startTime", startTime).toJson());
				GoodsSuppliersInventoryDO result = null;
				if (j == null)
					return result;
				try {
					// TODO ������
					// j.del(String.valueOf(InventoryEnum.HASHCACHE+ ":"+
					// SuppliersInventoryId));
					Map<String, String> objMap = j
							.hgetAll(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(SuppliersInventoryId));
					if (!CollectionUtils.isEmpty(objMap)) {
						result = (GoodsSuppliersInventoryDO) ObjectUtil
								.convertMap(GoodsSuppliersInventoryDO.class,
										objMap);

					} else {
						result = nullCacheInitService.initSuppliersInventory(j,
								SuppliersInventoryId);
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(SuppliersInventoryId))
							.addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getSuppliersInventoryBySiId run exception!",e);
				}
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(SuppliersInventoryId))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
			
		});

	}

	@Override
	public List<GoodsSelectionRelationModel> getSelectionRelationBySrIds(
			final List<Long> selectionRelationIdList, final long goodsId)
			throws Exception {
		return readJedisFactory
				.withJedisDo(new JWork<List<GoodsSelectionRelationModel>>() {
					@Override
					public List<GoodsSelectionRelationModel> work(Jedis j)
							throws Exception {
						LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getSelectionRelationBySrIds(selectionRelationIdList,goodsId)");
						long startTime = System.currentTimeMillis();
						log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
								.addMetaData("selectionRelationIdList", selectionRelationIdList)
								.addMetaData("startTime", startTime).toJson());
						List<GoodsSelectionRelationModel> result = null;
						if (j == null)
							return result;
						try {
							if (!CollectionUtils
									.isEmpty(selectionRelationIdList)) { // if1
								result = new ArrayList<GoodsSelectionRelationModel>();
								for (Long lIds : selectionRelationIdList) {
									RedisGoodsSelectionRelationDO resultTmp = null;
									Map<String, String> objMap = j
											.hgetAll(InventoryEnum.HASHCACHE
													+ ":"
													+ String.valueOf(lIds));
									if (!CollectionUtils.isEmpty(objMap)) {
										resultTmp = (RedisGoodsSelectionRelationDO) ObjectUtil
												.convertMap(
														RedisGoodsSelectionRelationDO.class,
														objMap);
										if (resultTmp != null) {
											result.add(asembly(resultTmp));
										}

									} else {
										resultTmp = nullCacheInitService
												.initSRelation(j, lIds);
										if (resultTmp != null) {
											result.add(asembly(resultTmp));
										}
									}
								}
							}// if1
						} catch (Exception e) {
							log.error(lm.addMetaData("goodsId", String.valueOf(goodsId))
									.addMetaData("selectionRelationIdList", selectionRelationIdList)
									.addMetaData("endTime", System.currentTimeMillis())
									.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
							throw new RedisRunException("InventoryProviderReadService:getSelectionRelationBySrIds run exception!",e);
						}
						log.info(lm.addMetaData("goodsId", String.valueOf(goodsId))
								.addMetaData("selectionRelationIdList", selectionRelationIdList)
								.addMetaData("endTime", System.currentTimeMillis())
								.addMetaData("useTime", LogUtil.getRunTime(startTime))
								.addMetaData("result", result).toJson());
						return result;
					}
					
				});

	}

	@Override
	public RedisInventoryDO getNotSeleInventory(final long goodsId)
			throws Exception {
		return readJedisFactory.withJedisDo(new JWork<RedisInventoryDO>() {
			@Override
			public RedisInventoryDO work(Jedis j) throws Exception {
				LogModel lm = LogModel.newLogModel("InventoryProviderReadServiceImpl.getNotSeleInventory(goodsId)");
				long startTime = System.currentTimeMillis();
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(goodsId))
						.addMetaData("startTime", startTime).toJson());
				RedisInventoryDO result = null;
				if (j == null)
					return result;
				try {
					Map<String, String> objMap = j
							.hgetAll(InventoryEnum.HASHCACHE + ":"
									+ String.valueOf(goodsId));
					if (!CollectionUtils.isEmpty(objMap)) {
						result = (RedisInventoryDO) ObjectUtil.convertMap(
								RedisInventoryDO.class, objMap);

					} else {
						result = nullCacheInitService
								.initGoodsInventoryNotSelectionOrSuppliers(j,
										goodsId);
					}
				} catch (Exception e) {
					log.error(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
							+ String.valueOf(goodsId)).addMetaData("endTime", System.currentTimeMillis())
							.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
					throw new RedisRunException("InventoryProviderReadService:getNotSeleInventory run exception!",e);
				}
				log.info(lm.addMetaData("key", InventoryEnum.HASHCACHE + ":"
						+ String.valueOf(goodsId))
						.addMetaData("endTime", System.currentTimeMillis())
						.addMetaData("useTime", LogUtil.getRunTime(startTime))
						.addMetaData("result", result).toJson());
				return result;
			}
			
		});
		
		
	}

	/**
	 * װ��ѡ��model������Ϣ
	 * 
	 * @param resultTmp
	 * @return
	 */
	private GoodsSelectionRelationModel asembly(
			RedisGoodsSelectionRelationDO resultTmp) {
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
