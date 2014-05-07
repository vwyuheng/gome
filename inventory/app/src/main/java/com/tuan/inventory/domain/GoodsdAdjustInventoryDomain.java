package com.tuan.inventory.domain;

import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.domain.base.AbstractGoodsInventoryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.result.InventoryCallResult;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.JsonStrVerificationUtils;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;

public class GoodsdAdjustInventoryDomain extends AbstractGoodsInventoryDomain{
	//商品id
	private String goodsId;
	//type 2:商品id，4:选型id，6:分店id
	private String id;
	private String userId;
	//2:商品调整，4.选型库存调整 6.分店库存调整
	private String type;
	private int num;

	private LogModel lm;
	private Message messageRoot;
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	private AdjustInventoryParam param;
	private UpdateRequestPacket packet;
	//private static Logger logger = Logger.getLogger(GoodsdAdjustInventoryDomain.class);
	private static Log logger = LogFactory.getLog(GoodsdAdjustInventoryDomain.class);
	public GoodsdAdjustInventoryDomain(UpdateRequestPacket packet,String goodsId,String id,String userId,String type,String num,LogModel lm,Message messageRoot){
		this.packet = packet;
		this.goodsId = goodsId;
		this.id = id;
		this.userId = userId;
		this.type = type;
		this.num = StringUtils.isEmpty(num)?0:Integer.valueOf(num);
		this.lm = lm;
		this.messageRoot = messageRoot;
		makeParameterMap(this.parameterMap);
		this.param = this.fillAdjustIParam();
		
	}
	
	public AdjustInventoryParam fillAdjustIParam() {
		AdjustInventoryParam param = new AdjustInventoryParam();
		param.setGoodsId(goodsId);
		param.setId(id);
		param.setUserId(userId);
		param.setType(type);
		param.setNum(num);
		return param;
	}
	@Override
	public ResultEnum checkParameter() {
		if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(goodsId))){
			return ResultEnum.INVALID_ADJUST_ID;
		}
		if(StringUtils.isEmpty(JsonStrVerificationUtils.validateStr(type))){
			return ResultEnum.INVALID_INVENTORY_TYPE;
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
			InventoryCallResult resp = goodsInventoryUpdateService.adjustmentInventory(
					clientIp, clientName, param, messageRoot);
			if(resp == null){
				return ResultEnum.ERROR_2000;
			}
			if(!(resp.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
				return ResultEnum.getResultStatusEnum(String.valueOf(resp.getCode()));
			}
		} catch (Exception e) {
			logger.error(lm.setMethod("GoodsdAdjustInventoryDomain.doBusiness").addMetaData("errMsg", e.getMessage()).toJson(),e);
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
		parameterMap.put("id", id);
		parameterMap.put("userId", userId);
		parameterMap.put("type", type);
		parameterMap.put("num", String.valueOf(num));
		super.init(packet.getClient(), packet.getIp());
		packet.addParameterMap(parameterMap);
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}

	
	
	
}
