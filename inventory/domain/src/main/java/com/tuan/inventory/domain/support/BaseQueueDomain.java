package com.tuan.inventory.domain.support;

import org.apache.commons.lang.StringUtils;

import com.tuan.core.common.lang.utils.TimeUtil;
import com.tuan.inventory.dao.data.redis.GoodsInventoryActionDO;
import com.tuan.inventory.domain.AbstractDomain;
import com.tuan.inventory.model.GoodsInventoryActionModel;
import com.tuan.inventory.model.enu.ResultStatusEnum;


/**
 * 对列数据参数
 * 
 * @author henry.yu
 * @Date  2014/4/24
 */
public class BaseQueueDomain extends AbstractDomain{
	
	protected Long id;  //日志主键
	private Long goodsBaseId;  //库存基表ID
	protected Long goodsId;// 商品ID(FK)
	protected Long orderId;// 订单id
	protected Long userId ;//用户id
	protected String type;  //标识：选型、总数、分店
	protected String item; //若选型存选型id，若分店存分店id，若既非选型又非分店则不存数据
	protected Integer createTime;  //创建日期
	//protected Integer updateTime;  //更新时间[状态被更新]
	protected String variableQuantity;// 库存变化量
	protected String originalQuantity;// 原始库存量【库存扣减前的数值】
	protected String operateType;  //业务类型:库存扣减、初始化库存、商品退款还库存、手工调整库存、出错补偿还库存
	protected String content; //操作内容
	protected String system; // 来源系统
	protected String clientIp; // 来源ip
	//protected String exception;  //是否异常信息
	protected String remark;  //备注
	
	/**
	 * 更新对象
	 */
	//protected QueueDO queueDOUpdate;

	public BaseQueueDomain() {

	}

	public BaseQueueDomain(GoodsInventoryActionModel logModel) {
		super();
		this.id = logModel.getId();
		this.goodsBaseId = logModel.getGoodsBaseId();
		this.goodsId = logModel.getGoodsId();
		this.orderId = logModel.getOrderId();
		this.type = logModel.getBusinessType();
	    this.item = logModel.getItem();
		this.createTime = logModel.getCreateTime();
		this.variableQuantity = logModel.getInventoryChange();
		this.originalQuantity = logModel.getOriginalInventory();
		this.operateType = logModel.getActionType();
		this.userId = logModel.getUserId();
		this.content = logModel.getContent();
		this.system = logModel.getClientName();
		this.clientIp = logModel.getClientIp();
		this.remark = logModel.getRemark();
	}


	public BaseQueueDomain(Long id, Long goodsBaseId,Long goodsId,
			Long orderId, String selectType, String suppliersType, String num,String oldNum,
			String operateType, Long userId, String system, String clientIp,
			String remark, String jsonContent) {
		super();
		this.id = id;
		this.goodsBaseId = goodsBaseId;
		this.goodsId = goodsId;
		this.orderId = orderId;
		if (StringUtils.isNotEmpty(selectType) ) {
			this.setType(ResultStatusEnum.GOODS_SELECTION.getDescription());
			this.setItem(selectType);
		} else if (StringUtils.isNotEmpty(suppliersType)) {
			this.setType(ResultStatusEnum.GOODS_SUPPLIERS.getDescription());
			this.setItem(suppliersType);
		} else {
			this.setType(ResultStatusEnum.GOODS_SELF.getDescription());
		}
		this.createTime = TimeUtil.getNowTimestamp10Int();
		this.variableQuantity = num;
		this.originalQuantity = oldNum;
		this.operateType = operateType;
		this.userId = userId;
		this.content = jsonContent;
		this.system = system;
		this.clientIp = clientIp;
		this.remark = remark;
	
	}


	/**
	 * 创建队列dao
	 */
	public GoodsInventoryActionDO toLogQueueDO() {
		GoodsInventoryActionDO logQueueDO = new GoodsInventoryActionDO();
		logQueueDO.setId(this.id);
		logQueueDO.setGoodsBaseId(this.goodsBaseId);
		logQueueDO.setGoodsId(this.goodsId);
		logQueueDO.setOrderId(this.orderId);
		logQueueDO.setBusinessType(this.type);
		logQueueDO.setItem(this.item);
		logQueueDO.setCreateTime(this.createTime);
		logQueueDO.setInventoryChange(this.variableQuantity);
		logQueueDO.setOriginalInventory(this.originalQuantity);
		logQueueDO.setActionType(this.operateType);
		logQueueDO.setUserId(this.userId);
		logQueueDO.setContent(this.content);
		logQueueDO.setClientName(this.system);
		logQueueDO.setClientIp(this.clientIp);
		logQueueDO.setRemark(this.remark);
		
		return logQueueDO;
	}
	
	
	/**
	 * 创建队列模型
	 */
	public GoodsInventoryActionModel toLogQueueModel() {
		GoodsInventoryActionModel queue = new GoodsInventoryActionModel();
		queue.setId(this.id);
		queue.setGoodsBaseId(goodsBaseId);
		queue.setGoodsId(this.goodsId);
		queue.setOrderId(this.orderId);
		queue.setBusinessType(this.type);
		queue.setItem(this.item);
		queue.setCreateTime(this.createTime);
		queue.setInventoryChange(this.variableQuantity);
		queue.setOriginalInventory(this.originalQuantity);
		queue.setActionType(this.operateType);
		queue.setUserId(this.userId);
		queue.setContent(this.content);
		queue.setClientName(this.system);
		queue.setClientIp(this.clientIp);
		queue.setRemark(this.remark);
		return queue;
	}

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

	public String getVariableQuantity() {
		return variableQuantity;
	}

	public void setVariableQuantity(String variableQuantity) {
		this.variableQuantity = variableQuantity;
	}

	public String getOriginalQuantity() {
		return originalQuantity;
	}

	public void setOriginalQuantity(String originalQuantity) {
		this.originalQuantity = originalQuantity;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getGoodsBaseId() {
		return goodsBaseId;
	}

	public void setGoodsBaseId(Long goodsBaseId) {
		this.goodsBaseId = goodsBaseId;
	}
	

}