package com.tuan.inventory.job;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.InventoryLockedScheduledDomain;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.param.InventoryScheduledParam;
import com.tuan.job.util.AbstractJobRunnable;
import com.tuan.job.util.ExecutorManager;

public class LockedQueueConsumeJob extends AbstractJobRunnable {
	private static Log logJob=LogFactory.getLog("INVENTORY.JOB.LOG");
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	//默认间隔时长,与当前时间相比  单位:分种
	private static final int DEFAULTPERIOD = 5;
	private int period = 0;
	@Override
	public void run() {
	
		long startTime = System.currentTimeMillis();
		String method = "LockedQueueConsumeJob.run";
		final LogModel lm = LogModel.newLogModel(method);
		logJob.info(lm.setMethod(method)
				.addMetaData("start",startTime).toJson(true));
		
		// 线程是否终止
		if (isStop) {
			return;
		}
		InventoryScheduledParam param = new InventoryScheduledParam();
		param.setPeriod(getPeriod()==0?DEFAULTPERIOD:getPeriod());
		//构建领域对象
		final InventoryLockedScheduledDomain inventoryLockedScheduledDomain = new InventoryLockedScheduledDomain("127.0.0.1", "jobCenter:ConfirmQueueConsumeJob",param, lm);
		//注入仓储对象
		inventoryLockedScheduledDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		//业务处理
		inventoryLockedScheduledDomain.businessHandler();
		long endTime = System.currentTimeMillis();
		runResult = "[LockedQueueTask]业务处理历时" + (startTime - endTime) + "milliseconds(毫秒)执行完成!";
		logJob.info(lm.setMethod(method)
				.addMetaData("endTime",endTime).addMetaData("runResult",runResult).toJson(true));	
		// 完成任务退出，通知任务中心jobcenter
		ExecutorManager.callBack(logId, runResult, 1);
	}
	
	
	
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}

	
}
