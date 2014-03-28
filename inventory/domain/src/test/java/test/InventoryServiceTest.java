package test;

import java.net.MalformedURLException;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;

import com.tuan.back.model.SingleOrderModel;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.domain.repository.InventoryDeductWriteService;
import com.tuan.inventory.domain.repository.InventoryProviderReadService;
import com.tuan.inventory.domain.repository.InventoryQueueService;
import com.tuan.inventory.domain.repository.LogOfWaterHandleService;
import com.tuan.inventory.domain.support.config.InventoryConfig;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory;
import com.tuan.inventory.domain.support.jedistools.ReadJedisFactory.JWork;
import com.tuan.inventory.domain.support.jedistools.WriteJedisFactory;
import com.tuan.inventory.domain.support.redis.NullCacheInitService;
import com.tuan.inventory.domain.support.util.HessianProxyUtil;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.ordercenter.backservice.OrderQueryService;
import com.tuan.ordercenter.model.param.OrderQueryIncParam;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.SingleOrderQueryResult;


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
	/*@Resource
	OrderCenterFacade orderCenterFacade;*/
	
	@Test
	public void singleOrderSummaryQuery() throws MalformedURLException, ClassNotFoundException{
		//String url = "http://ordercenter53.55tuan.me:8086/remoting/orderQueryBack";
		//HessianProxyFactory factory = new HessianProxyFactory();
		//OrderQueryService basic = (OrderQueryService) factory.create(url);

		OrderQueryService basic = (OrderQueryService) HessianProxyUtil
				.getObject(OrderQueryService.class,
						InventoryConfig.QUERY_URL);
		
		
		final OrderQueryIncParam incParam = new OrderQueryIncParam();
		
		
		CallResult<SingleOrderQueryResult>  cllResult= basic.singleOrderQuery("61.135.132.59", "USER_CENTER", "38110009159", 19204477L, null,incParam);
		SingleOrderModel model = (SingleOrderModel) cllResult.getBusinessResult().getResultObject();
		//model.getOrderInfoModel().getPayStatus();
		System.out.print("singleOrderQueryResult="+model.getOrderInfoModel().getPayStatus());
	     //System.out.print("成功"+cllResult.getBusinessResult().getResult());
	}

	
	@Test
	public void testSingleOrderQuery(){
		/*OrderQueryIncParam incParam= new OrderQueryIncParam();
		incParam.setIncExtend(true);
		incParam.setIncGoods(true);
		incParam.setIncLogistic(true);
		incParam.setIncSelection(true);
		incParam.setIncTicket(true);
		CallResult<SingleOrderQueryResult>  cllResult=  orderCenterFacade.singleOrderQuery("61.135.132.59", "USER_CENTER", 38110009159L, 19204477, null, null);
		System.out.print("成功1"+cllResult.getBusinessResult().getResult());
		if(cllResult.getCallResult())
		{
			SingleOrderQueryResult singleOrderQueryResult =cllResult.getBusinessResult();
			System.out.print("singleOrderQueryResult="+singleOrderQueryResult);
	       System.out.print("成功"+cllResult.getBusinessResult().getResult());
			
		}*/
	}
	
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
				//logDO.setId(1056l);
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
			
			/*RedisInventoryLogDO logDO = new RedisInventoryLogDO();
			//logDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			logDO.setId(2091l);
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
			logDO.setType("库存扣减");
			inventoryQueueService.pushLogQueues(logDO);*/
			//logOfWaterHandleService.createLogOfWater(logDO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
