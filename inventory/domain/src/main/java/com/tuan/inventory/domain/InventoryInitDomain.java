package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.result.CallResult;
/**
 * 初始化库存domain
 * @author henry.yu
 * @date 2014/4/23
 */
public class InventoryInitDomain extends AbstractDomain{
	
	private LogModel lm;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private GoodsInventoryDO inventoryInfoDO;
	//根据商品id得到的物流商品信息
	private GoodsInventoryWMSDO wmsInventory;
	//根据物流编码得到的物流商品信息
	private GoodsInventoryWMSDO wmsInventory4wmsGoodsId;
	// 初始化用
	private List<GoodsSuppliersDO> suppliersInventoryList;
	private List<GoodsSelectionDO> selectionInventoryList;
	
	private List<GoodsInventoryDO> wmsInventoryList;
	//真正准备保存的list
	private List<GoodsInventoryDO> wmsInventoryRadySaveList;
	private GoodsInventoryWMSDO wmsUpate;
	private List<GoodsSelectionDO> selWmsList;
	
	private List<GoodsSelectionDO> selWmsList4GoodsTypeId ;
	private long goodsId;
	private String wmsGoodsId;
	private String isBeDelivery;
	private List<Long> goodsTypeIdList;
	
	// 是否需要初始化
	private boolean isInit;
	// 是否需要初始化物流商品
	private boolean isInitWms;
	
	public InventoryInitDomain(long goodsId, LogModel lm) {
		this.goodsId = goodsId;
		this.lm = lm;
	}
	public InventoryInitDomain() {}
	
	// 业务检查4物流
	public CreateInventoryResultEnum business4WmsUpdateExecute() {
		CreateInventoryResultEnum resultEnum = null;
		// 初始化检查
		resultEnum = this.initCheck4Wms();
		if (isInitWms) {
			resultEnum = this.init4WmsUpdate();
		}
		return resultEnum;
	}
	// 业务检查4物流
	public CreateInventoryResultEnum business4WmsExecute() {
			CreateInventoryResultEnum resultEnum = null;
			// 初始化检查
			resultEnum = this.initCheck4Wms();
			if (isInitWms) {
				resultEnum = this.init4Wms();
			}
			return resultEnum;
		}
	
