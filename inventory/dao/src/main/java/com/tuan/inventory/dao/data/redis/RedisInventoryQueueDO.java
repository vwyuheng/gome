package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * 库存队列结构bean
 * @author henry.yu
 * @date 20140311
 */
public class RedisInventoryQueueDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long id;  //日志主键
	private Long goodsId;// 商品ID(FK)
	private Long orderId;// 订单id
	//private Long userId ;//用户id
	private String type;  //标识：选型、总数、分店
	private String item; //若选型存选型id，若分店存分店id，若既非选型又非分店则不存数据
	private Integer createTime;  //创建日期
	private Integer updateTime;  //更新时间[状态被更新]
	private String variableQuantityJsonData;// 库存变化量 【这个是商品主体信息中的库存变化量】 若是存在选型商品或分店则其分别对应的库存变化保存在json数据中
	private String status;  //处理状态位 : 1:正常：有效可处理（active） 3：初始状态（locked），超过一定时间该标记未被更新为正常1的消息做超期处理，（会有个队列定时扫这个标记的消息） 5：标记可以被用作异常队列处(exception)
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
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public Integer getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
	public Integer getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Integer updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getVariableQuantityJsonData() {
		return variableQuantityJsonData;
	}
	public void setVariableQuantityJsonData(String variableQuantityJsonData) {
		this.variableQuantityJsonData = variableQuantityJsonData;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
//	private String operateType;  //操作类别 :是增库存还是//减库存 
//	private String content; //操作内容
//	private String system; // 来源系统
//	private String clientIp; // 来源ip
//	private String exception;  //是否异常信息
//	private String remark;  //备注
	
//	private java.lang.Integer leftNumber;// 当前剩余数库存默认值:0
//	private java.lang.Integer limitStorage; // 0:库存无限制；1：限制库存
//	private int isAddGoodsSelection;  //商品是否添加配型 0：不添加；1：添加
//	private int isDirectConsumption; //商品销售是否需要指定分店 0：不指定；1：指定
//	private java.lang.Integer waterfloodVal;  //注水值
//	//物流有关的
//	private int isBeDelivery; //是否发货
	
	
}
