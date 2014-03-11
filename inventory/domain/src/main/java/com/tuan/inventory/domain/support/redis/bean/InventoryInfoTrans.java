package com.tuan.inventory.domain.support.redis.bean;

import java.io.Serializable;

/**
 * <pre>
 * 库存对象信息转化
 * @author henry.yu
 * @date 20140306
 * </pre>
 */
public class InventoryInfoTrans implements Serializable{

	private static final long serialVersionUID = 1L;
	
/*	
	public static String bean2String(Object bean){
		if(bean == null){
			return null;
		}
		StringBuilder result = new StringBuilder();
		result.append(bean.orderStatus == null ? "" : bean.orderStatus);
		result.append(",");
		result.append(bean.payStatus == null ? "" : bean.payStatus);
		result.append(",");
		result.append(bean.refundStatus == null ? "" : bean.refundStatus);
		result.append(",");
		result.append(bean.ticketCount == null ? "" : bean.ticketCount);
		result.append(",");
		result.append(bean.createTime == null ? "" : bean.createTime);
		result.append(",");
		result.append(bean.updateTime == null ? "" : bean.updateTime);
		result.append(",");
		result.append(bean.hasTicket == null ? "" : bean.hasTicket);
		result.append(",");
		result.append(bean.goodsCount == null ? "" : bean.goodsCount);
		result.append(",");
		result.append(bean.orderTicketStatus == null ? "" : bean.orderTicketStatus);
		result.append(",");
		result.append("end");
		return result.toString();
	}
	
	public static OrderInfoStatus string2Bean(String str){
		if(str == null || str.isEmpty()){
			return null;
		}
		OrderInfoStatus bean = new OrderInfoStatus();
		String[] arrayStr = str.split(",");
		bean.orderStatus =  Byte.valueOf(arrayStr[0]);
		bean.payStatus = Byte.valueOf(arrayStr[1]);
		bean.refundStatus = Byte.valueOf(arrayStr[2]);
		bean.ticketCount =  Short.valueOf(arrayStr[3]);
		bean.createTime =  Integer.valueOf(arrayStr[4]);
		bean.updateTime =  Integer.valueOf(arrayStr[5]);
		if(arrayStr.length > 7){
			bean.hasTicket = "".equals(arrayStr[6]) ? -1 : Integer.valueOf(arrayStr[6]);
			bean.goodsCount = "".equals(arrayStr[7]) ? null : Integer.valueOf(arrayStr[7]);
		}
		if(arrayStr.length > 9){
			bean.orderTicketStatus = "".equals(arrayStr[8]) ? null : Integer.valueOf(arrayStr[8]);
		}
		return bean;
	}
*/
	
	public static void main(String[] args) {
		String str = "1,2,3,4,5,6,end";
		/*OrderInfoStatus orderInfoStatus = OrderInfoStatus.string2Bean(str);
		str = OrderInfoStatus.bean2String(orderInfoStatus);
		
		orderInfoStatus = OrderInfoStatus.string2Bean(str);
		str = OrderInfoStatus.bean2String(orderInfoStatus);*/
	}
}
