package com.tuan.inventory.job.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/***
 * 调度线程池的基类
 * @author henry.yu
 * @date 2014/3/28
 */
public abstract class AbstractEventScheduled {

	/**
	 * 调度线程池，用于初始化日志事件数据
	 */
	protected ScheduledExecutorService scheduledExecutorService;
	/**
	 * 单个cpu的线程数
	 */
	protected final int POOL_SIZE = 5;

	/** 监听等待时间 */
	protected long waitTime = 200;
	
	/**
	 * 构造带不带缓存的客户端
	 */
	public AbstractEventScheduled() {
		// 初始化ScheduledExecutorService 服务
		this.scheduledExecutorService = Executors
				.newScheduledThreadPool(Runtime.getRuntime()
						.availableProcessors() * POOL_SIZE);
	}
}
