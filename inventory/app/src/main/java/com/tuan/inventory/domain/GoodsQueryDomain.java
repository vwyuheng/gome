package com.tuan.inventory.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.GoodsDomain;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsQueryDomain extends GoodsDomain{
	private static Log logerror = LogFactory.getLog("HTTPQUERYRESULT.LOG");
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
		logerror.info(lm.addMetaData("start", "start").toJson(false));
		CallResult<GoodsInventoryModel> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsInventoryByGoodsId(clientIp, clientName, Long.parseLong(goodsId));
			
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
			GoodsInventoryModel gsModel = queryCallResult.getBusinessResult();
			GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
			resp.setGoodsInventory(gsModel);
			this.resp = resp;
			this.resp.addHeadParameMap4Resp(parameterRespMap);
		}
		}catch(Exception e){
			logerror.error(lm.addMetaData("errorMsg", e.getMessage()).addMetaData("result", resp).toJson(false), e);
			return ResultEnum.SYS_ERROR;
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
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
