package com.tuan.inventory.domain.support.mysql;

import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;

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
	public void insertAndUpdateSelectionRelation(GoodsSelectionDO rgsrDo) throws Exception;
}
