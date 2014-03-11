package com.tuan.inventory.domain.support.mysql;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;

/**
 * @title ��ʼ��mysql
 *        ��Ӧ������
 * @author henry.yu
 *
 */
public interface NullDbInitService {
	/**
	 * ��ʼ������Ʒ�ֵ���ѡ�͹�ϵ
	 * mysql db����
	 * @throws Exception
	 */
	public void insertAndUpdateSelectionRelation(RedisGoodsSelectionRelationDO rgsrDo) throws Exception;
}
