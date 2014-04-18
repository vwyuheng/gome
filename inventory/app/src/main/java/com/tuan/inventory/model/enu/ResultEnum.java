package com.tuan.inventory.model.enu;


public enum ResultEnum {
	//SUCCESS		("0000",	"成功"),
	SUCCESS	("success",	"成功"),
	ERROR	("error",	"失败"),
	INVALID_IP			("1001",	"客服端IP无效"),
	INVALID_CLIENT		("1002",	"客户端名称无效"),
	INVALID_TIME		("1003",	"时间戳无效"),
	INVALID_GOODSID     ("1004",	"无效的商品id"),
	INVALID_SELECTIONID     ("1005",	"无效的商品选型id"),
	INVALID_SUPPLIERSID     ("1006",	"无效的商品选型id"),
	INVALID_ISLIMIT_STORAGE     ("1007",	"是否限制库存无效"),      
	INVALID_ACK     ("1008",	"无效的确认标识"),
	INVALID_KEY     ("1011",	"无效的key"),
	
	NO_PARAMETER		("1009",	"请求参数不能为空"),
	INVALID_RETURN      ("1010",	"返回值不正确"),
	
	INVALID_ADJUST_ID		("1012",	"无效的库存调整id"),
	INVALID_INVENTORY_TYPE		("1013",	"无效的库存类型"),
	INVALID_PERIOD     ("1014",	"无效的时间间隔"),
	
	
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
		if(code.equals("1004")){
			return ResultEnum.INVALID_GOODSID;
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
