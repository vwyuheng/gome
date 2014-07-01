package com.tuan.inventory.domain;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.dao.data.GoodsUpdateNumberDO;
import com.tuan.inventory.domain.repository.GoodUpdateNumberDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.SelectionNotifyMessageParam;
import com.tuan.inventory.model.param.SuppliersNotifyMessageParam;

public class InventoryUpdateByNotifyMessageDomain extends AbstractDomain {
	protected static Log logSysDeduct = LogFactory.getLog("INVENTORY.DEDUCT.LOG");
	private LogModel lm;
	private InventoryNotifyMessageParam param;
	private final String method = "InventoryUpdateCallBackServiceImpl.receive";
	private final Log log=LogFactory.getLog("COMPENSATION.NOTIFY.LOG");
	public InventoryUpdateByNotifyMessageDomain(InventoryNotifyMessageParam param, LogModel lm) {
		this.param = param;
		this.lm = lm;
	}
	
	private GoodUpdateNumberDomainRepository goodUpdateNumberDomainRepository;
	public GoodUpdateNumberDomainRepository getGoodUpdateNumberDomainRepository() {
		return goodUpdateNumberDomainRepository;
	}
	public void setGoodUpdateNumberDomainRepository(
			GoodUpdateNumberDomainRepository goodUpdateNumberDomainRepository) {
		this.goodUpdateNumberDomainRepository = goodUpdateNumberDomainRepository;
	}

	public TuanCallbackResult reWrite(){
		        //库存调整
				int totalNumber = param.getTotalNumber();
				int leftNumber = param.getLeftNumber();
				int limitStorage = param.getLimitStorage();
				int goodsSaleCount = StringUtils.isEmpty(param.getSales())?Integer.parseInt("0"):Integer.parseInt(param.getSales());  //商品销量
				GoodsUpdateNumberDO goodsUpdateNumberDO=new GoodsUpdateNumberDO();
				goodsUpdateNumberDO.setLeftNum(leftNumber);
				goodsUpdateNumberDO.setTotalNum(totalNumber);
				goodsUpdateNumberDO.setLimitStorage(limitStorage);
				goodsUpdateNumberDO.setId(param.getGoodsId());
				//更新attribute表
				goodUpdateNumberDomainRepository.updateGoodsAttributesNumber(goodsUpdateNumberDO);
				goodsUpdateNumberDO.setGoodsSaleCount(goodsSaleCount);
				//goodsUpdateNumberDO.setSaleCount(saleCount);
				//更新jeehe_goods表销量,只需根据商品id即可，因更新的是商品的销量
				goodUpdateNumberDomainRepository.updataGoodsNum(goodsUpdateNumberDO);
				
				log.info(lm.setMethod(method).addMetaData("updatetraget", "GoodsAttributes").toJson());
				List<SelectionNotifyMessageParam> selectionRelation =param.getSelectionRelation();
				if(!CollectionUtils.isEmpty(selectionRelation)){
					for (SelectionNotifyMessageParam selectionNotifyMessageParam : selectionRelation) {
						goodsUpdateNumberDO.setLeftNum(selectionNotifyMessageParam.getLeftNumber());
						goodsUpdateNumberDO.setTotalNum(selectionNotifyMessageParam.getTotalNumber());
						goodsUpdateNumberDO.setLimitStorage(selectionNotifyMessageParam.getLimitStorage());
						goodsUpdateNumberDO.setId(selectionNotifyMessageParam.getId());
						goodUpdateNumberDomainRepository.updateSelectionRelationNumber(goodsUpdateNumberDO);
						String wmsGoodsId=selectionNotifyMessageParam.getWmsGoodsId();
						if(!StringUtils.isEmpty(wmsGoodsId)){
							goodsUpdateNumberDO.setWmsGoodsId(wmsGoodsId);
							goodUpdateNumberDomainRepository.updataGoodsWmsNumByID(goodsUpdateNumberDO);
						}
					}
					log.info(lm.setMethod(method).addMetaData("updatetraget", "SelectionRelation")
							.addMetaData("size",selectionRelation.size()).toJson());
				}
				List<SuppliersNotifyMessageParam> suppliersRelation =param.getSuppliersRelation();
				if(!CollectionUtils.isEmpty(suppliersRelation)){
					for (SuppliersNotifyMessageParam suppliersNotifyMessageParam : suppliersRelation) {
						goodsUpdateNumberDO.setLeftNum(suppliersNotifyMessageParam.getLeftNumber());
						goodsUpdateNumberDO.setTotalNum(suppliersNotifyMessageParam.getTotalNumber());
						goodsUpdateNumberDO.setLimitStorage(suppliersNotifyMessageParam.getLimitStorage());
						goodsUpdateNumberDO.setId(suppliersNotifyMessageParam.getId());
						goodUpdateNumberDomainRepository.updateSuppliersInventoryNumber(goodsUpdateNumberDO);
					}
					log.info(lm.setMethod(method).addMetaData("updatetraget", "Suppliers")
							.addMetaData("size",suppliersRelation.size()).toJson());
				}
				return TuanCallbackResult.success();
	}
}
