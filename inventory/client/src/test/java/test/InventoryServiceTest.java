package test;

import javax.annotation.Resource;

import org.junit.Test;

import redis.clients.jedis.exceptions.JedisConnectionException;

import com.tuan.inventory.client.GoodsInventoryQueryClientService;
import com.tuan.inventory.client.support.jedistools.RedisClientCacheUtil;
import com.tuan.inventory.model.GoodsBaseModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.result.CallResult;


public class InventoryServiceTest extends InventroyAbstractTest {

	/*@Resource
	SpyMemcachedClient memcachedClient;*/
	@Resource
	RedisClientCacheUtil redisCacheUtil;
	@Resource
	GoodsInventoryQueryClientService goodsInventoryQueryClientService;
	
	 @Test
	    public void sentinel() throws InterruptedException {

			for (int i = 0; i < 100; i++) {
				try {
					
					System.out.println(redisCacheUtil.get("KEY: " + i)+ "," + i);
					System.out.print(".");
					Thread.sleep(500);
					
				} catch (JedisConnectionException e) {
					System.out.print("x");
					i--;
					Thread.sleep(1000);
				}
			}
		
	    }
	
	@Test
	public void testInventory() {
		try {
			// 查询商品库存
			CallResult<GoodsInventoryModel> result = goodsInventoryQueryClientService
					.findGoodsInventoryByGoodsId(clientIP, clientName, 239563);

			// System.out.println("11GoodsInventoryModel11size="+result.getBusinessResult());
			System.out.println("11GoodsInventoryModel11="
					+ result.getBusinessResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testBaseInventory() {
		try {
			// 查询商品库存
			CallResult<GoodsBaseModel> result = goodsInventoryQueryClientService
					.findSalesCountByGoodsBaseId(clientIP, clientName, 8000000555060l);
			
			// System.out.println("11GoodsInventoryModel11size="+result.getBusinessResult());
			System.out.println("11GoodsBaseModell11="
					+ result.getBusinessResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			
}
