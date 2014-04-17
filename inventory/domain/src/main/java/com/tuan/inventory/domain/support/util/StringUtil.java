package com.tuan.inventory.domain.support.util;

import java.util.List;

import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;

public class StringUtil {
    /**
     * 多个选型id关系 返回 以空格分隔
     * @param rgsrList
     * @return
     */
	public static String getSelectionRelationString(List<GoodsSelectionDO> rgsrList) {
		StringBuffer sb  = new StringBuffer();
		//多个选型id以空格间隔的字符串
		for(GoodsSelectionDO rgsr:rgsrList) {
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
	public static String getSuppliersInventoryString(List<GoodsSuppliersDO> rgsiList) {
		StringBuffer sb  = new StringBuffer();
		//i.首先保存商品id与选型id的关联关系
		for(GoodsSuppliersDO rgsi:rgsiList) {
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
	public static String getIdsStringSelection(List<GoodsSelectionModel> goodsSelectionList) {
		StringBuffer sb  = new StringBuffer();
			
			for(GoodsSelectionModel ogsm:goodsSelectionList) {  
				if (ogsm.getId()!= null
						&& ogsm.getId()> 0) { //商品选型的id,多个选型id以空格间隔的字符串
					sb.append(ogsm.getId());
					//sb.append(":");
					//sb.append(ogsm.getCount().intValue());
					sb.append(String.valueOf((char) 29));
				}
				
			}
		
		return sb.toString();
	}
	
	
	public static String getIdsStringSuppliers(List<GoodsSuppliersModel> goodsSelectionList) {
		StringBuffer sb  = new StringBuffer();
			
			for(GoodsSuppliersModel ogsm:goodsSelectionList) {  
				if (ogsm.getId()!= null
						&& ogsm.getId()> 0) { //商品选型的id,多个选型id以空格间隔的字符串
					sb.append(ogsm.getId());
					//sb.append(":");
					//sb.append(ogsm.getCount().intValue());
					sb.append(String.valueOf((char) 29));
				}
				
			}
		
		return sb.toString();
	}
}
