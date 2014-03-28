package com.tuan.inventory.domain.repository;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
/**
 * 库存队列处理后端服务接口
 * 包括：日志，库存更新队列等
 * @author henry.yu
 * @date 2014/3/13
 */
public interface InventoryQueueService {

	public void pushLogQueues(final RedisInventoryLogDO logDO) throws Exception;
	
	public void pushQueueSendMsg(final RedisInventoryQueueDO queueDO) throws Exception;
	
	public void lremLogQueue(final RedisInventoryLogDO logDO) throws Exception;
	/**
	 * 库存还原:回滚库存
	 * @param key
	 * @param upStatusNum :该参数特别说明：当你想减值时传负值，当是增加值时传正整数值
	 * @return
	 * @throws Exception
	 */
	public void rollbackInventoryCache(final String key,final int upStatusNum) throws Exception ;
	/**
	 * 标记队列状态
	 * @param key
	 * @param upStatusNum :该参数特别说明：当你想减值时传负值，当是增加值时传正整数值
	 * @throws Exception
	 */
	public void markQueueStatus(final String key,final int upStatusNum) throws Exception ;
}
