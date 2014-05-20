package test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;

import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.job.result.RequestPacket;
import com.tuan.inventory.job.util.JobUtils;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.param.AdjustInventoryParam;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;
import com.tuan.inventory.model.param.CallbackParam;
import com.tuan.inventory.model.param.CreaterInventoryParam;
import com.tuan.inventory.model.param.InventoryScheduledParam;
import com.tuan.inventory.model.param.OverrideAdjustInventoryParam;
import com.tuan.inventory.model.param.UpdateInventoryParam;
import com.tuan.inventory.model.param.UpdateWmsDataParam;
import com.tuan.inventory.model.param.WmsInventoryParam;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.service.GoodsInventoryQueryService;
import com.tuan.inventory.service.GoodsInventoryScheduledService;
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
	GoodsInventoryScheduledService goodsInventoryScheduledService;
	
	@Resource
	SequenceUtil sequenceUtil;
	
	
	@Test
	public void testUpdateWmsData() {
		UpdateWmsDataParam param = new UpdateWmsDataParam();
		param.setGoodsId("187237");
		param.setSuppliersId("1");
		param.setWmsGoodsId("T01000000116");
		param.setIsBeDelivery(1);
		param.setGoodsSelectionIds("1,2,3");
		param.setGoodsTypeIds("3,4,5");
		
		goodsInventoryUpdateService.updateWmsData(clientIP, clientName, param, null);
	}
	@Test
	public void testOverrideAdjustInventory() {
		OverrideAdjustInventoryParam param = new OverrideAdjustInventoryParam();
		param.setGoodsId("1");
		param.setTotalnum(1000);
		param.setLimitStorage(1);
		param.setType(ResultStatusEnum.GOODS_SELF.getCode());
		/*RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");*/
		goodsInventoryUpdateService.overrideAdjustInventory(clientIP, clientName, param, null);
	}
	//物流库存调整
	@Test
	public void testAdjustWmsInventory() {
		WmsInventoryParam param = new WmsInventoryParam();
		//选型
		 List<GoodsSelectionModel> goodsSelection = new ArrayList<GoodsSelectionModel>();
		 param.setId(2l);
		 param.setWmsGoodsId("2");
		 param.setIsBeDelivery(1);
		 param.setNum(1);
		 for(int i=2;i>0;i--) {
				GoodsSelectionModel smodel = new GoodsSelectionModel();
				//smodel.setGoodsId(1L);
				long goodTypeId = 2+i;
				long id = 15+i;
				smodel.setId(id);
				smodel.setGoodTypeId(goodTypeId);
				smodel.setNum(1);
				goodsSelection.add(smodel);
				
				System.out.println("goodTypeId="+goodTypeId);
				
			}
			param.setGoodsSelection(goodsSelection);
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		goodsInventoryUpdateService.adjustWmsInventory(clientIP, clientName, param, traceMessage);
	}
	
	//新增物流库存测试
	@Test
	public void testCreateWmsInventory() {
		WmsInventoryParam param = new WmsInventoryParam();
		//选型
		 List<GoodsSelectionModel> goodsSelection = new ArrayList<GoodsSelectionModel>();
		//分店
		 String wmsGoodsId = String.valueOf(sequenceUtil.getSequence(SEQNAME.seq_inventory));
		// String goodsId = "2001";
		 System.out.println("wmsGoodsId="+wmsGoodsId);
		 param.setId(sequenceUtil.getSequence(SEQNAME.seq_wms));
		param.setGoodsName("test1");
		param.setGoodsSupplier("55");
		//param.setUserId("2");
		param.setLeftNumber(100);
		//param.setLimitStorage(1);
		param.setTotalNumber(100);
		param.setIsBeDelivery(1);
		param.setWmsGoodsId(wmsGoodsId);
		
		for(int i=2;i>0;i--) {
			GoodsSelectionModel smodel = new GoodsSelectionModel();
			//smodel.setGoodsId(1L);
			//smodel.setUserId(2l);
			smodel.setId((15l+i));
			//smodel.setId(2000l);
			smodel.setTotalNumber(50);
			smodel.setLeftNumber(50);
			smodel.setLimitStorage(1);
			smodel.setGoodTypeId(sequenceUtil.getSequence(SEQNAME.seq_goods_type));
			
			goodsSelection.add(smodel);
			
			
		}
		System.out.println("goodsSelectionparam="+LogUtil.formatListLog(goodsSelection));
		param.setGoodsSelection(goodsSelection);
		
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		System.out.println("11param="+LogUtil.formatObjLog(param));
		goodsInventoryUpdateService.createWmsInventory(clientIP, clientName, param, traceMessage);
		//System.out.println(sequenceUtil.getSequence(SEQNAME.seq_log));
		
	}
	
	
	
	
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
		 String goodsId = String.valueOf(sequenceUtil.getSequence(SEQNAME.seq_inventory));
		// String goodsId = "2001";
		 System.out.println("goodsId="+goodsId);
		param.setGoodsId(goodsId);
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
			//smodel.setId(2000l);
			smodel.setTotalNumber(50);
			smodel.setLeftNumber(50);
			smodel.setLimitStorage(1);
			smodel.setWaterfloodVal(20);
			goodsSelection.add(smodel);
			GoodsSuppliersModel supmodel = new GoodsSuppliersModel();
			supmodel.setId(sequenceUtil.getSequence(SEQNAME.seq_suppliers));
			//supmodel.setId(2000l);
			supmodel.setGoodsId(1l);
			supmodel.setUserId(2l);
			supmodel.setTotalNumber(50);
			supmodel.setLeftNumber(50);
			supmodel.setLimitStorage(1);
			supmodel.setWaterfloodVal(20);
			
			goodsSuppliers.add(supmodel);
			
		}
		System.out.println("goodsSelectionparam="+LogUtil.formatListLog(goodsSelection));
		System.out.println("goodsSuppliersparam="+LogUtil.formatListLog(goodsSuppliers));
		param.setGoodsSelection(goodsSelection);
		param.setGoodsSuppliers(goodsSuppliers);
		
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		System.out.println("11param="+LogUtil.formatObjLog(param));
		goodsInventoryUpdateService.createInventory(clientIP, clientName, param, traceMessage);
		//System.out.println(sequenceUtil.getSequence(SEQNAME.seq_log));
		
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
	public void  testCallbackAckInventory() {
		CallbackParam param = new CallbackParam();
		param.setAck(ResultStatusEnum.ROLLBACK.getCode());
		param.setKey("4060");
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		goodsInventoryUpdateService.callbackAckInventory(clientIP, clientName, param, traceMessage);
	}
	
	
	
	@Test
	public void testAdjustmentInventory() {
		AdjustInventoryParam param = new AdjustInventoryParam();
		param.setId("7");
		param.setNum(-1);
		param.setType(ResultStatusEnum.GOODS_SUPPLIERS.getCode());
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		goodsInventoryUpdateService.adjustmentInventory(clientIP, clientName, param, traceMessage);
	}
	
	@Test
	public void testAdjustmentWaterflood() {
		AdjustWaterfloodParam param = new AdjustWaterfloodParam();
		param.setId("1");
		param.setNum(-1);
		param.setType(ResultStatusEnum.GOODS_SELF.getCode());
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		goodsInventoryUpdateService.adjustmentWaterflood(clientIP, clientName, param, traceMessage);
	}
	
	@Test
	public void testConfirmQueueConsume() {
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		goodsInventoryScheduledService.confirmQueueConsume(clientIP, clientName, traceMessage);
	}
	@Test
	public void testLockedQueueConsume() {
		InventoryScheduledParam param  = new InventoryScheduledParam();
		param.setPeriod(5);
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		goodsInventoryScheduledService.lockedQueueConsume(clientIP, clientName,param, traceMessage);
	}
	
	@Test
	public void testLogsQueueConsume() {
		RequestPacket packet = new RequestPacket();
		packet.setTraceId(UUID.randomUUID().toString());
		packet.setTraceRootId(UUID.randomUUID().toString());
		Message traceMessage = JobUtils.makeTraceMessage(packet);
		TraceMessageUtil.traceMessagePrintS(traceMessage, MessageTypeEnum.CENTS, "Inventory", "test", "test");
		goodsInventoryScheduledService.logsQueueConsume(clientIP, clientName, traceMessage);
	}
}
