package com.tuan.inventory.domain.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.GoodsSelectionAndSuppliersResult;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.SynInitAndAysnMysqlService;
import com.tuan.inventory.domain.support.BaseDAOService;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.util.DataUtil;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.GoodsInventoryModel;
import com.tuan.inventory.model.GoodsInventoryQueueModel;
import com.tuan.inventory.model.GoodsSelectionModel;
import com.tuan.inventory.model.GoodsSuppliersModel;
import com.tuan.inventory.model.result.CallResult;

public class GoodsInventoryDomainRepository extends AbstractInventoryRepository {
	@Resource
	private BaseDAOService  baseDAOService;
	@Resource
	private NotifyServerSendMessage notifyServerSendMessage;
	@Resource
	private SynInitAndAysnMysqlService synInitAndAysnMysqlService;
	
	
	public void sendNotifyServerMessage(String sender,JSONObject jsonObj) {
		this.notifyServerSendMessage.sendNotifyServerMessage(sender,jsonObj);
	}
	public Long clearWmsSelRelation(Long goodsId,  String member) {
		return this.baseDAOService.clearWmsSelRelation(goodsId, member);
	}
	//保存商品库存
	public String saveGoodsInventory(Long goodsId, GoodsInventoryDO inventoryInfoDO) {
		return this.baseDAOService.saveInventory(goodsId, inventoryInfoDO);
	}
	
	//保存商品库存基本信息
		public String saveGoodsBaseInventory(Long goodsBaseId, GoodsBaseInventoryDO goodsBaseInventoryDO) {
			return this.baseDAOService.saveGoodsBaseInventory(goodsBaseId, goodsBaseInventoryDO);
		}
	//更新商品库存基本信息
		public List<Long> updateGoodsBaseInventory(Long goodsBaseId, int saleCount,int totalCount) {
					return this.baseDAOService.updateGoodsBaseInventory(goodsBaseId, saleCount,totalCount);
				}
	public String saveBatchGoodsInventory(List<GoodsInventoryDO> goodsIds) {
		if(!CollectionUtils.isEmpty(goodsIds)) {
			for(GoodsInventoryDO inventoryInfoDO:goodsIds) {
				long goodsId = inventoryInfoDO.getGoodsId();
				long goodsBaseId = inventoryInfoDO.getGoodsBaseId();
				String result = this.baseDAOService.saveInventory(goodsId, inventoryInfoDO);
				if(StringUtils.isNotEmpty(result)&&result.equalsIgnoreCase("OK")) {
					if(goodsBaseId>0) {  //常态化刚上线时是1:1的,故可以直接取
						GoodsBaseInventoryDO tmpDo = baseDAOService.queryGoodsBaseById(goodsBaseId);
						if(tmpDo==null) {
							//初始化基本信息
							CallResult<GoodsBaseInventoryDO> callResult = this.synInitAndAysnMysqlService
									.selectInventoryBase4Init(goodsBaseId);
							if (callResult != null&&callResult.isSuccess()) {
								GoodsBaseInventoryDO baseDO = 	callResult.getBusinessResult();
								if(baseDO!=null) {
									result =	baseDAOService.saveGoodsBaseInventory(goodsBaseId, baseDO);
								}
							}
							
						}else {
							
							CallResult<GoodsBaseInventoryDO> callResult = this.synInitAndAysnMysqlService
									.selectInventoryBase4Init(goodsBaseId);
							if (callResult != null&&callResult.isSuccess()) {
								GoodsBaseInventoryDO baseDO = 	callResult.getBusinessResult();
								if(baseDO!=null) {
									result =	baseDAOService.saveGoodsBaseInventory(goodsBaseId, baseDO);
								}
							}
							
						}
					}
				}
				
				if(StringUtils.isEmpty(result)) {
					return null;
				}else {
					if(!result.equalsIgnoreCase("OK")) {
						return null;
					}
				}
			}
		}else {
			return null;
		}
		return "OK";
		
	}
	//将日志压入队列
	public void pushLogQueues(final GoodsInventoryActionDO logActionDO){
		this.baseDAOService.pushLogQueues(logActionDO);
	}
	public String pushQueueSendMsg(final GoodsInventoryQueueDO queueDO) {
		return this.baseDAOService.pushQueueSendMsg(queueDO);
	}
	//判断库存是否已存在
	public boolean isExists(Long goodsId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isExists(goodsId);
	}
	//判断物流库存是否已存在
	public boolean isWmsExists(String wmsGoodsId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isWmsExists(wmsGoodsId);
	}
	public boolean isGoodsExists(Long goodsId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isGoodsExists(goodsId,HashFieldEnum.leftNumber.toString());
	}
	public boolean isSelectionExists(Long selectionId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isSelectionExists(selectionId,HashFieldEnum.leftNumber.toString());
	}
	public boolean isSupplierExists(Long suppliesId) {
		//已存在返回false,不存在返回true
		return baseDAOService.isSupplierExists(suppliesId,HashFieldEnum.leftNumber.toString());
	}
	
