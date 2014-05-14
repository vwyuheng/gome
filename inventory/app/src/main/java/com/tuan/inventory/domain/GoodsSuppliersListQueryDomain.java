package com.tuan.inventory.domain;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private static Log logerror = LogFactory.getLog("HTTPQUERYRESULT.LOG");
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
		logerror.info(lm.addMetaData("start", "start").toJson(false));
		CallResult<List<GoodsSuppliersModel>> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsSuppliersListByGoodsId(clientIp, clientName, Long.parseLong(goodsId));
			
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
			List<GoodsSuppliersModel> gSuppliersList = queryCallResult.getBusinessResult();
			GoodsSuppliersListQueryInnerResp resp = new GoodsSuppliersListQueryInnerResp();
			resp.setgSuppliersList(gSuppliersList);
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
	public GoodsSuppliersListQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsSuppliersListQueryInnerResp resp = new GoodsSuppliersListQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setgSuppliersList(this.resp.getgSuppliersList());
		}
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
