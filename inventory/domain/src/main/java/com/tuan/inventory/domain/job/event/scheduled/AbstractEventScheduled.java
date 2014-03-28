package com.tuan.inventory.domain.job.event.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractEventScheduled {

	/**
	 * �����̳߳أ����ڳ�ʼ����־�¼�����
	 */
	protected ScheduledExecutorService scheduledExecutorService;
	/**
	 * ����cpu���߳���
	 */
	protected final int POOL_SIZE = 5;

	/** ���м����ȴ�ʱ�� */
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
