package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: OrderGoodsSelectionModel
 * @Description: ������Ʒѡ����ϢBean�࣬��ӦOrderGoodsSelectionDO
 * 		��Ӧ������Ʒѡ����Ϣ�� order_center.jeehe_order_info_detail
 * @author wowo
 * @date 2011-12-21
 */
public class OrderGoodsSelectionModel extends TuanBaseDO {
	/** ���л���ʶ*/
	private static final long serialVersionUID = 1L;
	/** ��¼id */
    private Long id;
    /** ����id */
	private Long orderId;
	/** ��Ʒid */
	private Long goodsId;
	/** ��Ʒ�Ĺ������� */
	private Short count;
	/** ѡ��id */
	private Integer selectionRelationId;
	/** ״̬ 0����Ч��1����Ч */
	private Byte status;
	/** �������޸�ʱ�� */
	private int dateTime;
	/** �ֵ�id */
	private int suppliersId;
	/** �����ֶ� */
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