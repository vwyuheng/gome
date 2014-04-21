package test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;

import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.job.result.RequestPacket;
import com.tuan.inventory.job.util.JobUtils;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.param.CreaterInventoryParam;
import com.tuan.inventory.model.param.UpdateInventoryParam;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.service.GoodsInventoryUpdateService;
import com.wowotrace.trace.model.Message;
import com.wowotrace.trace.util.TraceMessageUtil;
import com.wowotrace.traceEnum.MessageTypeEnum;

public class InventoryServiceTest extends InventroyAbstractTest {

	
	@Resource
	GoodsInventoryQueryService goodsInventoryQueryService;
	@Resource
	GoodsInventoryUpdateService goodsInventoryUpdateService;
	
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
	
	//新增库测试	
	@Test
	public void testCreateInventory() {
		CreaterInventoryParam param = new CreaterInventoryParam();
		//选型
		 List<GoodsSelectionModel> goodsSelection = new ArrayList<GoodsSelectionModel>();
		//分店
		 List<GoodsSuppliersModel> goodsSuppliers = new ArrayList<GoodsSuppliersModel>();
		 
		param.setGoodsId("1");
		param.setUserId("2");
		param.setLeftNumber(100);
		param.setLimitStorage(1);
		param.setTotalNumber(100);
		param.setWaterfloodVal(50);
		for(int i=2;i>0;i--) {
			GoodsSelectionModel smodel = new GoodsSelectionModel();
			smodel.setGoodsId(1L);
			smodel.setUserId(2l);
			smodel.setId(sequenceUtil.getSequence(SEQNAME.seq_selection));
			smodel.setTotalNumber(50);
			smodel.setLeftNumber(50);
			smodel.setLimitStorage(1);
			smodel.setWaterfloodVal(20);
			goodsSelection.add(smodel);
			GoodsSuppliersModel supmodel = new GoodsSuppliersModel();
			supmodel.setId(sequenceUtil.getSequence(SEQNAME.seq_suppliers));
			supmodel.setGoodsId(1l);
			supmodel.setUserId(2l);
			supmodel.setTotalNumber(50);
			supmodel.setLeftNumber(50);
			supmodel.setLimitStorage(1);
			supmodel.setWaterfloodVal(20);
			
			goodsSuppliers.add(supmodel);
			
		}
		param.setGoodsSelection(goodsSelection);
		param.setGoodsSuppliers(goodsSuppliers);
		
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		
		goodsInventoryUpdateService.createInventory(clientIP, clientName, param, traceMessage);
		System.out.println(sequenceUtil.getSequence(SEQNAME.seq_log));
		
	}
	/**
	 * 扣减库存测试
	 */
	@Test
	public void testupdateInventory() {
		UpdateInventoryParam param = new UpdateInventoryParam();
		//选型
		 List<GoodsSelectionModel> goodsSelection = new ArrayList<GoodsSelectionModel>();
		//分店
		 List<GoodsSuppliersModel> goodsSuppliers = new ArrayList<GoodsSuppliersModel>();
		 param.setGoodsId("1");
		 param.setLimitStorage(1);
		 param.setNum(1);
		 for(int i=2;i>0;i--) {
				GoodsSelectionModel smodel = new GoodsSelectionModel();
				//smodel.setGoodsId(1L);
				long id1 = 7+i;
				smodel.setId(id1);
				smodel.setNum(1);
				goodsSelection.add(smodel);
				
				GoodsSuppliersModel supmodel = new GoodsSuppliersModel();
				long id2 = 6+i;
				supmodel.setId(id2);
				//supmodel.setGoodsId(1l);
				supmodel.setNum(1);
				
				goodsSuppliers.add(supmodel);
				
				System.out.println("id1="+id1+",id2="+id2);
				
			}
			param.setGoodsSelection(goodsSelection);
			param.setGoodsSuppliers(goodsSuppliers);
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		goodsInventoryUpdateService.updateInventory(clientIP, clientName, param, traceMessage);
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
	
	
}
