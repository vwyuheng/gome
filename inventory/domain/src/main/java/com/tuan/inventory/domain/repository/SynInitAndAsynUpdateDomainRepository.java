package com.tuan.inventory.domain.repository;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.SynInitAndAsynUpdateDAO;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
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
	//@Resource
	//private SequenceUtil sequenceUtil;
	/**
	 * 保存插入商品库存
	 * @param goodsDO
	 */
	public void saveGoodsInventory(GoodsInventoryDO goodsDO) {
		goodsDO.setTotalNumber(goodsDO.getLimitStorage()==0?Integer.MAX_VALUE:goodsDO.getTotalNumber());
		goodsDO.setLeftNumber(goodsDO.getLimitStorage()==0?Integer.MAX_VALUE:goodsDO.getLeftNumber());
		this.synInitAndAsynUpdateDAO.insertGoodsInventoryDO(goodsDO);
	}
	
	
	public void saveBatchGoodsInventory(List<GoodsInventoryDO> wmsInventoryList) throws Exception {

		if (!CollectionUtils.isEmpty(wmsInventoryList)) { // if1
			for (GoodsInventoryDO goodsDO : wmsInventoryList) { // for
				if (goodsDO.getGoodsId() > 0) { // if选型
					//将商品id set到选型中
					this.saveGoodsInventory(goodsDO);
				}
				
			}//for
		}//if1
			
	
	}
	
	
	/**
	 * 更新商品库存
	 * @param goodsDO
	 */
	public void updateGoodsInventory(GoodsInventoryDO goodsDO) {
		this.synInitAndAsynUpdateDAO.updateGoodsInventoryDO(goodsDO);
	}
	
	
	public void updateBatchGoodsInventory(List<GoodsInventoryDO> wmsInventoryList) throws Exception{
		
		if (!CollectionUtils.isEmpty(wmsInventoryList)) { // if1
			for (GoodsInventoryDO result : wmsInventoryList) { // for
				if (result.getGoodsId() > 0) { // if选型
					this.updateGoodsInventory(result);
				}
				
			}//for
		}//if1
		
		
	}
	
	public int deleteGoodsInventory(long goodsId) {
		return this.synInitAndAsynUpdateDAO.deleteGoodsInventoryDO(goodsId);
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
	public void saveBatchGoodsWms(List<GoodsSelectionDO> selectionDOList) throws Exception {
		
		if (!CollectionUtils.isEmpty(selectionDOList)) { // if1
			for (GoodsSelectionDO srDO : selectionDOList) { // for
				if (srDO.getId() > 0) { // if选型
					//将商品id set到选型中
					//srDO.setGoodsId(goodsId);
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
		//try {
		this.synInitAndAsynUpdateDAO.insertGoodsSelectionDO(selectionDO);
		/*}  catch (Exception e) {
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
			
		}*/
	}
	
	public int deleteGoodsSelection(long selectionId) throws Exception{
		
		return this.synInitAndAsynUpdateDAO.deleteGoodsSelectionDO(selectionId);
		
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
	public void updateBatchGoodsSelectionWms(List<GoodsWmsSelectionResult> selectionResultList) throws Exception{
		
		if (!CollectionUtils.isEmpty(selectionResultList)) { // if1
			for (GoodsWmsSelectionResult result : selectionResultList) { // for
				if (result.getGoodTypeId() > 0) { // if选型
					//将商品id set到选型中
					//srDO.setGoodsId(goodsId);
					this.updateGoodsSelectionWms(result);
				}
				
			}//for
		}//if1
		
		
	}
	
	public void batchDelGoodsSelection(List<GoodsSelectionDO> selectionDOList) throws Exception{

		if (!CollectionUtils.isEmpty(selectionDOList)) { // if1
			for (GoodsSelectionDO srDO : selectionDOList) { // for
				if (srDO.getId() > 0) { // if选型
					//将商品id set到选型中
					this.deleteGoodsSelection(srDO.getId());
				}
				
			}//for
		}//if1
			
	
	}
	
	/**
	 * 更新商品选型库存
	 * @param selectionDO
	 */
	public void updateGoodsSelection(GoodsSelectionDO selectionDO) throws Exception{
		//try {
		this.synInitAndAsynUpdateDAO.updateGoodsSelectionDO(selectionDO);
		/*} catch (Exception e) {
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
			
		}*/
	}
	public void updateGoodsSelectionWms(GoodsWmsSelectionResult selection) throws Exception{
		
		this.synInitAndAsynUpdateDAO.updateGoodsSelectionWmsDO(selection);
		
	}
	/**
	 * 批量保存分店库存信息
	 * @param goodsId
	 * @param suppliersDOList
	 */
	public void saveBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) throws Exception{
		if (!CollectionUtils.isEmpty(suppliersDOList)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDOList) { // for
				
				if (sDO.getSuppliersId() > 0) { // if分店
					//sDO.setId(sequenceUtil.getSequence(SEQNAME.seq_suppliers));
					sDO.setGoodsId(goodsId);
					sDO.setTotalNumber(sDO.getLimitStorage()==0?Integer.MAX_VALUE:sDO.getTotalNumber());
					sDO.setLeftNumber(sDO.getLimitStorage()==0?Integer.MAX_VALUE:sDO.getLeftNumber());
					this.saveGoodsSuppliers(sDO);
				}
				
			}//for
		}//if1
			
	}
	
	/**
	 * 批量保存物流库存信息
	 * @param goodsId
	 * @param wmsInventoryList
	 * @throws Exception
	 */
	public void saveGoodsWms(GoodsInventoryWMSDO wmsInventory) throws Exception{
		if (wmsInventory!=null) { // if1
			//for (GoodsInventoryWMSDO wmsDO : wmsInventoryList) { // for
				if (!StringUtils.isEmpty(wmsInventory.getWmsGoodsId())) { // if分店
					this.saveGoodsInventoryWMS(wmsInventory);
				}
				
			//}//for
		}//if1
			
	}
	/**
	 * 保存商品分店库存
	 * @param suppliersDO
	 */
	public void saveGoodsSuppliers(GoodsSuppliersDO suppliersDO) throws Exception{
		//try {
		this.synInitAndAsynUpdateDAO.insertGoodsSuppliersDO(suppliersDO);
		/*}  catch (Exception e) {
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
			
		}*/
		
	}
	
	public int deleteGoodsSuppliers(long suppliersId) throws Exception{
		
		return this.synInitAndAsynUpdateDAO.deleteGoodsSuppliersDO(suppliersId);
		
		
	}
	/**
	 * 批量更新分店库存
	 * @param goodsId
	 * @param suppliersDOList
	 */
	public void updateBatchGoodsSuppliers(Long goodsId, List<GoodsSuppliersDO> suppliersDOList) throws Exception{
		if (!CollectionUtils.isEmpty(suppliersDOList)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDOList) { // for
				if (sDO.getSuppliersId() > 0) { // if分店
					//分店中
					sDO.setGoodsId(goodsId);
					this.updateGoodsSuppliers(sDO);
				}
				
			}//for
		}//if1
			
	}
	
	public void batchDeleteGoodsSuppliers(List<GoodsSuppliersDO> suppliersDOList) throws Exception{
		if (!CollectionUtils.isEmpty(suppliersDOList)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDOList) { // for
				if (sDO.getSuppliersId() > 0) { // if分店
					//分店中
					this.deleteGoodsSuppliers(sDO.getSuppliersId());
				}
				
			}//for
		}//if1
			
	}
	
	/**
	 * 更新商品分店库存
	 * @param suppliersDO
	 */
	public void updateGoodsSuppliers(GoodsSuppliersDO suppliersDO) throws Exception{
		//try {
		this.synInitAndAsynUpdateDAO.updateGoodsSuppliersDO(suppliersDO);
		/*} catch (Exception e) {
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
			
		}*/
	}
	/**
	 * 保存物流商品库存
	 * @param wmsDO
	 */
	public void saveGoodsInventoryWMS(GoodsInventoryWMSDO wmsDO) throws Exception{
		this.synInitAndAsynUpdateDAO.insertGoodsInventoryWMSDO(wmsDO);
	}
	
	public void batchUpdateGoodsInventoryWMS(List<GoodsInventoryWMSDO> wmsList) throws Exception{
		
		if (!CollectionUtils.isEmpty(wmsList)) { // if1
			for (GoodsInventoryWMSDO result : wmsList) { // for
				if (!StringUtils.isEmpty(result.getWmsGoodsId())) { // if选型
					this.updateGoodsInventoryWMS(result);
				}
				
			}//for
		}//if1
		
		
	}
	
	/**
	 * 更新物流商品库存
	 * @param wmsDO
	 */
	public void updateGoodsInventoryWMS(GoodsInventoryWMSDO wmsDO) {
		this.synInitAndAsynUpdateDAO.updateGoodsInventoryWMSDO(wmsDO);
	}

	
}
