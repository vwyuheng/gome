package com.tuan.inventory.controller;

import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuan.inventory.domain.GoodsCreateInventoryDomain;
import com.tuan.inventory.domain.GoodsWmsInventoryAdjustDomain;
import com.tuan.inventory.domain.GoodsWmsInventoryCreateDomain;
import com.tuan.inventory.domain.GoodsdAckInventoryDomain;
import com.tuan.inventory.domain.GoodsdAdjustInventoryDomain;
import com.tuan.inventory.domain.GoodsdAdjustWaterfloodDomain;
import com.tuan.inventory.domain.GoodsdOverrideAdjustInventoryDomain;
import com.tuan.inventory.domain.GoodsdUpdateInventoryDomain;
import com.tuan.inventory.domain.UpdateWmsDataDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.param.rest.CreaterInventoryRestParam;
import com.tuan.inventory.model.param.rest.RestTestParam;
import com.tuan.inventory.model.param.rest.TestParam;
import com.tuan.inventory.model.param.rest.UpdateInventoryRestParam;
import com.tuan.inventory.model.param.rest.WmsInventoryRestParam;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageTypeEnum;

/**
 * @description <p>
 *              库存更新控制器
 *              </p>
 * @author henry.yu
 * @date 2014.4.15
 */
@Controller
@RequestMapping("/update")
public class GoodsInventoryUpdateController {
	@Resource
	private GoodsInventoryUpdateService goodsInventoryUpdateService;
	/**
	 * http://localhost:882/rest/j/update/create?&ip==127.0.0.1&client=ordercenter&t=123456789&tokenid=1&goodsId=2&userId=2&totalNumber=1000&leftNumber=1000&limitStorage=1&waterfloodVal=100&goodsSelection=[{"goodTypeId":0,"goodsId":2,"id":16,"leftNumber":50,"limitStorage":1,"num":0,"suppliersInventoryId":0,"totalNumber":50,"userId":2,"waterfloodVal":20},{"goodTypeId":0,"goodsId":2,"id":17,"leftNumber":50,"limitStorage":1,"num":0,"suppliersInventoryId":0,"totalNumber":50,"userId":2,"waterfloodVal":20}]&goodsSuppliers=[{"goodsId":2,"id":15,"leftNumber":50,"limitStorage":1,"num":0,"suppliersId":0,"totalNumber":50,"userId":2,"waterfloodVal":20},{"goodsId":2,"id":16,"leftNumber":50,"limitStorage":1,"num":0,"suppliersId":0,"totalNumber":50,"userId":2,"waterfloodVal":20}]
	 * 创建商品库存信息,包括其下的选型及分店
	 * @param packet
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp createInventory(@ModelAttribute UpdateRequestPacket packet,
			@ModelAttribute CreaterInventoryRestParam param, HttpServletRequest request) {

		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"createInventory");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/create")
		.addMetaData("RequestPacket", packet)
		.addMetaData("param", param);
		GoodsCreateInventoryDomain createInventoryDomain = new GoodsCreateInventoryDomain(
				packet, param, lm, messageRoot);
		createInventoryDomain
				.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = createInventoryDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return createInventoryDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = createInventoryDomain.doBusiness();
		// 返回结果
		return createInventoryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/update/up?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2&userId=2&orderId=3&num=1&goodsSelection=[{"stNum":1,"selectionId":16,"wmsGoodsId":"T01001002004"},{"stNum":1,"selectionId":17}]&goodsSuppliers=[{"ssNum":1,"suppliersId":15},{"ssNum":1,"suppliersId":16}]
	 * 库存扣减
	 * @param packet
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/up", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp updateInventory(@ModelAttribute UpdateRequestPacket packet,
			@ModelAttribute UpdateInventoryRestParam param, HttpServletRequest request) {

		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"createInventory");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/up")
		.addMetaData("RequestPacket", packet)
		.addMetaData("param", param);
		GoodsdUpdateInventoryDomain updateInventoryDomain = new GoodsdUpdateInventoryDomain(
				packet, param, lm, messageRoot);
		updateInventoryDomain
				.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = updateInventoryDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return updateInventoryDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = updateInventoryDomain.doBusiness();
		// 返回结果
		return updateInventoryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/update/ack?&ip==127.0.0.1&client=ordercenter&t=123456789&ack=1&key=4062
	 * 库存确认回调接口
	 * @param packet
	 * @param ack
	 * @param key
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/ack", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp callbackAckInventory(@ModelAttribute UpdateRequestPacket packet,
			String ack, String key, HttpServletRequest request) {
		
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"callbackAckInventory");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/ack")
		.addMetaData("RequestPacket", packet)
		.addMetaData("ack", ack).addMetaData("key", key);
		GoodsdAckInventoryDomain ackInventoryDomain = new GoodsdAckInventoryDomain(
				packet, ack,key, lm, messageRoot);
		ackInventoryDomain
		.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = ackInventoryDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return ackInventoryDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = ackInventoryDomain.doBusiness();
		// 返回结果
		return ackInventoryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/update/adjusti?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2&id=0&num=-1&type=2
	 * 库存调整
	 * @param packet
	 * @param goodsId 
	 * @param id 
	 * @param userId
	 * @param type
	 * @param num
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/adjusti", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp adjustmentInventory(@ModelAttribute UpdateRequestPacket packet,
			String goodsId,String id, String userId,String type, String num, String goodsBaseId, HttpServletRequest request) {
		
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"adjustmentInventory");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/adjusti")
		.addMetaData("RequestPacket", packet)
		.addMetaData("goodsId", goodsId)
		.addMetaData("id", id)
		.addMetaData("userId", userId)
		.addMetaData("type", type)
		.addMetaData("goodsBaseId", goodsBaseId)
		.addMetaData("num", num);
		GoodsdAdjustInventoryDomain adjustInventoryDomain = new GoodsdAdjustInventoryDomain(
				packet,goodsId, id,userId,type,num, lm, messageRoot,goodsBaseId);
		adjustInventoryDomain
		.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = adjustInventoryDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return adjustInventoryDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = adjustInventoryDomain.doBusiness();
		// 返回结果
		return adjustInventoryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/update/adjustw?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2&id=0&num=-1&type=2
	 * 注水值调整接口
	 * @param packet
	 * @param id
	 * @param userId
	 * @param type
	 * @param num
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/adjustw", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp adjustmentWaterflood(@ModelAttribute UpdateRequestPacket packet,
			String goodsId,String id, String userId,String type, String num, HttpServletRequest request,String goodsBaseId) {
		
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"adjustmentWaterflood");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/adjustw")
		.addMetaData("RequestPacket", packet)
		.addMetaData("goodsId", goodsId)
		.addMetaData("id", id)
		.addMetaData("userId", userId)
		.addMetaData("type", type)
		.addMetaData("goodsBaseId", goodsBaseId)
		.addMetaData("num", num);
		GoodsdAdjustWaterfloodDomain adjustWaterfloodDomain = new GoodsdAdjustWaterfloodDomain(
				packet,goodsId,id,userId,type,num, lm, messageRoot,goodsBaseId);
		adjustWaterfloodDomain
		.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = adjustWaterfloodDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return adjustWaterfloodDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = adjustWaterfloodDomain.doBusiness();
		// 返回结果
		return adjustWaterfloodDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/update/createwms?&ip==127.0.0.1&client=ordercenter&t=123456789&id=1&wmsGoodsId=1&goodsSupplier=test&goodsName=55t&totalNumber=1000&leftNumber=800&isBeDelivery=1&goodsSelection=[{"goodTypeId":18,"id":18,"leftNumber":50,"totalNumber":50,"limitStorage":1}]
	 * 物流库存创建接口
	 * @param packet
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/createwms", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp createWmsInventory(@ModelAttribute UpdateRequestPacket packet,
			@ModelAttribute WmsInventoryRestParam param, HttpServletRequest request) {

		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"createWmsInventory");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/createwms")
		.addMetaData("RequestPacket", packet)
		.addMetaData("param", param);
		
		GoodsWmsInventoryCreateDomain createWmsDomain = new GoodsWmsInventoryCreateDomain(
				packet, param, lm, messageRoot);
		createWmsDomain
				.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = createWmsDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return createWmsDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = createWmsDomain.doBusiness();
		// 返回结果
		return createWmsDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/update/adjustwms?&ip==127.0.0.1&client=ordercenter&t=123456789&id=1&wmsGoodsId=1&isBeDelivery=1&num=1&goodsIds=[173552,217335]&goodsSelection=[{"goodTypeId":18,"id":18,"num":1}]
	 * 物流库存调整接口
	 * @param packet
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/adjustwms", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp adjustWmsInventory(@ModelAttribute UpdateRequestPacket packet,
			@ModelAttribute WmsInventoryRestParam param, HttpServletRequest request) {

		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"adjustWmsInventory");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/adjustwms")
		.addMetaData("RequestPacket", packet)
		.addMetaData("param", param);
		
		GoodsWmsInventoryAdjustDomain adjustWmsDomain = new GoodsWmsInventoryAdjustDomain(
				packet, param, lm, messageRoot);
		adjustWmsDomain
				.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = adjustWmsDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return adjustWmsDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = adjustWmsDomain.doBusiness();
		// 返回结果
		return adjustWmsDomain.makeResult(resEnum);
	}
	/***
	 * http://localhost:882/rest/j/update/oradjusti?&ip==127.0.0.1&client=ordercenter&t=123456789&tokenid=1&goodsId=1&id=0&totalnum=-1&type=2
	 * 覆盖更新库存量，包括总量和剩余量
	 * @param packet
	 * @param goodsId
	 * @param id
	 * @param userId
	 * @param type
	 * @param totalnum
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/oradjusti", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp overrideAdjustInventory(@ModelAttribute UpdateRequestPacket packet,
			String tokenid,String goodsId,String id, String userId,String type, String totalnum, HttpServletRequest request,String goodsBaseId) {
		
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"overrideAdjustInventory");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/oradjusti")
		.addMetaData("RequestPacket", packet)
		.addMetaData("tokenid", tokenid)
		.addMetaData("goodsId", goodsId)
		.addMetaData("id", id)
		.addMetaData("userId", userId)
		.addMetaData("type", type)
		.addMetaData("goodsBaseId", goodsBaseId)
		.addMetaData("totalnum", totalnum);
		GoodsdOverrideAdjustInventoryDomain adjustInventoryDomain = new GoodsdOverrideAdjustInventoryDomain(
				packet,tokenid,goodsId, id,userId,type,totalnum, lm, messageRoot,goodsBaseId);
		adjustInventoryDomain
		.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = adjustInventoryDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return adjustInventoryDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = adjustInventoryDomain.doBusiness();
		// 返回结果
		return adjustInventoryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/update/upwmsdata?&ip==127.0.0.1&client=ordercenter&t=123456789&tokenid=1&goodsId=187237&wmsGoodsId=T01000000010&isBeDelivery=1&suppliersId=1&goodsTypeIds=173552,217335&goodsSelectionIds=1,2,3
	 * 物流关系数据更新接口
	 * @param packet
	 * @param goodsId
	 * @param suppliersId
	 * @param wmsGoodsId
	 * @param goodsTypeIds
	 * @param goodsSelectionIds
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/upwmsdata", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp updateWmsData(@ModelAttribute UpdateRequestPacket packet,
			String tokenid,String goodsId,String suppliersId, String wmsGoodsId,String isBeDelivery,String goodsTypeIds, String goodsSelectionIds, String goodsBaseId, HttpServletRequest request) {
		
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"updateWmsData");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/upwmsdata")
		.addMetaData("RequestPacket", packet)
		.addMetaData("tokenid", tokenid)
		.addMetaData("goodsId", goodsId)
		.addMetaData("suppliersId", suppliersId)
		.addMetaData("wmsGoodsId", wmsGoodsId)
		.addMetaData("goodsTypeIds", goodsTypeIds)
		.addMetaData("goodsSelectionIds", goodsSelectionIds)
		.addMetaData("goodBaseId", goodsBaseId);
		UpdateWmsDataDomain upWmsDataDomain = new UpdateWmsDataDomain(
				packet,tokenid,goodsId, suppliersId,wmsGoodsId,isBeDelivery,goodsTypeIds,goodsSelectionIds, lm, messageRoot,goodsBaseId);
		upWmsDataDomain
		.setGoodsInventoryUpdateService(goodsInventoryUpdateService);
		// 接口参数校验
		ResultEnum resEnum = upWmsDataDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return upWmsDataDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = upWmsDataDomain.doBusiness();
		// 返回结果
		return upWmsDataDomain.makeResult(resEnum);
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public void test(@ModelAttribute UpdateRequestPacket packet,
			@ModelAttribute RestTestParam param, HttpServletRequest request) {
		  Type type = new TypeToken<List<TestParam>>(){}.getType();
		  String jsonResult = param.getGoodsSelection();
		  @SuppressWarnings("unchecked")
		List<TestParam> tet1= (List<TestParam>)new Gson().fromJson(jsonResult, type);
		System.out.println("packet="+packet);
		System.out.println("param="+param);
		System.out.println("tet1="+tet1);
		
	}
}
