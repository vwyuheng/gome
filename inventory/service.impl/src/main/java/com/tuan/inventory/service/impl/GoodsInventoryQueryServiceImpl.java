package com.tuan.inventory.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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

	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;

	@Resource
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	//@Resource
	//@Resource
	//InitCacheDomainRepository initCacheDomainRepository;
	@Resource
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	//@Resource
	//private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	@Resource
	private DLockImpl dLock;//分布式锁
	
	@Override
	public CallResult<GoodsSelectionModel> findGoodsSelectionBySelectionId(
			final String clientIp, final String clientName, final long goodsId,
			final long selectionId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionBySelectionId");
		lm.addMetaData("clientIp", clientIp)
		.addMetaData("clientName", clientName)
		.addMetaData("goodsId", goodsId)
		.addMetaData("selectionId", selectionId)
		.addMetaData("start", "start");
		writeSysBusLog(lm,false);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
						lm.addMetaData("init","init,after").addMetaData("init[findGoodsSelectionBySelectionId]", goodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
						}
		
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
									InventoryQueryEnum.SYS_ERROR, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		writeSysBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject()),
				false);
		writeSysLog(lm.toJson(false));
		return new CallResult<GoodsSelectionModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsSelectionModel) qresult.getResultObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<GoodsSuppliersModel> findGoodsSuppliersBySuppliersId(
			final String clientIp,final String clientName, final long goodsId, final long suppliersId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSuppliersBySuppliersId");
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  . addMetaData("goodsId", goodsId)
		  .addMetaData("suppliersId", suppliersId)
		  .addMetaData("start", "start");
		writeSysBusLog(lm,false);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
						lm.addMetaData("init","init,after").addMetaData("init[findGoodsSuppliersBySuppliersId]", goodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
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
		writeSysBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", "end")
				,false);
		writeSysLog(lm.toJson());
		return new CallResult<GoodsSuppliersModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsSuppliersModel) qresult.getResultObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<GoodsInventoryModel> findGoodsInventoryByGoodsId(final String clientIp,
			final String clientName,  final long goodsId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsInventoryByGoodsId");
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		  .addMetaData("start", "start");
		writeSysBusLog(lm,false);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						
						InventoryQueryEnum enumRes = null;
				
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
						lm.addMetaData("init","init,after").addMetaData("init[findGoodsInventoryByGoodsId]", goodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
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
						GoodsInventoryModel gsModel = goodsInventoryDomainRepository
								.queryGoodsInventoryByGoodsId(goodsId);
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
		writeSysBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", "end")
				,false);
		writeSysLog(lm.toJson());
		
		return new CallResult<GoodsInventoryModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsInventoryModel) qresult.getResultObject(),
				result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionModel>> findGoodsSelectionListByGoodsId(
			final String clientIp, final String clientName, final long goodsId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionListByGoodsId");
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		  .addMetaData("start", "start");
		writeSysBusLog(lm,false);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
						lm.addMetaData("init","init,after").addMetaData("init[findGoodsSelectionListByGoodsId]", goodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
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
						List<GoodsSelectionModel> result = goodsInventoryDomainRepository
								.queryGoodsSelectionListByGoodsId(goodsId);
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
		writeSysBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", "end")
				,false);
		writeSysLog(lm.toJson());
		return new CallResult<List<GoodsSelectionModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSelectionModel>) qresult.getResultObject(),
				result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSuppliersModel>> findGoodsSuppliersListByGoodsId(
			final String clientIp, final String clientName, final long goodsId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionListByGoodsId");
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		   .addMetaData("start", "start");
		writeSysBusLog(lm,false);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
						lm.addMetaData("init","init,after").addMetaData("init[findGoodsSuppliersListByGoodsId]", goodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
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
		writeSysBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", "end")
				,false);
		writeSysLog(lm.toJson());
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
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionListBySelectionIdList");
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		  .addMetaData("selectionIdList", selectionIdList)
		   .addMetaData("start", "start");
		writeSysBusLog(lm,false);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);

						lm.addMetaData("init","init,after").addMetaData("init[findGoodsSelectionListBySelectionIdList]", goodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
						}
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
									InventoryQueryEnum.SYS_ERROR, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		writeSysBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", "end")
				,false);
		writeSysLog(lm.toJson());
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
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSuppliersListBySuppliersIdList");
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
				.addMetaData("goodsId", goodsId)
		        .addMetaData("suppliersIdList", suppliersIdList)
		         .addMetaData("start", "start");
		writeSysBusLog(lm,false);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						
						InventoryQueryEnum enumRes = null;
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
						lm.addMetaData("init","init,after").addMetaData("init[findGoodsSuppliersListBySuppliersIdList]", goodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
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
		writeSysBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", "end")
				,false);
		writeSysLog(lm.toJson());
		return new CallResult<List<GoodsSuppliersModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSuppliersModel>) qresult.getResultObject(),
				result.getThrowable());
	}
	
	@Override
	public CallResult<WmsIsBeDeliveryModel> findWmsIsBeDeliveryByWmsGoodsId(
			final String clientIp, final String clientName, final String wmsGoodsId,final String isBeDelivery) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findWmsIsBeDeliveryByWmsGoodsId");
		lm.addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("wmsGoodsId", wmsGoodsId)
		  .addMetaData("start", "start");
		writeSysBusLog(lm,false);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						
						InventoryQueryEnum enumRes = null;
				
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initWmsCheck(wmsGoodsId,isBeDelivery,lm);
						
						lm.addMetaData("init","init,after").addMetaData("init[findWmsIsBeDeliveryByWmsGoodsId]", wmsGoodsId).addMetaData("message", resultEnum.getDescription());
						writeBusInitLog(lm,true);
						
						if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
							return TuanCallbackResult.failure(resultEnum.getCode(), null, resultEnum.getDescription());
						}
						if (StringUtils.isEmpty(wmsGoodsId)) {
							enumRes = InventoryQueryEnum.INVALID_WMSGOODSID;
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
									InventoryQueryEnum.SYS_ERROR, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}

					}

				}

				);
		final int resultCode = result.getResultCode();
		final InventoryQueryResult qresult = (InventoryQueryResult) result.getBusinessObject();
		writeSysBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", "end")
				,false);
		writeSysLog(lm.toJson());
		
		return new CallResult<WmsIsBeDeliveryModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(WmsIsBeDeliveryModel) qresult.getResultObject(),
				result.getThrowable());
	}
	
	//初始化检查
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum initCheck(long goodsId,LogModel lm) {
		//初始化加分布式锁
		lm.addMetaData("initCheck","initCheck,start").addMetaData("initCheck[" + (goodsId) + "]", goodsId);
		writeBusInitLog(lm,false);
		LockResult<String> lockResult = null;
		CreateInventoryResultEnum resultEnum = null;
		String key = DLockConstants.INIT_LOCK_KEY+"_goodsId_" + goodsId;
			try {
				lockResult = dLock.lockManualByTimes(key, 5000L, 5);
				if (lockResult == null
						|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
								.getCode()) {
					writeBusInitLog(
							lm.setMethod("dLock").addMetaData("errorMsg",
									goodsId), false);
				}
				InventoryInitDomain create = new InventoryInitDomain(goodsId,
						lm);
				//注入相关Repository
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
				//create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
				resultEnum = create.businessExecute();
			} finally{
				dLock.unlockManual(key);
			}
			lm.addMetaData("result", resultEnum);
			lm.addMetaData("result", "end");
			writeBusInitLog(lm,false);
			return resultEnum;
		}

	    //初始化物流库存库存
		@SuppressWarnings("unchecked")
		public CreateInventoryResultEnum initWmsCheck(String wmsGoodsId,String isBeDelivery,LogModel lm) {
					//初始化加分布式锁
					lm.addMetaData("GoodsInventoryQueryServiceImpl initWmsCheck","initWmsCheck,start").addMetaData("initWmsCheck[" + (wmsGoodsId) + "]", wmsGoodsId);
					writeBusInitLog(lm,false);
					LockResult<String> lockResult = null;
					CreateInventoryResultEnum resultEnum = null;
					String key = DLockConstants.INIT_LOCK_KEY+"_wmsGoodsId_" + wmsGoodsId;
					try {
						lockResult = dLock.lockManualByTimes(key, DLockConstants.INIT_LOCK_TIME, DLockConstants.INIT_LOCK_RETRY_TIMES);
						if (lockResult == null
								|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
										.getCode()) {
							writeBusInitLog(
									lm.addMetaData("GoodsInventoryQueryServiceImpl initWmsCheck dLock errorMsg",
											wmsGoodsId), true);
						}
						InventoryInitDomain create = new InventoryInitDomain();
						//注入相关Repository
						create.setWmsGoodsId(wmsGoodsId);
						create.setIsBeDelivery(isBeDelivery);
						create.setLm(lm);
						create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
						create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
						//create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
						resultEnum = create.business4WmsExecute();
					} finally{
						dLock.unlockManual(key);
					}
					lm.addMetaData("result", resultEnum);
					lm.addMetaData("result", "end");
					writeBusInitLog(lm,false);
					return resultEnum;
				}

		@Override
		public CallResult<GoodsBaseModel> findSalesCountByGoodsBaseId(
				String clientIp, String clientName, final String goodsBaseIdStr) {
			final LogModel lm = LogModel
					.newLogModel("GoodsInventoryQueryService.findGoodsInventoryByGoodsId");
			lm.addMetaData("clientIp", clientIp)
			  .addMetaData("clientName", clientName)
			  .addMetaData("goodsBaseId", goodsBaseIdStr)
			  .addMetaData("start", "start");
			writeSysBusLog(lm,false);
			final long goodsBaseId = Long.valueOf(goodsBaseIdStr);
			TuanCallbackResult result = this.inventoryServiceTemplate
					.execute(new InventoryQueryServiceCallback() {
						@Override
						public TuanCallbackResult preHandler() {
							
							InventoryQueryEnum enumRes = null;
							if (StringUtils.isEmpty(goodsBaseIdStr)||!StringUtils.isNumeric(goodsBaseIdStr)) {
								enumRes = InventoryQueryEnum.INVALID_GOODSBASEID;
							}
							if(goodsBaseId>0){
								GoodsBaseInventoryDO tmpBaseDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
								if(tmpBaseDO == null) {
									GoodsBaseInventoryDO sourceBaseDO =synInitAndAsynUpdateDomainRepository.selectInventoryBase4Init(goodsBaseId);
									if(sourceBaseDO!=null) {
										try {
											synInitAndAsynUpdateDomainRepository.saveGoodsBaseInventoryDO(sourceBaseDO);
										} catch (Exception e) {
											enumRes = InventoryQueryEnum.SYS_ERROR;
										}
									}else {
										enumRes = InventoryQueryEnum.SYS_ERROR;
									}
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
							GoodsBaseInventoryDO gsModel = goodsInventoryDomainRepository
									.queryGoodsBaseById(goodsBaseId);
							if(gsModel==null){
								 CallResult<GoodsBaseInventoryDO> callResult =synInitAndAysnMysqlService.selectGoodsBaseInventory(goodsBaseId);
								 gsModel = (GoodsBaseInventoryDO) callResult.getBusinessObject();
							}
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
			writeSysBusLog(lm.addMetaData("result", result.isSuccess())
					.addMetaData("resultCode", result.getResultCode())
					.addMetaData("qresult", qresult.getResultObject())
					.addMetaData("end", "end")
					,false);
			writeSysLog(lm.toJson());
			
			return new CallResult<GoodsBaseModel>(result.isSuccess(),
					PublicCodeEnum.valuesOf(resultCode),
					(GoodsBaseModel) qresult.getResultObject(),
					result.getThrowable());
		}
}
