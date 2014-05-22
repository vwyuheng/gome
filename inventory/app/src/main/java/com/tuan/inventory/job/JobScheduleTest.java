package com.tuan.inventory.job;

import java.util.Random;

import com.tuan.job.util.AbstractJobRunnable;
import com.tuan.job.util.ExecutorManager;

public class JobScheduleTest extends AbstractJobRunnable {
	//private static Log logger = LogFactory.getLog(JobScheduleTest.class);
	@Override
	public void run() {
	
		Random r = new Random();
		int max = r.nextInt(500);
		// for 或者 while
		for (int i = 0; i < max; i++) {
			if (isStop) {
				break;
			}
			// 业务处理 示例的业务就是睡100毫秒
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			runResult = "第" + (i + 1) + "个业务执行完成!";
			System.out.println(runResult);
		}
		// 完成任务退出，知道任务中心jobcenter
		ExecutorManager.callBack(logId, runResult, 1);
	}

}
