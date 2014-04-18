package com.tuan.inventory.domain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.CreaterGoodsSelectionParam;
import com.tuan.inventory.model.param.CreaterGoodsSuppliersParam;
import com.tuan.inventory.model.param.CreaterInventoryParam;
import com.tuan.inventory.model.param.rest.CreaterInventoryRestParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsCreateInventoryDomain extends AbstractGoodsInventoryDomain{
	//选型类型
	private static Type typeSelection = new TypeToken<List<CreaterGoodsSelectionParam>>(){}.getType();
	//分店类型
	private static Type typeSuppliers = new TypeToken<List<CreaterGoodsSuppliersParam>>(){}.getType();
	private String userId;
	private String goodsId;// 商品ID(FK)
	private Integer totalNumber;// 当前总库存999999：无限制
	private Integer leftNumber;// 当前剩余数库存默认值:0
	private int limitStorage; // 0:库存无限制；1：限制库存
	private Integer waterfloodVal;  //注水值
	//选型
	private List<GoodsSelectionModel> goodsSelection;
	//分店
	private List<GoodsSuppliersModel> goodsSuppliers;
	
	//选型
	private List<CreaterGoodsSelectionParam> reqGoodsSelection;
	//分店
	private List<CreaterGoodsSuppliersParam> reqGoodsSuppliers;
	
	private UpdateRequestPacket packet;
	//private CreaterInventoryRestParam reqparam;		//创建商品库存参数
	private CreaterInventoryParam param;		
	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	
	private static Logger logger = Logger.getLogger(GoodsCreateInventoryDomain.class);
	
	@SuppressWarnings("unchecked")
	public GoodsCreateInventoryDomain(UpdateRequestPacket packet,CreaterInventoryRestParam reqparam,LogModel lm,Message messageRoot){
		if(reqparam!=null) {
			
			this.userId = reqparam.getUserId();
			this.goodsId = reqparam.getGoodsId();
			this.totalNumber = reqparam.getTotalNumber();
			this.leftNumber = reqparam.getLeftNumber();
			this.limitStorage = reqparam.getLimitStorage();
			this.waterfloodVal = reqparam.getWaterfloodVal();
			String jsonSelectionResult =  reqparam.getGoodsSelection();
			String jsonSuppliersResult =  reqparam.getGoodsSuppliers();
			if(StringUtils.isNotEmpty(jsonSelectionResult)) {
				this.reqGoodsSelection =  (List<CreaterGoodsSelectionParam>)new Gson().fromJson(jsonSelectionResult, typeSelection);
			}
			if(StringUtils.isNotEmpty(jsonSuppliersResult)) {
				this.reqGoodsSuppliers =  (List<CreaterGoodsSuppliersParam>)new Gson().fromJson(jsonSuppliersResult, typeSuppliers);
			}
		}
		this.packet = packet;
		//this.reqparam = reqparam;
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillCreateParam();
		
	}
	
	public CreaterInventoryParam fillCreateParam() {
		CreaterInventoryParam param = new CreaterInventoryParam();
		param.setGoodsId(this.goodsId);
		param.setUserId(userId);
		param.setWaterfloodVal(waterfloodVal);
		param.setLeftNumber(leftNumber);
		param.setTotalNumber(totalNumber);
		param.setLimitStorage(limitStorage);
		
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
		if(!CollectionUtils.isEmpty(reqGoodsSuppliers)) {
			goodsSuppliers = new ArrayList<GoodsSuppliersModel> ();
			for(CreaterGoodsSuppliersParam sparam:reqGoodsSuppliers) {
				GoodsSuppliersModel gsModel = new GoodsSuppliersModel();
				gsModel.setGoodsId(sparam.getGoodsId());
				gsModel.setId(sparam.getId());
				gsModel.setLeftNumber(sparam.getLeftNumber());
				gsModel.setLimitStorage(sparam.getLimitStorage());
				gsModel.setSuppliersId(sparam.getSuppliersId());
				gsModel.setTotalNumber(sparam.getTotalNumber());
				gsModel.setWaterfloodVal(sparam.getWaterfloodVal());
				gsModel.setUserId(sparam.getUserId());
				goodsSuppliers.add(gsModel);
			}
		}
		if(!CollectionUtils.isEmpty(goodsSelection))
		       param.setGoodsSelection(goodsSelection);
		if(!CollectionUtils.isEmpty(goodsSuppliers))
		       param.setGoodsSuppliers(goodsSuppliers);
		return param;
		
	}
	@Override
	public ResultEnum checkParameter() {
		if(StringUtils.isEmpty(goodsId)){
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
			InventoryCallResult resp = goodsInventoryUpdateService.createInventory(
					clientIp, clientName, param, messageRoot);
			if(resp == null){
				return ResultEnum.ERROR_2000;
			}
			if(!(resp.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
				return ResultEnum.INVALID_RETURN;
			}
		} catch (Exception e) {
			logger.error(lm.setMethod("GoodsCreateInventoryDomain.doBusiness").addMetaData("errMsg", e.getMessage()).toJson(),e);
			return ResultEnum.ERROR_2000;
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
		parameterMap.put("userId", userId);
		parameterMap.put("goodsId", goodsId);
		parameterMap.put("totalNumber", String.valueOf(totalNumber));
		parameterMap.put("leftNumber",  String.valueOf(leftNumber));
	    parameterMap.put("limitStorage",  String.valueOf(limitStorage));
		parameterMap.put("waterfloodVal",  String.valueOf(waterfloodVal));
		parameterMap.put("goodsSuppliers", JsonUtils.convertObjectToString(goodsSuppliers));
		parameterMap.put("goodsSelection", JsonUtils.convertObjectToString(goodsSelection));
		
		packet.addParameterMap(parameterMap);
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}
