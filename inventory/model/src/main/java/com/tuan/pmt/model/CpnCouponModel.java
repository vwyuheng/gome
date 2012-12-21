package com.tuan.pmt.model;

import com.tuan.core.common.lang.TuanBaseDO;

public class CpnCouponModel extends TuanBaseDO{

	/** 序列化标识 */
	private static final long serialVersionUID = 2224423703132142104L;
	/** 主键ID 自增*/
	private Long couponId;
	/** 批次ID */
	private Long batchId;
	/** 前缀 */
	private String prefix;
	/** 验证码 */
	private String code;
	/** 生成时间 */
	private String genTime;
	/** 绑定时间 */
	private String bindTime;
	/** 使用时间 */
	private String usedTime;
	/** 用户ID */
	private Long userId;
	/** 订单ID */
	private Long orderId;
	/** 状态：0:未绑定、1:未使用、2:已使用、3:已冻结、4:已作废 */
	private Integer status;
	/** 作废时间 */
	private String invalidTime;
	/** 作废操作员ID */
	private Long invalidAdminId;
	/** 作废理由 */
	private String invalidReason;
	/**代金券所在批次*/
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