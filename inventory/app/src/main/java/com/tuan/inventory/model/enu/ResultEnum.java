package com.tuan.inventory.model.enu;

public enum ResultEnum {
	SUCCESS		("0000",	"成功"),
	
	INVALID_IP			("1001",	"客服端IP无效"),
	INVALID_CLIENT		("1002",	"客户端名称无效"),
	INVALID_TIME		("1003",	"时间戳无效"),
	INVALID_GOODSID     ("1004",	"无效的商品id"),
	INVALID_SELECTIONID     ("1005",	"无效的商品选型id"),
	INVALID_SUPPLIERSID     ("1006",	"无效的商品选型id"),
	NO_PARAMETER		("1009",	"请求参数不能为空"),
	INVALID_RETURN      ("1010",	"返回值不正确"),
	INVALID_SHOP_NO		("100011",	"无效的门店号"),
	INVALID_TERM_NO		("100012",	"无效的终端号"),
	INVALID_PART_CARD_NO("100013",	"无效的部分卡号"),
	INVALID_AMOUNT		("100014",	"无效的消费金额"),
	INVALID_COUPON_NO	("100015",	"无效的券号"),
	INVALID_EVENT_NO	("100016",	"无效的活动号"),
	INVALID_EVENT_TITLE	("100017",	"无效的活动主题"),
	INVALID_EVENT_DESC	("100018",	"无效的活动介绍"),
	INVALID_BEGIN_DATE	("100019",	"无效的开始时间"),
	INVALID_END_DATE	("100020",	"无效的结束时间"),
	INVALID_EVENT_RULE	("100021",	"无效的活动规则"),
	INVALID_RULE_DESC	("100022",	"无效的规则描述"),
	INVALID_SPEC_BANK_FLAG("100023",	"无效的限制银行"),
	INVALID_EVENT_STATUS("100024",	"无效的活动状态"),
	INVALID_EVENT_LINK	("100025",	"无效的活动链接"),
	INVALID_START_PAGE	("100026",	"无效的起始页"),
	
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
		if(code.equals("0000")){
			return ResultEnum.SUCCESS;
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
		if(code.equals("1010")){
			return ResultEnum.INVALID_RETURN;
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
