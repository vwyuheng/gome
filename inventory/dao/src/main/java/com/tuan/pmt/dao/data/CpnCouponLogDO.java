package com.tuan.pmt.dao.data;

import java.sql.Timestamp;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * ����ȯ��־��cpn_coupon_log��
 * @ClassName: CpnCouponLog
 * @author duandongjun
 * @date 2012-11-15
 */
public class CpnCouponLogDO extends TuanBaseDO{
	/** ���л���ʶ */
	private static final long serialVersionUID = 5651317059372625677L;

	/** id */
	private Long logId;
	/** ����ȯid */
	private Long couponId;
	/** ����ʱ�� */
	private Timestamp createTime;
	/** ������ �����������ȯ���������͵Ĳ����ͱ仯��Ŀǰδ��ȷ����ֵ������С�������,"��"�������ᡱ����ʹ�á��������ϡ������˿ */
	private String code;
	/** ���� */
	private String description;
	/** ����ID��user_id or order_id */
	private Long objectId;
	
	public Long getLogId() {
		return logId;
	}
	public void setLogId(Long logId) {
		this.logId = logId;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
