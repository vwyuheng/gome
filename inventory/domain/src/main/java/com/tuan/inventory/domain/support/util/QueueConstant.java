package com.tuan.inventory.domain.support.util;

/**
 * �������ƶ���
 * @author henry.yu
 * @date 2014/3/12
 */
public class QueueConstant {
	
	public final static String QUEUE_KEY_MEMBER="queue:key.member";//������е� key member��Ϣ
	public final static String QUEUE_SEND_MESSAGE="queue:jobs.send";//�첽��������notifyserver�����Ϣ�Ķ���
	public final static String QUEUE_LOGS_MESSAGE="queue:jobs.logs";//�첽������¼��־��ˮ�Ķ�������
	//����״̬
	//public final static String ACTIVE = "1";  // 1:��������Ч�ɴ���active��
	//public final static String LOCKED = "3";  //3����ʼ״̬��locked��
	//public final static String EXCEPTION = "5"; //5����ǿ��Ա������쳣���д�(exception)
	
	//��Ʒ�������
	public final static String SELECTION = "ѡ����Ʒ���";  // ��ʶ��ѡ�͡��������ֵ�
	public final static String SUBBRANCH = "�ֵ���Ʒ���";  
	public final static String GOODS = "��Ʒ�ܿ��"; 
	//���ۼ����ز��������ɵĶ�������idֵ��key���ƶ���
	public final static String QUEUE_KEY_ID="queue:key.id";//�첽������¼��־��ˮ�Ķ�������
}
