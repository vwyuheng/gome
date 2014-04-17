package com.tuan.inventory.domain.base;

import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;

import com.tuan.inventory.domain.GoodsSupplierQueryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public abstract class GoodsSuppliersDomain extends AbstractGoodsInventoryDomain{
	public RequestPacket requestPacket;		//窝窝内部统一的请求报文头
	public String goodsId;					//商品id
	public String suppliersId;					//分店id
	public LogModel lm;					
	public Message traceMessage;					
	
	/**
	 * 初始化方法
	 * @param packet
	 * @param cardNo 银行卡号(未加密)
	 * @param mobile 手机号
	 * @return
	 */
	public void init(RequestPacket packet,String goodsId, String suppliersId
			,LogModel lm,Message traceMessage){
		this.requestPacket = packet;
		this.goodsId = goodsId;
		this.suppliersId = suppliersId;
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
	public static GoodsSupplierQueryDomain makeGoodsSupplierQueryDomain(RequestPacket packet,String goodsId,String suppliersId,LogModel lm,Message traceMessage){
				return GoodsSupplierQueryDomain.makeInstance(packet,goodsId,suppliersId,lm,traceMessage);
	}
	
	public void makeParameterMap(SortedMap<String, String> parameterMap) {
		parameterMap.put("goodsId", goodsId);
		parameterMap.put("suppliersId", suppliersId);
		
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
		if(!StringUtils.isEmpty(suppliersId)){
			try{
				if(Long.parseLong(suppliersId) <= 0){
					return ResultEnum.INVALID_SUPPLIERSID;
				}
				
			}catch(Exception e){
				return ResultEnum.INVALID_SUPPLIERSID;
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
