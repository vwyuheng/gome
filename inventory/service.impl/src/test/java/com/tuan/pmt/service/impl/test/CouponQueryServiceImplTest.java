package com.tuan.pmt.service.impl.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.tuan.core.common.datasource.DataSourceContextHolder;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourceType;
import com.tuan.core.common.page.PageList;
import com.tuan.pmt.model.CpnCouponBatchModel;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.constant.res.CouponPlatTypeEnum;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponQueryResult;
import com.tuan.pmt.service.CouponQueryService;

@ContextConfiguration(locations = { 
		"classpath:/bean/youhuiTest-dao-env-bean.xml",
		"classpath:/bean/youhuiTest-dao-test-env-bean.xml",
		"classpath:/bean/youhui-dao-bean.xml",
		"classpath:/bean/youhui-domain-bean.xml",
		"classpath:/bean/youhuiTest-service-impl-bean.xml",
		"classpath:/bean/youhui-cache-impl-bean.xml",
		"classpath:/log4j.xml"
})
public class CouponQueryServiceImplTest extends AbstractJUnit4SpringContextTests{
	@Autowired
	CouponQueryService couponQueryService;
	
	@Before
	public void before(){
		DataSourceContextHolder.setDataSourceType(MSDataSourceType.MASTER);	
	}
	
	@Test
	public void queryCouponsByUserIdTest(){
		long userId = 17203781L;
		List<Integer> statusList = new ArrayList<Integer>();
		CallResult<CouponQueryResult<PageList>> callResult = couponQueryService.queryCouponsByUserId(userId, statusList,CouponPlatTypeEnum.PLATTYPE_WEB.getName(),1,10);
		System.out.println(callResult);
		if(callResult.getCallResult() && callResult.getBusinessResult() != null){
		    PageList pageList = callResult.getBusinessResult().getResultObject();
		    System.out.println("查询分页信息=="+pageList.getPaginator());
            for(Object obj : pageList){
                System.out.println("查询列表=="+(CpnCouponModel)obj);
            }
		}
		
	}
	
	@Test
	public void queryCouponByCouponIdTest(){
		long couponId = 1L;
		CallResult<CouponQueryResult<CpnCouponModel>> callResult = couponQueryService.queryCouponByCouponId(couponId);
		System.out.println(callResult);
	}
	
	@Test
	public void queryCouponByCouponCodeTest(){
	    String couponCode = "rrrrrrrr";
        CallResult<CouponQueryResult<CpnCouponModel>> callResult = couponQueryService.queryCouponByCouponCode(couponCode);
        System.out.println(callResult);
	}
	
	@Test
	public void queryCouponBatchByBatchIdTest(){
		long batchId = 1L;
		CallResult<CouponQueryResult<CpnCouponBatchModel>> callResult = couponQueryService.queryCouponBatchByBatchId(batchId);
        System.out.println(callResult);
	}
	
	@Test
	public void queryCouponByOrderIdTest(){
		long userId = 17203781L;
		long orderId = 47081861L;
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(0);
		CallResult<CouponQueryResult<CpnCouponModel>> callResult = couponQueryService.queryCouponByOrderId(
				userId, orderId, statusList);
		System.out.println(callResult);
	}
}
