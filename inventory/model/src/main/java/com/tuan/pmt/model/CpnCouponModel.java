package com.tuan.pmt.model;

import com.tuan.core.common.lang.TuanBaseDO;

public class CpnCouponModel extends TuanBaseDO{

	/** ���л���ʶ */
	private static final long serialVersionUID = 2224423703132142104L;
	/** ����ID ����*/
	private Long couponId;
	/** ����ID */
	private Long batchId;
	/** ǰ׺ */
	private String prefix;
	/** ��֤�� */
	private String code;
	/** ����ʱ�� */
	private String genTime;
	/** ��ʱ�� */
	private String bindTime;
	/** ʹ��ʱ�� */
	private String usedTime;
	/** �û�ID */
	private Long userId;
	/** ����ID */
	private Long orderId;
	/** ״̬��0:δ�󶨡�1:δʹ�á�2:��ʹ�á�3:�Ѷ��ᡢ4:������ */
	private Integer status;
	/** ����ʱ�� */
	private String invalidTime;
	/** ���ϲ���ԱID */
	private Long invalidAdminId;
	/** �������� */
	private String invalidReason;
	/**����ȯ��������*/
	private CpnCouponBatchModel cpnCouponBatchModel;
	
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getGenTime() {
		return genTime;
	}
	public void setGenTime(String genTime) {
		this.genTime = genTime;
	}
	public String getBindTime() {
		return bindTime;
	}
	public void setBindTime(String bindTime) {
		this.bindTime = bindTime;
	}
	public String getUsedTime() {
		return usedTime;
	}
	public void setUsedTime(String usedTime) {
		this.usedTime = usedTime;
	}
	public String getInvalidTime() {
		return invalidTime;
	}
	public void setInvalidTime(String invalidTime) {
		this.invalidTime = invalidTime;
	}
	public String getInvalidReason() {
		return invalidReason;
	}
	public void setInvalidReason(String invalidReason) {
		this.invalidReason = invalidReason;
	}
	public Long getInvalidAdminId() {
		return invalidAdminId;
	}
	public void setInvalidAdminId(Long invalidAdminId) {
		this.invalidAdminId = invalidAdminId;
	}
    public CpnCouponBatchModel getCpnCouponBatchModel() {
        return cpnCouponBatchModel;
    }
    public void setCpnCouponBatchModel(CpnCouponBatchModel cpnCouponBatchModel) {
        this.cpnCouponBatchModel = cpnCouponBatchModel;
    }
}