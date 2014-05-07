package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
/**
 * 校验 选型分店商品
 * @author henry.yu
 * @date 2014/5/7
 */
public class GoodsVerificationDomain {
	
	
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	
	private Long goodsId;
	private String type;
	private String id;
	private List<GoodsSelectionModel> getGoodsSelection;
	private List<GoodsSuppliersModel> getGoodsSuppliers;
	
	//是否选型商品
	private boolean isSelection;
	// 是否分店商品
	private boolean isSupplier;
	
	public GoodsVerificationDomain(long goodsId,List<GoodsSelectionModel> getGoodsSelection,List<GoodsSuppliersModel> getGoodsSuppliers) {
		this.goodsId = goodsId;
		this.getGoodsSelection = getGoodsSelection;
		this.getGoodsSuppliers = getGoodsSuppliers;
		
	}
	public GoodsVerificationDomain(long goodsId,String type,String id) {
		this.goodsId = goodsId;
		this.id = id;
		this.type = type;
		
	}

	/**
	 * 参数检查
	 * 
	 * @return
	 */
	public CreateInventoryResultEnum checkParam() {
		
		//查询商品所属的选型
		List<GoodsSelectionModel> selResult = goodsInventoryDomainRepository
				.queryGoodsSelectionListByGoodsId(goodsId);
		//检查商品所属选型商品
		if(!CollectionUtils.isEmpty(selResult)) {  //若商品存在选型，则为选型商品，
			isSelection = true;
		}
		//查询商品所属的分店
		List<GoodsSuppliersModel> suppResult = goodsInventoryDomainRepository
						.queryGoodsSuppliersListByGoodsId(goodsId);
		//这个逻辑比较清晰，首先若存在选型或分店的商品，则该商品所传参数中，选型或分店参数不能为空，否则校验不通过
		//检查商品所属分店商品
		if(!CollectionUtils.isEmpty(suppResult)) { //若商品存在分店，则为分店商品，
					isSupplier = true;
				}
		if(isSelection&&!isSupplier) {  //只包含选型的
			if (CollectionUtils.isEmpty(getGoodsSelection)) {
				return CreateInventoryResultEnum.SELECTION_GOODS;
			}
		}
		if(isSupplier&&!isSelection) {  //只包含分店的
			if (CollectionUtils.isEmpty(getGoodsSuppliers)) {
				return CreateInventoryResultEnum.SUPPLIERS_GOODS;
			}
		}
		
		if(isSupplier&&isSelection) {  //分店选型都有的
			if (CollectionUtils.isEmpty(getGoodsSuppliers)&&CollectionUtils.isEmpty(getGoodsSelection)) {
				return CreateInventoryResultEnum.SEL_SUPP_GOODS;
			}
		}
		//校验商品选型id
		if (!CollectionUtils.isEmpty(getGoodsSelection)) {
			
			List<Long> selectionIdlist = null;
			
			if(!CollectionUtils.isEmpty(selResult)) {
				selectionIdlist = new ArrayList<Long>();
				 for(GoodsSelectionModel model : selResult) {
					 selectionIdlist.add(model.getId());
				 }
			}
			if(!CollectionUtils.isEmpty(selectionIdlist)) {
				for(GoodsSelectionModel gsmdoel : getGoodsSelection) {
					if(!selectionIdlist.contains(gsmdoel.getId())||gsmdoel.getId()<=0) {
						return CreateInventoryResultEnum.INVALID_SELECTIONID;
					}
					if(gsmdoel.getNum()<0) {
						return CreateInventoryResultEnum.INVALID_SELECTIONNUM;
					}
				}
			}
			
		}
		
		//校验商品分店id，若存在的话
		if (!CollectionUtils.isEmpty(getGoodsSuppliers)) {
			List<Long> suppliersIdlist = null;
			
			if(!CollectionUtils.isEmpty(suppResult)) {
				 suppliersIdlist = new ArrayList<Long>();
				 for(GoodsSuppliersModel model : suppResult) {
					 suppliersIdlist.add(model.getSuppliersId());
				 }
			}
			if(!CollectionUtils.isEmpty(suppliersIdlist)) {
				for(GoodsSuppliersModel smdoel : getGoodsSuppliers) {
					if(!suppliersIdlist.contains(smdoel.getSuppliersId())||smdoel.getSuppliersId()<=0) {
						return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
					}
					if(smdoel.getNum()<0) {
						return CreateInventoryResultEnum.INVALID_SUPPLIERSNUM;
					}
				}
			}
			
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	//校验商品选型 分店
	public CreateInventoryResultEnum checkSelOrSupp() {
		
		//查询商品所属的选型
		List<GoodsSelectionModel> selResult = goodsInventoryDomainRepository
				.queryGoodsSelectionListByGoodsId(goodsId);
		//检查商品所属选型商品
		if(!CollectionUtils.isEmpty(selResult)) {  //若商品存在选型，则为选型商品，
			isSelection = true;
		}
		//查询商品所属的分店
		List<GoodsSuppliersModel> suppResult = goodsInventoryDomainRepository
				.queryGoodsSuppliersListByGoodsId(goodsId);
		//这个逻辑比较清晰，首先若存在选型或分店的商品，则该商品所传参数中，选型或分店参数不能为空，否则校验不通过
		//检查商品所属分店商品
		if(!CollectionUtils.isEmpty(suppResult)) { //若商品存在分店，则为分店商品，
			isSupplier = true;
		}
		if(isSelection&&!isSupplier) {  //只包含选型的
			//校验类型是否合法
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				if (StringUtils.isEmpty(id)) {
					return CreateInventoryResultEnum.INVALID_SELECTIONID;
				}else {
					if(Long.valueOf(id)<=0) {
						return CreateInventoryResultEnum.INVALID_SELECTIONID;
					}
				}
			}else {
				return CreateInventoryResultEnum.INVALID_TYPE;
			}
		}
		if(isSupplier&&!isSelection) {  //只包含分店的
			//校验类型是否合法
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				if (StringUtils.isEmpty(id)) {
					return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
				}else {
					if(Long.valueOf(id)<=0) {
						return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
					}
				}
			}else {
				return CreateInventoryResultEnum.INVALID_TYPE;
			}
		}
		
		if(isSupplier&&isSelection) {  //分店选型都有的
			//校验类型是否合法
			if (!type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())&&!type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				return CreateInventoryResultEnum.INVALID_TYPE;
			}else if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				if (StringUtils.isEmpty(id)) {
					return CreateInventoryResultEnum.INVALID_SELECTIONID;
				}else {
					if(Long.valueOf(id)<=0) {
						return CreateInventoryResultEnum.INVALID_SELECTIONID;
					}
				}
			}else if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				if (StringUtils.isEmpty(id)) {
					return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
				}else {
					if(Long.valueOf(id)<=0) {
						return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
					}
				}
			}
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	

}
