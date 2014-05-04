package com.tuan.inventory.domain;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.result.CallResult;
/**
 * 初始化库存domain
 * @author henry.yu
 * @date 2014/4/23
 */
public class InventoryInitDomain extends AbstractDomain{
	private LogModel lm;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private GoodsInventoryDO inventoryInfoDO;
	
	// 初始化用
	private List<GoodsSuppliersDO> suppliersInventoryList;
	private List<GoodsSelectionDO> selectionInventoryList;
	private long goodsId;
	
	// 是否需要初始化
	private boolean isInit;
	
	public InventoryInitDomain(long goodsId, LogModel lm) {
		this.goodsId = goodsId;
		this.lm = lm;
	}
	
	// 业务检查
	public CreateInventoryResultEnum businessExecute() {
		CreateInventoryResultEnum resultEnum = null;
		// 初始化检查
		resultEnum = this.initCheck();
		if (isInit) {
			resultEnum = this.init();
		}
		return resultEnum;
	}
	// 初始化检查
	public CreateInventoryResultEnum initCheck() {
		try {
		if (goodsId > 0) { // limitStorage>0:库存无限制；1：限制库存
			//boolean isExists = this.goodsInventoryDomainRepository
				//	.isGoodsExists(goodsId);
			
			this.inventoryInfoDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
			//if (isExists) { // 不存在
			if(inventoryInfoDO==null) {
				// 初始化库存
				this.isInit = true;
				// 初始化商品库存信息
				CallResult<GoodsInventoryDO> callGoodsInventoryDOResult = this.synInitAndAysnMysqlService
						.selectGoodsInventoryByGoodsId(goodsId);
				if (callGoodsInventoryDOResult == null || !callGoodsInventoryDOResult.isSuccess()) {
					this.isInit = false;
					return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
				}else {
				     this.inventoryInfoDO = 	callGoodsInventoryDOResult.getBusinessResult();
				}
				
				//this.inventoryInfoDO = this.synInitAndAysnMysqlService
						//.selectGoodsInventoryByGoodsId(goodsId);
				// 查询该商品选型库存信息
				CallResult<List<GoodsSelectionDO>> callGoodsSelectionListDOResult = this.synInitAndAysnMysqlService
						.selectGoodsSelectionListByGoodsId(goodsId);
				if (callGoodsSelectionListDOResult == null || !callGoodsSelectionListDOResult.isSuccess()) {
					this.isInit = false;
					return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
				}else {
				     this.selectionInventoryList = 	callGoodsSelectionListDOResult.getBusinessResult();
				}
				//selectionInventoryList = this.synInitAndAysnMysqlService
				//	.selectGoodsSelectionListByGoodsId(goodsId);
				// 查询该商品分店库存信息
				CallResult<List<GoodsSuppliersDO>> callGoodsSuppliersListDOResult = this.synInitAndAysnMysqlService
						.selectGoodsSuppliersListByGoodsId(goodsId);
				if (callGoodsSuppliersListDOResult == null || !callGoodsSuppliersListDOResult.isSuccess()) {
					this.isInit = false;
					return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
				}else {
				     this.suppliersInventoryList = 	callGoodsSuppliersListDOResult.getBusinessResult();
				}
				//suppliersInventoryList = this.synInitAndAysnMysqlService
					//	.selectGoodsSuppliersListByGoodsId(goodsId);
			}
		}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("initCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;

	}
	
	/**
	 * 库存初始化
	 */
	public CreateInventoryResultEnum init() {
		boolean result = false;
		try {
		// 保存商品库存
		if (inventoryInfoDO != null) {
			
			//mysql数据库处理成功后才处理redis
			result = this.inventoryInitAndUpdateHandle.saveGoodsInventory(inventoryInfoDO);
			if(result){
				this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
						inventoryInfoDO);
			}
			
			//保存库存信息到mysql
			//this.synInitAndAsynUpdateDomainRepository.saveGoodsInventory(inventoryInfoDO);
		}
			
		// 保选型库存
		if (!CollectionUtils.isEmpty(selectionInventoryList)) {
			result = this.inventoryInitAndUpdateHandle.saveBatchGoodsSelection(goodsId, selectionInventoryList);
			if(result){
				this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(
						goodsId, selectionInventoryList);
			}
			
			//批量保存商品选型库存到mysql
			//this.synInitAndAsynUpdateDomainRepository.saveBatchGoodsSelection(goodsId, selectionInventoryList);
		}
			
		// 保存分店库存
		if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
			result = this.inventoryInitAndUpdateHandle.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);
			if(result){
				this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
						goodsId, suppliersInventoryList);
			}
			
			//批量保存商品分店库存到mysql
			//this.synInitAndAsynUpdateDomainRepository.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);
		}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("init").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		if(!result) {
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
			
	}

