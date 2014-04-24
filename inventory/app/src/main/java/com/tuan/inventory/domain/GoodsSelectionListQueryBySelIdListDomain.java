package com.tuan.inventory.domain;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuan.inventory.domain.base.GoodsSelectionListBySelIdListDomain;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsSelectionListQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsSelectionListQueryBySelIdListDomain extends GoodsSelectionListBySelIdListDomain{
	
	private static Logger logger = Logger.getLogger(GoodsSelectionListQueryBySelIdListDomain.class);
	private static Type type = new TypeToken<List<Long>>(){}.getType();
	private GoodsSelectionListQueryInnerResp resp;		//请求验返回对象
	protected GoodsInventoryQueryService  goodsInventoryQueryService;
	
	private GoodsSelectionListQueryBySelIdListDomain(){}
	
	public static GoodsSelectionListQueryBySelIdListDomain makeInstance(RequestPacket packet,String goodsId,String selectionIdList,LogModel lm
			,Message traceMessage){
		List<Long> selectionIdTmpList = null;
		if(StringUtils.isNotEmpty(selectionIdList)) {
			selectionIdTmpList =  new Gson().fromJson(selectionIdList, type);
		}
		GoodsSelectionListQueryBySelIdListDomain queryDomain = new GoodsSelectionListQueryBySelIdListDomain();
		queryDomain.init(packet,goodsId,selectionIdTmpList,lm,traceMessage);
		return queryDomain;
	}
	
	@Override
	public ResultEnum doBusiness() {
		String method = "GoodsQueryDomain.doBusiness";
		
		//String respStr = null;
		CallResult<List<GoodsSelectionModel>> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsSelectionListBySelectionIdList(clientIp, clientName, Long.parseLong(goodsId),selectionIdList);
			
		} catch (Exception e) {
			logger.error(lm.setMethod(method).addMetaData("errorMsg", e.getMessage()).toJson(), e);
			return ResultEnum.ERROR_2000;
		}
		try{
		if (queryCallResult == null || !queryCallResult.isSuccess()) {
			return ResultEnum.INVALID_RETURN;
		}else {
			List<GoodsSelectionModel> gSelectionList = queryCallResult.getBusinessResult();
			GoodsSelectionListQueryInnerResp resp = new GoodsSelectionListQueryInnerResp();
			resp.setgSelectionList(gSelectionList);
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
	public GoodsSelectionListQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsSelectionListQueryInnerResp resp = new GoodsSelectionListQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setgSelectionList(this.resp.getgSelectionList());
		}
		//return JsonUtils.convertObjectToString(resp);
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
