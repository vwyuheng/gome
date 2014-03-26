package test;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.repository.InventoryDeductWriteService;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.repository.LogOfWaterHandleService;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory.JWork;
import com.tuan.inventory.domain.support.jedistools.WriteJedisFactory;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.QueueConstant;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;

public class InventoryServiceTest extends InventroyAbstractTest {

	@Autowired
	InventoryDeductWriteService inventoryDeductReadWriteService;
	@Autowired
	InventoryProviderReadService inventoryProviderReadService;
	@Resource
	LogOfWaterHandleService logOfWaterHandleService;
	
	@Resource 
	ReadJedisFactory readJedisFactory;
	@Resource
	SequenceUtil sequenceUtil;
	//JedisSentinelPool jedisSentinelPool;
	//EventManager eventManager;
	@Resource
	NullCacheInitService nullCacheInitService;
	@Resource
	InventoryQueueService inventoryQueueService;
	
	@Test
	public void test() {
		try {
			inventoryProviderReadService.getNotSeleInventory(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testConn() {
		Jedis jedis = null;
		try {
			jedis = WriteJedisFactory.getRes();
			//测试用
			//jedis.del(QueueConstant.QUEUE_LOGS_MESSAGE);
			//System.out.println(jedis.);
			//jedis.brpop(timeout, keys);
			//jedis.set("test77", "100");
			//System.out.println(jedis.get("test77"));
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			if(jedis!=null)
				//RedisCacheProvider.close(jedisSentinelPool, jedis);
				WriteJedisFactory.returnRes(jedis);
		}
		
	}
	
	
	@Test
	public void testSelectionRelation() {
		//Jedis jedis = WriteJedisFactory.getRes();
		//inventoryDeductReadWriteService.getSelectionRelationLeftNumberBySrId(1);
		System.out.println(sequenceUtil.getSequence(SEQNAME.seq_log));
		
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
		//eventManager.addEvent(null);
		//System.out.println(test1("myhash11"));
		try {
			 Thread.sleep(6000);  
			//inventoryProviderReadService.getSelection(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private int test1(final String key) {
		return readJedisFactory.withJedisDo(new JWork<Integer>() 
				{
					@Override
					public Integer work(Jedis j)
					{
						return j.hlen(key).intValue();				
					}
		
				});
		
	}
	
	@Test
	public void testWrite() {
		try {
			inventoryDeductReadWriteService.waterfloodValAdjustment("100", 1,11L,"库存管理系统","127.0.0.1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLogInsert() {
		
		try {
			for(int i=0;i<1000;i++) {
				RedisInventoryLogDO logDO = new RedisInventoryLogDO();
				logDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
				logDO.setGoodsId(2L);
				logDO.setOrderId(4L);
				logDO.setUserId(3L);
				logDO.setClientIp("127.0.0.1");
				logDO.setSystem("inventory system");
				logDO.setContent("content:11");
				logDO.setCreateTime(1000111);
				logDO.setItem("dfasds");
				logDO.setOperateType("商品");
				logDO.setRemark("备注");
				logDO.setVariableQuantity("numL:10");
				logDO.setType("库存扣减"+i);
				inventoryQueueService.pushLogQueues(logDO);
			}
			
			//logOfWaterHandleService.createLogOfWater(logDO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
