package com.tuan.inventory.model.util;

/**
 * �������ƶ���
 * @author henry.yu
 * @date 2014/3/12
 */
public class QueueConstant {
	
	public final static String QUEUE_KEY_MEMBER="queue:key.member";//������е� key member��Ϣ
	public final static String QUEUE_SEND_MESSAGE="queue:jobs.send";//�첽��������notifyserver�����Ϣ�Ķ���
	public final static String QUEUE_LOGS_MESSAGE="queue:jobs.logs";//�첽������¼��־��ˮ�Ķ�������
	
	//�����redis�д洢���ͣ�����key��ǰ׺��ʾ
	public final static String GOODS_INVENTORY_PREFIX="hash_goods.inventory";//��Ʒ�ܿ��ǰ׺
	public final static String GOODS_SELECTION_RELATIONSHIP_PREFIX="set_goods_selection.relation";//��Ʒid��ѡ��id������ϵǰ׺
	public final static String GOODS_SUPPLIERS_RELATIONSHIP_PREFIX="set_goods_suppliers.relation";//��Ʒid��ֵ�id������ϵǰ׺
	public final static String SELECTION_INVENTORY_PREFIX="hash_selection.inventory";//ѡ����Ʒ���ǰ׺
	public final static String SUPPLIERS_INVENTORY_PREFIX="hash_suppliers.inventory";//�ֵ���Ʒ���ǰ׺
	//����״̬
	//public final static String ACTIVE = "1";  // 1:��������Ч�ɴ���active��
	//public final static String LOCKED = "3";  //3����ʼ״̬��locked��
	//public final static String EXCEPTION = "5"; //5����ǿ��Ա������쳣���д�(exception)
	
	//��Ʒ�������
/*	public final static String SELECTION = "ѡ����Ʒ���";  // ��ʶ��ѡ�͡��������ֵ�
	public final static String SUPPLIERS = "�ֵ���Ʒ���";  
	public final static String GOODS = "��Ʒ�ܿ��"; */
	//���ۼ����ز��������ɵĶ�������idֵ��key���ƶ���
	public final static String QUEUE_KEY_ID="queue:key.id";//�첽������¼��־��ˮ�Ķ�������
	
	
	/** ���н��״̬ status */
	/*public static final int QUEUE_STATUS_NOT_CONSUMER = 1;//δ����
	public static final int QUEUE_STATUS_CONSUMER_SUCCESS = 2;//���ѳɹ�(�������Ѵ����Ѿ����꣬Ҳ��ǳɹ������Ƕ��еİ�״̬��һ��)
	public static final int QUEUE_STATUS_CONSUMER_FAILED = 3;//����ʧ��

	public static final int QUEUE_MAX_CONSUME_COUNT = 8;//������Ѵ���
*/	
	
	
	/** ϵͳ�쳣���� */
	public static final int NO_DATA = 0;// û�п��õ�����Դ
	public static final int SUCCESS = 1;// ��־�����ɹ�
	
	public static final int DATA_EXISTED = 2;// �����Ѿ�����
	
	public static final int INVALID_PARAM = -1;// �����������
	public static final int INVALID_GOODSID = -2;// ��Ʒid��Ч
	
	public static final int INVALID_SELECTIONID = -3;// ѡ��id��Ч
	public static final int INVALID_SUPPLIERSID = -4;// �ֵ�id��Ч
	
	
	public static final int UNKNOW_ERROR = -9;// δ֪����
	public static final int SERVICE_DATABASE_FALIURE = -99;// ���ݿ��쳣
	public static final int SERVICE_SYSTEM_FALIURE = -100;// ϵͳ�����쳣
	
	public static final int INVALID_IP = 5000;// �ͷ���IP��Ч
	public static final int INVALID_CLIENT = 5001;// �ͻ���������Ч
	public static final int INVALID_TIME = 5002;// ʱ�����Ч
	public static final int INVALID_RETURN = 5003;// ����ֵ����ȷ
	
		
	
	
}
