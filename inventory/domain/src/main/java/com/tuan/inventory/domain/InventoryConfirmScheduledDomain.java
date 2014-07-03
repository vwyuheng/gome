package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.repository.GoodsInventoryDomainRepository;
import com.tuan.inventory.domain.support.enu.NotifySenderEnum;
import com.tuan.inventory.domain.support.logs.LogModel;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.enu.res.CreateInventoryResultEnum;
import com.tuan.inventory.model.param.InventoryNotifyMessageParam;

public class InventoryConfirmScheduledDomain extends AbstractDomain {
	private LogModel lm;
	private GoodsInventoryModel goodsInventoryModel;
	//缓存商品id，并排重，以便归并相同商品的消息发送次数
	private ConcurrentHashSet<GoodsInventoryQueueModel> listGoodsIdSends;
	private GoodsInventoryDomainRepository goodsInventoryDomainRepository;
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	
	private List<GoodsSelectionDO> selectionInventoryList = null;
	private List<GoodsInventoryWMSDO> wmsInventoryList = null;
	private List<GoodsSuppliersDO> suppliersInventoryList = null;
	private GoodsInventoryDO inventoryInfoDO = null;
	private long goodsId = 0;
	private long goodsBaseId = 0;
	private final int delStatus = 6;
	public InventoryConfirmScheduledDomain(String clientIp,
			String clientName,LogModel lm) {
		this.clientIp = clientIp;
		this.clientName = clientName;
		this.lm = lm;
	}
	/***
	 * 业务处理前的预处理
	 */
	public boolean preHandler() {
		boolean preresult = true;
		try {
			listGoodsIdSends = new ConcurrentHashSet<GoodsInventoryQueueModel>();
			// 商品库存是否存在
			//取初始状态队列信息
			List<GoodsInventoryQueueModel> queueList = goodsInventoryDomainRepository
					.queryInventoryQueueListByStatus(Double
							.valueOf(ResultStatusEnum.CONFIRM.getCode()));
			if (!CollectionUtils.isEmpty(queueList)) {
				for (GoodsInventoryQueueModel model : queueList) {
					//队列数据数据按商品id 归集后 发送消息更新数据准备
					if (verifyId(model.getGoodsId()))
						listGoodsIdSends.add(model);
					
				}
			}else {
				writeJobLog("[ConfirmJob]获取队列:("+ResultStatusEnum.CONFIRM.getDescription()+"),状态为：("+ResultStatusEnum.CONFIRM.getCode()+")的队列为空！");
			}
		} catch (Exception e) {
			preresult = false;
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"preHandler error" + e.getMessage()),false, e);
		}
		
		return preresult;
	}

	// 业务处理
	@SuppressWarnings("static-access")
	public CreateInventoryResultEnum businessHandler() {

		try {
			 CreateInventoryResultEnum  updateDataEnum = null;
			// 业务检查前的预处理
			if(!preHandler()){
				return CreateInventoryResultEnum.SYS_ERROR;
			}
			
			if (!CollectionUtils.isEmpty(listGoodsIdSends)) {
				for (GoodsInventoryQueueModel queueModel : listGoodsIdSends) {
					long goodsId = queueModel.getGoodsId();
					//long goodsBaseId = queueModel.getGoodsBaseId();
					long queueId = queueModel.getId();
					if (loadMessageData(goodsId)) {  //更加商品id加载数据
						
						if (fillParamAndUpdate()) { // 组织数据

							writeJobLog("[fillParamAndUpdate,start]更新goodsId:("
									+ goodsId + "),inventoryInfoDO：("
									+ inventoryInfoDO
									+ "),selectionInventoryList:("
									+ selectionInventoryList
									+ "),wmsInventoryList:(" + wmsInventoryList
									+ ")");
							// 调用数据同步
							updateDataEnum = this.asynUpdateMysqlInventory(
									goodsId, inventoryInfoDO,
									selectionInventoryList,
									suppliersInventoryList, wmsInventoryList);
							if (updateDataEnum != updateDataEnum.SUCCESS) {
								writeJobLog("[fillParamAndUpdate]更新goodsId:("
										+ goodsId + "),inventoryInfoDO：("
										+ inventoryInfoDO
										+ "),selectionInventoryList:("
										+ selectionInventoryList
										+ "),wmsInventoryList:("
										+ wmsInventoryList + "),message("
										+ updateDataEnum.getDescription() + ")");
							} else {
								// 发消息
								this.sendNotify();
								// 消息发送完成后将取出的队列标记删除状态
								if(this.verifyId(queueId)) {
									this.markDelete(queueId);
								}
								writeJobLog("[fillParamAndUpdate,end]更新goodsId:("
										+ goodsId
										+ "),inventoryInfoDO：("
										+ inventoryInfoDO
										+ "),selectionInventoryList:("
										+ selectionInventoryList
										+ "),wmsInventoryList:("
										+ wmsInventoryList + ")");
							}
						}
					}// if1
				}

			}// if2
				
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"businessHandler error" + e.getMessage()),false, e);
			return CreateInventoryResultEnum.SYS_ERROR;
		}
		
		return CreateInventoryResultEnum.SUCCESS;
	}

	//加载消息数据
	public boolean loadMessageData(long goodsId) {
		try {
		this.goodsInventoryModel =	this.goodsInventoryDomainRepository.queryAllInventoryDataByGoodsId(goodsId);
		if(this.goodsInventoryModel==null){
			return false;
		}
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"loadMessageData error" + e.getMessage()),false, e);
			return false;
		}
		return true;
	}

	//异步更新mysql商品库存
	public CreateInventoryResultEnum asynUpdateMysqlInventory(long goodsId,GoodsInventoryDO inventoryInfoDO,List<GoodsSelectionDO> selectionInventoryList,List<GoodsSuppliersDO> suppliersInventoryList,List<GoodsInventoryWMSDO> wmsInventoryList) {
		InventoryInitDomain create = new InventoryInitDomain(goodsId,lm);
		//create.setGoodsId(goodsId);
		//注入相关Repository
		create.setGoodsInventoryDomainRepository(this.goodsInventoryDomainRepository);
		create.setSynInitAndAysnMysqlService(synInitAndAysnMysqlService);
		create.setLm(lm);
		return create.asynUpdateMysqlInventory(goodsId,inventoryInfoDO, selectionInventoryList, suppliersInventoryList,wmsInventoryList);
	}
	/**
	 * 组装数据并更新
	 */
	private boolean fillParamAndUpdate() {
		
		try {
			inventoryInfoDO = new GoodsInventoryDO();
			goodsId = goodsInventoryModel.getGoodsId();
			goodsBaseId = goodsInventoryModel.getGoodsBaseId();
			inventoryInfoDO.setGoodsId(goodsId);
			inventoryInfoDO.setGoodsBaseId(goodsBaseId);
			inventoryInfoDO.setLimitStorage(goodsInventoryModel
					.getLimitStorage());
			inventoryInfoDO.setWaterfloodVal(goodsInventoryModel
					.getWaterfloodVal());
			inventoryInfoDO
					.setTotalNumber(goodsInventoryModel.getTotalNumber());
			inventoryInfoDO.setLeftNumber(goodsInventoryModel.getLeftNumber());
			inventoryInfoDO.setGoodsSaleCount(goodsInventoryModel.getGoodsSaleCount());
			if (!CollectionUtils.isEmpty(goodsInventoryModel
					.getGoodsSelectionList())) {
				selectionInventoryList = new ArrayList<GoodsSelectionDO>();
				wmsInventoryList = new ArrayList<GoodsInventoryWMSDO>();
				List<GoodsSelectionModel> selectionModelList = goodsInventoryModel
						.getGoodsSelectionList();
				if (!CollectionUtils.isEmpty(selectionModelList)) {
					for (GoodsSelectionModel selModel : selectionModelList) {
						selectionInventoryList.add(ObjectUtils
								.toSelectionDO(selModel));
						if (!StringUtils.isEmpty(selModel.getWmsGoodsId())) {
							wmsInventoryList.add(ObjectUtils.toWmsDO(selModel)); //处理物流的数据
						}

					}
				}
			}
			if (!CollectionUtils.isEmpty(goodsInventoryModel
					.getGoodsSuppliersList())) {
				suppliersInventoryList = new ArrayList<GoodsSuppliersDO>();
				List<GoodsSuppliersModel> suppliersModelList = goodsInventoryModel
						.getGoodsSuppliersList();
				if (!CollectionUtils.isEmpty(suppliersModelList)) {
					for (GoodsSuppliersModel supModel : suppliersModelList) {
						suppliersInventoryList.add(ObjectUtils
								.toSuppliersDO(supModel));
					}
				}
			}
		} catch (Exception e) {
			this.writeBusJobErrorLog(
					lm.addMetaData("errorMsg",
							"fillParamAndUpdate error" + e.getMessage()),false, e);
			return false;
		}
		return true;
		
	}
	// 发送库存新增消息
	public void sendNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
			this.goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryConfirmScheduledDomain.toString(),JSONObject
					.fromObject(notifyParam));
			/*
			 * Type orderParamType = new
			 * TypeToken<NotifyCardOrder4ShopCenterParam>(){}.getType(); String
			 * paramJson = new Gson().toJson(notifyParam, orderParamType);
			 * extensionService.sendNotifyServer(paramJson, lm.getTraceId());
			 */
		} catch (Exception e) {
			writeBusJobErrorLog(lm.addMetaData("errMsg", "sendNotify error"+e.getMessage()),false, e);
		}
	}

	// 填充notifyserver发送参数
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() {
		if(goodsInventoryModel==null) {
			return null;
		}
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		notifyParam.setGoodsBaseId(goodsInventoryModel.getGoodsBaseId());
		notifyParam.setUserId(goodsInventoryModel.getUserId());
		notifyParam.setGoodsId(goodsInventoryModel.getGoodsId());
		notifyParam.setLimitStorage(goodsInventoryModel.getLimitStorage());
		notifyParam.setWaterfloodVal(goodsInventoryModel.getWaterfloodVal());
		notifyParam.setTotalNumber(goodsInventoryModel.getTotalNumber());
		notifyParam.setLeftNumber(goodsInventoryModel.getLeftNumber());
		//库存总数 减 库存剩余
		Integer sales = goodsInventoryModel.getGoodsSaleCount();
		//销量
		notifyParam.setSales(sales==null?0:sales);
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSelectionList())) {
			notifyParam.setSelectionRelation(ObjectUtils.toSelectionMsgList(goodsInventoryModel.getGoodsSelectionList()));
		}
		if (!CollectionUtils.isEmpty(goodsInventoryModel.getGoodsSuppliersList())) {
			notifyParam.setSuppliersRelation(ObjectUtils.toSuppliersMsgList(goodsInventoryModel.getGoodsSuppliersList()));
		}
		GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsInventoryModel.getGoodsBaseId());
		if(baseInventoryDO!=null){
			notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
			notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
		}
		
		return notifyParam;
	}

	// 将队列标记删除：逻辑删除[队列]\物理删除缓存的member
	public void markDelete(long queueId) {
		
		try {
			//if (!CollectionUtils.isEmpty(listQueueIdMarkDelete)) {
				//for(long queueId:listQueueIdMarkDelete) {
					String member = this.goodsInventoryDomainRepository.queryMember(String.valueOf(queueId));
					if(!StringUtils.isEmpty(member)) {
						this.goodsInventoryDomainRepository.markQueueStatusAndDeleteCacheMember(member, (delStatus),String.valueOf(queueId));
					}
				//}
			//}
			
		} catch (Exception e) {
			
			this.writeBusJobErrorLog(lm
					.addMetaData("errMsg", "markDelete error"+e.getMessage()),false, e);
			
		}
		
	}
	
	
	/**
	 * 校验id
	 * @param id
	 * @return
	 */
   public boolean verifyId(long id) {
	   if(id<=0) {
		   return false;
	   }else {
		   return true;
	   }
   }

	public void setGoodsInventoryDomainRepository(
			GoodsInventoryDomainRepository goodsInventoryDomainRepository) {
		this.goodsInventoryDomainRepository = goodsInventoryDomainRepository;
	}
	
	public void setSynInitAndAysnMysqlService(
			SynInitAndAysnMysqlService synInitAndAysnMysqlService) {
		this.synInitAndAysnMysqlService = synInitAndAysnMysqlService;
	}
	
	
}
