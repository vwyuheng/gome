package com.tuan.inventory.service.impl;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.stylesheets.LinkStyle;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tuan.inventory.domain.repository.GoodTypeDomainRepository;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;
import com.tuan.inventory.model.param.CreaterInventoryParam;
import com.tuan.inventory.model.param.InventoryRecordParam;
import com.tuan.inventory.model.param.OverrideAdjustInventoryParam;
import com.tuan.inventory.model.param.UpdateWmsDataParam;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.notifyserver.core.cclient.ConsumerReceiver;
import com.tuan.notifyserver.core.connect.net.pojo.Message;

public class MessageNotifyCallBackServiceImpl extends AbstractService implements ConsumerReceiver {
	@Resource
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	@Resource
	private GoodTypeDomainRepository goodTypeDomainRepository;
	
	private final Log log=LogFactory.getLog("COMPENSATION.NOTIFY.LOG");
	private final String method = "MessageNotifyCallBackServiceImpl.receive";
	
	
	private final String clientIp="0.0.0.0";
	private final String clientName="notifyserver";
	@Override
	public boolean receive(Message message) {
		String method = "MessageNotifyCallBackServiceImpl.receive";
		final LogModel lm = LogModel.newLogModel();
		if(null == message){
			writeSysLog(lm.setMethod(method).addMetaData("resultCode", "参数无效").toJson());
			return false;
		}
		log.info(lm.setMethod(method).addMetaData("message", message.getContent()).toJson());
		InventoryRecordParam inventoryRecordParam=null;
		try {
			Type paramType = new TypeToken<InventoryRecordParam>(){}.getType();
			inventoryRecordParam = new Gson().fromJson(message.getContent(),
					paramType);
		} catch (JsonSyntaxException e) {
			writeSysLog(lm.setMethod(method).addMetaData("resultCode", "数据格式有误").toJson());
		}
		String tokenId = inventoryRecordParam.getTokenid();
		String goodsId = inventoryRecordParam.getGoods_id();
		String action=inventoryRecordParam.getActions();
		LinkedHashMap<String, String> data=inventoryRecordParam.getData();
		updateInventory(tokenId,goodsId,action,data,lm);
		return true;
	}
	
	
	
