package com.tuan.inventory.service.impl;

import java.util.List;

import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.inventory.domain.repository.GoodTypeDomainRepository;
import com.tuan.inventory.domain.repository.GoodTypeService;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.model.GoodsSelectionRelationModel;
import com.tuan.inventory.model.enu.res.InventoryQueryEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.result.InventoryQueryResult;
import com.tuan.inventory.service.GoodSeletionQueryService;
import com.tuan.inventory.service.OrderServiceCallback;

/**
 * @ClassName: OrderQueryServiceImpl
 * @Description: ������ѯ�ӿ�ʵ���࣬ʵ�ֶ�����ѯģ���ṩ�Ĳ����ӿ� ��������������ѯ����Ʒ�����б��ѯ���û��������ܲ�ѯ���û������б��ѯ
 * @author wanghongwei
 * @date 2011-12-20
 */
public class GoodSeletionQueryServiceImpl extends AbstractService implements GoodSeletionQueryService {


	private GoodTypeDomainRepository goodTypeDomainRepository;
	
	public GoodTypeDomainRepository getGoodTypeDomainRepository() {
		return goodTypeDomainRepository;
	}

	public void setGoodTypeDomainRepository(
			GoodTypeDomainRepository goodTypeDomainRepository) {
		this.goodTypeDomainRepository = goodTypeDomainRepository;
	}
	
	private GoodTypeService goodTypeService;
	
	public GoodTypeService getGoodTypeService() {
		return goodTypeService;
	}
	public void setGoodTypeService(GoodTypeService goodTypeService) {
		this.goodTypeService = goodTypeService;
	}
		
	
	@Override
	public CallResult<InventoryQueryResult> querySelectionRelation(final String clientIp,
			final String clientName,final long goodsId,final long SelectionRelationId) {
		// ��ȡ�ӿ�����
		//final String interfaceMehtodName = ClientNameEnum.getCurrentMethodName(
				//Thread.currentThread().getStackTrace(),
			//	getClass().getInterfaces()[0].getName());
		final LogModel lm = LogModel.newLogModel("GoodSeletionQueryService.querySelectionRelation");
		lm.addMetaData("clientName",clientName).addMetaData("goodsId", goodsId).addMetaData("SelectionRelationId", SelectionRelationId);
		//InventoryQueryResult res = null;
		//final GoodTypeService goodtypeService = goodTypeDomainRepository.createGoodTypeService(goodTypeDomainRepository);
		TuanCallbackResult result =  this.busiServiceTemplate.execute(
				new OrderServiceCallback() {
					
					@Override
					public TuanCallbackResult executeParamsCheck() {
						InventoryQueryEnum enumRes = null;
						if (goodsId <= 0 ) {
							enumRes = InventoryQueryEnum.INVALID_GOODSID;
						}
						// �����ִ���
						if (enumRes != null) {
							return TuanCallbackResult.failure(
									enumRes.getCode(), null,
									new InventoryQueryResult(enumRes, null));
						}
						return TuanCallbackResult.success();
					}
					
					@Override
					public TuanCallbackResult executeBusiCheck() {
						return TuanCallbackResult.success();
					}
					
					@Override
					public TuanCallbackResult executeAction() {
						InventoryQueryResult res = null;
						List<GoodsSelectionRelationModel> listModel = 
								goodTypeService.getSelectionRelationBySrIdAndGoodsId(SelectionRelationId, goodsId);
						if (listModel != null) {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SUCCESS, listModel);
							return TuanCallbackResult.success(res.getResult()
									.getCode(), res);
						} else {
							res = new InventoryQueryResult(
									InventoryQueryEnum.SYS_ERROR, null);
							return TuanCallbackResult.failure(res.getResult()
									.getCode(), null, res);
						}
						//return TuanCallbackResult.failure(res.getResult().getCode(), null, res);
					}
					@Override
					public void executeAfter() {
						
					}
				}
				
				);
		writeBusLog(lm.addMetaData("result", result.isSuccess()).addMetaData("resultCode", result.getResultCode()).toJson(false));
		writeSysLog(lm.toJson());
		return new CallResult<InventoryQueryResult>(result.isSuccess(),
				(InventoryQueryResult) result.getBusinessObject(),
				result.getThrowable());
	}



}
