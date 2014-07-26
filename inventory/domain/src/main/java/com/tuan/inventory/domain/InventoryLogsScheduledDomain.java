package com.tuan.inventory.domain;

import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.job.event.Event;
import com.tuan.inventory.domain.support.job.event.EventHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.util.QueueConstant;

public class InventoryLogsScheduledDomain extends AbstractDomain {
	private static Log logLogs=LogFactory.getLog("LOGS.JOB.LOG");
	private LogModel lm;
	/** 每次循环提取的队列数 */
	private static final int dealStep = 500;
	private static final int haveRest = 1000 * 1;
	private static final int handleBatch = 250;
	//private List<GoodsInventoryActionModel> queueLogLists;
	private Event event;
	//缓存商品id，并排重，以便归并相同商品的消息发送次数
	private EventHandle logsEventHandle;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	
	public InventoryLogsScheduledDomain(String clientIp,
			String clientName,LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.lm = lm;
	}

	// 业务处理
	public CreateInventoryResultEnum businessHandler() {

		try {
			// 业务检查前的预处理
			int movedTotalCount = 0;
			final Long currentMaxId = goodsInventoryDomainRepository.queryLogQueueMaxLenth(QueueConstant.QUEUE_LOGS_MESSAGE);
			if (currentMaxId == null || currentMaxId.longValue() <= 0) {
				// 表中无记录，直接返回
				logLogs.warn("日志队列获取当前最大记录lenth(数)返回空，结束本次日志队列历史记录迁移");
				return CreateInventoryResultEnum.SUCCESS;
			}
			while (true) {
				
				List<GoodsInventoryActionModel> queueLogList =  goodsInventoryDomainRepository.queryFirstInGoodsInventoryAction();
				if(CollectionUtils.isEmpty(queueLogList)) {
					logLogs.warn("日志队列" + queueLogList + "无待迁移的日志队列记录！");
					break;
				}
				 long startTime = System.currentTimeMillis();
				 String method = "logsEventHandle.handleEvent";
				final LogModel lm = LogModel.newLogModel(method);
				logLogs.warn(lm.setMethod(method).addMetaData("batch handler logs start", startTime)
							.toJson(true));
				int curMovedCount = queueLogList.size();
				if (curMovedCount > 0) {
					if (fillActionEvent(queueLogList)) {	 //从队列中取事件
						 
							boolean eventResult = logsEventHandle.handleEvent(event);
							
							if(eventResult) {  //落mysql成功的话,也就是消费日志消息成功
								for (GoodsInventoryActionModel model : queueLogList) {
									String remmethod = "lremLogQueue,[移除日志队列]:("+model+",eventResult:)"+eventResult;
									final LogModel remlm = LogModel.newLogModel(remmethod);
									//循环删除所有元素
									//Long rem = this.goodsInventoryDomainRepository.lremLogQueue(model);
									String bydelLogMember = this.goodsInventoryDomainRepository.lpop(QueueConstant.QUEUE_LOGS_MESSAGE);
									
									logLogs.warn(remlm.setMethod(remmethod).addMetaData("日志id", model!=null?model.getId():0).addMetaData("删除日志", bydelLogMember));
								}
								
							   }
							}
				}
				 
				// 记录本次程序运行的处理结果
				long endTime = System.currentTimeMillis();
				String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
						+ "milliseconds(毫秒)执行完成!,日志队列本次循环处理条数:"+curMovedCount;
				logLogs.warn(lm.setMethod(method).addMetaData("endTime", endTime)
						.addMetaData("runResult", runResult));
				movedTotalCount += curMovedCount;
				if (curMovedCount < dealStep) {
					break;
				}
				try {
					int rd = (int)(Math.random()*100)%60+1;//1~60
					Thread.sleep(rd*haveRest);
				} catch (Exception e) {
					logLogs.debug(e.getMessage(), e);
				}
				
			}
			
			// 记录本次程序运行的处理结果
			logLogs.warn("日志队列本次程序运行处理总条数＝" + movedTotalCount);
			
			
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"businessHandler error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	//加载消息数据
	public boolean fillActionEvent(List<GoodsInventoryActionModel> modelList) {
		try {
			if(CollectionUtils.isEmpty(modelList)) {
				return false;
			}
			event = new Event();
			event.setData(modelList);
			event.setTryCount(0);
			event.setHandleBatch(handleBatch);
			event.setUUID(UUID.randomUUID().toString());
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"fillActionEvent error" + e.getMessage()),false, e);
			return false;
		}
		return true;
	}
	/**
	 * 校验id
	 * @param id
	 * @return
	 */
   public boolean verifyId(long id) {
	   if(id<=0) {
		   return false;
	   }else {
		   return true;
	   }
   }

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	public void setLogsEventHandle(EventHandle logsEventHandle) {
		this.logsEventHandle = logsEventHandle;
	}

}