	/**
	 * 新增库存
	 * @param isExists
	 * @param inventoryInfoDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 */
	public CreateInventoryResultEnum createInventory(boolean isExists, GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
		boolean result = false;
		try {
		// 保存商品库存
		if (isExists && inventoryInfoDO != null) {
			
			result = this.inventoryInitAndUpdateHandle.saveGoodsInventory(inventoryInfoDO);
			if(result){
				this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
						inventoryInfoDO);
			}
			
			
			//保存库存信息到mysql
			//this.synInitAndAsynUpdateDomainRepository.saveGoodsInventory(inventoryInfoDO);
		}
			
		// 保选型库存
		if (!CollectionUtils.isEmpty(selectionInventoryList)) {
			result = this.inventoryInitAndUpdateHandle.saveBatchGoodsSelection(goodsId, selectionInventoryList);
			if(result){
				this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(
						goodsId, selectionInventoryList);
			}
			
			//批量保存商品选型库存到mysql
			//this.synInitAndAsynUpdateDomainRepository.saveBatchGoodsSelection(goodsId, selectionInventoryList);
		}
			
		// 保存分店库存
		if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
			result = this.inventoryInitAndUpdateHandle.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);
			if(result){
				this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
						goodsId, suppliersInventoryList);
			}
			
			//批量保存商品分店库存到mysql
			//this.synInitAndAsynUpdateDomainRepository.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);
		}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("createInventory").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		if(!result) {
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;	
	}
	/**
	 * 更新mysql库库存信息
	 * @param inventoryInfoDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 */
	public boolean updateMysqlInventory(GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
		boolean handler = true;
		try {
		// 更新商品库存
		if (inventoryInfoDO != null) {
			//更新库存信息到mysql
			boolean result = this.inventoryInitAndUpdateHandle.updateGoodsInventory(inventoryInfoDO);
			if(!result) {
				handler = false;
			}
			//this.synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryInfoDO);
		}
		
		// 保选型库存
		if (!CollectionUtils.isEmpty(selectionInventoryList)) {
			//批量更新商品选型库存到mysql
			boolean result = this.inventoryInitAndUpdateHandle.updateBatchGoodsSelection(goodsId, selectionInventoryList);
			if(!result){
				handler = false;
			}
			//this.synInitAndAsynUpdateDomainRepository.updateBatchGoodsSelection(goodsId, selectionInventoryList);
		}
		
		// 保存分店库存
		if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
			//批量更新商品分店库存到mysql
			boolean result = this.inventoryInitAndUpdateHandle.updateBatchGoodsSuppliers(goodsId, suppliersInventoryList);
			if(result){
				handler = false;
			}
			//this.synInitAndAsynUpdateDomainRepository.updateBatchGoodsSuppliers(goodsId, suppliersInventoryList);
		}
		} catch (Exception e) {
			handler = false;
			this.writeBusErrorLog(
					lm.setMethod("updateMysqlInventory").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			//return CreateInventoryResultEnum.DB_ERROR;
		}
		return handler;
	}
	
	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	
	public void setSynInitAndAysnMysqlService(
			SynInitAndAysnMysqlService synInitAndAysnMysqlService) {
		this.synInitAndAysnMysqlService = synInitAndAysnMysqlService;
	}

	public void setInventoryInitAndUpdateHandle(
			InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle) {
		this.inventoryInitAndUpdateHandle = inventoryInitAndUpdateHandle;
	}

}
