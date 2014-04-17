package com.tuan.inventory.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tuan.inventory.domain.GoodsCreateInventoryDomain;
import com.tuan.inventory.domain.GoodsdAckInventoryDomain;
import com.tuan.inventory.domain.GoodsdUpdateInventoryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.model.param.rest.CreaterInventoryRestParam;
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
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
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

}
