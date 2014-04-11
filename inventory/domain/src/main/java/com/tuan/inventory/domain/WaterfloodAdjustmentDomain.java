package com.tuan.inventory.domain;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.domain.repository.InitCacheDomainRepository;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.AdjustWaterfloodParam;

public class WaterfloodAdjustmentDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private AdjustWaterfloodParam param;
	private GoodsInventoryDomainRepository updateInventoryDomainRepository;
	private InitCacheDomainRepository initCacheDomainRepository;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryDO inventoryDO;
	private GoodsSelectionDO selectionInventory;
	private GoodsSuppliersDO suppliersInventory;
	//初始化用
	private List<GoodsSuppliersDO> suppliersInventoryList;
	private List<GoodsSelectionDO> selectionInventoryList;
	private String type;
	private String id;
	//注水调整值
	private int adjustNum;
	private String businessType;
	// 原注水
	private int originalWaterfloodVal = 0;
	private Long goodsId;
	private Long selectionId;
	private Long suppliersId;
	//调整后库存
	private long resultACK;
	private SequenceUtil sequenceUtil;
	//是否需要初始化
	private boolean isInit;
	
	public WaterfloodAdjustmentDomain(String clientIp, String clientName,
			AdjustWaterfloodParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		try {
			// 初始化检查
			this.initCheck();
			if (isInit) {
				this.init();
			}
			// 真正的业务检查处理
			if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.businessType = ResultStatusEnum.GOODS_SELF
						.getDescription();
				if (inventoryDO != null) {
					this.originalWaterfloodVal = inventoryDO.getWaterfloodVal();
				} else {
					return CreateInventoryResultEnum.IS_EXISTED;
				}
			} else if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION
					.getCode())) {
				this.selectionId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SELECTION
						.getDescription();
				// 查询商品选型库存
				this.selectionInventory = this.updateInventoryDomainRepository
						.querySelectionRelationById(selectionId);
				if (selectionInventory != null) {
					this.originalWaterfloodVal = selectionInventory
							.getWaterfloodVal();
				} else {
					return CreateInventoryResultEnum.IS_EXISTED;
				}
			} else if (type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS
					.getCode())) {
				this.suppliersId = Long.valueOf(id);
				this.businessType = ResultStatusEnum.GOODS_SUPPLIERS
						.getDescription();
				// 查询商品分店库存
				this.suppliersInventory = this.updateInventoryDomainRepository
						.querySuppliersInventoryById(suppliersId);
				if (suppliersInventory != null) {
					this.originalWaterfloodVal = suppliersInventory
							.getWaterfloodVal();
				} else {
					return CreateInventoryResultEnum.IS_EXISTED;
				}
			}
		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("busiCheck").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 调整注水
	public CreateInventoryResultEnum adjustWaterfloodVal() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.updateInventoryDomainRepository.pushLogQueues(updateActionDO);

			if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELF.getCode())) {
				this.resultACK = this.updateInventoryDomainRepository.adjustGoodsWaterflood(goodsId, (adjustNum));
				if(!verifyWaterflood()) {
					//将注水还原到调整前
					this.updateInventoryDomainRepository.adjustGoodsWaterflood(goodsId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SELECTION.getCode())) {
				this.resultACK = this.updateInventoryDomainRepository.adjustSelectionWaterfloodById(selectionId, (adjustNum));
				if(!verifyWaterflood()) {
					//将注水还原到调整前
					this.updateInventoryDomainRepository.adjustSelectionWaterfloodById(selectionId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
			}else if(type.equalsIgnoreCase(ResultStatusEnum.GOODS_SUPPLIERS.getCode())) {
				this.resultACK = this.updateInventoryDomainRepository.adjustSuppliersWaterfloodById(suppliersId, (adjustNum));
				if(!verifyWaterflood()) {
					//将注水还原到调整前
					this.updateInventoryDomainRepository.adjustSuppliersWaterfloodById(suppliersId, (-adjustNum));
					return CreateInventoryResultEnum.FAIL_ADJUST_WATERFLOOD;
				}
			}

		} catch (Exception e) {
			this.writeBusErrorLog(
					lm.setMethod("adjustWaterfloodVal").addMetaData("errorMsg",
							"DB error" + e.getMessage()), e);
			return CreateInventoryResultEnum.DB_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	// 初始化参数
	private void fillParam() {
		// 2:商品 4：选型 6：分店
		this.type = param.getType();
		// 2：商品id 4：选型id 6 分店id
		this.id = param.getId();
		this.adjustNum = param.getNum();
		
	}
	//初始化检查
	public void initCheck() {
		this.fillParam();
		this.goodsId = Long.valueOf(id);
		//查询商品库存
		this.inventoryDO = this.updateInventoryDomainRepository.queryGoodsInventory(goodsId);
		if(inventoryDO==null) {
			//初始化库存
			this.isInit = true;
			//初始化商品库存信息
			this.inventoryDO = this.initCacheDomainRepository
					.getInventoryInfoByGoodsId(goodsId);
			//查询该商品分店库存信息
			selectionInventoryList = this.initCacheDomainRepository.querySelectionByGoodsId(goodsId);
			suppliersInventoryList =  this.initCacheDomainRepository.selectGoodsSuppliersInventoryByGoodsId(goodsId);
		}
	}
	public void init() {
		//保存商品库存
		if(inventoryDO!=null)
		      this.updateInventoryDomainRepository.saveGoodsInventory(goodsId, inventoryDO);
		//保选型库存
		if(!CollectionUtils.isEmpty(selectionInventoryList))
		      this.updateInventoryDomainRepository.saveGoodsSelectionInventory(goodsId, selectionInventoryList);
		//保存分店库存
		if(!CollectionUtils.isEmpty(suppliersInventoryList))
		      this.updateInventoryDomainRepository.saveGoodsSuppliersInventory(goodsId, suppliersInventoryList);
	}
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			updateActionDO.setBusinessType(businessType);
			updateActionDO.setItem(id);
			updateActionDO.setOriginalInventory(String
					.valueOf(originalWaterfloodVal));
			updateActionDO.setInventoryChange(String.valueOf(adjustNum));
			updateActionDO.setActionType(ResultStatusEnum.ADJUST_WATERFLOOD
					.getDescription());
			updateActionDO.setUserId(Long.valueOf(param.getUserId()));
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			//updateActionDO.setOrderId(0l);
			updateActionDO
					.setContent(JSONObject.fromObject(param).toString()); // 操作内容
			updateActionDO.setRemark("注水调整");
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
	private boolean verifyWaterflood() {
		if(resultACK>=0) {
			return true;
		}else {
			return false;
		}
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
		if (StringUtils.isEmpty(param.getId())) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getType())) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	public void setUpdateInventoryDomainRepository(
			GoodsInventoryDomainRepository updateInventoryDomainRepository) {
		this.updateInventoryDomainRepository = updateInventoryDomainRepository;
	}

	public void setInitCacheDomainRepository(
			InitCacheDomainRepository initCacheDomainRepository) {
		this.initCacheDomainRepository = initCacheDomainRepository;
	}

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public Long getGoodsId() {
		return goodsId;
	}

}
