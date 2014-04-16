package com.tuan.inventory.domain.base;

import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;

import com.tuan.inventory.domain.GoodsSelectionQueryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public abstract class GoodsSelectionDomain extends AbstractDomain{
	public RequestPacket requestPacket;		//窝窝内部统一的请求报文头
	public String goodsId;					//商品id
	public String selectionId;					//选型id
	public LogModel lm;					
	public Message traceMessage;					
	
	/**
	 * 初始化方法
	 * @param packet
	 * @param cardNo 银行卡号(未加密)
	 * @param mobile 手机号
	 * @return
	 */
	public void init(RequestPacket packet,String goodsId, String selectionId
			,LogModel lm,Message traceMessage){
		this.requestPacket = packet;
		this.goodsId = goodsId;
		this.selectionId = selectionId;
		this.lm = lm;
		this.traceMessage = traceMessage;
		makeParameterMap(parameterMap);
	}
	
	/**
	 * 创建卡签名domain
	 * @param packet	窝窝内部统一的请求报文头
	 * @param cardNo	银行卡号(未加密)
	 * @param mobile	手机号
	 * @return
	 */
	public static GoodsSelectionQueryDomain makeGoodsSelectionQueryDomain(RequestPacket packet,String goodsId,String selectionId,LogModel lm,Message traceMessage){
				return GoodsSelectionQueryDomain.makeInstance(packet,goodsId,selectionId,lm,traceMessage);
	}
	
	public void makeParameterMap(SortedMap<String, String> parameterMap) {
		parameterMap.put("goodsId", goodsId);
		parameterMap.put("selectionId", selectionId);
		
		requestPacket.addParameterMap(parameterMap);
	}
	
	@Override
	public ResultEnum checkParameter() {
		if(!StringUtils.isEmpty(goodsId)){
			try{
				if(Long.parseLong(goodsId) <= 0){
					return ResultEnum.INVALID_GOODSID;
				}
				
			}catch(Exception e){
				return ResultEnum.INVALID_GOODSID;
			}
		}
		if(!StringUtils.isEmpty(selectionId)){
			try{
				if(Long.parseLong(selectionId) <= 0){
					return ResultEnum.INVALID_SELECTIONID;
				}
				
			}catch(Exception e){
				return ResultEnum.INVALID_SELECTIONID;
			}
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
