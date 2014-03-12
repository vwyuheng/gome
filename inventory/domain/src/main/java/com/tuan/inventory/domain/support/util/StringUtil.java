package com.tuan.inventory.domain.support.util;

import java.util.List;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.model.OrderGoodsSelectionModel;

public class StringUtil {
    /**
     * ���ѡ��id��ϵ ���� �Կո�ָ�
     * @param rgsrList
     * @return
     */
	public static String getSelectionRelationString(List<RedisGoodsSelectionRelationDO> rgsrList) {
		StringBuffer sb  = new StringBuffer();
		//���ѡ��id�Կո������ַ���
		for(RedisGoodsSelectionRelationDO rgsr:rgsrList) {
			sb.append(rgsr.getId());
			sb.append(String.valueOf((char) 29));
		}
		return sb.toString();
	}
	
	
	/**
     * ����ֵ�id��ϵ ���� �Կո�ָ�
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
	
	/**
	 * ƴװ����ѡ�ͻ�ֵ�id���ַ��� 
	 * �Կո�ָ�
	 * @param goodsSelectionList
	 * @return
	 */
	public static String getIdsString(List<OrderGoodsSelectionModel> goodsSelectionList) {
		StringBuffer sb  = new StringBuffer();
			
			for(OrderGoodsSelectionModel ogsm:goodsSelectionList) {  
				if (ogsm.getSelectionRelationId()!= null
						&& ogsm.getSelectionRelationId()> 0) { //��Ʒѡ�͵�id,���ѡ��id�Կո������ַ���
					sb.append(ogsm.getSelectionRelationId());
					//sb.append(":");
					//sb.append(ogsm.getCount().intValue());
					sb.append(String.valueOf((char) 29));
				}else if(ogsm.getSuppliersId()>0){  //��Ʒ�ֵ��id,����ֵ�id�Կո������ַ���
					sb.append(ogsm.getSuppliersId());
					//sb.append(":");
					//sb.append(ogsm.getCount().intValue());
					sb.append(String.valueOf((char) 29));
				}
				
			}
		
		return sb.toString();
	}
}
