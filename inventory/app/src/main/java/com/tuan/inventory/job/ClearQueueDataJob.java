package com.tuan.inventory.job;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.util.QueueConstant;
import com.tuan.job.util.AbstractJobRunnable;
import com.tuan.job.util.ExecutorManager;

public class ClearQueueDataJob extends AbstractJobRunnable {
	private static Log logJob=LogFactory.getLog("CLEAR.JOB.LOG");
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	
	@Override
	public void run() {
	
		long startTime = System.currentTimeMillis();
		String method = "ClearQueueDataJob.run";
		final LogModel lm = LogModel.newLogModel(method);
		logJob.info(lm.setMethod(method)
				.addMetaData("start",startTime).toJson(false));
		
		// 线程是否终止
		if (isStop) {
			return;
		}
		//清除了多少条队列数据
		long byClearDataItems = 0;
		// 构建领域对象
		Long resultAck = goodsInventoryDomainRepository.clearQueueData(QueueConstant.MARK_QUEUE_DELETE_STATUS, QueueConstant.MARK_QUEUE_DELETE_STATUS);
		if(resultAck!=null) {
			byClearDataItems = resultAck;
		}
		long endTime = System.currentTimeMillis();
		runResult = "清理数据记录数="+byClearDataItems+",[ClearQueueDataJobTask]业务处理历时" + (startTime - endTime) + "milliseconds(毫秒)执行完成!";
		logJob.info(lm.setMethod(method)
				.addMetaData("endTime",endTime).addMetaData("runResult",runResult).toJson(false));	
		// 完成任务退出，通知任务中心jobcenter
		ExecutorManager.callBack(logId, runResult, 1);
	}

}
