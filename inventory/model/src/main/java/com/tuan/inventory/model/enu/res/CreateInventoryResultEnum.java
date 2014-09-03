package com.tuan.inventory.model.enu.res;

public enum CreateInventoryResultEnum {
	SUCCESS						(0, 	"成功"),
	DATA_EXISTED						(2, 	"DATA_EXISTED"),
	INVALID_USER_ID				(-1, 	"用户id无效"),
	FAIL_ADJUST_INVENTORY			(-2, 	"库存调整失败"),
	FAIL_ADJUST_WATERFLOOD			(-3, 	"注水调整失败"),
	SHORTAGE_STOCK_INVENTORY				(-4, 	"库存不足"),
	INVALID_PARAM				(-5, 	"参数无效"),
	INVALID_ORDER_ID			(-6, 	"订单ID无效"),
	REPEAT_REQUEST				(-7, 	"重复请求"),
	/**商品id无效*/
	INVALID_GOODSID         (-8, "商品id无效"),
	IS_EXISTED				(-9, 	"商品库存已存在"),
	/**分店id无效*/
	INVALID_SUPPLIERSID         (-11, "商品分店id无效"),
	INVALID_SELECTIONID         (-12, "商品选型id无效"),
	INVALID_TYPE         (-14, "无效的类型"),
	INVALID_SELECTIONNUM         (1016, "扣减商品选型库存数量不能为负数"),
	INVALID_SUPPLIERSNUM         (1017, "扣减商品分店库存数量不能为负数"),
	AFT_ADJUST_WATERFLOOD			(1021, 	"调整后的注水值为负数"),
	SELECTION_GOODS         (1018, "选型商品，选型不能为空!"),
	SUPPLIERS_GOODS         (1019, "分店商品，分店不能为空!"),
	SEL_SUPP_GOODS         (1020, "包含选型和分店的商品，选型和分店都不能为空!"),
	INVALID_WMSGOODSID         (1032, "物流编码无效"),
	INVALID_WMSID         (1033, "物流库存表主键无效"),
	INVALID_GOODSTYPEID         (1034, "无效的商品类型id"),
	INVALID_SELIDANDGOODSTYPEID         (1035, "无效的物流选型id和商品类型id"),
	RUNTIME_EXCEPTION	(2000,	"程序运行时错误"),
	QUERY_ERROR         (1031, "通过商品类型id获取商品id时发生错误"),
	SERVICE_REDIS_FALIURE					(-88, 	"redis数据库错误"),
	DLOCK_ERROR(-98, "获取锁错误"),
	DB_ERROR					(-99, 	"数据库错误"),
	SYS_ERROR					(-100, 	"系统错误"),
	INIT_INVENTORY_ERROR					(-10, 	"库存初始化过程中发生错误"),
	SEL_OR_SUPP        (1036, "包含选型和分店的商品，类型应为选型或分店!"),
	AFT_ADJUST_INVENTORY			(1037, 	"库存调整后数量不能为负"),
	NONE_LIMIT_STORAGE			(1038, 	"非限制库存商品无需调整其库存"),
	NO_SELECT_SELECTION			(1039, 	"当前物流码有选型信息，但用户没有选择相关选型."),
	NO_WMS_DATA			(1041, 	"物流信息不存在."),
	INVALID_SELECTION_GOODSTYPEID         (1042, "非法的商品选型类型id"),
	INVALID_GOODSBASEID         (1043, "商品基本id无效"),
	NO_GOODSBASE        (1044, "商品基本信息不存在!"),
	FAILED_ORDERQUERYSERVICE        (1045, "调用订单中心接口失败!"),
	NO_GOODS       (1046, "商品信息不存在!"),
	NO_SELECTION       (1047, "选型信息不存在！"),
	NO_RETORE       (1048, "新老商品id相同,无需还还库存!"),
	HAD_RETORE_COMPLETED       (1049, "订单未支付占用库存超过了实际库存,并已还还完毕!"),
	NO_SUPPORT_ADJUST       (1050, "由于调减量大于调整前剩余库存量,会导致销量剩余库存与总库存不平,故此情况不支持调整!"),
	ISGOODSIDEXIST_INVALID       (1051, "商品id是否存在标识无效!"),
	INVALID_LOG_PARAM				(1, 	"无效的日志");
	
	
	
	private int code;
	private String description;

	private CreateInventoryResultEnum(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public static CreateInventoryResultEnum valueOfEnum(int code) {
		switch (code) {
			case 0:
				return SUCCESS;
			case -1:
				return INVALID_USER_ID;
			case -2:
				return FAIL_ADJUST_INVENTORY;
			case -3:
				return FAIL_ADJUST_WATERFLOOD;
			case -4:
				return SHORTAGE_STOCK_INVENTORY;
			case -5:
				return INVALID_PARAM;
			case -6:
				return INVALID_ORDER_ID;
			case -7:
				return REPEAT_REQUEST;
			case -8:
				return INVALID_GOODSID;
			case -9:
				return IS_EXISTED;
			case -10:
				return INIT_INVENTORY_ERROR;
			case -11:
				return INVALID_SUPPLIERSID;
			case -12:
				return INVALID_SELECTIONID;
			case 1:
				return INVALID_LOG_PARAM;
			case -14:
				return INVALID_TYPE;
			case 1016:
				return INVALID_SELECTIONNUM;
			case 1046:
				return NO_GOODS;
			case 1047:
				return NO_SELECTION;
			case 2:
				return DATA_EXISTED;
			case 1032:
				return INVALID_WMSGOODSID;
			case 1041:
				return NO_WMS_DATA;
			case -88:
				return SERVICE_REDIS_FALIURE;
			case -98:
				return DLOCK_ERROR;
			case -99:
				return DB_ERROR;
			case -100:
				return SYS_ERROR;
			default:
				return SYS_ERROR;
		}
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
