package test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;

public class Test {
	private static boolean isGoodsOf;
	public static void main(String[] args) {
		//RedisGoodsSelectionRelationDO rdo = new RedisGoodsSelectionRelationDO();
		GoodsSelectionRelationDO rdo = new GoodsSelectionRelationDO();
		
		rdo.setId(1);
		//rdo.setGoodsId(2);
		rdo.setGoodTypeId(3);
	    rdo.setLeftNumber(100);
	    rdo.setTotalNumber(1000);
		//rdo.setSuppliersInventoryId(4);
		rdo.setLimitStorage(0);
		
		//JSONObject jsonObject = new JSONObject();
		//jsonObject.accumulate("code", "1000");
		//jsonObject.accumulate("msg", "success");
		//jsonObject.accumulate("data", rdo);
		//String objStr = JSON.p(rdo);
		Test test = new Test();
	//	GoodsSelectionRelationDO do1 = (GoodsSelectionRelationDO) LogUtil.jsonToObject(objStr,GoodsSelectionRelationDO.class);
		//System.out.println("test="+JsonUtils.convertStringToObject(JsonUtils.convertObjectToString(rdo), Map.class));
		//System.out.println(JSONObject.fromObject(rdo));
		System.out.println("11test11="+toHashMap(rdo));
//      GoodsSelectionRelationDO rdo1 = new GoodsSelectionRelationDO();
//		
//      rdo1.setId(1);
//     // rdo1.setGoodsId(2);
//      rdo1.setGoodTypeId(3);
//      rdo1.setLeftNumber(100);
//      rdo1.setTotalNumber(1000);
//		//rdo.setSuppliersInventoryId(4);
//      rdo1.setLimitStorage(0);
//		
//		Set<GoodsSelectionRelationDO> set = new HashSet<GoodsSelectionRelationDO>();
//		set.add(rdo);
//		System.out.println(set.size()+" ,"+set.toString());
//		
//		set.add(rdo1);
//		System.out.println(set.size()+" ,"+set.toString());
		
		/*
		try {
			System.out.println(ObjectUtil.convertBean(rdo));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	*/
		//{id=1, leftNumber=0, totalNumber=0, goodTypeId=333766, limitStorage=0}
	/*Map<String,String> map = new HashMap<String,String>();
	map.put("id", "1");
	map.put("leftNumber", "0");
	map.put("totalNumber", "0");
	map.put("goodTypeId", "333766");
	map.put("limitStorage", "0");*/
	
	//System.out.println("jsonutils="+JsonUtils.convertObjectToString(map));
	
	//System.out.println("object="+JsonUtils.convertStringToObject(JsonUtils.convertObjectToString(map), GoodsSelectionRelationDO.class));
	
	/*try {
		System.out.println(ObjectUtil.convertMap(GoodsSelectionRelationDO.class, map));
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IntrospectionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
	
		//JSONObject jsonData = new JSONObject();
		/*Map<String,String> map = new HashMap<String,String>();
		map.put("k", "1");
		map.put("k", "2");*/
		//jsonData.put("k", "1");
		//jsonData.put("k", "2");
		System.out.println(String.valueOf(-1));
	}
	
	
	
	@SuppressWarnings("rawtypes")
	private static Map<String,String> toHashMap(Object object) {
		Map<String,String> data = new HashMap<String, String>();
		  JSONObject jsonObject = toJSONObject(object);
		  Iterator it = jsonObject.keys();
		  while (it.hasNext()) {
		   String key = String.valueOf(it.next());
		   String value = jsonObject.get(key).toString();
		   data.put(key, value);
		  }

		  return data;
		 }

	private static JSONObject toJSONObject(Object object) {
		  return JSONObject.fromObject(object);
		 }
	
	
	
	// Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map  
    public static Map<String, Object> transBean2Map(Object obj) {  
  
        if(obj == null){  
            return null;  
        }          
        Map<String, Object> map = new HashMap<String, Object>();  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = getter.invoke(obj);  
  
                    map.put(key, value);  
                }  
  
            }  
        } catch (Exception e) {  
            System.out.println("transBean2Map Error " + e);  
        }  
  
        return map;  
  
    }  
}
