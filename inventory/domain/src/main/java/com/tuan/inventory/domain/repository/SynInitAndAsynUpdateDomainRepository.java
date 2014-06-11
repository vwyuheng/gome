package com.tuan.inventory.domain.repository;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.SynInitAndAsynUpdateDAO;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
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
	
	public GoodsInventoryDO selectGoodsInventoryDO(long goodsId) {
		return this.synInitAndAsynUpdateDAO.selectGoodsInventoryDO(goodsId);
	}
	public GoodsSelectionDO selectGoodsSelectionDO(long selectionId) {
		return this.synInitAndAsynUpdateDAO.selectGoodsSelectionDO(selectionId);
	}
	public GoodsSuppliersDO selectGoodsSuppliersDO(long suppliersId) {
		return this.synInitAndAsynUpdateDAO.selectGoodsSuppliersDO(suppliersId);
	}
	public GoodsInventoryWMSDO selectGoodsInventoryWMSDO(String wmsGoodsId) {
		return this.synInitAndAsynUpdateDAO.selectGoodsInventoryWMSDO(wmsGoodsId);
	}
	
	/**
	 * 保存插入商品库存
	 * @param goodsDO
	 */
	public void saveGoodsInventory(GoodsInventoryDO goodsDO) {
		//goodsDO.setTotalNumber(goodsDO.getLimitStorage()==0?Integer.MAX_VALUE:goodsDO.getTotalNumber());
		//goodsDO.setLeftNumber(goodsDO.getLimitStorage()==0?Integer.MAX_VALUE:goodsDO.getLeftNumber());
		this.synInitAndAsynUpdateDAO.insertGoodsInventoryDO(goodsDO);
	}
	
	
	public void saveBatchGoodsInventory(List<GoodsInventoryDO> wmsInventoryList) throws Exception {

		if (!CollectionUtils.isEmpty(wmsInventoryList)) { // if1
			for (GoodsInventoryDO goodsDO : wmsInventoryList) { // for
				long goodsId = goodsDO.getGoodsId();
				if (goodsId > 0) { // if选型
					GoodsInventoryDO tmpDO = synInitAndAsynUpdateDAO.selectGoodsInventoryDO(goodsId);
					if(tmpDO==null) {
						//
						this.saveGoodsInventory(goodsDO);
					}
					
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
					long selectionId = srDO.getId();
					//保存前检查下是否存在？
					GoodsSelectionDO tmpDo = synInitAndAsynUpdateDAO.selectGoodsSelectionDO(selectionId);
					if(tmpDo==null) {
						//将商品id set到选型中
						srDO.setGoodsId(goodsId);
						//srDO.setTotalNumber(srDO.getLimitStorage()==0?Integer.MAX_VALUE:srDO.getTotalNumber());
						//srDO.setLeftNumber(srDO.getLimitStorage()==0?Integer.MAX_VALUE:srDO.getLeftNumber());
						this.saveGoodsSelection(srDO);
					}
					
				}
				
			}//for
		}//if1
			
	
	}
	public void saveBatchGoodsWms(long goodsId,List<GoodsSelectionDO> selectionDOList) throws Exception {
		
		if (!CollectionUtils.isEmpty(selectionDOList)) { // if1
			for (GoodsSelectionDO srDO : selectionDOList) { // for
				long selectionId = srDO.getId();
				if (selectionId > 0) { // if选型
					//将商品id set到选型中
					GoodsSelectionDO tmpDO = synInitAndAsynUpdateDAO.selectGoodsSelectionDO(selectionId);
					if(tmpDO==null) {
						srDO.setGoodsId(goodsId);
						this.saveGoodsSelection(srDO);
					}
					
				}
				
			}//for
		}//if1
		
		
	}
	/**
	 * openapi 调的就不需要商品id，因为还不知道该选型属于哪个商品
	 * @param selectionDOList
	 * @throws Exception
	 */
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
		this.synInitAndAsynUpdateDAO.insertGoodsSelectionDO(selectionDO);
		
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
		this.synInitAndAsynUpdateDAO.updateGoodsSelectionDO(selectionDO);
		
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
					long suppliersId = sDO.getSuppliersId();
					GoodsSuppliersDO tmpDO = synInitAndAsynUpdateDAO.selectGoodsSuppliersDO(suppliersId);
					if(tmpDO==null) {
						sDO.setGoodsId(goodsId);
						//sDO.setTotalNumber(sDO.getLimitStorage()==0?Integer.MAX_VALUE:sDO.getTotalNumber());
						//sDO.setLeftNumber(sDO.getLimitStorage()==0?Integer.MAX_VALUE:sDO.getLeftNumber());
						this.saveGoodsSuppliers(sDO);
					}
					
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
			String wmsGoodsId = wmsInventory.getWmsGoodsId();
			
				if (!StringUtils.isEmpty(wmsGoodsId)) { // if分店
					GoodsInventoryWMSDO tmpDO = synInitAndAsynUpdateDAO.selectGoodsInventoryWMSDO(wmsGoodsId);
					if(tmpDO==null) {
						this.saveGoodsInventoryWMS(wmsInventory);
					}
					
				}
				
			//}//for
		}//if1
			
	}
	/**
	 * 保存商品分店库存
	 * @param suppliersDO
	 */
	public void saveGoodsSuppliers(GoodsSuppliersDO suppliersDO) throws Exception{
		this.synInitAndAsynUpdateDAO.insertGoodsSuppliersDO(suppliersDO);
		
		
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
	
		this.synInitAndAsynUpdateDAO.updateGoodsSuppliersDO(suppliersDO);
		
	}
	/**
	 * 保存物流商品库存
	 * @param wmsDO
	 */
	public void saveGoodsInventoryWMS(GoodsInventoryWMSDO wmsDO) throws Exception{
		this.synInitAndAsynUpdateDAO.insertGoodsInventoryWMSDO(wmsDO);
	}
	
	
	/**
	 * 保存商品库存基表
	 * @param wmsDO
	 */
	public void saveGoodsBaseInventoryDO(GoodsBaseInventoryDO baseInventoryDO) throws Exception{
		this.synInitAndAsynUpdateDAO.insertGoodsBaseInventoryDO(baseInventoryDO);
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

	/**
	 * 更新商品库存基表
	 * @param wmsDO
	 */
	public void updateGoodsBaseInventoryDO(GoodsBaseInventoryDO baseInventoryDO) throws Exception{
		this.synInitAndAsynUpdateDAO.updateGoodsBaseInventoryDO(baseInventoryDO);
	}
	
	public GoodsBaseInventoryDO getGoodBaseBygoodsId(long goodsBaseId) {
		return synInitAndAsynUpdateDAO.selectGoodBaseBygoodsId(goodsBaseId);
	}
	
	public GoodsBaseInventoryDO selectInventoryBase4Init(long goodsBaseId) {
		return synInitAndAsynUpdateDAO.selectInventoryBase4Init(goodsBaseId);
	}
}
