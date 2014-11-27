package test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.dao.data.GoodsWmsSelectionResult;
import com.tuan.inventory.dao.data.redis.GoodsBaseInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsInventoryDO;
import com.tuan.inventory.dao.data.redis.GoodsSelectionDO;
import com.tuan.inventory.domain.support.util.ObjectUtils;
import com.tuan.inventory.domain.support.util.StringUtil;

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
		//System.out.println("dd1="+ObjectSelfCopy(rdo,GoodsSelectionRelationDO.class));
		
		rdo.setLeftNumber(400);
		//System.out.println("dd2="+rdo);
		//JSONObject jsonObject = new JSONObject();
		//jsonObject.accumulate("code", "1000");
		//jsonObject.accumulate("msg", "success");
		//jsonObject.accumulate("data", rdo);
		//String objStr = JSON.p(rdo);
		Test test = new Test();
	//	GoodsSelectionRelationDO do1 = (GoodsSelectionRelationDO) LogUtil.jsonToObject(objStr,GoodsSelectionRelationDO.class);
		//System.out.println("test="+JsonUtils.convertStringToObject(JsonUtils.convertObjectToString(rdo), Map.class));
		//System.out.println(JSONObject.fromObject(rdo));
		//System.out.println("11test11="+toHashMap(rdo));
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
		//System.out.println(String.valueOf(-1));
		
		GoodsBaseInventoryDO	baseInventoryDO = new GoodsBaseInventoryDO();
		//baseInventoryDO.setGoodsBaseId(8000009999l);
		//baseInventoryDO.setBaseTotalCount(0);
		//baseInventoryDO.setBaseSaleCount(0);

		//System.out.println("InventoryWmsUpdateDomain:物流库存调整t".getBytes().length);
		/*List<GoodsWmsSelectionResult> tmpSelectionParam = new ArrayList<GoodsWmsSelectionResult>();
		System.out.println(CollectionUtils.isEmpty(tmpSelectionParam));
		List<GoodsSelectionDO> rgsrList = new ArrayList<GoodsSelectionDO>();
		for(int i=1;i<=5;i++) {
			GoodsSelectionDO sel = new GoodsSelectionDO();
			sel.setId((long) i);
			rgsrList.add(sel);
		}*/
		//{"goodsBaseId":8000010009751,"goodsId":10009790,"goodsSaleCount":0,"goodsSelectionIds":"","isAddGoodsSelection":0,
		//	"isDirectConsumption":0,"leftNumber":0,"limitStorage":0,"totalNumber":0,"userId":0,"waterfloodVal":0,"wmsId":0}
		GoodsInventoryDO inventoryInfoDO = new GoodsInventoryDO();
		inventoryInfoDO.setGoodsId(10009790l);
		inventoryInfoDO.setGoodsBaseId(8000010009751l);
		inventoryInfoDO.setGoodsSaleCount(0);
		inventoryInfoDO.setGoodsSelectionIds("");
		inventoryInfoDO.setIsAddGoodsSelection(0);
		inventoryInfoDO.setIsDirectConsumption(0);
		inventoryInfoDO.setLeftNumber(0);
		inventoryInfoDO.setTotalNumber(0);
		inventoryInfoDO.setLimitStorage(0);
		inventoryInfoDO.setUserId(0l);
		inventoryInfoDO.setWaterfloodVal(0);
		inventoryInfoDO.setWmsId(0l);
		System.out.println(ObjectUtils.toHashMap(inventoryInfoDO));

	}
	
	//对象的自我拷贝
	public static  <T> T ObjectSelfCopy(Object tmpObject,Class<T> classType) {
			return JSON.parseObject(JSON.toJSONString(tmpObject),
					classType);
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
