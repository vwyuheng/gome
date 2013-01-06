package com.tuan.inventory.model.enu;

/**
 * 客户端名称表示枚举，code表示唯一标识，description表示描述信息
 * 客户端只有传入正确的标识才能访问相应的接口，并且部分接口需要根据
 * 标识判断是否校验附加参数（如查询订单时，传入用户id）
 * @author wanghongwei
 *
 */
public enum OrderInterfaceEnum {
	/**订单创建接口*/
	ORDER_CREATE("订单创建接口"),
	/**订单修改接口*/
	ORDER_UPDATE("订单修改接口"),
	/**订单修改接口-为后台*/
	ORDER_UPDATE_FOR_ADMIN("订单修改接口-为后台"),
	/**单个订单查询*/
	SINGLE_ORDER_QUERY("单个订单查询"),
	/**用户订单列表查询*/
	USER_ORDER_QUERY("用户订单列表查询"),
	/**用户订单统计*/
	USER_ORDER_STAT("用户订单统计"),
	/**商品订单列表查询*/
	GOODS_ORDER_QUERY("商品订单列表查询"),
	/**订单状态修改接口*/
	ORDER_STATUS_UPDATE("订单状态修改接口"),
	/**订单校验接口*/
	ORDER_CHECK("订单校验接口"),
	/**消费券确认接口*/
	TICKET_CONFIRM("消费券确认接口"),
	/**单个消费券查询接口*/
	SINGLE_TICKET_QUERY("单个消费券查询接口"),
	/**用户消费券列表查询*/
	USER_TICKET_QUERY("用户消费券列表查询"),
	/**消费券统计接口*/
	USER_TICKET_STAT("消费券统计接口"),
	/**创建订单日志接口*/
	CREATE_ORDER_ACTION("创建订单日志接口"),
	/**查询订单日志接口*/
	QUERY_ORDER_ACTION("查询订单日志接口"),
	/**券冻结接口*/
	USER_TICKET_FREEZE("券冻结接口"),
	/**券解冻接口*/
	USER_TICKET_UNFREEZE("券解冻接口");

	private String description;
	
	private OrderInterfaceEnum(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
