package com.tuan.inventory.domain.support.util;

import java.util.List;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;

public class StringUtil {
    /**
     * 商品id与选型id关系 返回 以空格分隔
     * @param rgsrList
     * @return
     */
	public static String getSelectionRelationString(List<RedisGoodsSelectionRelationDO> rgsrList) {
		StringBuffer sb  = new StringBuffer();
		//i.首先保存商品id与选型id的关联关系
		for(RedisGoodsSelectionRelationDO rgsr:rgsrList) {
			sb.append(rgsr.getId());
			sb.append(String.valueOf((char) 29));
		}
		return sb.toString();
	}
	
	
	/**
     * 商品id与选型id关系 返回 以空格分隔
     * @param rgsrList
     * @return
     */
	public static String getSuppliersInventoryString(List<RedisGoodsSuppliersInventoryDO> rgsiList) {
		StringBuffer sb  = new StringBuffer();
		//i.首先保存商品id与选型id的关联关系
		for(RedisGoodsSuppliersInventoryDO rgsi:rgsiList) {
			sb.append(rgsi.getId());
			sb.append(String.valueOf((char) 29));
		}
		return sb.toString();
	}
}
