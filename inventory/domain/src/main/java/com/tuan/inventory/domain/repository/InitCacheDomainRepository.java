package com.tuan.inventory.domain.repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.NullCacheInitDAO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSelectionRelationGoodDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.dao.data.redis.WmsIsBeDeliveryDO;
/**
 * 用于初始化redis缓存
 * @author henry.yu
 * @date 2014/3/11
 */
public class InitCacheDomainRepository {
	@Resource
	private GoodTypeDomainRepository goodTypeDomainRepository;
	@Resource
	private NullCacheInitDAO nullCacheInitDAO;
	
	private static Log log = LogFactory.getLog("SYSERROR.LOG");
	/**
	 * 加载分店配型关系数据
	 */
	public GoodsSelectionDO getCacheSelectionRelationInfoById(
			int id){
		GoodsSelectionRelationDO srDo = null;
		try {
			srDo = goodTypeDomainRepository.selectSelectionRelationBySrId(id);
		} catch (Exception e) {
			log.error("InitCacheDomainRepository.getCacheSelectionRelationInfoById invoke error [SelectionRelationId="
					+ id + "]", e);
		}
		if (srDo == null) {
			return null;
		}else {
			GoodsSelectionDO rsrDo = new GoodsSelectionDO();
			rsrDo.setId(srDo.getId().longValue());
			rsrDo.setSuppliersId(Long.valueOf(srDo==null?0:(srDo.getSuppliersId()==null?0:srDo.getSuppliersId())));
			rsrDo.setGoodTypeId(srDo.getGoodTypeId().longValue());
			rsrDo.setLeftNumber(srDo.getLeftNumber());
			rsrDo.setTotalNumber(srDo.getTotalNumber());
			rsrDo.setLimitStorage(srDo.getLimitStorage());
			rsrDo.setSuppliersInventoryId(srDo.getSuppliersInventoryId());
			rsrDo.setSuppliersSubId(srDo.getSuppliersSubId());
			
			return rsrDo;
		}
		
	}
	/**
	 * 更加商品id查询商品所属的选型信息
	 * @param goodsId
	 * @return
	 */
	public List<GoodsSelectionDO> querySelectionByGoodsId(
			long goodsId){
		List<GoodsSelectionDO> result = null;
		List<GoodsSelectionRelationGoodDO> selectionList = null;
		selectionList = goodTypeDomainRepository.selectSelectionRelationBySrIds(null, goodsId);
		if(!CollectionUtils.isEmpty(selectionList)) {
			result = new ArrayList<GoodsSelectionDO>();
			for(GoodsSelectionRelationGoodDO selection:selectionList) {
				GoodsSelectionDO rsrDo = new GoodsSelectionDO();
				rsrDo.setId(selection.getId().longValue());
				rsrDo.setSuppliersId(Long.valueOf(selection==null?0:(selection.getSuppliersId()==null?0:selection.getSuppliersId())));
				rsrDo.setSuppliersInventoryId(selection.getSuppliersInventoryId());
				rsrDo.setGoodTypeId(selection.getGoodTypeId().longValue());
				rsrDo.setLeftNumber(selection.getLeftNumber());
				rsrDo.setTotalNumber(selection.getTotalNumber());
				rsrDo.setLimitStorage(selection.getLimitStorage());
				rsrDo.setSuppliersSubId(selection.getSuppliersSubId());
				rsrDo.setGoodsId(goodsId);
				result.add(rsrDo);
			}
		}
		return result ;
	}
	public List<GoodsSuppliersDO> selectGoodsSuppliersInventoryByGoodsId(long goodsId){

		List<GoodsSuppliersDO> result = null;
		List<GoodsSuppliersInventoryDO> suppliersList = goodTypeDomainRepository.selectGoodsSuppliersInventoryByGoodsId(goodsId);
		if(!CollectionUtils.isEmpty(suppliersList)) {
			result = new ArrayList<GoodsSuppliersDO>();
			for(GoodsSuppliersInventoryDO supplier:suppliersList) {
				GoodsSuppliersDO rgsrDo = new GoodsSuppliersDO();
				rgsrDo.setId(supplier.getId().longValue());
				rgsrDo.setSuppliersId(Long.valueOf(supplier.getSuppliersId()));
				rgsrDo.setGoodsId(Long.valueOf(supplier.getGoodsId()));
				rgsrDo.setLeftNumber(supplier.getLeftNumber());
				rgsrDo.setTotalNumber(supplier.getTotalNumber());
				rgsrDo.setLimitStorage(supplier.getLimitStorage());
				result.add(rgsrDo);
			}
		}
		return result ;
	
	}
	
