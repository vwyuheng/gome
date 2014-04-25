package com.tuan.inventory.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.GoodsDomain;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsQueryDomain extends GoodsDomain{
	//private static Type respType = new TypeToken<GoodsSelectionQueryInnerResp>(){}.getType();
	//private static Logger logger = Logger.getLogger(GoodsQueryDomain.class);
	private static Log logger = LogFactory.getLog(GoodsQueryDomain.class);
	private GoodsQueryInnerResp resp;		//请求验返回对象
	protected GoodsInventoryQueryService  goodsInventoryQueryService;
	private GoodsQueryDomain(){}
	
	public static GoodsQueryDomain makeInstance(RequestPacket packet,String goodsId,LogModel lm
			,Message traceMessage){
		GoodsQueryDomain queryDomain = new GoodsQueryDomain();
		queryDomain.init(packet,goodsId,lm,traceMessage);
		return queryDomain;
	}
	
	@Override
	public ResultEnum doBusiness() {
		String method = "GoodsQueryDomain.doBusiness";
		
		//String respStr = null;
		CallResult<GoodsInventoryModel> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsInventoryByGoodsId(clientIp, clientName, Long.parseLong(goodsId));
			
		} catch (Exception e) {
			logger.error(lm.setMethod(method).addMetaData("errorMsg", e.getMessage()).toJson(), e);
			return ResultEnum.ERROR_2000;
		}
		try{
		if (queryCallResult == null || !queryCallResult.isSuccess()) {
			return ResultEnum.INVALID_RETURN;
		}else {
			GoodsInventoryModel gsModel = queryCallResult.getBusinessResult();
			GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
			resp.setGoodsInventory(gsModel);
			this.resp = resp;
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
	public GoodsQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setGoodsInventory(this.resp.getGoodsInventory());
		}
		//return JsonUtils.convertObjectToString(resp);
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
