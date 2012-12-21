package com.tuan.pmt.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tuan.core.common.lang.cache.remote.SpyMemcachedClient;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.domain.CouponQueryDomain;
import com.tuan.pmt.domain.CouponUpdateDomain;
import com.tuan.pmt.domain.repository.CouponQueryDomainRepository;
import com.tuan.pmt.domain.repository.CouponUpdateDomainRepository;
import com.tuan.pmt.domain.support.util.DateUtils;
import com.tuan.pmt.domain.support.util.LogModel;
import com.tuan.pmt.model.constant.cache.CacheKeyEnum;
import com.tuan.pmt.model.constant.res.CouponPlatTypeEnum;
import com.tuan.pmt.model.constant.res.CouponUpdateResEnum;
import com.tuan.pmt.model.constant.status.CouponBatchStatusEnum;
import com.tuan.pmt.model.constant.status.CouponStatusEnum;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponUpdateResult;
import com.tuan.pmt.service.CouponServiceCallback;
import com.tuan.pmt.service.CouponUpdateService;

public class CouponUpdateServiceImpl extends AbstractService implements CouponUpdateService {
	private CouponQueryDomainRepository couponQueryDomainRepository;
	private CouponUpdateDomainRepository couponUpdateDomainRepository;
	private SpyMemcachedClient youhuiSpyMemcachedClient;

