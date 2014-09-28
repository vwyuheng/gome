package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

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
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.DLockConstants;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.enu.PublicCodeEnum;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.OverrideAdjustInventoryParam;
import com.tuan.inventory.model.param.SelectionNotifyMessageParam;
import com.tuan.inventory.model.param.SuppliersNotifyMessageParam;
import com.tuan.inventory.model.result.CallResult;

public class InventoryOverrideAdjustDomain extends AbstractDomain {
	//private static Log logger = LogFactory.getLog("INVENTORY.INIT");
	protected static Log logger = LogFactory.getLog("SYS.UPDATERESULT.LOG");
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private OverrideAdjustInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	//private DLockImpl dLock;//分布式锁
	
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO preInventoryDO;
	private GoodsInventoryDO inventoryDO;
	private GoodsSelectionDO selectionInventory;
	private GoodsSuppliersDO suppliersInventory;
	//选型
	private List<SelectionNotifyMessageParam> selectionMsg;
	//分店
	private List<SuppliersNotifyMessageParam> suppliersMsg;
	private String goodsBaseId2str;
	private String goodsId2str;
	private String type;
	private String id;
	private String tokenid;  //redis序列,解决接口幂等问题
	private String businessType;
	private int limitStorage;  //是否限制库存
	private int preLimitStorage;  //调整前商品的是否限制库存标识
	
	// 调整前剩余库存
	private int preleftnum = 0;
	// 调整前总库存
	private int pretotalnum = 0;
	// 调整前选型或分店剩余库存
	private int preselOrSuppleftnum = 0;
	// 调整前选型或分店总库存
	private int preselOrSupptotalnum = 0;
	
	// 调整后剩余库存
	private int aftleftnum = 0;
	// 调整后总库存:传过来的
	private int afttotalnum = 0;
	// 选型或分店调整后剩余库存
	private int aftSelOrSuppleftnum = 0;
	// 调整后总库存
	private int aftSelOrSupptotalnum = 0;
	private Long goodsId;
	private Long goodsBaseId;
	private long selectionId;
	private long suppliersId;
	private String goodsSelectionIds = "";
	boolean idemptent = false;
	public boolean isSendMsg = false;
	
