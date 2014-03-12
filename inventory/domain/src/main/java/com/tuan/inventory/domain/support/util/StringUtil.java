package com.tuan.inventory.domain.support.util;

import java.util.List;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.model.OrderGoodsSelectionModel;

public class StringUtil {
    /**
     * 多个选型id关系 返回 以空格分隔
     * @param rgsrList
     * @return
     */
	public static String getSelectionRelationString(List<RedisGoodsSelectionRelationDO> rgsrList) {
		StringBuffer sb  = new StringBuffer();
		//多个选型id以空格间隔的字符串
		for(RedisGoodsSelectionRelationDO rgsr:rgsrList) {
			sb.append(rgsr.getId());
			sb.append(String.valueOf((char) 29));
		}
		return sb.toString();
	}
	
	
	/**
     * 多个分店id关系 返回 以空格分隔
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
	
	/**
	 * 拼装返回选型或分店id的字符串 
	 * 以空格分隔
	 * @param goodsSelectionList
	 * @return
	 */
	public static String getIdsString(List<OrderGoodsSelectionModel> goodsSelectionList) {
		StringBuffer sb  = new StringBuffer();
			
			for(OrderGoodsSelectionModel ogsm:goodsSelectionList) {  
				if (ogsm.getSelectionRelationId()!= null
						&& ogsm.getSelectionRelationId()> 0) { //商品选型的id,多个选型id以空格间隔的字符串
					sb.append(ogsm.getSelectionRelationId());
					//sb.append(":");
					//sb.append(ogsm.getCount().intValue());
					sb.append(String.valueOf((char) 29));
				}else if(ogsm.getSuppliersId()>0){  //商品分店的id,多个分店id以空格间隔的字符串
					sb.append(ogsm.getSuppliersId());
					//sb.append(":");
					//sb.append(ogsm.getCount().intValue());
					sb.append(String.valueOf((char) 29));
				}
				
			}
		
		return sb.toString();
	}
}
