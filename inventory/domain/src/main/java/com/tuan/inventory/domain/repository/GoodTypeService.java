package com.tuan.inventory.domain.repository;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuan.goods.model.CommonGoodsAttribute;
import com.tuan.goods.model.enu.LimitStorageTypeEnum;
import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.OrderGoodsDO;
import com.tuan.inventory.dao.data.OrderInfoDetailDO;
import com.tuan.ordercenter.model.OrderGoodsSelectionModel;
import com.tuan.ordercenter.model.enu.res.OrderCreatorEnum;


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

	public boolean updateInventoryNumber(long goodsId, int pindaoId, int num,
			List<OrderGoodsSelectionModel> goodsSelectionList,CommonGoodsAttribute goodsAttribute) {
		try {
			log.info("goodsId="+goodsId+"pindoId="+pindaoId+"num="+num+"goodsAttribute="+goodsAttribute);
			if (goodsId > 0) {
				OrderGoodsDO orderGoodsDO = new OrderGoodsDO();
				orderGoodsDO.setGoodsId(Long.valueOf(goodsId).intValue());
				orderGoodsDO.setGoodsNumber(num);
				 if (goodsAttribute == null) {
					    log.error("goods attribute error "+goodsAttribute);
						return false;
					}else{
					if (goodsAttribute.getLimitStorage() == LimitStorageTypeEnum.Limit) {
						goodTypeDomainRepository.updateGoodsAttributesLeftNumber(orderGoodsDO);
					}
	
					if (goodsAttribute.getGoodsWmsId() != null
							&& goodsAttribute.getGoodsWmsId() > 0) {
						goodTypeDomainRepository.updataGoodsWmsLeftNumByID(goodsAttribute.getGoodsWmsId(), num);
					}
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
			log.error("updateInventoryNumber invoke error [goodsId=" + goodsId + "]", e);
			
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
