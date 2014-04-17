package com.tuan.inventory.model.enu;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;


/**
 * 客户端名称表示枚举，code表示唯一标识，description表示描述信息
 * 客户端只有传入正确的标识才能访问相应的接口，并且部分接口需要根据
 * 标识判断是否校验附加参数（如查询订单时，传入用户id）
 * @author wanghongwei
 *
 */
public enum ClientNameEnum {
	/**用户中心*/
	USER_CENTER("USER_CENTER", "用户中心", (byte)1,
			EnumSet.of(
		    		  OrderInterfaceEnum.ORDER_CREATE,
		    		  OrderInterfaceEnum.ORDER_UPDATE,
		    		  OrderInterfaceEnum.SINGLE_ORDER_QUERY,
		    		  OrderInterfaceEnum.USER_ORDER_QUERY,
		    		  OrderInterfaceEnum.USER_ORDER_STAT,
		    		  OrderInterfaceEnum.ORDER_STATUS_UPDATE,
		    		  OrderInterfaceEnum.ORDER_CHECK,
		    		  OrderInterfaceEnum.SINGLE_TICKET_QUERY,
		    		  OrderInterfaceEnum.USER_TICKET_QUERY,
		    		  OrderInterfaceEnum.USER_TICKET_STAT),
		    new HashSet<String>(Arrays.asList(
		    		"TicketUpdateService.ticketResendToNewMobile",
		    		"TicketUpdateService.ticketConsumeRecordAdd",
		    		"TicketUpdateService.ticketConsumeRecordUpdate",
		    		"ImagecoTicketService.reSend",
		    		"OrderQueryService.querySelectionRelation",		    		
		    		"ImagecoTicketService.queryTicketExtend",
		    		"TicketUpdateService.ticketResend"))) {
		boolean checkUserId(int userId) {
			return (userId > 0 ? true : false);
		}
		boolean checkKey(String key) {
			return ((key != null && key.length() == 16) ? true : false);
		}
	},
	/**支付中心*/
	PAY_CENTER("PAY_CENTER", "支付中心", (byte)2,
			EnumSet.of(
		    		  OrderInterfaceEnum.SINGLE_ORDER_QUERY,
		    		  OrderInterfaceEnum.ORDER_STATUS_UPDATE,
		    		  OrderInterfaceEnum.ORDER_CHECK,
		    		  OrderInterfaceEnum.ORDER_UPDATE),
		    new HashSet<String>(Arrays.asList(
		    		""))) {
		boolean checkUserId(int userId) {
			return (userId > 0 ? true : false);
		}
		boolean checkKey(String key) {
			return ((key != null && key.length() == 16) ? true : false);
		}
	},
	/**客服中心*/
	SERVICE_CENTER("SERVICE_CENTER", "客服中心", (byte)3,
			EnumSet.of(
		    		  OrderInterfaceEnum.SINGLE_ORDER_QUERY,
		    		  OrderInterfaceEnum.ORDER_STATUS_UPDATE,
		    		  OrderInterfaceEnum.SINGLE_TICKET_QUERY,
		    		  OrderInterfaceEnum.ORDER_UPDATE_FOR_ADMIN,
		    		  OrderInterfaceEnum.GOODS_ORDER_QUERY),
		    new HashSet<String>(Arrays.asList(
		    		"TicketUpdateService.eubRefundUpdate",
		    		"TicketUpdateService.wowoRefundUpdate",
		    		"TicketUpdateService.setIsDelete",
		    		"TicketUpdateService.ticketResendToNewMobile",
		    		"TicketUpdateService.ticketResend",
		    		"ImagecoTicketService.reSend",
		    		"VendorTicketService.vendorTicketImport",
		    		"VendorTicketService.vendorTicketStat",
		    		"VendorTicketService.queryVendorTicketList",
		    		"TicketUpdateService.freezeTickets",
		    		"TicketUpdateService.unfreezeTickets",
		    		"ImagecoTicketService.refundUpdate",
		    		"ImagecoTicketService.refundUpdateDecode",
		    		"OrderActionQueryService.queryOrderAction",
		    		"OrderActionQueryService.queryOrderActionPopular"))) {
		boolean checkUserId(int userId) {
			return true;
		}
		boolean checkKey(String key) {
			return true;
		}
	},
	/**翼码客服中心*/
	SUPPLIER_CENTER_IMAGECO("SUPPLIER_CENTER_IMAGECO", "客服中心", (byte)3,
			EnumSet.of(
					OrderInterfaceEnum.SINGLE_ORDER_QUERY,
					OrderInterfaceEnum.ORDER_STATUS_UPDATE,
					OrderInterfaceEnum.SINGLE_TICKET_QUERY,
					OrderInterfaceEnum.ORDER_UPDATE_FOR_ADMIN,
					OrderInterfaceEnum.GOODS_ORDER_QUERY),
					new HashSet<String>(Arrays.asList(
							"ImagecoTicketService.reSend",
							"ImagecoTicketService.refundUpdate",
							"ImagecoTicketService.refundUpdateDecode"))) {
		boolean checkUserId(int userId) {
			return true;
		}
		boolean checkKey(String key) {
			return true;
		}
	},
	/**物流中心*/
	LOGISTIC_CENTER("LOGISTIC_CENTER", "物流中心", (byte)4,
			EnumSet.of(
		    		  OrderInterfaceEnum.ORDER_STATUS_UPDATE),
		    new HashSet<String>(Arrays.asList(
		    		""))) {
		boolean checkUserId(int userId) {
			return true;
		}
		boolean checkKey(String key) {
			return true;
		}
	},
	/**商户中心*/
	SUPPLIER_CENTER("SUPPLIER_CENTER", "商户中心", (byte)5,
			EnumSet.of(
		    		  OrderInterfaceEnum.TICKET_CONFIRM,
		    		  OrderInterfaceEnum.GOODS_ORDER_QUERY),
		    new HashSet<String>(Arrays.asList(
		    		"TicketUpdateService.ticketConfirm",
		    		"ImagecoTicketService.querySyncConsume",
		    		"TicketUpdateService.eubTicketUseReceive",
		    		"ImagecoTicketService.syncConsume",
		    		"VendorTicketService.vendorTicketImport",
		    		"VendorTicketService.vendorTicketStat",
		    		"VendorTicketService.queryVendorTicketList"))) {
		boolean checkUserId(int userId) {
			return true;
		}
		boolean checkKey(String key) {
			return true;
		}
	},
	/**线下屏*/
	OUTLINE_SCREEN("OUTLINE_SCREEN", "线下屏", (byte)6,
			EnumSet.of(
					OrderInterfaceEnum.ORDER_CREATE,
		    		  OrderInterfaceEnum.ORDER_UPDATE,
		    		  OrderInterfaceEnum.SINGLE_ORDER_QUERY,
		    		  OrderInterfaceEnum.USER_ORDER_QUERY,
		    		  OrderInterfaceEnum.USER_ORDER_STAT,
		    		  OrderInterfaceEnum.ORDER_STATUS_UPDATE,
		    		  OrderInterfaceEnum.ORDER_CHECK,
		    		  OrderInterfaceEnum.SINGLE_TICKET_QUERY,
		    		  OrderInterfaceEnum.USER_TICKET_QUERY,
		    		  OrderInterfaceEnum.USER_TICKET_STAT),
		    new HashSet<String>(Arrays.asList(
		    		""))) {
		boolean checkUserId(int userId) {
			return true;
		}
		boolean checkKey(String key) {
			return true;
		}
	},
	/**手机客户端*/
	APPAGENT_SYSTEM("APPAGENT_SYSTEM", "手机客户端", (byte)7,
			EnumSet.of(
					  OrderInterfaceEnum.ORDER_CREATE,
		    		  OrderInterfaceEnum.ORDER_UPDATE,
		    		  OrderInterfaceEnum.SINGLE_ORDER_QUERY,
		    		  OrderInterfaceEnum.USER_ORDER_QUERY,
		    		  OrderInterfaceEnum.USER_ORDER_STAT,
		    		  OrderInterfaceEnum.ORDER_STATUS_UPDATE,
		    		  OrderInterfaceEnum.ORDER_CHECK,
		    		  OrderInterfaceEnum.SINGLE_TICKET_QUERY,
		    		  OrderInterfaceEnum.USER_TICKET_QUERY,
		    		  OrderInterfaceEnum.USER_TICKET_STAT),
		    new HashSet<String>(Arrays.asList(
		    		""))) {
		boolean checkUserId(int userId) {
			return true;
		}
		boolean checkKey(String key) {
			return true;
		}
	},
	/**后台系统*/
	INNER_SYSTEM("INNER_SYSTEM", "后台系统", (byte)11,
			EnumSet.of(
					OrderInterfaceEnum.CREATE_ORDER_ACTION,
					OrderInterfaceEnum.QUERY_ORDER_ACTION
			),
		    new HashSet<String>(Arrays.asList(
		    		""))) {
		boolean checkUserId(int userId) {
			return true;
		}
		boolean checkKey(String key) {
			return true;
		}
	},
	/**订单中心web系统*/
	FRONT_ORDER_WEB("FRONT_ORDER_WEB", "订单WEB系统", (byte)12,
			EnumSet.of(
					OrderInterfaceEnum.CREATE_ORDER_ACTION,
					OrderInterfaceEnum.QUERY_ORDER_ACTION
			),
		    new HashSet<String>(Arrays.asList(
		    		"ImagecoTicketService.deliverSync",
		    		"ImagecoTicketService.syncConsume"))) {
		boolean checkUserId(int userId) {
			return true;
		}
		boolean checkKey(String key) {
			return true;
		}
	};
	//用户id有效性校验
	abstract boolean checkUserId(int userId);
	//保密码有效性校验
	abstract boolean checkKey(String key);

