package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * ��־�ṹbean
 * @author henry.yu
 * @date 20140311
 */
public class RedisInventoryLogDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long id;  //��־����
	private Long goodsId;// ��ƷID(FK)
	private Long orderId;// ����id
	private Long userId ;//�û�id
	private String type;  //ҵ������:���ۼ�����ʼ����桢��Ʒ�˿��桢�ֹ�������桢�����������
	private Integer createTime;  //��������
	//private Integer updateTime;  //����ʱ��[״̬������]
	private int variableQuantity;// ���仯��
	
	private String operateType;  //������� :������滹�Ǽ���� 
	private String content; //��������
	private String system; // ��Դϵͳ
	private String clientIp; // ��Դip
	private String exception;  //�Ƿ��쳣��Ϣ
	private String remark;  //��ע
	
//	private java.lang.Integer leftNumber;// ��ǰʣ�������Ĭ��ֵ:0
//	private java.lang.Integer limitStorage; // 0:��������ƣ�1�����ƿ��
//	private int isAddGoodsSelection;  //��Ʒ�Ƿ�������� 0������ӣ�1�����
//	private int isDirectConsumption; //��Ʒ�����Ƿ���Ҫָ���ֵ� 0����ָ����1��ָ��
//	private java.lang.Integer waterfloodVal;  //עˮֵ
//	//�����йص�
//	private int isBeDelivery; //�Ƿ񷢻�
	
	
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
