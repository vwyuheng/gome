package com.tuan.inventory.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.res.InventoryQueryEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryQueryResult;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.service.InventoryQueryServiceCallback;

public class GoodsInventoryQueryServiceImpl extends AbstractInventoryService implements
		GoodsInventoryQueryService {

	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	
	@Override
	public CallResult<GoodsSelectionModel> findGoodsSelectionBySelectionId(
			final String clientIp, final String clientName, final long userId,
			final long selectionId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionBySelectionId");
		lm.addMetaData("clientName", clientName).addMetaData("userId", userId)
				.addMetaData("selectionId", selectionId);
		TuanCallbackResult result = this.busiServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						InventoryQueryEnum enumRes = null;
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.toJson(false));
		writeSysLog(lm.toJson());
		return new CallResult<GoodsSelectionModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsSelectionModel) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<GoodsSuppliersModel> findGoodsSuppliersBySuppliersId(
			final String clientIp,final String clientName, final long userId, final long suppliersId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSuppliersBySuppliersId");
		lm.addMetaData("clientName", clientName).addMetaData("userId", userId)
				.addMetaData("suppliersId", suppliersId);
		TuanCallbackResult result = this.busiServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
						InventoryQueryEnum enumRes = null;
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.toJson(false));
		writeSysLog(lm.toJson());
		return new CallResult<GoodsSuppliersModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsSuppliersModel) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<GoodsInventoryModel> findGoodsInventoryByGoodsId(final String clientIp,
			final String clientName, final long userId, final long goodsId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsInventoryByGoodsId");
		lm.addMetaData("clientName", clientName).addMetaData("userId", userId)
				.addMetaData("goodsId", goodsId);
		TuanCallbackResult result = this.busiServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.toJson(false));
		writeSysLog(lm.toJson());
		return new CallResult<GoodsInventoryModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsInventoryModel) result.getBusinessObject(),
				result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionModel>> findGoodsSelectionListByGoodsId(
			final String clientIp, final String clientName, final long userId, final long goodsId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionListByGoodsId");
		lm.addMetaData("clientName", clientName).addMetaData("userId", userId)
				.addMetaData("goodsId", goodsId);
		TuanCallbackResult result = this.busiServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.toJson(false));
		writeSysLog(lm.toJson());
		return new CallResult<List<GoodsSelectionModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSelectionModel>) result.getBusinessObject(),
				result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSuppliersModel>> findGoodsSuppliersListByGoodsId(
			final String clientIp, final String clientName, final long userId, final long goodsId) {
		final LogModel lm = LogModel
				.newLogModel("GoodsInventoryQueryService.findGoodsSelectionListByGoodsId");
		lm.addMetaData("clientName", clientName).addMetaData("userId", userId)
				.addMetaData("goodsId", goodsId);
		TuanCallbackResult result = this.busiServiceTemplate
				.execute(new InventoryQueryServiceCallback() {
					@Override
					public TuanCallbackResult preHandler() {
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
		writeBusLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", result.getResultCode())
				.toJson(false));
		writeSysLog(lm.toJson());
		return new CallResult<List<GoodsSuppliersModel>>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(List<GoodsSuppliersModel>) result.getBusinessObject(),
				result.getThrowable());
	}

}
