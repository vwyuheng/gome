package com.tuan.inventory.domain.support.job.handle;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LocalLogger;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.result.CallResult;

/**
 * 库存初始化和更新处理工具类
 * @author henry.yu
 * @Date  2014/3/21
 */
public class InventoryInitAndUpdateHandle  {
	
	private final static LocalLogger log = LocalLogger.getLog("COMMON.BUSINESS");
	
	@Resource
	SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	
	
	public boolean saveGoodsWmsInventory(final GoodsInventoryWMSDO wmsDO,final List<GoodsSelectionDO> selectionList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(wmsDO ==null&&CollectionUtils.isEmpty(selectionList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.saveGoodsWmsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("wmsDO",wmsDO)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<Boolean> callResult  = null;
		try {
			// 消费对列的信息
			callResult = synInitAndAysnMysqlService.saveGoodsWmsInventory(wmsDO,selectionList);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS
					/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
				// 消息数据不存并且不成功
				isSuccess = false;
				message = "saveGoodsInventory2Mysql_error[" + publicCodeEnum.getMessage()
						+ "]wmsGoodsId:" + wmsDO==null?"":wmsDO.getWmsGoodsId();
			} else {   //TODO 该处理也有问题
				message = "saveGoodsInventory2Mysql_success[save2mysql success]wmsGoodsId:" + wmsDO.getWmsGoodsId();
				if (wmsDO != null) {
					this.goodsInventoryDomainRepository.saveGoodsWmsInventory(wmsDO);
				}
				// 保选型库存
				this.goodsInventoryDomainRepository.saveGoodsSelectionWmsInventory(selectionList);	
				
			}
			
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("wmsDO",wmsDO)
					.addMetaData("selectionList",selectionList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("wmsDO",wmsDO)
				.addMetaData("selectionList",selectionList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	
	
	public boolean saveGoodsWmsInventory(final GoodsInventoryWMSDO wmsDO,final List<GoodsInventoryDO> wmsInventoryList,final List<GoodsSelectionDO> selectionList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(wmsDO ==null&&CollectionUtils.isEmpty(selectionList)&&CollectionUtils.isEmpty(wmsInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.saveGoodsWmsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("wmsDO",wmsDO)
				.addMetaData("wmsInventoryList",wmsInventoryList)
				.addMetaData("selectionList",selectionList)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<Boolean> callResult  = null;
		try {
			// 消费对列的信息
			callResult = synInitAndAysnMysqlService.saveGoodsWmsInventory(wmsDO,wmsInventoryList,selectionList);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS
					/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
				// 消息数据不存并且不成功
				isSuccess = false;
				message = "saveGoodsInventory2Mysql_error[" + publicCodeEnum.getMessage()
						+ "]wmsGoodsId:" + wmsDO==null?"":wmsDO.getWmsGoodsId();
			} else {   //TODO 该处理也有问题
				message = "saveGoodsInventory2Mysql_success[save2mysql success]wmsGoodsId:" + wmsDO.getWmsGoodsId();
				if (wmsDO != null) {
					this.goodsInventoryDomainRepository.saveGoodsWmsInventory(wmsDO);
				}
				// 保选型库存saveBatchGoodsInventory
				this.goodsInventoryDomainRepository.saveGoodsSelectionWmsInventory(selectionList);	
				this.goodsInventoryDomainRepository.saveBatchGoodsInventory(wmsInventoryList);	
				
			}
			
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("wmsDO",wmsDO)
					.addMetaData("wmsInventoryList",wmsInventoryList)
					.addMetaData("selectionList",selectionList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("wmsDO",wmsDO)
				.addMetaData("wmsInventoryList",wmsInventoryList)
				.addMetaData("selectionList",selectionList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	/**
	 * 不维护 物流的商品保存
	 * @param goodsId
	 * @param goodsDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 * @return
	 */
	public boolean saveGoodsInventory(final long goodsId,final GoodsInventoryDO goodsDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(goodsId <= 0){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleGoodsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsDO",goodsDO)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<Boolean> callResult  = null;
		try {
			// 消费对列的信息
			callResult = synInitAndAysnMysqlService.saveGoodsInventory(goodsId,goodsDO,selectionInventoryList,suppliersInventoryList);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS
					/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
				// 消息数据不存并且不成功
				isSuccess = false;
				message = "saveGoodsInventory2Mysql_error[" + publicCodeEnum.getMessage()
						+ "]goodsId:" + goodsId;
			} else {
				message = "saveGoodsInventory2Mysql_success[save2mysql success]goodsId:" + goodsId;
				if (goodsDO != null) {
					this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
							goodsDO);
				}
				// 保选型库存
				if (!CollectionUtils.isEmpty(selectionInventoryList)) {
					this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(
							goodsId, selectionInventoryList);
					
					
				}
				// 保存分店库存
				if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
					this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
							goodsId, suppliersInventoryList);
					
				}
				
			}
			
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsId",goodsId)
					.addMetaData("goodsDO",goodsDO)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("goodsDO",goodsDO)
				.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("suppliersInventoryList",suppliersInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean saveGoodsInventory(final long goodsId,final GoodsInventoryDO goodsDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList,GoodsInventoryWMSDO wmsInventory,GoodsInventoryWMSDO wmsInventory4wmsGoodsId) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(goodsId <= 0){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.handleGoodsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsDO",goodsDO)
				.addMetaData("startTime", startTime).toJson());
	
		CallResult<Boolean> callResult  = null;
		try {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.saveGoodsInventory(goodsId,goodsDO,selectionInventoryList,suppliersInventoryList,wmsInventory,wmsInventory4wmsGoodsId);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS
						/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "saveGoodsInventory2Mysql_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "saveGoodsInventory2Mysql_success[save2mysql success]goodsId:" + goodsId;
					if (goodsDO != null) {
						this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
								goodsDO);
					}
					// 保选型库存
					if (!CollectionUtils.isEmpty(selectionInventoryList)) {
							this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(
									goodsId, selectionInventoryList);
						
					
					}
					// 保存分店库存
					if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
							this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
									goodsId, suppliersInventoryList);
						
					}
					// 保存物流商品库存
					if (wmsInventory!=null) {
							this.goodsInventoryDomainRepository.saveGoodsWmsInventory(wmsInventory);
					}
					// 保存物流商品库存
					if (wmsInventory4wmsGoodsId!=null) {
							this.goodsInventoryDomainRepository.saveGoodsWmsInventory(wmsInventory4wmsGoodsId);
					}
				}
			
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsId",goodsId)
					.addMetaData("goodsDO",goodsDO)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("goodsDO",goodsDO)
				.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("suppliersInventoryList",suppliersInventoryList)
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
			
			//if (goodsDO != null) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "updateGoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsDO.getGoodsId();
				} else {
					message = "updateGoodsInventory_success[save success]goodsId:" + goodsDO.getGoodsId();
				}
			//} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsDO",goodsDO)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("goodsDO",goodsDO)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean updateGoodsInventory(final long goodsId,final GoodsInventoryDO goodsDO,final List<GoodsSelectionDO> selectionInventoryList,final List<GoodsSuppliersDO> suppliersInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(goodsDO == null){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.updateGoodsInventory");
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
					message = "updateGoodsInventory2Mysql_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "updateGoodsInventory2mysql_success[save2mysql success]goodsId:" + goodsId;
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsId",goodsId)
					.addMetaData("goodsDO",goodsDO)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("goodsDO",goodsDO)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean deleteGoodsInventory(final long goodsId) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(goodsId<=0){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.deleteGoodsInventory");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<Integer> callResult  = null;
		try {
			
			//if (goodsId != null) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.deleteGoodsInventory(goodsId);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "updateGoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "updateGoodsInventory_success[save success]goodsId:" + goodsId;
				}
			//} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("goodsId",goodsId)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean updateGoodsSelection(final GoodsInventoryDO goodsDO,final GoodsSelectionDO selectionDO) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(selectionDO == null){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.updateGoodsSelection");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("selectionDO",selectionDO)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<GoodsSelectionDO> callResult  = null;
		try {
			
			//if (selectionDO != null) {
				
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.updateGoodsSelection(goodsDO,selectionDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "updateGoodsSelection_error[" + publicCodeEnum.getMessage()
							+ "]selectionId:" + selectionDO.getId();
				} else {
					message = "updateGoodsSelection_success[save success]selectionId:" + selectionDO.getId();
				}
			//} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("selectionDO",selectionDO)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("selectionDO",selectionDO)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	
	public boolean updateGoodsSuppliers(final GoodsInventoryDO goodsDO,final GoodsSuppliersDO suppliersDO) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(suppliersDO == null){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.updateGoodsSuppliers");
		long startTime = System.currentTimeMillis();
		log.info(lm.setMethod("InventoryInitAndUpdateHandle.updateGoodsSuppliers")
				.addMetaData("goodsDO",goodsDO)
				.addMetaData("suppliersDO",suppliersDO)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<GoodsSuppliersDO> callResult  = null;
		try {
			
			//if (suppliersDO != null) {
				
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.updateGoodsSuppliers(goodsDO,suppliersDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "updateGoodsSuppliers_error[" + publicCodeEnum.getMessage()
							+ "]suppliersId:" + suppliersDO.getId();
				} else {
					message = "updateGoodsSuppliers_success[save success]suppliersId:" + suppliersDO.getId();
				}
			//} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("suppliersDO",suppliersDO)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("suppliersDO",suppliersDO)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	

	public boolean saveBatchGoodsSelection(final long goodsId, final List<GoodsSelectionDO> selectionInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(CollectionUtils.isEmpty(selectionInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.saveBatchGoodsSelection");
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
						/*&& publicCodeEnum != PublicCodeEnum.DATA_EXISTED*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "saveBatchGoodsSelection_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "saveBatchGoodsSelection_success[save success]goodsId:" + goodsId;
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
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.updateBatchGoodsSelection");
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
					message = "updateBatchGoodsSelection_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "updateBatchGoodsSelection_success[save success]goodsId:" + goodsId;
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
			
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	
	
	/*public boolean updateBatchGoodsWms(final GoodsInventoryWMSDO wmsDO,final List<GoodsSelectionDO> selectionList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(wmsDO ==null&&CollectionUtils.isEmpty(selectionList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.updateBatchGoodsSelectionWms");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("wmsDO",wmsDO)
				.addMetaData("selectionList",selectionList)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<Boolean> callResult  = null;
		try {
			// 消费对列的信息
			callResult = synInitAndAysnMysqlService.updateBatchGoodsWms(wmsDO, selectionList);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS
					&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
				// 消息数据不存并且不成功
				isSuccess = false;
				message = "updateBatchGoodsWms2Mysql_error[" + publicCodeEnum.getMessage()
						+ "]wmsGoodsId:" + wmsDO==null?"":wmsDO.getWmsGoodsId();
			} 
			
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("wmsDO",wmsDO)
					.addMetaData("selectionList",selectionList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("wmsDO",wmsDO)
				.addMetaData("selectionList",selectionList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}*/
	
	public boolean batchAdjustGoodsWms(final GoodsInventoryWMSDO wmsDO,final List<GoodsInventoryDO> wmsInventoryList,final List<GoodsWmsSelectionResult> selectionList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(wmsDO ==null&&CollectionUtils.isEmpty(selectionList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.batchAdjustGoodsWms");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("wmsDO",wmsDO)
				.addMetaData("selectionList",selectionList)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<Boolean> callResult  = null;
		try {
			// 消费对列的信息
			callResult = synInitAndAysnMysqlService.batchUpdateGoodsWms(wmsDO,wmsInventoryList, selectionList);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS
					/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
				// 消息数据不存并且不成功
				isSuccess = false;
				message = "updateBatchGoodsWms2Mysql_error[" + publicCodeEnum.getMessage()
						+ "]wmsGoodsId:" + wmsDO==null?"":wmsDO.getWmsGoodsId();
			} 
			
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm.addMetaData("wmsDO",wmsDO)
					.addMetaData("selectionList",selectionList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm.addMetaData("wmsDO",wmsDO)
				.addMetaData("selectionList",selectionList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	
	
	public boolean deleteBatchGoodsSelection( final List<GoodsSelectionDO> selectionInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(CollectionUtils.isEmpty(selectionInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.deleteBatchGoodsSelection");
		long startTime = System.currentTimeMillis();
		log.info(lm.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<List<GoodsSelectionDO>> callResult  = null;
		try {
			
			if (!CollectionUtils.isEmpty(selectionInventoryList)) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.deleteBatchGoodsSelection(selectionInventoryList);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "updateBatchGoodsSelection_error[" + publicCodeEnum.getMessage()
							+ "]";
				} else {
					message = "updateBatchGoodsSelection_success[save success]";
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm
				.addMetaData("selectionInventoryList",selectionInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}

	public boolean saveBatchGoodsSuppliers(final long goodsId, final List<GoodsSuppliersDO> suppliersInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(CollectionUtils.isEmpty(suppliersInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.saveBatchGoodsSuppliers");
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
						/*&& publicCodeEnum != PublicCodeEnum.DATA_EXISTED*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "saveBatchGoodsSuppliers_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "saveBatchGoodsSuppliers_success[save success]goodsId:" + goodsId;
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
			
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("suppliersInventoryList",suppliersInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	//成功返回true，失败返回false
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
					message = "GoodsSuppliers_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				} else {
					message = "GoodsSuppliers_success[save success]goodsId:" + goodsId;
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
			
		}
		log.info(lm.addMetaData("goodsId",goodsId)
				.addMetaData("suppliersInventoryList",suppliersInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
	public boolean deleteBatchGoodsSuppliers( final List<GoodsSuppliersDO> suppliersInventoryList) {
		boolean isSuccess = true;
		String message = StringUtils.EMPTY;
		if(CollectionUtils.isEmpty(suppliersInventoryList)){
			isSuccess = false;
		}
		LogModel lm = LogModel.newLogModel("InventoryInitAndUpdateHandle.deleteBatchGoodsSuppliers");
		long startTime = System.currentTimeMillis();
		log.info(lm
				.addMetaData("selectionInventoryList",suppliersInventoryList)
				.addMetaData("startTime", startTime).toJson());
		
		CallResult<List<GoodsSuppliersDO>> callResult  = null;
		try {
			
			if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
				// 消费对列的信息
				callResult = synInitAndAysnMysqlService.deleteBatchGoodsSuppliers(suppliersInventoryList);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					isSuccess = false;
					message = "GoodsSuppliers_error[" + publicCodeEnum.getMessage()
							+ "]";
				} else {
					message = "GoodsSuppliers_success[save success]";
				}
			} 
			
		} catch (Exception e) {
			isSuccess = false;
			log.error(lm
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("callResult",callResult)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson(),e);
			
		}
		log.info(lm
				.addMetaData("suppliersInventoryList",suppliersInventoryList)
				.addMetaData("callResult",callResult)
				.addMetaData("message",message)
				.addMetaData("endTime", System.currentTimeMillis())
				.addMetaData("useTime", LogUtil.getRunTime(startTime)).toJson());
		return isSuccess;
	}
}
