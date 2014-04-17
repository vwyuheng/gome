package com.tuan.inventory.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tuan.inventory.domain.GoodsSelectionQueryDomain;
import com.tuan.inventory.domain.GoodsSupplierQueryDomain;
import com.tuan.inventory.model.enu.ResultEnum;
import com.tuan.inventory.resp.inner.GoodsQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.utils.LogModel;
import com.tuan.inventory.utils.StringUtils;
import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageTypeEnum;
/**
 * @description
 * <p>库存查询控制器</p>
 * @author henry.yu
 * @date 2014.4.15
 */
@Controller
@RequestMapping("/query")
public class GoodsInventoryQueryController {

	
	@RequestMapping(value = "/gselection")
	public @ModelAttribute("resp")GoodsQueryInnerResp goodsSelectionQuery(@ModelAttribute("inputPacket") RequestPacket packet
			,String goodsId,String selectionId,HttpServletRequest request) {
		Message traceMessage = StringUtils.makeTraceMessage(packet);
		if(traceMessage == null){
			GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory-App", "GoodsInventoryQueryController", "goodsSelectionQuery");
		LogModel lm = (LogModel)request.getAttribute("lm");
		GoodsSelectionQueryDomain queryDomain = GoodsSelectionQueryDomain.makeGoodsSelectionQueryDomain(packet
				,  goodsId, selectionId,lm,traceMessage);
		if(queryDomain == null){
			GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		//接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		//参数检查未通过时
		if(resEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return (GoodsQueryInnerResp) queryDomain.makeResult(resEnum);
		}
		//调用合作方接口
		resEnum = queryDomain.doBusiness();
		//返回结果
		return (GoodsQueryInnerResp) queryDomain.makeResult(resEnum);
	}
	
	@RequestMapping(value = "/gSuppliers")
	public @ModelAttribute("resp")GoodsQueryInnerResp goodsSuppliersQuery(@ModelAttribute("inputPacket") RequestPacket packet
			,String goodsId,String suppliersId,HttpServletRequest request) {
		Message traceMessage = StringUtils.makeTraceMessage(packet);
		if(traceMessage == null){
			GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory-App", "GoodsInventoryQueryController", "goodsSuppliersQuery");
		LogModel lm = (LogModel)request.getAttribute("lm");
		GoodsSupplierQueryDomain queryDomain = GoodsSupplierQueryDomain.makeGoodsSupplierQueryDomain(packet
				,  goodsId, suppliersId,lm,traceMessage);
		if(queryDomain == null){
			GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		//接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		//参数检查未通过时
		if(resEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return (GoodsQueryInnerResp) queryDomain.makeResult(resEnum);
		}
		//调用合作方接口
		resEnum = queryDomain.doBusiness();
		//返回结果
		return (GoodsQueryInnerResp) queryDomain.makeResult(resEnum);
	}
	
}
