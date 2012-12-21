package com.tuan.pmt.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.tuan.core.common.page.PageList;
import com.tuan.pmt.client.YouhuiCenterFacade;
import com.tuan.pmt.model.CpnCouponModel;
import com.tuan.pmt.model.constant.res.CouponPlatTypeEnum;
import com.tuan.pmt.model.result.CallResult;
import com.tuan.pmt.model.result.CouponQueryResult;
import com.tuan.pmt.model.result.CouponUpdateResult;

@ContextConfiguration(locations = { 
	"classpath:/bean/tuan-youhui-center-bean.xml",
	"classpath:/log4j.xml"
})
public class YouhuiCenterFacadeImplTest extends AbstractJUnit4SpringContextTests{
	@Autowired
	private YouhuiCenterFacade youhuiCenterFacade;

	
	//根据id查询
	@Test
	public void queryCouponByCouponIdTest(){
		long couponId = 2L;
		CallResult<CouponQueryResult<CpnCouponModel>> res = youhuiCenterFacade.queryCouponByCouponId(couponId);
		System.out.println(res);
	}
	
	//根据code查询
	@Test
    public void queryCouponByCouponCodeTest(){
        String couponCode = "1111112";
        CallResult<CouponQueryResult<CpnCouponModel>> res = youhuiCenterFacade.queryCouponByCouponCode(couponCode);
        System.out.println(res);
    }
    
	//根据用户id 状态列表查询
	@Test
    public void queryCouponByUserIdTest(){
        long userId = 4L;
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        
        int currPage = 1;
        int pageSize = 10;
        
        CallResult<CouponQueryResult<PageList>> cr= youhuiCenterFacade.queryCouponsByUserId(userId, list, CouponPlatTypeEnum.PLATTYPE_WEB.getName(), currPage, pageSize);
        System.out.println("queryCouponByUserIdTest  "+cr);
    }
	
	//绑定
    @Test
    public void bindCoupon2UserTest(){
        long userId = 4L;
        String couponCode = "1111112";
        CallResult<CouponUpdateResult> callResult = youhuiCenterFacade.bindCoupon2User(userId, String.valueOf(couponCode), CouponPlatTypeEnum.PLATTYPE_WEB.getName());
        System.out.println("bindCoupon2UserTest  "+callResult);
    }
    
	
	public YouhuiCenterFacade getYouhuiCenterFacade() {
		return youhuiCenterFacade;
	}
	public void setYouhuiCenterFacade(YouhuiCenterFacade youhuiCenterFacade) {
		this.youhuiCenterFacade = youhuiCenterFacade;
	}
}
