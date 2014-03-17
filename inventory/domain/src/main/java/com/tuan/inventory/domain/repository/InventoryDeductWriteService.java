package com.tuan.inventory.domain.repository;

import java.util.List;
import java.util.Map;

import com.tuan.inventory.dao.data.redis.RedisGoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.redis.RedisGoodsSuppliersInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.model.OrderGoodsSelectionModel;
/**
 * 库存扣减写服务接口
 * @author henry.yu
 * @date 2014/3/13
 */
public interface InventoryDeductWriteService {

	/**
	 * 新增库存信息
	 * @param goodsId[商品id],RedisInventoryDO[商品库存主体信息],
	 * List<RedisGoodsSelectionRelationDO>[商品选型],List<RedisGoodsSuppliersInventoryDO>[商品分店]
	 * @return
	 */
	public Boolean createInventory(final long goodsId,final RedisInventoryDO riDO,final List<RedisGoodsSelectionRelationDO> rgsrList,final List<RedisGoodsSuppliersInventoryDO> rgsiList) throws Exception;
	/**
	 * 更新库存
	 * @param orderId
	 * @param goodsId
	 * @param pindaoId
	 * @param num
	 * @param limitStorage
	 * @param goodsSelectionList
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> updateInventory(final long orderId,
			final long goodsId, final int pindaoId, final int num,
			final int limitStorage,
			final List<OrderGoodsSelectionModel> goodsSelectionList,
			final Long userId, final String system, final String clientIp) throws Exception;
	/**
	 * 新增库存相关的某一个域的值,原子操作
	 * 新增注水值等
	 * 该域不存在的情况下
	 * 注意新增注水值时，指定了field：waterfloodVal
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public boolean insertSingleInventoryInfoNotExist(final long goodsId, final String field, final String value) throws Exception;
	
	/**
	 * 更新库存相关的某一个域的值,原子操作
	 * 更新注水值等
	 * 这个更新是覆盖旧值，替换为新值的更新,且不存在则创建
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public boolean updateOverrideSingleInventoryInfo(long goodsId,String field,String value) throws Exception;
	/**
	 * 删除商品时清除该商品的库存信息
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public boolean deleteInventory(final long goodsId,final List<OrderGoodsSelectionModel> goodsSelectionList) throws Exception;
	/**
	 * 库存回调确认接口
	 * 当调用方对库存的操作一切正常时 确认参数传1，当调用方发生异常时 在catch到异常时回调传确认参数为5
	 * @param ack 确认参数 1：正常 5：异常
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean inventoryCallbackAck(String ack,String key) throws Exception;
	/**
	 * 库存值调整服务
	 * @param key 
	 * @param field 可传 totalNumber、leftNumber、waterfloodVal，其他field勿用此方法
	 * @param num 调整值：可调整(+),亦可调减(-) 默认调增,若调减请传负参数
	 * @return
	 * @throws Exception
	 */
	public boolean inventoryAdjustment(String key,String field,int num) throws Exception;
	/**
	 * 注水值调整服务,指定了field：waterfloodVal
	 * @param key 
	 * @param num 调整值：可调整(+),亦可调减(-) 默认调增,若调减请传负参数
	 * @return
	 * @throws Exception
	 */
	public boolean waterfloodValAdjustment(String key,int num) throws Exception;
}