	/**
	 * 保存物流选型库存
	 * @param selectionDO
	 */
	public String saveGoodsSelectionWmsInventory(List<GoodsSelectionDO> selectionDO) {
		String success = null;
		if (!CollectionUtils.isEmpty(selectionDO)) { // if1
			for (GoodsSelectionDO srDO : selectionDO) { // for
				if (srDO.getId() > 0) { // if选型
					//首先根据选型id判断该选型是否已存在
					GoodsSelectionDO tmpSelDO = this.baseDAOService.querySelectionRelationById(srDO.getId());
					if(tmpSelDO==null) {  //不存在才创建
						String retAck = this.baseDAOService.saveGoodsSelectionWmsInventory(srDO);
						if(!retAck.equalsIgnoreCase("ok")) {
							return null;
						}else {
							success = retAck;
						}
					}else {
						success = "OK";
					}
					
				}
				
			}//for
		}//if1
		return success;
		
	}
	
	/**
	 * 保存物流选型库存
	 * @param selectionDO
	 */
	public String saveGoodsSelectionWmsInventory(Long goodsId,List<GoodsSelectionDO> selectionDO) {
		String success = null;
		if (!CollectionUtils.isEmpty(selectionDO)) { // if1
			for (GoodsSelectionDO srDO : selectionDO) { // for
				if (srDO.getId() > 0) { // if选型
					
					//首先根据选型id判断该选型是否已存在
					GoodsSelectionDO tmpSelDO = this.baseDAOService.querySelectionRelationById(srDO.getId());
					if(tmpSelDO==null) {  //不存在才创建,TODO
						srDO.setGoodsId(goodsId);
						boolean retAck = this.baseDAOService.saveGoodsSelectionInventory(goodsId,srDO);
						if(!retAck) {
							return null;
						}else {
							success = "OK";
						}
					}else {
						success = "OK";
					}
					
				}
				
			}//for
		}//if1
		return success;
		
	}
	/**
	 * 保存物流选型库存
	 * @param selectionDO
	 */
	public String saveGoodsSelectionWmsUpdateInventory(Long goodsId,List<GoodsSelectionDO> selectionDO) {
		String success = null;
		if (!CollectionUtils.isEmpty(selectionDO)) { // if1
			for (GoodsSelectionDO srDO : selectionDO) { // for
				if (srDO.getId() > 0) { // if选型
					
					//首先根据选型id判断该选型是否已存在
					//GoodsSelectionDO tmpSelDO = this.baseDAOService.querySelectionRelationById(srDO.getId());
					//if(tmpSelDO==null) {  //不存在才创建,TODO
					//srDO.setGoodsId(goodsId);
					boolean retAck = this.baseDAOService.saveGoodsSelectionInventory(goodsId,srDO);
					if(!retAck) {
						return null;
					}else {
						success = "OK";
					}
					/*}else {
						success = "OK";
					}*/
					
				}
				
			}//for
		}//if1
		return success;
		
	}
	public boolean saveGoodsSelectionInventory(Long goodsId, List<GoodsSelectionDO> selectionDO) {
		if (!CollectionUtils.isEmpty(selectionDO)) { // if1
			for (GoodsSelectionDO srDO : selectionDO) { // for
				if (srDO.getId() > 0) { // if选型
					//首先根据选型id判断该选型是否已存在
					GoodsSelectionDO tmpSelDO = this.baseDAOService.querySelectionRelationById(srDO.getId());
					if(tmpSelDO==null) {  //不存在才创建,初始化时无需更新数据
						//将商品id set到选型中
					srDO.setGoodsId(goodsId);
					boolean subRet = this.baseDAOService.saveGoodsSelectionInventory(goodsId, srDO);
					if(!subRet) {
						return false;
					  }
					
					}
					
				}
				
			}//for
		}//if1
	    return true;
	
	}
	public boolean saveAndUpdateGoodsSeleInventory(Long goodsId, List<GoodsSelectionDO> selectionDO) {
		if (!CollectionUtils.isEmpty(selectionDO)) { // if1
			for (GoodsSelectionDO srDO : selectionDO) { // for
				if (srDO.getId() > 0) { // if选型
					//首先根据选型id判断该选型是否已存在
					//GoodsSelectionDO tmpSelDO = this.baseDAOService.querySelectionRelationById(srDO.getId());
					//if(tmpSelDO==null) {  //不存在才创建,初始化时无需更新数据
						//将商品id set到选型中
						srDO.setGoodsId(goodsId);
						boolean subRet = this.baseDAOService.saveGoodsSelectionInventory(goodsId, srDO);
						if(!subRet) {
							return false;
						}
						
					}
					
				//}
				
			}//for
		}//if1
		return true;
		
	}
	public boolean saveGoodsSelectionInventory(Long goodsId, GoodsSelectionDO selectionDO) {
		if(selectionDO!=null) {
			return this.baseDAOService.saveGoodsSelectionInventory(goodsId, selectionDO);
		}else {
			return false;
		}
		
	}
	public boolean saveGoodsSuppliersInventory(Long goodsId, List<GoodsSuppliersDO> suppliersDO) {
		if (!CollectionUtils.isEmpty(suppliersDO)) { // if1
			for (GoodsSuppliersDO sDO : suppliersDO) { // for
				if (sDO.getSuppliersId() > 0) { // if分店
					GoodsSuppliersDO tmpSuppDO = this.baseDAOService.querySuppliersInventoryById(sDO.getSuppliersId());
					if(tmpSuppDO==null) { //不存在才创建
						sDO.setGoodsId(goodsId);
						boolean retAck = this.baseDAOService.saveGoodsSuppliersInventory(goodsId, sDO);
						if(!retAck) {
							return false;
						}
					}
					
				}
				
			}//for
		}//if1
		return true;
	}
	public boolean saveGoodsSuppliersInventory(Long goodsId, GoodsSuppliersDO suppliersDO) {
		if(suppliersDO!=null) {
			return this.baseDAOService.saveGoodsSuppliersInventory(goodsId, suppliersDO);
		}else {
			return false;
		}
		
	}
	public String saveGoodsWmsInventory(GoodsInventoryWMSDO wmsDO) {
		if (wmsDO!=null) { // if1
			//for (GoodsInventoryWMSDO wmsDO : wmsDOList) { // for
			GoodsInventoryWMSDO tmpWmsDO = this.baseDAOService.queryWmsInventoryById(wmsDO.getWmsGoodsId());
					if(tmpWmsDO==null&&!StringUtils.isEmpty(wmsDO.getWmsGoodsId())) { //不存在才创建
						return this.baseDAOService.saveGoodsWmsInventory(wmsDO);
					}else {
						return "OK";
					}
				
			//}//for
		}//if1
		return null;
	}
	public String saveAndUpdateGoodsWmsInventory(GoodsInventoryWMSDO wmsDO) {
		if (wmsDO!=null) { // if1
			//for (GoodsInventoryWMSDO wmsDO : wmsDOList) { // for
			//GoodsInventoryWMSDO tmpWmsDO = this.baseDAOService.queryWmsInventoryById(wmsDO.getWmsGoodsId());
			if(!StringUtils.isEmpty(wmsDO.getWmsGoodsId())) { //不存在才创建
				return this.baseDAOService.saveGoodsWmsInventory(wmsDO);
			}
			
			//}//for
		}//if1
		return null;
	}
	//更新物流库存
	public List<Long> updateGoodsWmsInventory(String wmsGoodsId,int num) {
		if(StringUtils.isEmpty(wmsGoodsId)) { return null;}
		return this.baseDAOService.updateGoodsWms(wmsGoodsId,num);
	}
	
	
	//根据商品id查询库存信息
	public GoodsInventoryDO queryGoodsInventory(long goodsId) {
		return this.baseDAOService.queryGoodsInventory(goodsId);
	}
	public GoodsInventoryWMSDO queryGoodsInventoryWms(String wmsGoodsId) {
		return this.baseDAOService.queryWmsInventoryById(wmsGoodsId);
	}
	
