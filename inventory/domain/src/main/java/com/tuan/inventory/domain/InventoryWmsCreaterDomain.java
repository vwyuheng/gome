package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;
import com.tuan.inventory.model.param.WmsInventoryParam;

public class InventoryWmsCreaterDomain extends AbstractDomain {
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private WmsInventoryParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private SequenceUtil sequenceUtil;
	private GoodsInventoryActionDO updateActionDO;
	private GoodsInventoryWMSDO wmsDO;
	private List<GoodsSelectionDO> selectionRelation;

	private List<GoodsSelectionModel> selectionList;

	private String wmsGoodsId;  //物流商品的一种编码
	// 物流商品库存是否存在
	boolean isExists = false;
	// 新增选型
	boolean addSelection = false;

	public InventoryWmsCreaterDomain(String clientIp, String clientName,
			WmsInventoryParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}
	/***
	 * 业务处理前的预处理
	 */
	public void preHandler() {

		this.wmsGoodsId = param.getWmsGoodsId();
		// 物流商品库存是否存在
		isExists = this.goodsInventoryDomainRepository.isWmsExists(wmsGoodsId);
		if (isExists) { // 不存在
			// 根据接口参数填充物流商品库存信息
			this.fillGoodsWmsDO();
			// 填充物流商品选型库信息
			this.fillWmsSelection();
			
		} else { // 物流商品库存已存在,则可能新增其下选型
			if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // 选型库存
				addSelection = true;
			}
			
		}
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {

		try {
			// 业务检查前的预处理
			this.preHandler();

			if (addSelection) { // 选型库存
				// 填充物流商品选型库库存信息
				this.fillWmsSelection();

			}
			
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"busiCheck error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		if (!isExists && !addSelection) {
			return CreateInventoryResultEnum.IS_EXISTED;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 新增物流库存
	public CreateInventoryResultEnum createWmsInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);

		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.addMetaData("errorMsg",
							"createWmsInventory error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		//保存库存
		return this.saveInventory();
	}

	public CreateInventoryResultEnum saveInventory() {
		InventoryInitDomain create = new InventoryInitDomain();
		create.setWmsGoodsId(wmsGoodsId);
		//注入相关Repository
		create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
		create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		return create.createWmsInventory(wmsDO, selectionRelation);
	}
	


	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			//updateActionDO.setGoodsId(goodsId);
			if (isExists && wmsDO != null) {
				updateActionDO.setBusinessType(ResultStatusEnum.GOODS_WMS
						.getDescription());
				updateActionDO
						.setOriginalInventory(String
								.valueOf(param.getLeftNumber()));
				updateActionDO
						.setInventoryChange(String
								.valueOf(param.getLeftNumber()));
			}
			if (addSelection && !CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_WMS_SELECTION
						.getDescription():updateActionDO.getBusinessType()+",选型："+ResultStatusEnum.GOODS_WMS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?StringUtil
						.getIdsStringSelection(selectionList):updateActionDO.getItem()+",选型item："+StringUtil
						.getIdsStringSelection(selectionList));
			}
			
