package com.tuan.inventory.domain.support;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryQueueDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.dao.data.redis.GoodsSuppliersDO;
import com.tuan.inventory.domain.support.enu.HashFieldEnum;
import com.tuan.inventory.domain.support.jedistools.RedisCacheUtil;
import com.tuan.inventory.domain.support.util.JsonUtils;
import com.tuan.inventory.domain.support.util.LogUtil;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.model.enu.ResultStatusEnum;
import com.tuan.inventory.model.util.QueueConstant;

public class CacheDAOServiceImpl implements BaseDAOService {

	@Resource
	RedisCacheUtil redisCacheUtil;
	
	@Override
	public boolean isExists(Long goodsId) {
		//�Ѵ��ڷ���false,�����ڷ���true
		return this.redisCacheUtil.exists(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId))?false:true;
	}

	@Override
	public void pushLogQueues(GoodsInventoryActionDO logActionDO) {
		// �������־������Ϣѹ�뵽redis list
		this.redisCacheUtil.lpush(QueueConstant.QUEUE_LOGS_MESSAGE, JSONObject
				.fromObject(logActionDO).toString());
		
	}

	@Override
	public void saveInventory(Long goodsId, GoodsInventoryDO inventoryInfoDO) {

		this.redisCacheUtil.hmset(QueueConstant.GOODS_INVENTORY_PREFIX + ":"
				+ String.valueOf(goodsId), ObjectUtils.toHashMap(inventoryInfoDO));

	}

	@Override
	public void saveGoodsSelectionInventory(Long goodsId, GoodsSelectionDO selectionDO) {
		this.redisCacheUtil.sadd(
				QueueConstant.GOODS_SELECTION_RELATIONSHIP_PREFIX + ":"
						+ String.valueOf(goodsId),
				String.valueOf(selectionDO.getId()));
		this.redisCacheUtil.hmset(QueueConstant.SELECTION_INVENTORY_PREFIX
				+ ":" + String.valueOf(selectionDO.getId()), ObjectUtils.toHashMap(selectionDO));

	}

	@Override
	public void saveGoodsSuppliersInventory(Long goodsId, GoodsSuppliersDO suppliersDO) {
		this.redisCacheUtil.sadd(
				QueueConstant.GOODS_SUPPLIERS_RELATIONSHIP_PREFIX + ":"
						+ String.valueOf(goodsId),
				String.valueOf(suppliersDO.getId()));
		this.redisCacheUtil.hmset(QueueConstant.SUPPLIERS_INVENTORY_PREFIX
				+ ":" + String.valueOf(suppliersDO.getId()), ObjectUtils.toHashMap(suppliersDO));

	}

	@Override
	public boolean isGoodsExists(Long goodsId, String field) {  //���ڷ���false �����ڷ���true
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
	public void pushQueueSendMsg(GoodsInventoryQueueDO queueDO) {
		// �������¶�����Ϣѹ�뵽redis zset���� ����ͳ��
		// job�����л�ÿ�ν�scoreΪ1��Ԫ��ȡ�����������Ϣ���µĴ������������key score
		// member(��id����Ψһ�ģ����ÿ��member���ǲ�һ����)�������Դ�����е����Ԫ�أ����ظ�����
		// ���� ����ָ��scoreֵȡֵ ZADD salary 2500 jack
		// ZRANGEBYSCORE salary 2500 2500 WITHSCORES
		// ��2ȡ����Ӧmember�󣬰���member����ɾ�� [ZREM key member]
		// ɾ��ָ��score��Ԫ�� ZREMRANGEBYSCORE salary 2500 2500
		String jsonMember = JSONObject.fromObject(queueDO)
				.toString();
		// ������е�key��member��Ϣ 1СʱʧЧ
		this.redisCacheUtil.setex(QueueConstant.QUEUE_KEY_MEMBER + ":"
				+ String.valueOf(queueDO.getId()), 3600, jsonMember);
		// zset key score value ����score��Ϊstatus��
		this.redisCacheUtil.zadd(QueueConstant.QUEUE_SEND_MESSAGE,
				Double.valueOf(ResultStatusEnum.LOCKED.getCode()),
				//Double.valueOf(ResultStatusEnum.CONFIRM.getCode()),  //������
				jsonMember);
		
	}

	@Override
	public void markQueueStatus(String key, int upStatusNum) {
		// ����keyȡ������Ķ��󣬽�ϵͳ��������ʱ���ã���Ϊ������Ч��Ĭ����60����
		String member = this.redisCacheUtil.get(QueueConstant.QUEUE_KEY_MEMBER + ":"
				+ key);
		// ����Ϣ���Ͷ���״̬����Ϊ:ResultStatusEnum ��Ӧ�Ķ���״ֵ̬
		// Double scoreAck =
		this.redisCacheUtil.zincrby(QueueConstant.QUEUE_SEND_MESSAGE, (upStatusNum),
				member);
		
	}

	@Override
	public GoodsInventoryQueueDO queryInventoryQueueDO(String key) {
		        // ����keyȡ������Ķ��󣬽�ϵͳ��������ʱ[���������øýӿ�ʱ]���ã���Ϊ������Ч��Ĭ����60����
				String member = this.redisCacheUtil.get(QueueConstant.QUEUE_KEY_MEMBER
						+ ":" + key);
				GoodsInventoryQueueDO queueDO = null;
				if(StringUtils.isNotEmpty(member)) {
					queueDO = (GoodsInventoryQueueDO) LogUtil.jsonToObject(member,GoodsInventoryQueueDO.class);
				}
				return queueDO;
	}

	@Override
	public Long adjustGoodsWaterflood(Long goodsId, int num) {
		// hincrby���ص���field���º��ֵ
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
		// ����ѡ��idɾ��ѡ�Ϳ����Ϣ
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
		// �������־������Ϣ�Ƴ�:�����Ƴ����һ����list��ĩ����ǰ�� value ��ͬ�Ķ���
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
		// ����ѡ��idɾ��ѡ�Ϳ����Ϣ
		return this.redisCacheUtil.del(QueueConstant.QUEUE_KEY_MEMBER + ":"
						+ key);
	}
}
