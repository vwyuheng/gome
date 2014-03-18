package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 选型库存变化量bean
 * @author henry.yu
 * @date 2014/03/18
 */
public class RedisSelectionNumDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Integer selectionRelationId; //选型id
	private int selectionNum;//TOTAL_SELECTIONNUM("选型商品库存变化量"),
	private Long selectionLeftNum;//TOTAL_SELECTION_LEFTNUM("选型商品库存剩余量"),
	
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