	@Override
	public CallResult<CouponUpdateResult> bindCoupon2User(final long userId, final String couponCode,
			final String platType) {
		final LogModel lm = LogModel.newLogModel("CouponUpdateService.bindCoupon2User");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("couponCode", couponCode)
				.addMetaData("platType", platType).toJson());
		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(
		new CouponServiceCallback() {
			CouponUpdateResEnum errorResultEnum = null;
			@Override
			public TuanCallbackResult executeParamsCheck() {
				if (userId <= 0) {
					errorResultEnum = CouponUpdateResEnum.INVALID_USERID;
				}
				if (couponCode == null || couponCode.equals("")) {
					errorResultEnum = CouponUpdateResEnum.INVALID_CODE;
				}
				if (CouponPlatTypeEnum.valueOfEnum(platType) == CouponPlatTypeEnum.UNKNOW) {
					errorResultEnum = CouponUpdateResEnum.INVALID_PLATTYPE;
				}
				if (errorResultEnum != null) {
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, new CouponUpdateResult(
							errorResultEnum.getName(), null));
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CpnCouponDO cpnCouponDO = couponQueryDomainRepository.queryCpnCouponDOByCode(couponCode);
				// 验证代金券号码
				if (cpnCouponDO == null) {
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, new CouponUpdateResult(
							CouponUpdateResEnum.INVALID_CODE.getName(), null));
				}
				// 判断该代金券是否绑定
				if(		(cpnCouponDO.getUserId() != null && cpnCouponDO.getUserId() > 0) 
						|| cpnCouponDO.getStatus() == null 
						|| cpnCouponDO.getStatus()!= CouponStatusEnum.UNBOUND.getCode()){
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, new CouponUpdateResult(
							CouponUpdateResEnum.INVALID_ISBIND.getName(), null));
				}
				CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
				couponQueryDomain.setCpnCouponDO(cpnCouponDO);
				couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(cpnCouponDO.getBatchId());

				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponDO(cpnCouponDO);
				couponUpdateDomain.setCpnCouponBatchDO(cpnCouponBatchDO);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				couponUpdateDomain.setCouponQueryDomain(couponQueryDomain);
				// 验证批次是否存在
				if (cpnCouponBatchDO == null) {
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, new CouponUpdateResult(
							CouponUpdateResEnum.INVALID_BATCHID.getName(), null));
				}
				// 该用户已绑定该批次数量
				int couponCount = couponQueryDomain.queryCouponCountByUserIdAndBatchId(userId, cpnCouponDO.getBatchId()); 
				// 验证批次相关
				errorResultEnum = couponUpdateDomain.validateCouponBatch(platType, couponCount);
				if (errorResultEnum != null) {
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, new CouponUpdateResult(
							errorResultEnum.getName(), null));
				}
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}

			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.bindCoupon2User(userId)) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(),
							couponUpdateDomain.makeCouponModel());
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(),
							couponUpdateDomain.makeCouponModel());
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}

			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", ((CouponUpdateResult) result.getBusinessObject()).getResult()).toJson(false));
		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<CouponUpdateResult> couponFreeze(final long userId, final long couponId, final long orderId) {
		LogModel lm = LogModel.newLogModel("CouponUpdateService.couponFreeze");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("couponId", couponId)
				.addMetaData("orderId", orderId).toJson());
		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(new CouponServiceCallback() {
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CouponUpdateResEnum errorResult = null;
				if (userId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_USERID;
				}
				if (couponId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_COUPONID;
				}
				if (orderId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_ORDERID;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CpnCouponDO cpnCouponDO = couponQueryDomainRepository.queryCouponDoByCouponId(couponId);
				CouponUpdateResEnum errorEnum = null;
				if (cpnCouponDO == null) {
					errorEnum = CouponUpdateResEnum.INVALID_COUPONID;
				}
				if (userId != cpnCouponDO.getUserId()) {
					errorEnum = CouponUpdateResEnum.INVALID_USER;
				}
				if (cpnCouponDO.getStatus().intValue() == CouponStatusEnum.FREEZED.getCode()) {
					errorEnum = CouponUpdateResEnum.SAME_STATE;
				}
				if (cpnCouponDO.getStatus() != CouponStatusEnum.UNUSED.getCode()) {
					errorEnum = CouponUpdateResEnum.INVALID_STATUS;
				}
				CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
				couponQueryDomain.setCpnCouponDO(cpnCouponDO);
				couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
				if (cpnCouponBatchDO.getStatus().intValue() != CouponBatchStatusEnum.CREATED.getCode()) {
					errorEnum = CouponUpdateResEnum.INVALID_STATUS;
				}
				if (DateUtils.checkTimeScope(DateUtils.getNowTimestamp(), cpnCouponBatchDO.getStartTime(),
						cpnCouponBatchDO.getEndTime()) != 0) {
					errorEnum = CouponUpdateResEnum.INVALID_TIME;
				}
				if (errorEnum != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorEnum.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponDO(cpnCouponDO);
				couponUpdateDomain.setCpnCouponBatchDO(cpnCouponBatchDO);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}

			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.couponFreeze(orderId, couponUpdateDomain)) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(),null);
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(),null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}
			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess())
			.addMetaData("resultCode", ((CouponUpdateResult) result.getBusinessObject()).getResult()).toJson(false));
		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<CouponUpdateResult> couponUnFreeze(final long userId, final long couponId) {
		LogModel lm = LogModel.newLogModel("CouponUpdateService.couponUnFreeze");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("couponId", couponId).toJson());

		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(new CouponServiceCallback() {
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CouponUpdateResEnum errorResult = null;
				if (userId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_USERID;
				}
				if (couponId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_COUPONID;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CpnCouponDO cpnCouponDO = couponQueryDomainRepository.queryCouponDoByCouponId(couponId);
				CouponUpdateResEnum errorEnum = null;
				if (cpnCouponDO == null) {
					errorEnum = CouponUpdateResEnum.INVALID_COUPONID;
				}
				if (userId != cpnCouponDO.getUserId()) {
					errorEnum = CouponUpdateResEnum.INVALID_USER;
				}
				if (cpnCouponDO.getStatus() != CouponStatusEnum.FREEZED.getCode()) {
					errorEnum = CouponUpdateResEnum.INVALID_STATUS;
				}
				if (errorEnum != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorEnum.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponDO(cpnCouponDO);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}
			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.couponUnFreeze()) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(),null);
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(),null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}
			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess())
			.addMetaData("resultCode",((CouponUpdateResult) result.getBusinessObject()).getResult()).toJson(false));
		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<CouponUpdateResult> couponUnFreezeByOrderId(final long userId, final long orderId) {
		LogModel lm = LogModel.newLogModel("CouponUpdateService.couponUnFreezeByOrderId");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("orderId", orderId).toJson());

		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(new CouponServiceCallback() {
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CouponUpdateResEnum errorResult = null;
				if (userId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_USERID;
				}
				if (orderId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_ORDERID;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				List<CpnCouponDO> cpnCouponDOList = couponQueryDomainRepository.queryCouponDOByOrderId(orderId);
				CouponUpdateResEnum errorEnum = null;
				if (cpnCouponDOList == null) {
					errorEnum = CouponUpdateResEnum.INVALID_ORDERID;
				}
				for (CpnCouponDO cpnCouponDO : cpnCouponDOList) {
					if (userId != cpnCouponDO.getUserId()) {
						errorEnum = CouponUpdateResEnum.INVALID_USER;
					}
					if (cpnCouponDO.getStatus() != CouponStatusEnum.FREEZED.getCode()) {
						errorEnum = CouponUpdateResEnum.INVALID_STATUS;
					}
				}
				if (errorEnum != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorEnum.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponDOList(cpnCouponDOList);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}

			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.couponUnFreezeByOrderId()) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(),null);
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(),null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}

			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess())
			.addMetaData("resultCode", ((CouponUpdateResult) result.getBusinessObject()).getResult()).toJson(false));
		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<CouponUpdateResult> couponUsed(final long userId, final long couponId) {
		LogModel lm = LogModel.newLogModel("CouponUpdateService.couponUsed");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("couponId", couponId).toJson());

		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(new CouponServiceCallback() {
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CouponUpdateResEnum errorResult = null;
				if (userId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_USERID;
				}
				if (couponId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_COUPONID;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CpnCouponDO cpnCouponDO = couponQueryDomainRepository.queryCouponDoByCouponId(couponId);
				CouponUpdateResEnum errorEnum = null;
				if (cpnCouponDO == null) {
					errorEnum = CouponUpdateResEnum.INVALID_COUPONID;
				}
				if (userId != cpnCouponDO.getUserId()) {
					errorEnum = CouponUpdateResEnum.INVALID_USER;
				}
				if (cpnCouponDO.getStatus().intValue() == CouponStatusEnum.USED.getCode()) {
					errorEnum = CouponUpdateResEnum.SAME_STATE;
				}
				if (cpnCouponDO.getStatus().intValue() != CouponStatusEnum.FREEZED.getCode()) {
					errorEnum = CouponUpdateResEnum.INVALID_STATUS;
				}
				CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
				couponQueryDomain.setCpnCouponDO(cpnCouponDO);
				couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(cpnCouponDO.getBatchId());
				if (cpnCouponBatchDO.getStatus().intValue() != CouponBatchStatusEnum.CREATED.getCode()) {
					errorEnum = CouponUpdateResEnum.INVALID_STATUS;
				}
				if (DateUtils.checkTimeScope(DateUtils.getNowTimestamp(), cpnCouponBatchDO.getStartTime(),
						cpnCouponBatchDO.getEndTime()) != 0) {
					errorEnum = CouponUpdateResEnum.INVALID_TIME;
				}
				if (errorEnum != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorEnum.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponDO(cpnCouponDO);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}

			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.couponUsed()) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(), null);
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}

			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess())
			.addMetaData("resultCode", ((CouponUpdateResult) result.getBusinessObject()).getResult()).toJson(false));
		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}
	
	@Override
	public CallResult<CouponUpdateResult> couponRefundByOrderId(
			final long userId, final long orderId, final long invalidAdminId) {
		LogModel lm = LogModel.newLogModel("CouponUpdateService.couponRefundByOrderId");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("orderId", orderId).toJson());
		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(new CouponServiceCallback() {
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CouponUpdateResEnum errorResult = null;
				if (userId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_USERID;
				}
				if (orderId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_ORDERID;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				List<CpnCouponDO> cpnCouponDOList = couponQueryDomainRepository.queryCouponDOByOrderId(orderId);
				CouponUpdateResEnum errorResult = null;
				if (cpnCouponDOList == null || cpnCouponDOList.isEmpty()) {
					errorResult = CouponUpdateResEnum.INVALID_ORDERID;
				}
				for (CpnCouponDO cpnCouponDO : cpnCouponDOList) {
					if (cpnCouponDO.getStatus().intValue() == CouponStatusEnum.CANCEL.getCode()) {
						errorResult = CouponUpdateResEnum.SAME_STATE;
					}
					if (cpnCouponDO.getStatus().intValue() == CouponStatusEnum.FREEZED.getCode()) {
						errorResult = CouponUpdateResEnum.INVALID_STATUS;
					}
					if (userId != cpnCouponDO.getUserId()) {
						errorResult = CouponUpdateResEnum.INVALID_USER;
					}
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponDOList(cpnCouponDOList);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}

			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.couponRefundByOrderId(invalidAdminId)) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(), null);
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}

			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess())
			.addMetaData("resultCode", ((CouponUpdateResult) result.getBusinessObject()).getResult()).toJson(false));
		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<CouponUpdateResult> couponCancelByOrderId(final long userId, final long orderId,
			final long invalidAdminId, final String invalidReason) {
		LogModel lm = LogModel.newLogModel("CouponUpdateService.couponCancel");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("orderId", orderId).toJson());
		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(new CouponServiceCallback() {
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CouponUpdateResEnum errorResult = null;
				if (userId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_USERID;
				}
				if (orderId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_ORDERID;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				List<CpnCouponDO> cpnCouponDOList = couponQueryDomainRepository.queryCouponDOByOrderId(orderId);
				CouponUpdateResEnum errorResult = null;
				if (cpnCouponDOList == null || cpnCouponDOList.isEmpty()) {
					errorResult = CouponUpdateResEnum.INVALID_ORDERID;
				}
				for (CpnCouponDO cpnCouponDO : cpnCouponDOList) {
					if (cpnCouponDO.getStatus().intValue() == CouponStatusEnum.CANCEL.getCode()) {
						errorResult = CouponUpdateResEnum.SAME_STATE;
					}
					if (cpnCouponDO.getStatus().intValue() == CouponStatusEnum.FREEZED.getCode()) {
						errorResult = CouponUpdateResEnum.INVALID_STATUS;
					}
					if (userId != cpnCouponDO.getUserId()) {
						errorResult = CouponUpdateResEnum.INVALID_USER;
					}
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponDOList(cpnCouponDOList);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}

			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.couponCancelByOrderId(invalidAdminId,invalidReason)) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(), null);
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}

			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess())
			.addMetaData("resultCode", ((CouponUpdateResult) result.getBusinessObject()).getResult()).toJson(false));
		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<CouponUpdateResult> couponCancel(final long userId, final long couponId,
			final long invalidAdminId, final String invalidReason) {
		LogModel lm = LogModel.newLogModel("CouponUpdateService.couponCancel");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("couponId", couponId).toJson());

		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(new CouponServiceCallback() {
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CouponUpdateResEnum errorResult = null;
				if (userId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_USERID;
				}
				if (couponId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_COUPONID;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CpnCouponDO cpnCouponDO = couponQueryDomainRepository.queryCouponDoByCouponId(couponId);
				CouponUpdateResEnum errorResult = null;
				if (cpnCouponDO == null) {
					errorResult = CouponUpdateResEnum.INVALID_COUPONID;
				}
				if (cpnCouponDO.getStatus().intValue() == CouponStatusEnum.CANCEL.getCode()) {
					errorResult = CouponUpdateResEnum.SAME_STATE;
				}
				if (cpnCouponDO.getStatus().intValue() == CouponStatusEnum.FREEZED.getCode()) {
					errorResult = CouponUpdateResEnum.INVALID_STATUS;
				}
				if (userId != cpnCouponDO.getUserId()) {
					errorResult = CouponUpdateResEnum.INVALID_USER;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponDO(cpnCouponDO);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}

			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.couponCancel(invalidAdminId, invalidReason)) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(), null);
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}

			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess())
			.addMetaData("resultCode", ((CouponUpdateResult) result.getBusinessObject()).getResult()).toJson(false));
		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}

	@Override
	public CallResult<CouponUpdateResult> couponBatchCancel(final long batchId, final long invalidAdminId,
			final String invalidReason) {
		LogModel lm = LogModel.newLogModel("CouponUpdateService.couponBatchCancel");
		writeSysLog(lm.addMetaData("batchId", batchId).toJson());
		final Map<String, Object> map = new HashMap<String, Object>(); // 用以在模版间传递数据对象
		TuanCallbackResult result = this.couponServiceTemplate.execute(new CouponServiceCallback() {
			@Override
			public TuanCallbackResult executeParamsCheck() {
				CouponUpdateResEnum errorResult = null;
				if (batchId <= 0) {
					errorResult = CouponUpdateResEnum.INVALID_BATCHID;
				}
				if (errorResult != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorResult.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				} else {
					return TuanCallbackResult.success();
				}
			}

			@Override
			public TuanCallbackResult executeBusiCheck() {
				CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
				couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				CpnCouponBatchDO cpnCouponBatchDO = couponQueryDomain.queryCouponBatchByBatchId(batchId);
				CouponUpdateResEnum errorEnum = null;
				if (cpnCouponBatchDO == null) {
					errorEnum = CouponUpdateResEnum.INVALID_BATCHID;
				}
				if (cpnCouponBatchDO.getStatus().intValue() == CouponBatchStatusEnum.CANCEL.getCode()) {
					errorEnum = CouponUpdateResEnum.SAME_STATE;
				}
				if (errorEnum != null) {
					CouponUpdateResult res = new CouponUpdateResult(errorEnum.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
				CouponUpdateDomain couponUpdateDomain = new CouponUpdateDomain();
				couponUpdateDomain.setCpnCouponBatchDO(cpnCouponBatchDO);
				couponUpdateDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
				couponUpdateDomain.setCouponUpdateDomainRepository(couponUpdateDomainRepository);
				couponUpdateDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
				map.put("couponUpdateDomain", couponUpdateDomain);
				return TuanCallbackResult.success();
			}

			@Override
			public TuanCallbackResult executeAction() {// 业务执行，带事务
				CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
				if (couponUpdateDomain.batchCancel(invalidAdminId, invalidReason)) {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.SUCCESS.getName(), null);
					return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
				} else {
					CouponUpdateResult res = new CouponUpdateResult(CouponUpdateResEnum.DB_ERROR.getName(), null);
					return TuanCallbackResult.failure(TuanCallbackResult.FAILURE, null, res);
				}
			}

			@Override
			public void executeAfter() {
			}
		});
		writeSysLog(lm.addMetaData("result", result.isSuccess()).addMetaData("resultCode", result.getResultCode())
				.toJson(false));
		// 执行成功后，删除缓存
		if (result.isSuccess()) {
			CouponUpdateDomain couponUpdateDomain = (CouponUpdateDomain) map.get("couponUpdateDomain");
			if (couponUpdateDomain.getCpnCouponDO() != null) {
				deleteCache(couponUpdateDomain.getCpnCouponDO());
			}
		}

		return new CallResult<CouponUpdateResult>(result.isSuccess(), (CouponUpdateResult) result.getBusinessObject(),
				result.getThrowable());
	}

	public CouponQueryDomainRepository getCouponQueryDomainRepository() {
		return couponQueryDomainRepository;
	}

	public void setCouponQueryDomainRepository(CouponQueryDomainRepository couponQueryDomainRepository) {
		this.couponQueryDomainRepository = couponQueryDomainRepository;
	}

	public CouponUpdateDomainRepository getCouponUpdateDomainRepository() {
		return couponUpdateDomainRepository;
	}

	public void setCouponUpdateDomainRepository(CouponUpdateDomainRepository couponUpdateDomainRepository) {
		this.couponUpdateDomainRepository = couponUpdateDomainRepository;
	}

	/**
	 * 删除缓存信息
	 * 
	 * @param cpnCouponDO
	 */
	private void deleteCache(CpnCouponDO cpnCouponDO) {
		if (cpnCouponDO.getBatchId() != null && cpnCouponDO.getBatchId() > 0) {
			youhuiSpyMemcachedClient.delete(CacheKeyEnum.CACHE_BATCH_KEY.getName() + cpnCouponDO.getBatchId());
		}
	}

	public void setYouhuiSpyMemcachedClient(SpyMemcachedClient youhuiSpyMemcachedClient) {
		this.youhuiSpyMemcachedClient = youhuiSpyMemcachedClient;
	}
}
