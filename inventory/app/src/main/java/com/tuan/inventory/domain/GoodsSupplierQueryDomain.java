package com.tuan.inventory.domain;

import org.apache.log4j.Logger;

import com.tuan.inventory.domain.base.GoodsSuppliersDomain;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsSupplierQueryDomain extends GoodsSuppliersDomain{
	//private static Type respType = new TypeToken<GoodsSelectionQueryInnerResp>(){}.getType();
	private static Logger logger = Logger.getLogger(GoodsSupplierQueryDomain.class);
	private GoodsQueryInnerResp resp;		//请求验返回对象
	protected GoodsInventoryQueryService  goodsInventoryQueryService;
	private GoodsSupplierQueryDomain(){}
	
	public static GoodsSupplierQueryDomain makeInstance(RequestPacket packet,String goodsId,String suppliersId,LogModel lm
			,Message traceMessage){
		GoodsSupplierQueryDomain queryDomain = new GoodsSupplierQueryDomain();
		queryDomain.init(packet,goodsId, suppliersId,lm,traceMessage);
		return queryDomain;
	}
	
	@Override
	public ResultEnum doBusiness() {
		String method = "GoodsSupplierQueryDomain.doBusiness";
		
		String respStr = null;
		CallResult<GoodsSuppliersModel> queryCallResult = null;
		try {
			//请求银商，卡号转加密(卡签名)接口
			queryCallResult = goodsInventoryQueryService.findGoodsSuppliersBySuppliersId(clientIp, clientName, Long.parseLong(goodsId), Long.parseLong(suppliersId));
			
		} catch (Exception e) {
			logger.error(lm.setMethod(method).addMetaData("errorMsg", e.getMessage()).toJson(), e);
			return ResultEnum.ERROR_2000;
		}
		if (queryCallResult == null || !queryCallResult.isSuccess()) {
			return ResultEnum.INVALID_RETURN;
		}else {
			GoodsSuppliersModel gsModel = queryCallResult.getBusinessResult();
			respStr = JsonUtils.convertObjectToString(gsModel);
		}
		
		try{
			GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
			resp.setJsonResult(respStr);
			this.resp = resp;
			//this.resp = (GoodsSelectionQueryInnerResp)new Gson().fromJson(respStr, respType);
			this.resp.addHeadParameMap4Resp(parameterRespMap);
			
		}catch(Exception e){
			logger.error(lm.setMethod(method).addMetaData("errorMsg", e.getMessage()).addMetaData("respStr", respStr).toJson(), e);
			return ResultEnum.INVALID_RETURN;
		}
		
		return ResultEnum.SUCCESS;
	}
	

	@Override
	public GoodsQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setJsonResult(this.resp.getJsonResult());
		}
		//return JsonUtils.convertObjectToString(resp);
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
