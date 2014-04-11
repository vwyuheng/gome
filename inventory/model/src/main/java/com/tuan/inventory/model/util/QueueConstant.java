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
	public static final int QUEUE_STATUS_NOT_CONSUMER = 1;//δ����
	public static final int QUEUE_STATUS_CONSUMER_SUCCESS = 2;//���ѳɹ�(�������Ѵ����Ѿ����꣬Ҳ��ǳɹ������Ƕ��еİ�״̬��һ��)
	public static final int QUEUE_STATUS_CONSUMER_FAILED = 3;//����ʧ��

	public static final int QUEUE_MAX_CONSUME_COUNT = 8;//������Ѵ���
	
	
	
	/** ϵͳ�쳣���� */
	public static final int SUCCESS = 0xFF0001;// ��־�����ɹ�
	public static final int SERVICE_SYSTEM_FALIURE = 0xFF0002;// ϵͳ�����쳣
	public static final int SERVICE_DATABASE_FALIURE = 0xFF0003;// ���ݿ��쳣
	public static final int NO_ALIVE_DATASOURCE = 0xFF0004;// û�п��õ�����Դ
	public static final int NOT_SUPPORT = 0xFF0005;// ��ǰ��֧��
	public static final int PARAMS_INVALID = 0xFF0006;// ������Ч
	public static final int NO_DATA = 0xFF0007;// û����־����
	public static final int NOT_SAFE_IPADDRESS = 0xFF0008;// ���ڰ�ȫip�б�����
	public static final int OVER_TOP_VALUE = 0xFF0009;// ��������������ֵ,һ����ȡ����ֵ̫��
	public static final int DATA_EXISTED = 0xFF0010;// �����Ѿ�����
	public static final int UNKNOW_ERROR = 0xFF0012;// δ֪����
}
