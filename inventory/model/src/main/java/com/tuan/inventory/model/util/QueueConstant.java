package com.tuan.inventory.model.util;

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
	
	/** 队列操作 status */
	public static final int QUEUE_STATUS_ACTIVE = 1;//消息可用
	public static final int QUEUE_STATUS_LOCKED = 3;//消息被占用
	public static final int QUEUE_STATUS_DELETED = 5;//消息删除（表示成功）
	public static final int QUEUE_STATUS_CANCEL = 4;//消费作废（表示失败的）
	
	/** 队列结果状态 status */
	public static final int QUEUE_STATUS_NOT_CONSUMER = 1;//未消费
	public static final int QUEUE_STATUS_CONSUMER_SUCCESS = 2;//消费成功(包括消费次数已经用完，也标记成功，但是队列的阿状态不一样)
	public static final int QUEUE_STATUS_CONSUMER_FAILED = 3;//消费失败

	public static final int QUEUE_MAX_CONSUME_COUNT = 8;//最大消费次数
	
	
	
	/** 系统异常常数 */
	public static final int SUCCESS = 0xFF0001;// 日志操作成功
	public static final int SERVICE_SYSTEM_FALIURE = 0xFF0002;// 系统级别异常
	public static final int SERVICE_DATABASE_FALIURE = 0xFF0003;// 数据库异常
	public static final int NO_ALIVE_DATASOURCE = 0xFF0004;// 没有可用的数据源
	public static final int NOT_SUPPORT = 0xFF0005;// 当前不支持
	public static final int PARAMS_INVALID = 0xFF0006;// 参数无效
	public static final int NO_DATA = 0xFF0007;// 没有日志数据
	public static final int NOT_SAFE_IPADDRESS = 0xFF0008;// 不在安全ip列表以内
	public static final int OVER_TOP_VALUE = 0xFF0009;// 参数超出了期限值,一般是取得数值太大
	public static final int DATA_EXISTED = 0xFF0010;// 数据已经存在
	public static final int UNKNOW_ERROR = 0xFF0012;// 未知错误
}
