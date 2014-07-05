package com.tuan.inventory.utils;

import org.apache.commons.lang.StringUtils;


public class JsonStrVerificationUtils extends org.apache.commons.lang.StringUtils{
	
	/**
	 * //双引号:"\"\""  单引号:"'\'"  空json串:"[]"
	 * @param valStr
	 * @return
	 */
	public static String validateStr(String valStr) {
		if(StringUtils.isNotEmpty(valStr)) {
			if(valStr.equals("\"\"")||valStr.equals("'\'")||valStr.equals("[]")) {
				return null;
	         }
		}
		return valStr;
	}
	
	
	public static void main(String[] args) {
		//String orderParam = "{\"msg_type\":\"00\",\"msg_txn_code\":\"002100\",\"msg_crrltn_id\":\"12345678901234567890123456789000\",\"msg_flg\":\"1\",\"msg_sender\":\"11\",\"msg_time\":\"20130318163559\",\"msg_sys_sn\":\"12345678900987654321\",\"msg_rsp_code\":\"0000\",\"msg_rsp_desc\":\"成功\",\"orig_amt\":\"10000\",\"discount_amt\":\"2000\",\"pay_amt\":\"8000\",\"ad\":\"满100立减20\",\"serv_chg\":\"1000\",\"commission\":\"300\",\"sign\":\"qasdfqwer1231231223453452435\"}";
		//Type orderParamType = new TypeToken<CardCheckResp>(){}.getType();
        ///CardCheckResp resp = (CardCheckResp)new Gson().fromJson(orderParam, orderParamType);
		//System.out.println(resp);
	}

}
