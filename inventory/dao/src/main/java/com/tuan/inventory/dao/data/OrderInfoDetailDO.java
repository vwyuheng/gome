package com.tuan.inventory.dao.data;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * @ClassName: OrderInfoDetailDO 
 * @Description: ����ѡ��������Ϣ
 * @author tianzq
 * @date 2012.11.30
 */
public class OrderInfoDetailDO extends TuanBaseDO{

	/** 
	 * @Fields serialVersionUID : TODO 
	 */ 
	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;		//id
	private java.lang.Integer orderId;	//����ID��jeehe_order_goods.order_id
	private java.lang.Integer count;	//ѡ�͹�������
	private java.lang.Integer selectionRelationId;	//ѡ�����̼ҹ�ϵ��ID
	private java.lang.Integer status;	//������Ч״̬ 0����ǰ������Ч��1����ǰ������Ч
	private java.lang.Integer dateTime;	//����ʱ����޸�ʱ��
	private java.lang.Integer suppliersId; //�̼ҷֵ���ϵID
	private String description;	//ѡ�������������޸Ķ����������ݻ�ȡ
	
	/**
	 * �������ã��� �޸Ŀ��ʱʵ�� ��ȡ�����޸ü�¼
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
