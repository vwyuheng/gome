package com.tuan.inventory.domain;

import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.model.param.UpdateWmsDataParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class UpdateWmsDataDomain extends AbstractGoodsInventoryDomain{
	private String tokenid;  //redis序列,解决接口幂等问题
	private String goodsId;// 商品ID(FK)
	private String suppliersId;// 分店ID(FK)
	private String wmsGoodsId;  //物流商品的一种编码
	private String goodsTypeIds;  //选项类型表id集合,以逗号分隔开；如：1,2,3或2
	private String goodsSelectionIds;  //共享库存时所绑定的选型表ID集合，以逗号分隔开；如：1,2,3或2
	private String isBeDelivery;  // 仓库类型
	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	private UpdateWmsDataParam param;
	private UpdateRequestPacket packet;
	private String goodsBaseId;
	private static Log logerror = LogFactory.getLog("HTTP.UPDATE.LOG");
	public UpdateWmsDataDomain(UpdateRequestPacket packet,String tokenid,String goodsId,String suppliersId, String wmsGoodsId,String isBeDelivery,String goodsTypeIds, String goodsSelectionIds,LogModel lm,Message messageRoot,String goodsBaseId){
		this.packet = packet;
		this.tokenid = JsonStrVerificationUtils.validateStr(tokenid);
		this.goodsId = JsonStrVerificationUtils.validateStr(goodsId);
		this.suppliersId = JsonStrVerificationUtils.validateStr(suppliersId);
		this.wmsGoodsId = JsonStrVerificationUtils.validateStr(wmsGoodsId);
		this.goodsTypeIds = JsonStrVerificationUtils.validateStr(goodsTypeIds);
		this.goodsSelectionIds = JsonStrVerificationUtils.validateStr(goodsSelectionIds);
		this.isBeDelivery = JsonStrVerificationUtils.validateStr(isBeDelivery);
		this.goodsBaseId =goodsBaseId;
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillAdjustIParam();
		
	}
	
	public UpdateWmsDataParam fillAdjustIParam() {
		UpdateWmsDataParam param = new UpdateWmsDataParam();
		param.setTokenid(tokenid);
		param.setGoodsId(goodsId);
		param.setSuppliersId(suppliersId);
		param.setWmsGoodsId(wmsGoodsId);
		param.setIsBeDelivery(JsonStrVerificationUtils.validateStr(isBeDelivery));
		param.setGoodsTypeIds(goodsTypeIds);
		param.setGoodsSelectionIds(goodsSelectionIds);
		param.setGoodsBaseId(goodsBaseId);
		return param;
	}
	@Override
	public ResultEnum checkParameter() {
		if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(goodsId))){
			return ResultEnum.INVALID_GOODSID;
		}
		/*if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(suppliersId))){
			return ResultEnum.INVALID_SUPPLIERSID;
		}*/
		if (StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(wmsGoodsId))) {
			return ResultEnum.INVALID_WMSGOODSID;
		}
	    /*if (StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(goodsTypeIds))) {
			return ResultEnum.INVALID_GOODS_TYPE_ID;
		}*/
	    
		ResultEnum checkPackEnum = packet.checkParameter();
		if(checkPackEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return checkPackEnum;
		}
		
		return ResultEnum.SUCCESS;
		
	}

	@Override
	public ResultEnum doBusiness() {
		try {
			//调用
			InventoryCallResult resp = goodsInventoryUpdateService.updateWmsData(
					clientIp, clientName, param, messageRoot);
			if(resp == null){
				return ResultEnum.SYS_ERROR;
			}
			if(!(resp.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
				return ResultEnum.getResultStatusEnum(String.valueOf(resp.getCode()));
			}
		} catch (Exception e) {
			logerror.error(lm.addMetaData("errMsg", "UpdateWmsDataDomain.doBusiness error"+e.getMessage()).toJson(false),e);
			return ResultEnum.SYS_ERROR;
		}
		return ResultEnum.SUCCESS;
	}

	@Override
	public GoodsInventoryUpdateResp makeResult(ResultEnum resultStatusEnum) {
		GoodsInventoryUpdateResp resp = new GoodsInventoryUpdateResp();
		if (resultStatusEnum.compareTo(ResultEnum.SUCCESS) == 0) {
			resp.setResult(ResultEnum.SUCCESS.getCode());
		}else{
			resp.setResult(ResultEnum.ERROR.getCode());
			resp.setErrorCode(resultStatusEnum.getCode());
			resp.setErrorMsg(resultStatusEnum.getDescription());
		}
	
		return resp;
	}
	
	@Override
	public void makeParameterMap(SortedMap<String, String> parameterMap) {
		parameterMap.put("tokenid", tokenid);
		parameterMap.put("goodsId", goodsId);
		parameterMap.put("suppliersId", suppliersId);
		parameterMap.put("wmsGoodsId", wmsGoodsId);
		parameterMap.put("isBeDelivery", isBeDelivery);
		parameterMap.put("goodsTypeIds", goodsTypeIds);
		parameterMap.put("goodsSelectionIds", goodsSelectionIds);
		parameterMap.put("goodsBaseId", goodsBaseId);
		super.init(packet.getClient(), packet.getIp());
		packet.addParameterMap(parameterMap);
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}