	// 业务检查
	public CreateInventoryResultEnum businessExecute() {
		CreateInventoryResultEnum resultEnum = null;
		// 初始化检查
		resultEnum = this.initCheck();
		if (isInit) {
			resultEnum = this.init();
		}
		return resultEnum;
	}
	// 物流初始化检查
	public CreateInventoryResultEnum initCheck4Wms() {
		try {
			if (!StringUtils.isEmpty(wmsGoodsId)) { // 
				this.wmsUpate = this.goodsInventoryDomainRepository.queryGoodsInventoryWms(wmsGoodsId);
				if(wmsUpate==null) {
					// 初始化库存
					this.isInitWms = true;
					//先查询本系统mysql表:为了防止mysql与redis数据不一致
					CallResult<GoodsInventoryWMSDO> callSelfResult = synInitAndAysnMysqlService.selectSelfGoodsInventoryWMSByWmsGoodsId(wmsGoodsId);
					if (callSelfResult == null || !callSelfResult.isSuccess()) {
						this.isInitWms = false;
						return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
					}else {  //初始化赋值
						GoodsInventoryWMSDO tmpWmsDO = callSelfResult.getBusinessResult();
						if(tmpWmsDO!=null) {
							this.wmsUpate = tmpWmsDO;
						}else {
							// 初始化商品库存信息
							CallResult<GoodsInventoryWMSDO> callGoodsWmsResult = synInitAndAysnMysqlService
									.selectGoodsInventoryWMSByWmsGoodsId(wmsGoodsId,StringUtils.isEmpty(isBeDelivery)?null:Integer.valueOf(isBeDelivery));
							
							if (callGoodsWmsResult == null || !callGoodsWmsResult.isSuccess()) {
								this.isInitWms = false;
								return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
							}else {  //初始化赋值
								this.wmsUpate = callGoodsWmsResult.getBusinessResult();
							}
						}
					}
					
					//通过物流编码查询商品id是否存在,
					CallResult<List<GoodsInventoryDO>> callGoodsInventoryListDOResult = synInitAndAysnMysqlService
							.selectInventoryList4Wms(wmsGoodsId);
					if (callGoodsInventoryListDOResult == null || !callGoodsInventoryListDOResult.isSuccess()) {
						this.isInitWms = false;
						return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
					}else {
					     this.wmsInventoryList = 	callGoodsInventoryListDOResult.getBusinessResult();
					}
					
					//根据商品检查是否已存在
					if(!CollectionUtils.isEmpty(wmsInventoryList)) {
						wmsInventoryRadySaveList = new ArrayList<GoodsInventoryDO>();
						for(GoodsInventoryDO goodsDO:wmsInventoryList) {
							if(goodsDO.getGoodsId()>0) {
								GoodsInventoryDO tmpDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsDO.getGoodsId());
								if(tmpDO==null) {
									wmsInventoryRadySaveList.add(goodsDO);
								}
							}
							
						}
					}
					
				}
			}
			
			//处理物流选型类型的
			if(!CollectionUtils.isEmpty(goodsTypeIdList)) {
				//根据选型id校验下该列表下的选型是否已存在
				CallResult<List<GoodsSelectionDO>> callSelListResult = this.synInitAndAysnMysqlService
						.selectSelectionByGoodsTypeIds(goodsTypeIdList);
				if (callSelListResult == null || !callSelListResult.isSuccess()) {
					this.isInitWms = false;
					return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
				}else {  //初始化赋值
					this.selWmsList = callSelListResult.getBusinessResult();
				}
				
				if(!CollectionUtils.isEmpty(selWmsList)) {
					selWmsList4GoodsTypeId = new ArrayList<GoodsSelectionDO>();
						for(GoodsSelectionDO selDO:selWmsList) {
							selDO.setGoodsId(goodsId);
							selDO.setWmsGoodsId(wmsGoodsId);
							selWmsList4GoodsTypeId.add(selDO);			
						}
					
				}else {
					this.isInitWms = false;
					return CreateInventoryResultEnum.INVALID_SELECTION_GOODSTYPEID;
				}
				
				
			}
			
			//当物流商品已存在，但是后台调整了其下的选型，兼容物流选型更新时的情况
			if(!isInitWms&&!CollectionUtils.isEmpty(selWmsList4GoodsTypeId)) {
				wmsUpate = null; //置空，以免冲突
				// 初始化库存
				this.isInitWms = true;
			}
			
		} catch (Exception e) {
			this.isInitWms = false;
			/*this.writeBusInitErrorLog(
					lm.addMetaData("errorMsg",
							"initCheck4Wms error" + e.getMessage()),false, e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"initCheck4Wms error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
		
	}
	// 初始化检查
	public CreateInventoryResultEnum initCheck() {
		try {
			if (goodsId > 0) { //

				this.inventoryInfoDO = this.goodsInventoryDomainRepository
						.queryGoodsInventory(goodsId);
				if (inventoryInfoDO == null) {
					// 初始化库存
					this.isInit = true;
					// 先查本地库
					CallResult<GoodsInventoryDO> callSelfResult = this.synInitAndAysnMysqlService
							.selectSelfGoodsInventoryByGoodsId(goodsId);
					if (callSelfResult == null || !callSelfResult.isSuccess()) {
						this.isInit = false;
						return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
					} else {
						this.inventoryInfoDO = callSelfResult
								.getBusinessResult();
						if (inventoryInfoDO == null) {
							// 初始化商品库存信息
							CallResult<GoodsInventoryDO> callGoodsInventoryDOResult = this.synInitAndAysnMysqlService
									.selectGoodsInventoryByGoodsId(goodsId);
							if (callGoodsInventoryDOResult == null
									|| !callGoodsInventoryDOResult.isSuccess()) {
								this.isInit = false;
								return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
							} else {
								this.inventoryInfoDO = callGoodsInventoryDOResult
										.getBusinessResult();
								if (inventoryInfoDO == null) {
									return CreateInventoryResultEnum.NO_GOODS;
								}
							}
						}
					}

					// 查询该商品选型库存信息
					CallResult<List<GoodsSelectionDO>> callGoodsSelectionListDOResult = this.synInitAndAysnMysqlService
							.selectGoodsSelectionListByGoodsId(goodsId);
					if (callGoodsSelectionListDOResult == null
							|| !callGoodsSelectionListDOResult.isSuccess()) {
						this.isInit = false;
						return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
					} else {
						this.selectionInventoryList = callGoodsSelectionListDOResult
								.getBusinessResult();
					}
					// 查询该商品分店库存信息
					CallResult<List<GoodsSuppliersDO>> callGoodsSuppliersListDOResult = this.synInitAndAysnMysqlService
							.selectGoodsSuppliersListByGoodsId(goodsId);
					if (callGoodsSuppliersListDOResult == null
							|| !callGoodsSuppliersListDOResult.isSuccess()) {
						this.isInit = false;
						return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
					} else {
						this.suppliersInventoryList = callGoodsSuppliersListDOResult
								.getBusinessResult();
					}
					// 商品物流库存信息处理
					// 首先判断是否物流商品
					CallResult<GoodsInventoryWMSDO> callIsOrNotWmsResult = this.synInitAndAysnMysqlService
							.selectIsOrNotGoodsWMSByGoodsId(goodsId);
					if (callIsOrNotWmsResult == null
							|| !callIsOrNotWmsResult.isSuccess()) {
						this.isInit = false;
						return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
					} else { // 是物流商品
						this.wmsInventory = callIsOrNotWmsResult
								.getBusinessResult();
					}

					// 再根据传过来的物流编码来查询物流库存信息
					if (StringUtils.isNotEmpty(wmsGoodsId)) { // 目的是为了怕遗漏数据，因为该物流表是源头,可能先于商品
						CallResult<GoodsInventoryWMSDO> callWmsResult = this.synInitAndAysnMysqlService
								.selectGoodsInventoryWMSByWmsGoodsId(
										wmsGoodsId, StringUtils
												.isEmpty(isBeDelivery) ? null
												: Integer.valueOf(isBeDelivery));
						if (callWmsResult == null || !callWmsResult.isSuccess()) {
							this.isInit = false;
							return CreateInventoryResultEnum.INIT_INVENTORY_ERROR;
						} else { // 是物流商品
							this.wmsInventory4wmsGoodsId = callWmsResult
									.getBusinessResult();
						}
					}
					// 校验是否是统一物流商品
					if (wmsInventory != null && wmsInventory4wmsGoodsId != null) {
						if (wmsInventory.getWmsGoodsId().equals(
								wmsInventory4wmsGoodsId.getWmsGoodsId())) { // 冲突
							// 有冲突，则以物流参数id不为空的为主
							this.wmsInventory = null;
						}
					}
				}
			} else {
				return CreateInventoryResultEnum.INVALID_GOODSID;
			}
		} catch (Exception e) {
			/*this.writeBusInitErrorLog(
					lm.addMetaData("errorMsg",
							"initCheck error" + e.getMessage()), false, e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"initCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;

	}
	
	/**
	 * 物流库存初始化
	 */
	public CreateInventoryResultEnum init4WmsUpdate() {
		String message = StringUtils.EMPTY;
		CallResult<Boolean> callResult  = null;
		long startTime = System.currentTimeMillis();
		try {
			// 保存商品库存
			callResult = synInitAndAysnMysqlService.saveGoodsWmsUpdateInventory(goodsId,wmsUpate,wmsInventoryRadySaveList,selWmsList4GoodsTypeId);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS
					/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
				// 消息数据不存并且不成功
				message = "init4Wms_error[" + publicCodeEnum.getMessage()
						+ "]wmsGoodsId:" + wmsUpate==null?"":wmsUpate.getWmsGoodsId();
				return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
			} else {   
				message = "init4Wms_success[init4Wms success]goodsId="+goodsId+",wmsUpate="+wmsUpate+",wmsInventoryRadySaveList="+wmsInventoryRadySaveList+",selWmsList4GoodsTypeId="+selWmsList4GoodsTypeId;
				
				
			}
		} catch (Exception e) {
			/*this.writeBusInitErrorLog(
					lm.addMetaData("errorMsg",
							"init4Wms error" + e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"init4Wms error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("wmsUpate",wmsUpate)
					.addMetaData("wmsInventoryList",wmsInventoryList)
					.addMetaData("selWmsList",selWmsList)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("message",message)
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		
		return CreateInventoryResultEnum.SUCCESS;
		
	}
	/**
	 * 物流库存初始化
	 */
	public CreateInventoryResultEnum init4Wms() {
		String message = StringUtils.EMPTY;
		CallResult<Boolean> callResult  = null;
		long startTime = System.currentTimeMillis();
		try {
			// 保存商品库存
			callResult = synInitAndAysnMysqlService.saveGoodsWmsInventory(goodsId,wmsUpate,wmsInventoryRadySaveList,selWmsList4GoodsTypeId);
				PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();
						
						if (publicCodeEnum != PublicCodeEnum.SUCCESS
								/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
							// 消息数据不存并且不成功
							message = "init4Wms_error[" + publicCodeEnum.getMessage()
									+ "]wmsGoodsId:" + wmsUpate==null?"":wmsUpate.getWmsGoodsId();
							return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
						} else {   
							message = "init4Wms_success[init4Wms success]goodsId="+goodsId+",wmsUpate="+wmsUpate+",wmsInventoryRadySaveList="+wmsInventoryRadySaveList+",selWmsList4GoodsTypeId="+selWmsList4GoodsTypeId;
							
							
						}
		} catch (Exception e) {
			/*this.writeBusInitErrorLog(
					lm.addMetaData("errorMsg",
							"init4Wms error" + e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"init4Wms error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("wmsUpate",wmsUpate)
					.addMetaData("wmsInventoryList",wmsInventoryList)
					.addMetaData("selWmsList",selWmsList)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("message",message)
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		
		return CreateInventoryResultEnum.SUCCESS;
		
	}
	/**
	 * 库存初始化
	 */
	public CreateInventoryResultEnum init() {
		String message = StringUtils.EMPTY;
		CallResult<Boolean> callResult  = null;
		long startTime = System.currentTimeMillis();
		try {
		     // 保存商品库存
			callResult = synInitAndAysnMysqlService.saveGoodsInventory(goodsId,inventoryInfoDO,selectionInventoryList,suppliersInventoryList,wmsInventory,wmsInventory4wmsGoodsId);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS
					/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
				// 消息数据不存并且不成功
				message = "init_error[" + publicCodeEnum.getMessage()
						+ "]goodsId:" + goodsId;
				return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
			} else {
				message = "init_success[save success]goodsId:" + goodsId;
				
			}
		} catch (Exception e) {
			/*this.writeBusInitErrorLog(
					lm.addMetaData("errorMsg",
							"init error" + e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"init error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("goodsId",goodsId)
					.addMetaData("inventoryInfoDO",inventoryInfoDO)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("message",message)
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		
		return CreateInventoryResultEnum.SUCCESS;
			
	}

	/**
	 * 新增库存
	 * @param isExists
	 * @param inventoryInfoDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 */
	public CreateInventoryResultEnum createInventory(String tokenid,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
		String message = StringUtils.EMPTY;
		CallResult<Boolean> callResult  = null;
		long startTime = System.currentTimeMillis();
		try {
		// 保存商品库存		
				callResult = synInitAndAysnMysqlService.saveGoodsInventory(goodsId,inventoryInfoDO,selectionInventoryList,suppliersInventoryList);
				if(callResult==null) {
					return CreateInventoryResultEnum.SYS_ERROR;
				}
				PublicCodeEnum publicCodeEnum = callResult
						.getPublicCodeEnum();
				if (publicCodeEnum != PublicCodeEnum.SUCCESS
						/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
					// 消息数据不存并且不成功
					message = "saveGoodsInventory2Mysql_error[" + publicCodeEnum.getMessage()
							+ "]goodsId:" + goodsId;
				     return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
					
				} else {
					message = "saveGoodsInventory2MysqlAndRedis_success[save2mysqlAndRedis success]goodsId:" + goodsId;
					if(!StringUtils.isEmpty(tokenid)) {
						//处理成功后设置的一个tag
						goodsInventoryDomainRepository.setTag(DLockConstants.CREATE_INVENTORY_SUCCESS + "_"+ tokenid, DLockConstants.IDEMPOTENT_DURATION_TIME, DLockConstants.HANDLER_SUCCESS);
					}
					
				}
			
		} catch (Exception e) {
			/*this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"createInventory error" + e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
							"createInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("goodsId",goodsId)
					.addMetaData("inventoryInfoDO",inventoryInfoDO)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		
		return CreateInventoryResultEnum.SUCCESS;
		
	}
	/**
	 * 创建物流商品库存
	 * @param wmsDO
	 * @param selectionList
	 * @return
	 */
	public CreateInventoryResultEnum createWmsInventory(GoodsInventoryWMSDO wmsDO,List<GoodsSelectionDO> selectionList) {
		CallResult<Boolean> callResult  = null;
		String message = StringUtils.EMPTY;
		long startTime = System.currentTimeMillis();
		try {
			// 保存商品库存		
			callResult = synInitAndAysnMysqlService.saveGoodsWmsInventory(wmsDO,selectionList);
			PublicCodeEnum publicCodeEnum = callResult.getPublicCodeEnum();

			if (publicCodeEnum != PublicCodeEnum.SUCCESS
			/* && publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED) */) { // 当数据已经存在时返回true,为的是删除缓存中的队列数据
				// 消息数据不存并且不成功
			message = "saveGoodsWmsInventory.createWmsInventory2Mysql_error["
						+ publicCodeEnum.getMessage() + "]wmsGoodsId:" + wmsDO == null ? ""
						: wmsDO.getWmsGoodsId();
			return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
			} else { 
				message = "saveGoodsWmsInventory.createWmsInventory2Mysql_success[save2mysql success]wmsGoodsId:"
						+ wmsDO.getWmsGoodsId();
				

			}
			
		} catch (Exception e) {
			/*this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"createWmsInventory error" + e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"createWmsInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("wmsDO",wmsDO)
					.addMetaData("selectionList",selectionList)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		
		return CreateInventoryResultEnum.SUCCESS;	
	}
	/**
	 * 更新mysql库库存信息
	 * @param inventoryInfoDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 */
	public CreateInventoryResultEnum asynUpdateMysqlInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList,List<GoodsInventoryWMSDO> wmsInventoryList) {
	
		CallResult<Boolean> callResult  = null;
		String message = StringUtils.EMPTY;
		long startTime = System.currentTimeMillis();
		try {
		// 更新商品库存
		callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,inventoryInfoDO,selectionInventoryList,suppliersInventoryList,wmsInventoryList);
					PublicCodeEnum publicCodeEnum = callResult
							.getPublicCodeEnum();
					
					if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
						// 消息数据不存并且不成功
						message = "updateMysqlInventory2Mysql_error[" + publicCodeEnum.getMessage()
								+ "]goodsId:" + goodsId;
						return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
					} else {
						message = "updateMysqlInventory2mysql_success[save2mysql success]goodsId:" + goodsId;
					}
				//} 
		} catch (Exception e) {
			/*this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg:"+message,
							"updateMysqlInventory error " + e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"updateMysqlInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("goodsId",goodsId)
					.addMetaData("inventoryInfoDO",inventoryInfoDO)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("wmsInventoryList",wmsInventoryList)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		return CreateInventoryResultEnum.SUCCESS;	
		
	}
	/**
	 * 异步还库库存
	 * @param inventoryInfoDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 */
	public CreateInventoryResultEnum asynRestorUpdateInventory(long goodsId,long goodsBaseId,int limitStorage,int  deductNum,List<GoodsSelectionAndSuppliersResult> selectionParam,List<GoodsSelectionAndSuppliersResult> suppliersParam,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList,List<GoodsInventoryWMSDO> wmsInventoryList) {
		
		CallResult<Boolean> callResult  = null;
		String message = StringUtils.EMPTY;
		long startTime = System.currentTimeMillis();
		try {
			// 更新商品库存
			callResult = synInitAndAysnMysqlService.restoreGoodsInventory(goodsId,goodsBaseId,limitStorage,deductNum,selectionParam,suppliersParam,inventoryInfoDO,selectionInventoryList,suppliersInventoryList,wmsInventoryList);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
				// 消息数据不存并且不成功
				message = "asynRestorUpdateInventory_error[" + publicCodeEnum.getMessage()
						+ "]goodsId:" + goodsId;
				return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
			} else {
				message = "asynRestorUpdateInventory_success[restoreGoodsInventory success]goodsId:" + goodsId;
			}
			//} 
		} catch (Exception e) {
			/*this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg:"+message,
							"restoreGoodsInventory error " + e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"restoreGoodsInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("goodsId",goodsId)
					.addMetaData("inventoryInfoDO",inventoryInfoDO)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("wmsInventoryList",wmsInventoryList)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		return CreateInventoryResultEnum.SUCCESS;	
		
	}
	/**
	 * 商品改价更新老商品信息
	 * @param goodsId
	 * @param inventoryInfoDO
	 * @param selectionInventoryList
	 * @param suppliersInventoryList
	 * @return
	 */
	public CreateInventoryResultEnum updateInventory4GoodsCost(GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList) {
		
		CallResult<Boolean> callResult  = null;
		String message = StringUtils.EMPTY;
		long startTime = System.currentTimeMillis();
		try {
			// 更新商品库存
			callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,inventoryInfoDO,selectionInventoryList,suppliersInventoryList);
			PublicCodeEnum publicCodeEnum = callResult
					.getPublicCodeEnum();
			
			if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
				// 消息数据不存并且不成功
				message = "updateInventory4GoodsCost_error[" + publicCodeEnum.getMessage()
						+ "]goodsId:" + goodsId;
				return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
			} else {
				message = "updateInventory4GoodsCost_success[save success]goodsId:" + goodsId;
			}
			//} 
		} catch (Exception e) {
			/*this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg:"+message,
							"updateInventory4GoodsCost error " + e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"updateInventory4GoodsCost error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("goodsId",goodsId)
					.addMetaData("inventoryInfoDO",inventoryInfoDO)
					.addMetaData("selectionInventoryList",selectionInventoryList)
					.addMetaData("suppliersInventoryList",suppliersInventoryList)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		return CreateInventoryResultEnum.SUCCESS;	
		
	}
	//更新物流的库存信息
	public CreateInventoryResultEnum updateWmsMysqlInventory(GoodsInventoryWMSDO wmsDO, List<GoodsInventoryDO> wmsInventoryList,List<GoodsWmsSelectionResult> selectionList) {
		String message = StringUtils.EMPTY;
		CallResult<Boolean> callResult  = null;
		long startTime = System.currentTimeMillis();
		try {
			// 更新商品库存
			callResult = synInitAndAysnMysqlService.batchUpdateGoodsWms(wmsDO,wmsInventoryList, selectionList);
						PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();
						
						if (publicCodeEnum != PublicCodeEnum.SUCCESS
								/*&& publicCodeEnum.equals(PublicCodeEnum.DATA_EXISTED)*/) {  //当数据已经存在时返回true,为的是删除缓存中的队列数据
							// 消息数据不存并且不成功
							message = "updateBatchGoodsWms2Mysql_error[" + publicCodeEnum.getMessage()
									+ "]wmsGoodsId:" + wmsDO==null?"":wmsDO.getWmsGoodsId();
							return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
							
						} else {
							message = "updateMysqlInventory2mysql_success[save2mysql success]wmsDO:" + wmsDO;
						} 
		} catch (Exception e) {
			//handler = false;
			/*this.writeBusWmsUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"updateWmsMysqlInventory:"+message +","+ e.getMessage()),false,  e);*/
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"updateWmsMysqlInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}finally {
			logSysUpdate.info(lm.addMetaData("wmsDO",wmsDO)
					.addMetaData("wmsInventoryList",wmsInventoryList)
					.addMetaData("selectionList",selectionList)
					.addMetaData("message",message)
					.addMetaData("endTime", System.currentTimeMillis())
					.addMetaData("useTime", JsonUtils.getRunTime(startTime)).toJson(false));
		}
		return CreateInventoryResultEnum.SUCCESS;	
	}
	
	public void setWmsGoodsId(String wmsGoodsId) {
		this.wmsGoodsId = wmsGoodsId;
	}
	/*public void setSelIds(List<Long> selIds) {
		this.selIds = selIds;
	}*/
	public void setGoodsTypeIdList(List<Long> goodsTypeIdList) {
		this.goodsTypeIdList = goodsTypeIdList;
	}
	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	
	public void setSynInitAndAysnMysqlService(
			SynInitAndAysnMysqlService synInitAndAysnMysqlService) {
		this.synInitAndAysnMysqlService = synInitAndAysnMysqlService;
	}

	public void setLm(LogModel lm) {
		this.lm = lm;
	}
	
	public void setIsBeDelivery(String isBeDelivery) {
		this.isBeDelivery = isBeDelivery;
	}
	
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
	
	

}
