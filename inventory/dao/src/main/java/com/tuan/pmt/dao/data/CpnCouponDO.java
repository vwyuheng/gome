package com.tuan.pmt.dao.data;

import java.sql.Timestamp;

import com.tuan.core.common.lang.TuanBaseDO;

/**
 * 代金券表（cpn_coupon）
 * @ClassName: CpnCouponDO
 * @author duandongjun
 * @date 2012-11-15
 */
public class CpnCouponDO extends TuanBaseDO {
	/** 序列化标识 */
	private static final long serialVersionUID = 2951665936037707502L;
	/** 主键ID 自增*/
	private Long couponId;
	/** 批次ID */
	private Long batchId;
	/** 前缀 */
	private String prefix;
	/** 验证码 */
	private String code;
	/** 生成时间 */
	private Timestamp genTime;
	/** 绑定时间 */
	private Timestamp bindTime;
	/** 使用时间 */
	private Timestamp usedTime;
	/** 用户ID */
	private Long userId;
	/** 订单ID */
	private Long orderId;
	/** 状态：0:未绑定、1:未使用、2:已使用、3:已冻结、4:已作废 */
	private Integer status;
	/** 作废时间 */
	private Timestamp invalidTime;
	/** 作废操作员ID */
	private Long invalidAdminId;
	/** 作废理由 */
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
