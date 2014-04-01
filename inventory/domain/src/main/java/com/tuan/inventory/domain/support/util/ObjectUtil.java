package com.tuan.inventory.domain.support.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.redis.RedisInventoryDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
import com.tuan.inventory.domain.support.bean.job.NotifyMessage;
import com.tuan.inventory.model.OrderGoodsSelectionModel;
import com.tuan.inventory.model.RedisInventoryModel;
/**
 * map与object对象转换工具类
 * @author henry.yu
 * @2014/3/10
 */
public class ObjectUtil {

	/**
	 * 将一个 JavaBean 对象转化为一个 Map
	 * 
	 * @param bean
	 *            要转化的JavaBean 对象
	 * @return 转化出来的 Map 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> convertBean(Object bean)
			throws IntrospectionException, IllegalAccessException,
			InvocationTargetException {
		@SuppressWarnings("rawtypes")
		Class type = bean.getClass();
		Map<String, String> returnMap = new HashMap<String, String>();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);

		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, String.valueOf(result));
				} else {
					// returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}

	/**
	 * 将一个 Map 对象转化为一个 JavaBean
	 * 
	 * @param type
	 *            要转化的类型
	 * @param map
	 *            包含属性值的 map
	 * @return 转化出来的 JavaBean 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InstantiationException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertMap(Class type, Map<String, String> map)
			throws IntrospectionException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		Object obj = type.newInstance(); // 创建 JavaBean 对象

		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();

			if (map.containsKey(propertyName)) {
				// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
				Object value = map.get(propertyName);

				Object[] args = new Object[1];
				// @注意value的类型
				args[0] = Integer.valueOf((String) value);

				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
	}
	/**
	 * 用于返回队列列表信息
	 * @param members
	 * @return
	 */
	public static List<RedisInventoryQueueDO> convertSet(Set<String> members/*,String score*/) {
		List<RedisInventoryQueueDO> result = null;
		if (!CollectionUtils.isEmpty(members)) {
			result = new ArrayList<RedisInventoryQueueDO>();
			for(String member:members) {
				RedisInventoryQueueDO memberDO = (RedisInventoryQueueDO) LogUtil.jsonToObject(member, RedisInventoryQueueDO.class);
				//更新状态为真实状态
				//memberDO.setStatus(score);
				result.add(memberDO);
			}
		}
		return result;
	}
	
	public static List<RedisInventoryLogDO> convertList(List<String> elements) {
		List<RedisInventoryLogDO> result = null;
		if (!CollectionUtils.isEmpty(elements)) {
			result = new ArrayList<RedisInventoryLogDO>();
			for(String element:elements) {
				RedisInventoryLogDO memberDO = (RedisInventoryLogDO) LogUtil.jsonToObject(element, RedisInventoryLogDO.class);
				//更新状态为真实状态
				//memberDO.setStatus(score);
				result.add(memberDO);
			}
		}
		return result;
	}
	
	/**
	 * 装配notifyserver消息对象
	 * @param userId
	 * @param orderId
	 * @param goodsId
	 * @param limitStorage
	 * @param waterfloodVal
	 * @param variableQuantityJsonData
	 * @return
	 */
	public static NotifyMessage asemblyNotifyMessage(Long userId, long orderId,
			Long goodsId, int limitStorage,int waterfloodVal,int totalNumber, int leftNumber,List<OrderGoodsSelectionModel> goodsSelectionList) {
		//构建消息体
				NotifyMessage message = new NotifyMessage();
				message.setUserId(userId);
				message.setOrderId(orderId);
				message.setGoodsId(goodsId);
				message.setLimitStorage(limitStorage);
				message.setWaterfloodVal(waterfloodVal);
				message.setTotalNumber(totalNumber);
				message.setLeftNumber(leftNumber);
				message.setGoodsSelectionList(goodsSelectionList);
				
				return message;
	}
	
	public static NotifyMessage asemblyNotifyMessage(Long userId,RedisInventoryModel result) {
		//构建消息体
				NotifyMessage message = new NotifyMessage();
				message.setUserId(userId);
				//message.setOrderId(result.get);
				if(result==null) {
					return null;
				}
				message.setGoodsId(result.getGoodsId());
				message.setLimitStorage(result.getLimitStorage());
				message.setWaterfloodVal(result.getWaterfloodVal());
				message.setTotalNumber(result.getTotalNumber());
				message.setLeftNumber(result.getLeftNumber());
				message.setGoodsSelectionList(result.getGoodsSelectionList());
				
				return message;
	}
	
	public static RedisInventoryModel asemblyRedisInventoryBean(Long goodsId,int totalNumber,int leftNumber, int limitStorage,int waterfloodVal,List<OrderGoodsSelectionModel> goodsSelectionList) {
		RedisInventoryModel  riBean = new RedisInventoryModel();
		riBean.setGoodsId(goodsId);
		riBean.setTotalNumber(totalNumber);
		riBean.setLeftNumber(leftNumber);
		riBean.setLimitStorage(limitStorage);
		riBean.setWaterfloodVal(waterfloodVal);
		riBean.setGoodsSelectionList(goodsSelectionList);
		return riBean;
	}
	
	public static RedisInventoryModel asemblyRedisInventoryBean(RedisInventoryDO riDo,List<OrderGoodsSelectionModel> goodsSelectionList) {
		RedisInventoryModel  riBean = new RedisInventoryModel();
		if(riDo==null) {
			return null;
		}
		riBean.setGoodsId(Long.valueOf(riDo.getGoodsId()));
		riBean.setTotalNumber(riDo.getTotalNumber());
		riBean.setLeftNumber(riDo.getLeftNumber());
		riBean.setLimitStorage(riDo.getLimitStorage());
		riBean.setWaterfloodVal(riDo.getWaterfloodVal());
		riBean.setGoodsSelectionList(goodsSelectionList);
		return riBean;
	}
	
	public static RedisInventoryDO asemblyRedisInventoryDO(Long goodsId,int totalNumber,int leftNumber, int limitStorage,int waterfloodVal) {
		RedisInventoryDO  riDO = new RedisInventoryDO();
		if(goodsId==null) {
			return null;
		}
		riDO.setGoodsId(goodsId.intValue());
		riDO.setTotalNumber(totalNumber);
		riDO.setLeftNumber(leftNumber);
		riDO.setLimitStorage(limitStorage);
		riDO.setWaterfloodVal(waterfloodVal);
		//riDO.setGoodsSelectionList(goodsSelectionList);
		return riDO;
	}
	
	public static RedisInventoryModel switchBean(RedisInventoryDO riDo) {
		RedisInventoryModel  riBean = new RedisInventoryModel();
		if(riDo==null) {
			return null;
		}
		riBean.setGoodsId(Long.valueOf(riDo.getGoodsId()));
		riBean.setTotalNumber(riDo.getTotalNumber());
		riBean.setLeftNumber(riDo.getLeftNumber());
		riBean.setLimitStorage(riDo.getLimitStorage());
		riBean.setWaterfloodVal(riDo.getWaterfloodVal());
		//riBean.setGoodsSelectionList(goodsSelectionList);
		return riBean;
	}
	
}
