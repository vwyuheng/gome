package com.tuan.inventory.model.util;

/**
 * 队列名称定义
 * @author henry.yu
 * @date 2014/3/12
 */
public class QueueConstant {
	
	public final static String QUEUE_KEY_MEMBER="queue:key.member";//缓存队列的 key member信息
	public final static String QUEUE_SEND_MESSAGE="queue:jobs.send";//异步触发发布notifyserver库存信息的队列
	//public final static String QUEUE_LOGS_MESSAGE="queue:jobs.logs";//异步触发记录日志流水的队列名称
	public final static String QUEUE_LOGS_MESSAGE="inventory_logs";//新队列名称
	
	//库存在redis中存储类型，关联key的前缀表示
	public final static String GOODS_BASE_INVENTORY_PREFIX="hash_goods_base.inventory";//商品总库存基本信息前缀
	public final static String GOODS_INVENTORY_PREFIX="hash_goods.inventory";//商品总库存前缀
	public final static String GOODS_SELECTION_RELATIONSHIP_PREFIX="set_goods_selection.relation";//商品id与选型id关联关系前缀
	public final static String GOODS_SUPPLIERS_RELATIONSHIP_PREFIX="set_goods_suppliers.relation";//商品id与分店id关联关系前缀
	public final static String GOODS_WMS_RELATIONSHIP_PREFIX="set_goods_wms.relation";//商品id与物流库存id关联关系前缀
	public final static String SELECTION_INVENTORY_PREFIX="hash_selection.inventory";//选型商品库存前缀
	public final static String SUPPLIERS_INVENTORY_PREFIX="hash_suppliers.inventory";//分店商品库存前缀
	public final static String WMS_INVENTORY_PREFIX="hash_wms.inventory";//物流商品库存前缀
	//队列状态
	//public final static String ACTIVE = "1";  // 1:正常：有效可处理（active）
	//public final static String LOCKED = "3";  //3：初始状态（locked）
	//public final static String EXCEPTION = "5"; //5：标记可以被用作异常队列处(exception)
	
	
	//库存扣减返回参数：生成的队列主键id值的key名称定义
	public final static String QUEUE_KEY_ID="queue:key.id";//异步触发记录日志流水的队列名称
	
	public final static String INVENTORY_ADJUST="inventory:adjust"; //库存调整
	/** 队列结果状态 status */
	/*public static final int QUEUE_STATUS_NOT_CONSUMER = 1;//未消费
	public static final int QUEUE_STATUS_CONSUMER_SUCCESS = 2;//消费成功(包括消费次数已经用完，也标记成功，但是队列的阿状态不一样)
	public static final int QUEUE_STATUS_CONSUMER_FAILED = 3;//消费失败

	public static final int QUEUE_MAX_CONSUME_COUNT = 8;//最大消费次数
*/	
	
	
	/** 系统异常常数 */
	public static final int NO_GOODS = 1046;// 没有可用的商品数据
	
	public static final int NO_WMS_DATA = 1041;// 物流信息不存在
	public static final int NO_SELECTION = 1047;// 没有可用的选型数据
	public static final int NO_DATA = 0;// 没有可用的数据
	public static final int SUCCESS = 1;// 操作成功
	public static final int INCORRECT_UPDATE = -13;// 更改超过预期的记录数
	public static final int DATA_EXISTED = 2;// 数据已经存在
	
	public static final int INVALID_PARAM = -5;// 传入参数错误
	public static final int INVALID_GOODSID = -8;// 商品id无效
	public static final int INVALID_WMSGOODSID = 1032;// 物流编码无效
	public static final int INVALID_GOODSBASEID = 1043;// 商品基本id无效
	        
	public static final int INVALID_SELECTIONID = -12;// 选型id无效
	public static final int INVALID_SUPPLIERSID = -11;// 分店id无效
	
	
	public static final int UNKNOW_ERROR = -9;// 未知错误
	public static final int SERVICE_REDIS_FALIURE = -88;// redis数据库异常
	public static final int SERVICE_DATABASE_FALIURE = -99;// 数据库异常
	public static final int SERVICE_SYSTEM_FALIURE = -100;// 系统级别异常
	
	public static final int INVALID_IP = 1001;// 客服端IP无效
	public static final int INVALID_CLIENT = 1002;// 客户端名称无效
	public static final int INVALID_TIME = 1003;// 时间戳无效
	public static final int INVALID_RETURN = 1010;// 返回值不正确
	
	public static final int FAIL_ADJUST_INVENTORY = -2; //	库存调整失败
	
	public static final int NO_GOODSBASE = 1044 ;//商品基本信息不存在
	
	//订单中心跑任务还库存用
	public static final int TASK_RESTORE_INVENTORY = 1 ;//商品基本信息不存在
	
	
	
	
}