	private String code;
	private String description;
	private byte enumId;
	private EnumSet<OrderInterfaceEnum> interfaceEnumSet;
	private Set<String> interfaceMethodSet;
	
	private ClientNameEnum(String code, String description, byte enumId,
			EnumSet<OrderInterfaceEnum> interfaceEnumSet, Set<String> interfaceMethodSet) {
		this.code = code;
		this.description = description;
		this.enumId = enumId;
		this.interfaceEnumSet = interfaceEnumSet;
		this.interfaceMethodSet = interfaceMethodSet;
	}
	
	/**
	 * 检查该客户端是否可调用指定的接口
	 * @param interfaceEnum OrderInterfaceEnum 目标接口枚举
	 * @return 如果目标枚举在集合中，返回true；如果目标枚举不在集合中，返回false
	 */
	public boolean isCanRequest(OrderInterfaceEnum interfaceEnum) {
		return interfaceEnumSet.contains(interfaceEnum);
	}
	
	/**
	 * 检查该客户端是否可调用指定的接口
	 * @param interfaceMethodName String 目标接口与方法名
	 * @return 如果目标在集合中，返回true；如果目标不在集合中，返回false
	 */
	public boolean isCanRequest(String interfaceMethodName) {
		return interfaceMethodSet.contains(interfaceMethodName);
	}

