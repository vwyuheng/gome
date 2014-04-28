package com.tuan.inventory.domain.support.job.handle;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.result.CallResult;

/**
 * 日志异步处理事件
 * @author henry.yu
 * @Date  2014/3/21
 */
public class InventoryInitAndUpdateHandle  {
	
	private final static LocalLogger log = LocalLogger.getLog("LogsEventHandle.LOG");
	
	@Resource
	SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	
	
	public boolean handleGoodsInventory(final GoodsInventoryDO goodsDO) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(goodsDO == null){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleGoodsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsDO",goodsDO)
				.addMetaData("startTime", startTime).toJson());
	
		CallResult<GoodsInventoryDO> callResult  = null;
		try {
			
			if (goodsDO != null) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.saveGoodsInventory(goodsDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS
						&& publicCodeEnum != PublicCodeEnum.DATA_EXISTED) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsDO.getGoodsId();
				} else {
					message = "GoodsInventory_success[save success]goodsId:" + goodsDO.getGoodsId();
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsDO",goodsDO)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			//throw new CacheRunTimeException("InventoryInitAndUpdateHandle.handleGoodsInventory run exception!",e);
		}
		log.info(lm.addMetaData("goodsDO",goodsDO)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean updateGoodsInventory(final GoodsInventoryDO goodsDO) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(goodsDO == null){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleGoodsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsDO",goodsDO)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<GoodsInventoryDO> callResult  = null;
		try {
			
			if (goodsDO != null) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsDO.getGoodsId();
				} else {
					message = "GoodsInventory_success[save success]goodsId:" + goodsDO.getGoodsId();
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsDO",goodsDO)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			//throw new CacheRunTimeException("InventoryInitAndUpdateHandle.handleGoodsInventory run exception!",e);
		}
		log.info(lm.addMetaData("goodsDO",goodsDO)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean updateGoodsSelection(final GoodsSelectionDO selectionDO) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(selectionDO == null){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleGoodsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("selectionDO",selectionDO)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<GoodsSelectionDO> callResult  = null;
		try {
			
			if (selectionDO != null) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.updateGoodsSelection(selectionDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]selectionId:" + selectionDO.getId();
				} else {
					message = "GoodsInventory_success[save success]selectionId:" + selectionDO.getId();
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("selectionDO",selectionDO)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			//throw new CacheRunTimeException("InventoryInitAndUpdateHandle.handleGoodsInventory run exception!",e);
		}
		log.info(lm.addMetaData("selectionDO",selectionDO)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	
	public boolean updateGoodsSuppliers(final GoodsSuppliersDO suppliersDO) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(suppliersDO == null){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleGoodsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("suppliersDO",suppliersDO)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<GoodsSuppliersDO> callResult  = null;
		try {
			
			if (suppliersDO != null) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.updateGoodsSuppliers(suppliersDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]suppliersId:" + suppliersDO.getId();
				} else {
					message = "GoodsInventory_success[save success]suppliersId:" + suppliersDO.getId();
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("suppliersDO",suppliersDO)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			//throw new CacheRunTimeException("InventoryInitAndUpdateHandle.handleGoodsInventory run exception!",e);
		}
		log.info(lm.addMetaData("suppliersDO",suppliersDO)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	

	public boolean handleBatchGoodsSelection(final long goodsId, final List<GoodsSelectionDO> selectionInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(CollectionUtils.isEmpty(selectionInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleBatchGoodsSelection");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("startTime", startTime).toJson());
	
		CallResult<List<GoodsSelectionDO>> callResult  = null;
		try {
			
			if (!CollectionUtils.isEmpty(selectionInventoryList)) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.saveBatchGoodsSelection(goodsId,selectionInventoryList);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS
						&& publicCodeEnum != PublicCodeEnum.DATA_EXISTED) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "GoodsInventory_success[save success]goodsId:" + goodsId;
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsId",goodsId)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			//throw new CacheRunTimeException("InventoryInitAndUpdateHandle.handleGoodsInventory run exception!",e);
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean updateBatchGoodsSelection(final long goodsId, final List<GoodsSelectionDO> selectionInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(CollectionUtils.isEmpty(selectionInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleBatchGoodsSelection");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<List<GoodsSelectionDO>> callResult  = null;
		try {
			
			if (!CollectionUtils.isEmpty(selectionInventoryList)) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.updateBatchGoodsSelection(goodsId,selectionInventoryList);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "GoodsInventory_success[save success]goodsId:" + goodsId;
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsId",goodsId)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			//throw new CacheRunTimeException("InventoryInitAndUpdateHandle.handleGoodsInventory run exception!",e);
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}

	public boolean handleBatchGoodsSuppliers(final long goodsId, final List<GoodsSuppliersDO> suppliersInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(CollectionUtils.isEmpty(suppliersInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleBatchGoodsSuppliers");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("selectionInventoryList",suppliersInventoryList)
				.addMetaData("startTime", startTime).toJson());
	
		CallResult<List<GoodsSuppliersDO>> callResult  = null;
		try {
			
			if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.saveBatchGoodsSuppliers(goodsId,suppliersInventoryList);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS
						&& publicCodeEnum != PublicCodeEnum.DATA_EXISTED) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "GoodsInventory_success[save success]goodsId:" + goodsId;
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsId",goodsId)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			//throw new CacheRunTimeException("InventoryInitAndUpdateHandle.handleBatchGoodsSuppliers run exception!",e);
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("suppliersInventoryList",suppliersInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean updateBatchGoodsSuppliers(final long goodsId, final List<GoodsSuppliersDO> suppliersInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(CollectionUtils.isEmpty(suppliersInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleBatchGoodsSuppliers");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("selectionInventoryList",suppliersInventoryList)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<List<GoodsSuppliersDO>> callResult  = null;
		try {
			
			if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.updateBatchGoodsSuppliers(goodsId,suppliersInventoryList);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "GoodsInventory_success[save success]goodsId:" + goodsId;
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsId",goodsId)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			//throw new CacheRunTimeException("InventoryInitAndUpdateHandle.handleBatchGoodsSuppliers run exception!",e);
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("suppliersInventoryList",suppliersInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
}