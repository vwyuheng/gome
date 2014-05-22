package com.tuan.inventory.domain.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryWMSDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.jedistools.RedisCacheUtil;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.util.QueueConstant;

public class CacheDAOServiceImpl implements BaseDAOService {

	@Resource
	RedisCacheUtil redisCacheUtil;
	
	@Override
	public boolean isExists(Long goodsId) {
		//已存在返回false,不存在返回true
		return this.redisCacheUtil.exists(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId))?false:true;
	}

	@Override
	public void pushLogQueues(GoodsInventoryActionDO logActionDO) {
		// 将库存日志队列信息压入到redis list
		this.redisCacheUtil.lpush(QueueConstant.QUEUE_LOGS_MESSAGE, JSONObject
				.fromObject(logActionDO).toString());
		
	}

	@Override
	public String saveInventory(Long goodsId, GoodsInventoryDO inventoryInfoDO) {

		return this.redisCacheUtil.hmset(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId), ObjectUtils.toHashMap(inventoryInfoDO));

	}

	@Override
	public boolean saveGoodsSelectionInventory(Long goodsId, GoodsSelectionDO selectionDO) {
		/*this.redisCacheUtil.sadd(
				QueueConstant.GOODS_SELECTION_RELATIONSHIP_PREFIX + ":"
						+ String.valueOf(goodsId),
				String.valueOf(selectionDO.getId()));
		this.redisCacheUtil.hmset(QueueConstant.SELECTION_INVENTORY_PREFIX
				+ ":" + String.valueOf(selectionDO.getId()), ObjectUtils.toHashMap(selectionDO));
*/
		return this.redisCacheUtil.saddAndhmset(QueueConstant.GOODS_SELECTION_RELATIONSHIP_PREFIX + ":"
				+ String.valueOf(goodsId), QueueConstant.SELECTION_INVENTORY_PREFIX
				+ ":" + String.valueOf(selectionDO.getId()), String.valueOf(selectionDO.getId()), ObjectUtils.toHashMap(selectionDO));
	}

	@Override
	public boolean saveGoodsSuppliersInventory(Long goodsId, GoodsSuppliersDO suppliersDO) {
		/*this.redisCacheUtil.sadd(
				QueueConstant.GOODS_SUPPLIERS_RELATIONSHIP_PREFIX + ":"
						+ String.valueOf(goodsId),
				String.valueOf(suppliersDO.getId()));
		this.redisCacheUtil.hmset(QueueConstant.SUPPLIERS_INVENTORY_PREFIX
				+ ":" + String.valueOf(suppliersDO.getId()), ObjectUtils.toHashMap(suppliersDO));*/
		
		return this.redisCacheUtil.saddAndhmset(QueueConstant.GOODS_SUPPLIERS_RELATIONSHIP_PREFIX + ":"
						+ String.valueOf(goodsId), QueueConstant.SUPPLIERS_INVENTORY_PREFIX
				+ ":" + String.valueOf(suppliersDO.getSuppliersId()), String.valueOf(suppliersDO.getSuppliersId()), ObjectUtils.toHashMap(suppliersDO));

	}

	@Override
	public boolean isGoodsExists(Long goodsId, String field) {  //存在返回false 不存在返回true
		return this.redisCacheUtil.hexists(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId),field)?false:true;
	}

	@Override
	public boolean isSelectionExists(Long selectionId, String field) {
		return this.redisCacheUtil.hexists(QueueConstant.SELECTION_INVENTORY_PREFIX + ":"
				+ String.valueOf(selectionId),field)?false:true;
	}

	@Override
	public boolean isSupplierExists(Long suppliesId, String field) {
		return this.redisCacheUtil.hexists(QueueConstant.SUPPLIERS_INVENTORY_PREFIX + ":"
				+ String.valueOf(suppliesId),field)?false:true;
	}

	@Override
	public GoodsInventoryDO queryGoodsInventory(Long goodsId) {
		Map<String, String> objMap = this.redisCacheUtil.hgetAll(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
						+ String.valueOf(goodsId));
		if (!CollectionUtils.isEmpty(objMap)) {
			return JsonUtils.convertStringToObject(JsonUtils.convertObjectToString(objMap), GoodsInventoryDO.class);
		}
		return null;
	}

	@Override
	public GoodsSelectionDO querySelectionRelationById(Long selectionId) {
		Map<String, String> objMap = this.redisCacheUtil
				.hgetAll(QueueConstant.SELECTION_INVENTORY_PREFIX + ":"
						+ String.valueOf(selectionId));
		if (!CollectionUtils.isEmpty(objMap)) {
			return JsonUtils.convertStringToObject(
					JsonUtils.convertObjectToString(objMap),
					GoodsSelectionDO.class);
		}
		return null;
	}

	@Override
	public GoodsSuppliersDO querySuppliersInventoryById(
			Long suppliersId) {
		Map<String, String> objMap = this.redisCacheUtil
				.hgetAll(QueueConstant.SUPPLIERS_INVENTORY_PREFIX + ":"
						+ String.valueOf(suppliersId));
		if (!CollectionUtils.isEmpty(objMap)) {
			return JsonUtils.convertStringToObject(
					JsonUtils.convertObjectToString(objMap),
					GoodsSuppliersDO.class);
		}
		return null;
	}



	@Override
	public Long updateGoodsInventory(Long goodsId, int num) {
		return this.redisCacheUtil.hincrBy(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId),
				HashFieldEnum.leftNumber.toString(), (num));
	}

	@Override
	public Long updateSelectionInventory(Long selectionId, int num) {
		return this.redisCacheUtil.hincrBy(QueueConstant.SELECTION_INVENTORY_PREFIX + ":"
				+ String.valueOf(selectionId),
				HashFieldEnum.leftNumber.toString(), (num));
	}

	@Override
	public Long updateSuppliersInventory(Long suppliersId, int num) {
		return this.redisCacheUtil.hincrBy(QueueConstant.SUPPLIERS_INVENTORY_PREFIX + ":"
				+ String.valueOf(suppliersId),
				HashFieldEnum.leftNumber.toString(), (num));
	}

	@Override
	public String pushQueueSendMsg(GoodsInventoryQueueDO queueDO) {
		// 将库存更新队列信息压入到redis zset集合 便于统计
		// job程序中会每次将score为1的元素取出，做库存消息更新的处理，处理完根据key score
		// member(因id都是唯一的，因此每个member都是不一样的)立即清空源集合中的相关元素，并重复操作
		// 如下 可以指定score值取值 ZADD salary 2500 jack
		// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
		// ，2取到相应member后，按照member及其删除 [ZREM key member]
		// 删除指定score的元素 ZREMRANGEBYSCORE salary 2500 2500
		//String jsonMember = JSONObject.fromObject(queueDO).toString();
		// 缓存队列的key、member信息 一年失效
		//this.redisCacheUtil.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
			//	+ String.valueOf(queueDO.getId()), 3600*24*365, jsonMember);
		// zset key score value 其中score作为status用
		//this.redisCacheUtil.zadd(QueueConstant.QUEUE_SEND_MESSAGE,
				//Double.valueOf(ResultStatusEnum.LOCKED.getCode()),
				//Double.valueOf(ResultStatusEnum.CONFIRM.getCode()),  //测试用
				//jsonMember);
		String queueKeyId = String.valueOf(queueDO.getId());
		boolean success = this.redisCacheUtil.setexAndzadd(QueueConstant.QUEUE_KEY_MEMBER + ":"+ queueKeyId, 
				QueueConstant.QUEUE_SEND_MESSAGE, queueDO);
		if(success) {
			return queueKeyId;
		}else {
			return null;
		}
	}

	
	@Override
	public void markQueueStatus(String member, int upStatusNum) {
		// 根据key取出缓存的对象，仅系统运行正常时有用，因为其有有效期默认是60分钟
		//String member = this.redisCacheUtil.get(QueueConstant.QUEUE_KEY_MEMBER + ":"
			//	+ key);
		// 将消息发送队列状态更新为:ResultStatusEnum 对应的队列状态值
		// Double scoreAck =
		this.redisCacheUtil.zincrby(QueueConstant.QUEUE_SEND_MESSAGE, (upStatusNum),
				member);
		
		
	}
	
	@Override
	public void markQueueStatusAndDeleteCacheMember(String member, int upStatusNum,String delkey) {
	
		// 根据key取出缓存的对象，仅系统运行正常时有用，因为其有有效期默认是60分钟
		//String member = this.redisCacheUtil.get(QueueConstant.QUEUE_KEY_MEMBER + ":"+ key);
		// 将消息发送队列状态更新为:ResultStatusEnum 对应的队列状态值
		// Double scoreAck =
		
		//this.redisCacheUtil.zincrby(QueueConstant.QUEUE_SEND_MESSAGE, (upStatusNum),member);
		this.redisCacheUtil.zincrbyAnddel(QueueConstant.QUEUE_SEND_MESSAGE, member,  (upStatusNum), QueueConstant.QUEUE_KEY_MEMBER + ":"+delkey);
		
		
	}

	@Override
	public GoodsInventoryQueueDO queryInventoryQueueDO(String key) {
		        // 根据key取出缓存的对象，仅系统运行正常时[能正常调用该接口时]有用，因为其有有效期默认是60分钟
				String member = this.redisCacheUtil.get(QueueConstant.QUEUE_KEY_MEMBER
						+ ":" + key);
				GoodsInventoryQueueDO queueDO = null;
				if(StringUtils.isNotEmpty(member)) {
					
//					queueDO = (GoodsInventoryQueueDO) LogUtil.jsonToObject(member,GoodsInventoryQueueDO.class);
					queueDO = JsonUtils.convertStringToObject(member,GoodsInventoryQueueDO.class);
				}
				return queueDO;
	}

	@Override
	public Long adjustGoodsWaterflood(Long goodsId, int num) {
		// hincrby返回的是field更新后的值
		return this.redisCacheUtil.hincrBy(QueueConstant.GOODS_INVENTORY_PREFIX
				+ ":"+String.valueOf(goodsId),
				HashFieldEnum.waterfloodVal.toString(), (num));
	}

	@Override
	public Long adjustSelectionWaterflood(Long selectionId, int num) {
		return this.redisCacheUtil.hincrBy(QueueConstant.SELECTION_INVENTORY_PREFIX
				+ ":"+String.valueOf(selectionId),
				HashFieldEnum.waterfloodVal.toString(), (num));
	}

	@Override
	public Long adjustSuppliersWaterflood(Long suppliersId, int num) {
		return this.redisCacheUtil.hincrBy(QueueConstant.SUPPLIERS_INVENTORY_PREFIX
				+ ":"+String.valueOf(suppliersId),
				HashFieldEnum.waterfloodVal.toString(), (num));
	}

	@Override
	public Long deleteGoodsInventory(Long goodsId) {
		// 根据选型id删除选型库存信息
		return this.redisCacheUtil.del(QueueConstant.GOODS_INVENTORY_PREFIX
						+ ":"
						+ String.valueOf(goodsId));
	}

	@Override
	public Long deleteSelectionInventory(Long selectionId) {
		
		return this.redisCacheUtil.del(QueueConstant.SELECTION_INVENTORY_PREFIX
				+ ":"
				+ String.valueOf(selectionId));
	}

	@Override
	public Long deleteSuppliersInventory(Long suppliersId) {
		
		return this.redisCacheUtil.del(QueueConstant.SUPPLIERS_INVENTORY_PREFIX
				+ ":"
				+ String.valueOf(suppliersId));
	}

	@Override
	public void lremLogQueue(GoodsInventoryActionDO logActionDO) {
		// 将库存日志队列信息移除:总是移除最后一条从list最末端往前找 value 相同的对象
		this.redisCacheUtil.lrem(QueueConstant.QUEUE_LOGS_MESSAGE, (-1), JSONObject
				.fromObject(logActionDO).toString());
	}

	@Override
	public Set<String> queryGoodsSelectionRelation(Long goodsId) {
		
		return this.redisCacheUtil.smembers(QueueConstant.GOODS_SELECTION_RELATIONSHIP_PREFIX + ":"
						+ String.valueOf(goodsId));
	}

	@Override
	public Set<String> queryGoodsSuppliersRelation(Long goodsId) {
		
		return this.redisCacheUtil.smembers(QueueConstant.GOODS_SUPPLIERS_RELATIONSHIP_PREFIX + ":"
				+ String.valueOf(goodsId));
	}

	@Override
	public Set<String> queryInventoryQueueListByStatus(
			Double status) {
		return this.redisCacheUtil.zrangeByScore(QueueConstant.QUEUE_SEND_MESSAGE, status, status);
	}

	@Override
	public String queryLastIndexGoodsInventoryAction() {
		
		return this.redisCacheUtil.lindex(QueueConstant.QUEUE_LOGS_MESSAGE, (-1));
	}

	@Override
	public Long deleteQueueMember(String key) {
		// 根据选型id删除选型库存信息
		return this.redisCacheUtil.del(QueueConstant.QUEUE_KEY_MEMBER + ":"
						+ key);
	}

	@Override
	public String queryMember(String key) {
		
		return  this.redisCacheUtil.get(QueueConstant.QUEUE_KEY_MEMBER + ":"+ key);
	}
	/**
	 * 商品库存调整
	 */
	@Override
	public List<Long> adjustGoodsInventory(Long goodsId, int num,int limitStorage) {

		return this.redisCacheUtil.hincrByAndhincrBy(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId),
				HashFieldEnum.totalNumber.toString(),
				HashFieldEnum.leftNumber.toString(), 
				HashFieldEnum.limitStorage.toString(),
				(num),(limitStorage));
	
	}

	@Override
	public List<Long> adjustSelectionInventory(Long goodsId,Long selectionId, int num) {
		return this.redisCacheUtil.hincrByAndhincrBy4sel(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId),QueueConstant.SELECTION_INVENTORY_PREFIX + ":"
				+ String.valueOf(selectionId),
				HashFieldEnum.totalNumber.toString(),
				HashFieldEnum.leftNumber.toString(), (num));
	}

	@Override
	public List<Long> adjustSuppliersInventory(Long goodsId,Long suppliersId, int num) {
		return this.redisCacheUtil.hincrByAndhincrBy4supp(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId),QueueConstant.SUPPLIERS_INVENTORY_PREFIX + ":"
				+ String.valueOf(suppliersId),
				HashFieldEnum.totalNumber.toString(),
				HashFieldEnum.leftNumber.toString(), (num));
	}

	@Override
	public List<Long> adjustSelectionWaterflood(Long goodsId, Long selectionId,
			int num) {
		return this.redisCacheUtil.hincrByAndhincrBy4wf(QueueConstant.GOODS_INVENTORY_PREFIX
				+ ":"+String.valueOf(goodsId),QueueConstant.SELECTION_INVENTORY_PREFIX
				+ ":"+String.valueOf(selectionId),
				HashFieldEnum.waterfloodVal.toString(), (num));
	}

	@Override
	public List<Long> adjustSuppliersWaterflood(Long goodsId, Long suppliersId,
			int num) {
		return this.redisCacheUtil.hincrByAndhincrBy4wf(QueueConstant.GOODS_INVENTORY_PREFIX
				+ ":"+String.valueOf(goodsId),QueueConstant.SUPPLIERS_INVENTORY_PREFIX
				+ ":"+String.valueOf(suppliersId),
				HashFieldEnum.waterfloodVal.toString(), (num));
	}

	@Override
	public void saveGoodsWmsInventory(GoodsInventoryWMSDO wmsDO) {
		
      /* this.redisCacheUtil.saddAndhmset(QueueConstant.GOODS_WMS_RELATIONSHIP_PREFIX + ":"
				+ String.valueOf(goodsId), QueueConstant.WMS_INVENTORY_PREFIX
		+ ":" + wmsDO.getWmsGoodsId(), wmsDO.getWmsGoodsId(), ObjectUtils.toHashMap(wmsDO));*/
       
       this.redisCacheUtil.hmset(QueueConstant.WMS_INVENTORY_PREFIX + ":"
				+ wmsDO.getWmsGoodsId(), ObjectUtils.toHashMap(wmsDO));
 
     }

	@Override
	public GoodsInventoryWMSDO queryWmsInventoryById(String wmsGoodsId) {
		Map<String, String> objMap = this.redisCacheUtil
				.hgetAll(QueueConstant.WMS_INVENTORY_PREFIX + ":"
						+ wmsGoodsId);
		if (!CollectionUtils.isEmpty(objMap)) {
			return JsonUtils.convertStringToObject(
					JsonUtils.convertObjectToString(objMap),
					GoodsInventoryWMSDO.class);
		}
		return null;
	}

	@Override
	public List<Long> updateGoodsWms(String wmsGoodsId, int num) {
		return this.redisCacheUtil.hincrByAndhincrBy(
				QueueConstant.WMS_INVENTORY_PREFIX + ":"
				+ wmsGoodsId,
				HashFieldEnum.leftNumber.toString(),
				HashFieldEnum.totalNumber.toString(),
				(num));
	}

	@Override
	public List<Long> adjustSelectionWmsInventory(Long selectionId,int adjustLeftNum,int adjustTotalNum) {
		return this.redisCacheUtil.hincrByAndhincrBy4wms(QueueConstant.SELECTION_INVENTORY_PREFIX + ":"
				+ String.valueOf(selectionId),
				HashFieldEnum.totalNumber.toString(),
				HashFieldEnum.leftNumber.toString(), adjustTotalNum,(adjustLeftNum));
	}

	@Override
	public boolean isWmsExists(String wmsGoodsId) {
		//已存在返回false,不存在返回true
		return this.redisCacheUtil.exists(QueueConstant.WMS_INVENTORY_PREFIX + ":"
				+ wmsGoodsId)?false:true;
	}

	@Override
	public void saveGoodsSelectionWmsInventory(GoodsSelectionDO selectionDO) {
        this.redisCacheUtil.hmset(QueueConstant.SELECTION_INVENTORY_PREFIX
		+ ":" + String.valueOf(selectionDO.getId()),ObjectUtils.toHashMap(selectionDO));
    }

	@Override
	public String setTag(String tag,int seconds, String tagValue) {
		return this.redisCacheUtil.setex(tag, seconds, tagValue);
	}

	@Override
	public boolean watch(String key,String tagval) {
		return this.redisCacheUtil.watch(key,tagval);
	}

	@Override
	public void updateFileds(Long goodsId, Map<String, String> hash) {

		this.redisCacheUtil.hmset(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId), hash);

	}

	@Override
	public void updateSelectionFileds(Long selectionId, Map<String, String> hash) {

		this.redisCacheUtil.hmset(QueueConstant.SELECTION_INVENTORY_PREFIX + ":"
				+ String.valueOf(selectionId), hash);

	}

	
	
}
