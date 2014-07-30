package com.tuan.inventory.domain.support.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.tuan.inventory.domain.support.jedistools.RedisCacheUtil;

/**
 * 基于redis的序列生成器
 * 
 * @author henry.yu
 * @date 2014/3/11
 */
public class SequenceUtil {
	//private static Logger logger = Logger.getLogger(SequenceUtil.class);
	//private static Log logger = LogFactory.getLog(SequenceUtil.class);
	private static Log logger = LogFactory.getLog("INVENTORY.HIS.LOG");
	private static Set<String> filterTableSet = new HashSet<String>();
	//维护的一个自增计数器<tableName,AtomicCount>
	private static final Map<String, AtomicCount> atomicCountMap = new HashMap<String, AtomicCount>();
	private static int local_ip_seq = 0;
	@Resource
	RedisCacheUtil redisCacheUtil;

	public Long getSequence(final SEQNAME seqName) {

		
		Long result = null;
		if (seqName == null) {
			return result;
		}
		String key = seqName.toString();
		long startTime = System.currentTimeMillis();
		//result = redisCacheUtil.incr(key);
		result =  getCommonSequence(key, 1);
		long endTime = System.currentTimeMillis();
		logger.info("key: " + key + "   value: " + result
				+ " Processed Time: " + (endTime - startTime) + " ms");
		return result;

	}

	
	/**
	 * 客户端批量生成id号的生成工具
	 * 1,要求id号对应同一个tableName唯一;
	 * 1,不要求id号严格自增;
	 * 2,不要求id号连续;
	 * 生成方案:13位毫秒级别时间戳+3位IP部分+3位ID流水  【 T{13}P{3}F{3}】  （供19位）
	 * 示例：116.213.178.99 （不足3位的 +300） 取值：1388387448555+399+001 
	 * 
	 * @param tableName使用的表的名称(主要是记录使用)
	 * @param getCount每次批量获取的个数(最大300)
	 * @return
	 */
	public Long getCommonSequence(String tableName, int getCount){
		logger.warn("ShowSequenceImpl.getCommonSequence["  + tableName + "] begin...");
		if(tableName == null || tableName.trim().equals("")){
			return null;
		}
		if(getCount <= 0 || getCount > 300){
			return null;
		}
		tableName = tableName.trim().toLowerCase();
		if(filterTableSet.contains(tableName)){//过滤的表不能调用该接口
			return null;
		}
		//取出服务器序列位
		int intPrefix = initIpPrefix();//(100~255,300~399)
		//取出时间戳
		long timestamp = System.currentTimeMillis();
		//取出自增序列(0~999)
		AtomicInteger atomicInteger = null;
		AtomicCount atomicCount = null;
		if(atomicCountMap.containsKey(tableName)){//已经存在
			atomicCount = atomicCountMap.get(tableName);
			if(atomicCount.getTimeStamp() == timestamp){//同一毫秒内
				atomicInteger = atomicCount.getCount();
			}else{//已经超时
				atomicInteger = new AtomicInteger(0);
				atomicCount.setTimeStamp(timestamp);
			}
		}else{//不存在
			atomicCount = AtomicCount.getInstance(tableName);
			atomicInteger = new AtomicInteger(0);
			atomicCount.setTimeStamp(timestamp);
		}
		int suffix = atomicInteger.getAndAdd(getCount);
		atomicCount.setCount(atomicInteger);
		atomicCountMap.put(tableName, atomicCount);
		
		if(suffix + getCount > 1000){//超长了
			return null;
		}
		List<String> seqList = new ArrayList<String>();
		for(int i = suffix; i < suffix + getCount; i ++){
			StringBuffer sb = new StringBuffer("");
			
			sb.append(timestamp);
			sb.append(intPrefix);
			StringBuffer seqSb = new StringBuffer("");
			if(i < 10){
				seqSb.append("00").append(i);
			}else if(i < 100){
				seqSb.append("0").append(i);
			}else{
				seqSb.append(i);
			}
			sb.append(seqSb);
			String idCode = sb.toString();
			logger.warn("MakeCommonSequence[" + timestamp + "," + intPrefix + "," + seqSb.toString() + "," + idCode + "]");
			seqList.add(idCode);
		}
		
		logger.warn("ShowSequenceImpl.getCommonSequence[" + tableName + "," + "] End[" + seqList.size() + "].");
			
		Long seq = 0l;
		if(!CollectionUtils.isEmpty(seqList)){
			 seq = Long.parseLong(seqList.get(0));
		}
		return seq;
		
	}
	
	private int initIpPrefix(){
		int intPrefix = 300;
		if((local_ip_seq >= 100 && local_ip_seq <= 255) || 
		   (local_ip_seq > 300 && local_ip_seq <= 399)){//100~255,300~399
			return local_ip_seq;
		}
		String addr = NetUtil.getLocalAddress0();//取得一个有效的ip或者null
		if(addr != null){
			String[] tmp = addr.split("\\.");
			if(tmp.length == 4){
				try{
					intPrefix = Integer.parseInt(tmp[3]);
					if(intPrefix < 100){
						intPrefix += 300;
					}
					local_ip_seq = intPrefix;
				}catch(Exception e){
					intPrefix = 300;
				}
			}
		}
		
		return intPrefix;
	}
	
	
	public static void main(String[] args) {
		SequenceUtil ss = new SequenceUtil();
		Long se = ss.getSequence(SEQNAME.seq_queue_send);
		System.out.println(se);
		System.out.println("Long.MAX_VALUE="+Long.MAX_VALUE);
	}
}
