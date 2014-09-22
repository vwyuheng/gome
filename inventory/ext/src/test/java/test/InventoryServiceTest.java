package test;

import javax.annotation.Resource;

import org.junit.Test;

import com.tuan.inventory.ext.InventoryCenterExtFacade;
import com.tuan.ordercenter.model.enu.ClientNameEnum;
import com.tuan.ordercenter.model.enu.status.OrderInfoPayStatusEnum;
import com.tuan.ordercenter.model.result.CallResult;
import com.tuan.ordercenter.model.result.OrderQueryResult;


public class InventoryServiceTest extends InventroyAbstractTest {
	@Resource
	InventoryCenterExtFacade inventoryCenterExtFacade;
	
	 @Test
	 public void testDubboInventory() {
		 try {
			 // 查询商品库存
			 CallResult<OrderQueryResult> cllResult = inventoryCenterExtFacade.queryOrderPayStatus( "INVENTORY_"+ClientNameEnum.INNER_SYSTEM.getValue(),"", String.valueOf(90100015909l));

	
			 OrderInfoPayStatusEnum statEnum = (OrderInfoPayStatusEnum) cllResult.getBusinessResult().getResultObject();
			 // System.out.println("11GoodsInventoryModel11size="+result.getBusinessResult());
			 System.out.println("inventoryCenterExtFacade="
					 + statEnum);
		 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	 }
	
			
}
