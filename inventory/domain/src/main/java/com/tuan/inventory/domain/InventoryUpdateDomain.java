package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.repository.InitCacheDomainRepository;
import com.tuan.inventory.domain.repository.SynInitAndAsynUpdateDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.UpdateInventoryParam;

public class InventoryUpdateDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private UpdateInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	private SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryQueueDO queueDO;
	private GoodsInventoryDO inventoryInfoDO;

	private List<GoodsSelectionModel> selectionList;
	private List<GoodsSuppliersModel> suppliersList;
	// 初始化用
	//private List<GoodsSuppliersDO> suppliersInventoryList;
	//private List<GoodsSelectionDO> selectionInventoryList;
	private Long goodsId;
	private Long userId;
	private boolean isEnough;
	private boolean isSelectionEnough = true;
	private boolean isSuppliersEnough = true;
	// 是否需要初始化
	//private boolean isInit;
	// 需扣减的商品库存
	private int deductNum = 0;
	// 原库存
	private int originalGoodsInventory = 0;
	// 领域中缓存选型和分店原始库存和扣减库存的list
	private List<GoodsSelectionAndSuppliersResult> selectionParam;
	private List<GoodsSelectionAndSuppliersResult> suppliersParam;
	// 当前库存
	private long resultACK;
	private SequenceUtil sequenceUtil;

	public InventoryUpdateDomain(String clientIp, String clientName,
			UpdateInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	/**
	 * 处理选型库存
	 */
	private void selectionInventoryHandler() {
		if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // if1
			this.selectionList = param.getGoodsSelection();
			this.selectionParam = new ArrayList<GoodsSelectionAndSuppliersResult>();
			// this.selectionRelation = new
			// ArrayList<RedisGoodsSelectionRelationDO>();
			for (GoodsSelectionModel model : selectionList) { // for
				if (model.getId() != null && model.getId() > 0) { // if选型
					GoodsSelectionAndSuppliersResult selection = null;
					Long selectionId = Long.valueOf(model.getId());
					// 查询商品选型库存
					GoodsSelectionDO selectionDO = this.goodsInventoryDomainRepository
							.querySelectionRelationById(selectionId);
					if (selectionDO != null
							&& selectionDO.getLimitStorage() == 1) {
						// 扣减库存并返回扣减标识,计算库存并
						if ((selectionDO.getLeftNumber() - model.getNum()) <= 0) {
							// 该处为了保证只要有一个选型商品库存不足则返回库存不足
							this.isSelectionEnough = false;
						} else {
							selection = new GoodsSelectionAndSuppliersResult();
							selection.setId(model.getId());
							// 扣减的库存量
							selection.setGoodsInventory(model.getNum());
							selection.setOriginalGoodsInventory(selectionDO
									.getLeftNumber());
							// 选型库存，并且是库存充足时用
							this.selectionParam.add(selection);
							// 更新selectionDO对象的库存属性值
							selectionDO.setLeftNumber(selection
									.getOriginalGoodsInventory()
									- selection.getGoodsInventory());
							// this.selectionRelation.add(selectionDO);
						}

					}

				}// if

			}// for

		} // if selection
	}

	/**
	 * 处理分店库存
	 */
	public void suppliersInventoryHandler() {
		if (!CollectionUtils.isEmpty(param.getGoodsSuppliers())) { // if1
			this.suppliersList = param.getGoodsSuppliers();
			this.suppliersParam = new ArrayList<GoodsSelectionAndSuppliersResult>();
			for (GoodsSuppliersModel model : suppliersList) { // for
				if (model.getId() > 0) { // if分店
					GoodsSelectionAndSuppliersResult suppliers = null;
					Long suppliersId = Long.valueOf(model.getId());
					GoodsSuppliersDO suppliersDO = this.goodsInventoryDomainRepository
							.querySuppliersInventoryById(suppliersId);

					if (suppliersDO != null
							&& suppliersDO.getLimitStorage() == 1) {
						// 扣减库存并返回扣减标识,计算库存并
						if ((suppliersDO.getLeftNumber() - model.getNum()) <= 0) {
							// 该处为了保证只要有一个选型商品库存不足则返回库存不足
							this.isSuppliersEnough = false;

						} else {
							suppliers = new GoodsSelectionAndSuppliersResult();
							// 扣减的库存量
							suppliers.setId(model.getId());
							suppliers.setGoodsInventory(model.getNum());
							suppliers.setOriginalGoodsInventory(suppliersDO
									.getLeftNumber());
							this.suppliersParam.add(suppliers);
							// 更新selectionDO对象的库存属性值
							suppliersDO.setLeftNumber(suppliers
									.getOriginalGoodsInventory()
									- suppliers.getGoodsInventory());
						}

					}

				}// if
			}
		}
	}

	private void calculateInventory() {
		// 再次查询商品库存信息[确保最新数据]
		this.inventoryInfoDO = this.goodsInventoryDomainRepository
				.queryGoodsInventory(goodsId);
		// 扣减库存
		this.deductNum = param.getNum();
		// 原始库存
		this.originalGoodsInventory = inventoryInfoDO.getLeftNumber();
		// 扣减库存并返回扣减标识,计算库存并
		if ((this.originalGoodsInventory - this.deductNum) >= 0) {
			this.isEnough = true;
			// 更新inventoryInfoDO对象的库存属性值
			this.inventoryInfoDO.setLeftNumber(originalGoodsInventory
					- deductNum);
		}
	}

	private boolean verifyInventory() {
		if (resultACK >= 0) {
			return true;
		} else {
			return false;
		}
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		// 初始化检查
		this.initCheck();
		/*if (isInit) {
			this.init();
		}*/
		// 真正的库存更新业务处理
		try {// 计算部分
			this.calculateInventory();
			// 商品选型处理
			this.selectionInventoryHandler();
			// 商品分店处理
			this.suppliersInventoryHandler();
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}

		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存
	public CreateInventoryResultEnum updateInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
			// 更新商品库存
			if (isEnough) {
				// 扣减库存
				resultACK = this.goodsInventoryDomainRepository
						.updateGoodsInventory(goodsId, (-deductNum));
				// 校验库存
				if (!verifyInventory()) {
					// 回滚库存
					this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId, (deductNum));
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			}
			// 更新选型库存
			if (isSelectionEnough) {
				resultACK = this.goodsInventoryDomainRepository
						.updateSelectionInventory(selectionParam);
				// 校验库存
				if (!verifyInventory()) {
					// 回滚库存
					// 先回滚总的 再回滚选型的
					this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId, (deductNum));
					this.goodsInventoryDomainRepository
							.rollbackSelectionInventory(selectionParam);
					return CreateInventoryResultEnum.SHORTAGE_STOCK_INVENTORY;
				}
			}
			// 更新分店库存
			if (isSuppliersEnough) {
				resultACK = this.goodsInventoryDomainRepository
						.updateSuppliersInventory(suppliersParam);
				// 校验库存
				if (!verifyInventory()) {
					// 回滚库存
					// 先回滚总的 再回滚分店的
					this.goodsInventoryDomainRepository.updateGoodsInventory(
							goodsId, (deductNum));
					this.goodsInventoryDomainRepository
							.rollbackSuppliersInventory(suppliersParam);
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

	public void pushSendMsgQueue() {
		// 填充队列
		if (fillInventoryQueueDO()) {
			this.goodsInventoryDomainRepository.pushQueueSendMsg(queueDO);
		}
	}

	// 初始化检查
	/*public void initCheck() {
		this.goodsId = Long.valueOf(param.getGoodsId());
		if (goodsId > 0 ) {
		//if (goodsId > 0 && param.getLimitStorage() == 1) { // limitStorage>0:库存无限制；1：限制库存
			//boolean isExists = this.goodsInventoryDomainRepository
					//.isGoodsExists(goodsId);
			this.inventoryInfoDO = this.goodsInventoryDomainRepository.queryGoodsInventory(goodsId);
			//if (isExists) { // 不存在
			if(inventoryInfoDO==null) {  // 不存在
				// 初始化库存
				this.isInit = true;
				// 初始化商品库存信息
				this.inventoryInfoDO = this.initCacheDomainRepository
						.getInventoryInfoByGoodsId(goodsId);
				// 查询该商品分店库存信息
				selectionInventoryList = this.initCacheDomainRepository
						.querySelectionByGoodsId(goodsId);
				suppliersInventoryList = this.initCacheDomainRepository
						.selectGoodsSuppliersInventoryByGoodsId(goodsId);
			}
		}

	}*/

	/*public void init() {
		// 保存商品库存
		if (inventoryInfoDO != null)
			this.goodsInventoryDomainRepository.saveGoodsInventory(goodsId,
					inventoryInfoDO);
		// 保选型库存
		if (!CollectionUtils.isEmpty(selectionInventoryList))
			this.goodsInventoryDomainRepository.saveGoodsSelectionInventory(
					goodsId, selectionInventoryList);
		// 保存分店库存
		if (!CollectionUtils.isEmpty(suppliersInventoryList))
			this.goodsInventoryDomainRepository.saveGoodsSuppliersInventory(
					goodsId, suppliersInventoryList);
	}
*/
	//初始化库存
	public void initCheck() {
		this.goodsId = Long.valueOf(param.getGoodsId());
				InventoryInitDomain create = new InventoryInitDomain();
				//注入相关Repository
				create.setGoodsId(this.goodsId);
				create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
				create.setInitCacheDomainRepository(this.initCacheDomainRepository);
				create.setSynInitAndAsynUpdateDomainRepository(this.synInitAndAsynUpdateDomainRepository);
				create.busiCheck();
			}
	
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			if (inventoryInfoDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_SELF
						.getDescription());
				updateActionDO.setOriginalInventory(String
						.valueOf(originalGoodsInventory));
				updateActionDO.setInventoryChange(String
						.valueOf(deductNum));
			}
			if (!CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_SELECTION
						.getDescription():updateActionDO.getBusinessType()+",选型："+ResultStatusEnum.GOODS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSelection(selectionList):updateActionDO.getItem()+",选型item："+StringUtil
						.getIdsStringSelection(selectionList));
				updateActionDO.setOriginalInventory(StringUtils.isEmpty(updateActionDO.getOriginalInventory())?JsonUtils.convertObjectToString(selectionParam):updateActionDO.getOriginalInventory()+",选型初始库存："+JsonUtils.convertObjectToString(selectionParam));
				updateActionDO.setInventoryChange(StringUtils.isEmpty(updateActionDO.getInventoryChange())?JsonUtils.convertObjectToString(selectionParam):updateActionDO.getInventoryChange()+",选型库存变化量："+JsonUtils.convertObjectToString(selectionParam));
			}
			if (!CollectionUtils.isEmpty(suppliersList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription():updateActionDO.getBusinessType()+",分店："+ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSuppliers(suppliersList):updateActionDO.getItem()+",分店item："+StringUtil
						.getIdsStringSuppliers(suppliersList));
				updateActionDO.setOriginalInventory(StringUtils.isEmpty(updateActionDO.getOriginalInventory())?JsonUtils.convertObjectToString(suppliersParam):updateActionDO.getOriginalInventory()+",分店初始库存："+JsonUtils.convertObjectToString(suppliersParam));
				updateActionDO.setInventoryChange(StringUtils.isEmpty(updateActionDO.getInventoryChange())?JsonUtils.convertObjectToString(suppliersParam):updateActionDO.getInventoryChange()+",分店库存变化量："+JsonUtils.convertObjectToString(suppliersParam));
			}
			updateActionDO.setActionType(ResultStatusEnum.DEDUCTION_INVENTORY
					.getDescription());
			if(!StringUtils.isEmpty(param.getUserId())) {
				this.userId = (Long.valueOf(param.getUserId()));
			}
			updateActionDO.setUserId(userId);
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			if(!StringUtils.isEmpty(param.getOrderId())) {
				updateActionDO.setOrderId(Long.valueOf(param.getOrderId()));
			}
			updateActionDO.setContent(JsonUtils.convertObjectToString(param)); // 操作内容
			updateActionDO.setRemark("修改库存");
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
	 * 填充库存队列信息
	 * 
	 * @return
	 */
	public boolean fillInventoryQueueDO() {
		GoodsInventoryQueueDO queueDO = new GoodsInventoryQueueDO();
		try {
			queueDO.setId(sequenceUtil.getSequence(SEQNAME.seq_queue_send));
			queueDO.setGoodsId(goodsId);
			if(!StringUtils.isEmpty(param.getOrderId())) {
				queueDO.setOrderId(Long.valueOf(param.getOrderId()));
			}
			if(!StringUtils.isEmpty(param.getUserId())) {
				queueDO.setUserId(Long.valueOf(param.getUserId()));
			}
			queueDO.setCreateTime(TimeUtil.getNowTimestamp10Long());
			// 封装库存变化信息到队列
			queueDO.setOriginalGoodsInventory(originalGoodsInventory);
			queueDO.setDeductNum(deductNum);
			queueDO.setSuppliersParam(suppliersParam);
			queueDO.setSelectionParam(selectionParam);

		} catch (Exception e) {
			this.writeBusErrorLog(lm.setMethod("fillInventoryQueueDO")
					.addMetaData("errMsg", e.getMessage()), e);
			this.queueDO = null;
			return false;
		}
		this.queueDO = queueDO;
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
		/*if (StringUtils.isEmpty(param.getOrderId())) {
			return CreateInventoryResultEnum.INVALID_ORDER_ID;
		}*/
		return CreateInventoryResultEnum.SUCCESS;
	}

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}

	public void setInitCacheDomainRepository(
			InitCacheDomainRepository initCacheDomainRepository) {
		this.initCacheDomainRepository = initCacheDomainRepository;
	}

	public void setSynInitAndAsynUpdateDomainRepository(
			SynInitAndAsynUpdateDomainRepository synInitAndAsynUpdateDomainRepository) {
		this.synInitAndAsynUpdateDomainRepository = synInitAndAsynUpdateDomainRepository;
	}

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
