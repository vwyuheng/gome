package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.job.handle.InventoryInitAndUpdateHandle;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.WmsInventoryParam;

public class InventoryWmsUpdateDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private WmsInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private InventoryInitAndUpdateHandle inventoryInitAndUpdateHandle;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryWMSDO wmsDO;
	private List<GoodsSelectionModel> selectionList;
	//选型商品类型的id列表
	private List<Long> selGoodsTypeIds;
	//缓存选型的id
	private List<Long> selIds;
	private String wmsGoodsId;  //物流商品的一种编码
	//private Long goodsId;

	private boolean isEnough;
	private boolean isSelectionEnough = true;
	// 需扣减的商品库存
	private int wmsGoodsDeductNum = 0;
	private int wmsSelectionDeductNum = 0;
	// 原剩余库存
	private int orileftnum = 0;
	// 原总库存
	private int oritotalnum = 0;
	// 领域中缓存选型和分店原始库存和扣减库存的list
	private List<GoodsWmsSelectionResult> selectionParam;
	// 当前库存
	private boolean resultACK;
	private SequenceUtil sequenceUtil;

	public InventoryWmsUpdateDomain(String clientIp, String clientName,
			WmsInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	/**
	 * 处理选型库存
	 */
	private void selectionInventoryHandler() {
		try {
		if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // if1
			this.selectionList = param.getGoodsSelection();
			this.selectionParam = new ArrayList<GoodsWmsSelectionResult>();
			// this.selectionRelation = new
			// ArrayList<RedisGoodsSelectionRelationDO>();
			for (GoodsSelectionModel model : selectionList) { // for
				if (model.getId() != null && model.getId() > 0) { // if选型
					GoodsWmsSelectionResult selection = null;
					Long selectionId = Long.valueOf(model.getId());
					// 查询商品选型库存
					GoodsSelectionDO selectionDO = this.goodsInventoryDomainRepository
							.querySelectionRelationById(selectionId);
					if (selectionDO != null
							/*&& selectionDO.getLimitStorage() == 1*/) { //为了计算销量 不管是否限制库存的都要扣减
						// 扣减库存并返回扣减标识,计算库存并
						if ((selectionDO.getLeftNumber() + model.getNum()) <= 0) {
							// 该处为了保证只要有一个选型商品库存不足则返回库存不足
							this.isSelectionEnough = false;
						} else {
							selection = new GoodsWmsSelectionResult();
							//redis更新用
							selection.setId(model.getId());
							//mysql更新用
							selection.setGoodTypeId(model.getGoodTypeId());
							// 扣减的库存量
							selection.setLeftNum(model.getNum());
							selection.setTotalNum(model.getNum());
							// 选型库存，并且是库存充足时用
							this.selectionParam.add(selection);
							
						}

					}

				}// if

			}// for

		} // if selection
		} catch (Exception e) {
			isSelectionEnough = false;
			this.writeBusErrorLog(
					lm.setMethod("selectionInventoryHandler").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			
		}
	}

	

	private void calculateInventory() {
		// 再次查询物流商品库存信息[确保最新数据]
		this.wmsDO = this.goodsInventoryDomainRepository.queryGoodsInventoryWms(wmsGoodsId);
		// 商品本身扣减库存量
		int deductNum = param.getNum();
		// 原始库存
		this.orileftnum = wmsDO.getLeftNumber();
		this.oritotalnum = wmsDO.getTotalNumber();
		//赋值
		this.wmsGoodsDeductNum = deductNum;
		// 扣减库存并返回扣减标识,计算库存并
		if (((orileftnum+deductNum) >= 0)||(oritotalnum+deductNum>=0)) {
			this.isEnough = true;
			
		}
	}

	private boolean verifyInventory() {
		if (resultACK) {
			return true;
		} else {
			return false;
		}
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		// 初始化检查
		CreateInventoryResultEnum resultEnum =	this.initCheck();
		
		// 真正的库存更新业务处理
		try {
			// 商品选型处理
			this.selectionInventoryHandler();
			
			if(isSelectionEnough) {
				//商品库存扣减的计算
				this.calculateInventory();
			}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
			return resultEnum;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存
	public CreateInventoryResultEnum updateAdjustWmsInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			// 更新商品库存
			if (isEnough) {
				if(wmsDO!=null) {
					// 更新inventoryInfoDO对象的库存属性值
					this.wmsDO.setLeftNumber(this.orileftnum + wmsGoodsDeductNum);
					this.wmsDO.setTotalNumber(this.oritotalnum + wmsGoodsDeductNum);
				}
				if(wmsDO.getLeftNumber()<0||wmsDO.getTotalNumber()<0) {
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			
			}
			//
			//更新mysql
			boolean handlerResult = this.synUpdateMysqlInventory(wmsDO, selectionParam);
			if(!handlerResult) {
				return CreateInventoryResultEnum.FAIL_ADJUST_INVENTORY;
			}
			//redis
			// 扣减库存
			resultACK =  this.goodsInventoryDomainRepository
					.updateGoodsWmsInventory(wmsGoodsId, (wmsGoodsDeductNum));
			// 校验库存
			if (!verifyInventory()) {
				// 回滚库存
				this.goodsInventoryDomainRepository.updateGoodsWmsInventory(
						wmsGoodsId, (-wmsGoodsDeductNum));
				return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
			}
			// 更新选型库存
			if (isSelectionEnough) {
				resultACK = this.goodsInventoryDomainRepository
						.batchAdjustSelectionWms(selectionParam);
				// 校验库存
				if (!verifyInventory()) {
					// 回滚库存
					// 先回滚总的 再回滚选型的
					this.goodsInventoryDomainRepository.updateGoodsWmsInventory(wmsGoodsId, (-wmsGoodsDeductNum));
					this.goodsInventoryDomainRepository.batchrollbackSelectionWms(selectionParam);
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			}
			

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("createInventory").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	//初始化库存
	public CreateInventoryResultEnum initCheck() {
		       //初始化物流编码
				this.wmsGoodsId = param.getWmsGoodsId();
				InventoryInitDomain create = new InventoryInitDomain();
				//注入相关Repository
				create.setWmsGoodsId(wmsGoodsId);
				create.setGoodsTypeIdList(selGoodsTypeIds);
				create.setSelIds(selIds);
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
				create.setInventoryInitAndUpdateHandle(inventoryInitAndUpdateHandle);
				return create.business4WmsExecute();
			}
	
	//异步更新mysql商品库存
		public boolean synUpdateMysqlInventory(GoodsInventoryWMSDO wmsDO, List<GoodsWmsSelectionResult>  selectionParam) {
			InventoryInitDomain create = new InventoryInitDomain();
			//注入相关Repository
			create.setInventoryInitAndUpdateHandle(this.inventoryInitAndUpdateHandle);
			return create.updateWmsMysqlInventory(wmsDO, selectionParam);
		}
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			//updateActionDO.setGoodsId(goodsId);
			if (wmsDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_WMS
						.getDescription());
				updateActionDO.setOriginalInventory("leftnum:"+String
						.valueOf(orileftnum)+",totalnum:"+String
						.valueOf(oritotalnum));
				
				updateActionDO.setInventoryChange(StringUtil.strHandler(wmsGoodsDeductNum, wmsSelectionDeductNum, 0));
			}
			if (!CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_WMS_SELECTION
						.getDescription():updateActionDO.getBusinessType()+",选型："+ResultStatusEnum.GOODS_WMS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSelection(selectionList):updateActionDO.getItem()+",选型item："+StringUtil
						.getIdsStringSelection(selectionList));
				updateActionDO.setOriginalInventory(StringUtils.isEmpty(updateActionDO.getOriginalInventory())?JsonUtils.convertObjectToString(selectionParam):updateActionDO.getOriginalInventory()+",选型初始库存："+JsonUtils.convertObjectToString(selectionParam));
				updateActionDO.setInventoryChange(StringUtils.isEmpty(updateActionDO.getInventoryChange())?JsonUtils.convertObjectToString(selectionParam):updateActionDO.getInventoryChange()+",选型库存变化量："+JsonUtils.convertObjectToString(selectionParam));
			}
			
			updateActionDO.setActionType(ResultStatusEnum.ADJUST_WMSINVENTORY
					.getDescription());
			
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			
			updateActionDO.setContent(JsonUtils.convertObjectToString(param)); // 操作内容
			updateActionDO.setRemark("物流库存调整");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("fillInventoryUpdateActionDO")
					.addMetaData("errMsg", e.getMessage()), e);
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
		if (param.getId()==null) { //因为这个在商品的attrbuti表里有存
			return CreateInventoryResultEnum.INVALID_WMSID;
		}
		if (param.getId()!=null&&param.getId()<=0) { //因为这个在商品的attrbuti表里有存
			return CreateInventoryResultEnum.INVALID_WMSID;
		}
		if (StringUtils.isEmpty(param.getWmsGoodsId())) {
			return CreateInventoryResultEnum.INVALID_WMSGOODSID;
		}
		List<GoodsSelectionModel> selList = param.getGoodsSelection();
		// 校验物流商品选型id
		if (!CollectionUtils.isEmpty(selList)) {
			selGoodsTypeIds = new ArrayList<Long>();
			selIds = new ArrayList<Long>();
			for (GoodsSelectionModel model : selList) {
				if (model.getId() <= 0) {
					return CreateInventoryResultEnum.INVALID_SELECTIONID;
				}
				if (model.getGoodTypeId() <= 0) {
					return CreateInventoryResultEnum.INVALID_GOODSTYPEID;
				}
				selIds.add(model.getId());
				selGoodsTypeIds.add(model.getGoodTypeId());
			}

			if (CollectionUtils.isEmpty(selIds)||CollectionUtils.isEmpty(selGoodsTypeIds)) {
				return CreateInventoryResultEnum.INVALID_SELIDANDGOODSTYPEID;
			}

		}
		
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

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	

}
