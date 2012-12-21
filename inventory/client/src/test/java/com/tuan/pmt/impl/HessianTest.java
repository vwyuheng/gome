package com.tuan.pmt.impl;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponQueryResult;
import com.tuan.pmt.service.CouponQueryService;


public class HessianTest {
	public static void main(String[] args) {
		String url = "http://localhost:16050/remoting/couponQuery";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			long couponId = 1L;
			Object obj = factory.create(CouponQueryService.class, url);
			CouponQueryService couponQueryService = (CouponQueryService) obj;
			CallResult<CouponQueryResult<CpnCouponModel>> callResult = 
					couponQueryService.queryCouponByCouponId(couponId);
			if (callResult.getCallResult()) {
				System.out.println(callResult.getBusinessResult().getResult());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