	public InventoryOverrideAdjustDomain(String clientIp, String clientName,
			OverrideAdjustInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		CreateInventoryResultEnum resultEnum = null;
		try {
			if (!StringUtils.isEmpty(tokenid)) { // if
				this.idemptent = idemptent();
				if(idemptent) {
					return CreateInventoryResultEnum.SUCCESS;
				}
			}
			
			long startTime = System.currentTimeMillis();
			if(logger.isDebugEnabled()) {
				logger.debug(lm.addMetaData("init start", startTime)
						.toJson(false));
			}
			
			//初始化检查
			resultEnum = this.initCheck("from_InventoryOverrideAdjustDomain");
			long endTime = System.currentTimeMillis();
			String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
					+ "milliseconds(毫秒)执行完成!";
			if(logger.isDebugEnabled()) {
				logger.debug(lm.addMetaData("endTime", endTime).addMetaData("goodsBaseId", goodsBaseId).addMetaData("goodsId", goodsId)
						.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription()).toJson(false));
			}
			
			if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
				return resultEnum;
			}
			//真正的库存调整业务处理
			if(goodsId!=null&&goodsId>0) {
				//查询商品库存
				GoodsInventoryDO inventoryDOTmp = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
				if(inventoryDOTmp!=null) {
					//初始化调整前商品的是否限制库存标识
					setPreLimitStorage(inventoryDOTmp.getLimitStorage());
					//this.preleftnum = inventoryDO.getLeftNumber();
					//this.pretotalnum = inventoryDO.getTotalNumber();
					//初始化调整前的库存和剩余库存
					if(inventoryDOTmp.getLimitStorage()==1) {
						setPreleftnum(inventoryDOTmp.getLeftNumber());
						setPretotalnum(inventoryDOTmp.getTotalNumber());
					}
					setInventoryDO(inventoryDOTmp);
					setPreInventoryDO(inventoryDOTmp); //保存现场
				}
			}
			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF.getDescription();
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION.getDescription();
				//查询商品选型库存
				this.selectionInventory = this.goodsInventoryDomainRepository.querySelectionRelationById(selectionId);
				if(selectionInventory!=null&&selectionInventory.getLimitStorage()==1) {
					this.preselOrSuppleftnum = selectionInventory.getLeftNumber();
					this.preselOrSupptotalnum = selectionInventory.getTotalNumber();
				}
				
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.suppliersId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SUPPLIERS.getDescription();
				//查询商品分店库存
				this.suppliersInventory = this.goodsInventoryDomainRepository.querySuppliersInventoryById(suppliersId);
				if(suppliersInventory!=null&&suppliersInventory.getLimitStorage()==1) {
					this.preselOrSuppleftnum = suppliersInventory.getLeftNumber();
					this.preselOrSupptotalnum = suppliersInventory.getTotalNumber();
				}
			}
			
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}
	//接口幂等处理
	public boolean idemptent() {
		//根据key取已缓存的tokenid  
		String gettokenid = goodsInventoryDomainRepository.queryToken(DLockConstants.OVERRIDE_ADJUST_INVENTORY + "_"+ String.valueOf(goodsId));
		if(StringUtils.isEmpty(gettokenid)) {  //如果为空则任务是初始的http请求过来，将tokenid缓存起来
			if(StringUtils.isNotEmpty(tokenid)) {
				goodsInventoryDomainRepository.setTag(DLockConstants.OVERRIDE_ADJUST_INVENTORY + "_"+ goodsId, DLockConstants.IDEMPOTENT_DURATION_TIME, tokenid);
			}
					
		}else {  //否则比对token值
			if(StringUtils.isNotEmpty(tokenid)) {
				if(tokenid.equalsIgnoreCase(gettokenid)) { //重复请求过来，判断是否处理成功
				//根据处理成功后设置的tag来判断之前http请求处理是否成功
				String gettag = goodsInventoryDomainRepository.queryToken(DLockConstants.OVERRIDE_ADJUST_INVENTORY_SUCCESS + "_"+ tokenid);
				if(!StringUtils.isEmpty(gettag)&&gettag.equalsIgnoreCase(DLockConstants.HANDLER_SUCCESS)) { 
								return true;
							}
						}
					}
		}
		return false;
	}
	
	
	// 库存系统新增库存 正数：+ 负数：-
	public CreateInventoryResultEnum adjustInventory() {
		String message = StringUtils.EMPTY;
		try {
			if(idemptent) {  //幂等控制，已处理成功
				return CreateInventoryResultEnum.SUCCESS;
			}
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);

			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
					if (inventoryDO != null) {
						//goodsSelectionIds = inventoryDO.getGoodsSelectionIds();
						setGoodsSelectionIds(inventoryDO.getGoodsSelectionIds());
						if(preLimitStorage==1&&limitStorage==0) { //由限制库存调整为非限制库存情况
							inventoryDO.setLimitStorage(0); // 更新数据库用
							inventoryDO.setLeftNumber(0);
							inventoryDO.setTotalNumber(0);
							///pretotalnum = 0;
						}else {
							//先计算调整量[可能为正也可能为负]
							int adjustnum = afttotalnum-pretotalnum;
							
							if(adjustnum==0&&limitStorage==preLimitStorage) {
								//不调整时消息也不能发
								setSendMsg(true);
								return CreateInventoryResultEnum.SUCCESS;
							}else if(adjustnum>=0) {
									// 剩余库存
									aftleftnum = preleftnum + adjustnum;
								
									if(preLimitStorage==0&&limitStorage==1) {  //由非限制库存调整为限制库存时必然是调增,此时计算总库存时要考虑进去销量
										afttotalnum = afttotalnum+inventoryDO.getGoodsSaleCount();
									}
							}else { //调减
								if((-adjustnum)>preleftnum) {
									return CreateInventoryResultEnum.NO_SUPPORT_ADJUST;
								}else {
									aftleftnum = preleftnum + adjustnum;//此时adjustnum<0
								}
								
							}
							
							// 调整后剩余库存数量
							inventoryDO.setLeftNumber(aftleftnum);
							// 调整商品总库存数量
							inventoryDO.setTotalNumber(afttotalnum);
							//清除选型关系
							inventoryDO.setWmsId(0l);
							inventoryDO.setGoodsSelectionIds("");
							if(goodsBaseId!=null&&goodsBaseId>0) {
								inventoryDO.setGoodsBaseId(goodsBaseId);
							}
							if (limitStorage == 1) {
								inventoryDO.setLimitStorage(1); // 更新数据库用
							}
						}
						
						
					}
					
					CallResult<GoodsInventoryDO> callResult  = null;
						
					if (goodsId>0&&inventoryDO != null) {
						// 消费对列的信息
						callResult = synInitAndAysnMysqlService.updateGoodsInventory(goodsId,pretotalnum,goodsSelectionIds,inventoryDO);
						PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();
						
						if (publicCodeEnum != PublicCodeEnum.SUCCESS) {  //
							// 消息数据不存并且不成功
							message = "oadjustInventory_error[" + publicCodeEnum.getMessage()
									+ "]goodsId:" + goodsId;
							return CreateInventoryResultEnum.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "uoadjustInventory_success[save success]goodsId:" + goodsId;
						}
						
					if(logger.isDebugEnabled()) {
						lm.addMetaData("oadjustInventory","oadjustInventory end").addMetaData("goodsId", goodsId).addMetaData("type", type).addMetaData("inventoryDO", inventoryDO.toString()).addMetaData("message", message);
						logger.debug(lm.toJson(false));
					}
					
					}
						
				} else if (type
						.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
								.getCode())) {
					if (selectionInventory.getLimitStorage() == 0&&selectionInventory.getTotalNumber() != 0) {
						inventoryDO.setLimitStorage(1); // 更新数据库用
						selectionInventory.setLimitStorage(1);
					} 
					
					if (selectionInventory != null) {
						//此时调整后数量检查
						if(aftSelOrSuppleftnum<0) {
							aftSelOrSuppleftnum = 0;
						}
						//计算剩余库存调整后的数量量
						//aftleftnum = adjustnum+preleftnum;
					
						//调整剩余库存数量
						selectionInventory.setLeftNumber(afttotalnum);
						//调整商品选型总库存数量
						selectionInventory.setTotalNumber(afttotalnum);
						//计算总的
						//afttotalnum =  pretotalnum + adjustnum;
						//计算剩余库存调整后的数量量
						//aftleftnum = adjustnum+preleftnum;
						
						if (limitStorage == 0) {
							afttotalnum = 0;
							inventoryDO.setLimitStorage(0);
							aftleftnum=0;
							afttotalnum=0;
						}
						//同时调整商品总的库存的剩余和总库存量
						//调整剩余库存数量
						inventoryDO.setLeftNumber(aftleftnum);
						//调整商品总库存数量
						inventoryDO.setTotalNumber(afttotalnum);
					}

					CallResult<GoodsSelectionDO> callResult = null;

					if (inventoryDO != null && selectionInventory != null) {
						lm.addMetaData("oadjustInventory",
								"oadjustInventory start")
								.addMetaData("goodsId", goodsId)
								.addMetaData("type", type)
								.addMetaData("inventoryDO", inventoryDO)
								.addMetaData("selectionInventory",
										selectionInventory);
						logger.info(lm.toJson(false));
						// 消费对列的信息
						callResult = synInitAndAysnMysqlService
								.updateGoodsSelection(inventoryDO,pretotalnum,
										selectionInventory);
						PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();

						if (publicCodeEnum != PublicCodeEnum.SUCCESS) { //
							// 消息数据不存并且不成功
							message = "updateGoodsSelection_error["
									+ publicCodeEnum.getMessage()
									+ "]selectionId:"
									+ selectionInventory.getId();
							return CreateInventoryResultEnum
									.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "oadjustInventorySelection_success[save success]selectionId:"
									+ selectionInventory.getId();
						}
						lm.addMetaData("oadjustInventory",
								"oadjustInventory mysql,end")
								.addMetaData("goodsId", goodsId)
								.addMetaData("type", type)
								.addMetaData("inventoryDO", inventoryDO)
								.addMetaData("selectionInventory",
										selectionInventory)
								.addMetaData("message",
										message);
						logger.info(lm.toJson(false));
					}

				} else if (type
						.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
								.getCode())) {
					if (suppliersInventory != null) {
						if (suppliersInventory.getLimitStorage() == 0&&suppliersInventory.getTotalNumber() != 0) {
							inventoryDO.setLimitStorage(1); // 更新数据库用
							suppliersInventory.setLimitStorage(1);
						} 
						//调整的总量
						int adjustnum = aftSelOrSupptotalnum - preselOrSupptotalnum;
						//计算剩余库存调整后的数量量
						aftSelOrSuppleftnum = adjustnum + preselOrSuppleftnum;
						//此时调整后数量检查
						if(aftSelOrSuppleftnum<0) {
							aftSelOrSuppleftnum = 0;
						}
						//计算剩余库存调整后的数量量
						aftleftnum = adjustnum+preleftnum;
						//此时调整后数量检查
						if(aftleftnum<0) {
							aftleftnum = 0;
						}
						//调整剩余库存数量
						suppliersInventory.setLeftNumber(aftSelOrSuppleftnum);
						//调整商品分店总库存数量
						suppliersInventory.setTotalNumber(aftSelOrSupptotalnum);
						afttotalnum =  pretotalnum + adjustnum;
						//计算剩余库存调整后的数量量
						aftleftnum = adjustnum+preleftnum;
						//此时调整后数量检查
						if(aftleftnum<0) {
							aftleftnum = 0;
						}
						if (limitStorage == 0) {
							afttotalnum = 0;
							inventoryDO.setLimitStorage(0);
							aftleftnum=0;
							afttotalnum=0;
						}
						//同时调整商品总的库存的剩余和总库存量
						//调整剩余库存数量
						inventoryDO.setLeftNumber(aftleftnum);
						//调整商品总库存数量
						inventoryDO.setTotalNumber(afttotalnum);
					}
					
					CallResult<GoodsSuppliersDO> callResult = null;

					if (inventoryDO != null && suppliersInventory != null) {
						lm.addMetaData("oadjustInventory",
								"oadjustInventory suppliers mysql,start")
								.addMetaData("goodsId", goodsId)
								.addMetaData("type", type)
								.addMetaData("inventoryDO", inventoryDO)
								.addMetaData("suppliersInventory",
										suppliersInventory);
						logger.info(lm.toJson(false));
						// 消费对列的信息:未加redis，目前公司业务只有商品的库存全量调整
						callResult = synInitAndAysnMysqlService
								.updateGoodsSuppliers(inventoryDO,
										suppliersInventory);
						PublicCodeEnum publicCodeEnum = callResult
								.getPublicCodeEnum();

						if (publicCodeEnum != PublicCodeEnum.SUCCESS) { //
							// 消息数据不存并且不成功
							message = "oadjustInventorySuppliers_error["
									+ publicCodeEnum.getMessage()
									+ "]suppliersId:" + suppliersInventory.getSuppliersId();
							return CreateInventoryResultEnum
									.valueOfEnum(publicCodeEnum.getCode());
						} else {
							message = "oadjustInventorySuppliers_success[save success]suppliersId:"
									+ suppliersInventory.getSuppliersId();
						}
						lm.addMetaData("oadjustInventory",
								"oadjustInventory mysql,end")
								.addMetaData("goodsId", goodsId)
								.addMetaData("type", type)
								.addMetaData("inventoryDO", inventoryDO)
								.addMetaData("suppliersInventory",
										suppliersInventory)
								.addMetaData("message", message);
						logger.info(lm.toJson(false));
					}

				}//else SUPPLIERS


		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"adjustInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		if(!StringUtils.isEmpty(tokenid)) {
			//先预留处理
			//String gettag = goodsInventoryDomainRepository.queryToken(DLockConstants.OVERRIDE_ADJUST_INVENTORY_SUCCESS + "_"+ tokenid);
			//if(!StringUtils.isEmpty(gettag)&&!gettag.equalsIgnoreCase(DLockConstants.HANDLER_SUCCESS)) { 
				
			//}
			//处理成返回前设置tag
			goodsInventoryDomainRepository.setTag(DLockConstants.OVERRIDE_ADJUST_INVENTORY_SUCCESS + "_"+ tokenid, DLockConstants.IDEMPOTENT_DURATION_TIME, DLockConstants.HANDLER_SUCCESS);
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	//发送库存新增消息
		public void sendNotify(){
			try {
				InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
				JSONObject json = JSONObject.fromObject(notifyParam);
				lm.addMetaData("InventoryOverrideAdjustDomain_sendNotify", json.toString());
				logger.info(lm.toJson(false));
				goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryOverrideAdjustDomain.toString(),json);
				
			} catch (Exception e) {
				logger.error(lm.addMetaData("errorMsg",
						"sendNotify error" + e.getMessage()).toJson(false), e);
			}
		}
		// 初始化参数
		private void fillParam() {
			this.tokenid = param.getTokenid();
			this.goodsBaseId2str = param.getGoodsBaseId();
			//商品id，必传参数，该参数无论是选型还是分店都要传过来
			this.goodsId2str = param.getGoodsId();
			// 2:商品 4：选型 6：分店
			this.type = param.getType();
			// 2：可不传 4：选型id 6 分店id
			this.id = param.getId();
			//this.limitStorage = param.getLimitStorage();
			setLimitStorage(param.getLimitStorage());
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.afttotalnum = param.getTotalnum();
			}else if (type
					.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
							.getCode())) {
				this.aftSelOrSupptotalnum = param.getTotalnum();
			}else if (type
					.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
							.getCode())) {
				this.aftSelOrSupptotalnum = param.getTotalnum();
			}
			
			
		}
		//填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam(){
			InventoryNotifyMessageParam notifyParam = null;
			try {
				notifyParam = new InventoryNotifyMessageParam();
				notifyParam.setUserId(Long.valueOf(param!=null&&StringUtils.isNotEmpty(param.getUserId())?param.getUserId():"0"));
				notifyParam.setGoodsBaseId(goodsBaseId);
				notifyParam.setGoodsId(goodsId);
				if(inventoryDO!=null) {
					notifyParam.setLimitStorage(inventoryDO.getLimitStorage());
					notifyParam.setWaterfloodVal(inventoryDO.getWaterfloodVal());
					notifyParam.setTotalNumber(afttotalnum);
					notifyParam.setLeftNumber(aftleftnum);
					//库存总数 减 库存剩余
					Integer sales = inventoryDO.getGoodsSaleCount();
					//销量
					notifyParam.setSales(sales==null?0:sales);
				}
				//发送库存基表信息
				GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
				if(baseInventoryDO!=null) {
					notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
					notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
				}
				if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
						.getCode()) && selectionInventory != null) {
					this.fillSelectionMsg();
					if (!CollectionUtils.isEmpty(selectionMsg)) {
	
						notifyParam.setSelectionRelation(selectionMsg);
					}
				}
	
				if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
						.getCode()) && suppliersInventory != null) {
					this.fillSuppliersMsg();
					if (!CollectionUtils.isEmpty(suppliersMsg)) {
	
						notifyParam.setSuppliersRelation(suppliersMsg);
					}
				}
				
			} catch (Exception e) {
				logger.error(lm.addMetaData("errorMsg",
						"fillInventoryNotifyMessageParam error" + e.getMessage()).toJson(false), e);
			}
			return notifyParam;
		}
		//初始化库存
		public CreateInventoryResultEnum initCheck(String initFromDesc) {
			this.fillParam();
			this.goodsId =  StringUtils.isEmpty(goodsId2str)?0:Long.valueOf(goodsId2str);
			if(StringUtils.isEmpty(goodsBaseId2str)&&goodsId!=0) {  //为了兼容参数goodsbaseid不传的情况
				GoodsInventoryDO temp = this.goodsInventoryDomainRepository
						.queryGoodsInventory(goodsId);
				if(temp!=null) {
					this.goodsBaseId = temp.getGoodsBaseId();
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
				this.goodsBaseId = Long.valueOf(goodsBaseId2str);
			}
			
			CreateInventoryResultEnum resultEnum = null;

				InventoryInitDomain create = new InventoryInitDomain(goodsId,
						lm);
				//注入相关Repository
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
				create.setInitFromDesc(initFromDesc);
				resultEnum = create.businessExecute();
			
			return resultEnum;
		}
		
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsBaseId(goodsBaseId);
			updateActionDO.setGoodsId(goodsId);
			updateActionDO.setBusinessType(businessType);
			updateActionDO.setItem(id);
			updateActionDO.setOriginalInventory(StringUtil.handlerOriInventory(type, preleftnum, pretotalnum, preselOrSuppleftnum, preselOrSupptotalnum));
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				updateActionDO.setInventoryChange(String.valueOf(afttotalnum));
			}else if (type
					.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
							.getCode())) {
				updateActionDO.setInventoryChange(String.valueOf(aftSelOrSupptotalnum));
			}else if (type
					.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
							.getCode())) {
				updateActionDO.setInventoryChange(String.valueOf(aftSelOrSupptotalnum));
			}
			
			updateActionDO.setActionType(ResultStatusEnum.OR_ADJUST_INVENTORY
					.getDescription());
			if(!StringUtils.isEmpty(param.getUserId())) {
				updateActionDO.setUserId(Long.valueOf(param!=null&&StringUtils.isNotEmpty(param.getUserId())?param.getUserId():"0"));
			}
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			//updateActionDO.setOrderId(0l);
			updateActionDO
					.setContent("preInventoryDO:"+(preInventoryDO!=null?JSON.toJSONString(preInventoryDO):"preInventoryDO is null!")+",param:"+JSON.toJSONString(param)); // 操作内容
			updateActionDO.setRemark("全量调整库存");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
			
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"fillInventoryUpdateActionDO error" + e.getMessage()).toJson(false), e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}
	
	
	public void fillSelectionMsg() {
		List<SelectionNotifyMessageParam> selectionMsg = new ArrayList<SelectionNotifyMessageParam>();
		SelectionNotifyMessageParam selMsg = new SelectionNotifyMessageParam();
		try {
			//selMsg.setGoodTypeId(selectionInventory.getGoodTypeId());
			selMsg.setGoodsId(goodsId);
			selMsg.setId(selectionId);
			selMsg.setLeftNumber(this.aftSelOrSuppleftnum);  //调整后的库存值
			selMsg.setTotalNumber(this.aftSelOrSupptotalnum);
			selMsg.setUserId(Long.valueOf(param!=null&&StringUtils.isNotEmpty(param.getUserId())?param.getUserId():"0"));
			
			selMsg.setLimitStorage(selectionInventory.getLimitStorage());
			selMsg.setWaterfloodVal(selectionInventory.getWaterfloodVal());
			selMsg.setWmsGoodsId(selectionInventory.getWmsGoodsId());
			selectionMsg.add(selMsg);
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"fillSelectionMsg error" + e.getMessage()).toJson(false), e);
			this.selectionMsg = null;
		}
		this.selectionMsg = selectionMsg;
	}
	public void fillSuppliersMsg() {
		List<SuppliersNotifyMessageParam> suppliersMsg = new ArrayList<SuppliersNotifyMessageParam>();
		SuppliersNotifyMessageParam supMsg = new SuppliersNotifyMessageParam();
		try {
			//supMsg.setSuppliersId(suppliersInventory.getSuppliersId());
			supMsg.setGoodsId(goodsId);
			supMsg.setId(suppliersId);
			supMsg.setLeftNumber(this.aftSelOrSuppleftnum);  //调整后的库存值
			supMsg.setTotalNumber(this.aftSelOrSupptotalnum);
			supMsg.setUserId(Long.valueOf(param!=null&&StringUtils.isNotEmpty(param.getUserId())?param.getUserId():"0"));
			//supMsg.setLimitStorage(suppliersInventory.getLimitStorage());
			supMsg.setWaterfloodVal(suppliersInventory.getWaterfloodVal());
			suppliersMsg.add(supMsg);
		} catch (Exception e) {
			logger.error(lm.addMetaData("errorMsg",
					"fillSuppliersMsg error" + e.getMessage()).toJson(false), e);
			this.suppliersMsg = null;
		}
		this.suppliersMsg = suppliersMsg;
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
		if (StringUtils.isEmpty(param.getType())) {
			return CreateInventoryResultEnum.INVALID_TYPE;
		}
		//简单的校验检查
		if (param.getType().equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())&&StringUtils.isEmpty(param.getId())) {
				return CreateInventoryResultEnum.INVALID_SELECTIONID;
			}
		if (param.getType().equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())&&StringUtils.isEmpty(param.getId())) {
				return CreateInventoryResultEnum.INVALID_SUPPLIERSID;
			}
		//构建校验领域:TODO 根据cms后台的要求，目前暂时注掉该校验逻辑，增加将修改库存时，清除下物流关系处理
		//GoodsVerificationDomain vfDomain = new GoodsVerificationDomain(Long.valueOf(param.getGoodsId()),param.getType(),param.getId());
		//注入仓储对象
		//vfDomain.setGoodsInventoryDomainRepository(goodsInventoryDomainRepository);
		//return vfDomain.checkSelOrSupp();
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
	public boolean isSendMsg() {
		return isSendMsg;
	}
	public void setSendMsg(boolean isSendMsg) {
		this.isSendMsg = isSendMsg;
	}
	public int getPreLimitStorage() {
		return preLimitStorage;
	}
	public void setPreLimitStorage(int preLimitStorage) {
		this.preLimitStorage = preLimitStorage;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	public int getPreleftnum() {
		return preleftnum;
	}
	public void setPreleftnum(int preleftnum) {
		this.preleftnum = preleftnum;
	}
	public int getPretotalnum() {
		return pretotalnum;
	}
	public void setPretotalnum(int pretotalnum) {
		this.pretotalnum = pretotalnum;
	}
	public GoodsInventoryDO getInventoryDO() {
		return inventoryDO;
	}
	public void setInventoryDO(GoodsInventoryDO inventoryDO) {
		this.inventoryDO = inventoryDO;
	}
	public String getGoodsSelectionIds() {
		return goodsSelectionIds;
	}
	public void setGoodsSelectionIds(String goodsSelectionIds) {
		this.goodsSelectionIds = goodsSelectionIds;
	}
	public GoodsInventoryDO getPreInventoryDO() {
		return preInventoryDO;
	}
	public void setPreInventoryDO(GoodsInventoryDO preInventoryDO) {
		this.preInventoryDO = preInventoryDO;
	}

}
