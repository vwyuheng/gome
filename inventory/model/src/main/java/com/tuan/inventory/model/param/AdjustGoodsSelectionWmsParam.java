package com.tuan.inventory.model.param;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: RedisGoodsSelectionRelationDO
 * @Description: 商品与选型关系
 * @author henry.yu
 * @date 2014.03.06
 */
public class AdjustGoodsSelectionWmsParam extends TuanBaseDO{

	private static final long serialVersionUID = 1L;
	private Long id;  //选型id
	//private Long userId;
	//private Long goodsId;	// 商品id
	private Long goodTypeId; // 选型类型表ID(FK)关联到选型表ID
	//private java.lang.Integer totalNumber; 	// 当前选型总库存
	//private java.lang.Integer leftNumber; 	// 当前选型剩余数库存默认值：0 
	//private Integer waterfloodVal;  //注水值
	private int limitStorage;	//0:库存无限制；1：限制库存
	private int num; //调整数量
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGoodTypeId() {
		return goodTypeId;
	}
	public void setGoodTypeId(Long goodTypeId) {
		this.goodTypeId = goodTypeId;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
	
	
	
}
