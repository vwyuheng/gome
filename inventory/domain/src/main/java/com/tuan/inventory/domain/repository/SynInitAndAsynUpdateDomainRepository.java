package com.tuan.inventory.domain.repository;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.TuanRuntimeException;
import com.tuan.inventory.dao.SynInitAndAsynUpdateDAO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.model.util.QueueConstant;
/**
 * 用于同步初始化数据到mysql，
 * 异步更新数据到mysql
 * @author henry.yu
 * @date 2014/4/24
 */
public class SynInitAndAsynUpdateDomainRepository {
	@Resource
	private SynInitAndAsynUpdateDAO synInitAndAsynUpdateDAO;
	
	private static Log log = LogFactory.getLog(SynInitAndAsynUpdateDomainRepository.class);
	/**
	 * 保存插入商品库存
	 * @param goodsDO
	 */
	public void saveGoodsInventory(GoodsInventoryDO goodsDO) {
		goodsDO.setTotalNumber(goodsDO.getLimitStorage()==0?Integer.MAX_VALUE:goodsDO.getTotalNumber());
		goodsDO.setLeftNumber(goodsDO.getLimitStorage()==0?Integer.MAX_VALUE:goodsDO.getLeftNumber());
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
	 * @throws Exception 
	 */
	public void saveBatchGoodsSelection(Long goodsId, List<GoodsSelectionDO> selectionDOList) throws Exception {

		if (!CollectionUtils.isEmpty(selectionDOList)) { // if1
			for (GoodsSelectionDO srDO : selectionDOList) { // for
				if (srDO.getId() > 0) { // if选型
					//将商品id set到选型中
					srDO.setGoodsId(goodsId);
					srDO.setTotalNumber(srDO.getLimitStorage()==0?Integer.MAX_VALUE:srDO.getTotalNumber());
					srDO.setLeftNumber(srDO.getLimitStorage()==0?Integer.MAX_VALUE:srDO.getLeftNumber());
					this.saveGoodsSelection(srDO);
				}
				
			}//for
		}//if1
			
	
	}
	
	/***
	 * 保存商品选型库存
	 * @param selectionDO
	 */
	public void saveGoodsSelection(GoodsSelectionDO selectionDO) throws Exception{
		try {
		this.synInitAndAsynUpdateDAO.insertGoodsSelectionDO(selectionDO);
		}  catch (Exception e) {
			log.error(
					"SynInitAndAsynUpdateDomainRepository.saveGoodsSelection error occured!"
							+ e.getMessage(), e);
			if (e instanceof DataIntegrityViolationException) {// 消息数据重复
				throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
						"Duplicate entry '" + selectionDO.getId()
								+ "' for key 'selectionId'", e);
			}
			throw new TuanRuntimeException(
					QueueConstant.SERVICE_DATABASE_FALIURE,
					"SynInitAndAsynUpdateDomainRepository.saveGoodsSelection error occured!",
					e);
			
		}
	}
	/**
	 * 批量更新选型库存
	 * @param goodsId
	 * @param selectionDOList
	 */
	public void updateBatchGoodsSelection(Long goodsId, List<GoodsSelectionDO> selectionDOList) throws Exception{

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
	public void updateGoodsSelection(GoodsSelectionDO selectionDO) throws Exception{
		try {
		this.synInitAndAsynUpdateDAO.updateGoodsSelectionDO(selectionDO);
		} catch (Exception e) {
			log.error(
					"SynInitAndAsynUpdateDomainRepository.updateGoodsInventory error occured!"
							+ e.getMessage(), e);
			if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
				throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
						"update invalid '" + selectionDO.getId()
								+ "' for key 'selectionId'", e);
			}
			throw new TuanRuntimeException(
					QueueConstant.SERVICE_DATABASE_FALIURE,
					"SynInitAndAsynUpdateDomainRepository.updateGoodsInventory error occured!",
					e);
			
		}
	}
	/**
	 * 批量保存分店库存信息
	 * @param goodsId
	 * @param suppliersDOList
	 */
	public void saveBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) throws Exception{
		if (!CollectionUtils.isEmpty(suppliersDOList)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDOList) { // for
				
				if (sDO.getId() > 0) { // if分店
					sDO.setGoodsId(goodsId);
					sDO.setTotalNumber(sDO.getLimitStorage()==0?Integer.MAX_VALUE:sDO.getTotalNumber());
					sDO.setLeftNumber(sDO.getLimitStorage()==0?Integer.MAX_VALUE:sDO.getLeftNumber());
					this.saveGoodsSuppliers(sDO);
				}
				
			}//for
		}//if1
			
	}
	
	/**
	 * 保存商品分店库存
	 * @param suppliersDO
	 */
	public void saveGoodsSuppliers(GoodsSuppliersDO suppliersDO) throws Exception{
		try {
		this.synInitAndAsynUpdateDAO.insertGoodsSuppliersDO(suppliersDO);
		}  catch (Exception e) {
			log.error(
					"SynInitAndAsynUpdateDomainRepository.saveGoodsSuppliers error occured!"
							+ e.getMessage(), e);
			if (e instanceof DataIntegrityViolationException) {// 消息数据重复
				throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
						"Duplicate entry '" + suppliersDO.getId()
								+ "' for key 'suppliersId'", e);
			}
			throw new TuanRuntimeException(
					QueueConstant.SERVICE_DATABASE_FALIURE,
					"SynInitAndAsynUpdateDomainRepository.saveGoodsSuppliers error occured!",
					e);
			
		}
		
	}
	/**
	 * 批量更新分店库存
	 * @param goodsId
	 * @param suppliersDOList
	 */
	public void updateBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) throws Exception{
		if (!CollectionUtils.isEmpty(suppliersDOList)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDOList) { // for
				if (sDO.getId() > 0) { // if分店
					//分店中
					sDO.setGoodsId(goodsId);
					this.updateGoodsSuppliers(sDO);
				}
				
			}//for
		}//if1
			
	}
	/**
	 * 更新商品分店库存
	 * @param suppliersDO
	 */
	public void updateGoodsSuppliers(GoodsSuppliersDO suppliersDO) throws Exception{
		try {
		this.synInitAndAsynUpdateDAO.updateGoodsSuppliersDO(suppliersDO);
		} catch (Exception e) {
			log.error(
					"SynInitAndAsynUpdateDomainRepository.updateGoodsSuppliers error occured!"
							+ e.getMessage(), e);
			if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
				throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
						"update invalid '" + suppliersDO.getId()
								+ "' for key 'suppliersId'", e);
			}
			throw new TuanRuntimeException(
					QueueConstant.SERVICE_DATABASE_FALIURE,
					"SynInitAndAsynUpdateDomainRepository.updateGoodsSuppliers error occured!",
					e);
			
		}
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