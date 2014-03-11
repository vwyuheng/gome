package com.tuan.inventory.domain.support.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;

public class ObjectUtil {

	/**
     * ��һ�� JavaBean ����ת��Ϊһ��  Map
     * @param bean Ҫת����JavaBean ����
     * @return ת��������  Map ����
     * @throws IntrospectionException �������������ʧ��
     * @throws IllegalAccessException ���ʵ���� JavaBean ʧ��
     * @throws InvocationTargetException ����������Ե� setter ����ʧ��
     */
    //@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String,String> convertBean(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        @SuppressWarnings("rawtypes")
		Class type = bean.getClass();
        Map<String,String> returnMap = new HashMap<String,String>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, String.valueOf(result));
                } else {
                    //returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }
    
    
    /**
     * ��һ�� Map ����ת��Ϊһ�� JavaBean
     * @param type Ҫת��������
     * @param map ��������ֵ�� map
     * @return ת�������� JavaBean ����
     * @throws IntrospectionException �������������ʧ��
     * @throws IllegalAccessException ���ʵ���� JavaBean ʧ��
     * @throws InstantiationException ���ʵ���� JavaBean ʧ��
     * @throws InvocationTargetException ����������Ե� setter ����ʧ��
     */
    @SuppressWarnings("rawtypes")
	public static Object convertMap(Class type, Map<String,String> map)
            throws IntrospectionException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // ��ȡ������
        Object obj = type.newInstance(); // ���� JavaBean ����

        // �� JavaBean ��������Ը�ֵ
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();

            if (map.containsKey(propertyName)) {
                // ����һ����� try ������������һ�����Ը�ֵʧ�ܵ�ʱ��Ͳ���Ӱ���������Ը�ֵ��
                Object value = map.get(propertyName);

                Object[] args = new Object[1];
                //@ע��value������
                args[0] = Integer.valueOf((String) value);

                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }
    
    public static GoodsSelectionRelationDO convertMap(Map<String,String> map) {
    	GoodsSelectionRelationDO result = null;
    	if(!CollectionUtils.isEmpty(map)) {
    		result = new GoodsSelectionRelationDO();
    		for (Map.Entry<String,String> parm:map.entrySet()) {
    			parm.getKey();
    		}
    	}
    	return result;
    }
}