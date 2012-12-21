package com.tuan.pmt.service.impl;

import com.tuan.core.common.lang.cache.remote.SpyMemcachedClient;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.domain.CouponCheckDaomain;
import com.tuan.pmt.domain.CouponQueryDomain;
import com.tuan.pmt.domain.repository.CouponQueryDomainRepository;
import com.tuan.pmt.domain.support.util.LogModel;
import com.tuan.pmt.model.constant.res.CouponCheckResEnum;
import com.tuan.pmt.model.param.GoodsInfoParam;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponCheckResult;
import com.tuan.pmt.service.CouponCheckService;

public class CouponCheckServiceImpl extends AbstractService implements CouponCheckService{
	private CouponQueryDomainRepository couponQueryDomainRepository;
	private SpyMemcachedClient youhuiSpyMemcachedClient;
	
	@Override
	public CallResult<CouponCheckResult> checkCoupon(
			final long userId,final long couponId,final String orderFrom,final GoodsInfoParam goodsInfoParam) {
		TuanCallbackResult result = null;
		final LogModel lm = LogModel.newLogModel("CouponCheckService.checkCoupon");
		writeSysLog(lm.addMetaData("userId", userId).addMetaData("couponId", couponId)
				.addMetaData("goodsId", goodsInfoParam.getGoodsId()).toJson());
		try{ 
			result = this.tuanServiceTemplate.executeWithoutTransaction(new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {// 业务执行
					CpnCouponDO cpnCouponDO = couponQueryDomainRepository.queryCouponDoByCouponId(couponId);
					if(cpnCouponDO == null){
						return TuanCallbackResult.failure(TuanCallbackResult.FAILURE,null, 
								new CouponCheckResult(CouponCheckResEnum.INVALID_COUPONID.getName(), null));
					}
					CouponCheckDaomain couponCheckDaomain = new CouponCheckDaomain();
					couponCheckDaomain.setCouponId(couponId);
					couponCheckDaomain.setGoodsInfoParam(goodsInfoParam);
					couponCheckDaomain.setUserId(userId);
					couponCheckDaomain.setOrderFrom(orderFrom);
					couponCheckDaomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
					couponCheckDaomain.setCpnCouponDO(cpnCouponDO);
					CouponQueryDomain couponQueryDomain = new CouponQueryDomain();
					couponQueryDomain.setCpnCouponDO(cpnCouponDO);
					couponQueryDomain.setCouponQueryDomainRepository(couponQueryDomainRepository);
					couponQueryDomain.setYouhuiSpyMemcachedClient(youhuiSpyMemcachedClient);
					couponCheckDaomain.setCouponQueryDomain(couponQueryDomain);
					CouponCheckResEnum checkEnum = couponCheckDaomain.checkCoupon();
					writeSysLog(lm.addMetaData("CheckCouponResMsg:", checkEnum.getName()).toJson());
					CouponCheckResult res = new CouponCheckResult(checkEnum.getName(), null);
					if(checkEnum.compareTo(CouponCheckResEnum.SUCCESS) == 0){
						return TuanCallbackResult.success(TuanCallbackResult.SUCCESS, res);
					}else{
						return TuanCallbackResult.failure(TuanCallbackResult.FAILURE,null, res);
					}
				}
				// 业务检查
				@SuppressWarnings("unused")
				public TuanCallbackResult executeCheck() {
					CouponCheckResEnum errorResultEnum = null;
					if(userId <= 0){
						errorResultEnum = CouponCheckResEnum.INVALID_USERID;
					}
					if(couponId <= 0){
						errorResultEnum = CouponCheckResEnum.INVALID_COUPONID;
					}
					if(goodsInfoParam == null){
						errorResultEnum = CouponCheckResEnum.INVALID_GOODSPARAM;
					}
					if(orderFrom == null){
						errorResultEnum = CouponCheckResEnum.INVALID_ORDERFROM;
					}
					if(errorResultEnum != null){
						writeSysLog(lm.addMetaData("CheckCouponResMsg:", errorResultEnum.getName()).toJson());
						CouponCheckResult res = new CouponCheckResult(errorResultEnum.getName(), null);
						return TuanCallbackResult.failure(TuanCallbackResult.FAILURE,null,res);
					}else{
						return TuanCallbackResult.success();
					}
				}
			}, null);
		}finally{
			writeSysLog(lm.addMetaData("result", result.isSuccess())
				.addMetaData("resultCode", ((CouponCheckResult) result.getBusinessObject()).getResult()).toJson(false));
		}
		return new CallResult<CouponCheckResult>(result.isSuccess(), 
			(CouponCheckResult) result.getBusinessObject(), result.getThrowable());
	}

	public CouponQueryDomainRepository getCouponQueryDomainRepository() {
		return couponQueryDomainRepository;
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
}
