package com.tuan.inventory.dao.data;

public class GoodsSelectionRelationGoodDO {
	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;
	private java.lang.Integer suppliersId;	// �̼�ID������Ʒ��ָ���ֵ�ͬʱ������ʱ,��ֵΪ��Ʒ�����̼�ID��,
	private java.lang.Integer status; 		// ��¼״̬���ƣ�Ĭ��ֵ��0��; 0:��ǰ��¼��Ч1����ǰ��¼��Ч,
	private java.lang.Integer goodTypeId; // ѡ�����ͱ�ID(FK)������ѡ�ͱ�ID
	private String ImageURL; 				// ѡ��ͼƬ��Ե�ַ(���û��ϴ�)
	private java.lang.Integer totalNumber; 	// ��ǰѡ���ܿ��
	private java.lang.Integer leftNumber; 	// ��ǰѡ��ʣ�������Ĭ��ֵ��0
	private java.lang.Integer addTotalNumber; 	// ��ǰѡ�����ӿ������Ĭ��ֵ��0
	private java.lang.Integer reduceTotalNumber; 	// ��ǰѡ�ͼ��ٿ������Ĭ��ֵ��0
	private java.lang.Integer limitNumber; 			// ��ǰѡ���޹�����0��������
	private java.lang.Integer suppliersSubId; 	// �ֵ�ID������Ʒָ���ֵ�ͬʱ������ʱ��ֵΪ�ֵ�ID����suppliers_idΪ��Ʒ�����̼�ID��
	private java.lang.Integer leftTotalNumDisplayFont; //'ʣ�����Ƿ�ǰ̨��ʾ:0������ʾ��1����ʾ',
	private java.lang.Integer limitStorage;	//0:��������ƣ�1�����ƿ��
	private java.lang.Integer goodId;
	private String name;
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	public java.lang.Integer getSuppliersId() {
		return suppliersId;
	}
	public void setSuppliersId(java.lang.Integer suppliersId) {
		this.suppliersId = suppliersId;
	}
	public java.lang.Integer getStatus() {
		return status;
	}
	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	public java.lang.Integer getGoodTypeId() {
		return goodTypeId;
	}
	public void setGoodTypeId(java.lang.Integer goodTypeId) {
		this.goodTypeId = goodTypeId;
	}
	public String getImageURL() {
		return ImageURL;
	}
	public void setImageURL(String imageURL) {
		ImageURL = imageURL;
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
	public java.lang.Integer getAddTotalNumber() {
		return addTotalNumber;
	}
	public void setAddTotalNumber(java.lang.Integer addTotalNumber) {
		this.addTotalNumber = addTotalNumber;
	}
	public java.lang.Integer getReduceTotalNumber() {
		return reduceTotalNumber;
	}
	public void setReduceTotalNumber(java.lang.Integer reduceTotalNumber) {
		this.reduceTotalNumber = reduceTotalNumber;
	}
	public java.lang.Integer getLimitNumber() {
		return limitNumber;
	}
	public void setLimitNumber(java.lang.Integer limitNumber) {
		this.limitNumber = limitNumber;
	}
	public java.lang.Integer getSuppliersSubId() {
		return suppliersSubId;
	}
	public void setSuppliersSubId(java.lang.Integer suppliersSubId) {
		this.suppliersSubId = suppliersSubId;
	}
	public java.lang.Integer getLeftTotalNumDisplayFont() {
		return leftTotalNumDisplayFont;
	}
	public void setLeftTotalNumDisplayFont(java.lang.Integer leftTotalNumDisplayFont) {
		this.leftTotalNumDisplayFont = leftTotalNumDisplayFont;
	}
	public java.lang.Integer getGoodId() {
		return goodId;
	}
	public void setGoodId(java.lang.Integer goodId) {
		this.goodId = goodId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public java.lang.Integer getLimitStorage() {
		return limitStorage;
	}
	public void setLimitStorage(java.lang.Integer limitStorage) {
		this.limitStorage = limitStorage;
	} 
	
}
