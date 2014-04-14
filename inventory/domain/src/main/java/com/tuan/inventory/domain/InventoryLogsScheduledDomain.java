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
	//������Ʒid�������أ��Ա�鲢��ͬ��Ʒ����Ϣ���ʹ���
	private EventHandle logsEventHandle;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	public InventoryLogsScheduledDomain(String clientIp,
			String clientName,LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.lm = lm;
	}
	/***
	 * ҵ����ǰ��Ԥ����
	 */
	public void preHandler() {
		try {
			// ��Ʒ����Ƿ����
			//ȡ��ʼ״̬������Ϣ
			List<GoodsInventoryActionModel> queueLogList =  goodsInventoryDomainRepository.queryLastIndexGoodsInventoryAction();
			if (!CollectionUtils.isEmpty(queueLogList)) {
				for (GoodsInventoryActionModel model : queueLogList) {
				  this.model = model;
				}
			}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("preHandler").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
		}
		
		
	}

	// ҵ����
	public CreateInventoryResultEnum businessHandler() {

		try {
			// ҵ����ǰ��Ԥ����
			this.preHandler();
			if (fillActionEvent()) {	 //�Ӷ�����ȡ�¼�
				boolean eventResult = logsEventHandle.handleEvent(event);
				if(eventResult) {  //��mysql�ɹ��Ļ�,Ҳ����������־��Ϣ�ɹ�
					//�Ƴ����һ��Ԫ��
					this.goodsInventoryDomainRepository.lremLogQueue(model);
				   }
				}
			
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	//������Ϣ����
	public boolean fillActionEvent() {
		try {
			if(model==null) {
				return false;
			}
			event = new Event();
			event.setData(model);
			// ���͵Ĳ������½��з���
			event.setTryCount(0);
			if(!verifyId(model.getId())) {
				return false;
			}
			event.setUUID(String.valueOf(model.getId()));
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("fillActionEvent").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return false;
		}
		return true;
	}
	/**
	 * У��id
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
