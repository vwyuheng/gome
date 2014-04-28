package com.tuan.inventory.domain.repository;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.TuanRuntimeException;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.core.common.service.TuanServiceTemplateImpl;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.util.QueueConstant;

public class SynInitAndAysnMysqlServiceImpl  extends TuanServiceTemplateImpl implements SynInitAndAysnMysqlService {
	private static final Log logger = LogFactory.getLog(SynInitAndAysnMysqlServiceImpl.class);
	@Resource
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	
	@Override
	public CallResult<GoodsInventoryDO> saveGoodsInventory(final GoodsInventoryDO inventoryInfoDO)
			throws Exception {
		
		    TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
						synInitAndAsynUpdateDomainRepository.saveGoodsInventory(inventoryInfoDO);
						} catch (Exception e) {
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.saveGoodsInventory error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + inventoryInfoDO.getGoodsId()
												+ "' for key 'goodsId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.saveGoodsInventory error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								inventoryInfoDO);
					}
					public TuanCallbackResult executeCheck() {
						if (inventoryInfoDO == null) {
							 logger.error(this.getClass()+"_create param invalid ,GoodsInventoryDO is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<GoodsInventoryDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(GoodsInventoryDO)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());

	}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionDO>> saveBatchGoodsSelection(
			final long goodsId, final List<GoodsSelectionDO> selectionInventoryList)
			throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					
					try {
						synInitAndAsynUpdateDomainRepository
								.saveBatchGoodsSelection(goodsId,
										selectionInventoryList);
					}  catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.saveBatchGoodsSelection error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataIntegrityViolationException) {// 消息数据重复
							throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
									"Duplicate entry '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.saveBatchGoodsSelection error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							selectionInventoryList);
				}
				public TuanCallbackResult executeCheck() {
					if (CollectionUtils.isEmpty(selectionInventoryList)) {
						 logger.error(this.getClass()+"_create param invalid ,List<GoodsSelectionDO> is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<List<GoodsSelectionDO>>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(List<GoodsSelectionDO>)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSuppliersDO>> saveBatchGoodsSuppliers(
			final long goodsId, final List<GoodsSuppliersDO> suppliersInventoryList)
			throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
						synInitAndAsynUpdateDomainRepository.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.saveBatchGoodsSuppliers error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataIntegrityViolationException) {// 消息数据重复
							throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
									"Duplicate entry '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.saveBatchGoodsSuppliers error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							suppliersInventoryList);
				}
				public TuanCallbackResult executeCheck() {
					if (CollectionUtils.isEmpty(suppliersInventoryList)) {
						 logger.error(this.getClass()+"_create param invalid ,List<GoodsSuppliersDO> is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<List<GoodsSuppliersDO>>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(List<GoodsSuppliersDO>)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	@Override
	public CallResult<GoodsInventoryDO> updateGoodsInventory(
			final GoodsInventoryDO inventoryInfoDO) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
					synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryInfoDO);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory error occured!"
										+ e.getMessage(), e);
						if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
							throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
									"update invalid '" + inventoryInfoDO.getGoodsId()
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							inventoryInfoDO);
				}
				public TuanCallbackResult executeCheck() {
					if (inventoryInfoDO == null) {
						 logger.error(this.getClass()+"_create param invalid ,GoodsInventoryDO is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<GoodsInventoryDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(GoodsInventoryDO)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionDO>> updateBatchGoodsSelection(
			final Long goodsId, final List<GoodsSelectionDO> selectionDOList)
			throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
					synInitAndAsynUpdateDomainRepository.updateBatchGoodsSelection(goodsId, selectionDOList);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.updateBatchGoodsSelection error occured!"
										+ e.getMessage(), e);
						if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
							throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
									"update invalid '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateBatchGoodsSelection error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							selectionDOList);
				}
				public TuanCallbackResult executeCheck() {
					if (CollectionUtils.isEmpty(selectionDOList)) {
						 logger.error(this.getClass()+"_create param invalid ,List<GoodsSelectionDO> is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<List<GoodsSelectionDO>>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(List<GoodsSelectionDO>)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSuppliersDO>> updateBatchGoodsSuppliers(
			final Long goodsId, final List<GoodsSuppliersDO> suppliersDOList)
			throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
					synInitAndAsynUpdateDomainRepository.updateBatchGoodsSuppliers(goodsId, suppliersDOList);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.updateBatchGoodsSuppliers error occured!"
										+ e.getMessage(), e);
						if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
							throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
									"update invalid '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateBatchGoodsSuppliers error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							suppliersDOList);
				}
				public TuanCallbackResult executeCheck() {
					if (CollectionUtils.isEmpty(suppliersDOList)) {
						 logger.error(this.getClass()+"_create param invalid ,List<GoodsSuppliersDO>  is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<List<GoodsSuppliersDO>>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(List<GoodsSuppliersDO>)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	@Override
	public CallResult<GoodsSelectionDO> updateGoodsSelection(
			final GoodsSelectionDO selectionDO) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
					synInitAndAsynUpdateDomainRepository.updateGoodsSelection(selectionDO);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.updateGoodsSelection error occured!"
										+ e.getMessage(), e);
						if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
							throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
									"update invalid '" + selectionDO.getId()
											+ "' for key 'selectionId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateGoodsSelection error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							selectionDO);
				}
				public TuanCallbackResult executeCheck() {
					if (selectionDO == null) {
						 logger.error(this.getClass()+"_create param invalid ,GoodsSelectionDO is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<GoodsSelectionDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(GoodsSelectionDO)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	@Override
	public CallResult<GoodsSuppliersDO> updateGoodsSuppliers(
			final GoodsSuppliersDO suppliersDO) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
					synInitAndAsynUpdateDomainRepository.updateGoodsSuppliers(suppliersDO);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.updateGoodsSuppliers error occured!"
										+ e.getMessage(), e);
						if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
							throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
									"update invalid '" + suppliersDO.getId()
											+ "' for key 'suppliersId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateGoodsSuppliers error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							suppliersDO);
				}
				public TuanCallbackResult executeCheck() {
					if (suppliersDO == null) {
						 logger.error(this.getClass()+"_create param invalid ,GoodsSuppliersDO is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<GoodsSuppliersDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(GoodsSuppliersDO)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	
	
	
}
