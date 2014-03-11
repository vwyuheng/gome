package test;

import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.tuan.core.common.datasource.DataSourceContextHolder;
import com.tuan.core.common.datasource.msloadbalancer.MSDataSourceType;

/**
 * ��������Ԫ���Գ�����
 * @author tianzq
 * @date 2012.03.13
 *
 */
@ContextConfiguration(locations = { 
		"classpath:/bean/inventory-domain-bean-test.xml",
		"classpath:/bean/inventory-redis.xml",
		"classpath:/bean/inventory-dao-bean-test.xml",
		"classpath:/bean/inventory-dao-env-bean-test.xml",
		//"classpath:/bean/redis-domain-test-bean.xml",
//		"classpath:/bean/order-activemq-bean.xml",
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