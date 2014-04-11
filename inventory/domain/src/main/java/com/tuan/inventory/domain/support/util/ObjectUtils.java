package com.tuan.inventory.domain.support.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
import com.tuan.inventory.model.GoodsSelectionAndSuppliersModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;

public class ObjectUtils {

	public static GoodsSelectionModel toModel(GoodsSelectionDO gsDO) {
		GoodsSelectionModel result = null;
		if(gsDO!=null) {
			result = new GoodsSelectionModel();
			result.setId(gsDO.getId());
			result.setGoodsId(gsDO.getGoodsId());
			result.setGoodTypeId(gsDO.getGoodTypeId());
			result.setLeftNumber(gsDO.getLeftNumber());
			result.setTotalNumber(gsDO.getTotalNumber());
			result.setSuppliersInventoryId(gsDO.getSuppliersInventoryId());
			result.setLimitStorage(gsDO.getLimitStorage());
			result.setUserId(gsDO.getUserId());
			result.setWaterfloodVal(gsDO.getWaterfloodVal());
		}
		return result;
	}
	
	public static GoodsSuppliersModel toModel(GoodsSuppliersDO gsDO) {
		GoodsSuppliersModel result = null;
		if(gsDO!=null) {
			result = new GoodsSuppliersModel();
			result.setId(gsDO.getId());
			result.setGoodsId(gsDO.getGoodsId());
			result.setSuppliersId(gsDO.getSuppliersId());
			result.setLeftNumber(gsDO.getLeftNumber());
			result.setTotalNumber(gsDO.getTotalNumber());
			result.setLimitStorage(gsDO.getLimitStorage());
			result.setUserId(gsDO.getUserId());
			result.setWaterfloodVal(gsDO.getWaterfloodVal());
		}
		return result;
	}
	
	public static GoodsInventoryModel toModel(GoodsInventoryDO giDO,List<GoodsSelectionModel> goodsSelectionList,List<GoodsSuppliersModel> goodsSuppliersList) {
		GoodsInventoryModel result = null;
		if(giDO!=null) {
			result = new GoodsInventoryModel();
			result.setGoodsId(giDO.getGoodsId());
			result.setLeftNumber(giDO.getLeftNumber());
			result.setTotalNumber(giDO.getTotalNumber());
			result.setLimitStorage(giDO.getLimitStorage());
			result.setUserId(giDO.getUserId());
			result.setWaterfloodVal(giDO.getWaterfloodVal());
			if(!CollectionUtils.isEmpty(goodsSelectionList))
			     result.setGoodsSelectionList(goodsSelectionList);
			if(!CollectionUtils.isEmpty(goodsSuppliersList))
			     result.setGoodsSuppliersList(goodsSuppliersList);
		}
		return result;
	}
	
	
	/**
	 * 用于返回队列列表信息
	 * @param members
	 * @return
	 */
	public static List<GoodsInventoryQueueModel> convertSet(Set<String> members/*,String score*/) {
		List<GoodsInventoryQueueModel> result = null;
		if (!CollectionUtils.isEmpty(members)) {
			result = new ArrayList<GoodsInventoryQueueModel>();
			for(String member:members) {
				GoodsInventoryQueueModel memberDO = (GoodsInventoryQueueModel) LogUtil.jsonToObject(member, GoodsInventoryQueueModel.class);
				result.add(memberDO);
			}
		}
		return result;
	}
	
	public static List<GoodsInventoryActionModel> getList(String element) {
		List<GoodsInventoryActionModel> result = null;
		if(StringUtils.isNotEmpty(element)){
			//if(!CollectionUtils.isEmpty(elements)){
				result =  new ArrayList<GoodsInventoryActionModel>();
				//for(String element:elements) {
				GoodsInventoryActionModel tmpResult = JsonUtils.convertStringToObject(element, GoodsInventoryActionModel.class);
					if(tmpResult!=null)
					     result.add(tmpResult);
				//}
				
			}
		return result;
	}
	
	public static GoodsInventoryQueueModel toModel(GoodsInventoryQueueDO queueDO) {
		GoodsInventoryQueueModel result = null;
		if(queueDO!=null) {
			result = new GoodsInventoryQueueModel();
			result.setId(queueDO.getId());
			result.setGoodsId(queueDO.getGoodsId());
			result.setUserId(queueDO.getUserId());
			result.setOrderId(queueDO.getOrderId());
			result.setDeductNum(queueDO.getDeductNum());
			result.setOriginalGoodsInventory(queueDO.getOriginalGoodsInventory());
			List<GoodsSelectionAndSuppliersModel> selectionParam = null;
			if(!CollectionUtils.isEmpty(queueDO.getSelectionParam())) {
				selectionParam = new ArrayList<GoodsSelectionAndSuppliersModel>();
				List<GoodsSelectionAndSuppliersResult> getSelectionParam = queueDO.getSelectionParam();
				for(GoodsSelectionAndSuppliersResult gresult:getSelectionParam) {
					GoodsSelectionAndSuppliersModel gmodel = JsonUtils.convertStringToObject(JSONObject.fromObject(gresult).toString(), GoodsSelectionAndSuppliersModel.class);
					if(gmodel!=null)
					    selectionParam.add(gmodel);
				}
			}
			List<GoodsSelectionAndSuppliersModel> suppliersParam = null;
			if(!CollectionUtils.isEmpty(queueDO.getSuppliersParam())) {
				suppliersParam = new ArrayList<GoodsSelectionAndSuppliersModel>();
				List<GoodsSelectionAndSuppliersResult> getSuppliersParam = queueDO.getSuppliersParam();
				for(GoodsSelectionAndSuppliersResult gresult:getSuppliersParam) {
					GoodsSelectionAndSuppliersModel gmodel = JsonUtils.convertStringToObject(JSONObject.fromObject(gresult).toString(), GoodsSelectionAndSuppliersModel.class);
					if(gmodel!=null)
						suppliersParam.add(gmodel);
				}
			}
			result.setSelectionParam(selectionParam);
			result.setSuppliersParam(suppliersParam);
			
		}
		return result;
	}
	
	public static GoodsInventoryActionDO toDO(GoodsInventoryActionModel model) {
		GoodsInventoryActionDO result = null;
		if(model!=null) {
			result = new GoodsInventoryActionDO();
			result.setId(model.getId());
			result.setGoodsId(model.getGoodsId());
			result.setActionType(model.getActionType());
			result.setBusinessType(model.getBusinessType());
			result.setClientIp(model.getClientIp());
			result.setClientName(model.getClientName());
			result.setUserId(model.getUserId());
			result.setContent(model.getContent());
			result.setCreateTime(model.getCreateTime());
			result.setInventoryChange(model.getInventoryChange());
			result.setOrderId(model.getOrderId());
			result.setItem(model.getItem());
			result.setOriginalInventory(model.getOriginalInventory());
			result.setRemark(model.getRemark());
		}
		return result;
	}
}
