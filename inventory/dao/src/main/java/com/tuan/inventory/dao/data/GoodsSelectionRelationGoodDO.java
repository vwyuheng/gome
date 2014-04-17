package com.tuan.inventory.dao.data;

public class GoodsSelectionRelationGoodDO {
	private static final long serialVersionUID = 1L;
	private java.lang.Integer id;
	private java.lang.Integer suppliersId;	// 商家ID（当商品不指定分店同时有配型时,此值为商品所属商家ID）,
	private java.lang.Integer status; 		// 记录状态控制（默认值：0）; 0:当前记录有效1：当前记录无效,
	private java.lang.Integer goodTypeId; // 选型类型表ID(FK)关联到选型表ID
	private String ImageURL; 				// 选型图片相对地址(由用户上传)
	private java.lang.Integer totalNumber; 	// 当前选型总库存
	private java.lang.Integer leftNumber; 	// 当前选型剩余数库存默认值：0
	private java.lang.Integer addTotalNumber; 	// 当前选型增加库存总量默认值：0
	private java.lang.Integer reduceTotalNumber; 	// 当前选型减少库存总量默认值：0
	private java.lang.Integer limitNumber; 			// 当前选型限购数量0：无限制
	private java.lang.Integer suppliersSubId; 	// 分店ID（当商品指定分店同时有配型时此值为分店ID并且suppliers_id为商品所属商家ID）
	private java.lang.Integer leftTotalNumDisplayFont; //'剩余库存是否前台显示:0：不显示；1：显示',
	private java.lang.Integer limitStorage;	//0:库存无限制；1：限制库存
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
