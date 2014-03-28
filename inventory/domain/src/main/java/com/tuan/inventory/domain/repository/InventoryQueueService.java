package com.tuan.inventory.domain.repository;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
/**
 * �����д����˷���ӿ�
 * ��������־�������¶��е�
 * @author henry.yu
 * @date 2014/3/13
 */
public interface InventoryQueueService {

	public void pushLogQueues(final RedisInventoryLogDO logDO) throws Exception;
	
	public void pushQueueSendMsg(final RedisInventoryQueueDO queueDO) throws Exception;
	
	public void lremLogQueue(final RedisInventoryLogDO logDO) throws Exception;
	/**
	 * ��滹ԭ:�ع����
	 * @param key
	 * @param upStatusNum :�ò����ر�˵�����������ֵʱ����ֵ����������ֵʱ��������ֵ
	 * @return
	 * @throws Exception
	 */
	public void rollbackInventoryCache(final String key,final int upStatusNum) throws Exception ;
	/**
	 * ��Ƕ���״̬
	 * @param key
	 * @param upStatusNum :�ò����ر�˵�����������ֵʱ����ֵ����������ֵʱ��������ֵ
	 * @throws Exception
	 */
	public void markQueueStatus(final String key,final int upStatusNum) throws Exception ;
}
