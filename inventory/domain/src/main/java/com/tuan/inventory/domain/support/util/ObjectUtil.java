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

import com.tuan.inventory.dao.data.redis.RedisInventoryLogDO;
import com.tuan.inventory.dao.data.redis.RedisInventoryQueueDO;
/**
 * map��object����ת��������
 * @author henry.yu
 * @2014/3/10
 */
public class ObjectUtil {

	/**
	 * ��һ�� JavaBean ����ת��Ϊһ�� Map
	 * 
	 * @param bean
	 *            Ҫת����JavaBean ����
	 * @return ת�������� Map ����
	 * @throws IntrospectionException
	 *             �������������ʧ��
	 * @throws IllegalAccessException
	 *             ���ʵ���� JavaBean ʧ��
	 * @throws InvocationTargetException
	 *             ����������Ե� setter ����ʧ��
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
	 * ��һ�� Map ����ת��Ϊһ�� JavaBean
	 * 
	 * @param type
	 *            Ҫת��������
	 * @param map
	 *            ��������ֵ�� map
	 * @return ת�������� JavaBean ����
	 * @throws IntrospectionException
	 *             �������������ʧ��
	 * @throws IllegalAccessException
	 *             ���ʵ���� JavaBean ʧ��
	 * @throws InstantiationException
	 *             ���ʵ���� JavaBean ʧ��
	 * @throws InvocationTargetException
	 *             ����������Ե� setter ����ʧ��
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertMap(Class type, Map<String, String> map)
			throws IntrospectionException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // ��ȡ������
		Object obj = type.newInstance(); // ���� JavaBean ����

		// �� JavaBean ��������Ը�ֵ
		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();

			if (map.containsKey(propertyName)) {
				// ����һ����� try ������������һ�����Ը�ֵʧ�ܵ�ʱ��Ͳ���Ӱ���������Ը�ֵ��
				Object value = map.get(propertyName);

				Object[] args = new Object[1];
				// @ע��value������
				args[0] = Integer.valueOf((String) value);

				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
	}
	/**
	 * ���ڷ��ض����б���Ϣ
	 * @param members
	 * @return
	 */
	public static List<RedisInventoryQueueDO> convertSet(Set<String> members/*,String score*/) {
		List<RedisInventoryQueueDO> result = null;
		if (!CollectionUtils.isEmpty(members)) {
			result = new ArrayList<RedisInventoryQueueDO>();
			for(String member:members) {
				RedisInventoryQueueDO memberDO = (RedisInventoryQueueDO) LogUtil.jsonToObject(member, RedisInventoryQueueDO.class);
				//����״̬Ϊ��ʵ״̬
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
				//����״̬Ϊ��ʵ״̬
				//memberDO.setStatus(score);
				result.add(memberDO);
			}
		}
		return result;
	}
}
