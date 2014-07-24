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
import com.tuan.inventory.model.param.CreaterGoodsSelectionParam;
import com.tuan.inventory.model.param.WmsInventoryParam;
import com.tuan.inventory.model.param.rest.WmsInventoryRestParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsWmsInventoryCreateDomain extends AbstractGoodsInventoryDomain{
	//选型类型
	private static Type typeSelection = new TypeToken<List<CreaterGoodsSelectionParam>>(){}.getType();
	//物流库存主键
	private Long id;// 主键id
	private String wmsGoodsId;  //物流商品的一种编码
	private String goodsSupplier;  //供货商
	private String goodsName;    //商品名称
	private java.lang.Integer totalNumber;// 当前总库存：无限制
	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
	private int isBeDelivery;
	//选型
	private List<GoodsSelectionModel> goodsSelection;
	
	//选型
	private List<CreaterGoodsSelectionParam> reqGoodsSelection;
	
	private UpdateRequestPacket packet;
	private WmsInventoryParam param;		
	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	
	private static Log logerror = LogFactory.getLog("HTTP.UPDATE.LOG");
	
	@SuppressWarnings("unchecked")
	public GoodsWmsInventoryCreateDomain(UpdateRequestPacket packet,WmsInventoryRestParam reqparam,LogModel lm,Message messageRoot){
		if(reqparam!=null) {
			String id = reqparam.getId();
			this.id = Long.valueOf(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(id))?"0":id);
			this.wmsGoodsId = JsonStrVerificationUtils.validateStr(reqparam.getWmsGoodsId()); 
			this.totalNumber = reqparam.getTotalNumber();
			this.leftNumber = reqparam.getLeftNumber();
			this.isBeDelivery = reqparam.getIsBeDelivery();
			this.goodsSupplier = JsonStrVerificationUtils.validateStr(reqparam.getGoodsSupplier());
			this.goodsName = JsonStrVerificationUtils.validateStr(reqparam.getGoodsName());
			String jsonSelectionResult =  reqparam.getGoodsSelection();
			
			if(StringUtils.isNotEmpty(JsonStrVerificationUtils.validateStr(jsonSelectionResult))) {
				this.reqGoodsSelection =  (List<CreaterGoodsSelectionParam>)new Gson().fromJson(jsonSelectionResult, typeSelection);
			}
			
		}
		this.packet = packet;
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillCreateParam();
		
	}
	
	public WmsInventoryParam fillCreateParam() {
		WmsInventoryParam param = new WmsInventoryParam();
		param.setId(id);
		param.setWmsGoodsId(wmsGoodsId);
		param.setGoodsName(goodsName);
		param.setGoodsSupplier(goodsSupplier);
		param.setLeftNumber(leftNumber);
		param.setTotalNumber(totalNumber);
		param.setIsBeDelivery(isBeDelivery);
		
		if(!CollectionUtils.isEmpty(reqGoodsSelection)) {
			goodsSelection = new ArrayList<GoodsSelectionModel> ();
			for(CreaterGoodsSelectionParam rparam:reqGoodsSelection) {
				GoodsSelectionModel gsModel = new GoodsSelectionModel();
				gsModel.setGoodsId(rparam.getGoodsId());
				gsModel.setId(rparam.getId());
				gsModel.setGoodTypeId(rparam.getGoodTypeId());
				gsModel.setLeftNumber(rparam.getLeftNumber());
				gsModel.setLimitStorage(rparam.getLimitStorage());
				gsModel.setSuppliersInventoryId(rparam.getSuppliersInventoryId());
				gsModel.setTotalNumber(rparam.getTotalNumber());
				gsModel.setWaterfloodVal(rparam.getWaterfloodVal());
				gsModel.setUserId(rparam.getUserId());
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
		if(id==null){
			return ResultEnum.INVALID_WMSID;
		}
		if(id!=null&&id<=0){
			return ResultEnum.INVALID_WMSID;
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
			InventoryCallResult resp = goodsInventoryUpdateService.createWmsInventory(clientIp, clientName, param, messageRoot);
			if(resp == null){
				return ResultEnum.SYS_ERROR;
			}
			if(!(resp.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
				return ResultEnum.getResultStatusEnum(String.valueOf(resp.getCode()));
			}
		} catch (Exception e) {
			logerror.error(lm.addMetaData("errMsg", "GoodsWmsInventoryCreateDomain.doBusiness error"+e.getMessage()).toJson(false),e);
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
		parameterMap.put("id", String.valueOf(id));
		parameterMap.put("wmsGoodsId", wmsGoodsId);
		parameterMap.put("totalNumber", String.valueOf(totalNumber));
		parameterMap.put("leftNumber",  String.valueOf(leftNumber));
	    parameterMap.put("isBeDelivery",  String.valueOf(isBeDelivery));
		parameterMap.put("goodsSupplier",  goodsSupplier);
		parameterMap.put("goodsName", goodsName);
		parameterMap.put("goodsSelection",  CollectionUtils.isEmpty(goodsSelection)?"":JSON.toJSONString(goodsSelection));
		packet.addParameterMap(parameterMap);
		super.init(packet.getClient(), packet.getIp());
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}