	/**库存调整
	 * @param tokenId 序列值
	 * @param goodsId
	 * @param action  操作类型
	 * @param data  元数据
	 */
	public  void updateInventory(String tokenId, String goodsId,String action,LinkedHashMap<String, String> data,LogModel lm){
		if("createstock".equals(action)){
			log.info(lm.setMethod(method).addMetaData("updatetraget", "createstock").toJson());
			boolean needUpdate = comparisonTokenid(tokenId, goodsId,
					DLockConstants.CREATE_INVENTORY,
					DLockConstants.CREATE_INVENTORY_SUCCESS,lm);
			if (needUpdate) {
				CreaterInventoryParam param= new CreaterInventoryParam();
				param.setGoodsId(goodsId);
				param.setLeftNumber(Integer.parseInt(data.get("leftNumber")));
				param.setLimitStorage(Integer.parseInt(data.get("limitStorage")));
				param.setTokenid(tokenId);
				param.setTotalNumber(Integer.parseInt(data.get("totalNumber")));
				param.setUserId(data.get("userId"));
				param.setWaterfloodVal(Integer.parseInt(data.get("waterfloodVal")));
				goodsInventoryUpdateService.createInventory(clientIp,clientName,param,null);
			}
		}
		//修改物流单的关系
		if("upwmsdata".equals(action)){
			log.info(lm.setMethod(method).addMetaData("updatetraget", "upwmsdata").toJson());
			boolean needUpdate = comparisonTokenid(tokenId, goodsId,
					DLockConstants.UPDATE_WMS_DATA,
					DLockConstants.UPDATE_WMS_DATA_SUCCESS,lm);
			log.info(lm.setMethod(method).addMetaData("isupdate", needUpdate).toJson());
			if (needUpdate) {
				UpdateWmsDataParam param= new UpdateWmsDataParam();
				param.setGoodsId(goodsId);
				param.setGoodsTypeIds(data.get("goodsTypeIds"));
				param.setIsBeDelivery(data.get("isBeDelivery"));
				param.setTokenid(tokenId);
				param.setWmsGoodsId(data.get("wmsGoodsId"));
				goodsInventoryUpdateService.updateWmsData(clientIp,clientName,param,null);
			}
		}
		//修改库存
		if("oradjusti".equals(action)){
			log.info(lm.setMethod(method).addMetaData("updatetraget", "oradjusti").toJson());
			boolean needUpdate = comparisonTokenid(tokenId, goodsId,
					DLockConstants.OVERRIDE_ADJUST_INVENTORY,
					DLockConstants.OVERRIDE_ADJUST_INVENTORY_SUCCESS,lm);
			log.info(lm.setMethod(method).addMetaData("isupdate", needUpdate).toJson());
			if (needUpdate) {
				OverrideAdjustInventoryParam param= new OverrideAdjustInventoryParam();
				param.setGoodsId(goodsId);
				param.setId(data.get("id"));
				param.setTokenid(tokenId);
				param.setTotalnum(Integer.parseInt(data.get("totalnum")));
				param.setType(data.get("type"));
				goodsInventoryUpdateService.overrideAdjustInventory(clientIp,clientName,param,null);
			}
		}
		//注水
		if("addsales".equals(action)){
			log.info(lm.setMethod(method).addMetaData("updatetraget", "addsales").toJson());
			AdjustWaterfloodParam adjustWaterfloodParam= new AdjustWaterfloodParam();
			adjustWaterfloodParam.setGoodsId(goodsId);
			String num= data.get("add_sales");;
			adjustWaterfloodParam.setNum(Integer.parseInt(num));
			goodsInventoryUpdateService.adjustmentWaterflood(clientIp,clientName,adjustWaterfloodParam,null);
		}
		
	}
	
	/**判断是否需要更新
	 * @param tokenid 序列值
	 * @param goodsId
	 * @param tokenKey redis存储的序列值
	 * @param successTokenKey 存储的序列值状态
	 * @return
	 */
	public boolean comparisonTokenid(String tokenid, String goodsId,
			String tokenKey, String successTokenKey,LogModel lm) {
		String redis_tokenid = goodsInventoryDomainRepository
				.queryToken(tokenKey + "_" + String.valueOf(goodsId));
		log.info(lm.setMethod(method).addMetaData("redis_tokenid", redis_tokenid).toJson());
		if (null == redis_tokenid) {
			return true;
		} else {
			if (Integer.valueOf(redis_tokenid) < Integer.valueOf(tokenid)) {
				return true;
			} else if (Integer.valueOf(redis_tokenid) == Integer
					.valueOf(tokenid)) {
				String gettag = goodsInventoryDomainRepository
						.queryToken(successTokenKey + "_"
								+ tokenid);
				if (!StringUtils.isEmpty(gettag)
						&& !gettag
								.equalsIgnoreCase(DLockConstants.HANDLER_SUCCESS)) {
					return true;
				}
			}
		}
		return false;
	}

	public GoodsInventoryUpdateService getGoodsInventoryUpdateService() {
		return goodsInventoryUpdateService;
	}


	public void setGoodsInventoryUpdateService(
			GoodsInventoryUpdateService goodsInventoryUpdateService) {
		this.goodsInventoryUpdateService = goodsInventoryUpdateService;
	}


	public GoodsInventoryDomainRepository getGoodsInventoryDomainRepository() {
		return goodsInventoryDomainRepository;
	}


	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}


	public GoodTypeDomainRepository getGoodTypeDomainRepository() {
		return goodTypeDomainRepository;
	}


	public void setGoodTypeDomainRepository(
			GoodTypeDomainRepository goodTypeDomainRepository) {
		this.goodTypeDomainRepository = goodTypeDomainRepository;
	}
	
	
}
