package com.tuan.inventory.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tuan.inventory.domain.GoodsBaseQueryDomain;
import com.tuan.inventory.domain.GoodsQueryDomain;
import com.tuan.inventory.domain.GoodsSelectionListQueryBySelIdListDomain;
import com.tuan.inventory.domain.GoodsSelectionListQueryDomain;
import com.tuan.inventory.domain.GoodsSelectionQueryDomain;
import com.tuan.inventory.domain.GoodsSupplierQueryDomain;
import com.tuan.inventory.domain.GoodsSuppliersListQueryDomain;
import com.tuan.inventory.domain.IsBeDeliveryQueryDomain;
import com.tuan.inventory.model.enu.res.ResultEnum;
import com.tuan.inventory.resp.inner.GoodsBaseQueryInnerResp;
import com.tuan.inventory.resp.inner.GoodsQueryInnerResp;
import com.tuan.inventory.resp.inner.GoodsSelectionListQueryInnerResp;
import com.tuan.inventory.resp.inner.GoodsSelectionQueryInnerResp;
import com.tuan.inventory.resp.inner.GoodsSuppliersListQueryInnerResp;
import com.tuan.inventory.resp.inner.GoodsSuppliersQueryInnerResp;
import com.tuan.inventory.resp.inner.IsBeDeliveryQueryInnerResp;
import com.tuan.inventory.resp.inner.RequestPacket;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.utils.LogModel;
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
	private static Log logquery = LogFactory.getLog("SYS.QUERYRESULT.LOG");
	@Resource
	GoodsInventoryQueryService  goodsInventoryQueryService;

	/***
	 * http://localhost:882/rest/j/query/gselection?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2499&selectionId=28&traceId=123&traceRootId=456
	 * 根据选型id查询选型库存信息
	 * 
	 * @param packet
	 * @param goodsId
	 * @param selectionId 
	 * @param request 
	 * @return
	 */
	@RequestMapping(value = "/gselection", method = RequestMethod.POST)
	public @ModelAttribute("resp")GoodsSelectionQueryInnerResp goodsSelectionQuery(
			@ModelAttribute("inputPacket") RequestPacket packet,
			String goodsId, String selectionId, HttpServletRequest request) {
		Message	traceMessage = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.OUTS,
					"Inventory-app", "GoodsInventoryQueryController",
					"gselection");
		
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/gselection")
		.addMetaData("goodsId", goodsId)
		.addMetaData("selectionId", selectionId)
		.addMetaData("RequestPacket", packet);
		 logquery.info(lm.toJson(false));
		GoodsSelectionQueryDomain queryDomain = GoodsSelectionQueryDomain
				.makeGoodsSelectionQueryDomain(packet, goodsId, selectionId,
						lm, traceMessage);
		if (queryDomain == null) {
			GoodsSelectionQueryInnerResp resp = new GoodsSelectionQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(),
					ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		queryDomain.setGoodsInventoryQueryService(goodsInventoryQueryService);
		// 接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		// 参数检查未通过时
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return (GoodsSelectionQueryInnerResp) queryDomain
					.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = queryDomain.doBusiness();
		// 返回结果
		return (GoodsSelectionQueryInnerResp) queryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/query/gsuppliers?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=1736&suppliersId=1685&traceId=123&traceRootId=456
	 * 根据分店id查询分店库存信息
	 * @param packet
	 * @param goodsId
	 * @param suppliersId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/gsuppliers", method = RequestMethod.POST)
	public @ModelAttribute("resp")GoodsSuppliersQueryInnerResp goodsSuppliersQuery(@ModelAttribute("inputPacket") RequestPacket packet
			,String goodsId,String suppliersId,HttpServletRequest request) {
			Message traceMessage = (Message) request.getAttribute("messageRoot"); // trace根
			TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.OUTS,
					"Inventory-app", "GoodsInventoryQueryController",
					"gsuppliers");
		    LogModel lm = (LogModel)request.getAttribute("lm");
		    lm.setMethod("/gsuppliers")
			.addMetaData("goodsId", goodsId)
			.addMetaData("suppliersId", suppliersId)
			.addMetaData("RequestPacket", packet);
		GoodsSupplierQueryDomain queryDomain = GoodsSupplierQueryDomain.makeGoodsSupplierQueryDomain(packet
				,  goodsId, suppliersId,lm,traceMessage);
		if(queryDomain == null){
			GoodsSuppliersQueryInnerResp resp = new GoodsSuppliersQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		queryDomain.setGoodsInventoryQueryService(goodsInventoryQueryService);
		//接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		//参数检查未通过时
		if(resEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return (GoodsSuppliersQueryInnerResp) queryDomain.makeResult(resEnum);
		}
		//调用合作方接口
		resEnum = queryDomain.doBusiness();
		//返回结果
		return (GoodsSuppliersQueryInnerResp) queryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/query/goods?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=1736&traceId=123&traceRootId=456
	 * 根据商品id查询商品库存
	 * @param packet
	 * @param goodsId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/goods", method = RequestMethod.POST)
	public @ModelAttribute("resp")GoodsQueryInnerResp goodsInventoryQuery(@ModelAttribute("inputPacket") RequestPacket packet
			,String goodsId,HttpServletRequest request) {
			Message traceMessage = (Message) request.getAttribute("messageRoot"); // trace根
			TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.OUTS,
					"Inventory-app", "GoodsInventoryQueryController",
					"goods");
		    LogModel lm = (LogModel)request.getAttribute("lm");
		    lm.setMethod("/goods")
			.addMetaData("goodsId", goodsId)
			.addMetaData("RequestPacket", packet);
		    //logquery.info(lm.toJson(false));
		GoodsQueryDomain queryDomain = GoodsQueryDomain.makeGoodsQueryDomain(packet,goodsId,lm,traceMessage);
		if(queryDomain == null){
			GoodsQueryInnerResp resp = new GoodsQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		queryDomain.setGoodsInventoryQueryService(goodsInventoryQueryService);
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
	/**
	 * http://localhost:882/rest/j/query/gsellist?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2499&traceId=123&traceRootId=456
	 * 根据商品id 查询商品选型库存列表
	 * @param packet
	 * @param goodsId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/gsellist", method = RequestMethod.POST)
	public @ModelAttribute("resp")GoodsSelectionListQueryInnerResp goodsSelectionListQuery(@ModelAttribute("inputPacket") RequestPacket packet
			,String goodsId,HttpServletRequest request) {
		
			Message traceMessage = (Message) request.getAttribute("messageRoot"); // trace根
			TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.OUTS,
					"Inventory-app", "GoodsInventoryQueryController",
					"gsellist");
		LogModel lm = (LogModel)request.getAttribute("lm");
		 lm.setMethod("/gsellist")
			.addMetaData("goodsId", goodsId)
			.addMetaData("RequestPacket", packet);
		 logquery.info(lm.toJson(false));
		GoodsSelectionListQueryDomain queryDomain = GoodsSelectionListQueryDomain.makeGoodsSelectionListQueryDomain(packet,goodsId,lm,traceMessage);
		if(queryDomain == null){
			GoodsSelectionListQueryInnerResp resp = new GoodsSelectionListQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		queryDomain.setGoodsInventoryQueryService(goodsInventoryQueryService);
		//接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		//参数检查未通过时
		if(resEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return (GoodsSelectionListQueryInnerResp) queryDomain.makeResult(resEnum);
		}
		//调用合作方接口
		resEnum = queryDomain.doBusiness();
		//返回结果
		return (GoodsSelectionListQueryInnerResp) queryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/query/sellist?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2499&selectionIdList=[28,29,30]&traceId=123&traceRootId=456
	 * 根据选型id list 批量获取指定选型列表
	 * @param packet
	 * @param goodsId
	 * @param selectionIdList
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/sellist", method = RequestMethod.POST)
	public @ModelAttribute("resp")GoodsSelectionListQueryInnerResp goodsSelectionListQueryByselidList(@ModelAttribute("inputPacket") RequestPacket packet
			,String goodsId,String selectionIdList,HttpServletRequest request) {
			Message traceMessage = (Message) request.getAttribute("messageRoot"); // trace根
			TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.OUTS,
					"Inventory-app", "GoodsInventoryQueryController",
					"sellist");
		    LogModel lm = (LogModel)request.getAttribute("lm");
		    lm.setMethod("/sellist")
			.addMetaData("goodsId", goodsId)
			.addMetaData("RequestPacket", packet);
		    logquery.info(lm.toJson(false));
		GoodsSelectionListQueryBySelIdListDomain queryDomain = GoodsSelectionListQueryBySelIdListDomain.makeGoodsSelectionListQueryDomain(packet,goodsId,selectionIdList,lm,traceMessage);
		if(queryDomain == null){
			GoodsSelectionListQueryInnerResp resp = new GoodsSelectionListQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		queryDomain.setGoodsInventoryQueryService(goodsInventoryQueryService);
		//接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		//参数检查未通过时
		if(resEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return (GoodsSelectionListQueryInnerResp) queryDomain.makeResult(resEnum);
		}
		//调用合作方接口
		resEnum = queryDomain.doBusiness();
		//返回结果
		return (GoodsSelectionListQueryInnerResp) queryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/query/gsupplist?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=1736&traceId=123&traceRootId=456
	 * 根据商品id 查询商品分店库存信息列表
	 * @param packet
	 * @param goodsId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/gsupplist", method = RequestMethod.POST)
	public @ModelAttribute("resp")GoodsSuppliersListQueryInnerResp goodsSuppliersListQuery(@ModelAttribute("inputPacket") RequestPacket packet
			,String goodsId,HttpServletRequest request) {
			Message traceMessage = (Message) request.getAttribute("messageRoot"); // trace根
			TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.OUTS,
					"Inventory-app", "GoodsInventoryQueryController",
					"gsupplist");
		LogModel lm = (LogModel)request.getAttribute("lm");
		 lm.setMethod("/gsupplist")
			.addMetaData("goodsId", goodsId)
			.addMetaData("RequestPacket", packet);
		GoodsSuppliersListQueryDomain queryDomain = GoodsSuppliersListQueryDomain.makeGoodsSuppliersListQueryDomain(packet,goodsId,lm,traceMessage);
		if(queryDomain == null){
			GoodsSuppliersListQueryInnerResp resp = new GoodsSuppliersListQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		queryDomain.setGoodsInventoryQueryService(goodsInventoryQueryService);
		//接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		//参数检查未通过时
		if(resEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return (GoodsSuppliersListQueryInnerResp) queryDomain.makeResult(resEnum);
		}
		//调用合作方接口
		resEnum = queryDomain.doBusiness();
		//返回结果
		return (GoodsSuppliersListQueryInnerResp) queryDomain.makeResult(resEnum);
	}
	/**
	 * http://localhost:882/rest/j/query/isbedelivery?&ip==127.0.0.1&client=ordercenter&t=123456789&wmsGoodsId=T01000000116&isBeDelivery=&traceId=123&traceRootId=456
	 * 根据物流码和物流仓库类型查询发货仓库信息
	 * @param packet
	 * @param wmsGoodsId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/isbedelivery", method = RequestMethod.POST)
	public @ModelAttribute("resp")IsBeDeliveryQueryInnerResp wmsIsBeDeliveryQuery(@ModelAttribute("inputPacket") RequestPacket packet
			,String wmsGoodsId,String isBeDelivery,HttpServletRequest request) {
		Message traceMessage = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.OUTS,
				"Inventory-app", "GoodsInventoryQueryController",
				"isbedelivery");
		LogModel lm = (LogModel)request.getAttribute("lm");
		lm.setMethod("/isbedelivery")
		.addMetaData("wmsGoodsId", wmsGoodsId)
		.addMetaData("RequestPacket", packet);
		logquery.info(lm.toJson(false));
		IsBeDeliveryQueryDomain queryDomain = IsBeDeliveryQueryDomain.makeGoodsSuppliersListQueryDomain(packet,wmsGoodsId,isBeDelivery,lm,traceMessage);
		if(queryDomain == null){
			IsBeDeliveryQueryInnerResp resp = new IsBeDeliveryQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(), ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		queryDomain.setGoodsInventoryQueryService(goodsInventoryQueryService);
		//接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		//参数检查未通过时
		if(resEnum.compareTo(ResultEnum.SUCCESS) != 0){
			return (IsBeDeliveryQueryInnerResp) queryDomain.makeResult(resEnum);
		}
		//调用合作方接口
		resEnum = queryDomain.doBusiness();
		//返回结果
		return (IsBeDeliveryQueryInnerResp) queryDomain.makeResult(resEnum);
	}
	
	
	/***
	 * http://localhost:882/rest/j/query/gselection?&ip==127.0.0.1&client=ordercenter&t=123456789&goodsId=2499&selectionId=28&traceId=123&traceRootId=456
	 * 根据选型id查询选型库存信息
	 * 
	 * @param packet
	 * @param goodsId
	 * @param selectionId 
	 * @param request 
	 * @return
	 */
	@RequestMapping(value = "/salescnt", method = RequestMethod.POST)
	public @ModelAttribute("resp")GoodsBaseQueryInnerResp goodsBaseQuery(
			@ModelAttribute("inputPacket") RequestPacket packet,
			String goodsBaseId, HttpServletRequest request) {
		Message	traceMessage = (Message) request.getAttribute("messageRoot"); // trace根
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.OUTS,
					"Inventory-app", "GoodsInventoryQueryController",
					"gsbaseinfo");
		LogModel lm = (LogModel) request.getAttribute("lm");
		lm.setMethod("/salescnt")
		.addMetaData("goodsBaseId", goodsBaseId)
		.addMetaData("RequestPacket", packet);
		logquery.info(lm.toJson(false));
		GoodsBaseQueryDomain queryDomain = 		GoodsBaseQueryDomain
				.makeGoodsBaseQueryDomain(packet,  goodsBaseId,
						lm, traceMessage);
		if (queryDomain == null) {
			GoodsBaseQueryInnerResp resp = new GoodsBaseQueryInnerResp();
			resp.setResult(ResultEnum.NO_PARAMETER.getCode(),
					ResultEnum.NO_PARAMETER.getDescription());
			return resp;
		}
		queryDomain.setGoodsInventoryQueryService(goodsInventoryQueryService);
		// 接口参数校验
		ResultEnum resEnum = queryDomain.checkParameter();
		// 参数检查未通过时
		if (resEnum.compareTo(ResultEnum.SUCCESS) != 0) {
			return (GoodsBaseQueryInnerResp) queryDomain
					.makeResult(resEnum);
		}
		// 调用合作方接口
		resEnum = queryDomain.doBusiness();
		// 返回结果
		return (GoodsBaseQueryInnerResp) queryDomain.makeResult(resEnum);
	}
}