			updateActionDO.setActionType(ResultStatusEnum.ADD_WMSINVENTORY
					.getDescription());
			
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			//updateActionDO.setOrderId(0l);
			updateActionDO.setContent(JSONObject.fromObject(param).toString()); // 操作内容
			updateActionDO.setRemark("新增物流库存");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillInventoryUpdateActionDO error" + e.getMessage()),false, e);
			this.updateActionDO = null;
			return false;
		}
		this.updateActionDO = updateActionDO;
		return true;
	}

	// 参数检查
	public CreateInventoryResultEnum checkParam() {
		if (param == null) {
			return CreateInventoryResultEnum.INVALID_PARAM;
		}
		if (StringUtils.isEmpty(param.getWmsGoodsId())) {
			return CreateInventoryResultEnum.INVALID_WMSGOODSID;
		}

		return CreateInventoryResultEnum.SUCCESS;
	}

	// 商品选型库存
	public void fillWmsSelection() {
		try {
			selectionList = param.getGoodsSelection();
			if (!CollectionUtils.isEmpty(selectionList)) {
				selectionRelation = new ArrayList<GoodsSelectionDO>();
				for (GoodsSelectionModel model : selectionList) {
					if (model.getId() != null && model.getId() > 0) {
						GoodsSelectionDO selection = new GoodsSelectionDO();
						//long goodsId = 0;
						//在根据商品选型类型找到商品id
						/*//CallResult<Long> goodsIdResult =synInitAndAysnMysqlService.selectGoodsId(model.getGoodTypeId());
						if (goodsIdResult == null || !goodsIdResult.isSuccess()) {
							return CreateInventoryResultEnum.QUERY_ERROR;
						}else {
							goodsId = 	goodsIdResult.getBusinessResult();
						}
						//将商品id set到选型中
						if(goodsId>0) {
							selection.setGoodsId(goodsId);
						}*/
						//注，物流那边插入时无法拿到商品id
						selection.setId(model.getId());
						selection.setLeftNumber(model.getLeftNumber());
						selection.setTotalNumber(model.getTotalNumber());
						selection.setLimitStorage(model.getLimitStorage());
						//selection.setWaterfloodVal(model.getWaterfloodVal());
						selection.setGoodTypeId(model.getGoodTypeId());
						//selection.setUserId(userId);
						selectionRelation.add(selection);
					}

				}

			}

		} catch (Exception e) {
			this.writeBusUpdateErrorLog(
					lm.setMethod("fillWmsSelection").addMetaData(
							"errMsg", e.getMessage()),false, e);
			this.selectionRelation = null;
			
		}
	}

	
	//填充物流库存信息
	public void fillGoodsWmsDO() {
		GoodsInventoryWMSDO wmsDO = new GoodsInventoryWMSDO();
		try {
			wmsDO.setId(param.getId());
			wmsDO.setLeftNumber(param.getLeftNumber());
			wmsDO.setTotalNumber(param.getTotalNumber());
			wmsDO.setWmsGoodsId(wmsGoodsId);
			wmsDO.setGoodsName(param.getGoodsName());
			wmsDO.setGoodsSupplier(param.getGoodsSupplier());
			wmsDO.setIsBeDelivery(param.getIsBeDelivery());
			//wmsDO.setLimitStorage(param.getLimitStorage());
			//wmsDO.setUserId(userId);
			

		} catch (Exception e) {
			this.writeBusUpdateErrorLog(lm.addMetaData("errMsg", "fillGoodsWmsDO error" +e.getMessage()),false, e);
			this.wmsDO = null;
		}
		this.wmsDO = wmsDO;
	}

	// 发送库存新增消息
		public void sendNotify() {
			try {
				InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
				goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryWmsCreaterDomain.toString(),JSONObject
						.fromObject(notifyParam));
				/*
				 * Type orderParamType = new
				 * TypeToken<NotifyCardOrder4ShopCenterParam>(){}.getType(); String
				 * paramJson = new Gson().toJson(notifyParam, orderParamType);
				 * extensionService.sendNotifyServer(paramJson, lm.getTraceId());
				 */
			} catch (Exception e) {
				writeBusUpdateErrorLog(lm.addMetaData("errMsg", "sendNotify error" +e.getMessage()),false, e);
			}
		}

		// 填充notifyserver发送参数
		private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() {
			InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
			//notifyParam.setUserId();
			//notifyParam.setGoodsId(goodsId);
			//notifyParam.setLimitStorage(param.getLimitStorage());
			//notifyParam.setWaterfloodVal(param.getWaterfloodVal());
			notifyParam.setTotalNumber(param.getTotalNumber());
			notifyParam.setLeftNumber(param.getLeftNumber());
			//库存总数 减 库存剩余
			int sales = param.getTotalNumber()-param.getLeftNumber();
			//销量
			notifyParam.setSales(String.valueOf(sales));
			
			if (!CollectionUtils.isEmpty(selectionList)) {
				notifyParam.setSelectionRelation(ObjectUtils.toSelectionMsgList(selectionList));
			}
			
			return notifyParam;
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
}
