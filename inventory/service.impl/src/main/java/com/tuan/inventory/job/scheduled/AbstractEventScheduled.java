package com.tuan.inventory.job.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/***
 * �����̳߳صĻ���
 * @author henry.yu
 * @date 2014/3/28
 */
public abstract class AbstractEventScheduled {

	/**
	 * �����̳߳أ����ڳ�ʼ����־�¼�����
	 */
	protected ScheduledExecutorService scheduledExecutorService;
	/**
	 * ����cpu���߳���
	 */
	protected final int POOL_SIZE = 5;

	/** �����ȴ�ʱ�� */
	protected long waitTime = 200;
	
	/**
	 * �������������Ŀͻ���
	 */
	public AbstractEventScheduled() {
		// ��ʼ��ScheduledExecutorService ����
		this.scheduledExecutorService = Executors
				.newScheduledThreadPool(Runtime.getRuntime()
						.availableProcessors() * POOL_SIZE);
	}
}
