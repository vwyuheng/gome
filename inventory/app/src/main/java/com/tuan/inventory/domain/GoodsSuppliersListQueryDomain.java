package com.tuan.inventory.domain;

import java.util.List;

import org.apache.log4j.Logger;

import com.tuan.inventory.domain.base.GoodsSuppliersListDomain;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsSuppliersListQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsSuppliersListQueryDomain extends GoodsSuppliersListDomain{
	//private static Type respType = new TypeToken<GoodsSelectionQueryInnerResp>(){}.getType();
	private static Logger logger = Logger.getLogger(GoodsSuppliersListQueryDomain.class);
	private GoodsSuppliersListQueryInnerResp resp;		//请求验返回对象
	protected GoodsInventoryQueryService  goodsInventoryQueryService;
	private GoodsSuppliersListQueryDomain(){}
	
	public static GoodsSuppliersListQueryDomain makeInstance(RequestPacket packet,String goodsId,LogModel lm
			,Message traceMessage){
		GoodsSuppliersListQueryDomain queryDomain = new GoodsSuppliersListQueryDomain();
		queryDomain.init(packet,goodsId,lm,traceMessage);
		return queryDomain;
	}
	
	@Override
	public ResultEnum doBusiness() {
		String method = "GoodsQueryDomain.doBusiness";
		
		//String respStr = null;
		CallResult<List<GoodsSuppliersModel>> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsSuppliersListByGoodsId(clientIp, clientName, Long.parseLong(goodsId));
			
		} catch (Exception e) {
			logger.error(lm.setMethod(method).addMetaData("errorMsg", e.getMessage()).toJson(), e);
			return ResultEnum.ERROR_2000;
		}
		try{
		if (queryCallResult == null || !queryCallResult.isSuccess()) {
			return ResultEnum.INVALID_RETURN;
		}else {
			List<GoodsSuppliersModel> gSuppliersList = queryCallResult.getBusinessResult();
			GoodsSuppliersListQueryInnerResp resp = new GoodsSuppliersListQueryInnerResp();
			resp.setgSuppliersList(gSuppliersList);
			this.resp = resp;
			this.resp.addHeadParameMap4Resp(parameterRespMap);
		}
			
		}catch(Exception e){
			logger.error(lm.setMethod(method).addMetaData("errorMsg", e.getMessage()).addMetaData("resp", resp).toJson(), e);
			return ResultEnum.INVALID_RETURN;
		}
		
		return ResultEnum.SUCCESS;
	}
	

	@Override
	public GoodsSuppliersListQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsSuppliersListQueryInnerResp resp = new GoodsSuppliersListQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setgSuppliersList(this.resp.getgSuppliersList());
		}
		//return JsonUtils.convertObjectToString(resp);
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}