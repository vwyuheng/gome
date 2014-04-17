package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 商品库存参数
 * @author henry.yu
 * @date 20140310
 */
public class CallbackParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	private String ack;
	private String key;
	/*private String userId;
	private String orderId;*/
	public String getAck() {
		return ack;
	}
	public void setAck(String ack) {
		this.ack = ack;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
	
}
