package test;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.domain.repository.InventoryDeductWriteService;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.support.jedistools.JedisFactory;
import com.tuan.inventory.domain.support.jedistools.JedisFactory.JWork;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;

public class InventoryServiceTest extends InventroyAbstractTest {

	@Autowired
	InventoryDeductWriteService inventoryDeductReadWriteService;
	@Autowired
	InventoryProviderReadService inventoryProviderReadService;
	
	@Resource 
	JedisFactory jedisFactory;
	//JedisSentinelPool jedisSentinelPool;
	//@Resource
	//RedisMap redisMap;
	
	@Test
	public void test() {
		//inventoryDeductReadWriteService.getNotSeleInventory(100);
	}
	@Test
	public void testConn() {
		Jedis jedis = null;
		try {
			jedis = JedisFactory.getRes();
			jedis.set("test77", "100");
			System.out.println(jedis.get("test77"));
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			if(jedis!=null)
				//RedisCacheProvider.close(jedisSentinelPool, jedis);
				JedisFactory.returnRes(jedis);
		}
		
	}
	
	
	@Test
	public void testSelectionRelation() {
		Jedis jedis = JedisFactory.getRes();
		//inventoryDeductReadWriteService.getSelectionRelationLeftNumberBySrId(1);
		System.out.println(SequenceUtil.getSequence(SEQNAME.seq_log, jedis));
		
	}
	@Test
	public void testSelectionRelation2() {
		try {
			inventoryProviderReadService.getSelectionRelationBySrId(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	public void  RedisMapTest() {
		//System.out.println(test1("myhash11"));
		try {
			//inventoryProviderReadService.getSelection(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private int test1(final String key) {
		return jedisFactory.withJedisDo(new JWork<Integer>() 
				{
					@Override
					public Integer work(Jedis j)
					{
						return j.hlen(key).intValue();				
					}			
				});
		
	}
}
