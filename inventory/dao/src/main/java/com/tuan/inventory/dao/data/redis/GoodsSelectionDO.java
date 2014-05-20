package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;

/** 
 * @ClassName: RedisGoodsSelectionRelationDO
 * @Description: 商品与选型关系
 * @author henry.yu
 * @date 2014.03.06
 */
public class GoodsSelectionDO extends TuanBaseDO{

	private static final long serialVersionUID = 1L;
	private Long id;  //选型id
	private Long suppliersId;	// 商家ID（当商品不指定分店同时有配型时,此值为商品所属商家ID）,
	private Long goodsId;	// 商品id
	private Long userId;	// 用户id
	private Long goodTypeId; // 选型类型表ID(FK)关联到选型表ID
	private java.lang.Integer totalNumber; 	// 当前选型总库存
	private java.lang.Integer leftNumber; 	// 当前选型剩余数库存默认值：0 
	private Integer waterfloodVal;  //注水值
	private int limitStorage;	//0:库存无限制；1：限制库存
	private long suppliersSubId; 	// 分店ID（当商品指定分店同时有配型时此值为分店ID并且suppliers_id为商品所属商家ID）
	private long suppliersInventoryId; //商家库存表ID(FK)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getGoodTypeId() {
		return goodTypeId;
	}
	public void setGoodTypeId(Long goodTypeId) {
		this.goodTypeId = goodTypeId;
	}
	public java.lang.Integer getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(java.lang.Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	public java.lang.Integer getLeftNumber() {
		return leftNumber;
	}
	public void setLeftNumber(java.lang.Integer leftNumber) {
		this.leftNumber = leftNumber;
	}
	public int getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(int limitStorage) {
		this.limitStorage = limitStorage;
	}
	public long getSuppliersInventoryId() {
		return suppliersInventoryId;
	}
	public void setSuppliersInventoryId(long suppliersInventoryId) {
		this.suppliersInventoryId = suppliersInventoryId;
	}
	public Integer getWaterfloodVal() {
		return waterfloodVal;
	}
	public void setWaterfloodVal(Integer waterfloodVal) {
		this.waterfloodVal = waterfloodVal;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(Long suppliersId) {
		this.suppliersId = suppliersId;
	}
	public long getSuppliersSubId() {
		return suppliersSubId;
	}
	public void setSuppliersSubId(long suppliersSubId) {
		this.suppliersSubId = suppliersSubId;
	}
	
	
	
}