	// 根据商品选型id查询库存信息
	public GoodsSelectionDO querySelectionRelationById(long selectionId) {
		return this.baseDAOService.querySelectionRelationById(selectionId);
	}
	public GoodsSuppliersDO querySuppliersInventoryById(Long suppliersId) {
		return this.baseDAOService.querySuppliersInventoryById(suppliersId);
	}
	
	public List<Long> updateGoodsInventory(Long goodsId, Long goodsBaseId,int leftnum,int num) {
		return this.baseDAOService.updateGoodsInventory(goodsId,goodsBaseId,leftnum, num);
	}
	//to
	public boolean updateBatchGoodsInventory(List<GoodsInventoryDO> goodsIds, int num) {
		boolean success = false;
		if(!CollectionUtils.isEmpty(goodsIds)) {
			for(GoodsInventoryDO goodsDO: goodsIds) {
				long goodsId = goodsDO.getGoodsId();
				List<Long> result = this.baseDAOService.updateInventory(goodsId, num);
				success = DataUtil.verifyInventory(result);
			}
		}
		return success;
	}
	
	public List<Long> adjustGoodsInventory(Long goodsId, Long goodBaseId,int num,int limitStorage) {
		return this.baseDAOService.adjustGoodsInventory(goodsId,goodBaseId, num,limitStorage);
	}
	
