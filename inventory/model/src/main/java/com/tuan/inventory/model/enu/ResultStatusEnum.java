package com.tuan.inventory.model.enu;

public enum ResultStatusEnum {
	
	//����״̬
	CONFIRM		("1",	"ȷ�ϣ���Ч�ɴ���CONFIRM��"),
	LOCKED		("3",	"����:��ʼ״̬��locked��"),
	ROLLBACK    ("5",   "�ع�:����滹ԭ"),
	DELETE      ("7", "�������:���ɾ����deleted��"),
	
	
	//LOG		("9",	"��־"),
	
	GOODS_SELF      ("2", "��Ʒ�ܿ��"),
	GOODS_SELECTION      ("4", "ѡ����Ʒ���"),
	GOODS_SUPPLIERS      ("6", "�ֵ���Ʒ���"),
	
	//ҵ������type
	DELETE_INVENTORY   ("11",   "ɾ�����"),
	ADJUST_WATERFLOOD   ("12",   "����עˮ"),
	CALLBACK_CONFIRM   ("13",   "�ص�ȷ��"),
	DEDUCTION_INVENTORY  ("14",   "���ۼ�"),
	ADD_INVENTORY   ("15",   "�������"),
	REFUND_RESTORE_INVENTORY   ("16",   "��Ʒ�˿���"),
	ADJUST_INVENTORY   ("17",   "�ֹ��������"),
	FAULT_COMPENSATION_INVENTORY   ("18",   "�����������"),
	
	//servlet�ӿ�
	SUCCESS		("0000",	"�ɹ�"),
	NO_PARAMETER		("1009",	"�����������Ϊ��"),
	
	/*########################*/
	
	
	INVALID_IP			("1001",	"�ͷ���IP��Ч"),
	INVALID_CLIENT		("1002",	"�ͻ���������Ч"),
	INVALID_TIME		("1003",	"ʱ�����Ч"),
	INVALID_GOODSID     ("1004",	"��Ч����Ʒid"),
	INVALID_SELECTIONID     ("1005",	"��Ч����Ʒѡ��id"),
	INVALID_RETURN      ("1010",	"����ֵ����ȷ"),
	
	INVALID_SHOP_NO		("100011",	"��Ч���ŵ��"),
	INVALID_TERM_NO		("100012",	"��Ч���ն˺�"),
	INVALID_PART_CARD_NO("100013",	"��Ч�Ĳ��ֿ���"),
	INVALID_AMOUNT		("100014",	"��Ч�����ѽ��"),
	INVALID_COUPON_NO	("100015",	"��Ч��ȯ��"),
	INVALID_EVENT_NO	("100016",	"��Ч�Ļ��"),
	INVALID_EVENT_TITLE	("100017",	"��Ч�Ļ����"),
	INVALID_EVENT_DESC	("100018",	"��Ч�Ļ����"),
	INVALID_BEGIN_DATE	("100019",	"��Ч�Ŀ�ʼʱ��"),
	INVALID_END_DATE	("100020",	"��Ч�Ľ���ʱ��"),
	INVALID_EVENT_RULE	("100021",	"��Ч�Ļ����"),
	INVALID_RULE_DESC	("100022",	"��Ч�Ĺ�������"),
	INVALID_SPEC_BANK_FLAG("100023",	"��Ч����������"),
	INVALID_EVENT_STATUS("100024",	"��Ч�Ļ״̬"),
	INVALID_EVENT_LINK	("100025",	"��Ч�Ļ����"),
	INVALID_START_PAGE	("100026",	"��Ч����ʼҳ"),
	
	ERROR_2000	("2000",	"��������ʱ����"),
	NET_ERROR			("10998",	"�����쳣"),
	SYSTEM_ERROR		("10999",	"ϵͳ����"),
	ERROR_UNKONW       ("9999",	"����");
	
	
	
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
