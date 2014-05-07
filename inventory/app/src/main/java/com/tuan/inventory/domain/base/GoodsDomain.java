package com.tuan.inventory.domain.base;

import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;

import com.tuan.inventory.domain.GoodsQueryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public abstract class GoodsDomain extends AbstractGoodsInventoryDomain{
	public RequestPacket requestPacket;		//窝窝内部统一的请求报文头
	public String goodsId;					//商品id
	public LogModel lm;					
	public Message traceMessage;					
	
	/**
	 * 初始化方法
	 * @param packet
	 * @return
	 */
	public void init(RequestPacket packet,String goodsId
			,LogModel lm,Message traceMessage){
		this.requestPacket = packet;
		this.goodsId = goodsId;
		this.lm = lm;
		this.traceMessage = traceMessage;
		makeParameterMap(parameterMap);
	}
	
	/**
	 * 创建卡签名domain
	 * @param packet	窝窝内部统一的请求报文头
	 * @return
	 */
	public static GoodsQueryDomain makeGoodsQueryDomain(RequestPacket packet,String goodsId,LogModel lm,Message traceMessage){
				return GoodsQueryDomain.makeInstance(packet,goodsId,lm,traceMessage);
	}
	
	public void makeParameterMap(SortedMap<String, String> parameterMap) {
		parameterMap.put("goodsId", goodsId);
		requestPacket.addParameterMap(parameterMap);
		super.init(requestPacket.getClient(), requestPacket.getIp());
	}
	
	@Override
	public ResultEnum checkParameter() {
		if(!StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(goodsId))){
			try{
				if(Long.parseLong(goodsId) <= 0){
					return ResultEnum.INVALID_GOODSID;
				}
				
			}catch(Exception e){
				return ResultEnum.INVALID_GOODSID;
			}
		}else {  //为空时
			return ResultEnum.INVALID_GOODSID;
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
}
