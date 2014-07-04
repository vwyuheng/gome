package com.tuan.inventory.domain.base;

import java.util.List;
import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.domain.GoodsSelectionListQueryBySelIdListDomain;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public abstract class GoodsSelectionListBySelIdListDomain extends AbstractGoodsInventoryDomain{
	public RequestPacket requestPacket;		//窝窝内部统一的请求报文头
	public String goodsId;					//商品id
	public List<Long> selectionIdList;				//选型id列表
	public LogModel lm;					
	public Message traceMessage;					
	
	/**
	 * 初始化方法
	 * @param packet
	 * @return
	 */
	public void init(RequestPacket packet,String goodsId
			,List<Long> selectionIdList,LogModel lm,Message traceMessage){
		this.requestPacket = packet;
		this.goodsId = goodsId;
		this.selectionIdList = selectionIdList;
		this.lm = lm;
		this.traceMessage = traceMessage;
		makeParameterMap(parameterMap);
	}
	
	/**
	 * 创建卡签名domain
	 * @param packet	窝窝内部统一的请求报文头
	 * @return
	 */
	public static GoodsSelectionListQueryBySelIdListDomain makeGoodsSelectionListQueryDomain(RequestPacket packet,String goodsId,String selectionIdList,LogModel lm,Message traceMessage){
				return GoodsSelectionListQueryBySelIdListDomain.makeInstance(packet,goodsId,selectionIdList,lm,traceMessage);
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
		}else {
			return ResultEnum.INVALID_GOODSID;
		}
		if(CollectionUtils.isEmpty(selectionIdList)){
			return ResultEnum.NO_PARAMETER;
			}else {
				for(long id:selectionIdList) {
					if(id<=0) {
						return ResultEnum.INVALID_SELECTIONID;
					}
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
