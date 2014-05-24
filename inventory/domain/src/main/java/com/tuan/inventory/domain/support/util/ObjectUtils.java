package com.tuan.inventory.domain.support.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
import com.tuan.inventory.model.GoodsSelectionAndSuppliersModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.WmsIsBeDeliveryModel;
import com.tuan.inventory.model.param.SelectionNotifyMessageParam;
import com.tuan.inventory.model.param.SuppliersNotifyMessageParam;

public class ObjectUtils {
	
	
	public static List<SelectionNotifyMessageParam> toSelectionMsgList(List<GoodsSelectionModel> list) {
		List<SelectionNotifyMessageParam> result = null;
			if(!CollectionUtils.isEmpty(list)){
				result =  new ArrayList<SelectionNotifyMessageParam>();
				for(GoodsSelectionModel element:list) {
					     result.add(toSelectionNotifyMessage(element));
				}
				
			}
		return result;
	}
	
	public static List<SuppliersNotifyMessageParam> toSuppliersMsgList(List<GoodsSuppliersModel> list) {
		List<SuppliersNotifyMessageParam> result = null;
			if(!CollectionUtils.isEmpty(list)){
				result =  new ArrayList<SuppliersNotifyMessageParam>();
				for(GoodsSuppliersModel element:list) {
					     result.add(toSuppliersNotifyMessage(element));
				}
				
			}
		return result;
	}
	
	
	public static SelectionNotifyMessageParam toSelectionNotifyMessage(GoodsSelectionModel selModel) {
		SelectionNotifyMessageParam result = null;
		if(selModel!=null) {
			result = new SelectionNotifyMessageParam();
			result.setId(selModel.getId());
			result.setGoodsId(selModel.getGoodsId());
			//result.setGoodTypeId(selModel.getGoodTypeId());
			result.setLeftNumber(selModel.getLeftNumber());
			result.setTotalNumber(selModel.getTotalNumber());
			//result.setSuppliersInventoryId(selModel.getSuppliersInventoryId());
			result.setLimitStorage(selModel.getLimitStorage());
			result.setUserId(selModel.getUserId());
			int sales = selModel.getTotalNumber() - selModel.getLeftNumber();
			result.setSales(String.valueOf(sales));
			result.setWaterfloodVal(selModel.getWaterfloodVal());
			result.setWmsGoodsId(selModel.getWmsGoodsId());
		}
		return result;
	}
	public static SuppliersNotifyMessageParam toSuppliersNotifyMessage(GoodsSuppliersModel supModel) {
		SuppliersNotifyMessageParam result = null;
		if(supModel!=null) {
			result = new SuppliersNotifyMessageParam();
			result.setId(supModel.getSuppliersId());  //存分店商品的id
			result.setGoodsId(supModel.getGoodsId());
			//result.setSuppliersId(supModel.getSuppliersId());
			result.setLeftNumber(supModel.getLeftNumber());
			result.setTotalNumber(supModel.getTotalNumber());
			result.setLimitStorage(supModel.getLimitStorage());
			result.setUserId(supModel.getUserId());
			result.setWaterfloodVal(supModel.getWaterfloodVal());
			int sales = supModel.getTotalNumber() - supModel.getLeftNumber();
			result.setSales(String.valueOf(sales));
		}
		return result;
	}
	
	
	public static GoodsInventoryWMSDO toWmsDO(GoodsSelectionModel selModel) {
		GoodsInventoryWMSDO result = null;
		if(selModel!=null) {
			result = new GoodsInventoryWMSDO();
			result.setWmsGoodsId(selModel.getWmsGoodsId());
			result.setLeftNumber(selModel.getLeftNumber());
			result.setTotalNumber(selModel.getTotalNumber());
			
		}
		return result;
	}
	public static GoodsSelectionDO toSelectionDO(GoodsSelectionModel selModel) {
		GoodsSelectionDO result = null;
		if(selModel!=null) {
			result = new GoodsSelectionDO();
			result.setId(selModel.getId());
			result.setGoodsId(selModel.getGoodsId());
			result.setGoodTypeId(selModel.getGoodTypeId());
			result.setLeftNumber(selModel.getLeftNumber());
			result.setTotalNumber(selModel.getTotalNumber());
			result.setSuppliersInventoryId(selModel.getSuppliersInventoryId());
			result.setSuppliersId(selModel.getSuppliersId());
			result.setSuppliersSubId(selModel.getSuppliersSubId());
			result.setLimitStorage(selModel.getLimitStorage());
			result.setUserId(selModel.getUserId());
			result.setWaterfloodVal(selModel.getWaterfloodVal());
		}
		return result;
	}
	public static GoodsSelectionModel toModel(GoodsSelectionDO gsDO) {
		GoodsSelectionModel result = null;
		if(gsDO!=null) {
			result = new GoodsSelectionModel();
			result.setId(gsDO.getId());
			result.setGoodsId(gsDO.getGoodsId());
			result.setSuppliersId(gsDO.getSuppliersId());
			result.setGoodTypeId(gsDO.getGoodTypeId());
			result.setLeftNumber(gsDO.getLeftNumber());
			result.setTotalNumber(gsDO.getTotalNumber());
			result.setSuppliersInventoryId(gsDO.getSuppliersInventoryId());
			result.setSuppliersSubId(gsDO.getSuppliersSubId());
			result.setLimitStorage(gsDO.getLimitStorage());
			result.setUserId(gsDO.getUserId());
			result.setWaterfloodVal(gsDO.getWaterfloodVal());
		}
		return result;
	}
	
