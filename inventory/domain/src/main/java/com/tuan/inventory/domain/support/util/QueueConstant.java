package com.tuan.inventory.domain.support.util;

/**
 * 队列名称定义
 * @author henry.yu
 * @date 2014/3/12
 */
public class QueueConstant {
	
	public final static String QUEUE_KEY_MEMBER="queue:key.member";//缓存队列的 key member信息
	public final static String QUEUE_SEND_MESSAGE="queue:jobs.send";//异步触发发布notifyserver库存信息的队列
	public final static String QUEUE_LOGS_MESSAGE="queue:jobs.logs";//异步触发记录日志流水的队列名称
	//队列状态
	//public final static String ACTIVE = "1";  // 1:正常：有效可处理（active）
	//public final static String LOCKED = "3";  //3：初始状态（locked）
	//public final static String EXCEPTION = "5"; //5：标记可以被用作异常队列处(exception)
	
	//商品属性类别
	public final static String SELECTION = "选型商品库存";  // 标识：选型、总数、分店
	public final static String SUBBRANCH = "分店商品库存";  
	public final static String GOODS = "商品总库存"; 
	//库存扣减返回参数：生成的队列主键id值的key名称定义
	public final static String QUEUE_KEY_ID="queue:key.id";//异步触发记录日志流水的队列名称
}
