package com.tuan.inventory.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.domain.InventoryInitDomain;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
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
	//@Resource
	//InitCacheDomainRepository initCacheDomainRepository;
	@Resource
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	@Resource
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	//@Resource
	//GoodsInventoryInitService goodsInventoryInitService;
	
	@Override
	public CallResult<GoodsSelectionModel> findGoodsSelectionBySelectionId(
			final String clientIp, final String clientName, final long goodsId,
			final long selectionId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionBySelectionId");
		lm.addMetaData("clientName", clientName).addMetaData("goodsId", goodsId)
				.addMetaData("selectionId", selectionId);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						InventoryQueryEnum enumRes = null;
						//初始化检查
						/*InventoryCallResult callResult = goodsInventoryInitService.goodsInit(goodsId);
						if(callResult == null){
							enumRes = InventoryQueryEnum.SYS_ERROR;
						}
						if(!(callResult.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
							enumRes = InventoryQueryEnum.DB_ERROR;
						}*/
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.toJson(false));
		writeSysLog(lm.toJson());
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
		lm.addMetaData("clientName", clientName).addMetaData("goodsId", goodsId)
				.addMetaData("suppliersId", suppliersId);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						//初始化检查
						//initCheck(goodsId);
						InventoryQueryEnum enumRes = null;
						//初始化检查
						/*InventoryCallResult callResult = goodsInventoryInitService.goodsInit(goodsId);
						if(callResult == null){
							enumRes = InventoryQueryEnum.SYS_ERROR;
						}
						if(!(callResult.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
							enumRes = InventoryQueryEnum.DB_ERROR;
						}*/
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.toJson(false));
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
		lm.addMetaData("clientName", clientName)
				.addMetaData("goodsId", goodsId);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						//初始化检查
						//initCheck(goodsId);
						InventoryQueryEnum enumRes = null;
						//初始化检查
						/*InventoryCallResult callResult = goodsInventoryInitService.goodsInit(goodsId);
						if(callResult == null){
							enumRes = InventoryQueryEnum.SYS_ERROR;
						}
						if(!(callResult.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
							enumRes = InventoryQueryEnum.DB_ERROR;
						}*/
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.toJson(false));
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
		lm.addMetaData("clientName", clientName)
				.addMetaData("goodsId", goodsId);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						//初始化检查
						//initCheck(goodsId);
						InventoryQueryEnum enumRes = null;
						//初始化检查
						/*InventoryCallResult callResult = goodsInventoryInitService.goodsInit(goodsId);
						if(callResult == null){
							enumRes = InventoryQueryEnum.SYS_ERROR;
						}
						if(!(callResult.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
							enumRes = InventoryQueryEnum.DB_ERROR;
						}*/
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.toJson(false));
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
		lm.addMetaData("clientName", clientName)
				.addMetaData("goodsId", goodsId);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						//初始化检查
						//initCheck(goodsId);
						InventoryQueryEnum enumRes = null;
						//初始化检查
						/*InventoryCallResult callResult = goodsInventoryInitService.goodsInit(goodsId);
						if(callResult == null){
							enumRes = InventoryQueryEnum.SYS_ERROR;
						}
						if(!(callResult.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
							enumRes = InventoryQueryEnum.DB_ERROR;
						}*/
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.toJson(false));
		writeSysLog(lm.toJson());
		return new CallResult<List<GoodsSuppliersModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSuppliersModel>) qresult.getResultObject(),
				result.getThrowable());
	}

	
	//初始化检查
	/*public void initCheck(long goodsId) {
		boolean isInit = false;
		List<GoodsSelectionDO> selectionInventoryList = null;
		List<GoodsSuppliersDO> suppliersInventoryList = null;
			//查询商品库存
		GoodsInventoryDO inventoryDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
			if(inventoryDO==null) {
				//初始化库存
				isInit = true;
				//初始化商品库存信息
				inventoryDO = this.initCacheDomainRepository
						.getInventoryInfoByGoodsId(goodsId);
				//查询该商品分店库存信息
				selectionInventoryList = this.initCacheDomainRepository.querySelectionByGoodsId(goodsId);
				suppliersInventoryList =  this.initCacheDomainRepository.selectGoodsSuppliersInventoryByGoodsId(goodsId);
			}
			
			if(isInit) {
				//保存商品库存
				if(inventoryDO!=null)
				      this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId, inventoryDO);
				//保选型库存
				if(!CollectionUtils.isEmpty(selectionInventoryList))
				      this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(goodsId, selectionInventoryList);
				//保存分店库存
				if(!CollectionUtils.isEmpty(suppliersInventoryList))
				      this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(goodsId, suppliersInventoryList);
			}
			
		}*/
	
	
	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionModel>> findGoodsSelectionListBySelectionIdList(
			final String clientIp, final String clientName, final long goodsId,
			final List<Long> selectionIdList) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionListBySelectionIdList");
		lm.addMetaData("clientName", clientName)
				.addMetaData("goodsId", goodsId)
		        .addMetaData("selectionIdList", selectionIdList);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						//初始化检查
						//initCheck(goodsId);
						InventoryQueryEnum enumRes = null;
						//初始化检查
						/*InventoryCallResult callResult = goodsInventoryInitService.goodsInit(goodsId);
						if(callResult == null){
							enumRes = InventoryQueryEnum.SYS_ERROR;
						}
						if(!(callResult.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
							enumRes = InventoryQueryEnum.DB_ERROR;
						}*/
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.toJson(false));
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
		lm.addMetaData("clientName", clientName)
				.addMetaData("goodsId", goodsId)
		        .addMetaData("suppliersIdList", suppliersIdList);
		TuanCallbackResult result = this.inventoryServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						//初始化检查
						//initCheck(goodsId);
						InventoryQueryEnum enumRes = null;
						//初始化检查
						/*InventoryCallResult callResult = goodsInventoryInitService.goodsInit(goodsId);
						if(callResult == null){
							enumRes = InventoryQueryEnum.SYS_ERROR;
						}
						if(!(callResult.getCode() == CreateInventoryResultEnum.SUCCESS.getCode())){
							enumRes = InventoryQueryEnum.DB_ERROR;
						}*/
						// 初始化检查
						CreateInventoryResultEnum resultEnum =  initCheck(goodsId,lm);
						
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.toJson(false));
		writeSysLog(lm.toJson());
		return new CallResult<List<GoodsSuppliersModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSuppliersModel>) qresult.getResultObject(),
				result.getThrowable());
	}
	
	
	//初始化检查
	public CreateInventoryResultEnum initCheck(long goodsId,LogModel lm) {
			InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
			//注入相关Repository
			//create.setGoodsId(goodsId);
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
			return create.businessExecute();
		}

}
