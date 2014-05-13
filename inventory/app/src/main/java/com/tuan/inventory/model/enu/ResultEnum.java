package com.tuan.inventory.model.enu;



public enum ResultEnum {
	//SUCCESS		("0000",	"成功"),
	SUCCESS	("success",	"成功"),
	ERROR	("error",	"失败"),
	INVALID_IP			("1001",	"客服端IP无效"),
	INVALID_CLIENT		("1002",	"客户端名称无效"),
	INVALID_TIME		("1003",	"时间戳无效"),
	INVALID_RETURN      ("1010",	"返回值不正确"),
	
	INVALID_LOG_PARAM				("1", 	"无效的日志"),
	FAIL_ADJUST_INVENTORY			("-2", 	"库存调整失败"),
	FAIL_ADJUST_WATERFLOOD			("-3", 	"注水调整失败"),
	SHORTAGE_STOCK_INVENTORY				("-4", 	"库存不足"),
	INVALID_PARAM				("-5", 	"参数无效"),
	INVALID_GOODSID     ("-8",	"无效的商品id"),
	IS_EXISTED				("-9", 	"商品库存已存在"),
	INIT_INVENTORY_ERROR					("-10", 	"库存初始化过程中发生错误"),
	INVALID_SUPPLIERSID     ("-11",	"无效的商品分店id"),
	INVALID_SELECTIONID     ("-12",	"无效的商品选型id"),
	INCORRECT_UPDATE     ("-13",	"更改超过预期的记录数"),
	INVALID_TYPE         ("-14", "无效的类型"),
	DB_ERROR					("-99", 	"数据库错误"),
	SYS_ERROR					("-100", 	"系统错误"),
	
	
	INVALID_ISLIMIT_STORAGE     ("1007",	"是否限制库存无效"),      
	INVALID_ACK     ("1008",	"无效的确认标识"),
	INVALID_KEY     ("1011",	"无效的key"),
	
	NO_PARAMETER		("1009",	"请求参数不能为空"),
	
	
	INVALID_ADJUST_ID		("1012",	"无效的库存调整id"),
	INVALID_INVENTORY_TYPE		("1013",	"无效的库存类型"),
	INVALID_PERIOD     ("1014",	"无效的时间间隔"),
	INVALID_NUM     ("1015",	"扣减商品库存数量不能为负数"),
	INVALID_SELECTIONNUM     ("1016",	"扣减商品选型库存数量不能为负数"),
	INVALID_SUPPLIERSNUM     ("1017",	"扣减商品分店库存数量不能为负数"),
	SELECTION_GOODS         ("1018", "选型商品，选型不能为空！"),
	SUPPLIERS_GOODS         ("1019", "分店商品，分店不能为空！"),
	SEL_SUPP_GOODS         ("1020", "包含选型和分店的商品，选型和分店不能同时为空！"),
	AFT_ADJUST_WATERFLOOD			("1021", 	"调整后的注水值为负数"),
	INVALID_LEFTNUM_SELECTION     ("1022",	"商品库存剩余数量与其下选型商品剩余库存数量之和不一致!"),
	INVALID_TOTALNUM_SELECTION    ("1023",	"商品库存总数量与其下选型商品库存总数量之和不一致!"),
	INVALID_LEFTNUM_SUPPLIER     ("1024",	"商品库存剩余数量与其下分店商品剩余库存数量之和不一致!"),
	INVALID_TOTALNUM_SUPPLIER    ("1025",	"商品库存总数量与其下分店商品库存总数量之和不一致!"),
	INVALID_LEFTNUM_SELANDSUPP     ("1026",	"商品库存剩余数量与其下选型、分店商品剩余库存数量之和不一致!"),
	INVALID_TOTALNUM_SELANDSUPP    ("1027",	"商品库存总数量与其下选型、分店商品库存总数量之和不一致!"),
	NOTNULL_SEL_SUPP_GOODS         ("1028", "包含选型和分店的商品，选型和分店都不能为空！"),
	INVALID_SEL_LIMT         ("1029", "选型库存限制与商品库存限制不一致！"),
	INVALID_SUPP_LIMT         ("1030", "分店库存限制与商品库存限制不一致！"),
	QUERY_ERROR         ("1031", "通过商品类型id获取商品id时发生错误"),
	INVALID_WMSGOODSID         ("1032", "物流编码无效"),
	INVALID_WMSID         ("1033", "物流库存表主键无效"),
	INVALID_GOODSTYPEID         ("1034", "无效的商品类型id"),
	INVALID_SELIDANDGOODSTYPEID         ("1035", "无效的物流选型id和商品类型id"),
	SEL_OR_SUPP        ("1036", "包含选型和分店的商品，类型应为选型或分店！"),
	AFT_ADJUST_INVENTORY			("1037", 	"库存调整后数量不能为负"),
	NONE_LIMIT_STORAGE			("1038", 	"非限制库存商品无需调整其库存"),
	NO_DATA("0","没有可用的数据"),
	
	ERROR_2000	("2000",	"程序运行时错误"),
	NET_ERROR			("10998",	"网络异常"),
	SYSTEM_ERROR		("10999",	"系统错误"),
	ERROR_UNKONW       ("9999",	"其它");

	private String code;
	private String description;
	
