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
	
	//库存在redis中存储类型，关联key的前缀表示
	public final static String GOODS_INVENTORY_PREFIX="hash_goods.inventory";//商品总库存前缀
	public final static String GOODS_SELECTION_RELATIONSHIP_PREFIX="set_goods_selection.relation";//商品id与选型id关联关系前缀
	public final static String GOODS_SUPPLIERS_RELATIONSHIP_PREFIX="set_goods_suppliers.relation";//商品id与分店id关联关系前缀
	public final static String SELECTION_INVENTORY_PREFIX="hash_selection.inventory";//选型商品库存前缀
	public final static String SUPPLIERS_INVENTORY_PREFIX="hash_suppliers.inventory";//分店商品库存前缀
	//队列状态
	//public final static String ACTIVE = "1";  // 1:正常：有效可处理（active）
	//public final static String LOCKED = "3";  //3：初始状态（locked）
	//public final static String EXCEPTION = "5"; //5：标记可以被用作异常队列处(exception)
	
	//商品属性类别
/*	public final static String SELECTION = "选型商品库存";  // 标识：选型、总数、分店
	public final static String SUPPLIERS = "分店商品库存";  
	public final static String GOODS = "商品总库存"; */
	//库存扣减返回参数：生成的队列主键id值的key名称定义
	public final static String QUEUE_KEY_ID="queue:key.id";//异步触发记录日志流水的队列名称
	
	
	/** 队列结果状态 status */
	/*public static final int QUEUE_STATUS_NOT_CONSUMER = 1;//未消费
	public static final int QUEUE_STATUS_CONSUMER_SUCCESS = 2;//消费成功(包括消费次数已经用完，也标记成功，但是队列的阿状态不一样)
	public static final int QUEUE_STATUS_CONSUMER_FAILED = 3;//消费失败

	public static final int QUEUE_MAX_CONSUME_COUNT = 8;//最大消费次数
*/	
	
	
	/** 系统异常常数 */
	public static final int SUCCESS = 1;// 日志操作成功
	public static final int SERVICE_SYSTEM_FALIURE = -2;// 系统级别异常
	public static final int SERVICE_DATABASE_FALIURE = -1;// 数据库异常
	public static final int NO_ALIVE_DATASOURCE = 2;// 没有可用的数据源
	public static final int NOT_SUPPORT = 0xFF0005;// 当前不支持
	public static final int PARAMS_INVALID = 0xFF0006;// 参数无效
	public static final int NO_DATA = 0xFF0007;// 没有日志数据
	public static final int NOT_SAFE_IPADDRESS = 0xFF0008;// 不在安全ip列表以内
	public static final int OVER_TOP_VALUE = 0xFF0009;// 参数超出了期限值,一般是取得数值太大
	public static final int DATA_EXISTED = 0;// 数据已经存在
	public static final int UNKNOW_ERROR = 0xFF0012;// 未知错误
}
