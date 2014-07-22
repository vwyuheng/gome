package com.tuan.inventory.client.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.client.AbstractInventoryClientService;
import com.tuan.inventory.client.GoodsInventoryQueryClientService;
import com.tuan.inventory.client.support.CacheDAOService;
import com.tuan.inventory.client.support.utils.LogModel;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.res.InventoryQueryEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryQueryResult;
import com.tuan.inventory.service.InventoryQueryServiceCallback;

public class GoodsInventoryQueryClientServiceImpl extends AbstractInventoryClientService implements GoodsInventoryQueryClientService {
	private static final Logger logger=LoggerFactory.getLogger("INVENTORY.CLIENT.LOG");
	@Resource
	private CacheDAOService cacheDAOService;
	@Override
	public CallResult<GoodsInventoryModel> findGoodsInventoryByGoodsId(final String clientIp,
			final String clientName,  final long goodsId) {
		long startTime = System.currentTimeMillis();
		String method = "GoodsInventoryQueryClientServiceImpl.findGoodsInventoryByGoodsId";
		final LogModel lm = LogModel
				.newLogModel(method);
		lm.addMetaData("start", startTime)
		   .addMetaData("clientIp", clientIp)
		  .addMetaData("clientName", clientName)
		  .addMetaData("goodsId", goodsId)
		  .addMetaData("start", "start");
		logger.info(lm.toJson(true));
		TuanCallbackResult result = this.inventoryServiceTemplate
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
						GoodsInventoryModel gsModel = cacheDAOService
								.queryGoodsInventory(goodsId);
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
				.addMetaData("resultCode", result.getResultCode())
				.addMetaData("qresult", qresult.getResultObject())
				.addMetaData("end", endTime);
		logger.info(lm.toJson(true));
		
		return new CallResult<GoodsInventoryModel>(result.isSuccess(),
				PublicCodeEnum.valuesOf(resultCode),
				(GoodsInventoryModel) qresult.getResultObject(),
				result.getThrowable());
	}

		@Override
		public CallResult<GoodsBaseModel> findSalesCountByGoodsBaseId(
				String clientIp, String clientName, final long goodsBaseId) {
			long startTime = System.currentTimeMillis();
			String method = "GoodsInventoryQueryClientServiceImpl.findSalesCountByGoodsBaseId";
			final LogModel lm = LogModel
					.newLogModel(method);
			lm.addMetaData("start", startTime)
			  .addMetaData("clientIp", clientIp)
			  .addMetaData("clientName", clientName)
			  .addMetaData("goodsBaseId", goodsBaseId)
			  .addMetaData("start", "start");
			logger.info(lm.toJson(true));
			
			TuanCallbackResult result = this.inventoryServiceTemplate
					.execute(new InventoryQueryServiceCallback() {
						@Override
						public TuanCallbackResult preHandler() {
							
							InventoryQueryEnum enumRes = null;
							
							if(goodsBaseId<=0){
								enumRes = InventoryQueryEnum.INVALID_GOODSBASEID;
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
							GoodsBaseModel baseModel = cacheDAOService
									.queryGoodsBaseById(goodsBaseId);
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
			logger.info(lm.toJson(true));
			
			return new CallResult<GoodsBaseModel>(result.isSuccess(),
					PublicCodeEnum.valuesOf(resultCode),
					(GoodsBaseModel) qresult.getResultObject(),
					result.getThrowable());
		}
}
