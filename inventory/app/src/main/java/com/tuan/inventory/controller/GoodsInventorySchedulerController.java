package com.tuan.inventory.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tuan.inventory.domain.ConfirmQueueConsumeDomain;
import com.tuan.inventory.domain.LockedQueueConsumeDomain;
import com.tuan.inventory.domain.LogsQueueConsumeDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.resp.inner.UpdateRequestPacket;
import com.tuan.inventory.resp.outer.GoodsInventoryUpdateResp;
import com.tuan.inventory.service.GoodsInventoryScheduledService;
import com.tuan.inventory.utils.LogModel;
import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageTypeEnum;

/**
 * @description <p>
 *              库存job控制器
 *              </p>
 * @author henry.yu
 * @date 2014.4.15
 */
@Controller
@RequestMapping("/job")
public class GoodsInventorySchedulerController {
	@Resource
	private GoodsInventoryScheduledService goodsInventoryScheduledService;

	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	public @ModelAttribute("outResp")
	GoodsInventoryUpdateResp confirmQueueConsume(
			@ModelAttribute UpdateRequestPacket packet,
			HttpServletRequest request) {
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventorySchedulerController",
				"confirmQueueConsume");
		LogModel lm = (LogModel) request.getAttribute("lm");
		ConfirmQueueConsumeDomain confirmQueueConsumeDomain = new ConfirmQueueConsumeDomain(
				packet, lm, messageRoot);
		confirmQueueConsumeDomain
				.setGoodsInventoryScheduledService(goodsInventoryScheduledService);
		// 接口参数校验
		ResultEnum resEnum = confirmQueueConsumeDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return confirmQueueConsumeDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = confirmQueueConsumeDomain.doBusiness();
		// 返回结果
		return confirmQueueConsumeDomain.makeResult(resEnum);
	}
	@RequestMapping(value = "/lock", method = RequestMethod.POST)
	public @ModelAttribute("outResp")
	GoodsInventoryUpdateResp lockedQueueConsume(
			@ModelAttribute UpdateRequestPacket packet,String period,
			HttpServletRequest request) {
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventorySchedulerController",
				"lockedQueueConsume");
		LogModel lm = (LogModel) request.getAttribute("lm");
		LockedQueueConsumeDomain lockedQueueConsumeDomain = new LockedQueueConsumeDomain(
				packet, period,lm, messageRoot);
		lockedQueueConsumeDomain
		.setGoodsInventoryScheduledService(goodsInventoryScheduledService);
		// 接口参数校验
		ResultEnum resEnum = lockedQueueConsumeDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return lockedQueueConsumeDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = lockedQueueConsumeDomain.doBusiness();
		// 返回结果
		return lockedQueueConsumeDomain.makeResult(resEnum);
	}
	@RequestMapping(value = "/log", method = RequestMethod.POST)
	public @ModelAttribute("outResp")
	GoodsInventoryUpdateResp logsQueueConsume(
			@ModelAttribute UpdateRequestPacket packet,
			HttpServletRequest request) {
		Message messageRoot = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(messageRoot, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventorySchedulerController",
				"logsQueueConsume");
		LogModel lm = (LogModel) request.getAttribute("lm");
		LogsQueueConsumeDomain logsQueueConsumeDomain = new LogsQueueConsumeDomain(
				packet, lm, messageRoot);
		logsQueueConsumeDomain
		.setGoodsInventoryScheduledService(goodsInventoryScheduledService);
		// 接口参数校验
		ResultEnum resEnum = logsQueueConsumeDomain.checkParameter();
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return logsQueueConsumeDomain.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = logsQueueConsumeDomain.doBusiness();
		// 返回结果
		return logsQueueConsumeDomain.makeResult(resEnum);
	}
	
}
