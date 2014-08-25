package com.tuan.inventory.domain;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.GoodsSelectionListDomain;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsSelectionListQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsSelectionListQueryDomain extends GoodsSelectionListDomain{
	private static Log logquery = LogFactory.getLog("SYS.QUERYRESULT.LOG");
	private GoodsSelectionListQueryInnerResp resp;		//请求验返回对象
	protected GoodsInventoryQueryService  goodsInventoryQueryService;
	private GoodsSelectionListQueryDomain(){}
	
	public static GoodsSelectionListQueryDomain makeInstance(RequestPacket packet,String goodsId,LogModel lm
			,Message traceMessage){
		GoodsSelectionListQueryDomain queryDomain = new GoodsSelectionListQueryDomain();
		queryDomain.init(packet,goodsId,lm,traceMessage);
		return queryDomain;
	}
	
	@Override
	public ResultEnum doBusiness() {
		logquery.info(lm.addMetaData("doBusiness", "doBusiness").toJson(false));
		CallResult<List<GoodsSelectionModel>> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsSelectionListByGoodsId(clientIp, clientName, Long.parseLong(goodsId));
			
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
			List<GoodsSelectionModel> gSelectionList = queryCallResult.getBusinessResult();
			GoodsSelectionListQueryInnerResp resp = new GoodsSelectionListQueryInnerResp();
			resp.setgSelectionList(gSelectionList);
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
	public GoodsSelectionListQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsSelectionListQueryInnerResp resp = new GoodsSelectionListQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setgSelectionList(this.resp.getgSelectionList());
		}
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
