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

public class InventoryLogsScheduledDomain extends AbstractDomain {
	private static Log logLogs=LogFactory.getLog("LOGS.JOB.LOG");
	private LogModel lm;
	//private GoodsInventoryActionModel model;
	private List<GoodsInventoryActionModel> queueLogLists;
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
	/***
	 * 业务处理前的预处理
	 */
	public boolean preHandler() {
		try {
			// 商品库存是否存在
			//取初始状态队列信息:一次取一条
			//List<GoodsInventoryActionModel> queueLogList =  goodsInventoryDomainRepository.queryLastIndexGoodsInventoryAction();
			//取初始状态队列信息:一次取100条
			List<GoodsInventoryActionModel> queueLogList =  goodsInventoryDomainRepository.queryFirstInGoodsInventoryAction();
			if (!CollectionUtils.isEmpty(queueLogList)) {
				logLogs.info("[LogsJob]获取队列:("+queueLogList.size()+")条");
				queueLogLists = queueLogList;
				/*for (GoodsInventoryActionModel model : queueLogList) {
				  this.model = model;
				}*/
			}else {
				logLogs.info("[LogsTask]获取队列:("+"日志"+")的队列为空！");
				return false;
			}
		} catch (Exception e) {
			
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"preHandler error" + e.getMessage()),false, e);
			return false;
		}
		return true;
		
	}

	// 业务处理
	public CreateInventoryResultEnum businessHandler() {

		try {
			// 业务检查前的预处理
			if(this.preHandler()) {
				if(!CollectionUtils.isEmpty(queueLogLists)) {
					//for (GoodsInventoryActionModel model : queueLogLists) {
						 // this.model = model;
						  if (fillActionEvent(queueLogLists)) {	 //从队列中取事件
							  long startTime = System.currentTimeMillis();
								String method = "logsEventHandle.handleEvent,[批量处理日志队列]:("+queueLogLists+"),start";
								final LogModel lm = LogModel.newLogModel(method);
								logLogs.info(lm.setMethod(method).addMetaData("start", startTime)
										.toJson(true));
								boolean eventResult = logsEventHandle.handleEvent(event);
								long endTime = System.currentTimeMillis();
								String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
										+ "milliseconds(毫秒)执行完成!,eventResult:"+eventResult;
								logLogs.info(lm.setMethod(method).addMetaData("endTime", endTime)
										.addMetaData("runResult", runResult));
								if(eventResult) {  //落mysql成功的话,也就是消费日志消息成功
									for (GoodsInventoryActionModel model : queueLogLists) {
										String remmethod = "lremLogQueue,[移除日志队列]:("+model+",eventResult:)"+eventResult;
										final LogModel remlm = LogModel.newLogModel(remmethod);
										//循环删除所有元素
										Long rem = this.goodsInventoryDomainRepository.lremLogQueue(model);
										logLogs.info(remlm.setMethod(remmethod).addMetaData("删除结果", rem));
									}
									
								   }
								}
						}
					
				//}
			}else {
				return CreateInventoryResultEnum.SYS_ERROR;
			}
			
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
			// 发送的不再重新进行发送
			event.setTryCount(0);
			/*if(!verifyId(model.getId())) {
				return false;
			}*/
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
