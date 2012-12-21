package com.tuan.pmt.dao.data;

import java.sql.Timestamp;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * ����ȯ��cpn_coupon��
 * @ClassName: CpnCouponDO
 * @author duandongjun
 * @date 2012-11-15
 */
public class CpnCouponDO extends TuanBaseDO {
	/** ���л���ʶ */
	private static final long serialVersionUID = 2951665936037707502L;
	/** ����ID ����*/
	private Long couponId;
	/** ����ID */
	private Long batchId;
	/** ǰ׺ */
	private String prefix;
	/** ��֤�� */
	private String code;
	/** ����ʱ�� */
	private Timestamp genTime;
	/** ��ʱ�� */
	private Timestamp bindTime;
	/** ʹ��ʱ�� */
	private Timestamp usedTime;
	/** �û�ID */
	private Long userId;
	/** ����ID */
	private Long orderId;
	/** ״̬��0:δ�󶨡�1:δʹ�á�2:��ʹ�á�3:�Ѷ��ᡢ4:������ */
	private Integer status;
	/** ����ʱ�� */
	private Timestamp invalidTime;
	/** ���ϲ���ԱID */
	private Long invalidAdminId;
	/** �������� */
	private String invalidReason;
	
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
	public Timestamp getGenTime() {
		return genTime;
	}
	public void setGenTime(Timestamp genTime) {
		this.genTime = genTime;
	}
	public Timestamp getBindTime() {
		return bindTime;
	}
	public void setBindTime(Timestamp bindTime) {
		this.bindTime = bindTime;
	}
	public Timestamp getUsedTime() {
		return usedTime;
	}
	public void setUsedTime(Timestamp usedTime) {
		this.usedTime = usedTime;
	}
	public Timestamp getInvalidTime() {
		return invalidTime;
	}
	public void setInvalidTime(Timestamp invalidTime) {
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
}
