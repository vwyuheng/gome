package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * ѡ�Ϳ��仯��bean
 * @author henry.yu
 * @date 2014/03/18
 */
public class RedisSelectionNumDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Integer selectionRelationId; //ѡ��id
	private int selectionNum;//TOTAL_SELECTIONNUM("ѡ����Ʒ���仯��"),
	private Long selectionLeftNum;//TOTAL_SELECTION_LEFTNUM("ѡ����Ʒ���ʣ����"),
	
	public Integer getSelectionRelationId() {
		return selectionRelationId;
	}
	public void setSelectionRelationId(Integer selectionRelationId) {
		this.selectionRelationId = selectionRelationId;
	}
	public int getSelectionNum() {
		return selectionNum;
	}
	public void setSelectionNum(int selectionNum) {
		this.selectionNum = selectionNum;
	}
	public Long getSelectionLeftNum() {
		return selectionLeftNum;
	}
	public void setSelectionLeftNum(Long selectionLeftNum) {
		this.selectionLeftNum = selectionLeftNum;
	}
	
	
	

}