	/**
	 * 根据状态值获取对应的枚举值，如果不在枚举值范围内，返回null
	 * @param code int 状态值
	 * @return OrderInfoOrderStatusEnum 枚举值，如果不存在返回null
	 */
	public static ClientNameEnum valueOfEnum(String code) {
		if (code == null || code.length() <= 0) {
			return null;
		}
		if (code.equalsIgnoreCase("USER_CENTER")) {
			return USER_CENTER;
		} else if (code.equalsIgnoreCase("PAY_CENTER")) {
			return PAY_CENTER;
		} else if (code.equalsIgnoreCase("SERVICE_CENTER")) {
			return SERVICE_CENTER;
		} else if (code.equalsIgnoreCase("LOGISTIC_CENTER")) {
			return LOGISTIC_CENTER;
		} else if (code.equalsIgnoreCase("SUPPLIER_CENTER")) {
			return SUPPLIER_CENTER;
		} else if (code.equalsIgnoreCase("OUTLINE_SCREEN")) {
			return OUTLINE_SCREEN;
		} else if (code.equalsIgnoreCase("APPAGENT_SYSTEM")) {
			return APPAGENT_SYSTEM;
		} else if (code.equalsIgnoreCase("INNER_SYSTEM")) {
			return INNER_SYSTEM;
		} else if (code.equalsIgnoreCase("SUPPLIER_CENTER_IMAGECO")) {
			return SUPPLIER_CENTER_IMAGECO;
		}else if (code.equalsIgnoreCase("FRONT_ORDER_WEB")) {
			return FRONT_ORDER_WEB;
		} else {
			return null;
		}
	}

	/**
	 * 根据堆轨迹数组和Service 类全名（包括包名）获取调用方法的名称字符串
	 * 		interfaceName.methodName
	 * @param ste final StackTraceElement[] 调用轨迹数组
	 * @param fullServiceName String 类全名（包括包名）
	 * @return String 接口和方法名，如果参数无效返回null
	 */
	public static String getCurrentMethodName(
			final StackTraceElement[] ste, final String fullServiceName) {
		if (ste == null || ste.length < 2) {
			return null;
		}
		if (fullServiceName == null) {
			return null;
		}
		StringBuilder strBuilder = new StringBuilder("");
		int index = fullServiceName.lastIndexOf('.');
		if (index >= 0) {
			strBuilder.append(fullServiceName.substring(index + 1));
		} else {
			strBuilder.append(fullServiceName);
		}
		strBuilder.append(".");
		strBuilder.append(ste[1].getMethodName());
		return strBuilder.toString();
	}

	public String getValue() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
	public byte getEnumId() {
		return enumId;
	}
}
