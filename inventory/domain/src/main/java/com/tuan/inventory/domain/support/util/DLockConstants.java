package com.tuan.inventory.domain.support.util;

/**
 * @Author: henry.yu
 * @Date:   2014-5-13
 * 分布式锁key值定义
 */

public class DLockConstants {
	//初始化库存分布式锁参数
	public static final String INIT_LOCK_KEY = "init";
	public static final int INIT_LOCK_RETRY_TIMES = 5;
	public static final long INIT_LOCK_TIME = 3000L;
	
	
	//库存扣减分布式锁参数
	public static final String DEDUCT_LOCK_KEY = "deduct";
	public static final int DEDUCT_LOCK_RETRY_TIMES = 5;
	public static final long DEDUCT_LOCK_TIME = 3000L;
	//库存回调确认回滚的分布式锁参数
	public static final String ROLLBACK_LOCK_KEY = "rollback";
	public static final int ROLLBACK_LOCK_RETRY_TIMES = 5;
	public static final long ROLLBACK_LOCK_TIME = 3000L;
	//库存调整的分布式锁参数
	public static final String ADJUST_LOCK_KEY = "adjust";
	public static final int ADJUST_LOCK_RETRY_TIMES = 5;
	public static final long ADJUSTK_LOCK_TIME = 3000L;
	//物流数据更新的分布式锁参数
	public static final String UPDATEWMS_LOCK_KEY = "updatewmsdata";
	public static final int UPDATEWMS_LOCK_RETRY_TIMES = 5;
	public static final long UPDATEWMS_LOCK_TIME = 3000L;
	
	
	//接口幂等key的前缀
	//job处理tag锚点
	public final static String JOB_HANDLER = "pending";
	//job处理完成后更新的key
	public final static String JOB_HANDLER_COMPLETED = "completed";
	//库存扣减接口
	public final static String DEDUCT_INVENTORY = "deduct";
	//扣减成功后更新的key
	public final static String DEDUCT_INVENTORY_SUCCESS = "deduct_success";
	public final static String DEDUCT_QUEUEID = "deduct_success_queueid";
	//库存扣减回调接口
	public final static String CALLBACK_INVENTORY = "callback";
	//扣减成功后更新的key
	public final static String CALLBACK_INVENTORY_SUCCESS = "callback_success";
	//库存创建接口
	public final static String CREATE_INVENTORY = "create";
	//创建成功后更新的key
	public final static String CREATE_INVENTORY_SUCCESS = "create_success";
	//全量调整库存接口
	public final static String OVERRIDE_ADJUST_INVENTORY = "oradjusti";
	//调整成功后更新的key
	public final static String OVERRIDE_ADJUST_INVENTORY_SUCCESS = "oradjusti_success";
	//物流数据更新接口
	public final static String UPDATE_WMS_DATA = "upwmsdata";
	//更新成功后更新的key
	public final static String UPDATE_WMS_DATA_SUCCESS = "upwmsdata_success";
	//处理成功的标识
	public final static String HANDLER_SUCCESS = "success";
	//缓存的时长:单位是秒
	public final static int IDEMPOTENT_DURATION_TIME = 60*15;
	//job锚点的过期时长:单位是秒
	public final static long JOB_LOCK_TIME = 3000L;
}
