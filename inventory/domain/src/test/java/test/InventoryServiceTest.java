package test;

import javax.annotation.Resource;

import org.junit.Test;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.domain.repository.SynInitAndAsynUpdateDomainRepository;


public class InventoryServiceTest extends InventroyAbstractTest {

	@Resource
	SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	
	@Test
	public void testSaveGoodsInventory(){
		GoodsInventoryDO goodsDO = new GoodsInventoryDO();
		goodsDO.setGoodsId(1l);
		goodsDO.setLeftNumber(100);
		goodsDO.setLimitStorage(1);
		goodsDO.setTotalNumber(200);
		goodsDO.setWaterfloodVal(10);
		synInitAndAsynUpdateDomainRepository.saveGoodsInventory(goodsDO);
	}
	
	@Test
	public void testUpdateGoodsInventory(){
//		GoodsInventoryDO goodsDO = new GoodsInventoryDO();
//		goodsDO.setGoodsId(1l);
//		goodsDO.setLeftNumber(99);
//		goodsDO.setLimitStorage(1);
//		goodsDO.setTotalNumber(100);
//		goodsDO.setWaterfloodVal(10);
//		synInitAndAsynUpdateDomainRepository.updateGoodsInventory(goodsDO);
	}
	
	
}
