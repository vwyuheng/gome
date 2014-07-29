package com.tuan.inventory.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
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
import com.tuan.inventory.model.util.QueueConstant;

public class InventoryConfirmScheduledDomain extends AbstractDomain {
	private static Log logConfirm=LogFactory.getLog("CONFIRM.JOB.LOG");
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
				//logConfirm.info("[ConfirmJob]获取队列:("+ResultStatusEnum.CONFIRM.getDescription()+"),状态为：("+ResultStatusEnum.CONFIRM.getCode()+")的队列:("+queueList.size()+")条,处理前队列详细信息queueList:("+queueList+")");
				for (GoodsInventoryQueueModel model : queueList) {
					//队列数据数据按商品id 归集后 发送消息更新数据准备
					if (verifyId(model.getGoodsId()!=null?model.getGoodsId():0)) {
						listGoodsIdSends.add(model);
					}else {
						logConfirm.info("[商品id不合法]队列详细信息model:("+model+")");
					}
					//logConfirm.info("[ConfirmJob]处理后队列详细信息listGoodsIdSends:("+listGoodsIdSends+")");
					
				}
			}else {
				logConfirm.info("[ConfirmJob]获取队列:("+ResultStatusEnum.CONFIRM.getDescription()+"),状态为：("+ResultStatusEnum.CONFIRM.getCode()+")的队列为空！");
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
				logConfirm.info("需处理的队列条数:("+listGoodsIdSends.size()+")");
				for (GoodsInventoryQueueModel queueModel : listGoodsIdSends) {
					long goodsId = 0;
					long queueId = 0;
					if(queueModel!=null) {
						 goodsId = queueModel.getGoodsId()!=null?queueModel.getGoodsId():0;
						//long goodsBaseId = queueModel.getGoodsBaseId();
						 queueId = queueModel.getId()!=null?queueModel.getId():0;
					}
					
					//当确保redis中数据是最新的数据后,第一件事应该就是将队列状态标记删除以保证不会被重复处理
					if(this.verifyId(queueId)) {
						if(!this.markDelete(queueId,JSON.toJSONString(queueModel))) {
							logConfirm.info("[队列状态标记删除状态及删除缓存的队列失败],queueId:("+queueId+")!!!");
						}else {
							//logConfirm.info("[队列状态标记删除状态及删除缓存的队列成功],queueId:("+queueId+"),end");
						}
					}else {
						logConfirm.info("[队列id不合法],queueId:("+queueId+")该队列详细信息为queueModel:"+queueModel);
					}
					
					if (loadMessageData(goodsId)) {  //更加商品id加载数据
						// 随后才是发消息
						if(!this.sendNotify()) {  //只有消息发成功后才进行队列的标记删除动作
							// 消息发送完成后将取出的队列标记删除状态
							logConfirm.info("[消息发送失败,]queueId:("+queueId+")"+",goodsId:("+goodsId+")");
							
						}
						//第三财是同步数据
						if (fillParamAndUpdate()) { // 组织数据
							// 调用数据同步
							updateDataEnum = this.asynUpdateMysqlInventory(
									goodsId, inventoryInfoDO,
									selectionInventoryList,
									suppliersInventoryList, wmsInventoryList);
							//if (updateDataEnum != updateDataEnum.SUCCESS) {
							if (updateDataEnum!=null&&!(updateDataEnum.compareTo(updateDataEnum.SUCCESS) == 0)) {
								logConfirm.info("[同步数据失败,failed],goodsId:("
										+ goodsId + "),inventoryInfoDO：("
										+ inventoryInfoDO
										+ "),selectionInventoryList:("
										+ selectionInventoryList
										+ "),wmsInventoryList:("
										+ wmsInventoryList + "),message("
										+ updateDataEnum.getDescription() + ")");
								
							} else {
								/*logConfirm.info("[同步数据成功,success]"
								+ ",message("
								+ updateDataEnum.getDescription() + ")");*/
							}
						}else {
							logConfirm.info("[fillParamAndUpdate,填充数据失败]goodsId:("+goodsId+")");
						}
					}else {
						logConfirm.info("[loadMessageData,加载数据失败]goodsId:("+goodsId+")");
					}
				}

			}else {
				logConfirm.info("本次调度无已确认队列需要处理!!!");
			}
				
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
	public boolean sendNotify() {
		try {
			InventoryNotifyMessageParam notifyParam = fillInventoryNotifyMessageParam();
			if(notifyParam!=null) {
				this.goodsInventoryDomainRepository.sendNotifyServerMessage(NotifySenderEnum.InventoryConfirmScheduledDomain.toString(),JSONObject
						.fromObject(notifyParam));
			}
			
		} catch (Exception e) {
			writeBusJobErrorLog(lm.addMetaData("errMsg", "sendNotify error"+e.getMessage()),false, e);
			return false;
		}
		return true;
	}

	// 填充notifyserver发送参数
	private InventoryNotifyMessageParam fillInventoryNotifyMessageParam() throws Exception{
		if(goodsInventoryModel==null) {
			return null;
		}
		InventoryNotifyMessageParam notifyParam = new InventoryNotifyMessageParam();
		long goodsBaseId = goodsInventoryModel.getGoodsBaseId();
		notifyParam.setGoodsBaseId(goodsBaseId);
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
		
		GoodsBaseInventoryDO baseInventoryDO = goodsInventoryDomainRepository.queryGoodsBaseById(goodsBaseId);
		if(baseInventoryDO!=null&&baseInventoryDO.getGoodsBaseId()!=null){
			notifyParam.setBaseTotalCount(baseInventoryDO.getBaseTotalCount());
			notifyParam.setBaseSaleCount(baseInventoryDO.getBaseSaleCount());
		}else {
			logConfirm.info("[GoodsBaseInventoryDO,非法数据]查询goodsBaseId:("+goodsBaseId+"),redis中所存储baseInventoryDO状态:"+baseInventoryDO);
		}
		
		return notifyParam;
	}

	// 将队列标记删除：逻辑删除[队列]\物理删除缓存的member
	public boolean markDelete(long queueId,String queuemember) {
		
		try {
			String member = this.goodsInventoryDomainRepository.queryMember(String.valueOf(queueId));
			if(!StringUtils.isEmpty(member)) {
				boolean mark=   this.goodsInventoryDomainRepository.markQueueStatusAndDeleteCacheMember(member, (delStatus),String.valueOf(queueId));
				if(!mark) {
					Double recv= this.goodsInventoryDomainRepository.zincrby(QueueConstant.QUEUE_SEND_MESSAGE, (delStatus),queuemember);
					if(recv== null) {
						logConfirm.info("[标记删除队列时失败]queuemember:("+queuemember+")");
						return false;
					}else if(recv==0) {
						logConfirm.info("[获取缓存的队列为null,并且标记删除队列时也匹配不上]queuemember:("+queuemember+")");
						return true;
					}else {
						logConfirm.info("[获取缓存的队列为null,将队列标记删除成功!]queueId:("+queueId+")");
						return true;
					}
				}else {
					return mark;
				}
			}else {
				
				Double recv= this.goodsInventoryDomainRepository.zincrby(QueueConstant.QUEUE_SEND_MESSAGE, (delStatus),queuemember);
				if(recv== null) {
					logConfirm.info("[标记删除队列时失败]queuemember:("+queuemember+")");
					return false;
				}else if(recv==0) {
					logConfirm.info("[获取缓存的队列为null,并且标记删除队列时也匹配不上]queuemember:("+queuemember+")");
					return true;
				}else {
					logConfirm.info("[获取缓存的队列为null,将队列标记删除成功!]queueId:("+queueId+")");
					return true;
				}
			 
			}
			
		} catch (Exception e) {
			this.writeBusJobErrorLog(lm
					.addMetaData("errMsg", "markDelete error"+e.getMessage()),false, e);
			return false;
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
