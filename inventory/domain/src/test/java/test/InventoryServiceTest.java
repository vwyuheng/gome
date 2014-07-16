package test;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.tuan.inventory.domain.support.jedistools.RedisCacheUtil;


public class InventoryServiceTest extends InventroyAbstractTest {

	/*@Resource
	SpyMemcachedClient memcachedClient;*/
	@Resource
	RedisCacheUtil redisCacheUtil;
	
	/*@Test
	public void testMem(){
		memcachedClient.set("1test1", "test");
	    System.out.println("ffff="+memcachedClient.get("1test1"));
	}*/
	
	
	 @Test
	    public void sentinel() {
		
		/*  List<Map<String, String>> slaves = redisCacheUtil.sentinel("mymaster");
		  System.out.println(slaves.size());
		  System.out.println( Integer.parseInt(slaves.get(0).get("master-port")));*/
		
	    }
	
	
}
