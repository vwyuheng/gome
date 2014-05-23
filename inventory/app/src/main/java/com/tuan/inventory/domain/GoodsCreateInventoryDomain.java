package com.tuan.inventory.domain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.domain.support.util.LogUtil;
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
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsCreateInventoryDomain extends AbstractGoodsInventoryDomain{
	//选型类型
	private static Type typeSelection = new TypeToken<List<CreaterGoodsSelectionParam>>(){}.getType();
	//分店类型
	private static Type typeSuppliers = new TypeToken<List<CreaterGoodsSuppliersParam>>(){}.getType();
	private String tokenid;  //redis序列,解决顺序问题
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
	
	//是否选型商品
	private boolean isSelection;
	// 是否分店商品
	private boolean isSupplier;
	
	private static Log logerror = LogFactory.getLog("HTTP.UPDATE.LOG");
	
	@SuppressWarnings("unchecked")
	public GoodsCreateInventoryDomain(UpdateRequestPacket packet,CreaterInventoryRestParam reqparam,LogModel lm,Message messageRoot){
		if(reqparam!=null) {
			this.tokenid = reqparam.getTokenid();
			this.userId = reqparam.getUserId();
			this.goodsId = reqparam.getGoodsId();
			this.totalNumber = reqparam.getTotalNumber();
			this.leftNumber = reqparam.getLeftNumber();
			this.limitStorage = reqparam.getLimitStorage();
			this.waterfloodVal = reqparam.getWaterfloodVal();
			String jsonSelectionResult =  reqparam.getGoodsSelection();
			String jsonSuppliersResult =  reqparam.getGoodsSuppliers();
			
			if(StringUtils.isNotEmpty(JsonStrVerificationUtils.validateStr(jsonSelectionResult))) {
				this.reqGoodsSelection =  (List<CreaterGoodsSelectionParam>)new Gson().fromJson(jsonSelectionResult, typeSelection);
			}
			if(StringUtils.isNotEmpty(JsonStrVerificationUtils.validateStr(jsonSuppliersResult))) {
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
		param.setTokenid(tokenid);
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
		if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(goodsId))){
			return ResultEnum.INVALID_GOODSID;
		}
		
		//根据传过来的参数判断是否选型商品
		if(!CollectionUtils.isEmpty(reqGoodsSelection)) {  //若商品存在选型，则为选型商品，
			isSelection = true;
		}
		//根据传过来的参数判断是否分店商品
		if(!CollectionUtils.isEmpty(reqGoodsSuppliers)) {  //若商品存在分店，则为分店商品，
			isSupplier = true;
		}
		//选型剩余库存
		int selLeftNum = 0;
		//选型总库存
		int selTotalNum = 0;
		//分店剩余库存
		int suppLeftNum = 0;
		//分店总库存
		int suppTotalNum = 0;
		if (isSelection && !isSupplier) { // 只包含选型的
			// 校验商品选型id
			if (!CollectionUtils.isEmpty(reqGoodsSelection)) {
				for (CreaterGoodsSelectionParam gsparam : reqGoodsSelection) {
					if (gsparam.getId() <= 0) {
						return ResultEnum.INVALID_SELECTIONID;
					}
					if (limitStorage == 1) { // 若是限制库存商品，则进行校验
						//校验是否限制库存是否一致
						if(gsparam.getLimitStorage()==0) {
							return ResultEnum.INVALID_SEL_LIMT;
						}
						selLeftNum = selLeftNum + gsparam.getLeftNumber();
						selTotalNum = selTotalNum + gsparam.getTotalNumber();
					}else {
						//校验是否限制库存是否一致
						if(gsparam.getLimitStorage()==1) {
							return ResultEnum.INVALID_SEL_LIMT;
						}
					}

				}
			} else {
				return ResultEnum.SELECTION_GOODS;
			}
			if (limitStorage == 1) { // 若是限制库存商品，则进行校验
				
				// 如果是限制库存商品 则需校验其下选型的剩余库存之和=商品总的剩余库存
				if (!(leftNumber == selLeftNum)) {
					return ResultEnum.INVALID_LEFTNUM_SELECTION;
				}
				// 其下选型总库存之和= 商品总的库存量
				if (!(totalNumber == selTotalNum)) {
					return ResultEnum.INVALID_TOTALNUM_SELECTION;
				}
			}
			
		}
		if(isSupplier&&!isSelection) {  //只包含分店的
			//校验商品分店id
			if(!CollectionUtils.isEmpty(reqGoodsSuppliers)) {
				for(CreaterGoodsSuppliersParam gsparam : reqGoodsSuppliers) {
					if(gsparam.getSuppliersId()<=0) {
						return ResultEnum.INVALID_SUPPLIERSID;
					}
					if (limitStorage == 1) { // 若是限制库存商品，则进行校验
						//校验是否限制库存是否一致
						if(gsparam.getLimitStorage()==0) {
							return ResultEnum.INVALID_SUPP_LIMT;
						}
						suppLeftNum = suppLeftNum + gsparam.getLeftNumber();
						suppTotalNum = suppTotalNum + gsparam.getTotalNumber();
					}else {
						//校验是否限制库存是否一致
						if(gsparam.getLimitStorage()==1) {
							return ResultEnum.INVALID_SUPP_LIMT;
						}
					}
				}
			}else {
				return ResultEnum.SUPPLIERS_GOODS;
			}
			if (limitStorage == 1) { // 若是限制库存商品，则进行校验
				// 如果是限制库存商品 则需校验其下选型的剩余库存之和=商品总的剩余库存
				if (!(leftNumber == suppLeftNum)) {
					return ResultEnum.INVALID_LEFTNUM_SUPPLIER;
					}
				// 其下分店总库存之和= 商品总的库存量
			    if (!(totalNumber == suppTotalNum)) {
					return ResultEnum.INVALID_TOTALNUM_SUPPLIER;
					}
			}
			
		}
		
		if(isSupplier&&isSelection) {  //分店选型都有的
			if (!CollectionUtils.isEmpty(reqGoodsSuppliers)&&!CollectionUtils.isEmpty(reqGoodsSelection)) {
				for(CreaterGoodsSelectionParam gsparam : reqGoodsSelection) {
					if(gsparam.getId()<=0) {
						return ResultEnum.INVALID_SELECTIONID;
					}
					if (limitStorage == 1) { // 若是限制库存商品，则进行校验
						//校验是否限制库存是否一致
						if(gsparam.getLimitStorage()==0) {
							return ResultEnum.INVALID_SEL_LIMT;
						}
						selLeftNum = selLeftNum + gsparam.getLeftNumber();
						selTotalNum = selTotalNum + gsparam.getTotalNumber();
					}else {
						if(gsparam.getLimitStorage()==1) {
							return ResultEnum.INVALID_SEL_LIMT;
						}
					}
				}
				for(CreaterGoodsSuppliersParam gsparam : reqGoodsSuppliers) {
					if(gsparam.getSuppliersId()<=0) {
						return ResultEnum.INVALID_SUPPLIERSID;
					}
					if (limitStorage == 1) { // 若是限制库存商品，则进行校验
						//校验是否限制库存是否一致
						if(gsparam.getLimitStorage()==0) {
							return ResultEnum.INVALID_SUPP_LIMT;
						}
						suppLeftNum = suppLeftNum + gsparam.getLeftNumber();
						suppTotalNum = suppTotalNum + gsparam.getTotalNumber();
					}else {
						if(gsparam.getLimitStorage()==1) {
							return ResultEnum.INVALID_SUPP_LIMT;
						}
					}
				}
				
			}else {
				return ResultEnum.NOTNULL_SEL_SUPP_GOODS;
			}
			
			if (limitStorage == 1) { // 若是限制库存商品，则进行校验
				// 如果是限制库存商品 则需校验其下选型的剩余库存之和=商品总的剩余库存
				if (!(leftNumber == (selLeftNum+suppLeftNum))) {
					return ResultEnum.INVALID_LEFTNUM_SELANDSUPP;
					}
				// 其下分店总库存之和= 商品总的库存量
			    if (!(totalNumber == (selTotalNum+suppTotalNum))) {
					return ResultEnum.INVALID_TOTALNUM_SELANDSUPP;
					}
			}
		}

		ResultEnum checkPackEnum = packet.checkParameter();
		if(checkPackEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return checkPackEnum;
		}
		
		return ResultEnum.SUCCESS;
		
	}

	@Override
	public ResultEnum doBusiness() {
		logerror.info(lm.addMetaData("start", "start"));
		InventoryCallResult resp = null;
		try {
			//调用
			resp = goodsInventoryUpdateService.createInventory(
					clientIp, clientName, param, messageRoot);
			if(resp == null){
				return ResultEnum.SYS_ERROR;
			}
			if(!(resp.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
				return ResultEnum.getResultStatusEnum(String.valueOf(resp.getCode()));
			}
		} catch (Exception e) {
			logerror.error(lm.addMetaData("errMsg", e.getMessage()).addMetaData("result", resp).toJson(false),e);
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
		parameterMap.put("userId", userId);
		parameterMap.put("goodsId", goodsId);
		parameterMap.put("totalNumber", String.valueOf(totalNumber));
		parameterMap.put("leftNumber",  String.valueOf(leftNumber));
	    parameterMap.put("limitStorage",  String.valueOf(limitStorage));
		parameterMap.put("waterfloodVal",  String.valueOf(waterfloodVal));
		parameterMap.put("goodsSuppliers", CollectionUtils.isEmpty(goodsSuppliers)?"":LogUtil.formatListLog(goodsSuppliers));
		parameterMap.put("goodsSelection",  CollectionUtils.isEmpty(goodsSelection)?"":LogUtil.formatListLog(goodsSelection));
		packet.addParameterMap(parameterMap);
		super.init(packet.getClient(), packet.getIp());
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}
