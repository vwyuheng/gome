package test;

import java.net.MalformedURLException;

import javax.annotation.Resource;

import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.NotifyServerSendMessage;
import com.tuan.inventory.domain.support.config.InventoryConfig;
import com.tuan.inventory.domain.support.jedistools.JedisFactory;
import com.tuan.inventory.domain.support.jedistools.JedisFactory.JWork;
import com.tuan.inventory.domain.support.jedistools.RedisCacheUtil;
import com.tuan.inventory.domain.support.util.HessianProxyUtil;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.ordercenter.backservice.OrderQueryService;
import com.tuan.ordercenter.model.enu.status.OrderInfoPayStatusEnum;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.OrderQueryResult;


public class InventoryServiceTest extends InventroyAbstractTest {

	
	@Resource 
	RedisCacheUtil redisCacheUtil;
	@Resource 
	JedisFactory jedisFactory;
	@Resource
	SequenceUtil sequenceUtil;
	@Resource
	GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	@Resource
	NotifyServerSendMessage notifyServerSendMessage;
	
	@Test
	public void singleOrderSummaryQuery() throws MalformedURLException, ClassNotFoundException{
		//String url = "http://ordercenter53.55tuan.me:8086/remoting/orderQueryBack";
		//HessianProxyFactory factory = new HessianProxyFactory();
		//OrderQueryService basic = (OrderQueryService) factory.create(url);

		OrderQueryService basic = (OrderQueryService) HessianProxyUtil
				.getObject(OrderQueryService.class,
						InventoryConfig.QUERY_URL);
		
		
		//final OrderQueryIncParam incParam = new OrderQueryIncParam();
		
		
		//CallResult<SingleOrderQueryResult>  cllResult= basic.singleOrderQuery("61.135.132.59", "USER_CENTER", "38110009159", 19204477L, null,incParam);
		CallResult<OrderQueryResult>  cllResult= basic.queryOrderPayStatus( "USER_CENTER","61.135.132.59", "38110009159");
		OrderInfoPayStatusEnum statEnum = (OrderInfoPayStatusEnum) cllResult.getBusinessResult().getResultObject();
		//model.getOrderInfoModel().getPayStatus();
		System.out.print("singleOrderQueryResult="+(statEnum.equals(OrderInfoPayStatusEnum.PAIED)));
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
			System.out.println(redisCacheUtil.get("test77"));
			//System.out.println(test1("test77"));
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
	private String test1(final String key) {
		return jedisFactory.withJedisDo(new JWork<String>() 
				{
					@Override
					public String work(Jedis j)
					{
						return j.get(key);				
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
		
		try {
			for(int i=0;i<1000;i++) {
				/*GoodsInventoryActionDO logDO = new GoodsInventoryActionDO();
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
				inventoryQueueService.pushLogQueues(logDO);*/
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
	
	@Test
	public void testQueueInsert() {
		for(int i=0;i<1000;i++) {/*
			RedisInventoryQueueDO queue = new RedisInventoryQueueDO();
			queue.setId(sequenceUtil.getSequence(SEQNAME.seq_queue_send));
			queue.setGoodsId(Long.valueOf(i));
			queue.setOrderId(3L);
			queue.setType(QueueConstant.GOODS);
			queue.setItem("测试");
			queue.setLimitStorage(1);
			queue.setUserId(5L);
			queue.setVariableQuantityJsonData("numL:10");
			queue.setCreateTime(DateUtils.getBeforXTimestamp10Long(6));
			try {
				inventoryDeductReadWriteService.createInventory(Long.valueOf(i), 1, 1, 5L, "system", "127.0.0.1", 100, 50, 100, null);
				inventoryQueueService.pushQueueSendMsg(queue);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/}
		
	}
	
	@Test
	public void testNotifyServer() {/*
		try {
			RedisInventoryQueueDO queue = new RedisInventoryQueueDO();
			queue.setId(sequenceUtil.getSequence(SEQNAME.seq_queue_send));
			queue.setGoodsId(2L);
			queue.setOrderId(3L);
			queue.setType(QueueConstant.GOODS);
			queue.setItem("测试");
			queue.setLimitStorage(1);
			queue.setUserId(5L);
			queue.setVariableQuantityJsonData("numL:10");
			queue.setCreateTime(DateUtils.getBeforXTimestamp10Long(6));
			notifyServerSendMessage.sendNotifyServerMessage(JSONObject.fromObject(queue));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/}

}
