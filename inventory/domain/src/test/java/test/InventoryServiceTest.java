package test;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import com.tuan.inventory.domain.repository.InventoryDeductReadWriteService;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;

public class InventoryServiceTest extends InventroyAbstractTest {

	@Autowired
	InventoryDeductReadWriteService inventoryDeductReadWriteService;
	@Resource 
	JedisSentinelPool jedisSentinelPool;

	@Test
	public void test() {
		//inventoryDeductReadWriteService.getNotSeleInventory(100);
	}
	@Test
	public void testConn() {
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			jedis.set("test77", "100");
			System.out.println(jedis.get("test77"));
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			if(jedis!=null)
				//RedisCacheProvider.close(jedisSentinelPool, jedis);
				jedisSentinelPool.returnResource(jedis);
		}
		
	}
	
	
	@Test
	public void testSelectionRelation() {
		Jedis jedis = jedisSentinelPool.getResource();
		//inventoryDeductReadWriteService.getSelectionRelationLeftNumberBySrId(1);
		System.out.println(SequenceUtil.getSequence(SEQNAME.seq_log, jedis));
		
	}
	@Test
	public void testSelectionRelation2() {
		//goodTypeService.getSelectionRelationBySrId(1);
	}
}
