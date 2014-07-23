package com.tuan.inventory.domain.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.TuanRuntimeException;
import com.tuan.core.common.service.TuanCallbackResult;
import com.tuan.core.common.service.TuanServiceCallback;
import com.tuan.core.common.service.TuanServiceTemplateImpl;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.result.CallResult;
import com.tuan.inventory.model.util.QueueConstant;

public class SynInitAndAysnMysqlServiceImpl  extends TuanServiceTemplateImpl implements SynInitAndAysnMysqlService {
	private static final Log logger = LogFactory.getLog("SYSERROR.LOG");
	@Resource
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	@Resource
	private InitCacheDomainRepository initCacheDomainRepository;
	@Resource
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	@Resource
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	
	@Override
	public CallResult<Boolean> saveGoodsWmsInventory(final GoodsInventoryWMSDO wmsDO,
			final List<GoodsSelectionDO> selectionList) throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							if(wmsDO!=null) {
								synInitAndAsynUpdateDomainRepository.saveGoodsWms(wmsDO);
							}
							if (!CollectionUtils.isEmpty(selectionList)) { // if1
								synInitAndAsynUpdateDomainRepository.saveBatchGoodsWms(selectionList);
							}
							if(wmsDO!=null) {
								String retAck = goodsInventoryDomainRepository.saveGoodsWmsInventory(wmsDO);
								if(StringUtils.isEmpty(retAck)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory to redis error occured!",
											new Exception());
								}
								if(!retAck.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory to redis error occured!",
											new Exception());
								}
							}
							if (!CollectionUtils.isEmpty(selectionList)) { // if1
								String retselAck = goodsInventoryDomainRepository.saveGoodsSelectionWmsInventory(selectionList);
								if(StringUtils.isEmpty(retselAck)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsSelectionWmsInventory to redis error occured!",
											new Exception());
								}
								if(!retselAck.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsSelectionWmsInventory to redis error occured!",
											new Exception());
								}
							}
							
							
						} catch (Exception e) {
							
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + wmsDO.getWmsGoodsId()
										+ "' for key 'wmsGoodsId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								true);
					}
					public TuanCallbackResult executeCheck() {
						if (wmsDO == null&&CollectionUtils.isEmpty(selectionList)) {
							logger.error(this.getClass()+"_create param invalid ,param is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
		
	}
	
	
	@Override
	public CallResult<Boolean> saveGoodsInventory(final long goodsId,final GoodsInventoryDO inventoryInfoDO,final List<GoodsSelectionDO> selectionInventoryList,final List<GoodsSuppliersDO> suppliersInventoryList)
			throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							//操作库存基表
							Long goodsBaseId = inventoryInfoDO.getGoodsBaseId();
							GoodsInventoryDO isGoodsBaseIdIsnull = null;
							if (goodsBaseId == null && goodsId != 0) {
								// 初始化商品库存信息
								isGoodsBaseIdIsnull = initCacheDomainRepository
										.getInventoryInfoByGoodsId(goodsId);
								if (isGoodsBaseIdIsnull != null) {
									goodsBaseId = isGoodsBaseIdIsnull
											.getGoodsBaseId();
									inventoryInfoDO.setGoodsBaseId(goodsBaseId);
								}
							}
							GoodsBaseInventoryDO baseInventoryDO = null;
							GoodsBaseInventoryDO upBaseDO = null;
							if(inventoryInfoDO!=null) {
								synInitAndAsynUpdateDomainRepository.saveGoodsInventory(inventoryInfoDO);
								baseInventoryDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
								if(baseInventoryDO == null){
									//初始化基本信息:注释掉是为了兼容以后历史baseid下增加商品时销量和库存总量的统计无误
									baseInventoryDO =  synInitAndAsynUpdateDomainRepository.selectInventoryBase4Init(goodsBaseId);
									if(baseInventoryDO!=null) {//新创建的商品
										if(baseInventoryDO.getGoodsBaseId()==null) {
											baseInventoryDO = new GoodsBaseInventoryDO();
											baseInventoryDO.setGoodsBaseId(goodsBaseId);
											baseInventoryDO.setBaseTotalCount(inventoryInfoDO.getTotalNumber());
											baseInventoryDO.setBaseSaleCount(0);
										}
										synInitAndAsynUpdateDomainRepository.saveGoodsBaseInventoryDO(baseInventoryDO);
									}
									
								}else{
									//计算库存总数:
									upBaseDO = synInitAndAsynUpdateDomainRepository.selectSelfInventoryBaseInit(goodsBaseId);
									synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(upBaseDO);
								}
							}
							// 保选型库存
							if (!CollectionUtils.isEmpty(selectionInventoryList)) {
								for(GoodsSelectionDO selDO:selectionInventoryList) {  //将商品id处理到选型中
									selDO.setGoodsId(goodsId);
								}
								synInitAndAsynUpdateDomainRepository.saveAndUpdateGoodsSelection(goodsId, selectionInventoryList);
							}
							// 保存分店库存
							if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
								synInitAndAsynUpdateDomainRepository.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);	
							}
							if(inventoryInfoDO!=null) {
								String retAck = goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
										inventoryInfoDO);
								//更新库存基表
								if(!StringUtils.isEmpty(retAck)&&retAck.equalsIgnoreCase("ok")&&baseInventoryDO!=null){
									retAck =	goodsInventoryDomainRepository.saveGoodsBaseInventory(goodsBaseId, baseInventoryDO);
								}
								if(!StringUtils.isEmpty(retAck)&&retAck.equalsIgnoreCase("ok")&&upBaseDO!=null){
									retAck =	goodsInventoryDomainRepository.saveGoodsBaseInventory(goodsBaseId, upBaseDO);
								}
								
								if(StringUtils.isEmpty(retAck)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsInventory to redis error occured!",
											new Exception());
								}
								if(!retAck.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsInventory to redis error occured!",
											new Exception());
								}
							}
							
							// 保选型库存
							if (!CollectionUtils.isEmpty(selectionInventoryList)) {
								boolean selSuccess = goodsInventoryDomainRepository.saveAndUpdateGoodsSeleInventory(
										goodsId, selectionInventoryList);
								if(!selSuccess) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsSelectionInventory to redis error occured!",
											new Exception());
								}
								
								
							}
							// 保存分店库存
							if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
								boolean suppSuccess =	goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
										goodsId, suppliersInventoryList);
								if(!suppSuccess) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsSuppliersInventory to redis error occured!",
											new Exception());
								}
								
								
							}
							
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
								true);
					}
					public TuanCallbackResult executeCheck() {
						if (goodsId<=0) {
							logger.error(this.getClass()+"_create param invalid ,goodsId is invalid");
							return TuanCallbackResult
									.failure(PublicCodeEnum.INVALID_GOODSID
											.getCode());
						}
						if (inventoryInfoDO == null) {
							logger.error(this.getClass()+"_create param invalid ,inventoryInfoDO is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_GOODS
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
		
	}
	@Override
	public CallResult<Boolean> saveGoodsInventory(final long goodsId,final GoodsInventoryDO inventoryInfoDO,final List<GoodsSelectionDO> selectionInventoryList,final List<GoodsSuppliersDO> suppliersInventoryList,final GoodsInventoryWMSDO wmsInventory,final GoodsInventoryWMSDO wmsInventory4wmsGoodsId)
			throws Exception {
		
		    TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							GoodsBaseInventoryDO baseDO =  null;
							GoodsBaseInventoryDO upBaseDO =  null;
							Long baseId= null;
							if(inventoryInfoDO!=null) {
								//先检查mysql库中是否存在
								GoodsInventoryDO tmpGoodsDo = synInitAndAsynUpdateDomainRepository.selectGoodsInventoryDO(goodsId);
								if(tmpGoodsDo==null) {
									baseId=inventoryInfoDO.getGoodsBaseId();
									synInitAndAsynUpdateDomainRepository.saveGoodsInventory(inventoryInfoDO);
									//检查goodsbase信息是否存在
									if(baseId!=null&&baseId!=0) {
										GoodsBaseInventoryDO tmpDo = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(baseId);
										if(tmpDo==null) {  //常态化初上线时是1vs1的关系,故直接取
											//初始化基本信息:注释掉是为了兼容以后历史baseid下增加商品时销量和库存总量的统计无误
											baseDO = synInitAndAsynUpdateDomainRepository.selectInventoryBase4Init(baseId);
											if(baseDO!=null) {
												synInitAndAsynUpdateDomainRepository.saveGoodsBaseInventoryDO(baseDO);
											}
											
										}else {  //相同baseid 不同的商品id时
											//计算库存总数:暂时去掉
											upBaseDO = synInitAndAsynUpdateDomainRepository.selectSelfInventoryBaseInit(baseId);
											synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(upBaseDO);
										}
									}
									
									
								}
								
							}
							if (!CollectionUtils.isEmpty(selectionInventoryList)) {
								synInitAndAsynUpdateDomainRepository.saveBatchGoodsSelection(goodsId, selectionInventoryList);
							}
							// 保存分店库存
							if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
								synInitAndAsynUpdateDomainRepository.saveBatchGoodsSuppliers(goodsId, suppliersInventoryList);
							}
							
							if(wmsInventory!=null) {
								synInitAndAsynUpdateDomainRepository.saveGoodsWms( wmsInventory);
							}
							
							if(wmsInventory4wmsGoodsId!=null) {
								synInitAndAsynUpdateDomainRepository.saveGoodsWms(wmsInventory4wmsGoodsId);
							}
						if(inventoryInfoDO!=null) {
							String retAck = goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
									inventoryInfoDO);
							if(!StringUtils.isEmpty(retAck)&&retAck.equalsIgnoreCase("ok")&&baseDO!=null&&baseId!=null&&baseId!=0){
								retAck =	goodsInventoryDomainRepository.saveGoodsBaseInventory(baseId, baseDO);
							}
							if(!StringUtils.isEmpty(retAck)&&retAck.equalsIgnoreCase("ok")&&upBaseDO!=null&&baseId!=null&&baseId!=0){
								retAck =	goodsInventoryDomainRepository.saveGoodsBaseInventory(baseId, upBaseDO);
							}
							 if(StringUtils.isEmpty(retAck)) {
								 throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsInventory to redis error occured!",
											new Exception());
							 }
							if(!retAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.saveGoodsInventory to redis error occured!",
										new Exception());
							}
						}
						
						if (!CollectionUtils.isEmpty(selectionInventoryList)) {
							boolean selSuccess = goodsInventoryDomainRepository.saveGoodsSelectionInventory(
									goodsId, selectionInventoryList);
							if(!selSuccess) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.saveGoodsSelectionInventory to redis error occured!",
										new Exception());
							}
					}
						// 保存分店库存
						if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
							boolean suppSuccess = goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
										goodsId, suppliersInventoryList);
							if(!suppSuccess) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.saveGoodsSuppliersInventory to redis error occured!",
										new Exception());
							}
						
							
						}
						
						if(wmsInventory!=null) {
							 String retAck = goodsInventoryDomainRepository.saveGoodsWmsInventory(wmsInventory);
							 if(StringUtils.isEmpty(retAck)) {
								 throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory to redis error occured!",
											new Exception());
							 }
							 if(StringUtils.isNotEmpty(retAck)&&!retAck.equalsIgnoreCase("ok")) {
								 throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory to redis error occured!",
											new Exception());
							 }
						}
						  
						if(wmsInventory4wmsGoodsId!=null) {
							 String retAck = goodsInventoryDomainRepository.saveGoodsWmsInventory(wmsInventory4wmsGoodsId);
							 if(StringUtils.isEmpty(retAck)) {
								 throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory to redis error occured!",
											new Exception());
							 }
							 if(StringUtils.isNotEmpty(retAck)&&!retAck.equalsIgnoreCase("ok")) {
								 throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory to redis error occured!",
											new Exception());
							 }
						}
							 
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
								true);
					}
					public TuanCallbackResult executeCheck() {
						if (goodsId<=0) {
							logger.error(this.getClass()+"_create param invalid ,goodsId is invalid");
							return TuanCallbackResult
									.failure(PublicCodeEnum.INVALID_GOODSID
											.getCode());
						}
						if (inventoryInfoDO == null) {
							 logger.error(this.getClass()+"_create param invalid ,goodsInfo is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_GOODS
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());

	}

	@Override
	public CallResult<Boolean> updateGoodsInventory(final long goodsId,
			final GoodsInventoryDO goodsDO,
			final List<GoodsSelectionDO> selectionInventoryList,
			final List<GoodsSuppliersDO> suppliersInventoryList,final List<GoodsInventoryWMSDO> wmsInventoryList) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
						if(goodsDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(goodsDO);
							Long goodsBaseId = goodsDO.getGoodsBaseId();
							//更新base表销量:累加base表销量:从redis中取最新的base数据同步到mysql表中去
							GoodsBaseInventoryDO baseDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
							if(baseDO!=null) {
								synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(baseDO);
							}/*else {
								// 初始化商品库存信息
								baseDO = synInitAndAsynUpdateDomainRepository.selectInventoryBase4Init(goodsBaseId);
								synInitAndAsynUpdateDomainRepository.saveGoodsBaseInventoryDO(baseDO);
							}*/
						}
						if (!CollectionUtils.isEmpty(selectionInventoryList)) { // if1
							synInitAndAsynUpdateDomainRepository.updateBatchGoodsSelection(goodsId, selectionInventoryList);
						}
						if (!CollectionUtils.isEmpty(suppliersInventoryList)) { // if1
							synInitAndAsynUpdateDomainRepository.updateBatchGoodsSuppliers(goodsId, suppliersInventoryList);
						}
						if (!CollectionUtils.isEmpty(wmsInventoryList)) { // if1
							synInitAndAsynUpdateDomainRepository.batchUpdateGoodsInventoryWMS(wmsInventoryList);
						}
						
						
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory error occured!"
										+ e.getMessage(), e);
						if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
							throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
									"update invalid '" + goodsDO.getGoodsId()
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							true);
				}
				public TuanCallbackResult executeCheck() {
					if (goodsDO == null) {
						logger.error(this.getClass()+"_create param invalid ,goodsinfo is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.NO_GOODS
										.getCode());
					}
					
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(Boolean)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	/*@SuppressWarnings("unchecked")
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
*/
	/*@SuppressWarnings("unchecked")
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
*/
	@Override
	public CallResult<GoodsInventoryDO> updateGoodsInventory(final Long goodsId, final Long goodBaseId,final int adjustNum,final int limitStorage,
			final GoodsInventoryDO inventoryInfoDO) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
					synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryInfoDO);
					
					long goodsBaseId = inventoryInfoDO.getGoodsBaseId();
					if(goodsBaseId>0) {
						GoodsBaseInventoryDO	baseDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
						if(baseDO!=null) {
							//更新base表调整后的库存总量
							baseDO.setBaseTotalCount(baseDO.getBaseTotalCount()+adjustNum);
							synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(baseDO);
						}else {
							// 初始化商品库存信息
							baseDO = synInitAndAsynUpdateDomainRepository.selectInventoryBase4Init(goodsBaseId);
							baseDO.setBaseTotalCount(baseDO.getBaseTotalCount()+adjustNum);
							synInitAndAsynUpdateDomainRepository.saveGoodsBaseInventoryDO(baseDO);
						}
						
					}
					
					List<Long>	resultACK = goodsInventoryDomainRepository
							.adjustGoodsInventory(goodsId, goodBaseId,(adjustNum),
									limitStorage);
					if(!DataUtil.verifyInventory(resultACK)) {  //已更新的库存回滚
						logger.info("rollback start resultACK:"+resultACK);
						List<Long> rollbackResponeResult =	goodsInventoryDomainRepository
								.adjustGoodsInventory(goodsId,goodBaseId,
										(-adjustNum), (-limitStorage));
						logger.info("rollback end rollbackResponeResult:"+rollbackResponeResult.toString());
						throw new TuanRuntimeException(
								QueueConstant.FAIL_ADJUST_INVENTORY,
								"SynInitAndAysnMysqlServiceImpl.adjustGoodsInventory to redis error occured!",
								new Exception());
					}
					
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
								.failure(PublicCodeEnum.NO_GOODS
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
	
	@Override
	public CallResult<GoodsInventoryDO> updateGoodsInventory(final long goodsId,final int pretotalnum,final String goodsSelectionIds,
			final GoodsInventoryDO inventoryInfoDO) throws Exception {
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryInfoDO);
							//更新商品基表
							long goodsBaseId = inventoryInfoDO.getGoodsBaseId();
							GoodsBaseInventoryDO	baseDO = null;
							if(goodsBaseId>0) {
								baseDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
								//更新base表调整后的库存总量
								if(baseDO!=null) {
									baseDO.setBaseTotalCount(baseDO.getBaseTotalCount()-pretotalnum+inventoryInfoDO.getTotalNumber());
									synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(baseDO);
								}else {//支持编辑
									// 初始化商品库存信息
									baseDO = synInitAndAsynUpdateDomainRepository.selectInventoryBase4Init(goodsBaseId);
									///baseDO.setBaseTotalCount(baseDO.getBaseTotalCount()-pretotalnum+inventoryInfoDO.getTotalNumber());
									synInitAndAsynUpdateDomainRepository.saveGoodsBaseInventoryDO(baseDO);
								}
								
							}
							
							String retAck =	goodsInventoryDomainRepository.saveGoodsInventory(goodsId, inventoryInfoDO);
							//更新redis商品基表
							String baseRetAck = null;
							if(!StringUtils.isEmpty(retAck)&&retAck.equalsIgnoreCase("ok")){
								baseRetAck = goodsInventoryDomainRepository.saveGoodsBaseInventory(goodsBaseId, baseDO);
							}
							if(StringUtils.isEmpty(retAck)||StringUtils.isEmpty(baseRetAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							if(!retAck.equalsIgnoreCase("ok")||!baseRetAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}else {  //清除选型关系
									if(!StringUtils.isEmpty(goodsSelectionIds)) {
										List<Long> goodsTypeIdList = new ArrayList<Long> ();
										String[] goodsTypeIdsArray = goodsSelectionIds.split(",");
										List<String> tmpGoodsTypeIdsList = null;
										if (goodsTypeIdsArray != null && goodsTypeIdsArray.length != 0) {
											tmpGoodsTypeIdsList =  Arrays.asList(goodsTypeIdsArray);
											for(String id : tmpGoodsTypeIdsList) {
												goodsTypeIdList.add(Long.parseLong(id));
											}
											
										}
										//清除关系逻辑
										List<GoodsSelectionDO> selList = initCacheDomainRepository.selectSelectionByGoodsTypeIds(goodsTypeIdList);
										if(!CollectionUtils.isEmpty(selList)) {
											for(GoodsSelectionDO selDO:selList) {
												if(selDO.getId()!=null&&selDO.getId()>0) {
													//Long ack =
													goodsInventoryDomainRepository.clearWmsSelRelation(goodsId,
															String.valueOf(selDO.getId()));
												}
												
											}
											
										}
										
									}else {
										//查询商品所属的选型
										List<GoodsSelectionModel> selResult = goodsInventoryDomainRepository
												.queryGoodsSelectionListByGoodsId(Long.valueOf(goodsId));
										//检查商品所属选型商品
										if(!CollectionUtils.isEmpty(selResult)) {  //若商品存在选型，则为选型商品，
											for(GoodsSelectionModel selModel:selResult) {
												if(selModel.getId()!=null&&selModel.getId()>0) {
													//Long ack =
													goodsInventoryDomainRepository.clearWmsSelRelation(goodsId,
															String.valueOf(selModel.getId()));
												}
												
											}
										}
									}
								
							}
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
						if (goodsId<=0) {
							logger.error(this.getClass()+"_create param invalid , goodsId  is invalid");
							return TuanCallbackResult
									.failure(PublicCodeEnum.INVALID_GOODSID
											.getCode());
						}
						if (inventoryInfoDO == null) {
							logger.error(this.getClass()+"_create param invalid ,GoodsInventoryDO   is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_GOODS
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
	@Override
	public CallResult<GoodsInventoryDO> updateGoodsInventory(final long goodsId,final int adjustNum,
			final GoodsInventoryDO inventoryInfoDO) throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryInfoDO);
							long resultACK = goodsInventoryDomainRepository.adjustGoodsWaterflood(goodsId, (adjustNum));
							if(resultACK<0) {
								goodsInventoryDomainRepository.adjustGoodsWaterflood(goodsId, (-adjustNum));
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.adjustGoodsWaterflood to redis error occured!",
										new Exception());
							}
							
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
						if (goodsId<=0) {
							logger.error(this.getClass()+"_create param invalid , goodsId  is invalid");
							return TuanCallbackResult
									.failure(PublicCodeEnum.INVALID_GOODSID
											.getCode());
						}
						if (inventoryInfoDO == null) {
							logger.error(this.getClass()+"_create param invalid ,GoodsInventoryDO  is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_GOODS
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
	@Override
	public CallResult<GoodsInventoryDO> updateGoodsInventory(final long goodsId,final Map<String, String> hash,
			final GoodsInventoryDO inventoryInfoDO,final int pretotalnum) throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							//
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(inventoryInfoDO);
							//更新库存基表
							Long goodsBaseId = inventoryInfoDO.getGoodsBaseId();
							GoodsBaseInventoryDO baseInventoryDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
							if(baseInventoryDO!=null){
								int num=baseInventoryDO.getBaseTotalCount()-pretotalnum+inventoryInfoDO.getTotalNumber();
								baseInventoryDO.setBaseTotalCount(num);
								synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(baseInventoryDO);
							}else {
								// 初始化商品库存信息
								baseInventoryDO = synInitAndAsynUpdateDomainRepository.selectSelfInventoryBaseInit(goodsBaseId);
								int num=baseInventoryDO.getBaseTotalCount()-pretotalnum+inventoryInfoDO.getTotalNumber();
								baseInventoryDO.setBaseTotalCount(num);
								synInitAndAsynUpdateDomainRepository.saveGoodsBaseInventoryDO(baseInventoryDO);
							}
							if(!CollectionUtils.isEmpty(hash)) {
								String retAck =	 goodsInventoryDomainRepository.updateFields(goodsId, hash);
								
								if(StringUtils.isEmpty(retAck)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
											new Exception());
								}
								if(!retAck.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
											new Exception());
								}
							}else {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"collection hash  is null, error occured!",
										new Exception());
							}
							if(baseInventoryDO!=null) {
								String baseACK = goodsInventoryDomainRepository.saveGoodsBaseInventory(goodsBaseId, baseInventoryDO);
								if(StringUtils.isEmpty(baseACK)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsBaseInventory to redis error occured!",
											new Exception());
								}
								if(!baseACK.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsBaseInventory to redis error occured!",
											new Exception());
								}
							}
								
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
						if (goodsId<=0) {
							logger.error(this.getClass()+"_create param invalid ,goodsId  is invalid");
							return TuanCallbackResult
									.failure(PublicCodeEnum.INVALID_GOODSID
											.getCode());
						}
						if (inventoryInfoDO == null) {
							logger.error(this.getClass()+"_create param invalid ,GoodsInventoryDO  is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_GOODS
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
					String retAck = goodsInventoryDomainRepository.updateSelectionFields(selectionDOList);
					if(StringUtils.isEmpty(retAck)) {
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_REDIS_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateSelectionFields to redis error occured!",
								new Exception());
					}
					if(!retAck.equalsIgnoreCase("ok")) {
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_REDIS_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateSelectionFields to redis error occured!",
								new Exception());
					}
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
					if (goodsId<=0) {
						logger.error(this.getClass()+"_create param invalid ,goodsId is invalid");
						return TuanCallbackResult
								.failure(PublicCodeEnum.INVALID_GOODSID
										.getCode());
					}
					if (CollectionUtils.isEmpty(selectionDOList)) {
						 logger.error(this.getClass()+"_create param invalid ,List<GoodsSelectionDO> is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.NO_SELECTION
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

	/*@SuppressWarnings("unchecked")
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

}*/

	@Override
	public CallResult<GoodsSelectionDO> updateGoodsSelection(final GoodsInventoryDO goodsDO,final int pretotalnum,
			final GoodsSelectionDO selectionDO) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
						long goodsBaseId = goodsDO.getGoodsBaseId();
						GoodsBaseInventoryDO	baseDO = null;
						if(goodsDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(goodsDO);
							//更新商品基表
							if(goodsBaseId>0) {
								baseDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
								if(baseDO!=null) {
									//更新base表调整后的库存总量
									baseDO.setBaseTotalCount(baseDO.getBaseTotalCount()-pretotalnum+goodsDO.getTotalNumber());
									synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(baseDO);
								}
								
							}
						}
						if(selectionDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsSelection(selectionDO);
						}
						if(goodsDO!=null) {
							//补上redis的处理
							String retAck =	goodsInventoryDomainRepository.saveGoodsInventory(goodsDO.getGoodsId(), goodsDO);
							if(StringUtils.isEmpty(retAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							if(!retAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							
							//更新redis商品基表
							String baseRetAck = null;
							if(!StringUtils.isEmpty(retAck)&&retAck.equalsIgnoreCase("ok")&&baseDO!=null){
								baseRetAck = goodsInventoryDomainRepository.saveGoodsBaseInventory(goodsBaseId, baseDO);
							}
							
							if(StringUtils.isEmpty(baseRetAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							if(!baseRetAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
						}
							if (selectionDO != null) {
								boolean selRetACK = goodsInventoryDomainRepository
										.saveGoodsSelectionInventory(
												goodsDO.getGoodsId(),
												selectionDO);
								if (!selRetACK) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
											new Exception());
								}

							}
						
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
					if (goodsDO==null) {
						 logger.error(this.getClass()+"_create param invalid ,goodsDO param is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.NO_GOODS
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
	public CallResult<GoodsSuppliersDO> updateGoodsSuppliers(final GoodsInventoryDO goodsDO,
			final GoodsSuppliersDO suppliersDO) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
						if(goodsDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(goodsDO);
						}
						if(suppliersDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsSuppliers(suppliersDO);
						}
					
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

	@Override
	public CallResult<GoodsInventoryDO> selectGoodsInventoryByGoodsId(
			final long goodsId) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					GoodsInventoryDO inventoryInfoDO = null;
					try {
						inventoryInfoDO = initCacheDomainRepository.getInventoryInfoByGoodsId(goodsId);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectGoodsInventoryByGoodsId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectGoodsInventoryByGoodsId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							inventoryInfoDO);
				}
				public TuanCallbackResult executeCheck() {
					if (goodsId <= 0) {
						 logger.error(this.getClass()+"_create param invalid ,goodsId is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.INVALID_GOODSID
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
	public CallResult<List<GoodsSelectionDO>> selectGoodsSelectionListByGoodsId(
			final long goodsId) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					List<GoodsSelectionDO> selectionInventoryList = null;
					try {
						selectionInventoryList = initCacheDomainRepository.querySelectionByGoodsId(goodsId);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectGoodsSelectionListByGoodsId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectGoodsSelectionListByGoodsId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							selectionInventoryList);
				}
				public TuanCallbackResult executeCheck() {
					if (goodsId <= 0) {
						 logger.error(this.getClass()+"_create param invalid ,goodsId is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.INVALID_GOODSID
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
	public CallResult<List<GoodsSuppliersDO>> selectGoodsSuppliersListByGoodsId(
			final long goodsId) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					List<GoodsSuppliersDO> suppliersInventoryList = null;
					try {
						suppliersInventoryList = initCacheDomainRepository.selectGoodsSuppliersInventoryByGoodsId(goodsId);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectGoodsSuppliersListByGoodsId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectGoodsSuppliersListByGoodsId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							suppliersInventoryList);
				}
				public TuanCallbackResult executeCheck() {
					if (goodsId <= 0) {
						 logger.error(this.getClass()+"_create param invalid ,goodsId is invalid!");
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

	/*@Override
	public CallResult<Integer> deleteGoodsInventory(final long goodsId) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					Integer result = 0;
					try {
						result = synInitAndAsynUpdateDomainRepository.deleteGoodsInventory(goodsId);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.deleteGoodsInventory error occured!"
										+ e.getMessage(), e);
						
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.deleteGoodsInventory error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							result);
				}
				public TuanCallbackResult executeCheck() {
					if (goodsId <= 0) {
						 logger.error(this.getClass()+"_create param invalid ,goodsId is invalid");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<Integer>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(Integer)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}*/

	/*@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionDO>> deleteBatchGoodsSelection(final
			List<GoodsSelectionDO> selectionDOList) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
					synInitAndAsynUpdateDomainRepository.batchDelGoodsSelection(selectionDOList);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.deleteBatchGoodsSelection error occured!"
										+ e.getMessage(), e);
						
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.deleteBatchGoodsSelection error occured!",
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

}*/

	/*@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSuppliersDO>> deleteBatchGoodsSuppliers(final
			List<GoodsSuppliersDO> suppliersDOList) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
					synInitAndAsynUpdateDomainRepository.batchDeleteGoodsSuppliers(suppliersDOList);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.deleteBatchGoodsSuppliers error occured!"
										+ e.getMessage(), e);
						
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.deleteBatchGoodsSuppliers error occured!",
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
*/
	@Override
	public CallResult<GoodsInventoryWMSDO> selectGoodsInventoryWMSByWmsGoodsId(final
			String wmsGoodsId,final Integer isBeDelivery) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					GoodsInventoryWMSDO wmsDO = null;
					try {
						wmsDO = initCacheDomainRepository.selectGoodsInventoryWMS(wmsGoodsId,isBeDelivery);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectGoodsInventoryWMSByWmsGoodsId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + wmsGoodsId
											+ "' for key 'wmsGoodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectGoodsInventoryWMSByWmsGoodsId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							wmsDO);
				}
				public TuanCallbackResult executeCheck() {
					if (StringUtils.isEmpty(wmsGoodsId)) {
						 logger.error(this.getClass()+"_create param invalid ,wmsGoodsId is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.INVALID_WMSGOODSID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<GoodsInventoryWMSDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(GoodsInventoryWMSDO)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	@Override
	public CallResult<GoodsInventoryWMSDO> selectIsOrNotGoodsWMSByGoodsId(final
			long goodsId) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					GoodsInventoryWMSDO wmsDO = null;
					try {
						wmsDO = initCacheDomainRepository.selectIsOrNotGoodsWMS(goodsId);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectIsOrNotGoodsWMSByGoodsId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectIsOrNotGoodsWMSByGoodsId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							wmsDO);
				}
				public TuanCallbackResult executeCheck() {
					if (goodsId<=0) {
						 logger.error(this.getClass()+"_create param invalid ,goodsId is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.INVALID_GOODSID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<GoodsInventoryWMSDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(GoodsInventoryWMSDO)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}
	@Override
	public CallResult<Boolean> saveGoodsWmsInventory(final long goodsId,final GoodsInventoryWMSDO wmsDO,final List<GoodsInventoryDO> wmsInventoryList,
			final List<GoodsSelectionDO> selectionList) throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							if(wmsDO!=null) {
								synInitAndAsynUpdateDomainRepository.saveGoodsWms(wmsDO);
							}
							if(!CollectionUtils.isEmpty(wmsInventoryList)) {
								synInitAndAsynUpdateDomainRepository.saveBatchGoodsInventory(wmsInventoryList);
							}
							if(!CollectionUtils.isEmpty(selectionList)) {
								//mysql的有事务
								synInitAndAsynUpdateDomainRepository.saveBatchGoodsWms(goodsId,selectionList);
							}
							if(wmsDO!=null) {
								 String retAck = goodsInventoryDomainRepository.saveGoodsWmsInventory(wmsDO);
								 if(StringUtils.isEmpty(retAck)) {
										throw new TuanRuntimeException(
												QueueConstant.SERVICE_REDIS_FALIURE,
												"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory to redis error occured!",
												new Exception());
									}
									if(!retAck.equalsIgnoreCase("ok")) {
										throw new TuanRuntimeException(
												QueueConstant.SERVICE_REDIS_FALIURE,
												"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory to redis error occured!",
												new Exception());
									}
							}
							if(!CollectionUtils.isEmpty(wmsInventoryList)) {
								String retWms = goodsInventoryDomainRepository.saveBatchGoodsInventory(wmsInventoryList);
								if(StringUtils.isEmpty(retWms)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveBatchGoodsInventory to redis error occured!",
											new Exception());
								}
								if(!retWms.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveBatchGoodsInventory to redis error occured!",
											new Exception());
								}
							}
							if(!CollectionUtils.isEmpty(selectionList)) {
								//mysql的有事务
								String retselWms =	goodsInventoryDomainRepository.saveGoodsSelectionWmsInventory(goodsId,selectionList);	
								if(StringUtils.isEmpty(retselWms)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsSelectionWmsInventory to redis error occured!",
											new Exception());
								}
								if(!retselWms.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsSelectionWmsInventory to redis error occured!",
											new Exception());
								}
							}
							
							
						} catch (Exception e) {
							
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + wmsDO.getWmsGoodsId()
										+ "' for key 'wmsGoodsId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsInventory error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								true);
					}
					public TuanCallbackResult executeCheck() {
						if (wmsDO == null) {
							logger.error(this.getClass()+"_create param invalid ,wmsDO is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_WMS_DATA
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsSelectionDO>> selectSelectionByGoodsTypeIds(final List<Long> goodsTypeIdList) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					List<GoodsSelectionDO> selList = null;
					try {
						selList = initCacheDomainRepository.selectSelectionByGoodsTypeIds(goodsTypeIdList);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectSelectionByGoodsTypeIds error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + goodsTypeIdList
											+ "' for key 'goodsTypeIdList'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectSelectionByGoodsTypeIds error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							selList);
				}
				public TuanCallbackResult executeCheck() {
					if (CollectionUtils.isEmpty(goodsTypeIdList)) {
						 logger.error(this.getClass()+"_create param invalid ,goodsTypeIdList is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<List<GoodsSelectionDO>>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			callBackResult.getBusinessObject()==null?null:(List<GoodsSelectionDO>)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}
	
	@Override
	public CallResult<Boolean> batchUpdateGoodsWms(final GoodsInventoryWMSDO wmsDO,
			final List<GoodsInventoryDO> wmsInventoryList,
			final List<GoodsWmsSelectionResult> selectionList)
			throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							if(wmsDO!=null) {
								 synInitAndAsynUpdateDomainRepository.updateGoodsInventoryWMS(wmsDO);
							}
							if (!CollectionUtils.isEmpty(selectionList)) { // if1
								synInitAndAsynUpdateDomainRepository.updateBatchGoodsSelectionWms(selectionList);
							}
							if (!CollectionUtils.isEmpty(wmsInventoryList)) { // if1
								synInitAndAsynUpdateDomainRepository.updateBatchGoodsInventory(wmsInventoryList);
							}
							
							
						} catch (Exception e) {
							
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.batchUpdateGoodsWms error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + wmsDO.getWmsGoodsId()
										+ "' for key 'wmsGoodsId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.batchUpdateGoodsWms error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								true);
					}
					public TuanCallbackResult executeCheck() {
						if (wmsDO == null) {
							logger.error(this.getClass()+"_create param invalid ,wmsDO is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_WMS_DATA
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public CallResult<List<GoodsInventoryDO>> selectInventoryList4Wms(final
			String wmsGoodsId) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					List<GoodsInventoryDO> goodsInventoryList = null;
					try {
						goodsInventoryList = initCacheDomainRepository.selectInventory4Wms(wmsGoodsId);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectIsOrNotGoodsWMSByGoodsId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + wmsGoodsId
											+ "' for key 'wmsGoodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectIsOrNotGoodsWMSByGoodsId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							goodsInventoryList);
				}
				public TuanCallbackResult executeCheck() {
					if (StringUtils.isEmpty(wmsGoodsId)) {
						 logger.error(this.getClass()+"_create param invalid ,wmsGoodsId is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.INVALID_WMSGOODSID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	
	return new CallResult<List<GoodsInventoryDO>>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(List<GoodsInventoryDO>)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}

	
	@Override
	public CallResult<Boolean> saveGoodsSuppliers(final long goodsId,final GoodsSuppliersDO suppliersDO)
			throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							if(goodsId>0&&suppliersDO!=null) {
								 synInitAndAsynUpdateDomainRepository.saveGoodsSuppliers(suppliersDO);
								
								 boolean retAck = goodsInventoryDomainRepository.saveGoodsSuppliersInventory(goodsId, suppliersDO);
								 if(!retAck) {
									 throw new TuanRuntimeException(
												QueueConstant.SERVICE_REDIS_FALIURE,
												"SynInitAndAysnMysqlServiceImpl.saveGoodsSuppliersInventory to redis error occured!",
												new Exception());
								 }
							}
							
						} catch (Exception e) {
							
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.saveGoodsSuppliers error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + suppliersDO.getGoodsId()
										+ "' for key 'goodsId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.saveGoodsSuppliers error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								true);
					}
					public TuanCallbackResult executeCheck() {
						if (suppliersDO == null) {
							logger.error(this.getClass()+"_create param invalid ,param is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
		
	}
	@Override
	public CallResult<GoodsSelectionDO> updateGoodsSelection(
			final GoodsInventoryDO goodsDO, final GoodsSelectionDO selectionDO)
			throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
						
						if(goodsDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(goodsDO);
							
						}
						if(selectionDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsSelection(selectionDO);
						}
						/*if(goodsDO!=null) {
							//补上redis的处理
							String retAck =	goodsInventoryDomainRepository.saveGoodsInventory(goodsDO.getGoodsId(), goodsDO);
							if(StringUtils.isEmpty(retAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							if(!retAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							
						}
						if (selectionDO != null) {
								boolean selRetACK = goodsInventoryDomainRepository
										.saveGoodsSelectionInventory(
												goodsDO.getGoodsId(),
												selectionDO);
								if (!selRetACK) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
											new Exception());
								}

							}*/
						
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
								.failure(PublicCodeEnum.NO_SELECTION
										.getCode());
					}
					if (goodsDO==null) {
						logger.error(this.getClass()+"_create param invalid ,GoodsInventoryDO is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.NO_GOODS
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
	public CallResult<GoodsBaseInventoryDO> selectGoodsBaseInventory(
			final Long goodsBaseId) {
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						GoodsBaseInventoryDO goodsBaseInventoryDO = null;
						try {
							goodsBaseInventoryDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
						} catch (Exception e) {
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.selectGoodsBaseInventory error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
								throw new TuanRuntimeException(QueueConstant.NO_DATA,
										"empty entry '" + goodsBaseId
												+ "' for key 'goodsBaseId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.selectGoodsBaseInventory error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								goodsBaseInventoryDO);
					}
					public TuanCallbackResult executeCheck() {
						if (goodsBaseId==null) {
							 logger.error(this.getClass()+"_create param invalid ,goodsBaseId is invalid!");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<GoodsBaseInventoryDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(GoodsBaseInventoryDO)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());

	}
	@Override
	public CallResult<Boolean> saveGoodsBaseInventory(
			final GoodsBaseInventoryDO baseInventoryDO) throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							if(baseInventoryDO!=null) {
								Long gbaseId = baseInventoryDO.getGoodsBaseId();
								if(gbaseId!=null&&gbaseId!=0) {
									synInitAndAsynUpdateDomainRepository.saveGoodsBaseInventoryDO(baseInventoryDO);
									String breAck=goodsInventoryDomainRepository.saveGoodsBaseInventory(gbaseId, baseInventoryDO);
									if(StringUtils.isEmpty(breAck)) {
										throw new TuanRuntimeException(
												QueueConstant.SERVICE_REDIS_FALIURE,
												"SynInitAndAysnMysqlServiceImpl.saveGoodsBaseInventory to redis error occured!",
												new Exception());
									}
									if(!breAck.equalsIgnoreCase("ok")) {
										throw new TuanRuntimeException(
												QueueConstant.SERVICE_REDIS_FALIURE,
												"SynInitAndAysnMysqlServiceImpl.saveGoodsBaseInventory to redis error occured!",
												new Exception());
									}
								}
								
							}
							
						} catch (Exception e) {
							
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.saveGoodsBaseInventory error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + baseInventoryDO.getGoodsBaseId()
										+ "' for key 'goodsBaseId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.saveGoodsSuppliers error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								true);
					}
					public TuanCallbackResult executeCheck() {
						if (baseInventoryDO == null) {
							logger.error(this.getClass()+"_create param invalid ,param is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
		
	}
	@Override
	public CallResult<GoodsBaseInventoryDO> selectInventoryBase4Init(
			final Long goodsBaseId) {
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						GoodsBaseInventoryDO goodsBaseInventoryDO = null;
						try {
							goodsBaseInventoryDO = synInitAndAsynUpdateDomainRepository.selectInventoryBase4Init(goodsBaseId);
						} catch (Exception e) {
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.selectInventoryBase4Init error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
								throw new TuanRuntimeException(QueueConstant.NO_DATA,
										"empty entry '" + goodsBaseId
												+ "' for key 'goodsBaseId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.selectInventoryBase4Init error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								goodsBaseInventoryDO);
					}
					public TuanCallbackResult executeCheck() {
						if (goodsBaseId==null) {
							 logger.error(this.getClass()+"_create param invalid ,goodsBaseId is invalid!");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<GoodsBaseInventoryDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(GoodsBaseInventoryDO)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
	}
	@Override
	public CallResult<GoodsSelectionDO> updateGoodsSelection(
			final GoodsInventoryDO goodsDO, final int pretotalnum, final int adjustNum,
			final GoodsSelectionDO selectionDO) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
						long goodsBaseId = 0;
						GoodsBaseInventoryDO	baseDO = null;
						if(goodsDO!=null) {
							goodsBaseId = goodsDO.getGoodsBaseId();
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(goodsDO);
							//更新商品基表
							if(goodsBaseId>0) {
								baseDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
								if(baseDO!=null) {
									//更新base表调整后的库存总量
									baseDO.setBaseTotalCount(baseDO.getBaseTotalCount()-pretotalnum+goodsDO.getTotalNumber());
									synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(baseDO);
								}
								
							}
						}
						if(selectionDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsSelection(selectionDO);
						}
						if(goodsDO!=null) {
							//补上redis的处理
							String retAck =	goodsInventoryDomainRepository.saveGoodsInventory(goodsDO.getGoodsId(), goodsDO);
							if(StringUtils.isEmpty(retAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							if(!retAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							
							//更新redis商品基表
							String baseRetAck = null;
							if(!StringUtils.isEmpty(retAck)&&retAck.equalsIgnoreCase("ok")&&baseDO!=null&&goodsBaseId!=0){
								baseRetAck = goodsInventoryDomainRepository.saveGoodsBaseInventory(goodsBaseId, baseDO);
							}
							
							if(StringUtils.isEmpty(baseRetAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							if(!baseRetAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
						}
					   if (selectionDO != null) {
								List<Long> selRetACK = goodsInventoryDomainRepository
										.adjustSelectionInventoryById(goodsDO.getGoodsId(),
												selectionDO.getId(), (adjustNum));
								if (!DataUtil.verifyInventory(selRetACK)) {
									//将库存还原到调整前
									 goodsInventoryDomainRepository
											.adjustSelectionInventoryById(goodsDO.getGoodsId(),
													selectionDO.getId(), (-adjustNum));
									throw new TuanRuntimeException(
											QueueConstant.FAIL_ADJUST_INVENTORY,
											"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
											new Exception());
								}

							}
						
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
					if (selectionDO == null&&goodsDO==null) {
						 logger.error(this.getClass()+"_create param invalid ,all param is null");
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
			final GoodsInventoryDO goodsDO, final int pretotalnum, final int adjustNum,
			final GoodsSuppliersDO suppliersDO) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
						long goodsBaseId = 0;
						GoodsBaseInventoryDO	baseDO = null;
						if(goodsDO!=null) {
							goodsBaseId = goodsDO.getGoodsBaseId();
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(goodsDO);
							//更新商品基表
							if(goodsBaseId>0) {
								baseDO = synInitAndAsynUpdateDomainRepository.getGoodBaseBygoodsId(goodsBaseId);
								if(baseDO!=null) {
									//更新base表调整后的库存总量
									baseDO.setBaseTotalCount(baseDO.getBaseTotalCount()-pretotalnum+goodsDO.getTotalNumber());
									synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(baseDO);
								}
								
							}
						}
						if(suppliersDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsSuppliers(suppliersDO);
						}
					
						if(goodsDO!=null) {
							//补上redis的处理
							String retAck =	goodsInventoryDomainRepository.saveGoodsInventory(goodsDO.getGoodsId(), goodsDO);
							if(StringUtils.isEmpty(retAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							if(!retAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							
						}
						if(baseDO!=null&&goodsBaseId!=0) {
							//更新redis商品基表
							String	baseRetAck = goodsInventoryDomainRepository.saveGoodsBaseInventory(goodsBaseId, baseDO);
							
							
							if(StringUtils.isEmpty(baseRetAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.saveGoodsBaseInventory to redis error occured!",
										new Exception());
							}
							if(!baseRetAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.saveGoodsBaseInventory to redis error occured!",
										new Exception());
							}
						}
						if (suppliersDO != null) {
							List<Long> suppACK = goodsInventoryDomainRepository
									.adjustSuppliersInventoryById(suppliersDO.getGoodsId(),
											suppliersDO.getSuppliersId(), (adjustNum));
							if (!DataUtil.verifyInventory(suppACK)) {
								//将库存还原到调整前
								goodsInventoryDomainRepository
										.adjustSuppliersInventoryById(suppliersDO.getGoodsId(),
												suppliersDO.getSuppliersId(), (-adjustNum));
								throw new TuanRuntimeException(
										QueueConstant.FAIL_ADJUST_INVENTORY,
										"SynInitAndAysnMysqlServiceImpl.adjustSuppliersInventoryById to redis error occured!",
										new Exception());
							}

						}
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
	@Override
	public CallResult<Boolean> updateGoodsInventory(final long goodsId,
			final GoodsInventoryDO goodsDO,
			final List<GoodsSelectionDO> selectionInventoryList,
			final List<GoodsSuppliersDO> suppliersInventoryList) throws Exception {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					try {
						GoodsBaseInventoryDO baseDO = null;
						long goodsBaseId = 0;
						if(goodsDO!=null) {
							synInitAndAsynUpdateDomainRepository.updateGoodsInventory(goodsDO);
							 goodsBaseId = goodsDO.getGoodsBaseId();
							 if(goodsBaseId!=0) {
								//更新base表销量:累加base表销量:从redis中取最新的base数据同步到mysql表中去
								 baseDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
							 }
							if(baseDO!=null) {
								synInitAndAsynUpdateDomainRepository.updateGoodsBaseInventoryDO(baseDO);
							}
						}
						if (!CollectionUtils.isEmpty(selectionInventoryList)) { // if1
							synInitAndAsynUpdateDomainRepository.updateBatchGoodsSelection(goodsId, selectionInventoryList);
						}
						if (!CollectionUtils.isEmpty(suppliersInventoryList)) { // if1
							synInitAndAsynUpdateDomainRepository.updateBatchGoodsSuppliers(goodsId, suppliersInventoryList);
						}
						
						if(goodsDO!=null) {
							String retAck = goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
									goodsDO);
							//更新库存基表
							if(!StringUtils.isEmpty(retAck)&&retAck.equalsIgnoreCase("ok")&&baseDO!=null&&goodsBaseId!=0){
								retAck =	goodsInventoryDomainRepository.saveGoodsBaseInventory(goodsBaseId, baseDO);
							}
							if(StringUtils.isEmpty(retAck)) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							if(!retAck.equalsIgnoreCase("ok")) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
						}
						
						// 保选型库存
						if (!CollectionUtils.isEmpty(selectionInventoryList)) {
							boolean selSuccess = goodsInventoryDomainRepository.saveAndUpdateGoodsSeleInventory(
									goodsId, selectionInventoryList);
							if(!selSuccess) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							
							
						}
						// 保存分店库存
						if (!CollectionUtils.isEmpty(suppliersInventoryList)) {
							boolean suppSuccess =	goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
									goodsId, suppliersInventoryList);
							if(!suppSuccess) {
								throw new TuanRuntimeException(
										QueueConstant.SERVICE_REDIS_FALIURE,
										"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory to redis error occured!",
										new Exception());
							}
							
							
						}
						
						
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory error occured!"
										+ e.getMessage(), e);
						if (e instanceof IncorrectUpdateSemanticsDataAccessException) {// 更新时超出了更新的记录数等
							throw new TuanRuntimeException(QueueConstant.INCORRECT_UPDATE,
									"update invalid '" + goodsDO.getGoodsId()
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.updateGoodsInventory error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							true);
				}
				public TuanCallbackResult executeCheck() {
					if (goodsId==0||(goodsDO == null&&CollectionUtils.isEmpty(selectionInventoryList)&&CollectionUtils.isEmpty(suppliersInventoryList))) {
						 logger.error(this.getClass()+"_create param invalid ,param is null");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(Boolean)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}
	@Override
	public CallResult<GoodsInventoryWMSDO> selectSelfGoodsInventoryWMSByWmsGoodsId(
			final String wmsGoodsId) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					GoodsInventoryWMSDO wmsDO = null;
					try {
						wmsDO = synInitAndAsynUpdateDomainRepository.selectGoodsInventoryWMSDO(wmsGoodsId);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectGoodsInventoryWMSByWmsGoodsId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + wmsGoodsId
											+ "' for key 'wmsGoodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectGoodsInventoryWMSByWmsGoodsId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							wmsDO);
				}
				public TuanCallbackResult executeCheck() {
					if (StringUtils.isEmpty(wmsGoodsId)) {
						 logger.error(this.getClass()+"_create param invalid ,wmsGoodsId is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.PARAM_INVALID
										.getCode());
					}
					
					return TuanCallbackResult.success();
					
				}
			}, null);
	final int resultCode = callBackResult.getResultCode();
	return new CallResult<GoodsInventoryWMSDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
			(GoodsInventoryWMSDO)callBackResult.getBusinessObject(),
			callBackResult.getThrowable());

}


	@Override
	public CallResult<GoodsInventoryDO> selectSelfGoodsInventoryByGoodsId(
			final long goodsId) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					GoodsInventoryDO inventoryInfoDO = null;
					try {
						inventoryInfoDO = synInitAndAsynUpdateDomainRepository.selectGoodsInventoryDO(goodsId);
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectGoodsInventoryByGoodsId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + goodsId
											+ "' for key 'goodsId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectGoodsInventoryByGoodsId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							inventoryInfoDO);
				}
				public TuanCallbackResult executeCheck() {
					if (goodsId <= 0) {
						 logger.error(this.getClass()+"_create param invalid ,goodsId is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.INVALID_GOODSID
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


	@Override
	public CallResult<GoodsBaseInventoryDO> selectSelfInventoryBaseInit(
			final Long goodsBaseId) {
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					
					public TuanCallbackResult executeAction() {
						GoodsBaseInventoryDO goodsBaseInventoryDO = null;
						try {
							goodsBaseInventoryDO = synInitAndAsynUpdateDomainRepository.selectSelfInventoryBaseInit(goodsBaseId);
						} catch (Exception e) {
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.selectSelfInventoryBaseInit error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
								throw new TuanRuntimeException(QueueConstant.NO_DATA,
										"empty entry '" + goodsBaseId
												+ "' for key 'goodsBaseId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.selectSelfInventoryBaseInit error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								goodsBaseInventoryDO);
					}
					public TuanCallbackResult executeCheck() {
						if (goodsBaseId==null) {
							 logger.error(this.getClass()+"_create param invalid ,goodsBaseId is invalid!");
							return TuanCallbackResult
									.failure(PublicCodeEnum.PARAM_INVALID
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<GoodsBaseInventoryDO>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(GoodsBaseInventoryDO)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
	}


	@Override
	public CallResult<Boolean> saveGoodsWmsUpdateInventory(final long goodsId,
			final GoodsInventoryWMSDO wmsDO, final List<GoodsInventoryDO> wmsInventoryList,
			final List<GoodsSelectionDO> selectionList) throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							if(wmsDO!=null) {
								synInitAndAsynUpdateDomainRepository.saveAndUpdateGoodsWms(wmsDO);
							}
							if(!CollectionUtils.isEmpty(wmsInventoryList)) {
								synInitAndAsynUpdateDomainRepository.saveBatchGoodsInventory(wmsInventoryList);
							}
							if(!CollectionUtils.isEmpty(selectionList)) {
								//mysql的有事务
								synInitAndAsynUpdateDomainRepository.saveBatchGoodsWmsUpdate(goodsId,selectionList);
							}
							if(wmsDO!=null) {
								 String retAck = goodsInventoryDomainRepository.saveAndUpdateGoodsWmsInventory(wmsDO);
								 if(StringUtils.isEmpty(retAck)) {
										throw new TuanRuntimeException(
												QueueConstant.SERVICE_REDIS_FALIURE,
												"SynInitAndAysnMysqlServiceImpl.saveAndUpdateGoodsWmsInventory to redis error occured!",
												new Exception());
									}
									if(!retAck.equalsIgnoreCase("ok")) {
										throw new TuanRuntimeException(
												QueueConstant.SERVICE_REDIS_FALIURE,
												"SynInitAndAysnMysqlServiceImpl.saveAndUpdateGoodsWmsInventory to redis error occured!",
												new Exception());
									}
							}
							if(!CollectionUtils.isEmpty(wmsInventoryList)) {
								String retWms = goodsInventoryDomainRepository.saveBatchGoodsInventory(wmsInventoryList);
								if(StringUtils.isEmpty(retWms)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveBatchGoodsInventory to redis error occured!",
											new Exception());
								}
								if(!retWms.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveBatchGoodsInventory to redis error occured!",
											new Exception());
								}
							}
							if(!CollectionUtils.isEmpty(selectionList)) {
								//mysql的有事务
								String retselWms =	goodsInventoryDomainRepository.saveGoodsSelectionWmsUpdateInventory(goodsId,selectionList);	
								if(StringUtils.isEmpty(retselWms)) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsSelectionWmsInventory to redis error occured!",
											new Exception());
								}
								if(!retselWms.equalsIgnoreCase("ok")) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.saveGoodsSelectionWmsInventory to redis error occured!",
											new Exception());
								}
							}
							
							
						} catch (Exception e) {
							
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsUpdateInventory error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + wmsDO!=null?wmsDO.getWmsGoodsId():"wmsDO is null"
										+ "' for key 'wmsGoodsId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.saveGoodsWmsUpdateInventory error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								true);
					}
					public TuanCallbackResult executeCheck() {
						if (goodsId <=0) {
							logger.error(this.getClass()+"_create param invalid ,goodsId is invalid");
							return TuanCallbackResult
									.failure(PublicCodeEnum.INVALID_GOODSID
											.getCode());
						}
						if (wmsDO == null&&CollectionUtils.isEmpty(wmsInventoryList)&&CollectionUtils.isEmpty(selectionList)) {
							logger.error(this.getClass()+"_create param invalid ,wmsDO is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_WMS_DATA
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
		
	}


	@Override
	public CallResult<GoodsSelectionDO> selectGoodsSelectionBySelId(
			final Long selId) {
		
	    TuanCallbackResult callBackResult = super.execute(
			new TuanServiceCallback() {
				public TuanCallbackResult executeAction() {
					GoodsSelectionDO selectionInventory = null;
					try {
						selectionInventory = initCacheDomainRepository.getCacheSelectionRelationInfoById(selId.intValue());
					} catch (Exception e) {
						logger.error(
								"SynInitAndAysnMysqlServiceImpl.selectGoodsSelectionBySelId error occured!"
										+ e.getMessage(), e);
						if (e instanceof DataRetrievalFailureException) {// 获取数据失败，如找不到对应主键的数据，使用了错误的列索引等
							throw new TuanRuntimeException(QueueConstant.NO_DATA,
									"empty entry '" + selId
											+ "' for key 'selId'", e);
						}
						throw new TuanRuntimeException(
								QueueConstant.SERVICE_DATABASE_FALIURE,
								"SynInitAndAysnMysqlServiceImpl.selectGoodsSelectionBySelId error occured!",
								e);
						
					}
					return TuanCallbackResult.success(
							PublicCodeEnum.SUCCESS.getCode(),
							selectionInventory);
				}
				public TuanCallbackResult executeCheck() {
					if (selId <= 0) {
						 logger.error(this.getClass()+"_create param invalid ,selId is invalid!");
						return TuanCallbackResult
								.failure(PublicCodeEnum.INVALID_SELECTIONID
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
	public CallResult<Boolean> updateGoodsWmsSel(
			final long goodsId,final GoodsSelectionDO selection) throws Exception {
		
		TuanCallbackResult callBackResult = super.execute(
				new TuanServiceCallback() {
					public TuanCallbackResult executeAction() {
						try {
							
							if(selection!=null) {
								//mysql的有事务
								synInitAndAsynUpdateDomainRepository.updateGoodsSelection(selection);
								boolean retselWms =	goodsInventoryDomainRepository.saveGoodsSelectionInventory(goodsId,selection);	
								if(!retselWms) {
									throw new TuanRuntimeException(
											QueueConstant.SERVICE_REDIS_FALIURE,
											"SynInitAndAysnMysqlServiceImpl.updateGoodsWmsSel to redis error occured!",
											new Exception());
								}
								
							}
							
						} catch (Exception e) {
							
							logger.error(
									"SynInitAndAysnMysqlServiceImpl.updateGoodsWmsSel error occured!"
											+ e.getMessage(), e);
							if (e instanceof DataIntegrityViolationException) {// 消息数据重复
								throw new TuanRuntimeException(QueueConstant.DATA_EXISTED,
										"Duplicate entry '" + selection.getId()
										+ "' for key 'selId'", e);
							}
							throw new TuanRuntimeException(
									QueueConstant.SERVICE_DATABASE_FALIURE,
									"SynInitAndAysnMysqlServiceImpl.updateGoodsWmsSel error occured!",
									e);
							
						}
						return TuanCallbackResult.success(
								PublicCodeEnum.SUCCESS.getCode(),
								true);
					}
					public TuanCallbackResult executeCheck() {
						
						if (selection == null) {
							logger.error(this.getClass()+"_create param invalid ,selection is null");
							return TuanCallbackResult
									.failure(PublicCodeEnum.NO_SELECTION
											.getCode());
						}
						
						return TuanCallbackResult.success();
						
					}
				}, null);
		final int resultCode = callBackResult.getResultCode();
		return new CallResult<Boolean>(callBackResult.isSuccess(),PublicCodeEnum.valuesOf(resultCode),
				(Boolean)callBackResult.getBusinessObject(),
				callBackResult.getThrowable());
		
	}
}
