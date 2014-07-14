package test;

import javax.annotation.Resource;

import org.junit.Test;

import com.tuan.core.common.lang.cache.remote.SpyMemcachedClient;


public class InventoryServiceTest extends InventroyAbstractTest {

	@Resource
	SpyMemcachedClient memcachedClient;
	
	@Test
	public void testMem(){
		memcachedClient.set("1test1", "test");
	    System.out.println("ffff="+memcachedClient.get("1test1"));
	}
	
	
}