	public List<Long> updateSelectionInventoryById(Long selectionId, int num) {
		return this.baseDAOService.updateSelectionInventory(selectionId, (num));
	}
	public List<Long> adjustSelectionInventoryById(Long goodsId,Long selectionId, int num) {
		return this.baseDAOService.adjustSelectionInventory(goodsId,selectionId, (num));
	}
	/*public List<Long> updateSuppliersInventoryById(Long suppliersId, int num) {
		//return this.baseDAOService.updateSuppliersInventory(suppliersId, (num));
	}*/
	public List<Long> adjustSuppliersInventoryById(Long goodsId,Long suppliersId, int num) {
		return this.baseDAOService.adjustSuppliersInventory(goodsId,suppliersId, (num));
	}
	public boolean updateSelectionInventory(List<GoodsSelectionAndSuppliersResult> selectionParam) {
		boolean success = false;
		if (!CollectionUtils.isEmpty(selectionParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : selectionParam) { // for
				if (param.getId() > 0) { // if选型
					long selectionId = param.getId();
					 List<Long> result = this.baseDAOService.updateSelectionInventory(selectionId,param.getWmsGoodsId(), (-param.getGoodsInventory()));
					 String ret = null;
					//更新物流编码到选型模型中
					 if(!StringUtils.isEmpty(param.getWmsGoodsId())) {
						 Map<String,String> hash = new HashMap<String,String>();
						 hash.put(HashFieldEnum.wmsGoodsId.toString(), param.getWmsGoodsId());
	 					 ret = this.baseDAOService.updateSelectionFileds(selectionId, hash);
	 					 
					 }
					 success = DataUtil.verifyInventory(result);
					 if(success&&!StringUtils.isEmpty(ret)&&ret.equalsIgnoreCase("ok")) {
						 success = true;
					 }else {
						 success = false;
					 }
				}
			
			}
		}
		return success;
		
	}
	
	//回滚选型库存
	public boolean rollbackSelectionInventory(List<GoodsSelectionAndSuppliersResult> selectionParam) {
		boolean success = false;
		if (!CollectionUtils.isEmpty(selectionParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : selectionParam) { // for
				if (param.getId() > 0) { // if选型
					List<Long>	result = this.baseDAOService.updateSelectionInventory(param.getId(),param.getWmsGoodsId(), (param.getGoodsInventory()));
					success = DataUtil.verifyInventory(result);
				}
			}
		}
		return success;
		
	}
	public boolean updateSuppliersInventory(List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		boolean success = false;
		if (!CollectionUtils.isEmpty(suppliersParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : suppliersParam) { // for
				if (param.getId() > 0) { // if分店
					Long result = this.baseDAOService.updateSuppliersInventory(param.getId(), (-param.getGoodsInventory()));
					/*success = DataUtil.verifyInventory(result);*/
					if(result!=null&&result>=0) {
						success = true;
					}
				}
			}
		}
		return success;
		
	}
	/**
	 * 批量调整物流选型库存
	 * @param selectionList
	 * @return
	 */
	public List<Long> batchAdjustSelectionWms(List<GoodsWmsSelectionResult> selectionList) {
		List<Long> result = null;
		if (!CollectionUtils.isEmpty(selectionList)) { // if1
	
			for (GoodsWmsSelectionResult param : selectionList) { // for
				if (param.getId() > 0) { // if分店
					result = this.baseDAOService.adjustSelectionWmsInventory(param.getId(), (param.getLeftNum()),(param.getTotalNum()));
				}
			}
		}
		return result;
		
	}
	
