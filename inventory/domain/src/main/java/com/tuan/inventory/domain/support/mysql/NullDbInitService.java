package com.tuan.inventory.domain.support.mysql;

import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;

/**
 * @title 初始化mysql
 *        相应表数据
 * @author henry.yu
 *
 */
public interface NullDbInitService {
	/**
	 * 初始更新商品分店与选型关系
	 * mysql db数据
	 * @throws Exception
	 */
	public void insertAndUpdateSelectionRelation(GoodsSelectionDO rgsrDo) throws Exception;
}
