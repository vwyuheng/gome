package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.UpdateWmsDataParam;
import com.tuan.inventory.model.result.CallResult;

public class InventoryWmsDataUpdateDomain extends AbstractDomain {
	private static Log logupdate = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	//private static Log logMsg=LogFactory.getLog("NOTIFYSERVER.USER");
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private UpdateWmsDataParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	//private DLockImpl dLock;//分布式锁
	private GoodsInventoryActionDO updateActionDO;
	private String tokenid;  //redis序列,解决接口幂等问题
	private String wmsGoodsId;  //物流编码
	private GoodsInventoryWMSDO wmsDO;
	private GoodsInventoryDO inventoryInfoDO;
	// 调整前总库存
	private int pretotalnum = 0;
	private GoodsSuppliersDO suppliersDO;
	private Long goodsId;
	private Long goodsBaseId;
	private Long suppliersId;
	private String isBeDelivery;
	private long premaryKey4Suppliers;  //分店主键
	private String goodsSelectionIds = "";  //共享库存时所绑定的选型表ID集合，以逗号分隔开；如：1,2,3或2,即选型表主键
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
		if (!StringUtils.isEmpty(wmsGoodsId)) { // if1
			// 再次查询物流商品库存信息[确保最新数据]
			GoodsInventoryWMSDO wmsDOTmp = this.goodsInventoryDomainRepository.queryGoodsInventoryWms(wmsGoodsId);
			
			if(wmsDOTmp!=null) {
				//转换
				if(wmsDOTmp.getIsBeDelivery()!=Integer.valueOf(isBeDelivery)) { //过滤
					wmsDOTmp = null;
				}	

			}else {  //从wowotuan库存加载
				// 初始化商品库存信息
				CallResult<GoodsInventoryWMSDO> callGoodsWmsResult = synInitAndAysnMysqlService
						.selectGoodsInventoryWMSByWmsGoodsId(wmsGoodsId,StringUtils.isEmpty(isBeDelivery)?null:Integer.valueOf(isBeDelivery));
				
				if (callGoodsWmsResult != null&&callGoodsWmsResult.isSuccess()) {
					wmsDOTmp = callGoodsWmsResult.getBusinessResult();
				}
			}
			setWmsDO(wmsDOTmp);//赋值

		} // if 
		//加载商品库存信息
		if(goodsId!=null&&goodsId!=0) {
			GoodsInventoryDO inventoryInfoDOTmp = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if(inventoryInfoDOTmp!=null) {
					setPretotalnum(inventoryInfoDOTmp.getTotalNumber());
					setInventoryInfoDO(inventoryInfoDOTmp);
				}else {
					// 初始化商品库存信息
					CallResult<GoodsInventoryDO> callGoodsInventoryDOResult = this.synInitAndAysnMysqlService
							.selectGoodsInventoryByGoodsId(goodsId);
					if (callGoodsInventoryDOResult != null
							&& callGoodsInventoryDOResult.isSuccess()) {
						inventoryInfoDOTmp = callGoodsInventoryDOResult.getBusinessResult();
						if (inventoryInfoDOTmp != null) {
							setPretotalnum(inventoryInfoDOTmp.getTotalNumber());
							setInventoryInfoDO(inventoryInfoDOTmp);
						}else {
							inventoryInfoDOTmp = new GoodsInventoryDO();
							inventoryInfoDOTmp.setGoodsId(goodsId);
							inventoryInfoDOTmp.setGoodsSaleCount(0);
							inventoryInfoDOTmp.setWaterfloodVal(0);
							setInventoryInfoDO(inventoryInfoDOTmp);
						}
					}
				}
				
		}
			
		
		//处理物流配型数据,如果存在的话
		if(!CollectionUtils.isEmpty(goodsTypeIdList)) {
			List<GoodsSelectionDO> selectionDOListTmp = null;
			//根据选型id校验下该列表下的选型是否已存在
			CallResult<List<GoodsSelectionDO>> callSelListResult = this.synInitAndAysnMysqlService
					.selectSelectionByGoodsTypeIds(goodsTypeIdList);
			if (callSelListResult != null && callSelListResult.isSuccess()) {
				selectionDOListTmp = callSelListResult.getBusinessResult();
				if(CollectionUtils.isEmpty(selectionDOListTmp)) {
					CallResult<List<GoodsSelectionDO>> callGoodsSelectionListDOResult = this.synInitAndAysnMysqlService
							.selectGoodsSelectionListByGoodsId(goodsTypeIdList,goodsId);
					if (callGoodsSelectionListDOResult != null
							&&callGoodsSelectionListDOResult.isSuccess()) {
						selectionDOListTmp = callSelListResult.getBusinessResult();
					} else {
						logupdate.info("根据选型id获取选型信息集合出错,selectionIdList:"+goodsTypeIdList);
					}
				}
				setSelectionDOList(selectionDOListTmp);//赋值
			}else {  
				logupdate.info("根据goodTypeId获取选型信息集合出错,goodsTypeIdList:"+goodsTypeIdList);
			}
			//根据商品id获取其下的选型
			/*List<GoodsSelectionModel> result = goodsInventoryDomainRepository.queryGoodsSelectionListByGoodsId(goodsId);
				selectionDOList = new ArrayList<GoodsSelectionDO>();
				for(Long tmpGoodsTypId : goodsTypeIdList) {
					//循环过滤掉不符合条件的选型
					if(!CollectionUtils.isEmpty(result)) {
						for(GoodsSelectionModel selModel :result) {
							if(selModel.getGoodTypeId()!=null&&selModel.getGoodTypeId().equals(tmpGoodsTypId)) {  //根据商品选型类型id过滤选型信息
								selectionDOList.add(ObjectUtils.toSelectionDO(selModel));
							}
						}
					}
					
				}*/
			//}
			
		}
		
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
		//幂等性检查
		if (!StringUtils.isEmpty(param.getTokenid())) { // if
			this.tokenid = param.getTokenid();
			this.idemptent = idemptent();
			if(idemptent) {
				return CreateInventoryResultEnum.SUCCESS;
			}
		}
		if (!StringUtils.isEmpty(param.getGoodsId())) { // if1
			goodsId = Long.valueOf(StringUtils.isEmpty(param.getGoodsId())?"0":param.getGoodsId());
		} // if 
		if (StringUtils.isEmpty(param.getGoodsBaseId())&&goodsId!=null&&goodsId!=0) { // if1
			GoodsInventoryDO temp = this.goodsInventoryDomainRepository
					.queryGoodsInventory(goodsId);
			if(temp!=null) {
				goodsBaseId = temp.getGoodsBaseId();
				if(goodsBaseId!=null&&goodsBaseId==0) {
					// 初始化商品库存信息
					CallResult<GoodsInventoryDO> callGoodsInventoryDOResult = this.synInitAndAysnMysqlService
							.selectGoodsInventoryByGoodsId(goodsId);
					if (callGoodsInventoryDOResult != null&&callGoodsInventoryDOResult.isSuccess()) {
						temp = 	callGoodsInventoryDOResult.getBusinessResult();
						if(temp!=null) {
							this.goodsBaseId = temp.getGoodsBaseId();
						}
					}
				}
			}
			
		}else {
			if(!StringUtils.isEmpty(param.getGoodsBaseId())) {
				goodsBaseId = Long.valueOf(param.getGoodsBaseId());
			}else {
				return CreateInventoryResultEnum.INVALID_GOODSBASEID;
			}
			
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
		
		//初始化
		CreateInventoryResultEnum resultWmsEnum = this.initWmsCheck("from_InventoryWmsDataUpdateDomain",goodsId,wmsGoodsId,isBeDelivery,goodsTypeIdList, lm);
		
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
			if(inventoryInfoDO==null) {
				return CreateInventoryResultEnum.NO_GOODS;
			}
			//这个分店id，这边也无法获取，故需传过来
			suppliersId = Long.valueOf(StringUtils.isEmpty(param.getSuppliersId())?"0":param.getSuppliersId());  
			if(suppliersId!=0) {
				//根据分店id检查分店信息是否存在
				suppliersDO = this.goodsInventoryDomainRepository.querySuppliersInventoryById(suppliersId);
			}
			
			
			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 更新物流相关数据
	public CreateInventoryResultEnum updateAndInsertWmsData() {
		String message = StringUtils.EMPTY;
		if(idemptent) {  //幂等控制，已处理成功
			return CreateInventoryResultEnum.SUCCESS;
		}
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			
			//更新附属表
			lm.addMetaData("goodsId", goodsId).addMetaData("goodsTypeIds", goodsTypeIds).addMetaData("start", "start update wmsdata!");
			logupdate.info(lm.toJson(false));
			
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
					
					lm.addMetaData("goodsId", goodsId).addMetaData("suppliersDO", suppliersDO).addMetaData("message", message);
					logupdate.info(lm.toJson(false));
			
			}
			//更新选型
			 Map<String, String> hash = null;
			//计算出的物流商品的剩余总库存
			int leftNum = 0;
			//计算出的物流商品的总库存
			int totalNum = 0;
			//更新配置关系
            if(!CollectionUtils.isEmpty(selectionDOList)) {
            	String selectionIdsList = StringUtil.getSelectionIdByDotSeparate(selectionDOList);
            	setGoodsSelectionIds(StringUtils.isEmpty(selectionIdsList)?"":selectionIdsList);
            	for(GoodsSelectionDO selDO: selectionDOList) {
            		//selDO.setSuppliersInventoryId(premaryKey4Suppliers);
            		//selDO.setSuppliersSubId(suppliersId);
            		//计算总剩余库存和总库存
            		leftNum = leftNum+selDO.getLeftNumber();
            		totalNum = totalNum+selDO.getTotalNumber();
            		
            	}
            	// 更新选型信息并建立商品和选型的关系
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
        				lm.addMetaData("goodsId", goodsId).addMetaData("selectionDOList", selectionDOList).addMetaData("message", message);
        				logupdate.info(lm.toJson(false));
            	
            }else {
            	// 将物流的剩余库存和总库存更新到对应的商品表
            	if(wmsDO!=null) {
            		leftNum = wmsDO.getLeftNumber();
            		totalNum = wmsDO.getTotalNumber();
            	}
            	
            }
            //更新物流wms库存信息
            if(wmsDO!=null) {
        		wmsDO.setLeftNumber(leftNum);
        		wmsDO.setTotalNumber(totalNum);
        	}
           //更新商品库存信息
			if (inventoryInfoDO != null) {
				inventoryInfoDO.setGoodsBaseId(goodsBaseId);
				inventoryInfoDO.setGoodsSelectionIds(goodsSelectionIds);
				inventoryInfoDO.setLeftNumber(leftNum);
				inventoryInfoDO.setTotalNumber(totalNum);
				inventoryInfoDO.setLimitStorage(1);  //物流商品都是限制库存商品
				hash = new HashMap<String, String>();
				hash.put(HashFieldEnum.goodsSelectionIds.toString(), goodsSelectionIds);
				hash.put(HashFieldEnum.wmsId.toString(), String.valueOf(wmsDO.getId()));
				hash.put(HashFieldEnum.leftNumber.toString(), String.valueOf(leftNum));
				hash.put(HashFieldEnum.totalNumber.toString(), String.valueOf(totalNum));
				hash.put(HashFieldEnum.limitStorage.toString(), String.valueOf(1));
				if(wmsDO!=null) {
					inventoryInfoDO.setWmsId(wmsDO.getId());
				}
				
				CallResult<GoodsInventoryDO> callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,hash,inventoryInfoDO,pretotalnum,wmsDO);
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				
				if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
					// 消息数据不存并且不成功
					message = "InventoryWmsDataUpdateDomain>updateGoodsInventory_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
					return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
				} else {
					message = "updateAndInsertWmsData>updateGoodsInventory_success[save success]goodsId:" + goodsId;
				}
				lm.addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId).addMetaData("hash", hash).addMetaData("pretotalnum", pretotalnum).addMetaData("inventoryInfoDO", inventoryInfoDO).addMetaData("message", message);
				logupdate.info(lm.toJson(false));
			}	

		} catch (Exception e) {
			
			logupdate.error(lm.addMetaData("errorMsg",
							"updateInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		//处理成返回前设置tag
		if(StringUtils.isNotEmpty(tokenid)) {
			goodsInventoryDomainRepository.setTag(DLockConstants.UPDATE_WMS_DATA_SUCCESS + "_"+ tokenid, DLockConstants.IDEMPOTENT_DURATION_TIME, DLockConstants.HANDLER_SUCCESS);
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 发送库存新增消息
	public void sendNotify() {
		try {
			if (goodsId != 0 &&goodsBaseId!=null&&goodsBaseId != 0) {
				InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
				goodsInventoryDomainRepository
						.sendNotifyServerMessage(
								NotifySenderEnum.InventoryWmsDataUpdateDomain
										.toString(), JSONObject
										.fromObject(notifyParam));
			} else {
				logupdate.warn("from InventoryWmsDataUpdateDomain send msg,goodsBaseId:"
						+ goodsBaseId + ",goodsId:" + goodsId + " is illegal!");
			}

			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"sendNotify error" + e.getMessage()).toJson(false), e);
		}
	}

		//填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(){
					InventoryNotifyMessageParam notifyParam = null;
					try {
						notifyParam = new InventoryNotifyMessageParam();
						notifyParam.setGoodsBaseId(goodsBaseId);
						notifyParam.setGoodsId(goodsId);
						if(inventoryInfoDO!=null) {
							notifyParam.setLimitStorage(inventoryInfoDO.getLimitStorage());
							notifyParam.setWaterfloodVal(inventoryInfoDO.getWaterfloodVal());
							notifyParam.setTotalNumber(inventoryInfoDO.getTotalNumber());
							notifyParam.setLeftNumber(inventoryInfoDO.getLeftNumber());
							//销量
							notifyParam.setSales(inventoryInfoDO.getGoodsSaleCount()==null?0:inventoryInfoDO.getGoodsSaleCount());
						}
						//查询base销量和商品销量
						GoodsBaseInventoryDO baseDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
						if(baseDO!=null) {
							//库存总数 减 库存剩余//创建商品时
							notifyParam.setBaseSaleCount(baseDO.getBaseSaleCount());
							notifyParam.setBaseTotalCount(baseDO.getBaseTotalCount());
						}
						//由于分店和选型的库存数量没有变化故无需处理
						
					} catch (Exception e) {
						logupdate.error(lm.addMetaData("errorMsg",
								"fillInventoryNotifyMessageParam error" + e.getMessage()).toJson(false), e);
					}
					return notifyParam;
				}
		
	 //初始化物流库存库存
	public CreateInventoryResultEnum initWmsCheck(String initFromDesc,Long goodsId,
			String wmsGoodsId, String isBeDelivery, List<Long> goodsTypeIdList,
			LogModel lm) {

		CreateInventoryResultEnum resultEnum = null;

		InventoryInitDomain create = new InventoryInitDomain();
		// 注入相关Repository
		create.setWmsGoodsId(wmsGoodsId);
		create.setIsBeDelivery(isBeDelivery);
		create.setGoodsTypeIdList(goodsTypeIdList);
		create.setGoodsId(goodsId);
		create.setLm(lm);
		create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
		create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		create.setInitFromDesc(initFromDesc);
		resultEnum = create.business4WmsUpdateExecute();

		return resultEnum;
	}
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsBaseId(goodsBaseId);
			updateActionDO.setGoodsId(goodsId);
			if (inventoryInfoDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_WMS
						.getDescription());
			}
			updateActionDO.setActionType(ResultStatusEnum.UPDATE_WMSDATA
					.getDescription());
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			updateActionDO.setContent("preInventoryInfoDO:"+inventoryInfoDO==null?"inventoryInfoDO is null!":JSON.toJSONString(inventoryInfoDO)+",param:"+JsonUtils.convertObjectToString(param)); // 操作内容
			updateActionDO.setRemark("更新物流相关数据");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
			
		} catch (Exception e) {
			logupdate.error(lm.addMetaData("errorMsg",
					"fillInventoryUpdateActionDO error" + e.getMessage()).toJson(false), e);
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

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public List<GoodsSelectionDO> getSelectionDOList() {
		return selectionDOList;
	}

	public void setSelectionDOList(List<GoodsSelectionDO> selectionDOList) {
		this.selectionDOList = selectionDOList;
	}

	public GoodsInventoryWMSDO getWmsDO() {
		return wmsDO;
	}

	public void setWmsDO(GoodsInventoryWMSDO wmsDO) {
		this.wmsDO = wmsDO;
	}

	public GoodsInventoryDO getInventoryInfoDO() {
		return inventoryInfoDO;
	}

	public void setInventoryInfoDO(GoodsInventoryDO inventoryInfoDO) {
		this.inventoryInfoDO = inventoryInfoDO;
	}

	public int getPretotalnum() {
		return pretotalnum;
	}

	public void setPretotalnum(int pretotalnum) {
		this.pretotalnum = pretotalnum;
	}

	public String getGoodsSelectionIds() {
		return goodsSelectionIds;
	}

	public void setGoodsSelectionIds(String goodsSelectionIds) {
		this.goodsSelectionIds = goodsSelectionIds;
	}
	

}
