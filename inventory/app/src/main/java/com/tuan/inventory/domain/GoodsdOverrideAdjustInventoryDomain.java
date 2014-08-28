package com.tuan.inventory.domain;

import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.model.param.OverrideAdjustInventoryParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsdOverrideAdjustInventoryDomain extends AbstractGoodsInventoryDomain{
	private String tokenid;  //redis序列,解决接口幂等问题
	//商品id
	private String goodsId;
	//type 2:商品id，4:选型id，6:分店id
	private String id;
	private String userId;
	//2:商品调整，4.选型库存调整 6.分店库存调整
	private String type;
	private String limitStorage;
	private int totalnum;
	private String goodsBaseId;
	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	private OverrideAdjustInventoryParam param;
	private UpdateRequestPacket packet;
	private static Log logerror = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	public GoodsdOverrideAdjustInventoryDomain(UpdateRequestPacket packet,String tokenid,String goodsId,String id,String userId,String type,String limitStorage,String totalnum,LogModel lm,Message messageRoot,String goodsBaseId){
		this.packet = packet;
		this.tokenid = JsonStrVerificationUtils.validateStr(tokenid);
		this.goodsId = goodsId;
		this.goodsBaseId = goodsBaseId;
		this.id = id;
		this.userId = userId;
		this.type = type;
		this.limitStorage = JsonStrVerificationUtils.validateStr(limitStorage);
		this.totalnum = StringUtils.isEmpty(totalnum)?0:Integer.valueOf(totalnum);
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillAdjustIParam();
		
	}
	
	public OverrideAdjustInventoryParam fillAdjustIParam() {
		OverrideAdjustInventoryParam param = new OverrideAdjustInventoryParam();
		param.setTokenid(tokenid);
		param.setGoodsId(goodsId);
		param.setId(id);
		param.setUserId(userId);
		param.setType(type);
		param.setLimitStorage(Integer.valueOf(limitStorage));
		param.setTotalnum(totalnum);
		param.setGoodsBaseId(goodsBaseId);
		return param;
	}
	@Override
	public ResultEnum checkParameter() {
		if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(goodsId))){
			return ResultEnum.INVALID_GOODSID;
		}
		if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(type))){
			return ResultEnum.INVALID_INVENTORY_TYPE;
		}
		if(StringUtils.isEmpty(limitStorage)){
			return ResultEnum.INVALID_ISLIMIT_STORAGE;
		}
		if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())&&StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(id))) {
			return ResultEnum.INVALID_SELECTIONID;
		}
	    if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())&&StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(id))) {
			return ResultEnum.INVALID_SUPPLIERSID;
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
			InventoryCallResult resp = goodsInventoryUpdateService.overrideAdjustInventory(
					clientIp, clientName, param, messageRoot);
			if(resp == null){
				return ResultEnum.SYS_ERROR;
			}
			if(!(resp.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
				return ResultEnum.getResultStatusEnum(String.valueOf(resp.getCode()));
			}
		} catch (Exception e) {
			logerror.error(lm.addMetaData("errMsg", "GoodsdAdjustInventoryDomain.doBusiness error"+e.getMessage()).toJson(false),e);
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
		lm.addMetaData("全量调整库存响应结果", JSON.toJSONString(resp));
		logerror.info(lm.toJson(false));
		return resp;
	}
	
	@Override
	public void makeParameterMap(SortedMap<String, String> parameterMap) {
		parameterMap.put("tokenid", tokenid);
		parameterMap.put("id", id);
		parameterMap.put("userId", userId);
		parameterMap.put("type", type);
		parameterMap.put("limitStorage", limitStorage);
		parameterMap.put("goodsBaseId", goodsBaseId);
		parameterMap.put("totalnum", String.valueOf(totalnum));
		super.init(packet.getClient(), packet.getIp());
		packet.addParameterMap(parameterMap);
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}
