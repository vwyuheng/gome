package test;

import java.net.MalformedURLException;

import org.junit.Test;

import com.tuan.inventory.domain.support.util.HessianProxyUtil;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.service.GoodsInventoryQueryService;

public class CopyOfInventoryServiceTest  {

	
	
	
  static	final String hessinaUrl = "http://inventory.55tuan.me:80/remoting/goodsInventoryQueryService";
	public static void main(String[] args) {
		testHessian();
	}
	
	public static void testHessian() {
		
		try {
			GoodsInventoryQueryService basic = (GoodsInventoryQueryService) HessianProxyUtil
					.getObject(GoodsInventoryQueryService.class,
							hessinaUrl);
			CallResult<GoodsInventoryModel> result =  basic.findGoodsInventoryByGoodsId("127.0.0.1", "inventory", 861013);
			if (result != null&& result.isSuccess()) {
				
				GoodsInventoryModel inventoryInfoDO = 	result.getBusinessResult();
				
				System.out.println("inventoryInfoDO="+inventoryInfoDO);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*CallResult<GoodsInventoryModel> result = 	inventoryCenterFacade.queryGoodsInventory(clientIP, clientName, 861013);
		if (result == null || !result.isSuccess()) {
			
			
		}else {
			GoodsInventoryModel inventoryInfoDO = 	result.getBusinessResult();
			
			System.out.println("inventoryInfoDO="+inventoryInfoDO);
		     
		}*/
	}
	
}
