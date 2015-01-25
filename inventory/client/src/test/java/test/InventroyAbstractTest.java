package test;

import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.tuan.core.common.datasource.DataSourceContextHolder;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourceType;

/**
 * 订单服务单元测试抽象类
 * @author tianzq
 * @date 2012.03.13
 *
 */
@ContextConfiguration(locations = { 
		//"classpath:/bean/inventory-cache-bean.xml",
		
	    "classpath:/bean/inventory-client-dubbo2.xml",
		//"classpath:/bean/inventory-client-redis.xml",
		//"classpath:/bean/inventory-tasks-bean.xml",
		//"classpath:/log4j.xml"
        })        
public abstract class InventroyAbstractTest extends AbstractJUnit4SpringContextTests {
	protected final String clientIP="localhost";
	protected final String clientName="USER_CENTER";
	@Before
	public void before(){
		DataSourceContextHolder.setDataSourceType(MSDataSourceType.MASTER);	
	}
	protected void  localLog(String msg){
		System.out.println("[LOG]-" +msg);
		logger.warn(msg);
	}
	
}
