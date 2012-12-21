package com.tuan.pmt.service.impl.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.tuan.core.common.datasource.DataSourceContextHolder;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourceType;
import com.tuan.pmt.model.constant.res.CouponPlatTypeEnum;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponUpdateResult;
import com.tuan.pmt.service.CouponUpdateService;

@ContextConfiguration(locations = { 
		"classpath:/bean/youhuiTest-dao-env-bean.xml",
		"classpath:/bean/youhuiTest-dao-test-env-bean.xml",
		"classpath:/bean/youhui-dao-bean.xml",
		"classpath:/bean/youhui-domain-bean.xml",
		"classpath:/bean/youhuiTest-service-impl-bean.xml",
		"classpath:/bean/youhui-cache-impl-bean.xml",
		"classpath:/log4j.xml"
})
public class CouponUpdateServiceImplTest extends AbstractJUnit4SpringContextTests{
	@Autowired
	CouponUpdateService couponUpdateService;
	
	@Before
	public void before(){
		DataSourceContextHolder.setDataSourceType(MSDataSourceType.MASTER);	
	}
	
	@Test
	public void bindCoupon2User(){
		long userId = 4L;
		String couponCode = "bbbbbbbb";
		CallResult<CouponUpdateResult> callResult = couponUpdateService.bindCoupon2User(userId, String.valueOf(couponCode), CouponPlatTypeEnum.PLATTYPE_WEB.getName());
		System.out.println(callResult);
	}
	
	@Test
	public void couponFreeze(){
		long userId = 4L;
		long couponId = 2474L;
		long orderId = 123L;
		CallResult<CouponUpdateResult> callResult = couponUpdateService.couponFreeze(userId, couponId, orderId);
		System.out.println(callResult);
	}
	
	@Test
	public void couponUnFreeze(){
		long userId = 4L;
		long couponId = 2474L;
		CallResult<CouponUpdateResult> callResult = couponUpdateService.couponUnFreeze(userId, couponId);
		System.out.println(callResult);
	}
	
	@Test
	public void couponUnFreezeByOrderId(){
		long userId = 4L;
		long orderId = 123L;
		CallResult<CouponUpdateResult> callResult = couponUpdateService.couponUnFreezeByOrderId(userId, orderId);
		System.out.println(callResult);
	}
	
	@Test
	public void couponUsed(){
		long userId = 4L;
		long couponId = 2474L;
		CallResult<CouponUpdateResult> callResult = couponUpdateService.couponUsed(userId, couponId);
		System.out.println(callResult);
	}

	@Test
	public void couponCancelByOrderId(){
		long userId = 4L;
		long orderId = 123L;
		long invalidAdminId = 123456L;
		String invalidReason = "가가가가가！";
		CallResult<CouponUpdateResult> callResult = couponUpdateService.couponCancelByOrderId(
				userId, orderId, invalidAdminId, invalidReason);
		System.out.println(callResult);
	}
	
	@Test
	public void couponCancel(){
		long userId = 4L;
		long couponId = 2474L;
		long invalidAdminId = 123456L;
		String invalidReason = "가가가가가！";
		CallResult<CouponUpdateResult> callResult = couponUpdateService.couponCancel(
				userId, couponId, invalidAdminId, invalidReason);
		System.out.println(callResult);
	}
	
	@Test
	public void couponBatchCancel(){
		long batchId = 1L;
		long invalidAdminId = 123456L;
		String invalidReason = "가가가가가！";
		CallResult<CouponUpdateResult> callResult = couponUpdateService.couponBatchCancel(
				batchId, invalidAdminId, invalidReason);
		System.out.println(callResult);
	}
}
