package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.SEQNAME;
import com.tuan.inventory.domain.support.util.SequenceUtil;
import com.tuan.inventory.domain.support.util.StringUtil;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.WmsAdjustSelectionParam;

public class InventoryWmsAdjust4SelectionDomain extends AbstractDomain {
	
	private LogModel lm;
	private String clientIp;
	private String clientName;
	private WmsAdjustSelectionParam param;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	private GoodsInventoryActionDO updateActionDO;
	//选型商品类型的id列表
	private List<Long> selGoodsTypeIds;
	//缓存选型的id
	private List<Long> selIds;
	private String wmsGoodsId;  //物流商品的一种编码
	private long goodsId;  //商品id
	private List<GoodsSelectionModel> selectionList;
	// 需扣减的商品库存
	private int wmsGoodsDeductNum = 0;
	private int wmsSelectionDeductNum;
	
	// 领域中缓存选型和分店原始库存和扣减库存的list
	private List<GoodsWmsSelectionResult> selectionParam;
	// 选型调整前库存量
	private GoodsSelectionDO preSelectionDO;
	private SequenceUtil sequenceUtil;

	public InventoryWmsAdjust4SelectionDomain(String clientIp, String clientName,
			WmsAdjustSelectionParam param, LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.param = param;
		this.lm = lm;
	}

	/**
	 * 处理选型库存
	 */
	private boolean selectionInventoryHandler() {
		try {
		if (!CollectionUtils.isEmpty(param.getGoodsSelection())) { // if1
			this.selectionList = param.getGoodsSelection();
			this.selectionParam = new ArrayList<GoodsWmsSelectionResult>();

			for (GoodsSelectionModel model : selectionList) { // for
				if (model.getId() != null && model.getId() > 0) { // if选型
					GoodsWmsSelectionResult selection = null;
					Long selectionId = Long.valueOf(model.getId());
					// 查询商品选型库存
					GoodsSelectionDO selectionDO = this.goodsInventoryDomainRepository
							.querySelectionRelationById(selectionId);
					if (selectionDO != null
							&& selectionDO.getLimitStorage() == 1) { //为了计算销量 不管是否限制库存的都要扣减
						    setPreSelectionDO(selectionDO);
						// 扣减库存并返回扣减标识,计算库存并
						    setWmsSelectionDeductNum(model.getNum());
							selection = new GoodsWmsSelectionResult();
							//redis更新用
							selection.setId(model.getId());
							selection.setGoodsId(goodsId); //更新选型的商品id
							//mysql更新用
							selection.setGoodTypeId(model.getGoodTypeId());
							// 技术选型的库存量
							if ((selectionDO.getTotalNumber() + model.getNum()) < 0) {
								selection.setLeftNum(0);
								selection.setTotalNum(0);
							}else {
								selection.setLeftNum((selectionDO.getLeftNumber() + model.getNum()) < 0?0:(selectionDO.getLeftNumber() + model.getNum()));
								selection.setTotalNum(selectionDO.getTotalNumber() + model.getNum());
							}
							
							// 选型库存，并且是库存充足时用
							this.selectionParam.add(selection);
					}else if(selectionDO != null
							&& selectionDO.getLimitStorage() == 0){
						return true;
					}

				}// if

			}// for

		} 
		} catch (Exception e) {
			logSysUpdate.error(lm.addMetaData("errorMsg",
							"selectionInventoryHandler error" + e.getMessage()).toJson(false), e);
			return false;
			
		}
		return true;
	}