	public List<Long> batchrollbackSelectionWms(List<GoodsWmsSelectionResult> selectionList) {
		List<Long> result = null;
		if (!CollectionUtils.isEmpty(selectionList)) { // if1
			for (GoodsWmsSelectionResult param : selectionList) { // for
				if (param.getId() > 0) { // if分店
					result = this.baseDAOService.adjustSelectionWmsInventory(param.getId(), (-param.getLeftNum()),(param.getTotalNum()));
				}
			}
		}
		return result;
		
	}
	//回滚分店库存
	public boolean rollbackSuppliersInventory(List<GoodsSelectionAndSuppliersResult> suppliersParam) {
		boolean success = false;
		if (!CollectionUtils.isEmpty(suppliersParam)) { // if1
			for (GoodsSelectionAndSuppliersResult param : suppliersParam) { // for
				if (param.getId() > 0) { // if选型
					Long result = this.baseDAOService.updateSuppliersInventory(param.getId(), (param.getGoodsInventory()));
					//success = DataUtil.verifyInventory(result);
					if(result!=null&&result>=0) {
						success = true;
					}
				}
			}
		}
		return success;
		
	}
	/**
	 * 标记队列状态
	 * @param member
	 * @param upStatusNum :该参数特别说明：当你想减值时传负值，当是增加值时传正整数值
	 * @throws Exception
	 */
	public void markQueueStatus(String member,int upStatusNum) {
		this.baseDAOService.markQueueStatus(member, upStatusNum);
	}
	public boolean markQueueStatusAndDeleteCacheMember(String member,int upStatusNum,String delkey) {
		return this.baseDAOService.markQueueStatusAndDeleteCacheMember(member, upStatusNum,delkey);
	}
	
	public GoodsInventoryQueueDO queryInventoryQueueDO(String key) {
		return this.baseDAOService.queryInventoryQueueDO(key);
	}
	
	public Long adjustGoodsWaterflood(Long goodsId, int num) {
		return this.baseDAOService.adjustGoodsWaterflood(goodsId, num);
	}
	
	public Long adjustSelectionWaterfloodById(Long selectionId, int num) {
		return this.baseDAOService.adjustSelectionWaterflood(selectionId, (num));
	}
	public List<Long> adjustSelectionWaterfloodById(Long goodsId,Long selectionId, int num) {
		return this.baseDAOService.adjustSelectionWaterflood(goodsId,selectionId, (num));
	}
	public Long adjustSuppliersWaterfloodById(Long suppliersId, int num) {
		return this.baseDAOService.adjustSuppliersWaterflood(suppliersId, (num));
	}
	public List<Long> adjustSuppliersWaterfloodById(Long goodsId,Long suppliersId, int num) {
		return this.baseDAOService.adjustSuppliersWaterflood(goodsId,suppliersId, (num));
	}
	
