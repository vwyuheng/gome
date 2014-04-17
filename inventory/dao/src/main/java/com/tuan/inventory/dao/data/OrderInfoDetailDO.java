package com.tuan.inventory.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: OrderInfoDetailDO 
 * @Description: 订单选型配置信息
 * @author tianzq
 * @date 2012.11.30
 */
public class OrderInfoDetailDO extends TuanBaseDO{

	/** 
	 * @Fields serialVersionUID : TODO 
	 */ 
	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;		//id
	private java.lang.Integer orderId;	//订单ID；jeehe_order_goods.order_id
	private java.lang.Integer count;	//选型购买数量
	private java.lang.Integer selectionRelationId;	//选型与商家关系表ID
	private java.lang.Integer status;	//数据有效状态 0：当前数据有效；1：当前数据无效
	private java.lang.Integer dateTime;	//创建时间或修改时间
	private java.lang.Integer suppliersId; //商家分店表关系ID
	private String description;	//选型描述，用于修改订单，简化数据获取
	
	/**
	 * ！！备用！！ 修改库存时实用 ，取数据无该记录
	 */
	private java.lang.Integer goodsId;
	

	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	public java.lang.Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(java.lang.Integer orderId) {
		this.orderId = orderId;
	}
	public java.lang.Integer getCount() {
		return count;
	}
	public void setCount(java.lang.Integer count) {
		this.count = count;
	}
	public java.lang.Integer getSelectionRelationId() {
		return selectionRelationId;
	}
	public void setSelectionRelationId(java.lang.Integer selectionRelationId) {
		this.selectionRelationId = selectionRelationId;
	}
	public java.lang.Integer getStatus() {
		return status;
	}
	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	public java.lang.Integer getDateTime() {
		return dateTime;
	}
	public void setDateTime(java.lang.Integer dateTime) {
		this.dateTime = dateTime;
	}
	public java.lang.Integer getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(java.lang.Integer suppliersId) {
		this.suppliersId = suppliersId;
	}
	public void setGoodsId(java.lang.Integer goodsId) {
		this.goodsId = goodsId;
	}
	public java.lang.Integer getGoodsId() {
		return goodsId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