	// 业务检查
	public CreateInventoryResultEnum busiCheck() {
		long startTime = System.currentTimeMillis();
		
		lm.addMetaData("init start", startTime)
				.toJson(true);
		if(logSysUpdate.isDebugEnabled()) {
			logSysUpdate.debug(lm.toJson(false));
		}
		// 初始化检查
		CreateInventoryResultEnum resultEnum =	this.initCheck();
		long endTime = System.currentTimeMillis();
		String runResult = "[" + "init" + "]业务处理历时" + (startTime - endTime)
				+ "milliseconds(毫秒)执行完成!";
		lm.addMetaData("endTime", endTime).addMetaData("wmsGoodsId", wmsGoodsId)
				.addMetaData("runResult", runResult).addMetaData("message", resultEnum.getDescription());
		if(logSysUpdate.isDebugEnabled()) {
			logSysUpdate.debug(lm.toJson(false));
		}
		
		if(resultEnum!=null&&!(resultEnum.compareTo(CreateInventoryResultEnum.SUCCESS) == 0)){
			return resultEnum;
		}
		// 真正的库存更新业务处理
		try {
			// 商品选型处理
			if(!this.selectionInventoryHandler()) {
				return CreateInventoryResultEnum.SYS_ERROR;
			}
			
		} catch (Exception e) {
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"busiCheck error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	// 库存系统新增库存
	@SuppressWarnings("static-access")
	public CreateInventoryResultEnum updateAdjustWmsInventory() {
		try {
			// 首先填充日志信息
			if (!fillInventoryUpdateActionDO()) {
				return CreateInventoryResultEnum.INVALID_LOG_PARAM;
			}
			// 插入日志
			this.goodsInventoryDomainRepository.pushLogQueues(updateActionDO);
				// 更新商品选型库存
			CreateInventoryResultEnum handlerResultEnum = this.synUpdateMysqlInventory(selectionParam);
			if (handlerResultEnum != handlerResultEnum.SUCCESS) {
				logSysUpdate.info("wmsGoodsId:"+ wmsGoodsId+",goodsId:"+goodsId+",selectionParam:"+selectionParam+",handlerResult:"+handlerResultEnum.getDescription().toString());
				return handlerResultEnum;
			}
				

		} catch (Exception e) {
			logSysUpdate.error(lm.addMetaData("errorMsg",
					"updateAdjustWmsInventory error" + e.getMessage()).toJson(false), e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		return CreateInventoryResultEnum.SUCCESS;
	}
	
	//初始化库存
	public CreateInventoryResultEnum initCheck() {
	       //初始化物流编码
			//this.wmsGoodsId = param.getWmsGoodsId();
			setWmsGoodsId(param.getWmsGoodsId());
			setGoodsId(param.getGoodsId());
			//初始化加分布式锁
			CreateInventoryResultEnum resultEnum = null;
		
			InventoryInitDomain create = new InventoryInitDomain();
			//注入相关Repository
			create.setWmsGoodsId(wmsGoodsId);
			create.setLm(lm);
			create.setGoodsTypeIdList(selGoodsTypeIds);
			create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			resultEnum = create.business4WmsExecute();
			
			return resultEnum;
			}
	
	//异步更新mysql商品库存
		public CreateInventoryResultEnum synUpdateMysqlInventory(List<GoodsWmsSelectionResult>  selectionParam) {
			InventoryInitDomain create = new InventoryInitDomain();
			//注入相关Repository
			create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
			create.setLm(lm);
			return create.updateWmsAdjustSeltion(goodsId,selectionParam);
		}
	
	// 填充日志信息
	public boolean fillInventoryUpdateActionDO() {
		GoodsInventoryActionDO updateActionDO = new GoodsInventoryActionDO();
		try {
			updateActionDO.setId(sequenceUtil.getSequence(SEQNAME.seq_log));
			updateActionDO.setGoodsId(goodsId);
			if(!StringUtils.isEmpty(wmsGoodsId)) {
				updateActionDO.setRemark(StringUtils.isEmpty(updateActionDO.getRemark())?"物流编码:"+wmsGoodsId:updateActionDO.getRemark()+",物流编码:"+wmsGoodsId);
			}
			
			if (!CollectionUtils.isEmpty(selectionList)) {
				updateActionDO.setBusinessType(StringUtils.isEmpty(updateActionDO.getBusinessType())?ResultStatusEnum.GOODS_WMS_SELECTION
						.getDescription():updateActionDO.getBusinessType()+",选型："+ResultStatusEnum.GOODS_WMS_SELECTION
						.getDescription());
				updateActionDO.setItem(StringUtils.isEmpty(updateActionDO.getItem())?"选型id:"+StringUtil
						.getIdsStringSelection(selectionList):updateActionDO.getItem()+",选型id:"+StringUtil
						.getIdsStringSelection(selectionList));
				updateActionDO.setOriginalInventory(StringUtils.isEmpty(updateActionDO.getOriginalInventory())?"选型调整前库存："+JsonUtils.convertObjectToString(preSelectionDO):updateActionDO.getOriginalInventory()+",选型调整前库存："+JsonUtils.convertObjectToString(preSelectionDO));
				updateActionDO.setInventoryChange(StringUtils.isEmpty(updateActionDO.getInventoryChange())?",选型调整后库存:"+JsonUtils.convertObjectToString(selectionParam):updateActionDO.getInventoryChange()+",选型调整后库存:"+JsonUtils.convertObjectToString(selectionParam));
			}
			
			updateActionDO.setActionType(ResultStatusEnum.ADJUST_WMSINVENTORY
					.getDescription());
			
			updateActionDO.setClientIp(clientIp);
			updateActionDO.setClientName(clientName);
			
			updateActionDO.setContent(JsonUtils.convertObjectToString(param)); // 操作内容
			updateActionDO.setRemark(StringUtils.isEmpty(updateActionDO.getRemark())?"物流库存调整":updateActionDO.getRemark()+",物流库存调整");
			updateActionDO.setCreateTime(TimeUtil.getNowTimestamp10Int());
		} catch (Exception e) {
			logSysUpdate.error(lm.addMetaData("errorMsg",
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
		if (StringUtils.isEmpty(param.getWmsGoodsId())) {
			return CreateInventoryResultEnum.INVALID_WMSGOODSID;
		}
		if (param.getGoodsId()==null) {
			return CreateInventoryResultEnum.INVALID_GOODSID;
		}else if(param.getGoodsId()<=0){
		    return CreateInventoryResultEnum.INVALID_GOODSID;
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

		}else {
			return CreateInventoryResultEnum.SELECTION_GOODS;
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

	public void setSequenceUtil(SequenceUtil sequenceUtil) {
		this.sequenceUtil = sequenceUtil;
	}

	public void setLm(LogModel lm) {
		this.lm = lm;
	}

	public int getWmsGoodsDeductNum() {
		return wmsGoodsDeductNum;
	}

	public void setWmsGoodsDeductNum(int wmsGoodsDeductNum) {
		this.wmsGoodsDeductNum = wmsGoodsDeductNum;
	}

	public int getWmsSelectionDeductNum() {
		return wmsSelectionDeductNum;
	}

	public void setWmsSelectionDeductNum(int wmsSelectionDeductNum) {
		this.wmsSelectionDeductNum = wmsSelectionDeductNum;
	}

	public String getWmsGoodsId() {
		return wmsGoodsId;
	}

	public void setWmsGoodsId(String wmsGoodsId) {
		this.wmsGoodsId = wmsGoodsId;
	}

	public long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}

	public GoodsSelectionDO getPreSelectionDO() {
		return preSelectionDO;
	}

	public void setPreSelectionDO(GoodsSelectionDO preSelectionDO) {
		this.preSelectionDO = preSelectionDO;
	}

	

}