	public Long deleteGoodsInventory(Long goodsId) {
		return this.baseDAOService.deleteGoodsInventory(goodsId);
	}
	public Long deleteSelectionInventory(List<GoodsSelectionModel> selectionList){
		long result = 0;
		if (!CollectionUtils.isEmpty(selectionList)) { // if1
			for (GoodsSelectionModel param : selectionList) { // for
				if (param.getId() > 0) { // if选型
					result = this.baseDAOService.deleteSelectionInventory(param.getId());
				}
			}
		}
		return result;
	}
	public Long deleteSuppliersInventory(List<GoodsSuppliersModel> suppliersList){
		long result = 0;
		if (!CollectionUtils.isEmpty(suppliersList)) { // if1
			for (GoodsSuppliersModel param : suppliersList) { // for
				if (param.getId() > 0) { // if选型
					result = this.baseDAOService.deleteSuppliersInventory(param.getId());
				}
			}
		}
		return result;
	}
	/**
	 * 删除日志
	 * @param logActionDO
	 */
	public void lremLogQueue(GoodsInventoryActionDO logActionDO) {
		 this.baseDAOService.lremLogQueue(logActionDO);
	}
	/**
	 * 根据选型id获取选型库存
	 * @param selectionId
	 * @return
	 */
	public GoodsSelectionModel queryGoodsSelectionBySelectionId(long selectionId) {
		
		return ObjectUtils.toModel(this.querySelectionRelationById(selectionId));
	}
	/**
	 * 根据商品id获取商品 所有的选型库存列表
	 * @param goodsId
	 * @return
	 */
	public List<GoodsSelectionModel> queryGoodsSelectionListByGoodsId(long goodsId) {
		List<GoodsSelectionModel> result = null;
		Set<String> selectionIdsSet = this.baseDAOService.queryGoodsSelectionRelation(goodsId);
		if(!CollectionUtils.isEmpty(selectionIdsSet)) {
			result = new ArrayList<GoodsSelectionModel>();
			for(String selectionId: selectionIdsSet) {
				GoodsSelectionModel gsModel = this.queryGoodsSelectionBySelectionId(Long.parseLong(selectionId));
				if(gsModel!=null) {
					result.add(gsModel);
				}
				
			}
		}
		return result;
	}
	/**
	 * 根据选型id的list获取选型信息列表
	 * @param selectionIdList
	 * @return
	 */
	public List<GoodsSelectionModel> queryGoodsSelectionListBySelectionIdList(List<Long> selectionIdList) {
		List<GoodsSelectionModel> result = null;
		//Set<String> selectionIdsSet = this.baseDAOService.queryGoodsSelectionRelation(goodsId);
		if(!CollectionUtils.isEmpty(selectionIdList)) {
			result = new ArrayList<GoodsSelectionModel>();
			for(long selectionId: selectionIdList) {
				GoodsSelectionModel gsModel = this.queryGoodsSelectionBySelectionId(selectionId);
				if(gsModel!=null) {
					result.add(gsModel);
				}
				
			}
		}
		return result;
	}
	/**
	 * 根据商品分店id获取分店库存
	 * @param suppliersId
	 * @return
	 */
	public GoodsSuppliersModel queryGoodsSuppliersBySuppliersId(long suppliersId) {
		
		return ObjectUtils.toModel(this.querySuppliersInventoryById(suppliersId));
	}
	/**
	 * 根据分店id list获取分店列表
	 * @param suppliersIdList
	 * @return
	 */
	public List<GoodsSuppliersModel> queryGoodsSuppliersListBySuppliersIdList(List<Long> suppliersIdList) {
		List<GoodsSuppliersModel> result = null;
		//Set<String> suppliersIdsSet = this.baseDAOService.queryGoodsSuppliersRelation(goodsId);
		if(!CollectionUtils.isEmpty(suppliersIdList)) {
			result = new ArrayList<GoodsSuppliersModel>();
			for(long suppliersId: suppliersIdList) {
				GoodsSuppliersModel gsModel = this.queryGoodsSuppliersBySuppliersId(suppliersId);
				if(gsModel!=null) {
					result.add(gsModel);
				}
				
			}
		}
		return result;
	}
	/**
	 * 根据商品id获取商品所属分店库存信息列表
	 * @param goodsId
	 * @return
	 */
	public List<GoodsSuppliersModel> queryGoodsSuppliersListByGoodsId(long goodsId) {
		List<GoodsSuppliersModel> result = null;
		Set<String> suppliersIdsSet = this.baseDAOService.queryGoodsSuppliersRelation(goodsId);
		if(!CollectionUtils.isEmpty(suppliersIdsSet)) {
			result = new ArrayList<GoodsSuppliersModel>();
			for(String suppliersId: suppliersIdsSet) {
				GoodsSuppliersModel gsModel = this.queryGoodsSuppliersBySuppliersId(Long.parseLong(suppliersId));
				if(gsModel!=null) {
					result.add(gsModel);
				}
				
			}
		}
		return result;
	}
	/**
	 * 根据商品id获取商品库存信息，
	 * 包含(若有的话)选型的库存及分店的库存
	 * @param goodsId
	 * @return
	 */
	public GoodsInventoryModel queryGoodsInventoryByGoodsId(long goodsId) {
		return ObjectUtils.toModel(this.queryGoodsInventory(goodsId)
				//,this.queryGoodsSelectionListByGoodsId(goodsId)
				//,this.queryGoodsSuppliersListByGoodsId(goodsId)
				);
	}
	public GoodsInventoryModel queryAllInventoryDataByGoodsId(long goodsId) {
		return ObjectUtils.toModel(this.queryGoodsInventory(goodsId)
				,this.queryGoodsSelectionListByGoodsId(goodsId)
				,null/*this.queryGoodsSuppliersListByGoodsId(goodsId)*/
				);
	}
	/**
	 * 根据队列状态取队列列表
	 * @param status
	 * @return
	 */
	public List<GoodsInventoryQueueModel> queryInventoryQueueListByStatus (final Double status) {
		
		return ObjectUtils.convertSet(this.baseDAOService.queryInventoryQueueListByStatus(status));
	}
	/**
	 * 获取list中最后一条日志
	 * @param status
	 * @return
	 */
	public List<GoodsInventoryActionModel> queryLastIndexGoodsInventoryAction () {
		
		return ObjectUtils.getList(this.baseDAOService.queryLastIndexGoodsInventoryAction());
	}
	public List<GoodsInventoryActionModel> queryFirstInGoodsInventoryAction () {
		
		return ObjectUtils.getList(this.baseDAOService.queryFirstInGoodsInventoryAction());
	}
	
