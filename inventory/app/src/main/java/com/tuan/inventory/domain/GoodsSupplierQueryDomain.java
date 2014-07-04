package com.tuan.inventory.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.GoodsSuppliersDomain;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.resp.inner.GoodsSuppliersQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsSupplierQueryDomain extends GoodsSuppliersDomain{
	private static Log logerror = LogFactory.getLog("HTTPQUERYRESULT.LOG");

	private GoodsSuppliersQueryInnerResp resp;		//请求验返回对象
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
		logerror.info(lm.addMetaData("start", "start").toJson(false));
		String respStr = null;
		CallResult<GoodsSuppliersModel> queryCallResult = null;
		try {
			//
			queryCallResult = goodsInventoryQueryService.findGoodsSuppliersBySuppliersId(clientIp, clientName, Long.parseLong(goodsId), Long.parseLong(suppliersId));
			
		} catch (Exception e) {
			logerror.error(lm.addMetaData("errorMsg", e.getMessage()).toJson(false), e);
			return ResultEnum.SYS_ERROR;
		}
		
		if (queryCallResult == null ) {
			return ResultEnum.SYS_ERROR;
		}else if (!queryCallResult.isSuccess()) {
			return ResultEnum.getResultStatusEnum(String.valueOf(queryCallResult.getPublicCodeEnum().getCode()));
		}else {
			GoodsSuppliersModel goodsSuppliers = queryCallResult.getBusinessResult();
			GoodsSuppliersQueryInnerResp resp = new GoodsSuppliersQueryInnerResp();
			resp.setGoodsSuppliers(goodsSuppliers);
			this.resp = resp;
			this.resp.addHeadParameMap4Resp(parameterRespMap);
		}
		
		try{
			
			
		}catch(Exception e){
			logerror.error(lm.addMetaData("errorMsg", e.getMessage()).addMetaData("respStr", respStr).toJson(false), e);
			return ResultEnum.INVALID_RETURN;
		}
		
		return ResultEnum.SUCCESS;
	}
	

	@Override
	public GoodsSuppliersQueryInnerResp makeResult(ResultEnum resultStatusEnum) {
		GoodsSuppliersQueryInnerResp resp = new GoodsSuppliersQueryInnerResp();
		resp.setResult(resultStatusEnum.getCode(), resultStatusEnum.getDescription());
		if(this.resp != null){
			resp.setGoodsSuppliers(this.resp.getGoodsSuppliers());
		}
		return (resp);
	}

	public void setGoodsInventoryQueryService(
			GoodsInventoryQueryService goodsInventoryQueryService) {
		this.goodsInventoryQueryService = goodsInventoryQueryService;
	}

}
