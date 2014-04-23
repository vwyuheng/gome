package com.tuan.inventory.model.enu;

public enum ResultStatusEnum {
	
	//队列状态
	CONFIRM		("1",	"确认：有效可处理（CONFIRM）"),
	LOCKED		("3",	"锁定:初始状态（locked）"),
	ROLLBACK    ("5",   "回滚:将库存还原"),
	DELETE      ("7", "处理完毕:标记删除（deleted）"),
	
	
	//LOG		("9",	"日志"),
	
	GOODS_SELF      ("2", "商品总库存"),
	GOODS_SELECTION      ("4", "选型商品库存"),
	GOODS_SUPPLIERS      ("6", "分店商品库存"),
	
	//业务类型type
	INIT_INVENTORY   ("10",   "初始化库存"),
	DELETE_INVENTORY   ("11",   "删除库存"),
	ADJUST_WATERFLOOD   ("12",   "调整注水"),
	CALLBACK_CONFIRM   ("13",   "回调确认"),
	DEDUCTION_INVENTORY  ("14",   "库存扣减"),
	ADD_INVENTORY   ("15",   "新增库存"),
	REFUND_RESTORE_INVENTORY   ("16",   "商品退款还库存"),
	ADJUST_INVENTORY   ("17",   "手工调整库存"),
	FAULT_COMPENSATION_INVENTORY   ("18",   "出错补偿还库存"),
	
	//servlet接口
	SUCCESS		("0000",	"成功"),
	NO_PARAMETER		("1009",	"请求参数不能为空"),
	
	/*########################*/
	
	
	INVALID_IP			("1001",	"客服端IP无效"),
	INVALID_CLIENT		("1002",	"客户端名称无效"),
	INVALID_TIME		("1003",	"时间戳无效"),
	INVALID_GOODSID     ("1004",	"无效的商品id"),
	INVALID_SELECTIONID     ("1005",	"无效的商品选型id"),
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
	
	
	
	/*########################*/
	
	
	private String code;
	private String description;

	public static ResultStatusEnum getResultStatusEnum(String code){
		if(code == null || code.isEmpty()){
			return ResultStatusEnum.ERROR_UNKONW;
		}
		if(code.equals("0000")){
			return ResultStatusEnum.SUCCESS;
		}
		if(code.equals("1001")){
			return ResultStatusEnum.INVALID_IP;
		}
		if(code.equals("1002")){
			return ResultStatusEnum.INVALID_CLIENT;
		}
		if(code.equals("1003")){
			return ResultStatusEnum.INVALID_TIME;
		}
		if(code.equals("1004")){
			return ResultStatusEnum.INVALID_GOODSID;
		}
		if(code.equals("1005")){
			return ResultStatusEnum.INVALID_SELECTIONID;
		}
		if(code.equals("1010")){
			return ResultStatusEnum.INVALID_RETURN;
		}
		if(code.equals("2000")){
			return ResultStatusEnum.ERROR_2000;
		}
		if(code.equals("10999")){
			return ResultStatusEnum.SYSTEM_ERROR;
		}
		if(code.equals("9999")){
			return ResultStatusEnum.ERROR_UNKONW;
		}
		
		return ResultStatusEnum.ERROR_UNKONW;
	}
	
	private ResultStatusEnum(String code, String description) {
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
	
	public static void main(String[] args) {
		
	}
}
