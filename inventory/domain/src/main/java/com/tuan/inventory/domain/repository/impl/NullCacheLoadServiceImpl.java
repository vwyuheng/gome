package com.tuan.inventory.domain.repository.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.dao.NullCacheInitDAO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.domain.repository.GoodTypeDomainRepository;
import com.tuan.inventory.domain.repository.NullCacheLoadService;

public class NullCacheLoadServiceImpl implements NullCacheLoadService {
	@Resource
	private GoodTypeDomainRepository goodTypeDomainRepository;
	@Resource
	private NullCacheInitDAO nullCacheInitDAO;
	
	private static Log log = LogFactory.getLog(NullCacheLoadService.class);
	/**
	 * 加载分店配型关系数据
	 */
	@Override
	public RedisGoodsSelectionRelationDO getCacheSelectionRelationInfoById(
			int id){
		GoodsSelectionRelationDO srDo = null;
		try {
			srDo = goodTypeDomainRepository.selectSelectionRelationBySrId(id);
		} catch (Exception e) {
			log.error("NullCacheLoadService:goodTypeDomainRepository.selectSelectionRelationBySrId invoke error [SelectionRelationId="
					+ id + "]", e);
		}
		if (srDo == null) {
			return null;
		}else {
			RedisGoodsSelectionRelationDO rsrDo = new RedisGoodsSelectionRelationDO();
			rsrDo.setId(srDo.getId());
			rsrDo.setGoodTypeId(srDo.getGoodTypeId());
			rsrDo.setLeftNumber(srDo.getLeftNumber());
			rsrDo.setTotalNumber(srDo.getTotalNumber());
			rsrDo.setLimitStorage(srDo.getLimitStorage());
			return rsrDo;
		}
		
	}
	/**
	 * 加载分店选型信息
	 */
	@Override
	public GoodsSelectionRelationDO getCacheSelectionRelationDOById(int id){
		GoodsSelectionRelationDO srDo = null;
		try {
			srDo = goodTypeDomainRepository.selectSelectionRelationBySrId(id);
		} catch (Exception e) {
			log.error("NullCacheLoadService:goodTypeDomainRepository.selectSelectionRelationBySrId invoke error [SelectionRelationId="
					+ id + "]", e);
		}
		if (srDo == null) {
			return null;
		}else {
			return srDo;
		}
			
		
	}
	/**
	 * 加载商品商家库存信息并转换为redis存储对象 
	 */
	@Override
	public RedisGoodsSuppliersInventoryDO getCacheSuppliersInventoryInfoById(int id) throws Exception{
		RedisGoodsSuppliersInventoryDO rgsiDO = null;
		GoodsSuppliersInventoryDO gsiDO = goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId(id);
		if(gsiDO == null){
			return null;
		}else {
			rgsiDO = new RedisGoodsSuppliersInventoryDO();
			rgsiDO.setId(gsiDO.getId());
			rgsiDO.setGoodsId(gsiDO.getGoodsId());
			rgsiDO.setLeftNumber(gsiDO.getLeftNumber());
			rgsiDO.setTotalNumber(gsiDO.getTotalNumber());
			rgsiDO.setSuppliersId(gsiDO.getSuppliersId());
			rgsiDO.setLimitStorage(gsiDO.getLimitStorage());
		}
			
		return rgsiDO;
	}
	/**
	 * 加载商品商家库存信息
	 */
	@Override
	public GoodsSuppliersInventoryDO getCacheSuppliersInventoryDOById(int id) throws Exception{
		return goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId(id);
	}
	@Override
	public RedisInventoryDO getRedisInventoryInfoByGoodsId(Long goodsId) {
		return nullCacheInitDAO.selectRedisInventory(goodsId);
	}

}
