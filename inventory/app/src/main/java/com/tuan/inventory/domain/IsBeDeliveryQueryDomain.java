package com.tuan.inventory.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.IsBeDeliveryDomain;
import com.tuan.inventory.model.WmsIsBeDeliveryModel;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.IsBeDeliveryQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class IsBeDeliveryQueryDomain extends IsBeDeliveryDomain{
	private static Log logquery = LogFactory.getLog("SYS.QUERYRESULT.LOG");
	private IsBeDeliveryQueryInnerResp resp;		//请求验返回对象
	protected GoodsInventoryQueryService  goodsInventoryQueryService;
	private IsBeDeliveryQueryDomain(){}
	
	public static IsBeDeliveryQueryDomain makeInstance(RequestPacket packet,String wmsGoodsId,String isBeDelivery,LogModel lm
			,Message traceMessage){
		IsBeDeliveryQueryDomain queryDomain = new IsBeDeliveryQueryDomain();
		queryDomain.init(packet,wmsGoodsId,isBeDelivery,lm,traceMessage);
		return queryDomain;
	}
	
	@Override
	public ResultEnum doBusiness() {
		logquery.info(lm.addMetaData("doBusiness", "IsBeDeliveryQueryDomain.doBusiness").toJson(false));
		CallResult<WmsIsBeDeliveryModel> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findWmsIsBeDeliveryByWmsGoodsId(clientIp, clientName, wmsGoodsId,isBeDelivery);
			
		} catch (Exception e) {
			logquery.error(lm.addMetaData("errorMsg", e.getMessage()).toJson(false), e);
			return ResultEnum.SYS_ERROR;
		}
		try{
		
		if (queryCallResult == null ) {
				return ResultEnum.SYS_ERROR;
		}else if (!queryCallResult.isSuccess()) {
				return ResultEnum.getResultStatusEnum(String.valueOf(queryCallResult.getPublicCodeEnum().getCode()));
		}else {
			WmsIsBeDeliveryModel isBeDelivery = queryCallResult.getBusinessResult();
			IsBeDeliveryQueryInnerResp resp = new IsBeDeliveryQueryInnerResp();
			resp.setIsBeDelivery(isBeDelivery);
			this.resp = resp;
			this.resp.addHeadParameMap4Resp(parameterRespMap);
		}
			
		}catch(Exception e){
			logquery.error(lm.addMetaData("errorMsg", e.getMessage()).addMetaData("result", resp).toJson(false), e);
			return ResultEnum.SYS_ERROR;
		}
		
		return ResultEnum.SUCCESS;
	}
	

	@Override
	public IsBeDeliveryQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		IsBeDeliveryQueryInnerResp resp = new IsBeDeliveryQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setIsBeDelivery(this.resp.getIsBeDelivery());
		}
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
