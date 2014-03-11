package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 日志结构bean
 * @author henry.yu
 * @date 20140311
 */
public class RedisInventoryLogDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long id;  //日志主键
	private Long goodsId;// 商品ID(FK)
	private Long orderId;// 订单id
	private Long userId ;//用户id
	private String type;  //业务类型:库存扣减、初始化库存、商品退款还库存、手工调整库存、出错补偿还库存
	private Integer createTime;  //创建日期
	//private Integer updateTime;  //更新时间[状态被更新]
	private int variableQuantity;// 库存变化量
	
	private String operateType;  //操作类别 :是增库存还是减库存 
	private String content; //操作内容
	private String system; // 来源系统
	private String clientIp; // 来源ip
	private String exception;  //是否异常信息
	private String remark;  //备注
	
//	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
//	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
//	private int isAddGoodsSelection;  //商品是否添加配型 0：不添加；1：添加
//	private int isDirectConsumption; //商品销售是否需要指定分店 0：不指定；1：指定
//	private java.lang.Integer waterfloodVal;  //注水值
//	//物流有关的
//	private int isBeDelivery; //是否发货
	
	
	public Long getId() {
		return id;
	}
	public int getVariableQuantity() {
		return variableQuantity;
	}
	public void setVariableQuantity(int variableQuantity) {
		this.variableQuantity = variableQuantity;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public Integer getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	
	
}
