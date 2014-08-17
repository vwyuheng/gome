package com.tuan.inventory.job;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.inventory.domain.InventoryConfirmScheduledDomain;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.job.util.AbstractJobRunnable;
import com.tuan.job.util.ExecutorManager;

public class ConfirmQueueConsumeJob extends AbstractJobRunnable {
	private static Log logJob=LogFactory.getLog("CONFIRM.JOB.LOG");
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	@Resource
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;	
	@Resource
	private DLockImpl dLock;//分布式锁
	
	@Override
	public void run() {
	
		long startTime = System.currentTimeMillis();
		String method = "ConfirmQueueConsumeJob.run";
		final LogModel lm = LogModel.newLogModel(method);
		logJob.info(lm.setMethod(method)
				.addMetaData("start",startTime).toJson(true));
		
		// 线程是否终止
		if (isStop) {
			return;
		}
		// 构建领域对象
		final InventoryConfirmScheduledDomain inventoryConfirmScheduledDomain = new InventoryConfirmScheduledDomain(
				"127.0.0.1", "jobCenter:ConfirmQueueConsumeJob", lm);
		// 注入仓储对象
		inventoryConfirmScheduledDomain
				.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		inventoryConfirmScheduledDomain.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		inventoryConfirmScheduledDomain.setdLock(dLock);
		// 业务处理
		inventoryConfirmScheduledDomain.businessHandler();
		long endTime = System.currentTimeMillis();
		runResult = "[ConfirmQueueTask]业务处理历时" + (startTime - endTime) + "milliseconds(毫秒)执行完成!";
		logJob.info(lm.setMethod(method)
				.addMetaData("endTime",endTime).addMetaData("runResult",runResult).toJson(true));	
		// 完成任务退出，通知任务中心jobcenter
		ExecutorManager.callBack(logId, runResult, 1);
	}

}
