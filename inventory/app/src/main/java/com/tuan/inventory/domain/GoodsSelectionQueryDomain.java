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
	//private static Type respType = new TypeToken<GoodsSelectionQueryInnerResp>(){}.getType();
	//private static Logger logger = Logger.getLogger(GoodsSelectionQueryDomain.class);
	private static Log logger = LogFactory.getLog(GoodsSelectionQueryDomain.class);
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
		String method = "GoodsSelectionQueryDomain.doBusiness";
		
		//String respStr = null;
		CallResult<GoodsSelectionModel> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsSelectionBySelectionId(clientIp, clientName, Long.parseLong(goodsId), Long.parseLong(selectionId));
			
		} catch (Exception e) {
			logger.error(lm.setMethod(method).addMetaData("errorMsg", e.getMessage()).toJson(), e);
			return ResultEnum.ERROR_2000;
		}
		try{
		if (queryCallResult == null || !queryCallResult.isSuccess()) {
			return ResultEnum.INVALID_RETURN;
		}else {
			GoodsSelectionModel goodsSelection = queryCallResult.getBusinessResult();
			GoodsSelectionQueryInnerResp resp = new GoodsSelectionQueryInnerResp();
			resp.setGoodsSelection(goodsSelection);
			this.resp = resp;
			//this.resp = (GoodsSelectionQueryInnerResp)new Gson().fromJson(respStr, respType);
			this.resp.addHeadParameMap4Resp(parameterRespMap);
			//respStr = JsonUtils.convertObjectToString(gsModel);
		}
	
		}catch(Exception e){
			logger.error(lm.setMethod(method).addMetaData("errorMsg", e.getMessage()).addMetaData("resp", resp).toJson(), e);
			return ResultEnum.INVALID_RETURN;
		}
		
		return ResultEnum.SUCCESS;
	}
	

	@Override
	public GoodsSelectionQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsSelectionQueryInnerResp resp = new GoodsSelectionQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setGoodsSelection(this.resp.getGoodsSelection());
		}
		//return JsonUtils.convertObjectToString(resp);
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
