package com.tuan.inventory.dao.data.redis;

import java.util.List;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * ���仯��bean
 * @author henry.yu
 * @date 2014/03/18
 */
public class RedisInventoryNumDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private int num;  //TOTAL_NUM("��Ʒ�ܿ��仯��"),
	private int leftNum;//TOTA_LLEFTNUM("��Ʒ�ܿ��ʣ����"),
	//����һ��������Ʒѡ�Ϳ��仯��list
	private List<RedisSelectionNumDO> sLists;
	//����һ��������Ʒ�ֵ���仯��list
	private List<RedisSuppliersNumDO> suppLists;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getLeftNum() {
		return leftNum;
	}
	public void setLeftNum(int leftNum) {
		this.leftNum = leftNum;
	}
	public List<RedisSelectionNumDO> getsLists() {
		return sLists;
	}
	public void setsLists(List<RedisSelectionNumDO> sLists) {
		this.sLists = sLists;
	}
	public List<RedisSuppliersNumDO> getSuppLists() {
		return suppLists;
	}
	public void setSuppLists(List<RedisSuppliersNumDO> suppLists) {
		this.suppLists = suppLists;
	}
	
}
