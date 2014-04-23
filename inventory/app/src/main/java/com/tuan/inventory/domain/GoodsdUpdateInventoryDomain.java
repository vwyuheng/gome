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
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.UpdateInventoryParam;
import com.tuan.inventory.model.param.rest.GoodsSelectionRestParam;
import com.tuan.inventory.model.param.rest.GoodsSuppliersRestParam;
import com.tuan.inventory.model.param.rest.UpdateInventoryRestParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsdUpdateInventoryDomain extends AbstractGoodsInventoryDomain{
	//选型类型
	private static Type typeSelection = new TypeToken<List<GoodsSelectionRestParam>>(){}.getType();
	//分店类型
	private static Type typeSuppliers = new TypeToken<List<GoodsSuppliersRestParam>>(){}.getType();
	private String userId;
	private String goodsId;// 商品ID(FK)
	private String orderId; //订单id
	private int num;// 扣减的库存
	//选型
	private List<GoodsSelectionRestParam> reqGoodsSelection;
	//分店
	private List<GoodsSuppliersRestParam> reqGoodsSuppliers;
	//选型
	private List<GoodsSelectionModel> goodsSelection;
	//分店
	private List<GoodsSuppliersModel> goodsSuppliers;
	private UpdateRequestPacket packet;
	private UpdateInventoryParam param;		
	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	
	private static Logger logger = Logger.getLogger(GoodsdUpdateInventoryDomain.class);
	
	@SuppressWarnings("unchecked")
	public GoodsdUpdateInventoryDomain(UpdateRequestPacket packet,UpdateInventoryRestParam reqparam,LogModel lm,Message messageRoot){
		if(reqparam!=null) {
			
			this.userId = reqparam.getUserId();
			this.goodsId = reqparam.getGoodsId();
			this.orderId = reqparam.getOrderId();
			this.num = reqparam.getNum();
			String jsonSelectionResult =  reqparam.getGoodsSelection();
			String jsonSuppliersResult =  reqparam.getGoodsSuppliers();
			if(StringUtils.isNotEmpty(jsonSelectionResult)) {
				this.reqGoodsSelection =  (List<GoodsSelectionRestParam>)new Gson().fromJson(jsonSelectionResult, typeSelection);
			}
			if(StringUtils.isNotEmpty(jsonSuppliersResult)) {
				this.reqGoodsSuppliers =  (List<GoodsSuppliersRestParam>)new Gson().fromJson(jsonSuppliersResult, typeSuppliers);
			}
		}
		this.packet = packet;
		//this.reqparam = reqparam;
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillUpdateParam();
	}
	
	public UpdateInventoryParam fillUpdateParam() {
		UpdateInventoryParam param = new UpdateInventoryParam();
		param.setGoodsId(this.goodsId);
		param.setUserId(userId);
		param.setNum(num);
		param.setOrderId(orderId);
		
		if(!CollectionUtils.isEmpty(reqGoodsSelection)) {
			goodsSelection = new ArrayList<GoodsSelectionModel> ();
			for(GoodsSelectionRestParam rparam:reqGoodsSelection) {
				GoodsSelectionModel gsModel = new GoodsSelectionModel();
				gsModel.setId(rparam.getSelectionId());
				gsModel.setNum(rparam.getStNum());
				goodsSelection.add(gsModel);
			}
		}
		if(!CollectionUtils.isEmpty(reqGoodsSuppliers)) {
			goodsSuppliers = new ArrayList<GoodsSuppliersModel> ();
			for(GoodsSuppliersRestParam sparam:reqGoodsSuppliers) {
				GoodsSuppliersModel gsModel = new GoodsSuppliersModel();
				gsModel.setId(sparam.getSuppliersId());
				gsModel.setNum(sparam.getSsNum());
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
			InventoryCallResult resp = goodsInventoryUpdateService.updateInventory(
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
		if(!StringUtils.isEmpty(userId))
		    parameterMap.put("userId", userId);
		if(!StringUtils.isEmpty(goodsId))
		parameterMap.put("goodsId", goodsId);
		if(!StringUtils.isEmpty(orderId))
		parameterMap.put("orderId", orderId);
		parameterMap.put("num",  String.valueOf(num));
		
		parameterMap.put("reqGoodsSuppliers", CollectionUtils.isEmpty(reqGoodsSuppliers)?"":LogUtil.formatListLog(reqGoodsSuppliers));
		parameterMap.put("reqGoodsSelection", CollectionUtils.isEmpty(reqGoodsSelection)?"":LogUtil.formatListLog(reqGoodsSelection));
		
		packet.addParameterMap(parameterMap);
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}