package com.tuan.inventory.model;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * ��־�ṹbean
 * @author henry.yu
 * @date 20140311
 */
public class GoodsInventoryActionModel extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long id;  //��־����
	private Long goodsId;// ��ƷID(FK)
	private Long orderId;// ����id
	private Long userId ;//�û�id
	private String businessType;  //ҵ����𡷱�ʶ��ѡ�͡��������ֵ�
	private String item; //��ѡ�ʹ�ѡ��id�����ֵ��ֵ�id�����ȷ�ѡ���ַǷֵ��򲻴�����
	private Integer createTime;  //��������
	//private Integer updateTime;  //����ʱ��[״̬������]
	private String inventoryChange;// ���仯��
	private String originalInventory;// ԭʼ����������ۼ�ǰ����ֵ��
	private String actionType;  //�����������:���ۼ�����ʼ����桢��Ʒ�˿��桢�ֹ�������桢�����������
	private String content; //��������
	private String clientName; // ��Դϵͳ
	private String clientIp; // ��Դip
	//private String exception;  //�Ƿ��쳣��Ϣ
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
	
	
}
