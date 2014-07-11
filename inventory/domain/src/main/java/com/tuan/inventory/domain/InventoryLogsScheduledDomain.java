package com.tuan.inventory.domain;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.job.event.Event;
import com.tuan.inventory.domain.support.job.event.EventHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;

public class InventoryLogsScheduledDomain extends AbstractDomain {
	private LogModel lm;
	private GoodsInventoryActionModel model;
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
	public void preHandler() {
		try {
			// 商品库存是否存在
			//取初始状态队列信息
			//List<GoodsInventoryActionModel> queueLogList =  goodsInventoryDomainRepository.queryLastIndexGoodsInventoryAction();
			List<GoodsInventoryActionModel> queueLogList =  goodsInventoryDomainRepository.queryFirstInGoodsInventoryAction();
			if (!CollectionUtils.isEmpty(queueLogList)) {
				for (GoodsInventoryActionModel model : queueLogList) {
				  this.model = model;
				}
			}else {
				writeJobLog("[LogsTask]获取队列:("+"日志"+")的队列为空！");
			}
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"preHandler error" + e.getMessage()),false, e);
		}
		
		
	}

	// 业务处理
	public CreateInventoryResultEnum businessHandler() {

		try {
			// 业务检查前的预处理
			this.preHandler();
			if (fillActionEvent()) {	 //从队列中取事件
				boolean eventResult = logsEventHandle.handleEvent(event);
				if(eventResult) {  //落mysql成功的话,也就是消费日志消息成功
					//移除最后一个元素
					this.goodsInventoryDomainRepository.lremLogQueue(model);
				   }
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
	public boolean fillActionEvent() {
		try {
			if(model==null) {
				return false;
			}
			event = new Event();
			event.setData(model);
			// 发送的不再重新进行发送
			event.setTryCount(0);
			if(!verifyId(model.getId())) {
				return false;
			}
			event.setUUID(String.valueOf(model.getId()));
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
