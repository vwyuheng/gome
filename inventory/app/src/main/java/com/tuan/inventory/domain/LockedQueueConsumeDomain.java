package com.tuan.inventory.domain;

import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.param.InventoryScheduledParam;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryScheduledService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class LockedQueueConsumeDomain extends AbstractGoodsInventoryDomain{
	private int period;
		
	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryScheduledService goodsInventoryScheduledService;
	private InventoryScheduledParam param;
	private UpdateRequestPacket packet;
	//private static Logger logger = Logger.getLogger(LockedQueueConsumeDomain.class);
	private static Log logger = LogFactory.getLog(LockedQueueConsumeDomain.class);
	public LockedQueueConsumeDomain(UpdateRequestPacket packet,String period,LogModel lm,Message messageRoot){
		this.packet = packet;
		this.period = Integer.valueOf(period);
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillScheduledParam();
		
	}
	
	public InventoryScheduledParam fillScheduledParam() {
		InventoryScheduledParam param = new InventoryScheduledParam();
		param.setPeriod(period);
		return param;
	}
	@Override
	public ResultEnum checkParameter() {
		if(period<=0){
			return ResultEnum.INVALID_PERIOD;
		}
		
		ResultEnum checkPackEnum = packet.checkParameter();
		if(checkPackEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return checkPackEnum;
		}
		
		return ResultEnum.SUCCESS;
		
	}

	@Override
	public ResultEnum doBusiness() {
		
		try {
			//调用
			goodsInventoryScheduledService.lockedQueueConsume(
					clientIp, clientName, param, messageRoot);
			
		} catch (Exception e) {
			logger.error(lm.setMethod("GoodsCreateInventoryDomain.doBusiness").addMetaData("errMsg", e.getMessage()).toJson(),e);
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
		parameterMap.put("period", String.valueOf(period));
		
		packet.addParameterMap(parameterMap);
	}

	public void setGoodsInventoryScheduledService(
			GoodsInventoryScheduledService goodsInventoryScheduledService) {
		this.goodsInventoryScheduledService = goodsInventoryScheduledService;
	}


}
