package com.tuan.inventory.domain.repository;

import java.util.List;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.model.OrderGoodsSelectionModel;

public interface InventoryDeductReadWriteService {

	/**
	 * ���������Ϣ
	 * @param goodsId[��Ʒid],RedisInventoryDO[��Ʒ���������Ϣ],
	 * List<RedisGoodsSelectionRelationDO>[��Ʒѡ��],List<RedisGoodsSuppliersInventoryDO>[��Ʒ�ֵ�,�����ݲ���]
	 * @return
	 */
	public boolean createInventory(long goodsId,RedisInventoryDO riDO,List<RedisGoodsSelectionRelationDO> rgsrList,List<RedisGoodsSuppliersInventoryDO> rgsiList) throws Exception;
	/**
	 * ���¿��
	 * @param orderId
	 * @param goodsId
	 * @param pindaoId
	 * @param num
	 * @param limitStorage
	 * @param goodsSelectionList
	 * @return
	 * @throws Exception
	 */
	public boolean updateInventory(long orderId,long goodsId, int pindaoId, int num,int limitStorage,
			List<OrderGoodsSelectionModel> goodsSelectionList) throws Exception;
	/**
	 * ���������ص�ĳһ�����ֵ,ԭ�Ӳ���
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String insertSingleInventory(long goodsId,String field,String value) throws Exception;
	
	/**
	 * ���¿����ص�ĳһ�����ֵ,ԭ�Ӳ���
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String updateSingleInventory(long goodsId,String field,String value) throws Exception;
	/**
	 * ɾ����Ʒʱ�������Ʒ�Ŀ����Ϣ
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public boolean deleteInventory(long goodsId,List<OrderGoodsSelectionModel> goodsSelectionList) throws Exception;
}
