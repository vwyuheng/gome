package com.tuan.inventory.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lock.eum.LockResultCodeEnum;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.core.common.lock.res.LockResult;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.domain.InventoryInitDomain;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.SynInitAndAsynUpdateDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.WmsIsBeDeliveryModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.enu.res.InventoryQueryEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryQueryResult;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.service.InventoryQueryServiceCallback;

public class GoodsInventoryQueryServiceImpl extends AbstractInventoryService implements
		GoodsInventoryQueryService {
	private static Log logger = LogFactory.getLog("INVENTORY.INIT");
	protected static Log logQuery = LogFactory.getLog("SYS.QUERYRESULT.LOG");
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	@Resource
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	@Resource
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	@Resource
	private DLockImpl dLock;//分布式锁
	
	@Override
	public CallResult<GoodsSelectionModel> findGoodsSelectionBySelectionId(
			final String clientIp, final String clientName, final long goodsId,
			final long selectionId) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryService.findGoodsSelectionBySelectionId";
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("clientIp", clientIp)
		.addMetaData("clientName", clientName)
		.addMetaData("goodsId", goodsId)
		.addMetaData("selectionId", selectionId)
		.addMetaData("start", startTime);
		if(logQuery.isDebugEnabled()) {
			logQuery.debug(lm.toJson(false));
		}
		
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						
						long startTime = System.currentTimeMillis();
						if(logQuery.isDebugEnabled()) {
							logQuery.debug(lm.addMetaData("init start", startTime)
									.toJson(true));
							
						}
						
						InventoryQueryEnum enumRes = null;
						//参数校验
						if (selectionId <= 0) {
							enumRes = InventoryQueryEnum.INVALID_SELECTIONID;
						}
						// 检查出现错误
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck("findGoodsSelectionBySelectionId("+goodsId+","+selectionId+")",goodsId,lm);
						
						long endTime = System.currentTimeMillis();
						String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
								+ "milliseconds(毫秒)执行完成!";
						if(logQuery.isDebugEnabled()) {
							logQuery.debug(lm.addMetaData("endTime", endTime).addMetaData("goodsId", goodsId)
									.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
						}
						
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, new InventoryQueryResult(
									InventoryQueryEnum.createQueryEnum(resultEnum.getCode(),resultEnum.getDescription()), null));
						}
		
						
						return TuanCallbackResult.success();
					}

					@Override
					public TuanCallbackResult doWork() {
						InventoryQueryResult res = null;
						GoodsSelectionModel gsModel = goodsInventoryDomainRepository
								.queryGoodsSelectionBySelectionId(selectionId);
						if (gsModel != null) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, gsModel);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.NO_SELECTION, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", endTime);
		if(logQuery.isDebugEnabled()) {
			logQuery.debug(lm.toJson(false));
		}
		
		return new CallResult<GoodsSelectionModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsSelectionModel) qresult.getResultObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<GoodsSuppliersModel> findGoodsSuppliersBySuppliersId(
			final String clientIp,final String clientName, final long goodsId, final long suppliersId) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryService.findGoodsSuppliersBySuppliersId";
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  . addMetaData("goodsId", goodsId)
		  .addMetaData("suppliersId", suppliersId)
		  .addMetaData("start", startTime);
		logQuery.info(lm.toJson(true));
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						long startTime = System.currentTimeMillis();
						String method = "findGoodsSuppliersBySuppliersId";
						final LogModel lm = LogModel.newLogModel(method);
						logger.info(lm.setMethod(method).addMetaData("start", startTime)
								.toJson(true));
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck("findGoodsSuppliersBySuppliersId("+goodsId+","+suppliersId+")",goodsId,lm);
						
						long endTime = System.currentTimeMillis();
						String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
								+ "milliseconds(毫秒)执行完成!";
						logger.info(lm.setMethod(method).addMetaData("endTime", endTime).addMetaData("goodsId", goodsId)
								.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(true));
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, new InventoryQueryResult(
									InventoryQueryEnum.createQueryEnum(resultEnum.getCode(),resultEnum.getDescription()), null));
						}
						if (suppliersId <= 0) {
							enumRes = InventoryQueryEnum.INVALID_SUPPLIERSID;
						}
						// 检查出现错误
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						return TuanCallbackResult.success();
					}

					@Override
					public TuanCallbackResult doWork() {
						InventoryQueryResult res = null;
						GoodsSuppliersModel gsModel = goodsInventoryDomainRepository
								.queryGoodsSuppliersBySuppliersId(suppliersId);
						if (gsModel != null) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, gsModel);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SYS_ERROR, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", endTime);
		logQuery.info(lm.toJson(true));
		return new CallResult<GoodsSuppliersModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsSuppliersModel) qresult.getResultObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<GoodsInventoryModel> findGoodsInventoryByGoodsId(final String clientIp,
			final String clientName,  final long goodsId) {
		
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryService.findGoodsInventoryByGoodsId_"+startTime;
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		  .addMetaData("start", startTime);
		
		if(logQuery.isDebugEnabled()) {
        	 logQuery.debug(lm.toJson(false));
		}
       
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						long startTime = System.currentTimeMillis();
						
					  if(logQuery.isDebugEnabled()) {
						  logQuery.debug(lm.addMetaData("init start", startTime)
									.toJson(false));
					  }
					  
						InventoryQueryEnum enumRes = null;

						if (goodsId <= 0) {
							enumRes = InventoryQueryEnum.INVALID_GOODSID;
						}
						// 检查出现错误
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck("findGoodsInventoryByGoodsId("+goodsId+")",goodsId,lm);
						
						long endTime = System.currentTimeMillis();
						String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
								+ "milliseconds(毫秒)执行完成!";
						 if(logQuery.isDebugEnabled()) {
							 logQuery.debug(lm.addMetaData("init endTime", endTime).addMetaData("goodsId", goodsId)
										.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
						 }
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							
							return TuanCallbackResult.failure(resultEnum.getCode(), null, new InventoryQueryResult(
									InventoryQueryEnum.createQueryEnum(resultEnum.getCode(),resultEnum.getDescription()), null));
						}
						
						
						return TuanCallbackResult.success();
					}

					@Override
					public TuanCallbackResult doWork() {
						InventoryQueryResult res = null;
						GoodsInventoryModel gsModel = goodsInventoryDomainRepository
								.queryGoodsInventoryByGoodsId(goodsId);
						if (gsModel != null) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, gsModel);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.NO_GOODS, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		
		lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
				.addMetaData("resultCode", resultCode)
				.addMetaData("qresult", qresult!=null?qresult.getResultObject():"qresult is null! this is vip error!")
				.addMetaData("end", endTime);
		if(logQuery.isDebugEnabled()) {
				logQuery.debug(lm.toJson(false));
		 }
		
		return new CallResult<GoodsInventoryModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				qresult!=null?(GoodsInventoryModel) qresult.getResultObject():null,
				result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionModel>> findGoodsSelectionListByGoodsId(
			final String clientIp, final String clientName, final long goodsId) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryService.findGoodsSelectionListByGoodsId";
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		  .addMetaData("start", startTime);
		if(logQuery.isDebugEnabled()) {
			logQuery.debug(lm.toJson(false));
		}
		
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						long startTime = System.currentTimeMillis();
						if(logQuery.isDebugEnabled()) {
							logQuery.debug(lm.addMetaData("start", startTime)
									.toJson(false));
						}
						
						InventoryQueryEnum enumRes = null;
						if (goodsId <= 0) {
							enumRes = InventoryQueryEnum.INVALID_GOODSID;
						}
						// 检查出现错误
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck("findGoodsSelectionListByGoodsId("+goodsId+")",goodsId,lm);
						
						long endTime = System.currentTimeMillis();
						String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
								+ "milliseconds(毫秒)执行完成!";
						if(logQuery.isDebugEnabled()) {
							logQuery.debug(lm.addMetaData("endTime", endTime).addMetaData("goodsId", goodsId)
									.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
						}
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, new InventoryQueryResult(
									InventoryQueryEnum.createQueryEnum(resultEnum.getCode(),resultEnum.getDescription()), null));
						}
						
						return TuanCallbackResult.success();
					}

					@Override
					public TuanCallbackResult doWork() {
						InventoryQueryResult res = null;
						List<GoodsSelectionModel> result = goodsInventoryDomainRepository
								.queryGoodsSelectionListByGoodsId(goodsId);
						if (!CollectionUtils.isEmpty(result)) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, result);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.NO_SELECTION, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
	   lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", endTime);
	   if(logQuery.isDebugEnabled()) {
		   logQuery.debug(lm.toJson(false));
	   }
	  
		return new CallResult<List<GoodsSelectionModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSelectionModel>) qresult.getResultObject(),
				result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSuppliersModel>> findGoodsSuppliersListByGoodsId(
			final String clientIp, final String clientName, final long goodsId) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryService.findGoodsSuppliersListByGoodsId";
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		   .addMetaData("start", startTime);
		 logQuery.info(lm.toJson(false));
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						long startTime = System.currentTimeMillis();
						/*String method = "findGoodsSuppliersListByGoodsId";
						final LogModel lm = LogModel.newLogModel(method);*/
						logger.info(lm.addMetaData("start", startTime)
								.toJson(false));
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck("findGoodsSuppliersListByGoodsId("+goodsId+")",goodsId,lm);
						
						long endTime = System.currentTimeMillis();
						String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
								+ "milliseconds(毫秒)执行完成!";
						logger.info(lm.addMetaData("endTime", endTime).addMetaData("goodsId", goodsId)
								.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, new InventoryQueryResult(
									InventoryQueryEnum.createQueryEnum(resultEnum.getCode(),resultEnum.getDescription()), null));
						}
						if (goodsId <= 0) {
							enumRes = InventoryQueryEnum.INVALID_GOODSID;
						}
						// 检查出现错误
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						return TuanCallbackResult.success();
					}

					@Override
					public TuanCallbackResult doWork() {
						InventoryQueryResult res = null;
						List<GoodsSuppliersModel> result = goodsInventoryDomainRepository
								.queryGoodsSuppliersListByGoodsId(goodsId);
						if (!CollectionUtils.isEmpty(result)) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, result);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SYS_ERROR, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", endTime);
		return new CallResult<List<GoodsSuppliersModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSuppliersModel>) qresult.getResultObject(),
				result.getThrowable());
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionModel>> findGoodsSelectionListBySelectionIdList(
			final String clientIp, final String clientName, final long goodsId,
			final List<Long> selectionIdList) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryService.findGoodsSelectionListBySelectionIdList";
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		  .addMetaData("selectionIdList", selectionIdList)
		   .addMetaData("start", startTime);
		if(logQuery.isDebugEnabled()) {
			logQuery.debug(lm.toJson(false));
		}
		
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						long startTime = System.currentTimeMillis();
						if(logQuery.isDebugEnabled()) {
							logQuery.debug(lm.addMetaData("start", startTime)
									.toJson(false));
						}
						
						InventoryQueryEnum enumRes = null;
						
						if (goodsId <= 0) {
							enumRes = InventoryQueryEnum.INVALID_GOODSID;
						}
						if (CollectionUtils.isEmpty(selectionIdList)) {
							enumRes = InventoryQueryEnum.INVALID_PARAM;
						}
						// 检查出现错误
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck("findGoodsSelectionListBySelectionIdList("+goodsId+","+selectionIdList+")",goodsId,lm);

						long endTime = System.currentTimeMillis();
						String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
								+ "milliseconds(毫秒)执行完成!";
						if(logQuery.isDebugEnabled()) {
							logQuery.debug(lm.addMetaData("endTime", endTime).addMetaData("goodsId", goodsId)
									.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
						}
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, new InventoryQueryResult(
									InventoryQueryEnum.createQueryEnum(resultEnum.getCode(),resultEnum.getDescription()), null));
						}
						
						return TuanCallbackResult.success();
					}

					@Override
					public TuanCallbackResult doWork() {
						InventoryQueryResult res = null;
						List<GoodsSelectionModel> result = goodsInventoryDomainRepository
								.queryGoodsSelectionListBySelectionIdList(selectionIdList);
						if (!CollectionUtils.isEmpty(result)) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, result);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.NO_SELECTION, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", endTime);
		if(logQuery.isDebugEnabled()) {
			logQuery.debug(lm.toJson(false));
		}
		return new CallResult<List<GoodsSelectionModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSelectionModel>) qresult.getResultObject(),
				result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSuppliersModel>> findGoodsSuppliersListBySuppliersIdList(
			final String clientIp, final String clientName, final long goodsId,
			final List<Long> suppliersIdList) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryService.findGoodsSuppliersListBySuppliersIdList";
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
				.addMetaData("goodsId", goodsId)
		        .addMetaData("suppliersIdList", suppliersIdList)
		         .addMetaData("start", startTime);
		logQuery.info(lm.toJson(true));
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck("findGoodsSuppliersListBySuppliersIdList("+goodsId+","+suppliersIdList+")",goodsId,lm);
						
						lm.addMetaData("init","init,after").addMetaData("init[findGoodsSuppliersListBySuppliersIdList]", goodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, new InventoryQueryResult(
									InventoryQueryEnum.createQueryEnum(resultEnum.getCode(),resultEnum.getDescription()), null));
						}
						if (goodsId <= 0) {
							enumRes = InventoryQueryEnum.INVALID_GOODSID;
						}
						// 检查出现错误
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						return TuanCallbackResult.success();
					}

					@Override
					public TuanCallbackResult doWork() {
						InventoryQueryResult res = null;
						List<GoodsSuppliersModel> result = goodsInventoryDomainRepository
								.queryGoodsSuppliersListBySuppliersIdList(suppliersIdList);
						if (!CollectionUtils.isEmpty(result)) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, result);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SYS_ERROR, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", endTime);
		logQuery.info(lm.toJson(true));
		return new CallResult<List<GoodsSuppliersModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSuppliersModel>) qresult.getResultObject(),
				result.getThrowable());
	}
	
	@Override
	public CallResult<WmsIsBeDeliveryModel> findWmsIsBeDeliveryByWmsGoodsId(
			final String clientIp, final String clientName, final String wmsGoodsId,final String isBeDelivery) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryService.findWmsIsBeDeliveryByWmsGoodsId";
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("wmsGoodsId", wmsGoodsId)
		  .addMetaData("start", startTime);
		if(logQuery.isDebugEnabled()) {
			logQuery.debug(lm.toJson(false));
		}
		
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						long startTime = System.currentTimeMillis();
						if(logQuery.isDebugEnabled()) {
							logQuery.debug(lm.addMetaData("start", startTime)
									.toJson(false));
						}
					
						InventoryQueryEnum enumRes = null;
						
						if (StringUtils.isEmpty(wmsGoodsId)) {
							enumRes = InventoryQueryEnum.INVALID_WMSGOODSID;
						}
						// 检查出现错误
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initWmsCheck("findWmsIsBeDeliveryByWmsGoodsId("+wmsGoodsId+","+isBeDelivery+")",wmsGoodsId,isBeDelivery,lm);
						
						long endTime = System.currentTimeMillis();
						String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
								+ "milliseconds(毫秒)执行完成!";
						if(logQuery.isDebugEnabled()) {
							logQuery.debug(lm.addMetaData("endTime", endTime).addMetaData("wmsGoodsId", wmsGoodsId)
									.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
						}
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, new InventoryQueryResult(
									InventoryQueryEnum.createQueryEnum(resultEnum.getCode(),resultEnum.getDescription()), null));
						}
						
						return TuanCallbackResult.success();
					}

					@Override
					public TuanCallbackResult doWork() {
						InventoryQueryResult res = null;
						WmsIsBeDeliveryModel isBeDeliveryModel = null;
						GoodsInventoryWMSDO wmsDO = goodsInventoryDomainRepository
								.queryGoodsInventoryWms(wmsGoodsId);
						if(wmsDO!=null) {
							isBeDeliveryModel = ObjectUtils.toModel(wmsDO);
							//转换
							switch (wmsDO.getIsBeDelivery()) {
							case 0:
								isBeDeliveryModel.setIsBeDeliveryDesc("wowo");
								isBeDeliveryModel.setDescription("窝窝发货");
								break;
							case 1:
								isBeDeliveryModel.setIsBeDeliveryDesc("shangjia");
								isBeDeliveryModel.setDescription("商家发货");
								break;
							}
							if(!StringUtils.isEmpty(isBeDelivery)&&wmsDO.getIsBeDelivery()!=Integer.valueOf(isBeDelivery)) {
								isBeDeliveryModel = null;
							}	
			
						}
						if (isBeDeliveryModel != null) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, isBeDeliveryModel);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.NO_WMS_DATA, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", endTime);
		if(logQuery.isDebugEnabled()) {
			logQuery.debug(lm.toJson(false));
		}
		
		return new CallResult<WmsIsBeDeliveryModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(WmsIsBeDeliveryModel) qresult.getResultObject(),
				result.getThrowable());
	}
	
	//初始化检查
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum initCheck(String initFromDesc,long goodsId,LogModel lm) {
		//初始化加分布式锁
		LockResult<String> lockResult = null;
		CreateInventoryResultEnum resultEnum = null;
		String key = DLockConstants.JOB_HANDLER+"_goodsId_" + goodsId;
			try {
				lockResult = dLock.lockManualByTimes(key, 5000L, 5);
				if (lockResult == null
						|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
								.getCode()) {
					logQuery.error(lm.addMetaData("dLock",goodsId).toJson(false));
				}
				
				InventoryInitDomain create = new InventoryInitDomain(goodsId,
						lm);
				//注入相关Repository
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
				create.setInitFromDesc(initFromDesc);
				resultEnum = create.businessExecute();
				
			} finally{
				dLock.unlockManual(key);
			}
		
			return resultEnum;
		}

	    //初始化物流库存库存
		@SuppressWarnings("unchecked")
		public CreateInventoryResultEnum initWmsCheck(String initFromDesc,String wmsGoodsId,String isBeDelivery,LogModel lm) {
					//初始化加分布式锁
					
					LockResult<String> lockResult = null;
					CreateInventoryResultEnum resultEnum = null;
					String key = DLockConstants.JOB_HANDLER+"_wmsGoodsId_" + wmsGoodsId;
					try {
						lockResult = dLock.lockManualByTimes(key, DLockConstants.INIT_LOCK_TIME, DLockConstants.INIT_LOCK_RETRY_TIMES);
						if (lockResult == null
								|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
										.getCode()) {
							logQuery.error(lm.addMetaData("GoodsInventoryQueryServiceImpl initWmsCheck dLock errorMsg",
									wmsGoodsId).toJson(false));
						}
						InventoryInitDomain create = new InventoryInitDomain();
						//注入相关Repository
						create.setWmsGoodsId(wmsGoodsId);
						create.setIsBeDelivery(isBeDelivery);
						create.setLm(lm);
						create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
						create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
						create.setInitFromDesc(initFromDesc);
						resultEnum = create.business4WmsExecute();
					} finally{
						dLock.unlockManual(key);
					}
					return resultEnum;
				}

		@Override
		public CallResult<GoodsBaseModel> findSalesCountByGoodsBaseId(
				String clientIp, String clientName, final long goodsBaseId) {
			long startTime = System.currentTimeMillis();
			String method = "GoodsInventoryQueryService.findSalesCountByGoodsBaseId_"+startTime;
			final LogModel lm = LogModel
					.newLogModel(method);
			lm.addMetaData("clientIp", clientIp)
			  .addMetaData("clientName", clientName)
			  .addMetaData("goodsBaseId", goodsBaseId)
			  .addMetaData("start", startTime);
			if(logQuery.isDebugEnabled()) {
				logQuery.debug(lm.toJson(false));
			}
			
			TuanCallbackResult result = this.inventoryServiceTemplate
					.execute(new InventoryQueryServiceCallback() {
						@Override
						public TuanCallbackResult preHandler() {
							
							InventoryQueryEnum enumRes = null;
							if (goodsBaseId<=0) {
								enumRes = InventoryQueryEnum.INVALID_GOODSBASEID;
							}
							
								long startTime = System.currentTimeMillis();
								GoodsBaseInventoryDO tmpBaseDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
								if(tmpBaseDO == null) {
									CallResult<GoodsBaseInventoryDO>  cresult=synInitAndAysnMysqlService.selectInventoryBase4Init(goodsBaseId);
									GoodsBaseInventoryDO sourceBaseDO = null;
									if (cresult == null || !cresult.isSuccess()) {
										enumRes = InventoryQueryEnum.SYS_ERROR;
									}else {
										sourceBaseDO = 	cresult.getBusinessResult();
									}
									if(sourceBaseDO!=null) {
										String message = StringUtils.EMPTY;
										CallResult<Boolean> callResult  = null;
										
										try {
											callResult = synInitAndAysnMysqlService.saveGoodsBaseInventory(sourceBaseDO);
											if(callResult==null) {
												enumRes = InventoryQueryEnum.SYS_ERROR;
											}
											PublicCodeEnum publicCodeEnum = callResult
													.getPublicCodeEnum();
											if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
												// 消息数据不存并且不成功
												message = "saveGoodsBaseInventory2Mysql_error[" + publicCodeEnum.getMessage()
														+ "]goodsBaseId:" + goodsBaseId;
												enumRes = InventoryQueryEnum.SYS_ERROR;
												
											} else {
												message = "saveGoodsBaseInventory2MysqlAndRedis_success[save2mysqlAndRedis success]goodsBaseId:" + goodsBaseId;
												
											}
										} catch (Exception e) {
											enumRes = InventoryQueryEnum.SYS_ERROR;
										}finally {
											long endTime = System.currentTimeMillis();
											String runResult = "[init]业务处理历时" + (startTime - endTime)
													+ "milliseconds(毫秒)执行完成!";
											if(logQuery.isDebugEnabled()) {
												logQuery.debug(lm.addMetaData("goodsBaseId",goodsBaseId)
														.addMetaData("sourceBaseDO",sourceBaseDO)
														.addMetaData("message",message)
														.addMetaData("runResult", runResult).toJson(false));
											}
											
										}
										
									}else {
										enumRes = InventoryQueryEnum.NO_GOODSBASE;
									}
								}
							
							// 检查出现错误
							if (enumRes != null) {
								return TuanCallbackResult.failure(
										enumRes.getCode(), null,
										new InventoryQueryResult(enumRes, null));
							}
							return TuanCallbackResult.success();
						}

						@Override
						public TuanCallbackResult doWork() {
							InventoryQueryResult res = null;
							GoodsBaseInventoryDO baseDO = goodsInventoryDomainRepository
									.queryGoodsBaseById(goodsBaseId);
							GoodsBaseModel baseModel =	ObjectUtils.toModel(baseDO);
							if (baseModel != null) {
								res = new InventoryQueryResult(
										InventoryQueryEnum.SUCCESS, baseModel);
								return TuanCallbackResult.success(res.getResult()
										.getCode(), res);
							} else {
								res = new InventoryQueryResult(
										InventoryQueryEnum.NO_GOODSBASE, null);
								return TuanCallbackResult.failure(res.getResult()
										.getCode(), null, res);
							}

						}

					}

					);
			final int resultCode = result.getResultCode();
			final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
			long endTime = System.currentTimeMillis();
			String runResult = "[" + method + "]业务处理历时" + (startTime - endTime)
					+ "milliseconds(毫秒)执行完成!";
			lm.addMetaData("result", result.isSuccess()).addMetaData("runResult", runResult)
					.addMetaData("resultCode", result.getResultCode())
					.addMetaData("qresult", qresult.getResultObject())
					.addMetaData("end", endTime);
			if(logQuery.isDebugEnabled()) {
				logQuery.debug(lm.toJson(false));
			}
			
			return new CallResult<GoodsBaseModel>(result.isSuccess(),
					PublicCodeEnum.valuesOf(resultCode),
					(GoodsBaseModel) qresult.getResultObject(),
					result.getThrowable());
		}
}
