package com.tuan.inventory.domain;

import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryScheduledService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class LogsQueueConsumeDomain extends AbstractGoodsInventoryDomain{

	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryScheduledService goodsInventoryScheduledService;
	private UpdateRequestPacket packet;
	//private static Logger logger = Logger.getLogger(LogsQueueConsumeDomain.class);
	private static Log logger = LogFactory.getLog(LogsQueueConsumeDomain.class);
	
	public LogsQueueConsumeDomain(UpdateRequestPacket packet,LogModel lm,Message messageRoot){
		this.packet = packet;
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		
	}
	
	@Override
	public ResultEnum checkParameter() {
		
		return ResultEnum.SUCCESS;
		
	}

	@Override
	public ResultEnum doBusiness() {
		
		try {
			//调用
			goodsInventoryScheduledService.logsQueueConsume(
					clientIp, clientName, messageRoot);
		} catch (Exception e) {
			logger.error(lm.setMethod("LogsQueueConsumeDomain.doBusiness").addMetaData("errMsg", e.getMessage()).toJson(),e);
			return ResultEnum.ERROR_2000;
		}
		return ResultEnum.SUCCESS;
	}

	@Override
	public GoodsInventoryUpdateResp makeResult(ResultEnum resultStatusEnum) {
		GoodsInventoryUpdateResp resp = new GoodsInventoryUpdateResp();
		if (resultStatusEnum.compareTo(ResultEnum.SUCCESS) == 0) {
			resp.setResult(ResultEnum.SUCCESS.getCode());
		}else{
			resp.setResult(ResultEnum.ERROR.getCode());
			resp.setErrorCode(resultStatusEnum.getCode());
			resp.setErrorMsg(resultStatusEnum.getDescription());
		}
	
		return resp;
	}
	
	@Override
	public void makeParameterMap(SortedMap<String, String> parameterMap) {
		super.init(packet.getClient(), packet.getIp());
		packet.addParameterMap(parameterMap);
	}

	public void setGoodsInventoryScheduledService(
			GoodsInventoryScheduledService goodsInventoryScheduledService) {
		this.goodsInventoryScheduledService = goodsInventoryScheduledService;
	}


	
}
