package com.tuan.inventory.model.enu;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;


/**
 * �ͻ������Ʊ�ʾö�٣�code��ʾΨһ��ʶ��description��ʾ������Ϣ
 * �ͻ���ֻ�д�����ȷ�ı�ʶ���ܷ�����Ӧ�Ľӿڣ����Ҳ��ֽӿ���Ҫ����
 * ��ʶ�ж��Ƿ�У�鸽�Ӳ��������ѯ����ʱ�������û�id��
 * @author wanghongwei
 *
 */
public enum ClientNameEnum {
	/**�û�����*/
	USER_CENTER("USER_CENTER", "�û�����", (byte)1,
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
	/**֧������*/
	PAY_CENTER("PAY_CENTER", "֧������", (byte)2,
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
	/**�ͷ�����*/
	SERVICE_CENTER("SERVICE_CENTER", "�ͷ�����", (byte)3,
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
	/**����ͷ�����*/
	SUPPLIER_CENTER_IMAGECO("SUPPLIER_CENTER_IMAGECO", "�ͷ�����", (byte)3,
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
	/**��������*/
	LOGISTIC_CENTER("LOGISTIC_CENTER", "��������", (byte)4,
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
	/**�̻�����*/
	SUPPLIER_CENTER("SUPPLIER_CENTER", "�̻�����", (byte)5,
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
	/**������*/
	OUTLINE_SCREEN("OUTLINE_SCREEN", "������", (byte)6,
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
	/**�ֻ��ͻ���*/
	APPAGENT_SYSTEM("APPAGENT_SYSTEM", "�ֻ��ͻ���", (byte)7,
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
	/**��̨ϵͳ*/
	INNER_SYSTEM("INNER_SYSTEM", "��̨ϵͳ", (byte)11,
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
	/**��������webϵͳ*/
	FRONT_ORDER_WEB("FRONT_ORDER_WEB", "����WEBϵͳ", (byte)12,
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
	//�û�id��Ч��У��
	abstract boolean checkUserId(int userId);
	//��������Ч��У��
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
	 * ���ÿͻ����Ƿ�ɵ���ָ���Ľӿ�
	 * @param interfaceEnum OrderInterfaceEnum Ŀ��ӿ�ö��
	 * @return ���Ŀ��ö���ڼ����У�����true�����Ŀ��ö�ٲ��ڼ����У�����false
	 */
	public boolean isCanRequest(OrderInterfaceEnum interfaceEnum) {
		return interfaceEnumSet.contains(interfaceEnum);
	}
	
	/**
	 * ���ÿͻ����Ƿ�ɵ���ָ���Ľӿ�
	 * @param interfaceMethodName String Ŀ��ӿ��뷽����
	 * @return ���Ŀ���ڼ����У�����true�����Ŀ�겻�ڼ����У�����false
	 */
	public boolean isCanRequest(String interfaceMethodName) {
		return interfaceMethodSet.contains(interfaceMethodName);
	}

	/**
	 * ����״ֵ̬��ȡ��Ӧ��ö��ֵ���������ö��ֵ��Χ�ڣ�����null
	 * @param code int ״ֵ̬
	 * @return OrderInfoOrderStatusEnum ö��ֵ����������ڷ���null
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
	 * ���ݶѹ켣�����Service ��ȫ����������������ȡ���÷����������ַ���
	 * 		interfaceName.methodName
	 * @param ste final StackTraceElement[] ���ù켣����
	 * @param fullServiceName String ��ȫ��������������
	 * @return String �ӿںͷ����������������Ч����null
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
