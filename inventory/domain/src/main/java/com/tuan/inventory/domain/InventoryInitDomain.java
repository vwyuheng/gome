package com.tuan.inventory.domain;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.InitCacheDomainRepository;
import com.tuan.inventory.domain.repository.SynInitAndAsynUpdateDomainRepository;
/**
 * 初始化库存domain
 * @author henry.yu
 * @date 2014/4/23
 */
public class InventoryInitDomain {

	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	private GoodsInventoryDO inventoryInfoDO;
	// 初始化用
	private List<GoodsSuppliersDO> suppliersInventoryList;
	private List<GoodsSelectionDO> selectionInventoryList;
	private Long goodsId;
	
	// 是否需要初始化
	private boolean isInit;

	// 业务检查
	public void busiCheck() {
		// 初始化检查
		this.initCheck();
		if (isInit) {
			this.init();
		}
		//return CreateInventoryResultEnum.SUCCESS;
	}
	// 初始化检查
	public void initCheck() {
		//this.goodsId = Long.valueOf(param.getGoodsId());
		if (goodsId > 0) { // limitStorage>0:库存无限制；1：限制库存
			//boolean isExists = this.goodsInventoryDomainRepository
				//	.isGoodsExists(goodsId);
			
			this.inventoryInfoDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
			//if (isExists) { // 不存在
			if(inventoryInfoDO==null) {
				// 初始化库存
				this.isInit = true;
				// 初始化商品库存信息
				this.inventoryInfoDO = this.initCacheDomainRepository
						.getInventoryInfoByGoodsId(goodsId);
				// 查询该商品分店库存信息
				selectionInventoryList = this.initCacheDomainRepository
						.querySelectionByGoodsId(goodsId);
				suppliersInventoryList = this.initCacheDomainRepository
						.selectGoodsSuppliersInventoryByGoodsId(goodsId);
			}
		}

	}
	/**
	 * 库存初始化
	 */
	public void init() {
		// 保存商品库存
		if (inventoryInfoDO != null) {
			this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
					inventoryInfoDO);
			//保存库存信息到mysql
			this.synInitAndAsynUpdateDomainRepository.saveGoodsInventory(inventoryInfoDO);
		}
			
		// 保选型库存
		if (!CollectionUtils.isEmpty(selectionInventoryList)) {
			this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(
					goodsId, selectionInventoryList);
			//批量保存商品选型库存到mysql
			this.synInitAndAsynUpdateDomainRepository.saveBatchGoodsSelection(goodsId, selectionInventoryList);
		}
			
		// 保存分店库存
		if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
			this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
					goodsId, suppliersInventoryList);
			//批量保存商品分店库存到mysql
			this.synInitAndAsynUpdateDomainRepository.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);
		}
			
	}

	/**
	 * 新增库存
	 * @param isExists
	 * @param inventoryInfoDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 */
	public void createInventory(boolean isExists, GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
		// 保存商品库存
		if (isExists && inventoryInfoDO != null) {
			this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
					inventoryInfoDO);
			//保存库存信息到mysql
			this.synInitAndAsynUpdateDomainRepository.saveGoodsInventory(inventoryInfoDO);
		}
			
		// 保选型库存
		if (!CollectionUtils.isEmpty(selectionInventoryList)) {
			this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(
					goodsId, selectionInventoryList);
			//批量保存商品选型库存到mysql
			this.synInitAndAsynUpdateDomainRepository.saveBatchGoodsSelection(goodsId, selectionInventoryList);
		}
			
		// 保存分店库存
		if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
			this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
					goodsId, suppliersInventoryList);
			//批量保存商品分店库存到mysql
			this.synInitAndAsynUpdateDomainRepository.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);
		}
			
	}
	/**
	 * 更新mysql库库存信息
	 * @param inventoryInfoDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 */
	public void updateMysqlInventory(GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
		// 更新商品库存
		if (inventoryInfoDO != null) {
			//更新库存信息到mysql
			this.synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryInfoDO);
		}
		
		// 保选型库存
		if (!CollectionUtils.isEmpty(selectionInventoryList)) {
			//批量更新商品选型库存到mysql
			this.synInitAndAsynUpdateDomainRepository.updateBatchGoodsSelection(goodsId, selectionInventoryList);
		}
		
		// 保存分店库存
		if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
			//批量更新商品分店库存到mysql
			this.synInitAndAsynUpdateDomainRepository.updateBatchGoodsSuppliers(goodsId, suppliersInventoryList);
		}
		
	}
	
	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}

	public void setInitCacheDomainRepository(
			InitCacheDomainRepository initCacheDomainRepository) {
		this.initCacheDomainRepository = initCacheDomainRepository;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	
	public void setSynInitAndAsynUpdateDomainRepository(
			SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository) {
		this.synInitAndAsynUpdateDomainRepository = synInitAndAsynUpdateDomainRepository;
	}

}
