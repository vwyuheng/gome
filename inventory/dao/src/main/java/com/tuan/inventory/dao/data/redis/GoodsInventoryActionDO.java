package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 日志结构bean
 * @author henry.yu
 * @date 20140311
 */
public class GoodsInventoryActionDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long id;  //日志主键
	private Long goodsId;// 商品ID(FK)
	private Long orderId;// 订单id
	private Long userId ;//用户id
	private String businessType;  //业务类别》标识：选型、总数、分店
	private String item; //若选型存选型id，若分店存分店id，若既非选型又非分店则不存数据
	private Integer createTime;  //创建日期
	//private Integer updateTime;  //更新时间[状态被更新]
	private String inventoryChange;// 库存变化量
	private String originalInventory;// 原始库存量【库存扣减前的数值】
	private String actionType;  //操作类别》类型:库存扣减、初始化库存、商品退款还库存、手工调整库存、出错补偿还库存
	private String content; //操作内容
	private String clientName; // 来源系统
	private String clientIp; // 来源ip
	//private String exception;  //是否异常信息
	private String remark;  //备注
	private Long goodsBaseId;  //库存基表ID
	
	
	public Long getId() {
		return id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	/*public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}*/
	public Integer getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
	
	public String getInventoryChange() {
		return inventoryChange;
	}
	public void setInventoryChange(String inventoryChange) {
		this.inventoryChange = inventoryChange;
	}
	public String getOriginalInventory() {
		return originalInventory;
	}
	public void setOriginalInventory(String originalInventory) {
		this.originalInventory = originalInventory;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public Long getGoodsBaseId() {
		return goodsBaseId;
	}
	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	
}
