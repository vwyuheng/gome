package com.tuan.pmt.service.impl.test;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.tuan.core.common.datasource.DataSourceContextHolder;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourceType;
import com.tuan.pmt.model.param.GoodsInfoParam;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponCheckResult;
import com.tuan.pmt.service.CouponCheckService;

@ContextConfiguration(locations = { 
		"classpath:/bean/youhuiTest-dao-env-bean.xml",
		"classpath:/bean/youhuiTest-dao-test-env-bean.xml",
		"classpath:/bean/youhui-dao-bean.xml",
		"classpath:/bean/youhui-domain-bean.xml",
		"classpath:/bean/youhuiTest-service-impl-bean.xml",
		"classpath:/bean/youhui-cache-impl-bean.xml",
		"classpath:/log4j.xml"
})
public class CouponCheckServiceImplTest extends AbstractJUnit4SpringContextTests{
	@Autowired
	CouponCheckService couponCheckService;
	
	@Before
	public void before(){
		DataSourceContextHolder.setDataSourceType(MSDataSourceType.MASTER);	
	}
	
	@Test
	public void checkCouponTest(){
		long userId = 17203781L;
		long couponId = 1L;
		String orderFrom = "web";
		GoodsInfoParam goodsInfoParam = new GoodsInfoParam();
		goodsInfoParam.setCityId(1L);
		goodsInfoParam.setGoodsAmount(new BigDecimal(10));
		goodsInfoParam.setGoodsId(1L);
		goodsInfoParam.setGoodsPrice(new BigDecimal(10));
		goodsInfoParam.setSecondCatId(14L);
		CallResult<CouponCheckResult> res = couponCheckService.checkCoupon(
				userId, couponId, orderFrom, goodsInfoParam);
		System.out.println(res);
	}
}
