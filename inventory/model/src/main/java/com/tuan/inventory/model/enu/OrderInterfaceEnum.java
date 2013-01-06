package com.tuan.inventory.model.enu;

/**
 * �ͻ������Ʊ�ʾö�٣�code��ʾΨһ��ʶ��description��ʾ������Ϣ
 * �ͻ���ֻ�д�����ȷ�ı�ʶ���ܷ�����Ӧ�Ľӿڣ����Ҳ��ֽӿ���Ҫ����
 * ��ʶ�ж��Ƿ�У�鸽�Ӳ��������ѯ����ʱ�������û�id��
 * @author wanghongwei
 *
 */
public enum OrderInterfaceEnum {
	/**���������ӿ�*/
	ORDER_CREATE("���������ӿ�"),
	/**�����޸Ľӿ�*/
	ORDER_UPDATE("�����޸Ľӿ�"),
	/**�����޸Ľӿ�-Ϊ��̨*/
	ORDER_UPDATE_FOR_ADMIN("�����޸Ľӿ�-Ϊ��̨"),
	/**����������ѯ*/
	SINGLE_ORDER_QUERY("����������ѯ"),
	/**�û������б��ѯ*/
	USER_ORDER_QUERY("�û������б��ѯ"),
	/**�û�����ͳ��*/
	USER_ORDER_STAT("�û�����ͳ��"),
	/**��Ʒ�����б��ѯ*/
	GOODS_ORDER_QUERY("��Ʒ�����б��ѯ"),
	/**����״̬�޸Ľӿ�*/
	ORDER_STATUS_UPDATE("����״̬�޸Ľӿ�"),
	/**����У��ӿ�*/
	ORDER_CHECK("����У��ӿ�"),
	/**����ȯȷ�Ͻӿ�*/
	TICKET_CONFIRM("����ȯȷ�Ͻӿ�"),
	/**��������ȯ��ѯ�ӿ�*/
	SINGLE_TICKET_QUERY("��������ȯ��ѯ�ӿ�"),
	/**�û�����ȯ�б��ѯ*/
	USER_TICKET_QUERY("�û�����ȯ�б��ѯ"),
	/**����ȯͳ�ƽӿ�*/
	USER_TICKET_STAT("����ȯͳ�ƽӿ�"),
	/**����������־�ӿ�*/
	CREATE_ORDER_ACTION("����������־�ӿ�"),
	/**��ѯ������־�ӿ�*/
	QUERY_ORDER_ACTION("��ѯ������־�ӿ�"),
	/**ȯ����ӿ�*/
	USER_TICKET_FREEZE("ȯ����ӿ�"),
	/**ȯ�ⶳ�ӿ�*/
	USER_TICKET_UNFREEZE("ȯ�ⶳ�ӿ�");

	private String description;
	
	private OrderInterfaceEnum(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
