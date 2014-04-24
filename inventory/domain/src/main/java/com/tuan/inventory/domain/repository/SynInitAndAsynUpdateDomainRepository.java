package com.tuan.inventory.domain.repository;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.SynInitAndAsynUpdateDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
/**
 * 用于同步初始化数据到mysql，
 * 异步更新数据到mysql
 * @author henry.yu
 * @date 2014/4/24
 */
public class SynInitAndAsynUpdateDomainRepository {
	@Resource
	private SynInitAndAsynUpdateDAO synInitAndAsynUpdateDAO;
	
	//private static Log log = LogFactory.getLog(SynInitAndAsynUpdateDomainRepository.class);
	/**
	 * 保存插入商品库存
	 * @param goodsDO
	 */
	public void saveGoodsInventory(GoodsInventoryDO goodsDO) {
		this.synInitAndAsynUpdateDAO.insertGoodsInventoryDO(goodsDO);
	}
	/**
	 * 更新商品库存
	 * @param goodsDO
	 */
	public void updateGoodsInventory(GoodsInventoryDO goodsDO) {
		this.synInitAndAsynUpdateDAO.updateGoodsInventoryDO(goodsDO);
	}
	
	/**
	 * 批量保存商品选型库存
	 * @param goodsId
	 * @param selectionDO
	 */
	public void saveBatchGoodsSelection(Long goodsId, List<GoodsSelectionDO> selectionDOList) {

		if (!CollectionUtils.isEmpty(selectionDOList)) { // if1
			for (GoodsSelectionDO srDO : selectionDOList) { // for
				if (srDO.getId() > 0) { // if选型
					//将商品id set到选型中
					srDO.setGoodsId(goodsId);
					this.saveGoodsSelection(srDO);
				}
				
			}//for
		}//if1
			
	
	}
	
	/***
	 * 保存商品选型库存
	 * @param selectionDO
	 */
	public void saveGoodsSelection(GoodsSelectionDO selectionDO) {
		this.synInitAndAsynUpdateDAO.insertGoodsSelectionDO(selectionDO);
	}
	/**
	 * 批量更新选型库存
	 * @param goodsId
	 * @param selectionDOList
	 */
	public void updateBatchGoodsSelection(Long goodsId, List<GoodsSelectionDO> selectionDOList) {

		if (!CollectionUtils.isEmpty(selectionDOList)) { // if1
			for (GoodsSelectionDO srDO : selectionDOList) { // for
				if (srDO.getId() > 0) { // if选型
					//将商品id set到选型中
					srDO.setGoodsId(goodsId);
					this.updateGoodsSelection(srDO);
				}
				
			}//for
		}//if1
			
	
	}
	/**
	 * 更新商品选型库存
	 * @param selectionDO
	 */
	public void updateGoodsSelection(GoodsSelectionDO selectionDO) {
		this.synInitAndAsynUpdateDAO.updateGoodsSelectionDO(selectionDO);
	}
	/**
	 * 批量保存分店库存信息
	 * @param goodsId
	 * @param suppliersDOList
	 */
	public void saveBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) {
		if (!CollectionUtils.isEmpty(suppliersDOList)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDOList) { // for
				//分店中
				sDO.setGoodsId(goodsId);
				if (sDO.getId() > 0) { // if分店
					this.saveGoodsSuppliers(sDO);
				}
				
			}//for
		}//if1
			
	}
	
	/**
	 * 保存商品分店库存
	 * @param suppliersDO
	 */
	public void saveGoodsSuppliers(GoodsSuppliersDO suppliersDO) {
		this.synInitAndAsynUpdateDAO.insertGoodsSuppliersDO(suppliersDO);
	}
	/**
	 * 批量更新分店库存
	 * @param goodsId
	 * @param suppliersDOList
	 */
	public void updateBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) {
		if (!CollectionUtils.isEmpty(suppliersDOList)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDOList) { // for
				//分店中
				sDO.setGoodsId(goodsId);
				if (sDO.getId() > 0) { // if分店
					this.updateGoodsSuppliers(sDO);
				}
				
			}//for
		}//if1
			
	}
	/**
	 * 更新商品分店库存
	 * @param suppliersDO
	 */
	public void updateGoodsSuppliers(GoodsSuppliersDO suppliersDO) {
		this.synInitAndAsynUpdateDAO.updateGoodsSuppliersDO(suppliersDO);
	}
	/**
	 * 保存物流商品库存
	 * @param wmsDO
	 */
	public void saveGoodsInventoryWMS(GoodsInventoryWMSDO wmsDO) {
		this.synInitAndAsynUpdateDAO.insertGoodsInventoryWMSDO(wmsDO);
	}
	/**
	 * 更新物流商品库存
	 * @param wmsDO
	 */
	public void updateGoodsInventoryWMS(GoodsInventoryWMSDO wmsDO) {
		this.synInitAndAsynUpdateDAO.updateGoodsInventoryWMSDO(wmsDO);
	}

	
}
