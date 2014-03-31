package com.tuan.inventory.domain.repository;

import java.util.List;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.support.bean.RedisInventoryBean;
import com.tuan.inventory.model.GoodsSelectionRelationModel;

public interface InventoryProviderReadService {
	/**
	 * 根据选型id获取选型库存信息
	 * @param SelectionRelationId
	 * @return
	 * @throws Exception
	 */
	public GoodsSelectionRelationDO getSelectionRelationBySrId(final int SelectionRelationId) throws Exception;
	/**
	 * 根据商品分店id获取fen
	 * @param SuppliersInventoryId
	 * @return
	 * @throws Exception
	 */
	public GoodsSuppliersInventoryDO getSuppliersInventoryBySiId(final int SuppliersInventoryId) throws Exception;
	/**
	 * 获取商品选型列表
	 * @param selectionRelationIdList
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public List<GoodsSelectionRelationModel>  getSelectionRelationBySrIds(final List<Long> selectionRelationIdList,final long goodsId) throws Exception;
	/**
	 * 根据商品id获取商品库存信息
	 * @param goodsId
	 * @return 
	 * @throws Exception
	 */
	public RedisInventoryDO getNotSeleInventory (final long goodsId) throws Exception;
	/**
	 * 根据zset score 状态取队列数据
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public List<RedisInventoryQueueDO> getInventoryQueueByScoreStatus (final Double status) throws Exception;
	
	public List<RedisInventoryLogDO> getInventoryLogsQueue (final String key,final int timeout) throws Exception;
	
	public List<RedisInventoryLogDO> getInventoryLogsQueue() throws Exception;
	
	public List<RedisInventoryLogDO> getInventoryLogsQueueByIndex() throws Exception;
	
	public RedisInventoryBean getInventoryInfosByKey(final String key) throws Exception;
}
