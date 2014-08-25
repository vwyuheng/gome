package com.tuan.inventory.domain;

import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.model.param.CallbackParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsdAckInventoryDomain extends AbstractGoodsInventoryDomain{
	private String ack;
	private String key;
		
	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	private CallbackParam param;
	private UpdateRequestPacket packet;
	private static Log logupdate = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	
	public GoodsdAckInventoryDomain(UpdateRequestPacket packet,String ack,String key,LogModel lm,Message messageRoot){
		this.packet = packet;
		this.ack = ack;
		this.key = key;
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillCallbackParam();
		
	}
	
	public CallbackParam fillCallbackParam() {
		CallbackParam param = new CallbackParam();
		param.setAck(ack);
		param.setKey(key);
		return param;
	}
	@Override
	public ResultEnum checkParameter() {
		if(StringUtils.isEmpty(ack)){
			return ResultEnum.INVALID_ACK;
		}
		if(StringUtils.isEmpty(key)){
			return ResultEnum.INVALID_KEY;
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
			InventoryCallResult resp = goodsInventoryUpdateService.callbackAckInventory(
					clientIp, clientName, param, messageRoot);
			if(resp == null){
				return ResultEnum.SYS_ERROR;
			}
			if(!(resp.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
				return ResultEnum.getResultStatusEnum(String.valueOf(resp.getCode()));
			}
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errMsg", "GoodsCreateInventoryDomain.doBusiness error"+e.getMessage()).toJson(false),e);
			return ResultEnum.SYS_ERROR;
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
		parameterMap.put("ack", ack);
		parameterMap.put("key", key);
		super.init(packet.getClient(), packet.getIp());
		packet.addParameterMap(parameterMap);
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}
