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
import com.tuan.inventory.domain.GoodsdAckInventoryDomain;
import com.tuan.inventory.domain.GoodsdAdjustInventoryDomain;
import com.tuan.inventory.domain.GoodsdAdjustWaterfloodDomain;
import com.tuan.inventory.domain.GoodsdUpdateInventoryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.param.rest.CreaterInventoryRestParam;
import com.tuan.inventory.model.param.rest.RestTestParam;
import com.tuan.inventory.model.param.rest.TestParam;
import com.tuan.inventory.model.param.rest.UpdateInventoryRestParam;
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
	 * http://localhost:882/rest/j/update/create?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2&userId=2&totalNumber=1000&leftNumber=1000&limitStorage=1&waterfloodVal=100&goodsSelection=[{"goodTypeId":0,"goodsId":2,"id":16,"leftNumber":50,"limitStorage":1,"num":0,"suppliersInventoryId":0,"totalNumber":50,"userId":2,"waterfloodVal":20},{"goodTypeId":0,"goodsId":2,"id":17,"leftNumber":50,"limitStorage":1,"num":0,"suppliersInventoryId":0,"totalNumber":50,"userId":2,"waterfloodVal":20}]&goodsSuppliers=[{"goodsId":2,"id":15,"leftNumber":50,"limitStorage":1,"num":0,"suppliersId":0,"totalNumber":50,"userId":2,"waterfloodVal":20},{"goodsId":2,"id":16,"leftNumber":50,"limitStorage":1,"num":0,"suppliersId":0,"totalNumber":50,"userId":2,"waterfloodVal":20}]
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
	 * http://localhost:882/rest/j/update/up?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2&userId=2&orderId=3&num=1&goodsSelection=[{"stNum":1,"selectionId":16},{"stNum":1,"selectionId":17}]&goodsSuppliers=[{"ssNum":1,"suppliersId":15},{"ssNum":1,"suppliersId":16}]
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
	 * http://localhost:882/rest/j/update/adjusti?&ip==127.0.0.1&client=ordercenter&t=123456789&id=2&num=-1&type=2
	 * 库存调整
	 * @param packet
	 * @param id
	 * @param userId
	 * @param type
	 * @param num
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/adjusti", method = RequestMethod.POST)
	public @ModelAttribute("outResp")GoodsInventoryUpdateResp adjustmentInventory(@ModelAttribute UpdateRequestPacket packet,
			String id, String userId,String type, String num, HttpServletRequest request) {
		
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"adjustmentInventory");
		LogModel lm = (LogModel) request.getAttribute("lm");
		GoodsdAdjustInventoryDomain adjustInventoryDomain = new GoodsdAdjustInventoryDomain(
				packet, id,userId,type,num, lm, messageRoot);
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
	 * http://localhost:882/rest/j/update/adjustw?&ip==127.0.0.1&client=ordercenter&t=123456789&id=2&num=-1&type=2
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
			String id, String userId,String type, String num, HttpServletRequest request) {
		
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryUpdateController",
				"adjustmentWaterflood");
		LogModel lm = (LogModel) request.getAttribute("lm");
		GoodsdAdjustWaterfloodDomain adjustWaterfloodDomain = new GoodsdAdjustWaterfloodDomain(
				packet, id,userId,type,num, lm, messageRoot);
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
