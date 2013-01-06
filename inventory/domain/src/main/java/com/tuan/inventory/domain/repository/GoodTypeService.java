package com.tuan.inventory.domain.repository;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.OrderGoodsDO;
import com.tuan.inventory.dao.data.OrderInfoDetailDO;
import com.tuan.inventory.model.OrderGoodsSelectionModel;


public class GoodTypeService{
	
	private GoodTypeDomainRepository goodTypeDomainRepository;

	private static Log log = LogFactory.getLog(GoodTypeService.class);


	public int getSelectionRelationLeftNumberBySrId(int SelectionRelationId) {
		GoodsSelectionRelationDO srDo = null;
		try {
			srDo = goodTypeDomainRepository.selectSelectionRelationBySrId(SelectionRelationId);
		} catch (Exception e) {
			log.error("goodTypeDomainRepository.selectSelectionRelationBySrId invoke error [SelectionRelationId="
					+ SelectionRelationId + "]", e);
		}
		if (srDo == null) {
			return 0;
		}
		if (srDo.getLimitStorage() == 0) {
			return Integer.MAX_VALUE;
		} else {
			return srDo.getLeftNumber();
		}
	}

	public int getSuppliersInventoryLeftNumberBySiId(int SuppliersInventoryId) {
		GoodsSuppliersInventoryDO gsiDo = null;
		try {
			gsiDo = goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId(SuppliersInventoryId);
		} catch (Exception e) {
			log.error("goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId invoke error [SuppliersInventoryId="
					+ SuppliersInventoryId + "]", e);
		}
		if (gsiDo == null) {
			return 0;
		}
		if (gsiDo.getLimitStorage() == 0) {
			return Integer.MAX_VALUE;
		} else {
			return gsiDo.getLeftNumber();
		}
	}

	public GoodsSelectionRelationDO getSelectionRelationBySrId(int SelectionRelationId) {
		try {
			return goodTypeDomainRepository.selectSelectionRelationBySrId(SelectionRelationId);
		} catch (Exception e) {
			log.error("goodTypeDomainRepository.selectSelectionRelationBySrId invoke error [SelectionRelationId="
					+ SelectionRelationId + "]", e);
		}
		return null;
	}

	public GoodsSuppliersInventoryDO getSuppliersInventoryBySiId(int SuppliersInventoryId) {
		try {
			return goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId(SuppliersInventoryId);
		} catch (Exception e) {
			log.error("goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId invoke error [SuppliersInventoryId="
					+ SuppliersInventoryId + "]", e);
		}
		return null;
	}

	public int getSelectionRelationMaxNumberBySrId(int SelectionRelationId) {
		GoodsSelectionRelationDO srDo = null;
		try {
			srDo = goodTypeDomainRepository.selectSelectionRelationBySrId(SelectionRelationId);
		} catch (Exception e) {
			log.error("goodTypeDomainRepository.selectSelectionRelationBySrId invoke error [SelectionRelationId="
					+ SelectionRelationId + "]", e);
		}
		if (srDo == null) {
			return 0;
		}
		if (srDo.getLimitNumber() == 0) {
			return Integer.MAX_VALUE;
		}
		return srDo.getLimitNumber();
	}

	public int getSuppliersInventoryMaxNumberBySiId(int SuppliersInventoryId) {
		GoodsSuppliersInventoryDO gsiDo = null;
		try {
			gsiDo = goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId(SuppliersInventoryId);
		} catch (Exception e) {
			log.error("goodTypeDomainRepository.selectGoodsSuppliersInventoryBySiId invoke error [SuppliersInventoryId="
					+ SuppliersInventoryId + "]", e);
		}
		if (gsiDo == null) {
			return 0;
		}
		if (gsiDo.getLimitNumber() == 0) {
			return Integer.MAX_VALUE;
		}
		return gsiDo.getLimitNumber();
	}

	public boolean updateInventoryNumber(long orderId,long goodsId, int pindaoId, int num,
			List<OrderGoodsSelectionModel> goodsSelectionList,int limitStorage,Long goodsWmsId) {
		try {
			log.info("orderId="+orderId+"goodsId="+goodsId+"pindoId="+pindaoId+"num="+num+"limitStorage="+limitStorage+"goodsWmsId="+goodsWmsId);
			if (goodsId > 0) {
				OrderGoodsDO orderGoodsDO = new OrderGoodsDO();
				orderGoodsDO.setGoodsId(Long.valueOf(goodsId).intValue());
				orderGoodsDO.setGoodsNumber(num);
				int LimitStorageTypeEnum_Limit=1; 
				if (limitStorage == LimitStorageTypeEnum_Limit) {
						goodTypeDomainRepository.updateGoodsAttributesLeftNumber(orderGoodsDO);
					}	
					if (goodsWmsId != null
							&& goodsWmsId > 0) {
						goodTypeDomainRepository.updataGoodsWmsLeftNumByID(goodsWmsId, num);
					}
			
			}
			if (goodsSelectionList != null) {
				for (OrderGoodsSelectionModel orderGoodsSelectionModel : goodsSelectionList) {
					OrderInfoDetailDO orderInfoDetailDO = new OrderInfoDetailDO();
					orderInfoDetailDO.setGoodsId(Long.valueOf(goodsId).intValue());
					orderInfoDetailDO.setSelectionRelationId(orderGoodsSelectionModel.getSelectionRelationId());
					orderInfoDetailDO.setSuppliersId(orderGoodsSelectionModel.getSuppliersId());
					orderInfoDetailDO.setCount(orderGoodsSelectionModel.getCount().intValue());
					if (orderInfoDetailDO.getSelectionRelationId() != null
							&& orderInfoDetailDO.getSelectionRelationId() > 0) {
						goodTypeDomainRepository.updateSelectionRelationLeftNumber(orderInfoDetailDO);
					}
					if (orderInfoDetailDO.getSuppliersId() != null && orderInfoDetailDO.getSuppliersId() > 0) {
						goodTypeDomainRepository.updateSuppliersInventoryLeftNumber(orderInfoDetailDO);
					}
				}
			}
		} catch (Exception e) {
			log.error("updateInventoryNumber invoke error [orderId="+orderId+";goodsId=" + goodsId +";num="+ num+";limitStorage="+limitStorage+";goodsWmsId="+goodsWmsId+"] cause is "+e.getMessage());
			
			return false;
		}
		return true;
	}

	public GoodTypeDomainRepository getGoodTypeDomainRepository() {
		return goodTypeDomainRepository;
	}

	public void setGoodTypeDomainRepository(GoodTypeDomainRepository goodTypeDomainRepository) {
		this.goodTypeDomainRepository = goodTypeDomainRepository;
	}

}
