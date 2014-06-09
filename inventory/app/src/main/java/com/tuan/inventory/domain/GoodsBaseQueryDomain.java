package com.tuan.inventory.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.GoodsBaseDomain;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsBaseQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsBaseQueryDomain extends GoodsBaseDomain{
	private static Log logerror = LogFactory.getLog("HTTPQUERYRESULT.LOG");
	private GoodsBaseQueryInnerResp resp;		//请求验返回对象
	protected GoodsInventoryQueryService  goodsInventoryQueryService;
	private GoodsBaseQueryDomain(){}
	
	public static GoodsBaseQueryDomain makeInstance(RequestPacket packet,String goodsBaseId,LogModel lm
			,Message traceMessage){
		GoodsBaseQueryDomain queryDomain = new GoodsBaseQueryDomain();
		queryDomain.init(packet,goodsBaseId,lm,traceMessage);
		return queryDomain;
	}
	
	@Override
	public ResultEnum doBusiness() {
		writeLog(lm.addMetaData("start", "start"),false);
		CallResult<GoodsBaseModel> queryCallResult = null;
		try {
			queryCallResult = goodsInventoryQueryService.findSalesCountByGoodsBaseId(clientIp, clientName, goodsBaseId);
		} catch (Exception e) {
			logerror.error(lm.addMetaData("errorMsg", e.getMessage()).toJson(false), e);
			return ResultEnum.SYS_ERROR;
		}
		try{
		
		if (queryCallResult == null ) {
				return ResultEnum.SYS_ERROR;
		}else if (!queryCallResult.isSuccess()) {
				return ResultEnum.getResultStatusEnum(String.valueOf(queryCallResult.getPublicCodeEnum().getCode()));
		}else {
			GoodsBaseModel goodsBaseModel = queryCallResult.getBusinessResult();
			GoodsBaseQueryInnerResp resp = new GoodsBaseQueryInnerResp();
			resp.setGoodsBaseModel(goodsBaseModel);
			this.resp = resp;
			this.resp.addHeadParameMap4Resp(parameterRespMap);
		}
	
		}catch(Exception e){
			logerror.error(lm.addMetaData("errorMsg", e.getMessage()).addMetaData("resp", resp).toJson(false), e);
			return ResultEnum.SYS_ERROR;
		}
		writeLog(lm.addMetaData("result", resp).addMetaData("end", "end"),false);
		return ResultEnum.SUCCESS;
	}

	@Override
	public GoodsBaseQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsBaseQueryInnerResp resp = new GoodsBaseQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setGoodsBaseModel(this.resp.getGoodsBaseModel());
		}
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}
}
