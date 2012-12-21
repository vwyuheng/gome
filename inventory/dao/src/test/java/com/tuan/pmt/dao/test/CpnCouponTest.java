package com.tuan.pmt.dao.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.tuan.core.common.datasource.DataSourceContextHolder;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourceType;
import com.tuan.pmt.dao.CpnCouponBatchDAO;
import com.tuan.pmt.dao.CpnCouponDAO;
import com.tuan.pmt.dao.CpnCouponLogDAO;
import com.tuan.pmt.dao.data.CpnCouponBatchDO;
import com.tuan.pmt.dao.data.CpnCouponDO;
import com.tuan.pmt.dao.data.CpnCouponLogDO;

@ContextConfiguration(locations = { 
		"classpath:/bean/youhuiTest-dao-test-env-bean.xml",
		"classpath:/bean/youhuiTest-dao-env-bean.xml",
		"classpath:/bean/youhui-dao-bean.xml",
		"classpath:/log4j.xml"
})
public class CpnCouponTest extends AbstractJUnit4SpringContextTests{
	@Autowired
	CpnCouponDAO cpnCouponDAO;
	@Autowired
	CpnCouponBatchDAO cpnCouponBatchDAO;
	@Autowired
	CpnCouponLogDAO cpnCouponLogDAO;
	
	@Before
	public void before(){
		DataSourceContextHolder.setDataSourceType(MSDataSourceType.MASTER);	
	}
	
	@Test
	public void cpnCouponSelectTest(){
		CpnCouponDO cpnCouponDO = cpnCouponDAO.queryCpnCouponByCouponId(1L);
		System.out.println(cpnCouponDO);
	}
	
	@Test
	public void cpnCouponUpdateTest(){
		Long dateLong = (new Date()).getTime();
		Timestamp timestamp = new Timestamp(dateLong);
		CpnCouponDO cpnCouponDO = new CpnCouponDO();
		cpnCouponDO.setCouponId(1L);
		cpnCouponDO.setBatchId(111111111L);
		cpnCouponDO.setBindTime(timestamp);
		cpnCouponDO.setGenTime(timestamp);
		cpnCouponDO.setInvalidAdminId(111111L);
		cpnCouponDO.setInvalidReason("11111111");
		cpnCouponDO.setInvalidTime(timestamp);
		cpnCouponDO.setOrderId(11111111L);
		cpnCouponDO.setPrefix("aa");
		cpnCouponDO.setStatus(0);
		cpnCouponDO.setUsedTime(timestamp);
		cpnCouponDO.setUserId(11111111L);
		cpnCouponDO.setCode("1111111");
		System.out.println(cpnCouponDAO.update(cpnCouponDO));
	}
	
	@Test
	public void cpnCouponBatchSelectTest(){
		CpnCouponBatchDO cpnCouponBatchDO = cpnCouponBatchDAO.queryCpnCouponBatchByBatchId(1L);
		System.out.println(cpnCouponBatchDO);
	}
	
	@Test
	public void queryCouponDOByOrderIdTest(){
		long userId = 17203781L;
		long orderId = 47081861L;
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(1);
		List<CpnCouponDO> cpnCouponDOList = cpnCouponDAO.queryCouponDOByOrderIdAndStatus(orderId, userId, statusList);
		System.out.println(cpnCouponDOList);
	}
	
	@Test
	public void cpnCouponLogInsert(){
		Long dateLong = (new Date()).getTime();
		Timestamp timestamp = new Timestamp(dateLong);
		CpnCouponLogDO cpnCouponLogDO = new CpnCouponLogDO();
		cpnCouponLogDO.setCode("111111");
		cpnCouponLogDO.setCouponId(11111L);
		cpnCouponLogDO.setCreateTime(timestamp);
		cpnCouponLogDO.setDescription("11111111");
		cpnCouponLogDO.setObjectId(111111L);
		System.out.println(cpnCouponLogDAO.insert(cpnCouponLogDO));
	}
	
	public static void main(String[] args){
		Long dateLong = (new Date()).getTime();
		System.out.println(new Timestamp(dateLong));
	}
}
