package com.tuan.pmt.client.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.tuan.core.common.page.PageList;
import com.tuan.pmt.client.YouhuiCenterFacade;
import com.tuan.pmt.model.CpnCouponBatchModel;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.constant.res.CouponBatchQueryResEnum;
import com.tuan.pmt.model.constant.res.CouponQueryResEnum;
import com.tuan.pmt.model.param.GoodsInfoParam;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponCheckResult;
import com.tuan.pmt.model.result.CouponQueryResult;
import com.tuan.pmt.model.result.CouponUpdateResult;
import com.tuan.pmt.service.CouponCheckService;
import com.tuan.pmt.service.CouponQueryService;
import com.tuan.pmt.service.CouponUpdateService;

public class YouhuiCenterFacadeImpl implements YouhuiCenterFacade{
	CouponCheckService couponCheckService;
	CouponQueryService couponQueryService;
	CouponUpdateService couponUpdateService;
	
	@Override
	public CallResult<CouponUpdateResult> couponUnFreezeByOrderId(long userId,long orderId) {
		return couponUpdateService.couponUnFreezeByOrderId(userId, orderId);
	}

	@Override
	public CallResult<CouponCheckResult> checkCoupon(long userId,
			long couponId, String orderFrom, GoodsInfoParam goodsInfoParam) {
		return couponCheckService.checkCoupon(userId, couponId, orderFrom, goodsInfoParam);
	}

	@Override
	public CallResult<CouponQueryResult<PageList>> queryCouponsByUserId(
        long userId, List<Integer> statusList,String platType ,final int currPage,final int pageSize) {
	    if(userId < 1){
            return new CallResult<CouponQueryResult<PageList>>(false,new CouponQueryResult<PageList>(CouponQueryResEnum.INVALID_USERID.getName(),null) ,null);
        }
        if(StringUtils.isEmpty(platType)){
            return new CallResult<CouponQueryResult<PageList>>(false,new CouponQueryResult<PageList>(CouponQueryResEnum.INVALID_PLATTYPE.getName(),null) ,null);
        }
        
        return couponQueryService.queryCouponsByUserId(userId,statusList,platType,currPage,pageSize);
	}

	@Override
	public CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponId(long couponId) {
	    if(couponId < 1){
            return new CallResult<CouponQueryResult<CpnCouponModel>>(false,new CouponQueryResult<CpnCouponModel>(CouponQueryResEnum.INVALID_COUPON.getName(),null) ,null);
        }
        return couponQueryService.queryCouponByCouponId(couponId);
	}

	@Override
	public CallResult<CouponQueryResult<CpnCouponBatchModel>> queryCouponBatchByBatchId(long batchId) {
	  //校验入参
        if(batchId <= 0){
            return new CallResult<CouponQueryResult<CpnCouponBatchModel>>(false, new CouponQueryResult<CpnCouponBatchModel>(CouponBatchQueryResEnum.INVALID_BATCHID.getName(),null),null);
        }
		return couponQueryService.queryCouponBatchByBatchId(batchId);
	}

	@Override
	public CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByCouponCode(String couponCode) {
	    if(StringUtils.isEmpty(couponCode)){
            return new CallResult<CouponQueryResult<CpnCouponModel>>(false,new CouponQueryResult<CpnCouponModel>(CouponQueryResEnum.INVALID_COUPONCODE.getName(),null) ,null);
        }
        return couponQueryService.queryCouponByCouponCode(couponCode);
	}
	
	@Override
	public CallResult<CouponQueryResult<CpnCouponModel>> queryCouponByOrderId(long userId, long orderId,List<Integer> statusList) {
		return couponQueryService.queryCouponByOrderId(userId, orderId, statusList);
	}

	@Override
	public CallResult<CouponUpdateResult> bindCoupon2User(long userId,String couponCode, String platType) {
		return couponUpdateService.bindCoupon2User(userId, couponCode, platType);
	}

	@Override
	public CallResult<CouponUpdateResult> couponFreeze(long userId,long couponId, long orderId) {
		return couponUpdateService.couponFreeze(userId, couponId, orderId);
	}

	@Override
	public CallResult<CouponUpdateResult> couponUnFreeze(long userId,long couponId) {
		return couponUpdateService.couponUnFreeze(userId, couponId);
	}

	@Override
	public CallResult<CouponUpdateResult> couponUsed(long userId, long couponId) {
		return couponUpdateService.couponUsed(userId, couponId);
	}

	@Override
	public CallResult<CouponUpdateResult> couponCancel(long userId,
			long couponId, long invalidAdminId, String invalidReason) {
		return couponUpdateService.couponCancel(userId, couponId, invalidAdminId, invalidReason);
	}
	
	@Override
	public CallResult<CouponUpdateResult> couponCancelByOrderId(long userId,
			long orderId, long invalidAdminId, String invalidReason) {
		return couponUpdateService.couponCancelByOrderId(userId, orderId, invalidAdminId, invalidReason);
	}

	@Override
	public CallResult<CouponUpdateResult> couponBatchCancel(long batchId,long invalidAdminId, String invalidReason) {
		return couponUpdateService.couponBatchCancel(batchId, invalidAdminId, invalidReason);
	}
	
	@Override
	public CallResult<CouponUpdateResult> couponRefundByOrderId(long userId, long orderId, long invalidAdminId) {
		return couponUpdateService.couponRefundByOrderId(userId, orderId, invalidAdminId);
	}
	
	public CouponCheckService getCouponCheckService() {
		return couponCheckService;
	}
	public void setCouponCheckService(CouponCheckService couponCheckService) {
		this.couponCheckService = couponCheckService;
	}
	public CouponQueryService getCouponQueryService() {
		return couponQueryService;
	}
	public void setCouponQueryService(CouponQueryService couponQueryService) {
		this.couponQueryService = couponQueryService;
	}
	public CouponUpdateService getCouponUpdateService() {
		return couponUpdateService;
	}
	public void setCouponUpdateService(CouponUpdateService couponUpdateService) {
		this.couponUpdateService = couponUpdateService;
	}
}
