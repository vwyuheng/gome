package com.tuan.inventory.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.tuan.inventory.resp.inner.RequestPacket;
import com.wowotrace.trace.model.Message;

public class WrapUtils extends org.apache.commons.lang.StringUtils{
	
	/**
	 * 创建trace对象
	 * @param packet
	 * @return
	 */
	public static Message makeTraceMessage(RequestPacket packet){
		if(packet == null || packet.getTraceId() == null || packet.getTraceRootId() == null
				|| packet.getTraceId().isEmpty() || packet.getTraceRootId().isEmpty()){
			return null;
		}
		Message traceMessage = Message.getMessage(packet.getTraceRootId(),packet.getTraceId());
		return traceMessage;
	}
	
	
	public static Message makeTraceMessageByParam(String traceRootId,String traceId){
		if(StringUtils.isEmpty(traceId)||StringUtils.isEmpty(traceRootId)){
			return null;
		}
		Message traceMessage = Message.getMessage(traceRootId,traceId);
		return traceMessage;
	}
	
	/**
	 * 生成唯一ID
	 * 规则: MD5(时间（毫秒） + 4位随机数)
	 * @return
	 */
	public static String makeUUID(){
		long date = System.currentTimeMillis();
		return MD5Util.MD5Encode(String.valueOf(date) + makeRandom(4),"UTF-8") ;
	}
	
	/**
	 * 生成唯一ID
	 * 规则: 时间（毫秒） + 4位随机数
	 * @return
	 */
	public static String makeSysSn(){
		long date = System.currentTimeMillis();
		return String.valueOf(date) + makeRandom(4) ;
	}
	
	/**
	 * 生成num个随机（数字和字母组合）串
	 * @param num	1到36之间
	 * @return	num不合法返回null
	 */
	private static String makeRandom(int num){  
		if(num <= 0 || num > 36){
			return null;
		}
        String[] beforeShuffle = new String[] { 
        		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
        		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
        		"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };  
        List<String> list = Arrays.asList(beforeShuffle);  
        Collections.shuffle(list);  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < list.size(); i++) {  
            sb.append(list.get(i));  
        }  
        String afterShuffle = sb.toString();  
        String result = afterShuffle.substring(0, num);  
        return result;  
    }
	
	public static void main(String[] args) {
		//String orderParam = "{\"msg_type\":\"00\",\"msg_txn_code\":\"002100\",\"msg_crrltn_id\":\"12345678901234567890123456789000\",\"msg_flg\":\"1\",\"msg_sender\":\"11\",\"msg_time\":\"20130318163559\",\"msg_sys_sn\":\"12345678900987654321\",\"msg_rsp_code\":\"0000\",\"msg_rsp_desc\":\"成功\",\"orig_amt\":\"10000\",\"discount_amt\":\"2000\",\"pay_amt\":\"8000\",\"ad\":\"满100立减20\",\"serv_chg\":\"1000\",\"commission\":\"300\",\"sign\":\"qasdfqwer1231231223453452435\"}";
		//Type orderParamType = new TypeToken<CardCheckResp>(){}.getType();
        ///CardCheckResp resp = (CardCheckResp)new Gson().fromJson(orderParam, orderParamType);
		//System.out.println(resp);
	}

}
