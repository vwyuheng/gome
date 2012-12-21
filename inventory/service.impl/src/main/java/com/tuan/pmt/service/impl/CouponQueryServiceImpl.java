package com.tuan.pmt.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.tuan.core.common.lang.cache.remote.SpyMemcachedClient;
import com.tuan.core.common.page.PageList;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.domain.CouponQueryDomain;
import com.tuan.pmt.domain.repository.CouponQueryDomainRepository;
import com.tuan.pmt.domain.support.util.LogModel;
import com.tuan.pmt.model.CpnCouponBatchModel;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.constant.res.CouponBatchQueryResEnum;
import com.tuan.pmt.model.constant.res.CouponQueryResEnum;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponQueryResult;
import com.tuan.pmt.service.CouponQueryService;

public class CouponQueryServiceImpl extends AbstractService implements CouponQueryService {
	private CouponQueryDomainRepository couponQueryDomainRepository;
	private SpyMemcachedClient youhuiSpyMemcachedClient;

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<CouponQueryResult<PageList>> queryCouponsByUserId(final long userId,
			final List<Integer> statusList, final String platType, final int currPage, final int pageSize) {
		LogModel lm = LogModel.newLogModel(" CouponQueryService.queryCouponsByUserId");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("statusList", statusList)
				.addMetaData("platType", platType).addMetaData("currPage", currPage)
				.addMetaData("pageSize", pageSize).toJson());
		TuanCallbackResult result = null;
		// 用以在模版间传递数据对象
		final Map<String, CouponQueryResult<PageList>> map = new HashMap<String, CouponQueryResult<PageList>>(); 
		try {
			result = this.tuanServiceTemplate.executeWithoutTransaction(new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {// 业务执行
					CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
					couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
					couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
					PageList resList = couponQueryDomain.queryCouponsByUserId(
							userId, statusList, platType,currPage, pageSize);
					CouponQueryResult<PageList> res = new CouponQueryResult<PageList>(
							CouponQueryResEnum.SUCCESS.getName(), resList);
					map.put("couponQueryResult", res);
					return TuanCallbackResult.success(CouponQueryResEnum.valueOf(res.getResult()).getCode(), res);
				}
				// 业务检查
				public TuanCallbackResult executeCheck() {
					CouponQueryResEnum errorResultEnum = null;
					if (userId <= 0) {
						errorResultEnum = CouponQueryResEnum.INVALID_USERID;
					}
					if (!StringUtils.hasText(platType)) {
						errorResultEnum = CouponQueryResEnum.INVALID_PLATTYPE;
					}
					if (errorResultEnum != null) {
						return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null,
								new CouponQueryResult<List<CpnCouponModel>>(errorResultEnum.getName(), null));
					} else {
						return TuanCallbackResult.success();
					}
				}
			}, null);
		} finally {
			writeSysLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", ((CouponQueryResult<PageList>) result.getBusinessObject()).getResult()).toJson(false));
		}
		return new CallResult<CouponQueryResult<PageList>>(result.isSuccess(),
				(CouponQueryResult<PageList>) result.getBusinessObject(), result.getThrowable());
		// 设置缓存 前两页设置缓存
		// if(map.get("couponQueryResult") != null && currPage < 3){
		//
		// StringBuffer buf = new
		// StringBuffer("QUERY_COUPONSLIST_").append(userId);
		// if(statusList != null && statusList.size() > 0 ){
		// buf.append("_").append(statusList.toArray());
		// }
		// buf.append("_").append(platType).append("_").append(currPage).append("_").append(pageSize);
		//
		// PageList pl = map.get("couponQueryResult").getResultObject();
		//
		// if(pl.getPaginator() != null && pl.getPaginator().getItems() > 0){
		//
		// youhuiSpyMemcachedClient.set(buf.toString(),60*5,
		// getJsonByType((CouponQueryResult<PageList>)map.get("couponQueryResult"),new
		// TypeToken<CouponQueryResult<PageList>>(){}.getType()));
		// }else{
		// youhuiSpyMemcachedClient.set(buf.toString(),60,
		// getJsonByType((CouponQueryResult<PageList>)map.get("couponQueryResult"),new
		// TypeToken<CouponQueryResult<PageList>>(){}.getType()));
		// }
		// writeSysLog(lm.addMetaData("set cache key",
		// buf.toString()).toJson(false));
		// }
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponId(final long couponId) {
		LogModel lm = LogModel.newLogModel(" CouponQueryService.queryCouponByCouponId");
		writeSysLog(lm.addMetaData("couponId", couponId).toJson());
		TuanCallbackResult result = null;
		try {
			result = this.tuanServiceTemplate.executeWithoutTransaction(new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {// 业务执行
					CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
					couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
					couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
					CpnCouponDO cpnCouponDO = couponQueryDomainRepository.queryCouponDoByCouponId(couponId);
					couponQueryDomain.setCpnCouponDO(cpnCouponDO);
					CouponQueryResult<CpnCouponModel> res = new CouponQueryResult<CpnCouponModel>(
							CouponQueryResEnum.SUCCESS.getName(),couponQueryDomain.makeCouponModel());
					return TuanCallbackResult.success(CouponQueryResEnum.valueOf(res.getResult()).getCode(), res);
				}
				// 业务检查
				public TuanCallbackResult executeCheck() {
					CouponQueryResEnum errorResultEnum = null;
					if (couponId <= 0) {
						errorResultEnum = CouponQueryResEnum.INVALID_COUPON;
					}
					if (errorResultEnum != null) {
						return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null,
								new CouponQueryResult<CpnCouponModel>(errorResultEnum.getName(), null));
					} else {
						return TuanCallbackResult.success();
					}
				}
			}, null);
		} finally {
			writeSysLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", ((CouponQueryResult<CpnCouponModel>)result.getBusinessObject()).getResult()).toJson(false));
		}
		return new CallResult<CouponQueryResult<CpnCouponModel>>(result.isSuccess(),
				(CouponQueryResult<CpnCouponModel>) result.getBusinessObject(), result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<CouponQueryResult<CpnCouponBatchModel>> queryCouponBatchByBatchId(final long batchId) {
		LogModel lm = LogModel.newLogModel(" CouponQueryService.queryCouponBatchByBatchId");
		writeSysLog(lm.addMetaData("batchId", batchId).toJson());
		TuanCallbackResult result = null;
		try {
			result = this.tuanServiceTemplate.executeWithoutTransaction(new TuanServiceCallback() {
				CouponBatchQueryResEnum resEnum = CouponBatchQueryResEnum.SUCCESS;
				public TuanCallbackResult executeAction() {// 业务执行
					CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
					couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
					couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
					CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(batchId);
					couponQueryDomain.setCpnCouponBatchDO(cpnCouponBatchDO);
					CouponQueryResult<CpnCouponBatchModel> res = new CouponQueryResult<CpnCouponBatchModel>(
							CouponBatchQueryResEnum.SUCCESS.getName(),
							couponQueryDomain.makeBatchModel(cpnCouponBatchDO.getBatchId()));
					return TuanCallbackResult.success(resEnum.getCode(), res);
				}
				// 业务检查
				public TuanCallbackResult executeCheck() {
					CouponBatchQueryResEnum errorResultEnum = null;
					if (batchId <= 0) {
						errorResultEnum = CouponBatchQueryResEnum.INVALID_BATCHID;
					}
					if (errorResultEnum != null) {
						return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null,
								new CouponQueryResult<CpnCouponModel>(errorResultEnum.getName(), null));
					} else {
						return TuanCallbackResult.success();
					}
				}
			}, null);
		} finally {
			writeSysLog(lm.addMetaData("result", result.isSuccess()).addMetaData("resultCode", result.getResultCode())
					.toJson(false));
		}
		return new CallResult<CouponQueryResult<CpnCouponBatchModel>>(result.isSuccess(), 
				(CouponQueryResult<CpnCouponBatchModel>) result.getBusinessObject(),result.getThrowable());
	}

	@SuppressWarnings("unchecked")
	public CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponCode(final String couponCode) {
		LogModel lm = LogModel.newLogModel(" CouponQueryService.queryCouponByCouponId");
		writeSysLog(lm.addMetaData("couponCode", couponCode).toJson());
		TuanCallbackResult result = null;
		try {
			result = this.tuanServiceTemplate.executeWithoutTransaction(new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {// 业务执行
					CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
					couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
					couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
					CpnCouponDO cpnCouponDO = couponQueryDomainRepository.queryCpnCouponDOByCode(couponCode);
					couponQueryDomain.setCpnCouponDO(cpnCouponDO);
					CouponQueryResult<CpnCouponModel> res = new CouponQueryResult<CpnCouponModel>(
							CouponQueryResEnum.SUCCESS.getName(), couponQueryDomain.makeCouponModel());
					return TuanCallbackResult.success(CouponQueryResEnum.valueOf(res.getResult()).getCode(), res);
				}
				// 业务检查
				public TuanCallbackResult executeCheck() {
					CouponQueryResEnum errorResultEnum = null;
					if (!StringUtils.hasText(couponCode)) {
						errorResultEnum = CouponQueryResEnum.INVALID_COUPONCODE;
					}
					if (errorResultEnum != null) {
						return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null,
								new CouponQueryResult<CpnCouponModel>(errorResultEnum.getName(), null));
					} else {
						return TuanCallbackResult.success();
					}
				}
			}, null);
		} finally {
			writeSysLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", ((CouponQueryResult<CpnCouponModel>)result.getBusinessObject()).getResult()).toJson(false));
		}
		return new CallResult<CouponQueryResult<CpnCouponModel>>(result.isSuccess(),
				(CouponQueryResult<CpnCouponModel>) result.getBusinessObject(), result.getThrowable());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByOrderId(final long userId, final long orderId,
			final List<Integer> statusList) {
		LogModel lm = LogModel.newLogModel(" CouponQueryService.queryCouponByOrderId");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("orderId", orderId)
				.addMetaData("statusList", statusList).toJson());
		TuanCallbackResult result = null;
		try {
			result = this.tuanServiceTemplate.executeWithoutTransaction(new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {// 业务执行
					CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
					couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
					couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
					List<CpnCouponDO> cpnCouponDOList = couponQueryDomainRepository.queryCouponDOByOrderId(
							orderId, userId, statusList);
					if(cpnCouponDOList != null && !cpnCouponDOList.isEmpty()){
						couponQueryDomain.setCpnCouponDO(cpnCouponDOList.get(0));
					}
					CouponQueryResult<CpnCouponModel> res = new CouponQueryResult<CpnCouponModel>(
							CouponQueryResEnum.SUCCESS.getName(), couponQueryDomain.makeCouponModel());
					return TuanCallbackResult.success(CouponQueryResEnum.valueOf(res.getResult()).getCode(), res);
				}
				// 业务检查
				public TuanCallbackResult executeCheck() {
					CouponQueryResEnum errorResultEnum = null;
					if (userId <= 0) {
						errorResultEnum = CouponQueryResEnum.INVALID_USERID;
					}
					if (errorResultEnum != null) {
						return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null,
								new CouponQueryResult<CpnCouponModel>(errorResultEnum.getName(), null));
					} else {
						return TuanCallbackResult.success();
					}
				}
			}, null);
		} finally {
			writeSysLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", ((CouponQueryResult<CpnCouponModel>)result.getBusinessObject()).getResult()).toJson(false));
		}
		return new CallResult<CouponQueryResult<CpnCouponModel>>(result.isSuccess(),
				(CouponQueryResult<CpnCouponModel>) result.getBusinessObject(), result.getThrowable());
	}

	public void setCouponQueryDomainRepository(CouponQueryDomainRepository couponQueryDomainRepository) {
		this.couponQueryDomainRepository = couponQueryDomainRepository;
	}
	public SpyMemcachedClient getYouhuiSpyMemcachedClient() {
		return youhuiSpyMemcachedClient;
	}
	public void setYouhuiSpyMemcachedClient(SpyMemcachedClient youhuiSpyMemcachedClient) {
		this.youhuiSpyMemcachedClient = youhuiSpyMemcachedClient;
	}
	public CouponQueryDomainRepository getCouponQueryDomainRepository() {
		return couponQueryDomainRepository;
	}
}