	public static ResultEnum getResultStatusEnum(String code){
		if(code == null || code.isEmpty()){
			return ResultEnum.ERROR_UNKONW;
		}
		if("success".equals(code)){
			return ResultEnum.SUCCESS;
		}
		if("error".equals(code)){
			return ResultEnum.ERROR;
		}
		if(code.equals("1001")){
			return ResultEnum.INVALID_IP;
		}
		if(code.equals("1002")){
			return ResultEnum.INVALID_CLIENT;
		}
		if(code.equals("1003")){
			return ResultEnum.INVALID_TIME;
		}
		if(code.equals("1")){
			return ResultEnum.INVALID_LOG_PARAM;
		}
		
		if(code.equals("-2")){
			return ResultEnum.FAIL_ADJUST_INVENTORY;
		}
		if(code.equals("-3")){
			return ResultEnum.FAIL_ADJUST_WATERFLOOD;
		}
		if(code.equals("-4")){
			return ResultEnum.SHORTAGE_STOCK_INVENTORY;
		}
		if(code.equals("-5")){
			return ResultEnum.INVALID_PARAM;
		}
		if(code.equals("-8")){
			return ResultEnum.INVALID_GOODSID;
		}
		if(code.equals("-9")){
			return ResultEnum.IS_EXISTED;
		}
		if(code.equals("-5")){
			return ResultEnum.INVALID_PARAM;
		}
		
		if(code.equals("-10")){
			return ResultEnum.INIT_INVENTORY_ERROR;
		}
		
		if(code.equals("-11")){
			return ResultEnum.INVALID_SUPPLIERSID;
		}
		if(code.equals("-12")){
			return ResultEnum.INVALID_SELECTIONID;
		}
		if(code.equals("-13")){
			return ResultEnum.INCORRECT_UPDATE;
		}
		if(code.equals("-14")){
			return ResultEnum.INVALID_TYPE;
		}
		if(code.equals("0")){
			return ResultEnum.NO_DATA;
		}
		if(code.equals("-99")){
			return ResultEnum.DB_ERROR;
		}
		if(code.equals("-100")){
			return ResultEnum.SYS_ERROR;
		}
		if(code.equals("1005")){
			return ResultEnum.INVALID_SELECTIONID;
		}
		if(code.equals("1006")){
			return ResultEnum.INVALID_SUPPLIERSID;
		}
		if(code.equals("1007")){
			return ResultEnum.INVALID_ISLIMIT_STORAGE;
		}
		if(code.equals("1008")){
			return ResultEnum.INVALID_ACK;
		}
		if(code.equals("1009")){
			return ResultEnum.NO_PARAMETER;
		}
		if(code.equals("1010")){
			return ResultEnum.INVALID_RETURN;
		}
		if(code.equals("1011")){
			return ResultEnum.INVALID_KEY;
		}
		if(code.equals("1012")){
			return ResultEnum.INVALID_ADJUST_ID;
		}
		if(code.equals("1013")){
			return ResultEnum.INVALID_INVENTORY_TYPE;
		}
		if(code.equals("1014")){
			return ResultEnum.INVALID_PERIOD;
		}
		if(code.equals("1015")){
			return ResultEnum.INVALID_NUM;
		}
		if(code.equals("1016")){
			return ResultEnum.INVALID_SELECTIONNUM;
		}
		if(code.equals("1017")){
			return ResultEnum.INVALID_SUPPLIERSNUM;
		}
		if(code.equals("1018")){
			return ResultEnum.SELECTION_GOODS;
		}
		if(code.equals("1019")){
			return ResultEnum.SUPPLIERS_GOODS;
		}
		if(code.equals("1020")){
			return ResultEnum.SEL_SUPP_GOODS;
		}
		if(code.equals("1021")){
			return ResultEnum.AFT_ADJUST_WATERFLOOD;
		}
		if(code.equals("1022")){
			return ResultEnum.INVALID_LEFTNUM_SELECTION;
		}
		if(code.equals("1023")){
			return ResultEnum.INVALID_TOTALNUM_SELECTION;
		}
		if(code.equals("1024")){
			return ResultEnum.INVALID_LEFTNUM_SUPPLIER;
		}
		if(code.equals("1025")){
			return ResultEnum.INVALID_TOTALNUM_SUPPLIER;
		}
		if(code.equals("1026")){
			return ResultEnum.INVALID_LEFTNUM_SELANDSUPP;
		}
		if(code.equals("1027")){
			return ResultEnum.INVALID_TOTALNUM_SELANDSUPP;
		}
		if(code.equals("1028")){
			return ResultEnum.NOTNULL_SEL_SUPP_GOODS;
		}
		if(code.equals("1029")){
			return ResultEnum.INVALID_SEL_LIMT;
		}
		if(code.equals("1030")){
			return ResultEnum.INVALID_SUPP_LIMT;
		}
		if(code.equals("1031")){
			return ResultEnum.QUERY_ERROR;
		}
		if(code.equals("1032")){
			return ResultEnum.INVALID_WMSGOODSID;
		}
		
		if(code.equals("1033")){
			return ResultEnum.INVALID_WMSID;
		}
		if(code.equals("1034")){
			return ResultEnum.INVALID_GOODSTYPEID;
		}
		if(code.equals("1035")){
			return ResultEnum.INVALID_SELIDANDGOODSTYPEID;
		}
		if(code.equals("1036")){
			return ResultEnum.SEL_OR_SUPP;
		}
		if(code.equals("1037")){
			return ResultEnum.AFT_ADJUST_INVENTORY;
		}
		if(code.equals("1038")){
			return ResultEnum.NONE_LIMIT_STORAGE;
		}
		if(code.equals("2000")){
			return ResultEnum.ERROR_2000;
		}
		if(code.equals("10999")){
			return ResultEnum.SYSTEM_ERROR;
		}
		
		if(code.equals("9999")){
			return ResultEnum.ERROR_UNKONW;
		}
		
		return ResultEnum.ERROR_UNKONW;
	}
	
	private ResultEnum(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
