package com.tuan.inventory.domain.support.util;

import java.util.List;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;

public class StringUtil {
    /**
     * ��Ʒid��ѡ��id��ϵ ���� �Կո�ָ�
     * @param rgsrList
     * @return
     */
	public static String getSelectionRelationString(List<RedisGoodsSelectionRelationDO> rgsrList) {
		StringBuffer sb  = new StringBuffer();
		//i.���ȱ�����Ʒid��ѡ��id�Ĺ�����ϵ
		for(RedisGoodsSelectionRelationDO rgsr:rgsrList) {
			sb.append(rgsr.getId());
			sb.append(String.valueOf((char) 29));
		}
		return sb.toString();
	}
	
	
	/**
     * ��Ʒid��ѡ��id��ϵ ���� �Կո�ָ�
     * @param rgsrList
     * @return
     */
	public static String getSuppliersInventoryString(List<RedisGoodsSuppliersInventoryDO> rgsiList) {
		StringBuffer sb  = new StringBuffer();
		//i.���ȱ�����Ʒid��ѡ��id�Ĺ�����ϵ
		for(RedisGoodsSuppliersInventoryDO rgsi:rgsiList) {
			sb.append(rgsi.getId());
			sb.append(String.valueOf((char) 29));
		}
		return sb.toString();
	}
}
