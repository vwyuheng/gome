package com.tuan.inventory.model.param.rest;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: RedisGoodsSelectionRelationDO
 * @Description: 商品与选型关系
 * @author henry.yu
 * @date 2014.03.06
 */
public class TestParam extends TuanBaseDO{

	private static final long serialVersionUID = 1L;
	private Long id;  //选型id
	private int limit;	//0:库存无限制；1：限制库存
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	
	
}
