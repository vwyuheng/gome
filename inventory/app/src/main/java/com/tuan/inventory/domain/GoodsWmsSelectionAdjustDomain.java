package com.tuan.inventory.domain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.model.param.AdjustGoodsSelectionWmsParam;
import com.tuan.inventory.model.param.WmsAdjustSelectionParam;
import com.tuan.inventory.model.param.rest.WmsSelectionInventoryRestParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsWmsSelectionAdjustDomain extends AbstractGoodsInventoryDomain{
	//选型类型
	private static Type typeSelection = new TypeToken<List<AdjustGoodsSelectionWmsParam>>(){}.getType();
	private String wmsGoodsId;  //物流商品的一种编码
	//物流商品id
	private Long goodsId;
	//选型
	private List<GoodsSelectionModel> goodsSelection;
	//选型
	private List<AdjustGoodsSelectionWmsParam> reqGoodsSelection;
	
	private UpdateRequestPacket packet;
	private WmsAdjustSelectionParam param;		
	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	
	private static Log logerror = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	
	@SuppressWarnings("unchecked")
	public GoodsWmsSelectionAdjustDomain(UpdateRequestPacket packet,WmsSelectionInventoryRestParam reqparam,LogModel lm,Message messageRoot){
		if(reqparam!=null) {
			
			this.wmsGoodsId = JsonStrVerificationUtils.validateStr(reqparam.getWmsGoodsId()); 
			String goodsId = reqparam.getGoodsId();
			this.goodsId = StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(goodsId))?0l:Long.parseLong(goodsId);
			String jsonSelectionResult =  reqparam.getGoodsSelection();
			if(StringUtils.isNotEmpty(JsonStrVerificationUtils.validateStr(jsonSelectionResult))) {
				this.reqGoodsSelection =  (List<AdjustGoodsSelectionWmsParam>)new Gson().fromJson(jsonSelectionResult, typeSelection);
			}
			
		}
		this.packet = packet;
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillCreateParam();
		
	}
	
	public WmsAdjustSelectionParam fillCreateParam() {
		WmsAdjustSelectionParam param = new WmsAdjustSelectionParam();
		param.setGoodsId(goodsId);
		param.setWmsGoodsId(wmsGoodsId);
		if(!CollectionUtils.isEmpty(reqGoodsSelection)) {
			goodsSelection = new ArrayList<GoodsSelectionModel> ();
			for(AdjustGoodsSelectionWmsParam rparam:reqGoodsSelection) {
				GoodsSelectionModel gsModel = new GoodsSelectionModel();
				gsModel.setGoodsId(goodsId);
				gsModel.setId(rparam.getId());
				gsModel.setGoodTypeId(rparam.getGoodTypeId());
				gsModel.setNum(rparam.getNum());
				gsModel.setLimitStorage(rparam.getLimitStorage());
				goodsSelection.add(gsModel);
			}
		}
		if(!CollectionUtils.isEmpty(goodsSelection))
		       param.setGoodsSelection(goodsSelection);
		
		return param;
		
	}
	@Override
	public ResultEnum checkParameter() {
		if(StringUtils.isEmpty(wmsGoodsId)){
			return ResultEnum.INVALID_WMSGOODSID;
		}
		if(goodsId==null){
			return ResultEnum.INVALID_GOODSID;
		}else if(goodsId!=null&&goodsId<=0){
			return ResultEnum.INVALID_GOODSID;
		}

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
			InventoryCallResult resp = goodsInventoryUpdateService.adjustWmsSelectionInventory(clientIp, clientName, param, messageRoot);
			if(resp == null){
				return ResultEnum.SYS_ERROR;
			}
			if(!(resp.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
				return ResultEnum.getResultStatusEnum(String.valueOf(resp.getCode()));
			}
		} catch (Exception e) {
			logerror.error(lm.addMetaData("errMsg","GoodsWmsSelectionAdjustDomain.doBusiness error"+ e.getMessage()).toJson(false),e);
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
		lm.addMetaData("物流选型调整响应结果", JSON.toJSONString(resp));
		logerror.info(lm.toJson(false));
		return resp;
	}
	
	@Override
	public void makeParameterMap(SortedMap<String, String> parameterMap) {
		parameterMap.put("wmsGoodsId", wmsGoodsId);
		parameterMap.put("goodsSelection",  CollectionUtils.isEmpty(goodsSelection)?"":JSON.toJSONString(goodsSelection));
		parameterMap.put("goodsId",  String.valueOf(goodsId));
		packet.addParameterMap(parameterMap);
		super.init(packet.getClient(), packet.getIp());
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}
