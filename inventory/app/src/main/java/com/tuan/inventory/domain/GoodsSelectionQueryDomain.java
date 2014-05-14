package com.tuan.inventory.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.GoodsSelectionDomain;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsSelectionQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsSelectionQueryDomain extends GoodsSelectionDomain{
	private static Log logerror = LogFactory.getLog("HTTPQUERYRESULT.LOG");
	private GoodsSelectionQueryInnerResp resp;		//请求验返回对象
	protected GoodsInventoryQueryService  goodsInventoryQueryService;
	private GoodsSelectionQueryDomain(){}
	
	public static GoodsSelectionQueryDomain makeInstance(RequestPacket packet,String goodsId,String selectionId,LogModel lm
			,Message traceMessage){
		GoodsSelectionQueryDomain queryDomain = new GoodsSelectionQueryDomain();
		queryDomain.init(packet,goodsId, selectionId,lm,traceMessage);
		return queryDomain;
	}
	
	@Override
	public ResultEnum doBusiness() {
		writeLog(lm.addMetaData("start", "start"),false);
		CallResult<GoodsSelectionModel> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsSelectionBySelectionId(clientIp, clientName, Long.parseLong(goodsId), Long.parseLong(selectionId));
			
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
			GoodsSelectionModel goodsSelection = queryCallResult.getBusinessResult();
			GoodsSelectionQueryInnerResp resp = new GoodsSelectionQueryInnerResp();
			resp.setGoodsSelection(goodsSelection);
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
	public GoodsSelectionQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsSelectionQueryInnerResp resp = new GoodsSelectionQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setGoodsSelection(this.resp.getGoodsSelection());
		}
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