	/**
	 * 加载分店选型信息
	 */
	public GoodsSelectionRelationDO getSelectionRelationDOById(int id){
		GoodsSelectionRelationDO srDo = null;
		try {
			srDo = goodTypeDomainRepository.selectSelectionRelationBySrId(id);
		} catch (Exception e) {
			log.error("InitCacheDomainRepository.getSelectionRelationDOById invoke error [SelectionRelationId="
					+ id + "]", e);
		}
		if (srDo == null) {
			return null;
		}else {
			return srDo;
		}
			
		
	}
	/**
	 * 加载商品商家库存信息并转换为redis存储对象 
	 */
	public GoodsSuppliersDO getCacheSuppliersInventoryInfoById(int id) {
		GoodsSuppliersDO rgsiDO = null;
		GoodsSuppliersInventoryDO gsiDO = goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId(id);
		if(gsiDO == null){
			return null;
		}else {
			rgsiDO = new GoodsSuppliersDO();
			rgsiDO.setId(gsiDO.getId().longValue());
			rgsiDO.setGoodsId(gsiDO.getGoodsId().longValue());
			rgsiDO.setLeftNumber(gsiDO.getLeftNumber());
			rgsiDO.setTotalNumber(gsiDO.getTotalNumber());
			rgsiDO.setSuppliersId(gsiDO.getSuppliersId().longValue());
			rgsiDO.setLimitStorage(gsiDO.getLimitStorage());
		}
			
		return rgsiDO;
	}
	/**
	 * 加载商品商家库存信息
	 */
	public GoodsSuppliersInventoryDO getSuppliersInventoryDOById(int id) {
		return goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId(id);
	}
	
	/**
	 * 根据goodsTypeId列表查询指定选型信息列表
	 * @param goodsTypeIdList
	 * @return
	 */
	public List<GoodsSelectionDO> selectSelectionByGoodsTypeIds(List<Long> goodsTypeIdList) {
		
		List<GoodsSelectionDO> result = null;
		List<GoodsSelectionRelationGoodDO> selectionList = null;
		selectionList = goodTypeDomainRepository.selectSelectionByGoodsTypeIds(goodsTypeIdList);
		if(!CollectionUtils.isEmpty(selectionList)) {
			result = new ArrayList<GoodsSelectionDO>();
			for(GoodsSelectionRelationGoodDO selection:selectionList) {
				GoodsSelectionDO rsrDo = new GoodsSelectionDO();
				rsrDo.setId(selection.getId().longValue());
				rsrDo.setSuppliersInventoryId(selection!=null?(selection.getSuppliersId()!=null?selection.getSuppliersId():0):0);
				rsrDo.setGoodTypeId(selection.getGoodTypeId().longValue());
				rsrDo.setLeftNumber(selection.getLeftNumber());
				rsrDo.setTotalNumber(selection.getTotalNumber());
				rsrDo.setLimitStorage(selection.getLimitStorage());
				result.add(rsrDo);
			}
		}
		return result ;

	}
	public GoodsInventoryDO getInventoryInfoByGoodsId(Long goodsId) {
		return nullCacheInitDAO.selectRedisInventory(goodsId);
	}
	
	public GoodsInventoryWMSDO selectGoodsInventoryWMS(String wmsGoodsId,int isBeDelivery) {
		return nullCacheInitDAO.selectGoodsInventoryWMS(wmsGoodsId,isBeDelivery);
	}
	public GoodsInventoryWMSDO selectIsOrNotGoodsWMS(Long goodsId) {
		return nullCacheInitDAO.selectIsOrNotGoodsWMS(goodsId);
	}
	public List<GoodsInventoryDO> selectInventory4Wms(String wmsGoodsId) {
		return nullCacheInitDAO.selectInventory4Wms(wmsGoodsId);
	}

	public WmsIsBeDeliveryDO selectWmsIsBeDeliveryResult(String wmsGoodsId) {
		return nullCacheInitDAO.selectWmsIsBeDeliveryResult(wmsGoodsId);
	}
}
