package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.core.common.lock.eum.LockResultCodeEnum;
import com.tuan.core.common.lock.impl.DLockImpl;
import com.tuan.core.common.lock.res.LockResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.UpdateWmsDataParam;
import com.tuan.inventory.model.result.CallResult;

public class InventoryWmsDataUpdateDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private UpdateWmsDataParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	//private InitCacheDomainRepository initCacheDomainRepository;
	private DLockImpl dLock;//分布式锁
	private GoodsInventoryActionDO updateActionDO;
	private String tokenid;  //redis序列,解决接口幂等问题
	private String wmsGoodsId;  //物流编码
	private GoodsInventoryWMSDO wmsDO;
	private GoodsInventoryDO inventoryInfoDO;
	private GoodsSuppliersDO suppliersDO;
	private Long goodsId;
	private Long suppliersId;
	private String isBeDelivery;
	private long premaryKey4Suppliers;  //分店主键
	//private String goodsSelectionIds = null;  //共享库存时所绑定的选型表ID集合，以逗号分隔开；如：1,2,3或2,即选型表主键
	private String goodsTypeIds = null;  //共享库存时所绑定的选型表ID集合，以逗号分隔开；如：1,2,3或2,即选型表主键
	private List<GoodsSelectionDO> selectionDOList ;//共享库存时所绑定的选型表ID集合，以逗号分隔开；如：1,2,3或2:两种表示形式
	private SequenceUtil sequenceUtil;
	private boolean idemptent = false;
	
	//private List<Long> selIds;  //选型id
	private List<Long> goodsTypeIdList;  //选型类型id
	
	public InventoryWmsDataUpdateDomain(String clientIp, String clientName,
			UpdateWmsDataParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	/**
	 * 处理物流信息
	 */
	private void wmsDataHandler() {
		try {
		if (!StringUtils.isEmpty(param.getTokenid())) { // if
				this.tokenid = param.getTokenid();
				this.idemptent = idemptent();
				if(idemptent) {
					return ;
				}
			}
		
		if (!StringUtils.isEmpty(wmsGoodsId)) { // if1
			//wmsGoodsId = param.getWmsGoodsId();
			// 再次查询物流商品库存信息[确保最新数据]
			this.wmsDO = this.goodsInventoryDomainRepository.queryGoodsInventoryWms(wmsGoodsId);
			
			if(wmsDO!=null) {
				//转换
				if(wmsDO.getIsBeDelivery()!=Integer.valueOf(isBeDelivery)) { //过滤
					this.wmsDO = null;
				}	

			}
			

		} // if 
		if (!StringUtils.isEmpty(param.getGoodsId())) { // if1
			goodsId = Long.valueOf(StringUtils.isEmpty(param.getGoodsId())?"0":param.getGoodsId());
			if(goodsId!=0) {
				this.inventoryInfoDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
			}
			
		} // if 
		
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"wmsGoodsHandler error" + e.getMessage()),false, e);
			
		}
		
	}
	
	public boolean idemptent() {
		//根据key取已缓存的tokenid  
		String gettokenid = goodsInventoryDomainRepository.queryToken(DLockConstants.UPDATE_WMS_DATA + "_"+ String.valueOf(goodsId));
		if(StringUtils.isEmpty(gettokenid)) {  //如果为空则任务是初始的http请求过来，将tokenid缓存起来
			if(StringUtils.isNotEmpty(tokenid)) {
				goodsInventoryDomainRepository.setTag(DLockConstants.UPDATE_WMS_DATA + "_"+ goodsId, DLockConstants.IDEMPOTENT_DURATION_TIME, tokenid);
			}
					
		}else {  //否则比对token值
			if(StringUtils.isNotEmpty(tokenid)) {
				if(tokenid.equalsIgnoreCase(gettokenid)) { //重复请求过来，判断是否处理成功
				//根据处理成功后设置的tag来判断之前http请求处理是否成功
				String gettag = goodsInventoryDomainRepository.queryToken(DLockConstants.UPDATE_WMS_DATA_SUCCESS + "_"+ tokenid);
				if(!StringUtils.isEmpty(gettag)&&gettag.equalsIgnoreCase(DLockConstants.HANDLER_SUCCESS)) { 
								return true;
							}
						}
					}
		}
		return false;
	}
	
	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		if(idemptent) {  //幂等控制，已处理成功
			return CreateInventoryResultEnum.SUCCESS;
		}
		
		if(!StringUtils.isEmpty(param.getGoodsTypeIds())) {
			goodsTypeIds = param.getGoodsTypeIds();	
		}
		if (!StringUtils.isEmpty(param.getWmsGoodsId())) {
			wmsGoodsId = param.getWmsGoodsId();
		}
		if (!StringUtils.isEmpty(param.getIsBeDelivery())) {
			isBeDelivery = param.getIsBeDelivery();
		}
		/*if (!StringUtils.isEmpty(param.getGoodsSelectionIds())) {
			selIds  = new ArrayList<Long> ();
			String[] goodsSelIdsArray = param.getGoodsSelectionIds().split(",");
			List<String> tmpgoodsSelIdsList = null;
			if (goodsSelIdsArray != null && goodsSelIdsArray.length != 0) {
				tmpgoodsSelIdsList =  Arrays.asList(goodsSelIdsArray);
				for(String id :tmpgoodsSelIdsList) {
					selIds.add(Long.parseLong(id));
				}
			}
		}*/
		if (!StringUtils.isEmpty(goodsTypeIds)) {
			goodsTypeIdList = new ArrayList<Long> ();
			String[] goodsTypeIdsArray = goodsTypeIds.split(",");
			List<String> tmpGoodsTypeIdsList = null;
			if (goodsTypeIdsArray != null && goodsTypeIdsArray.length != 0) {
				tmpGoodsTypeIdsList =  Arrays.asList(goodsTypeIdsArray);
				for(String id : tmpGoodsTypeIdsList) {
					goodsTypeIdList.add(Long.parseLong(id));
				}
				
			}
		}
		// 初始化检查
		//CreateInventoryResultEnum resultEnum =	this.initCheck();
		//if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
		//		return resultEnum;
		//}
		//初始化
		CreateInventoryResultEnum resultWmsEnum = this.initWmsCheck(wmsGoodsId,isBeDelivery,goodsTypeIdList, lm);
		if(resultWmsEnum!=null&&!(resultWmsEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
			return resultWmsEnum;
		}
		// 真正的库存更新业务处理
		try {
			// 物流数据处理
			this.wmsDataHandler();
			if(wmsDO==null) {
				return CreateInventoryResultEnum.NO_WMS_DATA;
			}else {  //根据物流主键查询选型信息,这边无法获取
				//赋值选型id字符串
				/*if(!StringUtils.isEmpty(param.getGoodsSelectionIds())) {
					goodsSelectionIds = param.getGoodsSelectionIds();	
				}*/
				
			}
			//这个分店id，这边也无法获取，故需传过来
			suppliersId = Long.valueOf(StringUtils.isEmpty(param.getSuppliersId())?"0":param.getSuppliersId());  
			if(suppliersId!=0) {
				//根据分店id检查分店信息是否存在
				suppliersDO = this.goodsInventoryDomainRepository.querySuppliersInventoryById(suppliersId);
			}
			
			//将以逗号分隔的字符串处理成list形式
			if(!CollectionUtils.isEmpty(goodsTypeIdList)) {
				//String goodsTypeIds = param.getGoodsTypeIds();
				/*String[] goodsTypeIdsArray = param.getGoodsTypeIds().split(",");
				List<String> tmpGoodsTypeIdsList = null;
				if (goodsTypeIdsArray != null && goodsTypeIdsArray.length != 0) {
					tmpGoodsTypeIdsList =  Arrays.asList(goodsTypeIdsArray);
				}*/
				//根据商品id获取其下的选型
				List<GoodsSelectionModel> result = goodsInventoryDomainRepository.queryGoodsSelectionListByGoodsId(goodsId);
				//if(!CollectionUtils.isEmpty(tmpGoodsTypeIdsList)) {
					selectionDOList = new ArrayList<GoodsSelectionDO>();
					for(Long tmpGoodsTypId : goodsTypeIdList) {
						//Long tmpGoodsTypId = Long.parseLong(id);
						//循环过滤掉不符合条件的选型
						if(!CollectionUtils.isEmpty(result)) {
							for(GoodsSelectionModel selModel :result) {
								if(selModel.getGoodTypeId()!=null&&selModel.getGoodTypeId().equals(tmpGoodsTypId)) {  //根据商品选型类型id过滤选型信息
									selectionDOList.add(ObjectUtils.toSelectionDO(selModel));
								}
							}
						}
						
					}
				//}
				
			}
			
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 更新物流相关数据
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum updateAndInsertWmsData() {
		String message = StringUtils.EMPTY;
		if(idemptent) {  //幂等控制，已处理成功
			return CreateInventoryResultEnum.SUCCESS;
		}
		//初始化加分布式锁
		lm.addMetaData("updateInventory","updateInventory,start").addMetaData("updateInventory[" + (goodsId) + "]", goodsId);
		writeSysDeductLog(lm,false);
		LockResult<String> lockResult = null;
		String key = DLockConstants.UPDATEWMS_LOCK_KEY+"_goodsId_" + goodsId;
		try {
			lockResult = dLock.lockManualByTimes(key, DLockConstants.UPDATEWMS_LOCK_TIME, DLockConstants.UPDATEWMS_LOCK_RETRY_TIMES);
			if (lockResult == null
					|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
							.getCode()) {
				writeSysDeductLog(
						lm.setMethod("dLock").addMetaData("updateAndInsertWmsData lock errorMsg",
								goodsId), true);
			}
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			
			//更新附属表
				//扣减开始记录日志
				lm.setMethod("InventoryUpdateDomain.updateAndInsertWmsData").addMetaData("goodsId", goodsId).addMetaData("goodsTypeIds", goodsTypeIds).addMetaData("start", "start update wmsdata!");
				writeSysDeductLog(lm,true);
			Map<String, String> hash = null;
			if (inventoryInfoDO != null) {
				inventoryInfoDO.setGoodsSelectionIds(goodsTypeIds);
				hash = new HashMap<String, String>();
				hash.put(HashFieldEnum.goodsSelectionIds.toString(), goodsTypeIds);
				hash.put(HashFieldEnum.wmsId.toString(), String.valueOf(wmsDO.getId()));
				if(wmsDO!=null) {
					inventoryInfoDO.setWmsId(wmsDO.getId());
				}
				//先更新mysql
				//boolean handlerResult = inventoryInitAndUpdateHandle.updateGoodsInventory(goodsId, hash,inventoryInfoDO);
				//if(handlerResult) {
				
//					 this.goodsInventoryDomainRepository.updateFields(goodsId, hash);
				//}
				
				CallResult<GoodsInventoryDO> callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,hash,inventoryInfoDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					message = "updateAndInsertWmsData>updateGoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
					return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
				} else {
					message = "updateAndInsertWmsData>updateGoodsInventory_success[save success]goodsId:" + goodsId;
				}
				lm.setMethod("InventoryUpdateDomain.updateAndInsertWmsData").addMetaData("goodsId", goodsId).addMetaData("hash", hash).addMetaData("inventoryInfoDO", inventoryInfoDO).addMetaData("message", message);
				writeSysDeductLog(lm,true);
			}
			
			if(suppliersDO!=null) {  //存在，更新配置关系
				premaryKey4Suppliers = suppliersDO.getId();
				
			}else if(suppliersId!=null&&suppliersId!=0){  //插入关系数据
				GoodsSuppliersDO suppliersDO = new  GoodsSuppliersDO();
				premaryKey4Suppliers = sequenceUtil.getSequence(SEQNAME.seq_suppliers);
				suppliersDO.setId(premaryKey4Suppliers);
				suppliersDO.setGoodsId(goodsId);
				suppliersDO.setSuppliersId(suppliersId);
				//不能为空，有约束限制
				suppliersDO.setTotalNumber(0);
				suppliersDO.setLeftNumber(0);
//				inventoryInitAndUpdateHandle.saveGoodsSuppliers(goodsId, suppliersDO);
				// 消费对列的信息
				CallResult<Boolean> callResult = synInitAndAysnMysqlService.saveGoodsSuppliers(goodsId,suppliersDO);
					PublicCodeEnum publicCodeEnum = callResult
							.getPublicCodeEnum();
					
					if (publicCodeEnum != PublicCodeEnum.SUCCESS
							/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
						// 消息数据不存并且不成功
						message = "updateAndInsertWmsData>saveGoodsSuppliers_error[" + publicCodeEnum.getMessage()
								+ "]GoodsId:" + suppliersDO==null?"":String.valueOf(goodsId);
						return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
					} else {   
						message = "updateAndInsertWmsData>saveGoodsSuppliers_success[save2mysql success]goodsId:" + String.valueOf(goodsId);
						
					}
					
					lm.setMethod("InventoryUpdateDomain.updateAndInsertWmsData>saveGoodsSuppliers").addMetaData("goodsId", goodsId).addMetaData("suppliersDO", suppliersDO).addMetaData("message", message);
					writeSysDeductLog(lm,true);
			
			}
			//更新选型
        	//boolean handlerResult = false;
			//更新配置关系
            if(!CollectionUtils.isEmpty(selectionDOList)) {
            	for(GoodsSelectionDO selDO: selectionDOList) {
            		selDO.setSuppliersInventoryId(premaryKey4Suppliers);
            		selDO.setSuppliersSubId(suppliersId);
            	}
            	//更新选型
            	//handlerResult = inventoryInitAndUpdateHandle.updateBatchGoodsSelection(goodsId, selectionDOList);
            	//if(handlerResult) {  //更新redis
            	//	goodsInventoryDomainRepository.updateSelectionFields(selectionDOList);
            	//}
            	// 消费对列的信息
            	CallResult<List<GoodsSelectionDO>> callResult = synInitAndAysnMysqlService.updateBatchGoodsSelection(goodsId,selectionDOList);
        				PublicCodeEnum publicCodeEnum = callResult
        						.getPublicCodeEnum();
        				
        				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
        					// 消息数据不存并且不成功
        					message = "updateAndInsertWmsData>updateBatchGoodsSelection_error[" + publicCodeEnum.getMessage()
        							+ "]goodsId:" + goodsId;
        					return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
        				} else {
        					message = "updateAndInsertWmsData>updateBatchGoodsSelection_success[save success]goodsId:" + goodsId;
        				}
        				lm.setMethod("InventoryUpdateDomain.updateAndInsertWmsData").addMetaData("goodsId", goodsId).addMetaData("selectionDOList", selectionDOList).addMetaData("message", message);
        				writeSysDeductLog(lm,true);	
            	
            }
				lm.setMethod("InventoryUpdateDomain.updateAndInsertWmsData").addMetaData("deduct,end", "end deduct inventory!");
				writeSysDeductLog(lm,true);

		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"updateInventory error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally{
			dLock.unlockManual(key);
		}
		lm.addMetaData("result", "end");
		writeSysDeductLog(lm,false);
		//处理成返回前设置tag
		goodsInventoryDomainRepository.setTag(DLockConstants.UPDATE_WMS_DATA_SUCCESS + "_"+ tokenid, DLockConstants.IDEMPOTENT_DURATION_TIME, DLockConstants.HANDLER_SUCCESS);
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	// 初始化库存
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum initCheck() {
		this.goodsId = Long.valueOf(param.getGoodsId());
		// 初始化加分布式锁
		lm.addMetaData("initCheck", "initCheck,start").addMetaData(
				"initCheck[" + (goodsId) + "]", goodsId);
		writeBusInitLog(lm, false);
		LockResult<String> lockResult = null;
		CreateInventoryResultEnum resultEnum = null;
		String key = DLockConstants.INIT_LOCK_KEY + "_goodsId_" + goodsId;
		try {
			lockResult = dLock.lockManualByTimes(key, 5000L, 5);
			if (lockResult == null
					|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
							.getCode()) {
				writeBusInitLog(
						lm.setMethod("dLock").addMetaData("errorMsg", goodsId),
						false);
			}
			InventoryInitDomain create = new InventoryInitDomain(goodsId, lm);
			// 注入相关Repository
		    create.setWmsGoodsId(wmsGoodsId);
			create.setIsBeDelivery(isBeDelivery);
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
			resultEnum = create.businessExecute();
		} finally {
			dLock.unlockManual(key);
		}
		lm.addMetaData("result", resultEnum);
		lm.addMetaData("result", "end");
		writeBusInitLog(lm, false);
		return resultEnum;
	}
	
	 //初始化物流库存库存
	@SuppressWarnings("unchecked")
	public CreateInventoryResultEnum initWmsCheck(String wmsGoodsId,String isBeDelivery,List<Long> goodsTypeIdList,LogModel lm) {
			
				//初始化加分布式锁
				lm.addMetaData("InventoryWmsDataUpdateDomain initWmsCheck","initWmsCheck,start").addMetaData("initWmsCheck[" + (wmsGoodsId) + "]", wmsGoodsId);
				writeBusInitLog(lm,false);
				LockResult<String> lockResult = null;
				CreateInventoryResultEnum resultEnum = null;
				String key = DLockConstants.INIT_LOCK_KEY+"_wmsGoodsId_" + wmsGoodsId;
				try {
					lockResult = dLock.lockManualByTimes(key, DLockConstants.INIT_LOCK_TIME, DLockConstants.INIT_LOCK_RETRY_TIMES);
					if (lockResult == null
							|| lockResult.getCode() != LockResultCodeEnum.SUCCESS
									.getCode()) {
						writeBusInitLog(
								lm.addMetaData("GoodsInventoryQueryServiceImpl initWmsCheck dLock errorMsg",
										wmsGoodsId), true);
					}
					InventoryInitDomain create = new InventoryInitDomain();
					//注入相关Repository
					create.setWmsGoodsId(wmsGoodsId);
					create.setIsBeDelivery(isBeDelivery);
					create.setGoodsTypeIdList(goodsTypeIdList);
					create.setLm(lm);
					create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
					create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
					create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
					//create.setInitCacheDomainRepository(initCacheDomainRepository);
					resultEnum = create.business4WmsExecute();
				} finally{
					dLock.unlockManual(key);
				}
				lm.addMetaData("result", resultEnum);
				lm.addMetaData("result", "end");
				writeBusInitLog(lm,false);
				return resultEnum;
			}
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			if (inventoryInfoDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_WMS
						.getDescription());
			}
			updateActionDO.setActionType(ResultStatusEnum.UPDATE_WMSDATA
					.getDescription());
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setContent(JsonUtils.convertObjectToString(param)); // 操作内容
			updateActionDO.setRemark("更新物流相关数据");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillInventoryUpdateActionDO error" + e.getMessage()),false, e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}

	/**
	 * 参数检查
	 * 
	 * @return
	 */
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getGoodsId())) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}
		if (StringUtils.isEmpty(param.getWmsGoodsId())) {
			return CreateInventoryResultEnum.INVALID_WMSGOODSID;
		}
		/*if (param.getIsAddGoodsSelection()==1&&StringUtils.isEmpty(param.getGoodsSelectionIds())) {
			return CreateInventoryResultEnum.NO_SELECT_SELECTION;
		}*/
		
		
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	
	public void setSynInitAndAysnMysqlService(
			SynInitAndAysnMysqlService synInitAndAysnMysqlService) {
		this.synInitAndAysnMysqlService = synInitAndAysnMysqlService;
	}

	public void setInventoryInitAndUpdateHandle(
			InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle) {
		this.inventoryInitAndUpdateHandle = inventoryInitAndUpdateHandle;
	}

	public void setdLock(DLockImpl dLock) {
		this.dLock = dLock;
	}

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}
	

}
