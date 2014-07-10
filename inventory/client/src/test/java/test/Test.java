package test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.tuan.inventory.client.InventoryCenterFacade;

public class Test {

	public static void main(String[] args) {
		ApplicationContext act = new FileSystemXmlApplicationContext(
				"classpath:/bean/inventory-client-bean.xml");
		InventoryCenterFacade facade = (InventoryCenterFacade) act
				.getBean("inventoryCenterFacade");
		
		
		//根据goodsId查询库存商品信息
		System.out.println(facade.queryGoodsInventory("", "", 111));

	}

}
