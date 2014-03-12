package com.tuan.inventory.dao.data.redis;

import com.tuan.core.common.lang.TuanBaseDO;
/**
 * �����нṹbean
 * @author henry.yu
 * @date 20140311
 */
public class RedisInventoryQueueDO extends TuanBaseDO {

	private static final long serialVersionUID = 1L;
	private Long id;  //��־����
	private Long goodsId;// ��ƷID(FK)
	private Long orderId;// ����id
	//private Long userId ;//�û�id
	private String type;  //��ʶ��ѡ�͡��������ֵ�
	private String item; //��ѡ�ʹ�ѡ��id�����ֵ��ֵ�id�����ȷ�ѡ���ַǷֵ��򲻴�����
	private Integer createTime;  //��������
	private Integer updateTime;  //����ʱ��[״̬������]
	private String variableQuantityJsonData;// ���仯�� ���������Ʒ������Ϣ�еĿ��仯���� ���Ǵ���ѡ����Ʒ��ֵ�����ֱ��Ӧ�Ŀ��仯������json������
	private String status;  //����״̬λ : 1:��������Ч�ɴ���active�� 3����ʼ״̬��locked��������һ��ʱ��ñ��δ������Ϊ����1����Ϣ�����ڴ��������и����ж�ʱɨ�����ǵ���Ϣ�� 5����ǿ��Ա������쳣���д�(exception)
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
	
//	private String operateType;  //������� :������滹��//����� 
//	private String content; //��������
//	private String system; // ��Դϵͳ
//	private String clientIp; // ��Դip
//	private String exception;  //�Ƿ��쳣��Ϣ
//	private String remark;  //��ע
	
//	private java.lang.Integer leftNumber;// ��ǰʣ�������Ĭ��ֵ:0
//	private java.lang.Integer limitStorage; // 0:��������ƣ�1�����ƿ��
//	private int isAddGoodsSelection;  //��Ʒ�Ƿ�������� 0������ӣ�1�����
//	private int isDirectConsumption; //��Ʒ�����Ƿ���Ҫָ���ֵ� 0����ָ����1��ָ��
//	private java.lang.Integer waterfloodVal;  //עˮֵ
//	//�����йص�
//	private int isBeDelivery; //�Ƿ񷢻�
	
	
}
