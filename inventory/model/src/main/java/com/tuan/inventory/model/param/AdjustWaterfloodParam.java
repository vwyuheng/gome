package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * ��Ʒ������
 * @author henry.yu
 * @date 20140310
 */
public class AdjustWaterfloodParam extends TuanBaseDO {
	
	private static final long serialVersionUID = 1L;
	//type 2:��Ʒid��4:ѡ��id��6:�ֵ�id
	private String id;
	private String userId;
	private int num;
	//2:��Ʒ������4.ѡ�Ϳ����� 6.�ֵ������
	private String type;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
