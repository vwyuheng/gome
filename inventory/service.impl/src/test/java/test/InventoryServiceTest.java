package test;

import javax.annotation.Resource;

import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.domain.support.jedistools.JedisFactory;
import com.tuan.inventory.domain.support.jedistools.JedisFactory.JWork;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.service.LogOfWaterHandleService;

public class InventoryServiceTest extends InventroyAbstractTest {

	
	@Resource
	LogOfWaterHandleService logOfWaterHandleService;
	
	@Resource 
	JedisFactory readJedisFactory;
	@Resource
	SequenceUtil sequenceUtil;
	
	@Test
	public void test() {
		try {
			//inventoryProviderReadService.getNotSeleInventory(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			//inventoryProviderReadService.getSelectionRelationBySrId(1);
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
			//inventoryDeductReadWriteService.waterfloodValAdjustment(100, 1,11L,"库存管理系统","127.0.0.1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLogInsert() {
		
	/*	GoodsInventoryActionDO logDO = new GoodsInventoryActionDO();
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
		logDO.setType("库存扣减");*/
		try {
			//logOfWaterHandleService.createLogOfWater(logDO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
		
	}
}
