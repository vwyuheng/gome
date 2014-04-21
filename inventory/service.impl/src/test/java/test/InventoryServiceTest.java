package test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.domain.support.jedistools.JedisFactory;
import com.tuan.inventory.domain.support.jedistools.JedisFactory.JWork;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.service.GoodsInventoryQueryService;

public class InventoryServiceTest extends InventroyAbstractTest {

	
	@Resource
	GoodsInventoryQueryService goodsInventoryQueryService;
	
	@Resource 
	JedisFactory jedisFactory;
	@Resource
	SequenceUtil sequenceUtil;
	
	@Test
	public void testInventory() {
		try {
			//查询商品库存
			CallResult<GoodsInventoryModel> result =	goodsInventoryQueryService.findGoodsInventoryByGoodsId(clientIP, clientName, 1736);
		
			//System.out.println("11GoodsInventoryModel11size="+result.getBusinessResult());
			System.out.println("11GoodsInventoryModel11="+result.getBusinessResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testSuppliers() {
		try {
	
			//查询分店库存
			CallResult<GoodsSuppliersModel> result =	goodsInventoryQueryService.findGoodsSuppliersBySuppliersId(clientIP, clientName, 1736,1685);
			
			System.out.println("11GoodsInventoryModel11="+result.getBusinessResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testSelection() {
		try {
			
			//查询选型库存
			CallResult<GoodsSelectionModel> result =	goodsInventoryQueryService.findGoodsSelectionBySelectionId(clientIP, clientName, 2499,28);
			
			System.out.println("11GoodsInventoryModel11="+result.getBusinessResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testSelectionList() {
		try {
			
			CallResult<List<GoodsSelectionModel>> result =	goodsInventoryQueryService.findGoodsSelectionListByGoodsId(clientIP, clientName, 2499);
			System.out.println("11GoodsInventoryModel11size="+result.getBusinessResult().size());
			System.out.println("11GoodsInventoryModel11="+result.getBusinessResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testSuppliersList() {
		try {
			CallResult<List<GoodsSuppliersModel>> result =	goodsInventoryQueryService.findGoodsSuppliersListByGoodsId(clientIP, clientName, 1736);
			System.out.println("11GoodsInventoryModel11size="+result.getBusinessResult().size());
			System.out.println("11GoodsInventoryModel11="+result.getBusinessResult());
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
		return jedisFactory.withJedisDo(new JWork<Integer>() 
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