	public static GoodsSuppliersDO toSuppliersDO(GoodsSuppliersModel supModel) {
		GoodsSuppliersDO result = null;
		if(supModel!=null) {
			result = new GoodsSuppliersDO();
			result.setId(supModel.getId());
			result.setGoodsId(supModel.getGoodsId());
			result.setSuppliersId(supModel.getSuppliersId());
			result.setLeftNumber(supModel.getLeftNumber());
			result.setTotalNumber(supModel.getTotalNumber());
			result.setLimitStorage(supModel.getLimitStorage());
			result.setUserId(supModel.getUserId());
			result.setWaterfloodVal(supModel.getWaterfloodVal());
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
			result.setGoodsSelectionIds(giDO.getGoodsSelectionIds());
			result.setLeftNumber(giDO.getLeftNumber());
			result.setTotalNumber(giDO.getTotalNumber());
			result.setLimitStorage(giDO.getLimitStorage());
			result.setUserId(giDO.getUserId());
			result.setIsAddGoodsSelection(giDO.getIsAddGoodsSelection());
			result.setIsDirectConsumption(giDO.getIsDirectConsumption());
			result.setWaterfloodVal(giDO.getWaterfloodVal());
			if(!CollectionUtils.isEmpty(goodsSelectionList))
			     result.setGoodsSelectionList(goodsSelectionList);
			if(!CollectionUtils.isEmpty(goodsSuppliersList))
			     result.setGoodsSuppliersList(goodsSuppliersList);
		}
		return result;
	}
	//只返回商品库存信息
	public static GoodsInventoryModel toModel(GoodsInventoryDO giDO) {
		GoodsInventoryModel result = null;
		if(giDO!=null) {
			result = new GoodsInventoryModel();
			result.setGoodsId(giDO.getGoodsId());
			result.setLeftNumber(giDO.getLeftNumber());
			result.setTotalNumber(giDO.getTotalNumber());
			result.setLimitStorage(giDO.getLimitStorage());
			result.setUserId(giDO.getUserId());
			result.setWaterfloodVal(giDO.getWaterfloodVal());
			//if(!CollectionUtils.isEmpty(goodsSelectionList))
				//result.setGoodsSelectionList(goodsSelectionList);
			//if(!CollectionUtils.isEmpty(goodsSuppliersList))
			//	result.setGoodsSuppliersList(goodsSuppliersList);
		}
		return result;
	}
	//
	public static WmsIsBeDeliveryModel toModel(GoodsInventoryWMSDO wmsDO) {
		WmsIsBeDeliveryModel result = null;
		if(wmsDO!=null) {
			result = new WmsIsBeDeliveryModel();
			result.setId(wmsDO.getId());
			result.setLeftNumber(wmsDO.getLeftNumber());
			result.setTotalNumber(wmsDO.getTotalNumber());
			result.setIsBeDelivery(wmsDO.getIsBeDelivery());
			result.setGoodsName(wmsDO.getGoodsName());
			result.setGoodsSupplier(wmsDO.getGoodsSupplier());
			result.setWmsGoodsId(wmsDO.getWmsGoodsId());
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
//				GoodsInventoryQueueModel memberDO = (GoodsInventoryQueueModel) LogUtil.jsonToObject(member, GoodsInventoryQueueModel.class);
				GoodsInventoryQueueModel memberDO = JsonUtils.convertStringToObject(member, GoodsInventoryQueueModel.class);
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
	

	@SuppressWarnings("rawtypes")
	public static Map<String,String> toHashMap(Object object) {
		Map<String,String> data = new HashMap<String, String>();
		  JSONObject jsonObject = toJSONObject(object);
		  Iterator it = jsonObject.keys();
		  while (it.hasNext()) {
		   String key = String.valueOf(it.next());
		   String value = jsonObject.get(key).toString();
		   data.put(key, value);
		  }

		  return data;
		 }

	private static JSONObject toJSONObject(Object object) {
		  return JSONObject.fromObject(object);
		 }
}