	public GoodsInventoryQueueModel queryGoodsInventoryQueue(String key) {
		
		return ObjectUtils.toModel(this.queryInventoryQueueDO(key));
	}
	/**
	 * 删除日志
	 * @param model
	 */
	public Long lremLogQueue(GoodsInventoryActionModel model) {
		 return this.baseDAOService.lremLogQueue(ObjectUtils.toDO(model));
	}
	public Long lremLogQueue1(GoodsInventoryActionModel model) {
		return this.baseDAOService.lremLogQueue1(ObjectUtils.toDO(model));
	}
	/**
	 * 删除队列member
	 * @param key
	 * @return
	 */
	public Long deleteQueueMember(String key) {
		return this.baseDAOService.deleteQueueMember(key);
	}
	/**
	 * 根据key获取缓存的member[队列]
	 * @param key
	 * @return
	 */
	public String queryMember(String key) {
		return this.baseDAOService.queryMember(key);
	}
	public String queryToken(String key) {
		return this.baseDAOService.queryToken(key);
	}
	/**
	 * 设置tag
	 * @param tag
	 * @param seconds
	 * @param tagValue
	 * @return
	 */
	public String setTag(String tag,int seconds, String tagValue) {
		return this.baseDAOService.setTag(tag,seconds,tagValue);
	}
	
	public boolean watch(String key,String tagval) {
		return this.baseDAOService.watch(key,tagval);
	}
	
	public String updateFields(Long goodsId, Map<String, String> hash) {
		 return this.baseDAOService.updateFileds(goodsId, hash);
	}
	
	public String updateSelectionFields(List<GoodsSelectionDO> selectionDOList) {
	    String success = null;
		if (!CollectionUtils.isEmpty(selectionDOList)) { // if1
			for (GoodsSelectionDO param : selectionDOList) { // for
				if (param.getId() > 0) { // if分店
					Map<String, String> hash = new HashMap<String, String>();
					hash.put(HashFieldEnum.suppliersSubId.toString(),  String.valueOf(param.getSuppliersSubId()));
					hash.put(HashFieldEnum.suppliersInventoryId.toString(), String.valueOf(param.getSuppliersInventoryId()));
					String retAck =	this.baseDAOService.updateSelectionFileds(param.getId(), hash);
					if(!retAck.equalsIgnoreCase("ok")) {
						return null;
					}else {
						success = retAck;
					}
		
				}
			}
		}else {
			return null;
		}
		return success;
	}
	
	
	public GoodsBaseInventoryDO queryGoodsBaseById(Long goodsBaseId){
		return baseDAOService.queryGoodsBaseById(goodsBaseId);
	}
	public Long queryLogQueueMaxLenth(String key) {
		//
		return baseDAOService.queryLogQueueMaxLenth(key);
	}
	public String lpop(String key) {

		return this.baseDAOService.lpop(key);

	}
	public Double zincrby(String key, double score, String member) {
				 return this.baseDAOService.zincrby(key, score, member);
			}
}
