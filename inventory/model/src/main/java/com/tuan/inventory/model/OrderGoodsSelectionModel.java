package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: OrderGoodsSelectionModel
 * @Description: 订单商品选型信息Bean类，对应OrderGoodsSelectionDO
 * 		对应订单商品选型信息表 order_center.jeehe_order_info_detail
 * @author wowo
 * @date 2011-12-21
 */
public class OrderGoodsSelectionModel extends TuanBaseDO {
	/** 序列化标识*/
	private static final long serialVersionUID = 1L;
	/** 记录id */
    private Long id;
    /** 订单id */
	private Long orderId;
	/** 商品id */
	private Long goodsId;
	/** 商品的购买数量 */
	private Short count;
	/** 选型id */
	private Integer selectionRelationId;
	/** 状态 0－有效；1－无效 */
	private Byte status;
	/** 创建或修改时间 */
	private int dateTime;
	/** 分店id */
	private int suppliersId;
	/** 描述字段 */
	private String description;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Short getCount() {
		return count;
	}
	public void setCount(Short count) {
		this.count = count;
	}
	public Integer getSelectionRelationId() {
		return selectionRelationId;
	}
	public void setSelectionRelationId(Integer selectionRelationId) {
		this.selectionRelationId = selectionRelationId;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public int getDateTime() {
		return dateTime;
	}
	public void setDateTime(int dateTime) {
		this.dateTime = dateTime;
	}
	public int getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(int suppliersId) {
		this.suppliersId = suppliersId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}