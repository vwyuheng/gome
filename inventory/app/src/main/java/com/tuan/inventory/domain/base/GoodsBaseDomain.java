package com.tuan.inventory.domain.base;

import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.GoodsBaseQueryDomain;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public abstract class GoodsBaseDomain extends AbstractGoodsInventoryDomain{
	private static Log logresult = LogFactory.getLog("HTTPQUERYRESULT.LOG");
	public RequestPacket requestPacket;		//窝窝内部统一的请求报文头
	public String goodsBaseId;	
	public LogModel lm;					
	public Message traceMessage;					
	
	/**
	 * 初始化方法
	 * @param packet
	 * @return
	 */
	public void init(RequestPacket packet,String goodsBaseId
			,LogModel lm,Message traceMessage){
		this.requestPacket = packet;
		this.goodsBaseId = goodsBaseId;
		this.lm = lm;
		this.traceMessage = traceMessage;
		makeParameterMap(parameterMap);
	}
	
	/**
	 * 创建卡签名domain
	 * @param packet	窝窝内部统一的请求报文头
	 * @return
	 */
	public static GoodsBaseQueryDomain makeGoodsBaseQueryDomain(RequestPacket packet,String goodsBaseId,LogModel lm,Message traceMessage){
				return GoodsBaseQueryDomain.makeInstance(packet,goodsBaseId,lm,traceMessage);
	}
	
	public void makeParameterMap(SortedMap<String, String> parameterMap) {
		parameterMap.put("goodsBaseId", goodsBaseId);
		requestPacket.addParameterMap(parameterMap);
		super.init(requestPacket.getClient(), requestPacket.getIp());
	}
	
	@Override
	public ResultEnum checkParameter() {
		if(!StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(goodsBaseId))){
			try{
				if(Long.parseLong(goodsBaseId) <= 0){
					return ResultEnum.INVALID_GOODSBASEID;
				}
				
			}catch(Exception e){
				return ResultEnum.INVALID_GOODSBASEID;
			}
		}else {
			return ResultEnum.INVALID_GOODSBASEID;
		}
		
		
		
		if(requestPacket == null){
			return ResultEnum.NO_PARAMETER;
		}
		ResultEnum checkPackEnum = requestPacket.checkParameter();
		if(checkPackEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return checkPackEnum;
		}
		
		return ResultEnum.SUCCESS;
	}
	
	protected void writeLog(LogModel lm,boolean toJson) {
		if (logresult.isInfoEnabled()) {
			logresult.info(lm.toJson(toJson));
		}
	}
}
