package test;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.tuan.inventory.dao.data.GoodsSelectionRelationDO;
import com.tuan.inventory.domain.support.util.LogUtil;

public class Test {

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
		String objStr = LogUtil.formatObjLog(rdo);
		GoodsSelectionRelationDO do1 = (GoodsSelectionRelationDO) LogUtil.jsonToObject(objStr,GoodsSelectionRelationDO.class);
		System.out.println(JSONObject.fromObject(rdo));
		System.out.println(JSONObject.fromObject(rdo));
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
	map.put("limitStorage", "0");
	
	try {
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
	
		JSONObject jsonData = new JSONObject();
		/*Map<String,String> map = new HashMap<String,String>();
		map.put("k", "1");
		map.put("k", "2");*/
		jsonData.put("k", "1");
		jsonData.put("k", "2");
		System.out.println(jsonData.toString());
	}
	
	
	
	

}
